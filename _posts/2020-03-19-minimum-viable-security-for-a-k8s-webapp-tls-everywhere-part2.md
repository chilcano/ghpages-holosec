---
layout:     post
title:      "Minimum Viable Security for a Kubernetised Webapp: HTTP Basic Auth on TLS - Part2"
categories: ['cloud', 'service mesh'] 
tags:       ['aws', 'k8s', 'microservice', 'x509', 'tls', 'mvp','HTTP Basic Auth']
date:       2020-03-19 00:10:00 +0100
permalink:  "/2020/03/19/minimum-viable-security-for-a-k8s-webapp-http-basic-auth-on-tls-part2"
comments:   true
---
In the "[Minimum Viable Security for a Kubernetised Webapp: TLS everywhere - Part1](/2020/03/08/minimum-viable-security-for-a-k8s-webapp-tls-everywhere-part1)" I used the [Affordable K8s](https://github.com/chilcano/affordable-k8s)' Terraform scripts to create a K8s Cluster with the Jetstack Cert-Manager and the NGINX Ingress Controller pre-installed, now I want to improve the security of a Webapp hosted in that Cluster according the __Minimum Viable Security__ (MVSec) and __Pareto Principle or 80/20 rule__. 

In this post I'll explain how to enable and configure _[HTTP Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication) over TLS_ in the [Weave Scope](https://www.weave.works/oss/scope) webapp running in the recently created K8s Cluster. I recommend follow the [Part 1](/2020/03/08/minimum-viable-security-for-a-k8s-webapp-tls-everywhere-part1) to get this scenario.

<!-- more --> 

Let's explore the K8s services created.
```sh
$ kubectl get svc -n weave
```
- The `weave-scope-app-svc-np` NodePort service will create the `weave-scope.cloud.holisticsecurity.io` subdomain entry in AWS Route 53.
- The `weave-scope-app-svc-np` NodePort service is required when I'm getting access through SSH tunnel.
- The `weave-scope-app` ClusterIP service was created when Weave Scope was installed.


Get access through SSH to your K8s Cluster and clone the [Affordable K8s](https://github.com/chilcano/affordable-k8s) Git Repo. It contains all YAML files required for next steps.

```sh 
$ ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem

$ git clone https://github.com/chilcano/affordable-k8s
$ cd affordable-k8s/examples/
```

Since I'll use HTTP Basic Auth, I need creating a K8s secret resources that the NGINX Ingress resources for Weave Scope will need. 
```sh
$ sudo apt install apache2-utils -y
$ htpasswd -bc auth YOUR_USR YOUR_PWD
$ kubectl create secret generic secret-http-basic-auth --from-file=auth -n weave
```

### Enabling and configuring HTTP Basic Authentication over TLS


**1. Create a Let’s Encrypt Issuer**

A Certificate issuer is the definition for where [Jetstack Cert-Manager](https://cert-manager.io/docs/concepts/issuer/#namespaces) will get request TLS certs. An `Issuer` is specific to a single namespace in Kubernetes, and a `ClusterIssuer` is meant to be a cluster-wide definition for the same purpose. 

In a scenario or project real, we should generate certificates for testing (`staging`) purporses, once tested the process to get and configure testing certificates, the next step is to get and use final (`production`) certificates. That is the reason I provide `letsencrypt-issuer-staging.yaml` and `letsencrypt-issuer-prod.yaml`. Then, let's create both issuers.

```sh
$ kubectl apply -f letsencrypt-issuer-staging.yaml -n weave
$ kubectl apply -f letsencrypt-issuer-prod.yaml -n weave
```

Checking the Let's Encrypt Issuers. If you got `"Reason: ACMEAccountRegistered"`, the Issuer is ready to issue certificates.
```sh
$ kubectl get issuer,secret -n weave
NAME                                                READY   AGE
issuer.cert-manager.io/letsencrypt-issuer-prod      True    14s
issuer.cert-manager.io/letsencrypt-issuer-staging   True    76s

NAME                                        TYPE                                  DATA   AGE
secret/default-token-gzbzs                  kubernetes.io/service-account-token   3      7m30s
secret/letsencrypt-issuer-privkey-prod      Opaque                                1      12s
secret/letsencrypt-issuer-privkey-staging   Opaque                                1      74s
secret/secret-http-basic-auth               Opaque                                1      3m6s
secret/weave-scope-token-vb7vg              kubernetes.io/service-account-token   3      7m31s


$ kubectl describe issuer letsencrypt-issuer-staging -n weave
$ kubectl describe issuer letsencrypt-issuer-prod -n weave
```

If you have under `Status` this `Reason: ACMEAccountRegistered` message in both issuers, that means they are ready to get certificates from Let's Encrypt.


**2. Create the Ingress resource with a Staging Certificate**

```sh
$ kubectl apply -f weave-scope-app-ingress.yaml 
```

With `weave-scope-app-ingress.yaml` I'll have a testing certificate.
```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: weave-scope-app-ingress
  annotations:
    kubernetes.io/ingress.class: "nginx"
    cert-manager.io/issuer: "letsencrypt-issuer-staging"
    #cert-manager.io/issuer: "letsencrypt-issuer-prod"
    nginx.ingress.kubernetes.io/auth-type: basic
    nginx.ingress.kubernetes.io/auth-secret: secret-http-basic-auth
  namespace: weave
spec:
  rules:
  - host: weave-scope.cloud.holisticsecurity.io
    http:
      paths:
      - path: /
        backend:
          serviceName: weave-scope-app-svc-np  ## NodePort
          servicePort: 82
  tls:
  - hosts:
    - weave-scope.cloud.holisticsecurity.io
    secretName: cert-tls
```

When creating the TLS NGINX Ingress resources, the NGINX Ingress Controller underhood will execute the next steps:
- Request a TLS Cert through the Let's Encrypt Issuer (`letsencrypt-issuer-staging`).
- Load the issued TLS Cert and its configuration to route the `weave-scope.cloud.holisticsecurity.io` incomming traffic over TLS to Weave Scope pods.
- The HTTP Basic Auth annotations (`nginx.ingress.kubernetes.io/auth-type: basic` and `nginx.ingress.kubernetes.io/auth-secret: secret-http-basic-auth`) will be used.

If everything looks good, you might check the K8s `secret` resource and the Let's Encrypt Issuer resources (`certificate`, `certificaterequest`, `order` and `challenge`) details.
```sh
$ kubectl get ingress,secret,certificate,certificaterequest,order -n weave 
$ kubectl describe secret cert-tls -n weave
Name:         cert-tls
Namespace:    weave
Labels:       <none>
Annotations:  cert-manager.io/alt-names: weave-scope.cloud.holisticsecurity.io
              cert-manager.io/certificate-name: cert-tls
              cert-manager.io/common-name: weave-scope.cloud.holisticsecurity.io
              cert-manager.io/ip-sans: 
              cert-manager.io/issuer-kind: Issuer
              cert-manager.io/issuer-name: letsencrypt-issuer-staging
              cert-manager.io/uri-sans: 

Type:  kubernetes.io/tls

Data
====
tls.crt:  3610 bytes
tls.key:  1675 bytes
ca.crt:   0 bytes


$ kubectl describe certificate cert-tls -n weave
...
...
Events:
  Type    Reason        Age    From          Message
  ----    ------        ----   ----          -------
  Normal  GeneratedKey  3m39s  cert-manager  Generated a new private key
  Normal  Requested     3m36s  cert-manager  Created new CertificateRequest resource "cert-tls-3240899713"
  Normal  Issued        2m2s   cert-manager  Certificate issued successfully
```

Now, if you open this url `https://weave-scope.cloud.holisticsecurity.io` in your browser you will get an error about _Certificate desn't Trusted_ and can not get access to Weave Scope. That doesn't mean that Cert-Manager or Ingress Controller don't work, that means specifically this:

> weave-scope.cloud.holisticsecurity.io has a security policy called HTTP Strict Transport Security (HSTS), which    
> means that Firefox can only connect to it securely. You can't add an exceptions to visit this site.
> 
> The issue is most likely with the website, and there is nothing you can do.

[![](/assets/blog20200319/mvp-sec-part2-weave-1-tls-http-basic-auth-fake-cert-error.png){:width="350"}](/assets/blog20200319/mvp-sec-part2-weave-1-tls-http-basic-auth-fake-cert-error.png)
[![](/assets/blog20200319/mvp-sec-part2-weave-2-tls-http-basic-auth-fake-cert-error.png){:width="350"}](/assets/blog20200319/mvp-sec-part2-weave-2-tls-http-basic-auth-fake-cert-error.png)

**3. Create the Ingress resource and getting a Production Certificate**


In this point we are ready to get a certificate for production purposes, before we should remove the previous ingress resource and its corresponding generated secret.
```sh
$ kubectl delete ingress weave-scope-app-ingress -n weave
$ kubectl delete secret cert-tls -n weave
```

Edit the `weave-scope-app-ingress.yaml`.
```sh
$ nano weave-scope-app-ingress.yaml 
```

Uncomment this line `cert-manager.io/issuer: "letsencrypt-issuer-prod"` and comment this other `cert-manager.io/issuer: "letsencrypt-issuer-staging"` in `weave-scope-app-ingress.yaml` file.
```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: weave-scope-app-ingress
  annotations:
    kubernetes.io/ingress.class: "nginx"
    #cert-manager.io/issuer: "letsencrypt-issuer-staging"
    cert-manager.io/issuer: "letsencrypt-issuer-prod"
    nginx.ingress.kubernetes.io/auth-type: basic
    nginx.ingress.kubernetes.io/auth-secret: secret-http-basic-auth
  namespace: weave
spec:
  rules:
  - host: weave-scope.cloud.holisticsecurity.io
    http:
      paths:
      - path: /
        backend:
          serviceName: weave-scope-app-svc-np  ## NodePort
          servicePort: 82
  tls:
  - hosts:
    - weave-scope.cloud.holisticsecurity.io
    secretName: cert-tls
```

Now create the updated ingress.
```sh
$ kubectl apply -f weave-scope-app-ingress.yaml 
```

Check all resources created.
```sh
$ kubectl get ingress,secret,certificate,certificaterequest,order -n weave 
NAME                                         HOSTS                                   ADDRESS   PORTS     AGE
ingress.extensions/weave-scope-app-ingress   weave-scope.cloud.holisticsecurity.io             80, 443   68s

NAME                                        TYPE                                  DATA   AGE
secret/cert-tls                             kubernetes.io/tls                     3      66s
secret/default-token-gzbzs                  kubernetes.io/service-account-token   3      31m
secret/letsencrypt-issuer-privkey-prod      Opaque                                1      24m
secret/letsencrypt-issuer-privkey-staging   Opaque                                1      25m
secret/secret-http-basic-auth               Opaque                                1      27m
secret/weave-scope-token-vb7vg              kubernetes.io/service-account-token   3      31m

NAME                                   READY   SECRET     AGE
certificate.cert-manager.io/cert-tls   True    cert-tls   68s

NAME                                                     READY   AGE
certificaterequest.cert-manager.io/cert-tls-1393507612   True    66s

NAME                                                        STATE   AGE
order.acme.cert-manager.io/cert-tls-1393507612-2800536094   valid   66s
```

Now, if you open this url `https://weave-scope.cloud.holisticsecurity.io` in your browser you will be prompted for user and password to get access to Weave Scope.

[![](/assets/blog20200319/mvp-sec-part2-weave-3-tls-http-basic-auth-ok.png){:width="350"}](/assets/blog20200319/mvp-sec-part2-weave-3-tls-http-basic-auth-ok.png)
[![](/assets/blog20200319/mvp-sec-part2-weave-4-tls-http-basic-auth-info.png){:width="350"}](/assets/blog20200319/mvp-sec-part2-weave-4-tls-http-basic-auth-info.png)

Once authenticated, you will be able to check the TLS Certificate issued by Let's Encrypt.
[![](/assets/blog20200319/mvp-sec-part2-weave-5-tls-http-basic-auth-certificates.png){:width="350"}](/assets/blog20200319/mvp-sec-part2-weave-5-tls-http-basic-auth-certificates.png)
[![](/assets/blog20200319/mvp-sec-part2-weave-6-tls-http-basic-authed.png){:width="350"}](/assets/blog20200319/mvp-sec-part2-weave-6-tls-http-basic-authed.png)



**4. Troubleshooting with logs**

Check the Cert-Manager logs to see if the TLS Cert, Keys, CSR, etc. were requested and approved successfully.
```sh
$ kubectl logs -f -n cert-manager -lapp=cert-manager
```

Check the NGINX Ingress Controller logs to see if the TLS Cert and configuration were created and loaded successfully.
```sh
$ kubectl logs -f -n ingress-nginx -lapp.kubernetes.io/part-of=ingress-nginx
```


## Conclusions

### You need a PKI

Let's Encrypt technically can issue TLS Client Certificates, but it isn't recommended because using Let’s Encrypt’s DV certificates directly as client certificates doesn’t offer a lot of flexibility, and probably doesn’t enhance overall security in most configurations. The best option would be to use your own CA for this process, as that allows for much more direct control, and client certificates don’t have to be publicly trusted by all clients, just trusted by your server.  
For other side, at operationaly speaking, TLS Certificate Management (revocation, renewals, validation, etc.) is expensive, that means we will require a PKI with a powerful RESTful API to manage the Cert Lifecycle during the deployment of Containers-based Applications.  
A PKI will be helpful allowing to create a private CA or Intermediate CA and manage their Lifecycle easily.

> I use Jetstack Cert-Manager to manage certs issued for Let's Encrypt, Hashicorp Vault and Venafi, in the 2nd post I'll explain how to use Hashicorp Vault as CA for enablling TLS and Mutual TLS Authentication (MTLS) in the services running in Kubernetes Cluster.  
> In the 3rd post I'll explain how to integrate and use Hashicorp Vault as a PKI.

### You need an IAM System

Mutual TLS Authentication (MTLS) is better than HTTP Basic Authentication over TLS, instead of using a pre-shared key with HTTP Basic Authentication, with TLS you are able to use a TLS Client Certificate, in fact to enable MTLS will require to issue 2 certificates (TLS Server and TLS Client Certificates) and deploy TLS configuration to enable authentication for that specified Service or Web Application. That will work perfectly if you have to enable MTLS for few services or applications, but in a scenario where you have several APIs or a Container-based Distributed Application, the task of dealing MTLS will turn very complicated. For that, many Organizations consider adoption of an IAM System like WSO2 Identity Server, KeyCloak, DEX, etc. I recommend have a look for IAM at OSS Product List that I prepared in the post [Security along Container-based SDLC - OSS Tools List](https://holisticsecurity.io/2020/02/10/security-along-the-container-based-sdlc#oss-doc-link).

> In the 4th post I'll explain how to integrate an IAM opensource as OIDC Provider for Kubernetes.

## References

- [Cert-Manager tutorial - Securing NGINX-ingress](https://cert-manager.io/docs/tutorials/acme/ingress)
- [How to Set Up an Nginx Ingress with Cert-Manager on DigitalOcean Kubernetes](https://www.digitalocean.com/community/tutorials/how-to-set-up-an-nginx-ingress-with-cert-manager-on-digitalocean-kubernetes)