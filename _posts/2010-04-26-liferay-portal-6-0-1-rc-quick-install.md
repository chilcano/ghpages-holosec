---
layout: post
title: Liferay Portal 5.2.3 & 6.0.1 RC quick install
date: 2010-04-26 17:09:15.000000000 +02:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Portal
tags:
- Liferay Portal
meta:
  _edit_last: '578869'
  _wpas_done_twitter: '1'
  geo_public: '0'
  _wpas_skip_5053092: '1'
  _wpcom_is_markdown: '1'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/04/26/liferay-portal-6-0-1-rc-quick-install/"
---
Hi there!

  
In the last two months I had to perform many Liferay instalations with different configurations, here I put some quick notes for a clean installation of Liferay (for version 5.2.3 and 6.0.1 RC1) very necessary if we want to develop applications on Liferay.

  
[![Liferay Portal 6.0.1 RC1]({{ site.baseurl }}/assets/liferay_601.png)](http://holisticsecurity.files.wordpress.com/2010/04/liferay_601.png) _Liferay Portal 6.0.1 RC1_  
  
<!-- more -->

  
  

  1. Unzip **liferay-portal-XXX.zip** in **%LIFERAY_HOME%**
  

  2.   
Change the default portsand avoid conflicts. Edit the file **%LIFERAY_HOME%\tomcat-YYY\conf\server.xml** and change the follow ports:

  

  

  
>   
> 8080 -> 9090  
>   
> 8005 -> 9005  
>   
> 8443 -> 9443  
>   
> 8009 -> 9009  
> 

  
  

  1. Edit the Pool connection in **%LIFERAY_PORTAL%\tomcat-YYY\webapps\ROOT\WEB-INF\classes\portal-ext.properties** (this file is not included in 5.2.3 and 6.0.1 bundle)
  

  
>   
> jdbc.default.jndi.name=jdbc/LiferayPool

  
  

  1. Edit **%LIFERAY_HOME%\tomcat-YYY\conf\Catalina\localhost\ROOT.xml** file and to add database (i.e. mysql) connection.  
  
 _ROOT.xml_
  

  
>   
>  […]
> 
>   
> 
> 
> […]
> 
>   
> 

  
  

  1.   
Ensure that the MySQL JDBC Driver exists in **%LIFERAY_HOME%\tomcat-YYY\lib\ext** folder.

  

  2. Create an empty Database with the necessary rights. Execute the follow sql command:


> CREATE DATABASE lportal_db CHARACTER SET utf8;  
>  GRANT ALL ON lportal_db.* TO “YourUserName“@”localhost”; 

  1. Start Liferay Portal server. Execute **%LIFERAY_HOME%\tomcat-YYY\bin\startup.bat**

These notes also apply to linux too.  
Bye.
