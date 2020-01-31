---
layout: post
title:  "Security along the Container-based Software Development Life Cycle"
date:   2020-02-05 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh','sdlc','DevSecOps'] 
tags: ['kubernetes', 'docker', 'container', 'microservice', 'pentesting', 'SAST', 'DAST', 'IAST', 'RASP']
permalink: "/2020/02/05/security-along-the-container-based-software-development-life-cycle"
comments: true
---

Containers are becoming the standard deployment/shipment unit of software, and that in the Cloud-based Application Security world means 2 additional things:

* The Software Applications are distributed.
* The minimum unit of deployment and shipment is the container.

In other words, using containers we are adding a new element to be considered along the software development life cycle (SDLC) as a new additional piece of software, and from Architectural point of view, those new pieces of software will be distributed. 

[![Security along the Container-based SDLC - Overview](/assets/img/xxxxx.png "Security along the Container-based SDLC - Overview")](/assets/img/xxxxx.png){:target="_blank"}

<!-- more -->


> *SAST (Static Application Security Testing)*, also known as “white box testing” has been around for more than a decade. It allows developers to find security vulnerabilities in the application source code earlier in the software development life cycle. It also ensures conformance to coding guidelines and standards without actually executing the underlying code.

> *DAST (Dynamic Application Security Testing)*, also known as “black box” testing, can find security vulnerabilities and weaknesses in a running application, typically web apps. It does that by employing fault injection techniques on an app, such as feeding malicious data to the software, to identify common security vulnerabilities, such as SQL injection and cross-­site scripting. DAST can also cast a spotlight in runtime problems that can’t be identified by static analysis­­ for example, authentication and server configuration issues, as well as flaws visible only when a known user logs in.
> https://en.wikipedia.org/wiki/Dynamic_application_security_testing

> *IAST (Interactive Application Security Testing)*. Because both SAST and DAST are older technologies, there are those who argue they lack what it takes to secure modern web and mobile apps. For example, SAST has a difficult time dealing with libraries and frameworks found in modern apps. That’s because static tools only see the application source code they can follow. What’s more, libraries and third­party components often cause static tools to choke, producing “lost sources” and “lost sinks” messages. The same is true for frameworks. Run a static tool on an API, web service or REST endpoint, and it won’t find anything wrong in them because it can’t understand the framework.
> https://en.wikipedia.org/wiki/Application_security

> *RASP (Run-time Application Security Protection)*. As with IAST, RASP, or Run­time Application Security Protection, works inside the application, but it is less a testing tool and more a security tool. It’s plugged into an application or its run­time environment and can control application execution. That allows RASP to protect the app even if a network’s perimeter defenses are breached and the apps contain security vulnerabilities missed by the development team. RASP lets an app run continuous security checks on itself and respond to live attacks by terminating an attacker’s session and alerting defenders to the attack.
> https://en.wikipedia.org/wiki/Runtime_application_self-protection


## Container Security Tools

### 1) Cleaning everything

## References

1. [QA and Security in Development Process - By Roger Carhuatocto, July 2005](https://www.slideshare.net/rcarhuatocto/qa-and-security-in-development-process)
2. [Systems development life cycle - Wikipedia](https://en.wikipedia.org/wiki/Systems_development_life_cycle)
3. [SP 800-190 - Application Container Security Guide - By NIST, September 2017](https://csrc.nist.gov/publications/detail/sp/800-190/final)
4. [29 Docker security tools compared - By Mateo Burillo, November 2018](https://sysdig.com/blog/20-docker-security-tools/)
5. xxx
6. xxx
7. xxx
8. 