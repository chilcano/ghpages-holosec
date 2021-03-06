---
layout:     post
title:      'Data routing, transformation, and system mediation in Big Data & IoT scenarios with Apache NiFi'
date:       2016-12-02 00:06:47
categories: ['BAM', 'Big Data', 'DevOps', 'IoT', 'Observability']
tags:       ['Apache NiFi']
status:     publish 
permalink:  "/2016/12/02/data-routing-transformation-and-system-mediation-in-big-data-iot-scenarios-with-apache-nifi/"
---
So a few months ago I published a serie of post explaining how to capture WIFI traffic and process it near to real time by using [WSO2 BAM](http://wso2.com/more-downloads/business-activity-monitor/), [CEP Siddhi](https://github.com/wso2/siddhi), Apache Cassandra, [Apache Thrift](https://thrift.apache.org/), [Kismet](https://www.kismetwireless.net) running on a Raspberry Pi and Docker.

[![](/assets/blog20161202/01-wifi-traffic-capture-wso2-bam.png){:width="70%"}](/assets/blog20161202/01-wifi-traffic-capture-wso2-bam.png){:target="_blank"}

Now, after several Big Data and Security projects, I can add to previous solution, fresh air and improve the technological approach.

<!-- more -->


## Using Elasticsearch, Logstash and Kibana

Well, the first approach I considered was starting with [ELK stack](https://www.elastic.co) (Elasticsearch, Logstash and Kibana), that is the natural way to follow.

![](/assets/blog20161202/02-wifi-traffic-capture-elasticsearch-logstash-kibana.png){:width="70%"}

But, there are still some issues to face:
* Deal with the resilience.  
* Several times Logstash stops because it was processing a malformed incoming message.
* Portability.  
* Logstash uses Java, Ruby and should be compiled and tuned for ARM architectures (Raspberry Pi). Yes, there are some instructions to do that, but I don't want to spent time to do that and I would like to focus on data analysis.
* Large scaling.  
* I would like to avoid to deploy Logstash in each Raspberry Pi just to transform in JSON the captured 802.11 (WIFI) traffic and send it to Elasticsearch. Other approach what I want to avoid is to deploy Logstash with the UDP/TCP Input Plugin in the Elasticsearch side, because both choices need parse/transform/filter the captured traffic by using [GROK ](https://www.elastic.co/guide/en/logstash/current/plugins-filters-grok.html)and Elasticsearch Index Templates for each Logstash instance deployed. What if I have 100 or more Raspberry Pi distributed in different locations?.
* Security.  
* I'm using Kismet installed in each Raspberry Pi to capture 802.11 traffic, by default Kismet sends that traffic over UDP, UDP is faster but not secure. The big problem with Logstash listening UDP traffic over a port is that Logstash is susceptible to DoS attacks and the traffic to be spoofed. I have to update UDP to the "secure UDP", UDP over SSL/TLS for example.
* Monitoring/Tracking.  
* How to monitor if Kismet is running in the Raspberry Pi?, How to know if Raspberry Pi is healthy ?.
* Administrable remotely. 
* Definitely I can't do that in a massively distributed Raspberry Pi's.
Then, what can I do ?....

## Apache NiFi to the rescue!


![](/assets/blog20161202/03-apache-nifi-logo.png){:width="300"}

I was involved in several Integration Project where I frequently used [WSO2 ESB](http://wso2.com/products/enterprise-service-bus/).
WSO2 ESB is based on [Apache Synapse](https://synapse.apache.org/), it is a lightweight and high-performance Enterprise Service Bus (ESB). Powered by a fast and asynchronous mediation engine, It provides support for XML, SOAP and REST. It supports HTTP/S, Mail (POP3, IMAP, SMTP), JMS, TCP, UDP, VFS, SMS, XMPP and FIX through "mediators".
Other opensource and popular choice is [Apache Camel](http://camel.apache.org). Also We can consider ETL tools such as [Pentaho Data Integration](http://community.pentaho.com/projects/data-integration/) (a.k.a Pentaho Kettle), but all them are too heavy to use with/in a Raspberry Pi. Until I found the Apache NiFi.
Taken from Apache NiFi webpage:
> _Apache NiFi supports powerful and scalable directed graphs of data routing, transformation, and system mediation logic. Some of the high-level capabilities and objectives of Apache NiFi include:_
> _Web-based user interface._  
>  _\- Seamless experience between design, control, feedback, and monitoring_  
>  _Highly configurable._  
>  _\- Loss tolerant vs guaranteed delivery_  
>  _\- Low latency vs high throughput_  
>  _\- Dynamic prioritization_  
>  _\- Flow can be modified at runtime_  
>  _\- Back pressure_  
>  _Data Provenance._  
>  _\- Track dataflow from beginning to end_  
>  _Designed for extension_  
>  _\- Build your own processors and more_  
>  _\- Enables rapid development and effective testing_  
>  _Secure._  
>  _\- SSL, SSH, HTTPS, encrypted content, etc..._  
>  _\- Multi-tenant authorization and internal authorization/policy management._
What do you think about that? Do you think that Apache NiFi can help me ?. Yes, It does. The new approach would be as follows:

![](/assets/blog20161202/04-wifi-traffic-capture-apache-nifi-minifi.png){:width="70%"}

The above choice covers basically all gaps above explained. In the side of Raspberry Pi we could use [Apache MiNiFi](https://cwiki.apache.org/confluence/display/MINIFI/MiNiFi), a subproject of NiFi suitable for constrained resources. The specific goals comprise:
* small and lightweight footprint
* central management of agents
* generation of data provenanceFor other side, the below choice is also a valid alternative. Even as PoC that demonstrates the ease and the power of using Apache NiFi, this approach is enough.

![](/assets/blog20161202/05-wifi-traffic-capture-apache-nifi.png){:width="70%"}

In the next post I will share technical details and code to implement the above approach. Meanwhile I share four great resources:
* [When SysOps need workflow.... Introducing Apache NiFi](https://www.linkedin.com/pulse/when-sysops-need-workflow-introducing-apache-nifi-jeroen-jacobs)
* [Real-Time Data Flows with Apache NiFi](http://www.slideshare.net/manishgforce/realtime-data-flows-with-apache-nifi)
* [Integrating Apache Spark and NiFi for Data Lakes](http://www.slideshare.net/HadoopSummit/integrating-apache-spark-and-nifi-for-data-lakes)
* [Integrating Apache NiFi and Apache Kafka](http://bryanbende.com/development/2016/09/15/apache-nifi-and-apache-kafka)

## Conclusions

* Apache NiFi as system mediator (data routing, transformation, etc.) to does data routing, data streaming, move big data chunks, pull, push and put from/to different sources of data, is the perfect companion for Big Data projects.
* Apache NiFi speaks different languages through [Processors](https://nifi.apache.org/docs/nifi-docs/). I can replace Logstash with all Input and Output Plugins easily. I can connect Apache NiFi to Elasticsearch (Put/Fetch Elasticsearch), Apache Hadoop (PutHDFS, FetchHDFS), Twitter, Kafka, etc.
