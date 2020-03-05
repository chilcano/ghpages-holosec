---
layout:     post
title:      "Minimum Viable Security for a Kubernetised Webapp: TLS everywhere - Part1"
categories: ['cloud', 'apaas', 'service mesh'] 
tags:       ['aws', 'kubernetes', 'microservice', 'x509', 'tls', 'mvsec']
permalink:  "/2020/03/02/minimum-viable-security-for-a-k8s-webapp-tls-everywhere-part1"
comments:   true
---

__Minimum Viable Security__ (MVSec) is a concept borrowed from the [Minimum Viable Product](https://en.wikipedia.org/wiki/Minimum_viable_product){:target="_blank"} (MVP) concept about the Product Development Strategy and from the [Pareto Principle or 80/20 rule](https://en.wikipedia.org/wiki/Pareto_principle){:target="_blank"}. The MVP concept applied to IT Security means the product (application) will contain only the minimum amount (20%) of effort invested in order to prove the viability (80%) of an idea (acceptable security). 

The purpose of this post is to explain how to implement __TLS everywhere__ to become __MVSec__ (roughly 80% of security with 20% of working) for a __Kubernetised Webapp hosted on AWS__.

[![K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png "K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt")](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png){:target="_blank"}

<!-- more --> 

## Why _TLS everywhere_ meets 80/20 rule?

Part of the answer gives us Vilfredo Pareto with the Pareto Principle, but to have a complete answer we have to turn to The Security Design Principles that NIST, OWASP, NCSC and other Security References give us.  

> The Security Design Principles to consider are:
> - [Building Secure Software: How to Avoid Security Problems the Right Way by Gary McGraw, John Viega, released September 2001](https://www.oreilly.com/library/view/building-secure-software/9780672334092) and the ["Exploiting Software: How to Break Code"](/assets/20200228-exploiting-software-how-to-break-code-2004-gary-mcgraw-cigital.pdf) Presentation by Gary McGrow, 2004.
> - OWASP Security by Design Principles:
>   * [10 Security Principles, archived by 2016 ](https://wiki.owasp.org/index.php/Security_by_Design_Principles)
>   * [11 Security Principles, updated by October 2015](https://github.com/OWASP/DevGuide/blob/master/02-Design/01-Principles%20of%20Security%20Engineering.md)
> - [Systems Security Engineering by NIST, SP 800-160 Vol. 1, updated 21/Mar/2018](https://csrc.nist.gov/publications/detail/sp/800-160/vol-1/final):
>   * There are 32 principles.
> - [Secure Design Principles by NCSC, version 1.0, updated 21/May/2019](https://www.ncsc.gov.uk/collection/cyber-security-design-principles): 
>   * There are 31 and 15 principles in total.
> - [Cliff Berg’s Principles for High-Assurance Design (Architecting Secure and Reliable Enterprise Applications), 23/October/2005](https://www.amazon.com/High-Assurance-Design-Architecting-Enterprise-Applications/dp/0321793277), etc.
  
There are many similarities between them at fundamental level, let's explore the OWASP for example and do a quick analysis to see how TLS can help us to meet these Security Principles.

OWASP Security Design Principles| Explanation                   | How to TLS help us?
---                             | ---                           | ---
1) Defense in Depth.            | Also known as layered defense.| TLS over HTTP provides a new secure layer.
2) Fail Safe.                   | Unless a subject is given explicit access to an object, it should be denied access to that object. | TLS Certificates can encrypt at rest and in transit these objects.
3) Least Privilege.             | A Process is given only the minimum level of access rights (privileges) that is necessary for that Process to complete an assigned operation. | Transit of sensitive objects must be over TLS.
4) Separation of Duties.        | Also known as the [compartmentalization principle](https://en.wikipedia.org/wiki/Compartmentalization_(information_security)), or separation of privilege. | HTTPS is for sensitive transactions and HTTP for non-sensitive.
5) Economy of Mechanism.        | Also known as [Keep It Simple, Stupid principle](https://en.wikipedia.org/wiki/KISS_principle). | The accepted and supported [HTTP/2](https://en.wikipedia.org/wiki/HTTP/2) includes TLS by default. So, if HTTP is insecure, why is it still being used?.
6) Complete Mediation.          | All accesses to objects must be checked to ensure that they are allowed. | TLS provides Simple and [Mutual (Two-Way) Authentication](https://en.wikipedia.org/wiki/Mutual_authentication) and allways once crypto keys have been verified a secure communication is stablished. 
7) Open Design.                 | The security of a mechanism should not depend on the secrecy of its design or implementation. | The disclosure of TLS' using in software designs don't put in risk the application.
8) Least Common Mechanism       | It disallows the sharing of mechanisms that are common to more than one user or process if the users and processes are at different levels of privilege. | TLS is a common secure mechanism shared along the application.
9) Psychological acceptability. | Tries maximizing the usage and adoption of the security functionality in the application by making it more usable. | TLS is a feature by default in HTTP/2. HTTP requires implement TLS and deal the crypto keys.  
10) Weakest Link.               | The resiliency of your software against hacker attempts will depend heavily on the protection of its weakest components. | TLS helps to harden the access to resources and secure the traffic.
11) Leveraging Existing Components. | It focuses on ensuring that the attack surface is not increased. | TLS is a feature by default in HTTP/2 for securing data in transit and data at rest, don't try to bring or implement your own _TLS_.

If this quick analysis does not convince you, then refer to the [Pillars of Security](https://en.wikipedia.org/wiki/Information_security), they are axioms and don't require be demostration.
Organization like OWASP recommends that all security controls should be designed with the __Core pillars of Information Security__ in mind:

Pillar of Security | Description                                                                   | How TLS apply?
---                | ---                                                                           | ---
1. Confidentiality | Only allow access to data for which the user is permitted.                    | Access Control(*) using TLS and Mutual TLS Authn.
2. Integrity       | Ensure data is not tampered or altered by unauthorised users.                 | Data no altered using TLS(*) encryption at rest.
3. Availability    | Ensure systems and data are available to authorised users when they need it.  | Mutual TLS Authn(*) helps to availability only to authorised users.

> (*) These Axioms refer to _access control_, _data no altered_, _unauthorised users_, etc. and that is implemented following the [Identity-based Security](https://en.wikipedia.org/wiki/Identity-based_security) Strategy.

## Let's implement TLS everywhere in Kubernetes.

### Create an Kubernetes Cluster

This blog post will use the "Building your own affordable K8s - Serie":
- [Part 1 - Building your own affordable K8s to host a Service Mesh](/2020/01/16/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-data-plane){:target="_blank"}.
- [Part 2 - Building your own affordable K8s - ExternalDNS and NGINX as Ingress](/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-external-dns-ingress){:target="_blank"}.
- [Part 3 - Building your own affordable K8s - Certificate Manager](/2020/01/29/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part3-certificate-manager){:target="_blank"}.


**1) Clone the Affordable K8s Cluster Git Repo and run the Terraform scripts**

I'll create a Kubernetes Cluster with this configuration:

- NGINX Ingress Controller will be deployed as `DaemonSet` (in the namespace `ingress-nginx`) with `hostNetwork: true` to ensure the NGINX server is reachable from all K8s Cluster nodes.
  * When a pod is configured with `hostNetwork: true`, the applications running in such a pod can directly see the network interfaces of the host machine where the pod was started.
  * Further information in the [`nginx-ingress-mandatory.yaml` deployment file](https://github.com/chilcano/affordable-k8s/blob/master/manifests/nginx-ingress-mandatory.yaml).
- Custom Domain Name System (DNS) is `cloud.holisticsecurity.io` and the Ingress Subdomain DNS is `ingress-nginx.cloud.holisticsecurity.io`.
- TLS Cert enabled using Jetstack Cert-Manager and Let's Encrypt.
- `NodePort` Service for the NGINX Ingress Controller. See its [configuration](https://github.com/chilcano/affordable-k8s/blob/master/manifests/nginx-ingress-nodeport.yaml.tmpl) here.

```sh
$ git clone https://github.com/chilcano/affordable-k8s
$ cd affordable-k8s

$ terraform init

$ terraform plan \
  -var cluster_name="cheapk8s" \
  -var k8s_ssh_key="ssh-key-for-us-east-1" \
  -var admin_cidr_blocks="83.45.101.2/32" \
  -var region="us-east-1" \
  -var kubernetes_version="1.14.3" \
  -var external_dns_enabled="1" \
  -var nginx_ingress_enabled="1" \
  -var nginx_ingress_domain="ingress-nginx.cloud.holisticsecurity.io" \
  -var cert_manager_enabled="1" \
  -var cert_manager_email="cheapk8s@holisticsecurity.io"

$ terraform apply \
  -var cluster_name="cheapk8s" \
  -var k8s_ssh_key="ssh-key-for-us-east-1" \
  -var admin_cidr_blocks="83.45.101.2/32" \
  -var region="us-east-1" \
  -var kubernetes_version="1.14.3" \
  -var external_dns_enabled="1" \
  -var nginx_ingress_enabled="1" \
  -var nginx_ingress_domain="ingress-nginx.cloud.holisticsecurity.io" \
  -var cert_manager_enabled="1" \
  -var cert_manager_email="cheapk8s@holisticsecurity.io"
```

**2) Once the K8s Cluster is created, check if NGINX Ingress Controller is running correctly**

Let's make a query to The NGINX Ingress Controller's NodePort Service. Likely you have to wait a 2-3 minutes till the Cluster is running completely.

```sh
$ ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem

$ kubectl get pod,svc -n ingress-nginx -o wide
NAME                                        READY   STATUS    RESTARTS   AGE
pod/default-http-backend-5c9bb94849-9lpn9   1/1     Running   0          7m35s
pod/nginx-ingress-controller-r2j8t          1/1     Running   0          7m34s
pod/nginx-ingress-controller-v22mp          1/1     Running   0          7m34s

NAME                           TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                      AGE
service/default-http-backend   ClusterIP   10.110.233.145   <none>        80/TCP                       7m36s
service/ingress-nginx          NodePort    10.111.197.179   <none>        80:30002/TCP,443:30414/TCP   7m33s
```

If everything is configured correctly, you should get a `default backend - 404` response when accessing the NGINX Ingress Controller using its `NodePort` and its `external IP address`. This tells us that the controller doesn’t know where to route the request to, so responds with the default backend.

Calling it from inside the K8s Cluster, here you have to use the `NodePorts` (`30002` for HTTP and `30414` for HTTPS):
```sh
$ curl http://localhost:30002/abc
default backend - 404

$ curl https://localhost:30414/pqr -k
default backend - 404
```

And calling from Internet, here you have to use Standard Ports (`80` for HTTP and `443` for HTTPS) and the `external IP address`. The FQDN `ingress-nginx.cloud.holisticsecurity.io` is the Subdomain DNS I'm using for the NGINX Ingress Controller:
```sh
$ curl http://ingress-nginx.cloud.holisticsecurity.io/abc
default backend - 404

$ curl https://ingress-nginx.cloud.holisticsecurity.io/pqr -k
default backend - 404
```


### Deploying a Sample Application

I love [Weave Scope](https://www.weave.works/docs/scope/latest/introducing), it is a good Web Application that I can use to enable security.

> __Weave Scope__ is a visualization and monitoring tool for Docker and Kubernetes. It provides a top down view into your app as well as your entire infrastructure, and allows you to diagnose any problems with your distributed containerized app, in real time, as it is being deployed to a cloud provider.

```sh
$ kubectl apply -f "https://cloud.weave.works/k8s/scope.yaml?v=${WS_VERSION}&k8s-service-type=${WS_SERVICE_TYPE}&k8s-version=$(kubectl version | base64 | tr -d '\n')"
```

Where:
* `WS_VERSION`: `v1.12.0|...|v1.7.3|...` - If it isn't used then a `latest` version will be used by default
* `WS_SERVICE_TYPE`: `LoadBalancer|NodePort` - If it isn't used then a ClusterIP will be created (internal use only)

```sh
# Get ssh access and in your K8s Cluster, run below command to install Weave Scope with everything by default.
$ kubectl apply -f "https://cloud.weave.works/k8s/scope.yaml?k8s-version=$(kubectl version | base64 | tr -d '\n')"

# Check if Weave Scope has been installed successfully. You will see Pods, Service, ReplicaSet, DaemonSet and Deployment.
$ kubectl get all -n weave

# Get the Weave Scope's TargetPort. By default the TargetPort for Weave Scope's ClusterIP service is 4040.
$ kubectl get -n weave svc weave-scope-app -o jsonpath='{.spec.ports[0].targetPort}'
4040
```

Since ClusterIP is for internal use only, I'll need that Weave Scope be exposed and reachable from Internet through a SSH Tunnel. I can do it by creating a NodePort service.
```sh
# Let's create a NodePort Resource for Weave Scope.
$ kubectl apply -f https://raw.githubusercontent.com/chilcano/ansible-role-weave-scope/master/sample-2-weave-scope-app-svc.yml -n weave
service/weave-scope-app-svc created

# Now, we have 2 services
$ kubectl get svc -n weave
NAME                  TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
weave-scope-app       ClusterIP   10.100.187.247   <none>        80/TCP         45m
weave-scope-app-svc   NodePort    10.110.173.129   <none>        80:30002/TCP   18m

# Get the Weave Scope's NodePort. Also you can see it in above command or in the 'sample-2-weave-scope-app-svc.yml' file.
$ kubectl get -n weave svc weave-scope-app-svc -o jsonpath='{.spec.ports[0].nodePort}'
30002
```

Now let's create a SSH tunnel from your Admin Computer over Internet to Weave Scope's NodePort service. 
```sh
$ ssh -nNT -L 4002:localhost:30002 ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem
```

Now open your favorite browser and enter this url [http://localhost:4002](http://localhost:4002){:target="_blank"} and you will be able to visualize all resources created in your Cluster in real-time.

[![](/assets/blog20200302/tls-everywhere-part1-weave-scope-ssh-tunnel.png){:width="80%"}](/assets/blog20200302/tls-everywhere-part1-weave-scope-ssh-tunnel.png){:target="_blank"}


### Enabling and configuring Security based on TLS

Since I'm using the [Affordable K8s](https://github.com/chilcano/affordable-k8s)' Terraform scripts to build a K8s Cluster with the Jetstack Cert-Manager, to get, renew, revoke any kind of X.509 Certificates, and the NGINX Ingress Controller, to manage the traffic, now i would be able to improve security according the __Minimum Viable Security__ (MVSec) and __Pareto Principle or 80/20 rule__ both explained above.


**1. Enabling [HTTP Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication) over TLS in Weave Scope**

The NGINX Ingress Controller exposes different options for configuring the NGINX server through `annotations` on the Ingress resource. The process will be:
1. Create a secret for HTTP-Basic Auth:
  ```sh
  $ sudo apt install apache2-utils -y 
  $ htpasswd -bc http-basic-auth-file WEAVE_SCOPE_USR WEAVE_SCOPE_PWD
  $ kubectl create secret generic weave-scope-secret-basic-auth --from-file=http-basic-auth-file
  ```
> The `weave-scope-secret-basic-auth` secret resource will be used for NGINX Ingress resource below.
> Change WEAVE_SCOPE_USR/WEAVE_SCOPE_PWD for yours.
2. Create the [TLS Ingress resource for Weave Scope](https://github.com/chilcano/affordable-k8s/blob/master/examples/weave-scope-ingress-tls.yaml):
   - Enable TLS: Point (1)
   - Enable Cert-Manager and select the issuer CA: Point (2)
   - Cert-Manager will request a TLS Cert: Point (3)
   - Enable HTTP-Basic Auth and define its secret: Point (4) and (5) 
     
   Let's review the Ingress resource [weave-scope-ingress-tls.yaml](https://github.com/chilcano/affordable-k8s/blob/master/examples/weave-scope-ingress-tls.yaml)
   ```yaml
   apiVersion: extensions/v1beta1
   kind: Ingress
   metadata:
     name: weave-scope-ingress
     annotations:
       kubernetes.io/ingress.class: "nginx"
       kubernetes.io/tls-acme: "true"
       certmanager.k8s.io/issuer: "letsencrypt-prod" # (2) Selecting the issuer CA
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
               serviceName: weave-scope-app  # Weave Scope ClusterIP service created by default during the installation
               servicePort: 80
     tls:
     - hosts:
       - weave-scope.cloud.holisticsecurity.io  # (1) This host (fqdn) is going to get a tls cert
       secretName: weave-scope-secret-tls-cert # (3) Cert-Manager will store the created tls cert in this k8s secret
   ```
3. Deploy the TLS Ingress resource for Weave Scope:
```sh
$ kubectl apply -f https://raw.githubusercontent.com/chilcano/affordable-k8s/master/examples/weave-scope-ingress-tls.yaml
```
4. Finally, from your browser open this url [https://weave-scope.cloud.holisticsecurity.io](https://weave-scope.cloud.holisticsecurity.io) and when a user and password are prompted, enter the secret created in the Step 1.




**2. Enabling [Mutual TLS Authentication](https://en.wikipedia.org/wiki/Mutual_authentication) in Weave Scope**




## Conclusions

### You need a PKI

Let's Encrypt technically can issue TLS Client Certificates, but it isn't recommended because using Let’s Encrypt’s DV certificates directly as client certificates doesn’t offer a lot of flexibility, and probably doesn’t enhance overall security in most configurations. The best option would be to use your own CA for this process, as that allows for much more direct control, and client certificates don’t have to be publicly trusted by all clients, just trusted by your server.  
For other side, at operationaly speaking, TLS Certificate Management (revocation, renewals, validation, etc.) is expensive, that means we will require a PKI with a powerful RESTful API to manage the Cert Lifecycle during the deployment of Containers-based Applications.  
A PKI will be helpful allowing to create a private CA or Intermediate CA and manage their Lifecycle easily.

> I use Jetstack Cert-Manager to manage certs issued for Let's Encrypt, Hashicorp Vault and Venafi, in the 2nd post I'll explain how to use Hashicorp Vault as CA for enablling TLS and MTLS in the services running in Kubernetes Cluster.

### You need an IAM System

Mutual TLS Authentication (MTLS) is better than HTTP Basic Authentication over TLS, instead of using a pre-shared key with HTTP Basic Authentication, with TLS you are able to use a TLS Client Certificate, in fact to enable MTLS will require to issue 2 certificates (TLS Server and TLS Client Certificates) and deploy TLS configuration to enable authentication for that specified Service or Web Application. That will work perfectly if you have to enable MTLS for few services or applications, but in a scenario where you have several APIs or a Container-based Distributed Application, the task of dealing MTLS will turn very complicated. For that, many Organizations consider adoption of an IAM System like WSO2 Identity Server, KeyCloak, DEX, etc. I recommend have a look for IAM at OSS Product List that I prepared in the post [Security along Container-based SDLC - OSS Tools List](https://holisticsecurity.io/2020/02/10/security-along-the-container-based-sdlc#oss-doc-link).

> In the 3rd post I'll explain how to integrate an IAM opensource as OIDC Provider for Kubernetes.