---
layout:     post
title:      'Strategy to create Microservice using WSO2 and Docker'
date:       2016-01-11 12:14:23
categories: ['Microservices', 'SOA']
tags:       ['Docker', 'WSO2']
status:     publish 
permalink:  "/2016/01/11/strategy-to-create-microservices-using-wso2-and-docker/"
---
The last year I was working in a big UK's project where I had to help in creation and preparation of the WSO2 Infrastructure to expose a bunch of functionalities as (Micro)services. While we solve everyday problems in infrastructure such as Continuous Delivery, Security, Mediation, Logging, Testing, etc. I had to code many APIs and to explain how to do It to the other team members (Teaching by doing). I will explain how to create an API layer from scratch using WSO2 but avoiding the common errors and bad practices.

[![Docker Containers to develop \(micro\)services with WSO2]({{ site.baseurl }}/assets/chilcano-wso2-dev-srv-docker-containers-map.png)](https://github.com/chilcano/docker-wso2-dev-srv)

<!-- more -->


## Microservice Infrastructure
Well, before stating you should choose your preferred Products/Frameworks/Stacks/PaaS and set up your Development and Production Environment Infrastructure. Actually there are a few good Products/Frameworks/Stacks/PaaS ready to create and manage the Microservice Lifecycle such as:
1. Dropwizard (http://www.dropwizard.io)
2. Deis (http://deis.io)
3. Spring Boot (http://projects.spring.io/spring-boot)
4. Ratpack (https://ratpack.io)
5. Spark with Unirest (http://sparkjava.com)
6. Vertx (http://vertx.io)
7. Seneca (http://senecajs.org - in Javascript / node.js)
8. WSO2 MSS (http://wso2.com/products/microservices-server - in Alpha)
9. Zato (https://zato.io - in Python)
>  If you want start working with Microservices but using WSO2 technology, I have created a set of (Docker WSO2 containers)(https://github.com/chilcano/docker-wso2-dev-srv) where in a few minutes you will have a development platform running.  
Just follow these steps:

```text  
$ git clone https://github.com/chilcano/docker-wso2-dev-srv.git  
$ cd docker-wso2-dev-srv  
$ docker build --rm -t chilcano/wso2-esb:4.8.1 wso2esb/4.8.1  
$ docker run --detach -t --name=wso2esb01a -p 19449:9443 chilcano/wso2-esb:4.8.1  
```

Or you can use the existing containers available in DockerHub (https://hub.docker.com/r/chilcano):

```text  
//Previous steps:  
$ git clone https://github.com/chilcano/docker-wso2-dev-srv.git  
$ cd docker-wso2-dev-srv  
//Build image:  
$ docker build --rm -t chilcano/wso2-esb wso2esb/latest  
//Run container:  
$ docker run -d -t --name=wso2esb01a -p 19449:9443 chilcano/wso2-esb  
//Check the WSO2 server:  
$ docker exec -i -t wso2esb01a bash  
root@4178f21fcad3:/opt/wso2esb01a/bin

# tail -f ../repository/logs/wso2carbon.log  
$ curl -v -k https://<IP_ADDRESS>:19449/services/Version  
```


## Microservice Methodology
Obviously, the best methodology is what is created based on experience, there are good books and guides for that:
* Building Microservices By Sam Newman (http://shop.oreilly.com/product/0636920033158.do)
* Microservices by Eberhard Wolff (http://microservices-book.com)
* Microservices Guide by Martin Fowler (http://martinfowler.com/microservices)
Also is very important internalize and understand the recommendations and best practices when creating (Micro)services, for example, here you could start with **The twelve-factor app is a methodology (http://12factor.net)** and implement the Support Infrastructure for Microservice such as:
* Service Discovery
* Continuous Delivery & Continuous Integration
* Security & Resilience
* Monitoring
* Logging, etc.
At the end, You will be ready to get starting to work with (Micro)services.
