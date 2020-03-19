---
layout:     post
title:      "Minimum Viable Security for a Kubernetised Webapp: Mutual TLS Authentication - Part3"
categories: ['cloud', 'service mesh'] 
tags:       ['aws', 'k8s', 'microservice', 'x509', 'tls', 'mvp','HTTP Basic Auth']
#date:       2020-03-20 10:00:00 +0100
permalink:  "/2020/03/20/minimum-viable-security-for-a-k8s-webapp-mutual-tls-auth-par3"
comments:   true
---



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

