---
layout: post
title: Install Alfresco 3.4c CE in 5 minutes
date: 2011-02-10 00:13:30.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- ECM
tags:
- Alfresco
meta:
  _edit_last: '578869'
  _wpas_done_twitter: '1'
  _oembed_62e4e05af73e21e2af8a3f7bcb3fc0e8: "{{unknown}}"
  _oembed_1cb477388014eab904e82d31a3372454: "{{unknown}}"
  _oembed_d3fb22c45b3c66c7fbc820db34ae4c7a: "{{unknown}}"
  _oembed_7305276a1ea094b387d8202069ed0d40: "{{unknown}}"
  _oembed_cc5fc29251d4cd35c76a78a0a300cd6b: "{{unknown}}"
  _oembed_6c50d5a112d14de2a7a326e1905ea280: "{{unknown}}"
  _oembed_7b638af77dc53107cd93c4363fa284a7: "{{unknown}}"
  _oembed_8654d5b00176f18025aaba835647ddf8: "{{unknown}}"
  _oembed_ff0f9045f1b15fbdd483973a4c43e77f: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2011/02/10/install-alfresco-34c-5-minutes/"
---
Sometimes it is necessary a fresh installation of Alfresco for testing purposes and of a quickly way. In my case i want to create virtual machines with different configurations and variations of Alfresco.  
  
Well, this post explains how to do an installation quickly of Alfresco.

  


## I. Pre-requisites

  


  

  * Virtual Box machine with Windows XP SP2 as guest S.O. and 1024 MB Ram.
  

  * JDK 1.6.0_13 installed.
  

  * MySQL Server 5.1.33-community.
  

  * Alfresco 3.4c CE installer downloaded (alfresco-community-3.4.c-installer-win-x32.exe).
  

  


## II. Installation

  


1\. Create an empty database for Alfresco named, in my case, "alf34c_db1".

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
C:\1bpms-demo\xampplite\mysql\bin>mysql -u root -p  
  
Enter password:  
  
Welcome to the MySQL monitor. Commands end with ; or \g.  
  
Your MySQL connection id is 3  
  
Server version: 5.1.33-community MySQL Community Server (GPL)

  


Type 'help;' or '\h' for help. Type '\c' to clear the buffer.

  


mysql> CREATE DATABASE alf34c_db1 DEFAULT CHARACTER SET utf8;  
  
mysql> GRANT ALL ON alf34c_db1.* TO alf_user1@'localhost' IDENTIFIED BY 'demodemo' WITH GRANT OPTION;  
  
mysql> GRANT ALL ON alf34c_db1.* TO alf_user1@'localhost.localdomain' IDENTIFIED BY 'demodemo' WITH GRANT OPTION;

  


Query OK, 0 rows affected (0.00 sec)

  


mysql> quit;  
  
Bye  
  
[/sourcecode]

  


2\. Execute the Alfresco installer.

  


3\. In "Installation type" selects "Advance - Configures serve ports and service properties", then in the "Database Installation" popup to select "I wish to use an existing database". Then, enter the following:

  


[caption id="" align="alignnone" width="407" caption="Alfresco installer - Database configuration"]![Alfresco installer - Database configuration]({{ site.baseurl }}/assets/1.database_install.png)[/caption]

  


4\. When finalizing installer, run ${ALF_HOME}/tomcat/bin/startup.bat.

  


5\. When starting you can see in the catalina out how to Alfresco create database structure and populates with initial data. Also, you can see several errors in catalina out console because you need modify JVM variables.

  


[caption id="" align="alignnone" width="521" caption="OutOfMemoryError and PermGen error when starting Alfresco"]![OutOfMemoryError and PermGen error when starting Alfresco]({{ site.baseurl }}/assets/2.1st_alfresco_startup_error.png)[/caption]

  


Then, add or modify JAVA_OPTS variable in Tomcat to ${ALF_HOME}/tomcat/bin/setenv.bat file. This file looks like to:

  


[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
set JAVA_OPTS=-Xms256m -Xmx1024m -Xss96k -XX:MaxPermSize=256m -server  
set JAVA_HOME=C:\1BPMS-~1\alf34c_1\java  
set JAVA_OPTS=%JAVA_OPTS%  
[/sourcecode]

In some case, you need to copy jaxb-api-2.1.jar from ${ALF_HOME}/tomcat/webapps/alfresco/WEB-INF/lib to ${ALF_HOME}/tomcat/endorsed.

6\. Restart.

When finished, open browser and go to <http://localhost:8080/alfresco>. You can log in to Alfresco with admin/admin.

END.

**References** :

1) Error al arrancar el servidor alfresco 3.2  
[http://forums.alfresco.com/es/viewtopic.php?f=5&t=1785](http://forums.alfresco.com/es/viewtopic.php?f=5&t=1785)

2) java.lang.OutOfMemoryError: PermGen space  
<http://forums.alfresco.com/en/viewtopic.php?t=14451>
