---
layout:     post
title:      "Sidecar Proxy: The Security Building Block"
#date:       2020-05-13 10:00:00 +0100
categories: ['cloud', 'apaas', 'security','patterns'] 
tags:       ['sidecar', 'proxy', 'ingress', 'gateway', 'load balancer']
permalink:  "/2020/05/13/sidecar-proxy-the-security-building-block"
comments:   true
---
Just as a HTTP reverse proxy is sitting in front of a web application and a sidecar is attached to a motorcycle; a sidecar proxy is attached to a main application to extend or add functionality. A Sidecar Proxy is an application design pattern which abstracts certain features, such as inter-service communications, monitoring and **security**, away from the main application to ease its maintainability, resilience and scalability of the application as a whole. 

In this post I will show you how to use the Sidecar Pattern to address security challenges in the Cloud Native Applications. 

![](/assets/blog20200513_sidecar/proxy-pattern-reverse-api-gateway-lb-sidecar-1-evolution.png){:width="80%"}{:style="display:block;margin:auto;"}

<!-- more --> 

### The Proxy Pattern

Wikipedia gives us a good detailed definition.

> In computer programming, the proxy pattern is a software design pattern. A proxy, in its most general form, is a class functioning as an interface to something else. The proxy could interface to anything: a network connection, a large object in memory, a file, or some other resource that is expensive or impossible to duplicate. In short, a proxy is a wrapper or agent object that is being called by the client to access the real serving object behind the scenes [...]
>  
> [https://en.wikipedia.org/wiki/Proxy_pattern](https://en.wikipedia.org/wiki/Proxy_pattern)

In brief, a Proxy is:

1. A software design pattern.
2. An interface to something. 
3. Agnostic, because It could interface to anything.
4. A wrapper or agent.

> [...] Use of the proxy can simply be forwarding to the real object, or can provide additional logic. In the proxy, extra functionality can be provided, for example caching when operations on the real object are resource intensive, or checking preconditions before operations on the real object are invoked. For the client, usage of a proxy object is similar to using the real object, because both implement the same interface. 
>  
> [https://en.wikipedia.org/wiki/Proxy_pattern](https://en.wikipedia.org/wiki/Proxy_pattern)

And a proxy can:

1. Forward the load.
2. Provide additional logic.


### A single pattern with multiple uses

There are several products that implements the proxy pattern, between them we have:

| 1. HTTP Reverse Proxy 
| ---
| A [Reverse Proxy](https://en.wikipedia.org/wiki/Reverse_proxy) takes requests from the internet and forwards them to server in an internal network. The most used are Apache HTTP server and NGINX HTTP server.
| ---

| 2. Mediator 
| ---         
| Frequently used in the SOA world, the [Mediator Pattern](https://en.wikipedia.org/wiki/Mediator_pattern) is one of [the 23 Design Patterns](https://en.wikipedia.org/wiki/Design_Patterns) and tries to solve tight coupling. The products family most known are SOA and Message-Oriented Middleware (Message Brokers) products such as:  MOM (Apache ActiveMQ, Apache Qpid, etc.) and SOA (Apache Synapse, Apache Camel, etc.).   
| ---


| 3. API Gateway
| ---
| The API Gateway is a [pattern frequently used in Microservices-based applications](https://microservices.io/patterns/apigateway.html) and also there are many productcs that implement it. The API Gateway is a proxy and single entry point into the application, all requests first go through the API Gateway, it then routes request to the appropriate microservice. It will often handle a request by invoking multiple services and aggregating the responses. Here some API Gateway products: Kong API Gateway, Netflix Zuul, WSO2 API Manager, Mule Runtime (it embeds an API Gateway), Amazon API Gateway, etc.
| ---

| 4. Ingress Controller
| ---
| An [Ingress Controller](https://kubernetes.io/docs/concepts/services-networking/ingress-controllers) is component in the Kubernetes Cluster that routes the incomming traffic to internal services (north-south traffic) according to Kubernetes [Ingress resources](https://kubernetes.io/docs/concepts/services-networking/ingress). Since the Ingress Controller is an important element in the traffic management in K8s Cluster, It is able to provide features such as Load Balancing, WAF, API Gateway, etc. Some products are: Azure Application Gateway, Envoy Proxy based (Ambassador API Gateway, Gloo, Contour, Istio, Kong Kuma, etc.), NGINX Ingress, HAProxy Ingress, Linkerd, Traefik, etc.
| ---


| 5. Load Balancer & Traffic Router
| ---
| A [Load Balancer](https://en.wikipedia.org/wiki/Load_balancing_(computing)), software or hardware, acts as a reverse proxy and distributes network or application traffic (L4-L7 traffic) across a number of servers, with the aim of making their overall processing more efficient. A Load Balancer is also known as Traffic Router. Some examples: NGINX, HAProxy, Apache HTTP server, AWS ELB (ALB - L7 traffic, NLB - L4 traffic), Traefik, etc.
| ---

| 6. Sidecar Proxy
| ---
| Just as a sidecar is attached to a motorcycle; a sidecar proxy is attached to a main application, generally running in containers, to extend or add functionality. A Sidecar Proxy is an application design pattern which abstracts certain features, such as inter-service communications, monitoring and **security**, away from the main application to ease its maintainability, resilience and scalability of the application as a whole. Examples: Envoy Proxy, Caddy Server, Linkerd, Hashicorp Consul, etc.
| ---


### Security use cases addressed by Sidecar Proxy

The [OWASP Security Design Principles](https://github.com/OWASP/DevGuide/blob/master/02-Design/01-Principles%20of%20Security%20Engineering.md) provide a good framework to identify security concerns in when designing a Cloud Native Application specifically if it is container-based, and It will help us to outline the Security use cases.


OWASP Security Design Principle | Security concern / Use case
---                             | ---                           
1) Defense in depth.            | Sidecar is a **distributed firewall** and a **security layer in the last-mile**. 
2) Fail safe.                   | Sidecar can implement the "Container Adapter Pattern" providing a secure default interface.
3) Least privilege.             | Sidecar bootstraps credentials to initialize and secure data plane, you can not communicate with other without sidecar.
4) Separation of duties.        | Once established a data plane connecting sidecars you will be able to create segments based on business domains or duties.
5) Economy of mechanism.        | Sidecar's purpose is to reduce complexity of main application. Sidecar offloads that complexity from main application. 
6) Complete mediation.          | Sidecar is the single entry point to main application. All incoming communication to main application are checked.
7) Open Design.                 | All sidecars are managed through its standard and public API.
8) Least common mechanism.      | Related with segmentation. Sidecars can load a single configuration profile according the type or role of main application.
9) Psychological acceptability. | A standard and mature RESTful API helps to its smooth adoption. 
10) Weakest link.               | Sidecar allows injecting faults to simulate service disruption with intention of make it reliable and resilient.
11) Leveraging existing components. | Sidecar can implement the "Container Ambassador Pattern" which provides a standard and homogeneous proxy to get a knew application interface, in this way avoid increase the attack surface of multiple different applications.


As you can see above, a single Sidecar implementation **potentialy can address all security concerns** what [OWASP Security Design Principles](https://github.com/OWASP/DevGuide/blob/master/02-Design/01-Principles%20of%20Security%20Engineering.md) states. 


![](/assets/blog20200513_sidecar/proxy-pattern-reverse-api-gateway-lb-sidecar-2-owasp-security-principles.png){:width="100%"}{:style="display:block;margin:auto;"}

The only difficult is be able to identify the security use case and understand your own technical constrains (i.e. don't introduce aditional hops, able to modify main application, etc.). A good reading about this is the Christian Posta's presentation published on Nov 19, 2019 ([The Truth About the Service Mesh Data Plane](https://www.slideshare.net/ceposta/the-truth-about-the-service-mesh-data-plane)).

### Why a Security Building Block?

**What is a Building Block?**  
According to [Wikipedia](https://en.wikipedia.org/wiki/Building_block), there are multiples meanings, this for example: 
> Components that are part of a larger system.

This means that the building blocks are a set of pieces or elements that form a unified whole. And applying this concept to Security in Cloud Native Applications, the Sidecar Proxy is the vehicle to embed one or all security controls according the security use cases identified above.  
Some examples:

* My application requires TLS but it can't handle its termination, then a sidecar proxy could implement that feature.
* My application exposes a service over HTTP, but HTTPS is mandatory, then a sidecar proxy could reject HTTP requests.
* My application must be accessed over TLS (HTTPS) with Mutual TLS Authn with a specific type of X.509 Cert, then a sidecar proxy switch to HTTPS and validate the client TLS Cert.
* I want to simulate the disruption of my service, a sidecar proxy in front of that service can simulate an error.
* I want to implement a mini Web Application Firewall (WAF) only for a microservice, a sidecar proxy could inspect (mediate) incoming traffic and identify DoS or flooding attacks.
* Etc. 

### Conclusions

1. Different type of Proxies and different combinations are required to address different requirements. For example, if your API requires `throttling` and `retries`, an API Gateway is most adecuate, but if `circuit breaking` is required, an API Gateway and a sidecar proxy for each microservice are needed.
2. Use a Sidecar Proxy implementation only to address any or all security concerns. Using an only implementation reduce the complexity and improve the mantenability. In this conext, [Envoy Proxy](https://www.envoyproxy.io) is the right implementation (see below list).
3. End-to-end security (along the traffic) is only possible if security is implemented in both endpoints, but using a sidecar proxy always is mandatory in the last-mile.
4. If you are going to use a combination of proxies, try to use those that implement a standard interface (API) or implement the "Adapter Pattern". In other words, choose an API Gateway and Sidecar Proxy if both speak the same idiom or if both implement the same Management API. For example, Gloo (as API Gateway and Ingress Controller) and Envoy Proxy. Other combination is Ambassador (API Gateway) and Envoy.

![](/assets/blog20200513_sidecar/envoy-proxy-logo.png){:width="40%"}{:style="display:block;margin:auto;"}  
_<center>Envoy Proxy can be used as Sidecar, Ingress Controller, API Gateway, LB or a WAF</center>_

### Proxy, Load Balancer, Gateway, Ingress and Sidecar comparison

The list below is an attempting to collect all active proxy implementations, open source and commercial products, highlighting their security features and comparing others.  

[](){:name="proxy-doc-link"}

<iframe src="https://docs.google.com/spreadsheets/d/e/2PACX-1vRm6AK8YknTLJlm3ZcCq8NuWflMU6P2fJ1ixtdebwGFYGxz98gtCAgzYWMJ7YXFYoNTho2gx7-se1Cz/pubhtml?widget=true&amp;headers=false" width="800" height="800"></iframe>


_<center>Proxies, LB, Gateway and Sidecar Tools</center>_

### References

* [The universal data plane API - Matt Klein / Sep 6, 2017](https://blog.envoyproxy.io/the-universal-data-plane-api-d15cec7a)
* [Service mesh data plane vs. control plane - Matt Klein / Oct 10, 2017](https://blog.envoyproxy.io/service-mesh-data-plane-vs-control-plane-2774e720f7fc)
* [The Truth About the Service Mesh Data Plane - Christian Posta / Nov 19, 2019](https://www.slideshare.net/ceposta/the-truth-about-the-service-mesh-data-plane)
* [Sidecar Proxy a Silent Partner for Better Security (Webinar) - Ariel Shuper / Feb 13, 2020](https://www.brighttalk.com/webcast/18009/383304/sidecar-proxy-a-silent-partner-for-better-security)
* [Data Gateways in the Cloud Native Era - Bilgin Ibryam / May 7, 2020](https://www.infoq.com/articles/data-gateways-cloud-native/)

