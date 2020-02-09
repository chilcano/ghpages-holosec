---
layout: post
title: "Security along the Container-based SDLC"
date: 2020-02-10 10:00:00 +0100
categories: ['cloud','sdlc','DevSecOps', 'holism'] 
tags: ['kubernetes', 'container', 'microservice', 'sca', 'pentesting', 'SAST', 'DAST', 'IAST', 'RASP']
permalink: "/2020/02/10/security-along-the-container-based-sdlc"
comments: true
---

Nowadays, containers are becoming the standard deployment unit of software, and that in the Cloud-based Application Security world means 2 things:

* The Software Applications are **distributed** into containers.
* The minimum unit of **deployment** and **shipment** is the container.

In other words, using containers we are adding a new element to be considered along the **[Software Development Life Cycle (SDLC)](https://en.wikipedia.org/wiki/Systems_development_life_cycle){:target="_blank"}** as a new additional piece of software (containers), and from Architectural point of view, those new pieces of software will be distributed.

Said that, the purpose of this post is explain you how to embed **Security along the Container-based SDLC (Secure-SDLC)** and how to **DevOps** practices will help its adoption.

[![Security along the Container-based SDLC - Overview](/assets/img/20200210-security-along-container-based-sdlc-v1.png "Security along the Container-based SDLC - Overview")](/assets/img/20200210-security-along-container-based-sdlc-v1.png){:target="_blank"}

The above Secure-SDLC diagram was created using the classic [Systems development life cycle published at Wikipedia](https://en.wikipedia.org/wiki/Systems_development_life_cycle){:target="_blank"} and software engineering and security-related techniques such as:

- [The Twelve-Factor App](https://12factor.net){:target="_blank"} is a methodology for building software-as-a-service apps.
- [SP 800-190 - Application Container Security Guide - By NIST, September 2017](https://csrc.nist.gov/publications/detail/sp/800-190/final){:target="_blank"}.
- [Exploring container security: An overview - By Maya Kaczorowski, Security & Privacy Product Manager at Google, March 2018](https://cloud.google.com/blog/products/gcp/exploring-container-security-an-overview){:target="_blank"}.
- Container Security — From Image Analysis to Network Segmentation, Options Are Maturing - By Gartner, August 2018
   * [Gartner Research](https://www.gartner.com/en/documents/3888664/container-security-from-image-analysis-to-network-segmen){:target="_blank"}
   * [](https://twitter.com/with_joerg/status/1034528900972507138?s=20){:target="_blank"}.
- Some thoughts picked from [QA and Security in Development Process - By Roger Carhuatocto, July 2005](https://www.slideshare.net/rcarhuatocto/qa-and-security-in-development-process){:target="_blank"}.

<!-- more -->

## Security-related Techniques and Practices

In the above picture you can see some security-related techniques and practices used in traditional security approaches that still can and must use. Yes, there are not nothing new. The only thing that changes are the tools. Below, at the end of post, you will see a list of *open source security tools* that can be used in each security technique proposed in the *Secure-SDLC*.

**SAST (Static Application Security Testing)**, also known as “white box testing” has been around for more than a decade. It allows developers to find security vulnerabilities in the application source code earlier in the software development life cycle. It also ensures conformance to coding guidelines and standards without actually executing the underlying code.

**DAST (Dynamic Application Security Testing)**, also known as “black box” testing, can find security vulnerabilities and weaknesses in a running application, typically web apps. It does that by employing fault injection techniques on an app, such as feeding malicious data to the software, to identify common security vulnerabilities, such as SQL injection and cross-­site scripting. DAST can also cast a spotlight in runtime problems that can’t be identified by static analysis­­ for example, authentication and server configuration issues, as well as flaws visible only when a known user logs in.
> https://en.wikipedia.org/wiki/Dynamic_application_security_testing

**IAST (Interactive Application Security Testing)**. Because both SAST and DAST are older technologies, there are those who argue they lack what it takes to secure modern web and mobile apps. For example, SAST has a difficult time dealing with libraries and frameworks found in modern apps. That’s because static tools only see the application source code they can follow. What’s more, libraries and third­party components often cause static tools to choke, producing “lost sources” and “lost sinks” messages. The same is true for frameworks. Run a static tool on an API, web service or REST endpoint, and it won’t find anything wrong in them because it can’t understand the framework.
> https://en.wikipedia.org/wiki/Application_security

**RASP (Run-time Application Security Protection)**. As with IAST, RASP, or Run­time Application Security Protection, works inside the application, but it is less a testing tool and more a security tool. It’s plugged into an application or its run­time environment and can control application execution. That allows RASP to protect the app even if a network’s perimeter defenses are breached and the apps contain security vulnerabilities missed by the development team. RASP lets an app run continuous security checks on itself and respond to live attacks by terminating an attacker’s session and alerting defenders to the attack.
> https://en.wikipedia.org/wiki/Runtime_application_self-protection

**Misuse cases**. 
> https://en.wikipedia.org/wiki/Misuse_case

**Abuse cases**. 
> https://en.wikipedia.org/wiki/Abuse_case

**Intrusion Detection System (IDS)**
> https://en.wikipedia.org/wiki/Intrusion_detection_system

**Software Composition Analysis (SCA) / Software Supply Chain**
> https://owasp.org/www-community/Component_Analysis
> https://cloud.google.com/solutions/secure-software-supply-chains-on-google-kubernetes-engine


## From Old-school SDLC to Container-based Secure-SDLC

Three things to consider:

### Mixing old with new, adapt old to new along all stages

One of OWASP Security Principles I allways use is [Minimise the Attack Surface (Leveraging existing components)](https://github.com/OWASP/DevGuide/blob/master/02-Design/01-Principles%20of%20Security%20Engineering.md), but since I'm using container as a new element in the software development process, I have to adapt existing techniques and frameworks to mitigate the new attack vectors that container brings. That means mixing old with new and adapt old to new along all stages, for example:

1. We do Static Code Analysis, now we have to consider container's definition and its dependencies.
2. We do Application Testing and Pentesting and we have to extend Pentesting to Application into containers.
3. We do Network Segmentation and Isolation, now we have to consider a new level of segmentation and isolation that container brings.
4. We do Monitor the performance and activity of Applications, now we have to do the same but at container level and at distributed way.
5. Etc.

### Security is a process, not a product

Buying and deploying a new Security Tool will not guarantee to solve all security problems because security means different things in different stages in the SDLC and it isn't a problem absolute.
We know that every year come new security problems and new attack vectors, it is impossible to get "Absolute Security". For that, we have to embed security in all stages of SDLC because [Security is a Process, not a Product](https://www.schneier.com/essays/archives/2000/04/the_process_of_secur.html){:target="_blank"}.

### From DevOps to DevSecOps

DevOps is a culture of collaboration between teams, Developers and Operations who have traditionally operated in isolated groups working together towards a common goal: release software faster and more reliably.

But what about security?. Security people also work towards to same goal. Then why they don't work together where everyone is responsible for security. If so, that will allow teams release software faster, reliably and secure.

## Open Source Container Security Tools

The criteria I'm going to use to select the tools are:

1. Adaptable or ready to be used with containers.
2. DevOps friendly.
3. Recommended to be used with The Best Security Practices (OWASP, NIST, CIS, etc.)
4. Open Source.

<iframe src="https://docs.google.com/spreadsheets/d/e/2PACX-1vRTLn8bLX-Sp6JEbKcJIludCb6wJbTM-5xV5te94srdYnmLYutCu9vcgmiWcc2taioH5cJcj2xXH_Ba/pubhtml?gid=0&amp;single=true&amp;widget=true&amp;headers=false" width="800" height="800"></iframe>

> [See whole document](/assets/pages/2020-02-10-security-along-the-container-based-sdlc-oss-tool-list){:target="_blank"}  
>  
> If don't see a open source security product in the document that is worth being reviewed, please, drop me an email and I'll add to document and review it.

## Conclusions

 1. Although you can do it, you shouln'd use all those open source security tools without outlining your SDLC before, without identifying the security challenges you want to face in each SDLC' stage and without to identifying the existing security practices you are using. 
 2. The [Container has certain inherent characteristics that impact the way the application behaves](https://www.redhat.com/en/resources/cloud-native-container-design-whitepaper){:target="_blank"}, its misuse may create breachs that can be exploited. Make sure to review the way that container images are created in early SDLC' stages and to use [RASP](https://en.wikipedia.org/wiki/Runtime_application_self-protection){:target="_blank"} approach in lastly SDLC' stages. 
 3. Since the containers are immutable and ephemeral, the CI/CD processes are shorters in order to deliver new pieces of applications, their updates and their dependencies. In fact, these processes without control are a common attack vector (weakest link) frequently used to introduce untrusted, unpatched, insecure third-party software. Then, make sure to adopt a [Software Composition Analysis (SCA) / Software Supply Chain](https://owasp.org/www-community/Component_Analysis){:target="_blank"} practice and a tool or framework.


That's it, I hope it helpful. In the next post I'll explain how use some of them.
Stay tuned.

## References

1. [33(+) K8s Security Tools - Sysdig / Mateo Burillo / July 2019](https://sysdig.com/blog/33-kubernetes-security-tools){:target="_blank"}.
2. [Kali Linux Tools Listing](https://tools.kali.org/tools-listing){:target="_blank"}.
3. [Top 10 Open Source Security Testing Tools for Web Applications - Hackr.io / Youssef Nader / Last Updated 05 Feb, 2020](https://hackr.io/blog/top-10-open-source-security-testing-tools-for-web-applications){:target="_blank"}.
4. [Awesome Penetration Testing / Nick Raienko](https://github.com/enaqx/awesome-pentest){:target="_blank"}.
