---
layout:     post
title:      "Sidecar Proxy: The Security Building Block"
#date:       2020-04-23 10:00:00 +0100
categories: ['cloud', 'apaas', 'security','patterns'] 
tags:       ['sidecar', 'proxy', 'ingress', 'gateway', 'load balancer']
#permalink:  "/2020/04/3/sidecar-proxy-the-security-building-block"
comments:   true
---
Just as a HTTP reverse proxy is sitting in front of a web application, a sidecar is attached to a motorcycle, a sidecar proxy is attached to a main application to extend or add functionality. A Sidecar Proxy is an application design pattern which abstracts certain features, such as inter-service communications, monitoring and *security*, away from the main application to ease its maintainability, resilience and scalability of the application as a whole. 

In this post I will show you how to use the Sidecar Pattern to address security challenges in the Cloud Native Applications. 

imagen xxxxxxxxxxxxxxxxxxxxx


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

#### 1. HTTP Reverse Proxy

The most used are Apache HTTP server and NGINX HTTP server.

#### 2. Mediator

Frequently used in the SOA world, the Mediator Pattern is one of [the 23 Design Patterns](https://en.wikipedia.org/wiki/Design_Patterns) and tries to solve tight coupling. The products family most known are SOA and Message-Oriented Middleware (Message Brokers) products such as:
- Apache ActiveMQ, Apache Qpid, etc.
- Apache Synapse, Apache Camel, etc.

#### 3. API Gateway

#### 4. Ingress Controller

#### 5. Load Balancer & Traffic Router

#### 6. Sidecar Proxy


### Security use cases addressed by Sidecar Proxy

xxxxxx


### Comparison

[](){:name="proxy-doc-link"}

<iframe src="https://docs.google.com/spreadsheets/d/e/2PACX-1vRm6AK8YknTLJlm3ZcCq8NuWflMU6P2fJ1ixtdebwGFYGxz98gtCAgzYWMJ7YXFYoNTho2gx7-se1Cz/pubhtml?widget=true&amp;headers=false" width="800" height="800"></iframe>


_<center>Proxies, LB, Gateway and Sidecar Tools</center>_

### References

https://servicemesh.es

Service mesh data plane vs. control plane
https://blog.envoyproxy.io/service-mesh-data-plane-vs-control-plane-2774e720f7fc

The universal data plane API
https://blog.envoyproxy.io/the-universal-data-plane-api-d15cec7a

Sidecar Proxy a Silent Partner for Better Security
About this webinar
https://www.brighttalk.com/webcast/18009/383304/sidecar-proxy-a-silent-partner-for-better-security





How to write WASM filters for Envoy and deploy it with Istio
https://banzaicloud.com/blog/envoy-wasm-filter
https://github.com/solo-io/wasme




https://docs.microsoft.com/en-us/azure/architecture/patterns/sidecar

https://www.infoq.com/articles/data-gateways-cloud-native/


https://searchitoperations.techtarget.com/definition/sidecar-proxy
