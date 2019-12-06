---
layout: post
title: Tomcat installation on VirtualBox's image of CentOS 5.5
date: 2010-08-13 11:41:05.000000000 +02:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Linux
- Security
tags:
- JAVA
- tomcat
meta:
  _edit_last: '578869'
  _wpas_done_twitter: '1'
  tagazine-media: a:7:{s:7:"primary";s:101:"http://dl.dropbox.com/u/2961879/blog20100813_tomcat5centos55/tomcat5centos55-2-openbrowsertomcat5.png";s:6:"images";a:2:{s:96:"http://dl.dropbox.com/u/2961879/blog20100813_tomcat5centos55/tomcat5centos55-1-mount_dvd_iso.png";a:6:{s:8:"file_url";s:96:"http://dl.dropbox.com/u/2961879/blog20100813_tomcat5centos55/tomcat5centos55-1-mount_dvd_iso.png";s:5:"width";s:3:"601";s:6:"height";s:3:"244";s:4:"type";s:5:"image";s:4:"area";s:6:"146644";s:9:"file_path";s:0:"";}s:101:"http://dl.dropbox.com/u/2961879/blog20100813_tomcat5centos55/tomcat5centos55-2-openbrowsertomcat5.png";a:6:{s:8:"file_url";s:101:"http://dl.dropbox.com/u/2961879/blog20100813_tomcat5centos55/tomcat5centos55-2-openbrowsertomcat5.png";s:5:"width";s:3:"602";s:6:"height";s:3:"607";s:4:"type";s:5:"image";s:4:"area";s:6:"365414";s:9:"file_path";s:0:"";}}s:6:"videos";a:0:{}s:11:"image_count";s:1:"2";s:6:"author";s:6:"578869";s:7:"blog_id";s:7:"2005905";s:9:"mod_stamp";s:19:"2010-12-16
    11:28:51";}
  geo_public: '0'
  _wpas_skip_5053092: '1'
  _wpcom_is_markdown: '1'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/08/13/tomcat-installation-on-virtualboxs-image-of-centos-5-5/"
---
It is very difficult to install services on servers that are not connected to the Internet directly or through a proxy (critical servers), for example It happens in production environments, in this case we have to previously download the packages, transport them on USB flash drive and repeat this process as many times as packages and dependencies we have forgotten.  
  
The situation is compounded when these critical servers are virtualized. If this is your case, I recommend you use the DVD / CD ISO images and mount them as your CentOS repositories.

  
<!-- more -->

  
If you are in doubt, follow this example for Tomcat:

  
  

  1. Mount DVD Iso image as a media-repo in your CentOS Guest Virtual Box
  

  
![]({{ site.baseurl }}/assets/tomcat5centos55-1-mount_dvd_iso.png)

  
  

  1. Verify if you have Java installed in your Guest
  

  

    
    
    [root@svdapp95 ~]# java -version  
    java version "1.6.0"  
    OpenJDK  Runtime Environment (build 1.6.0-b09)  
    OpenJDK Client VM (build 1.6.0-b09, mixed mode)

  
  

  1. Install Tomcat5 from your media-repo already mounted
  

  
 _Note:  
  
* It is not recommended to install Tomcat as ROOT, especially if It is for production environments._

  

    
    
    [root@svdapp95 ~]# yum --disablerepo=\* --enablerepo=c5-media -y install tomcat5 tomcat5-webapps tomcat5-admin-webapps

  
  

  1. Verify Tomcat5's packages installed
  

  

    
    
    [root@svdapp95 ~]# rpm -ql tomcat5  
    /etc/logrotate.d/tomcat5  
    /etc/rc.d/init.d/tomcat5  
    /etc/sysconfig/tomcat5  
    /etc/tomcat5  
    [...]  
    /var/log/tomcat5  
    /var/log/tomcat5/catalina.out

  
  1. Start Tomcat5's service
    
    
    [root@svdapp95 ~]# service tomcat5 start
    Starting tomcat5:                                          [  OK  ]

  1. Put follow URL http://localhost:8080, now you can see Tomcat's welcomepage.

![]({{ site.baseurl }}/assets/tomcat5centos55-2-openbrowsertomcat5.png)

  1. You can see Tomcat's log with:
    
    
    [root@svdapp95 ~]# tail -f /var/log/tomcat5/catalina.out

  1. End.
