---
layout:     post
title:      "Sidecar Proxy: The Security Building Block"
#date:       2020-04-23 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh','patterns'] 
tags:       ['sidecar', 'proxy', 'ingress', 'gateway', 'load balancer']
#permalink:  "/2020/04/3/sidecar-proxy-the-security-building-block"
comments:   true
---
A Sidecar Proxy is an application design pattern which abstracts certain features, such as inter-service communications, monitoring and security, away from the main architecture to ease the tracking and maintenance of the application as a whole. 

https://searchitoperations.techtarget.com/definition/sidecar-proxy

Just as a sidecar is attached to a motorcycle, a sidecar proxy is attached to a parent application to extend or add functionality. Sidecar proxies are typically used within the service mesh control plane (CP), microservices or containers. 



The Proxy Pattern and its importance in the Cloud Security world.

Proxy Pattern, multiple use cases even in Security.

Proxy: a single pattern with multiple use cases even in Security.

Proxy, Load Balancer, Ingress Controller, Sidecar or API Gateway?


<!-- more --> 

## What is the Proxy Pattern?

> In computer programming, the proxy pattern is a software design pattern. A proxy, in its most general form, is a class functioning as an interface to something else. The proxy could interface to anything: a network connection, a large object in memory, a file, or some other resource that is expensive or impossible to duplicate. In short, a proxy is a wrapper or agent object that is being called by the client to access the real serving object behind the scenes. Use of the proxy can simply be forwarding to the real object, or can provide additional logic. In the proxy, extra functionality can be provided, for example caching when operations on the real object are resource intensive, or checking preconditions before operations on the real object are invoked. For the client, usage of a proxy object is similar to using the real object, because both implement the same interface. 
>  
> [https://en.wikipedia.org/wiki/Proxy_pattern](https://en.wikipedia.org/wiki/Proxy_pattern)

### Common uses

#### 1. Reverse Proxy

#### 2. Mediator

#### 3. API Gateway

#### 4. Ingress Controller

#### 5. Sidecar Proxy

#### 6. Load Balancer & Traffic Router

#### 7. 


## xxxxxxxxxx

xxxxxx


## Comparison

[](){:name="proxy-doc-link"}

<iframe src="https://docs.google.com/spreadsheets/d/e/2PACX-1vRm6AK8YknTLJlm3ZcCq8NuWflMU6P2fJ1ixtdebwGFYGxz98gtCAgzYWMJ7YXFYoNTho2gx7-se1Cz/pubhtml?widget=true&amp;headers=false" width="800" height="800"></iframe>


_<center>Proxies, LB, Gateway and Sidecar Tools</center>_

## References

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


