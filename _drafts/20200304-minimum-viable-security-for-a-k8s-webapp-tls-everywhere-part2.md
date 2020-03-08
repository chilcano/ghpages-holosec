---
layout:     post
title:      "Minimum Viable Security for a Kubernetised Webapp: TLS everywhere - Part2"
categories: ['cloud', 'apaas', 'service mesh'] 
tags:       ['aws', 'kubernetes', 'microservice', 'x509', 'tls', 'mvsec']
permalink:  "/2020/03/04/minimum-viable-security-for-a-k8s-webapp-tls-everywhere-part2"
comments:   true
---

### Enabling and configuring Security based on TLS

Since I'm using the [Affordable K8s](https://github.com/chilcano/affordable-k8s)' Terraform scripts to build a K8s Cluster with the Jetstack Cert-Manager, to get, renew, revoke any kind of X.509 Certificates, and the NGINX Ingress Controller, to manage the traffic, now i would be able to improve security according the __Minimum Viable Security__ (MVSec) and __Pareto Principle or 80/20 rule__ both explained above.


**1. Enabling [HTTP Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication) over TLS in Weave Scope**

The NGINX Ingress Controller exposes different options for configuring the NGINX server through `annotations` on the Ingress resource. The process will be:
1. Create a secret for HTTP-Basic Auth in the namespace `weave` that the Ingress resource will use it:
```sh
$ sudo apt install apache2-utils -y 
$ htpasswd -bc http-basic-auth-file WEAVE_SCOPE_USR WEAVE_SCOPE_PWD
$ kubectl create secret generic weave-scope-secret-basic-auth --from-file=http-basic-auth-file -n weave
```
> The `weave-scope-secret-basic-auth` secret resource will be used for NGINX Ingress resource (see below).
> Change WEAVE_SCOPE_USR/WEAVE_SCOPE_PWD for yours.
2. Create the `letsencrypt-prod-issuer-tmp` as `Issuer` CA in the namespace `weave`:
   ```sh
   $ kubectl apply -f https://raw.githubusercontent.com/chilcano/affordable-k8s/master/examples/cert-manager-issuer-tmp.yaml -n weave
   ```
3. Create the TLS Ingress resource for Weave Scope ([weave-scope-app-ingress-tls.yaml](https://github.com/chilcano/affordable-k8s/blob/master/examples/weave-scope-app-ingress-tls.yaml)):
   - Enable TLS: Point (1)
   - Enable Cert-Manager and select the issuer CA: Point (2)
   - Cert-Manager will request a TLS Cert: Point (3)
   - Enable HTTP-Basic Auth and define its secret: Point (4) and (5) 
  
   ```yaml
   apiVersion: extensions/v1beta1
   kind: Ingress
   metadata:
     name: weave-scope-app-ingress-tls
     annotations:
       kubernetes.io/ingress.class: "nginx"
       certmanager.k8s.io/issuer: "letsencrypt-prod-issuer-tmp" # (2) Selecting the issuer CA
       certmanager.k8s.io/acme-challenge-type: http01
       ingress.kubernetes.io/auth-type: basic  # (4) Enabling http-basic auth
       ingress.kubernetes.io/auth-secret: weave-scope-secret-basic-auth  # (5) NGINX will read this secret for http-basic auth
     namespace: weave
   spec:
     rules:
     - host: weave-scope.cloud.holisticsecurity.io
       http:
         paths:
           - path: /
             backend:
               serviceName: weave-scope-app-svc  # Weave Scope NodePort service created 
               servicePort: 30002
     tls:
     - hosts:
       - weave-scope.cloud.holisticsecurity.io  # (1) This host (fqdn) is going to get a tls cert
       secretName: weave-scope-app-secret-tls # (3) Cert-Manager will store the created tls cert in this k8s secret
   ```
4. Deploy the TLS Ingress resource for Weave Scope:
   ```sh
   $ kubectl apply -f https://raw.githubusercontent.com/chilcano/affordable-k8s/master/examples/weave-scope-app-ingress-tls.yaml -n weave
   ```
5. Finally, from your browser open this url [https://weave-scope.cloud.holisticsecurity.io](https://weave-scope.cloud.holisticsecurity.io) and when a user and password are prompted, enter the secret created in the Step 1.
6. Troubleshooting NGINX Ingress Controller:
   ```sh
   $ kubectl get pods -n ingress-nginx 
   $ kubectl exec -it -n ingress-nginx nginx-ingress-controller-p5qz5 -- cat /etc/nginx/nginx.conf | grep ssl
   $ kubectl logs -f -n ingress-nginx nginx-ingress-controller-p5qz5 | grep Error
   $ kubectl logs -f -n ingress-nginx -lapp.kubernetes.io/name=ingress-nginx
   $ kubectl logs -f -n ingress-nginx -lapp.kubernetes.io/part-of=ingress-nginx
   ```
7. Troubleshooting Jetstack Cert-Manager:
   ```sh
   $ kubectl get pods -n cert-manager
   $ kubectl exec -it -n ingress-nginx cert-manager-54d94bb6fc-fmhcc -- cat /etc/nginx/nginx.conf | grep ssl
   $ kubectl logs -f -n cert-manager cert-manager-54d94bb6fc-fmhcc 
   $ kubectl logs -f -n cert-manager -lapp=cert-manager
   ```





**2. Enabling [Mutual TLS Authentication](https://en.wikipedia.org/wiki/Mutual_authentication) in Weave Scope**


xxxxxx


## Conclusions

### You need a PKI

Let's Encrypt technically can issue TLS Client Certificates, but it isn't recommended because using Let’s Encrypt’s DV certificates directly as client certificates doesn’t offer a lot of flexibility, and probably doesn’t enhance overall security in most configurations. The best option would be to use your own CA for this process, as that allows for much more direct control, and client certificates don’t have to be publicly trusted by all clients, just trusted by your server.  
For other side, at operationaly speaking, TLS Certificate Management (revocation, renewals, validation, etc.) is expensive, that means we will require a PKI with a powerful RESTful API to manage the Cert Lifecycle during the deployment of Containers-based Applications.  
A PKI will be helpful allowing to create a private CA or Intermediate CA and manage their Lifecycle easily.

> I use Jetstack Cert-Manager to manage certs issued for Let's Encrypt, Hashicorp Vault and Venafi, in the 2nd post I'll explain how to use Hashicorp Vault as CA for enablling TLS and MTLS in the services running in Kubernetes Cluster.

### You need an IAM System

Mutual TLS Authentication (MTLS) is better than HTTP Basic Authentication over TLS, instead of using a pre-shared key with HTTP Basic Authentication, with TLS you are able to use a TLS Client Certificate, in fact to enable MTLS will require to issue 2 certificates (TLS Server and TLS Client Certificates) and deploy TLS configuration to enable authentication for that specified Service or Web Application. That will work perfectly if you have to enable MTLS for few services or applications, but in a scenario where you have several APIs or a Container-based Distributed Application, the task of dealing MTLS will turn very complicated. For that, many Organizations consider adoption of an IAM System like WSO2 Identity Server, KeyCloak, DEX, etc. I recommend have a look for IAM at OSS Product List that I prepared in the post [Security along Container-based SDLC - OSS Tools List](https://holisticsecurity.io/2020/02/10/security-along-the-container-based-sdlc#oss-doc-link).

> In the 3rd post I'll explain how to integrate an IAM opensource as OIDC Provider for Kubernetes.

## References

- [Univeristy of Maryland - Computer & Network Security - Spring 2019](https://www.cs.umd.edu/class/spring2019/cmsc414/schedule.html)
- [Identity-based Security](https://en.wikipedia.org/wiki/Identity-based_security) 
- [Secure Kubernetes Services With Ingress, TLS And Let's Encrypt](https://docs.bitnami.com/kubernetes/how-to/secure-kubernetes-services-with-ingress-tls-letsencrypt)
- Adding security layers to your App on OpenShift - Serie:
    * [Part 1 — Deployment and TLS Ingress](https://itnext.io/adding-security-layers-to-your-app-on-openshift-part-1-deployment-and-tls-ingress-9ef752835599)
    * [Part 2 — Authentication and Authorization with Keycloak](https://itnext.io/adding-security-layers-to-your-app-on-openshift-part-2-8320018bcdd1)
    * [Part 3 — Secret Management with Vault](https://itnext.io/adding-security-layers-to-your-app-on-openshift-part-3-secret-management-with-vault-8efd4ec29ec4)
    * [Part 4 — Dynamic secrets with Vault](https://itnext.io/adding-security-layers-to-your-app-on-openshift-part-4-dynamic-secrets-with-vault-b5fe1fc7709b)
    * [Part 5 — Mutual TLS with Istio](https://itnext.io/adding-security-layers-to-your-app-on-openshift-part-5-mutual-tls-with-istio-a8800c2e4df4)
- [How to launch nginx-ingress and cert-manager in Kubernetes](https://medium.com/containerum/how-to-launch-nginx-ingress-and-cert-manager-in-kubernetes-55b182a80c8f)
- [Stepan Ilyin, COO, Wallarm / February 19, 2019 / Building security into cloud native apps with NGINX](https://www.helpnetsecurity.com/2019/02/19/building-security-into-cloud-native-apps-with-nginx)
- [Kubernetes with Keycloak](https://medium.com/@sagarpatkeatl/kubernetes-with-keycloak-eca47f86abec)
- [Accessing Kubernetes Pods from Outside of the Cluster by Aleš Nosek, Feb 14th, 2017](http://alesnosek.com/blog/2017/02/14/accessing-kubernetes-pods-from-outside-of-the-cluster)
- [NGINX Ingress Controller - Bare-metal considerations](https://kubernetes.github.io/ingress-nginx/deploy/baremetal)

