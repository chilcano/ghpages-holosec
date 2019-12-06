---
layout: post
title: Running multi-container (WSO2 BAM & MAC Address Lookup) Docker Application
  using Docker Compose
date: 2016-03-09 12:20:44.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Big Data
- Microservices
- Security
- SOA
tags:
- Cassandra
- Docker
- Kismet
- Raspberry Pi
- Siddhi
meta:
  _wpcom_is_markdown: '1'
  _edit_last: '578869'
  geo_public: '0'
  _publicize_done_external: a:1:{s:7:"twitter";a:1:{i:13849;s:54:"https://twitter.com/Chilcano/status/707526614393819137";}}
  _publicize_job_id: '20582854468'
  publicize_google_plus_url: https://plus.google.com/+RogerCarhuatocto/posts/6EzUi8agm1p
  _publicize_done_5110107: '1'
  _wpas_done_5053089: '1'
  publicize_linkedin_url: https://www.linkedin.com/updates?discuss=&scope=6985267&stype=M&topic=6113292237578268672&type=U&a=AS8b
  _publicize_done_5110110: '1'
  _wpas_done_5053092: '1'
  _publicize_done_17477: '1'
  _wpas_done_13849: '1'
  publicize_twitter_user: Chilcano
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2016/03/09/running-multi-container-wso2-bam-mac-address-lookup-docker-application-using-docker-compose/"
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