---
layout: post
title:  "Minimum Viable Security for a Kubernetised Webapp: TLS everywhere"
date:   2020-02-18 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh'] 
tags: ['aws', 'kubernetes', 'microservice', 'x509', 'tls']
permalink: "/2020/02/18/minimum-viable-security-for-a-k8s-webapp-tls-everywhere"
comments: true
---

Minimum Viable Security is a concept borrowed from the [Minimum Viable Product (MVP)](https://en.wikipedia.org/wiki/Minimum_viable_product){:target="_blank"} concept about the Product Development Strategy. The MVP concept means the product will contain only the minimum amount of effort invested in order to prove the viability of an idea, and if we use the same concept in the IT Security world, then the next question is What is the minimum amount of work that can be done that can be considered making a system (Kubernetised Webapp on AWS) more secure?. The answer is given to us by Vilfredo Pareto:

> Pareto Principle:  
> The Pareto principle (also known as the 80/20 rule, the law of the vital few, or the principle of factor sparsity) states that, for many events, __roughly 80% of the effects come from 20% of the causes__.
> Vilfredo Pareto, noted the 80/20 connection while at the University of Lausanne in 1896, as published in his first work, Cours d'économie politique. In it, Pareto showed that approximately 80% of the land in Italy was owned by 20% of the population.
> [https://en.wikipedia.org/wiki/Pareto_principle](https://en.wikipedia.org/wiki/Pareto_principle){:target="_blank"} 
> [https://en.wikipedia.org/wiki/Vilfredo_Pareto](https://en.wikipedia.org/wiki/Vilfredo_Pareto){:target="_blank"} 

The purpose of this post is to get the 80/20 (roughly 80% of security with 20% of working) and implement it in a Kubernetised Webapp on AWS. 


This blog post will use the "Building your own affordable K8s - Serie":

1. [Part 1 - Building your own affordable K8s to host a Service Mesh](/2020/01/16/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-data-plane){:target="_blank"}.
2. [Part 2 - Building your own affordable K8s - ExternalDNS and NGINX as Ingress](/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-external-dns-ingress){:target="_blank"}.
3. [Part 3 - Building your own affordable K8s - Certificate Manager](/2020/01/29/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part3-certificate-manager){:target="_blank"}.

In order to achieve that, we do need an affordable K8s running in AWS (see above serie), once got that
- NGINX Ingress Controller
- JetStack Certificate Manager
- 

[![K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png "K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt")](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png){:target="_blank"}

<!-- more -->

## Steps

### 1) Cleaning everything


## References
