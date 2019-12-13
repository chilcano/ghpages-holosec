---
layout:     post
title:      'Creating a VM with WSO2 servers for development with Vagrant & Puppet'
date:       2015-11-11 22:43:38
categories: ['Cloud', 'Open Source', 'SOA']
tags:       ['Puppet', 'Ubuntu', 'Vagrant', 'Wiremock', 'WSO2']
status:     publish 
permalink:  "/2015/11/11/creating-a-vm-with-wso2-servers-for-development/"
---
This VM is suitable to develop with WSO2 products and puts focus only in the _server side_ (WSO2 servers, mock server and different tools to host our microservices) and not in _desktop side_ (Eclipse, SoapUI, Maven, etc.).

![Vagrant box - WSO2 Development Server Map]({{ site.baseurl }}/assets/chilcano-box-vagrant-wso2-dev-srv-map1.png)Vagrant box - WSO2 Development Server Map

<!-- more -->

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
