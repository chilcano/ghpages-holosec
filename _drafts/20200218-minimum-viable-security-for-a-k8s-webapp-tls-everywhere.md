---
layout:     post
title:      "Minimum Viable Security for a Kubernetised Webapp: TLS everywhere"
categories: ['cloud', 'apaas', 'service mesh'] 
tags:       ['aws', 'kubernetes', 'microservice', 'x509', 'tls', 'mvsec']
permalink:  "/2020/02/18/minimum-viable-security-for-a-k8s-webapp-tls-everywhere"
comments:   true
---

__Minimum Viable Security__ (MVSec) is a concept borrowed from the [Minimum Viable Product](https://en.wikipedia.org/wiki/Minimum_viable_product){:target="_blank"} (MVP) concept about the Product Development Strategy and from the [Pareto Principle or 80/20 rule](https://en.wikipedia.org/wiki/Pareto_principle){:target="_blank"}. The MVP concept applied to IT Security means the product (application) will contain only the minimum amount (20%) of effort invested in order to prove the viability (80%) of an idea (acceptable security). 

The purpose of this post is to explain how to implement __TLS everywhere__ to become __MVSec__ (roughly 80% of security with 20% of working) for a __Kubernetised Webapp hosted on AWS__.

[![K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png "K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt")](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png){:target="_blank"}

This blog post will use the "Building your own affordable K8s - Serie":
- [Part 1 - Building your own affordable K8s to host a Service Mesh](/2020/01/16/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-data-plane){:target="_blank"}.
- [Part 2 - Building your own affordable K8s - ExternalDNS and NGINX as Ingress](/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-external-dns-ingress){:target="_blank"}.
- [Part 3 - Building your own affordable K8s - Certificate Manager](/2020/01/29/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part3-certificate-manager){:target="_blank"}.

<!-- more --> 

## Why _TLS everywhere_ meets 80/20 rule?

Part of the answer gives us Vilfredo Pareto with the Pareto Principle, but to have a complete answer we have to turn to The Security Design Principles.
There are many sets of Security Design Principles, for example:
- Viega & McGraw (10)
- OWASP (10)
- [Systems Security Engineering by NIST, SP 800-160 Vol. 1, updated 21/Mar/2018](https://csrc.nist.gov/publications/detail/sp/800-160/vol-1/final):
  * There are 32 principles.
- [Secure Design Principles by NCSC, version 1.0, updated 21/May/2019](https://www.ncsc.gov.uk/collection/cyber-security-design-principles): 
  * There are 31 and 15 principles in total.
- [Cliff Berg’s Principles for High-Assurance Design (Architecting Secure and Reliable Enterprise Applications), 23/October/2005](https://www.amazon.com/High-Assurance-Design-Architecting-Enterprise-Applications/dp/0321793277), etc.
  
Many similarities between them at fundamental level



## Steps

### 1) Cleaning everything


## References
