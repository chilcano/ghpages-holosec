---
layout:     post
title:      'rTail - a tool to collect and view the WSO2 Logs in a browser'
date:       2015-11-20 19:22:14
categories: ['Microservices', 'Observability', 'SOA']
tags:       ['Logging', 'rTail', 'Wiremock', 'WSO2']
status:     publish 
permalink:  "/2015/11/20/rtail-a-tool-to-collect-and-view-the-wso2-logs-in-a-browser/"
---
If you want some lightweight tool and easy to use to collect, track and visualise all type of events of your logs in real time and from an unique and standard way as a browser, then rTail is your tool.
From rTail Github page <https://github.com/kilianc/rtail>:
>  `rtail` is a command line utility that grabs every line in `stdin` and broadcasts it over **UDP**. That's it. Nothing fancy. Nothing complicated. Tail log files, app output, or whatever you wish, using `rtail` broadcasting to an `rtail-server` – See multiple streams in the browser, in realtime.  

<!-- more -->


## Why logging is important in Microservices?
If you think that implement (Micro)services is to put your services in containers and forgot of them, then you are wrong. You have to monitor the performance and health at Application and System level.
There are many tools out there, opensource, commercial, on-cloud, such as log.io, ELK, Clarity, Tailon, frontail, etc. In my opinion, for development and a VM the most simple, fresh and lightweight tool is rTail.
The logging is a time consuming process and we have to prepare before in order to be more productive.
The de-facto tool is [ELK (Elasticsearch, Logstash and Kibana)](https://www.elastic.co), useful and powerful, but for me is "using a sledge-hammer to crack a nut" (matar moscas a cañonazos).

## How I started to use it?
You can follow the instructions in rTail webpage, obviously this is the best way, but if you want to go further, you could use my Vagrant box and Puppet modules already configured, where WSO2 ESB, WSO2 AM, WSO2 DSS, WSO2 GREG and Wiremock are sending event logs to the rTail server, also configured. In just 10 minutes you will get a Ubuntu VM with WSO2, Wiremock running and rTail collecting logs as shown below.

![box-vagrant-wso2-dev-srv-rtail-logs]({{ site.baseurl }}/assets/chilcano-box-vagrant-wso2-dev-srv-rtail-logs.png)
If you want more details, you can use [my Github repository](https://github.com/Chilcano/box-vagrant-wso2-dev-srv) for this project.  
<https://github.com/Chilcano/box-vagrant-wso2-dev-srv>
Enjoy it.
