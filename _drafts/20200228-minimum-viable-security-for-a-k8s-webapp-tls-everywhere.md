---
layout:     post
title:      "Minimum Viable Security for a Kubernetised Webapp: TLS everywhere"
categories: ['cloud', 'apaas', 'service mesh'] 
tags:       ['aws', 'kubernetes', 'microservice', 'x509', 'tls', 'mvsec']
permalink:  "/2020/02/28/minimum-viable-security-for-a-k8s-webapp-tls-everywhere"
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

Part of the answer gives us Vilfredo Pareto with the Pareto Principle, but to have a complete answer we have to turn to The Security Design Principles that NIST, OWASP, NCSC and other Security References give us.  

> The Security Design Principles to consider:
> - [Building Secure Software: How to Avoid Security Problems the Right Way by Gary McGraw, John Viega, released September 2001](https://www.oreilly.com/library/view/building-secure-software/9780672334092) and the ["Exploiting Software: How to Break Code"](/assets/20200228-exploiting-software-how-to-break-code-2004-gary-mcgraw-cigital.pdf) Presentation by Gary McGrow, 2004.
>   * Contains xxx Principles
> - OWASP Security by Design Principles:
>   * [10 Security Principles, archived by 2016 ](https://wiki.owasp.org/index.php/Security_by_Design_Principles)
>   * [11 Security Principles, updated by October 2015](https://github.com/OWASP/DevGuide/blob/master/02-Design/01-Principles%20of%20Security%20Engineering.md)
> - [Systems Security Engineering by NIST, SP 800-160 Vol. 1, updated 21/Mar/2018](https://csrc.nist.gov/publications/detail/sp/800-160/vol-1/final):
>   * There are 32 principles.
> - [Secure Design Principles by NCSC, version 1.0, updated 21/May/2019](https://www.ncsc.gov.uk/collection/cyber-security-design-principles): 
>   * There are 31 and 15 principles in total.
> - [Cliff Berg’s Principles for High-Assurance Design (Architecting Secure and Reliable Enterprise Applications), 23/October/2005](https://www.amazon.com/High-Assurance-Design-Architecting-Enterprise-Applications/dp/0321793277), etc.
  
There are many similarities between them at fundamental level, let's explore the OWASP for example:

OWASP Security Design Principles| Explanation                   | Is it about TLS?
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

If this quick analysis does not convince you, then refer to the Pillars of Security, they are axioms and don't require be demostration.
Organization like OWASP recommends that all security controls should be designed with the __Core pillars of Information Security__ in mind:

* Confidentiality – only allow access to data for which the user is permitted.
* Integrity – ensure data is not tampered or altered by unauthorised users.
* Availability – ensure systems and data are available to authorised users when they need it.

These Axioms refer to _access control_, _data no altered_, _unauthorised users_, etc. and that is implemented following the [Identity-based Security](https://en.wikipedia.org/wiki/Identity-based_security) Strategy.

## Let's implement TLS in our Kubernetes cluster.

**1) Cleaning everything**

**xxx**


## References

- https://www.cs.umd.edu/class/spring2019/cmsc414/
- [Identity-based Security](https://en.wikipedia.org/wiki/Identity-based_security) 
- https://blog.threatpress.com/security-design-principles-owasp/