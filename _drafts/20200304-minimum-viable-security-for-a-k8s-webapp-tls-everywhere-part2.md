---
layout:     post
title:      "Minimum Viable Security for a Kubernetised Webapp: TLS everywhere - Part2"
categories: ['cloud', 'apaas', 'service mesh'] 
tags:       ['aws', 'kubernetes', 'microservice', 'x509', 'tls', 'mvsec']
permalink:  "/2020/03/04/minimum-viable-security-for-a-k8s-webapp-tls-everywhere-part2"
comments:   true
---



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

