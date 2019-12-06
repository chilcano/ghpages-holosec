---
layout: post
title: Creating a VM with WSO2 servers for development with Vagrant & Puppet
date: 2015-11-11 23:43:38.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Cloud
- Linux
- SOA
tags:
- Puppet
- Ubuntu
- Vagrant
- Wiremock
- WSO2
meta:
  _wpcom_is_markdown: '1'
  _edit_last: '578869'
  _wpas_skip_5053092: '1'
  geo_public: '0'
  publicize_google_plus_url: https://plus.google.com/+RogerCarhuatocto/posts/1mo73BXHEDj
  _publicize_job_id: '16766883004'
  _publicize_done_5110107: '1'
  _wpas_done_5053089: '1'
  _publicize_done_external: a:1:{s:7:"twitter";a:1:{i:13849;s:54:"https://twitter.com/Chilcano/status/664574265283338244";}}
  _publicize_done_17477: '1'
  _wpas_done_13849: '1'
  publicize_twitter_user: Chilcano
  _wpas_skip_5053089: '1'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2015/11/11/creating-a-vm-with-wso2-servers-for-development/"
---
This VM is suitable to develop with WSO2 products and puts focus only in the _server side_ (WSO2 servers, mock server and different tools to host our microservices) and not in _desktop side_ (Eclipse, SoapUI, Maven, etc.).

  


![Vagrant box - WSO2 Development Server Map]({{ site.baseurl }}/assets/chilcano-box-vagrant-wso2-dev-srv-map1.png)Vagrant box - WSO2 Development Server Map

  


  


The main objetive is to have a VM with all WSO2 products installed and configured to be ready for development and following the most common Middleware infrastructure pattern used to create Microservices.

  


The _naming_ used in _hostnames_ tries to use pre-defined values what also will be used in Integration and Production Environments. The _ports_ and _offsets_ used do not follow any special rule.

  


The WSO2 servers and tools installed and configured are:

  


  

  * wiremock-1.57-standalone.jar 
  

  * wso2am-1.8.0.zip 
  

  * wso2dss-3.5.0.zip 
  

  * wso2esb-4.8.1.zip 
  

  * wso2greg-5.1.0.zip 
  

  


And they are deployed following the above map.  
  
For further details can be found in my [repository of Github for this project](https://github.com/Chilcano/box-vagrant-wso2-dev-srv).

  


Enjoy it.

  

