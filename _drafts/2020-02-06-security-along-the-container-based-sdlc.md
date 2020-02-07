---
layout: post
title: "Security along the Container-based SDLC"
date: 2020-02-06 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh','sdlc','DevSecOps'] 
tags: ['kubernetes', 'docker', 'container', 'microservice', 'pentesting', 'SAST', 'DAST', 'IAST', 'RASP']
permalink: "/2020/02/06/security-along-the-container-based-sdlc"
comments: true
---

Nowadays, containers are becoming the standard deployment/shipment unit of software, and that in the Cloud-based Application Security world means 2 things:

* The Software Applications are distributed into containers.
* The minimum unit of deployment and shipment is the container.

In other words, using containers we are adding a new element to be considered along the software development life cycle (SDLC) as a new additional piece of software (containers), and from Architectural point of view, those new pieces of software will be distributed.

Said that, the purpose of this post is explain how to embed Security along the Container-based SDLC and how to DevOps practices will help its adoption.

[![Security along the Container-based SDLC - Overview](/assets/img/xxxxx.png "Security along the Container-based SDLC - Overview")](/assets/img/xxxxx.png){:target="_blank"}

<!-- more -->

## SDLC and its problems

### Mixing old with new and adapt old to new along all stages

One of OWASP Security Principles I allways use is [Minimise the Attack Surface (Leveraging existing components)](https://github.com/OWASP/DevGuide/blob/master/02-Design/01-Principles%20of%20Security%20Engineering.md), but since I'm using container as a new element in the software development process, I have to adapt existing techniques and frameworks to mitigate the new attack vectors that container brings. That means mixing old with new and adapt old to new along all stages, for example:

1. We do Static Code Analysis, now we have to consider container's definition and its dependencies.
2. We do Application Testing and Pentesting and we have to extend Pentesting to Application into containers.
3. We do Network Segmentation and Isolation, now we have to consider a new level of segmentation and isolation that container brings.
4. We do Monitor the performance and activity of Applications, now we have to do the same but at container level and at distributed way.
5. Etc.

### Security is a process, not a Product

Buying and deploying a new Security Tool will not guarantee to solve all security problems because security means different things in different stages in the SDLC and it isn't a problem absolute.
We know that every year come new security problems and new attack vectors, it is impossible to get "Absolute Security". For that, we have to embed security in all stages of SDLC because [Security is a Process, not a Product](https://www.schneier.com/essays/archives/2000/04/the_process_of_secur.html){:target="_blank"}.

### DevOps and DevSecOps

DevOps is a culture of collaboration between teams, Developers and Operations who have traditionally operated in isolated groups working together towards a common goal: release software faster and more reliably.

But what about security?. Security people also work towards to same goal. Then why they don't work together where everyone is responsible for security. If so, that will allow teams release software faster, reliably and secure.


## Security along Container-based SDLC

### Criteria


1. Adapting the SDLC to Containers
2. DevOps friendly 
3. Best Security Practices (OWASP, NIST, etc.)


### Open Source Container Security Tools

[See all document](/assets/pages/2020-02-06-security-along-the-container-based-sdlc-oss-tool-list){:target="_blank"}


<iframe width="800" height="800" src="https://docs.google.com/spreadsheets/d/e/2PACX-1vRTLn8bLX-Sp6JEbKcJIludCb6wJbTM-5xV5te94srdYnmLYutCu9vcgmiWcc2taioH5cJcj2xXH_Ba/pubhtml?gid=0&single=true"></iframe>


> *SAST (Static Application Security Testing)*, also known as “white box testing” has been around for more than a decade. It allows developers to find security vulnerabilities in the application source code earlier in the software development life cycle. It also ensures conformance to coding guidelines and standards without actually executing the underlying code.

> *DAST (Dynamic Application Security Testing)*, also known as “black box” testing, can find security vulnerabilities and weaknesses in a running application, typically web apps. It does that by employing fault injection techniques on an app, such as feeding malicious data to the software, to identify common security vulnerabilities, such as SQL injection and cross-­site scripting. DAST can also cast a spotlight in runtime problems that can’t be identified by static analysis­­ for example, authentication and server configuration issues, as well as flaws visible only when a known user logs in.
> https://en.wikipedia.org/wiki/Dynamic_application_security_testing

> *IAST (Interactive Application Security Testing)*. Because both SAST and DAST are older technologies, there are those who argue they lack what it takes to secure modern web and mobile apps. For example, SAST has a difficult time dealing with libraries and frameworks found in modern apps. That’s because static tools only see the application source code they can follow. What’s more, libraries and third­party components often cause static tools to choke, producing “lost sources” and “lost sinks” messages. The same is true for frameworks. Run a static tool on an API, web service or REST endpoint, and it won’t find anything wrong in them because it can’t understand the framework.
> https://en.wikipedia.org/wiki/Application_security

> *RASP (Run-time Application Security Protection)*. As with IAST, RASP, or Run­time Application Security Protection, works inside the application, but it is less a testing tool and more a security tool. It’s plugged into an application or its run­time environment and can control application execution. That allows RASP to protect the app even if a network’s perimeter defenses are breached and the apps contain security vulnerabilities missed by the development team. RASP lets an app run continuous security checks on itself and respond to live attacks by terminating an attacker’s session and alerting defenders to the attack.
> https://en.wikipedia.org/wiki/Runtime_application_self-protection


## References

1. [QA and Security in Development Process - By Roger Carhuatocto, July 2005](https://www.slideshare.net/rcarhuatocto/qa-and-security-in-development-process)
2. [Systems development life cycle - Wikipedia](https://en.wikipedia.org/wiki/Systems_development_life_cycle)
3. [SP 800-190 - Application Container Security Guide - By NIST, September 2017](https://csrc.nist.gov/publications/detail/sp/800-190/final)
4. [29 Docker security tools compared - By Mateo Burillo, November 2018](https://sysdig.com/blog/20-docker-security-tools/)
5. xxx
6. xxx
7. xxx
8. 