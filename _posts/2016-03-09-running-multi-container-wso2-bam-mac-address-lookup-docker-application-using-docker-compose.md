---
layout:     post
title:      'Running multi-container (WSO2 BAM & MAC Address Lookup) Docker Application using Docker Compose'
date:       2016-03-09 11:20:44
categories: ['Big Data', 'Microservices', 'Security', 'SOA']
tags:       ['Cassandra', 'Docker', 'Kismet', 'Raspberry Pi', 'Siddhi']
status:     publish 
permalink:  "/2016/03/09/running-multi-container-wso2-bam-mac-address-lookup-docker-application-using-docker-compose/"
---
In my 4 previous blog post I explained each part of this Proof-of-concept, they are:
  1. Analysing Wireless traffic in real time with WSO2 BAM, Apache Cassandra, Complex Event Processor (CEP Siddhi), Apache Thrift and Python:  
    * Part I (http://wp.me/p8pPj-pE)
    * Part II (http://wp.me/p8pPj-pW)
    * Part III (http://wp.me/p8pPj-qe)
  2. A Python Microservice in a Docker Container (MAC Address Manufacturer Lookup):  
    * http://wp.me/p8pPj-qG
Now, in this blog post I'm going to explain how to run two Docker Containers, the WSO2 BAM and the MAC Address Manufacturer Lookup containers, by using Docker Compose.  

![802.11 traffic capture PoC - Docker Compose]({{ site.baseurl }}/assets/chilcano-wso2bam-cep-siddhi-wifi-kismet-thrift-cassandra-docker-compose.png)

<!-- more -->

```sh  
// clone 2 repositories  
$ git clone https://github.com/chilcano/docker-wso2bam-kismet-poc.git  
$ cd docker-wso2bam-kismet-poc  
$ git clone https://github.com/chilcano/wso2bam-wifi-thrift-cassandra-poc.git
// run docker compose  
$ docker-compose up -d
Starting dockerwso2bamkismetpoc_mac-manuf_1  
Starting dockerwso2bamkismetpoc_wso2bam-dashboard-kismet_1  
```
Above, the diagram explaining this.
Now, if you want to run all together in a few minutes, just runs the Docker Compose Yaml file.  
For a deeply explanation, follow the instructions on README.md (https://github.com/chilcano/docker-wso2bam-kismet-poc).
If everything is OK, you will get a huge amount of data (WIFI traffic) stored in Apache Cassandra and a simple Dashboard showing captured MAC Addresses and Manufacturer of the Wireless Devices (PC, Mobiles, WIFI Access Points, Tablets, etc..) around of your Raspberry Pi.

![Visualising 802.11 captured traffic with the MAC Address Manufacturer]({{ site.baseurl }}/assets/chilcano-wso2bam-wifi-thrift-cassandra-5-kismet-toolbox-docker-manuf.png)
I hope you will find this blog posts useful.  
Bye.
