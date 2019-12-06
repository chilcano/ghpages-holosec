---
layout: post
title: Using Weave Scope to explore Microservices Communication and Service Mesh (OpenShift
  and Istio)
date: 2018-01-12 01:50:10.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- DevOps
- Microservices
- PaaS
tags:
- Envoy Proxy
- Istio Mesh
- OpenShift
- Weave-Scope
meta:
  _publicize_done_external: a:1:{s:7:"twitter";a:1:{i:13849;s:54:"https://twitter.com/Chilcano/status/951617305372516354";}}
  _wpcom_is_markdown: '1'
  _rest_api_published: '1'
  _rest_api_client_id: "-1"
  _publicize_job_id: '13491192134'
  _publicize_done_17477: '1'
  _wpas_done_13849: '1'
  publicize_twitter_user: Chilcano
  publicize_linkedin_url: https://www.linkedin.com/updates?discuss=&scope=6985267&stype=M&topic=6357383004045799424&type=U&a=iVLp
  _publicize_done_5110110: '1'
  _wpas_done_5053092: '1'
  _edit_last: '578869'
  geo_public: '0'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2018/01/12/using-weave-scope-to-explore-microservices-communication-and-service-mesh-openshift-and-istio/"
---
If you are working with ESB, Message Brokers, BPMS, SOA or Microservices, you will notice that you are solving the same problems of Standalone Applications but in different way, because all of them are different kind of Distributed Application. Those problems are:

  
  

  * Users Management, Authentication and Authorisation
  

  * Logging, Debugging, Monitoring and Alerting
  

  * Clustering, High Availability, Load Balancing, etc...
  

  
<!-- more -->

  
### What is a Service Mesh?

  
It's other type of Distributed Application where the Services, or Microservices or APIs, are being interconnected. ![api-mesh-security-2-weave-scope]({{ site.baseurl }}/assets/api-mesh-security-2-weave-scope.png)

  
### Service Mesh on Containerised and Orchestrated Platform

  
Generally a Service Mesh, based on the Microservices and/or APIs, is deployed by using multiple containers hosted and orchestrated for Kubernetes. In this scenario, we need to face new challenges such as ephemeral infrastructure, zero trust network, network segmentation or adopting new methodologies to test, to monitor, to deploy, to operate, etc. That's we are calling DevOps.

  
### Deployment Container Patterns

  
Like to EIP (Enterprise Integration Patterns) and Software Design Patterns, the Deployment of Service Mesh in a Containerised Platform also has Patterns. Google, Microsoft, Netflix, etc. recommend to use some patterns to implement solutions for the problems stated above. For example, Google explains very well 3 patterns:

  
  

  * Sidecar Pattern
  

  * Ambassador Pattern
  

  * Adapter Pattern
  

  
All of them support the building of the Service Mesh based on Containers. For further details read this paper: [Design patterns for container-based distributed systems. By Brendan Burns and David Oppenheimer](https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/45406.pdf)

  
### What is Istio?

  
Extracted from [Istio.io](https://istio.io/about/intro.html):

  
>   
>  Istio is an open platform that provides a uniform way to connect, manage, and secure microservices. Istio supports managing traffic flows between microservices, enforcing access policies, and aggregating telemetry data, all without requiring changes to the microservice code. Istio gives you:
> 
>   
> 
> 
>   
> 
>   * Automatic load balancing for HTTP, gRPC, WebSocket, and TCP traffic
>   
> 
>   * Fine-grained control of traffic behavior with rich routing rules, retries, failovers, and fault injection.
>   
> 
>   * A pluggable policy layer and configuration API supporting access controls, rate limits and quotas.
>   
> 
>   * Automatic metrics, logs, and traces for all traffic within a cluster, including cluster ingress and egress.
>   
> 
>   * Secure service-to-service authentication with strong identity assertions between services in a cluster.
>   
> 
  
> 

  
![istio-architecture]({{ site.baseurl }}/assets/istio-architecture.png)

  
### Why use Istio?

Extracted from [Istio.io](https://istio.io/docs/concepts/what-is-istio/overview.html):

> Istio addresses many of the challenges faced by developers and operators as monolithic applications transition towards a distributed microservice architecture. The term service mesh is often used to describe the network of microservices that make up such applications and the interactions between them. As a service mesh grows in size and complexity, it can become harder to understand and manage. Its requirements can include discovery, load balancing, failure recovery, metrics, and monitoring, and often more complex operational requirements such as A/B testing, canary releases, rate limiting, access control, and end-to-end authentication. Istio provides a complete solution to satisfy the diverse requirements of microservice applications by providing behavioral insights and operational control over the service mesh as a whole. It provides a number of key capabilities uniformly across a network of services:
> 
>   * Traffic Management. Control the flow of traffic and API calls between services, make calls more reliable, and make the network more robust in the face of adverse conditions.
>   * Observability. Gain understanding of the dependencies between services and the nature and flow of traffic between them, providing the ability to quickly identify issues.
>   * Policy Enforcement. Apply organizational policy to the interaction between services, ensure access policies are enforced and resources are fairly distributed among consumers. Policy changes are made by configuring the mesh, not by changing application code.
>   * Service Identity and Security. Provide services in the mesh with a verifiable identity and provide the ability to protect service traffic as it flows over networks of varying degrees of trustability.
> 
### Get a container-based Service Mesh with Istio

I've created 3 Ansible Roles to get easily a minimalist OpenShift Cluster (by using Minishift), Weave Scope (https://github.com/weaveworks/scope), Istio and BookInfo Application (API and Microservices on containers) to understand what challenges we have to face. The 3 Ansible Role are:

  1. **[Minishift Ansible Role](https://github.com/chilcano/ansible-role-minishift)** To get OpenShift Cluster in a VM by using [Minishift](https://github.com/minishift/minishift)
  2. **[Weave Scope Ansible Role](https://github.com/chilcano/ansible-role-weave-scope)** To deploy Weave Scope Application in an OpenShift running locally
  3. **[Istio Ansible Role](https://github.com/chilcano/ansible-role-istio)** To deploy and configure Istio in OpenShift running locally, to deploy BookInfo Application and inject sidecar proxies(Envoy Proxy)

[I have also created some examples so that you can have an environment where you can test and try quickly with all this.](https://github.com/chilcano/ansible-minishift-istio-security) [Weave Scope Application](https://github.com/weaveworks/scope) will play a important role here, because It will allow us to monitor, visualise, troubleshot and manage **easily** whole the container-based Service Mesh. Once completed [the step-by-step guide](https://github.com/chilcano/ansible-minishift-istio-security), you will get next: [![]({{ site.baseurl }}/assets/api-mesh-security-7-weave-scope-istio-system.png)](https://holisticsecurity.files.wordpress.com/2018/01/api-mesh-security-7-weave-scope-istio-system.png) [![]({{ site.baseurl }}/assets/api-mesh-security-8-weave-scope-bookinfo.png)](https://holisticsecurity.files.wordpress.com/2018/01/api-mesh-security-8-weave-scope-bookinfo.png) [![]({{ site.baseurl }}/assets/api-mesh-security-9-weave-scope-bookinfo-mesh.png)](https://holisticsecurity.files.wordpress.com/2018/01/api-mesh-security-9-weave-scope-bookinfo-mesh.png) ..and [![]({{ site.baseurl }}/assets/api-mesh-security-4-istio-zipkin.png)](https://holisticsecurity.files.wordpress.com/2018/01/api-mesh-security-4-istio-zipkin.png) [![]({{ site.baseurl }}/assets/api-mesh-security-5-istio-grafana.png)](https://holisticsecurity.files.wordpress.com/2018/01/api-mesh-security-5-istio-grafana.png) [![]({{ site.baseurl }}/assets/api-mesh-security-6-istio-servicegraph.png)](https://holisticsecurity.files.wordpress.com/2018/01/api-mesh-security-6-istio-servicegraph.png)

Hope this helps!.  
Regards.
