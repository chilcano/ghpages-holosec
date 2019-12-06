---
layout: post
title: Install Mirth Connect 2.1 on Ubuntu 10.10 with MySQL Server
date: 2011-05-09 18:02:49.000000000 +02:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- HL7
- Linux
- SOA
tags:
- ESB
- MIRTH
- MULE
meta:
  _edit_last: '578869'
  _wpas_done_twitter: '1'
  tagazine-media: a:7:{s:7:"primary";s:86:"http://dl.dropbox.com/u/2961879/blog20110509_mirth_connect/MirthConnectAdminLogin2.png";s:6:"images";a:3:{s:84:"http://dl.dropbox.com/u/2961879/blog20110509_mirth_connect/mirthconnect_logowide.png";a:6:{s:8:"file_url";s:84:"http://dl.dropbox.com/u/2961879/blog20110509_mirth_connect/mirthconnect_logowide.png";s:5:"width";s:3:"250";s:6:"height";s:2:"50";s:4:"type";s:5:"image";s:4:"area";s:5:"12500";s:9:"file_path";s:0:"";}s:88:"http://dl.dropbox.com/u/2961879/blog20110509_mirth_connect/mirthconnect_architecture.png";a:6:{s:8:"file_url";s:88:"http://dl.dropbox.com/u/2961879/blog20110509_mirth_connect/mirthconnect_architecture.png";s:5:"width";s:3:"491";s:6:"height";s:3:"368";s:4:"type";s:5:"image";s:4:"area";s:6:"180688";s:9:"file_path";s:0:"";}s:86:"http://dl.dropbox.com/u/2961879/blog20110509_mirth_connect/MirthConnectAdminLogin2.png";a:6:{s:8:"file_url";s:86:"http://dl.dropbox.com/u/2961879/blog20110509_mirth_connect/MirthConnectAdminLogin2.png";s:5:"width";s:3:"789";s:6:"height";s:3:"520";s:4:"type";s:5:"image";s:4:"area";s:6:"410280";s:9:"file_path";s:0:"";}}s:6:"videos";a:0:{}s:11:"image_count";s:1:"3";s:6:"author";s:6:"578869";s:7:"blog_id";s:7:"2005905";s:9:"mod_stamp";s:19:"2011-05-09
    17:02:49";}
  twitter_cards_summary_img_size: a:6:{i:0;i:250;i:1;i:50;i:2;i:3;i:3;s:23:"width="250"
    height="50"";s:4:"bits";i:8;s:4:"mime";s:9:"image/png";}
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2011/05/09/install-mirth-connect-ubuntu-mysql/"
---
![Mirth Connect, SOA, ESB, HL7]({{ site.baseurl }}/assets/mirthconnect_logowide.png)

  


Mirth Connect is a Service Oriented Integration Platform based on Mule ESB. Mirth Connect is specifically designed for HL7 (Health Level Seven - set of standards for the health messages exchanging) message exchange.

  


Mirth Connect as a SOA Platform provides tools for developing, testing, deploying, and monitoring interfaces (channel for the exchange of HL7 messages).

  


[caption id="" align="alignnone" width="491" caption="Mirth Connect Architecture"]![Mirth Connect Architecture]({{ site.baseurl }}/assets/mirthconnect_architecture.png)[/caption]

  


Mirth Connect is a project Open Source with Mozilla Public License 1.1 (MPL 1.1) and can be downloaded from here h[ttp://www.mirthcorp.com/community/downloads](http://www.mirthcorp.com/community/downloads)

  


Well, we will explain how to install on Ubuntu and how to switch Derby database server to MySQL server.

  


## 1\. Install Java

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
amawta@yachaywasi:~$ sudo apt-get install openjdk-6-jdk  
  
[/sourcecode]

  


## 2\. Install Mirth Connect

  


1\. Download and setup execution permissions:

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
amawta@yachaywasi:~$ chmod +x mirthconnect-2.1.0.5389.b671-unix.sh  
  
amawta@yachaywasi:~$ sudo ./mirthconnect-2.1.0.5389.b671-unix.sh  
  
[/sourcecode]

  


2\. Verify Mirth Connect installation:

  


Go to http://localhost:8080 where you can access to Mirth Connect Server via Java App Admin console.  
  
The administrator user and password are admin and admin, change it!.  
  
If you can not connect to server, then start it.

  


3\. Start Mirth Connect from Manager (mcmanager), Server (mcserver) or as Service (mcservice).

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
amawta@yachaywasi:/opt/mirthconnect$ sudo ./mcservice start  
  
[/sourcecode]

  


verify again Mirth Connect is running.

  


## 3\. Switch from Derby database to MySQL server

  


1\. Install MySQL:

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
amawta@yachaywasi:~$ sudo apt-get install mysql-server mysql-client  
  
[/sourcecode]

  


and verify if MySQL is running:

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
amawta@yachaywasi:~$ netstat -tulpan  
[/sourcecode]

or starting:

[sourcecode language="text" gutter="true" wraplines="false"]  
amawta@yachaywasi:~$ sudo service mysql start  
[/sourcecode]

after, connect to MySQL:

[sourcecode language="text" gutter="true" wraplines="false"]  
amawta@yachaywasi:~$ mysql -u root -p  
[/sourcecode]

2\. Create an empty database and an user for Mirth Connect:

[sourcecode language="text" gutter="true" wraplines="false"]  
CREATE DATABASE mirthdb DEFAULT CHARACTER SET utf8;  
GRANT ALL ON mirthdb.* TO mirthuser@'localhost' IDENTIFIED BY 'demodemo' WITH GRANT OPTION;  
GRANT ALL ON mirthdb.* TO mirthuser@'%' IDENTIFIED BY 'demodemo' WITH GRANT OPTION;  
[/sourcecode]

If you want to connect from other PC different to localhost, then edit /etc/mysql/my.cnf  
and comment the line "bind-address = 127.0.0.1", after re-start MySQL.

3\. Stop Mirth Connect:

[sourcecode language="text" gutter="true" wraplines="false"]  
amawta@yachaywasi:/opt/mirthconnect$ sudo ./mcservice stop  
[/sourcecode]

4\. Edit the `%MIRTH_HOME%/conf/mirth.properties` file and set the “database” property to the database of your choice, for example “mysql” (no quotes).  
Also, set the “database.url”, “database.username”, and “database.password” properties. In this case are:

[sourcecode language="text" gutter="true" wraplines="false"]  
database = mysql  
database.url =jdbc:mysql://localhost:3306/mirthdb  
database.username = mirthuser  
database.password = demodemo  
[/sourcecode]

6\. Restart the Mirth Connect Server and check logs.  
Mirth Connect server will create a new database schema:

[sourcecode language="text" gutter="true" wraplines="false"]  
amawta@yachaywasi:/opt/mirthconnect$ sudo ./mcserver  
INFO 2011-05-09 16:52:13,769 [Thread-1] com.mirth.connect.server.Mirth: Mirth Connect 2.1.0.5389 (Built on April 25, 2011) server successfully started.  
INFO 2011-05-09 16:52:13,771 [Thread-1] com.mirth.connect.server.Mirth: This product was developed by Mirth Corporation (http://www.mirthcorp.com) and its contributors (c)2005-2011.  
INFO 2011-05-09 16:52:13,772 [Thread-1] com.mirth.connect.server.Mirth: Running OpenJDK Server VM 1.6.0_20 on Linux (2.6.35-22-generic, i386), mysql, with charset UTF-8.  
[/sourcecode]

To verify new schema do the following:

[sourcecode language="text" gutter="true" wraplines="false"]  
amawta@yachaywasi:~$ mysqlshow -u mirthuser -p mirthdb  
Enter password:  
Database: mirthdb  
+--------------------+  
| Tables |  
+--------------------+  
| ALERT |  
| ALERT_EMAIL |  
| ATTACHMENT |  
| CHANNEL |  
| CHANNEL_ALERT |  
| CHANNEL_STATISTICS |  
| CODE_TEMPLATE |  
| CONFIGURATION |  
| ENCRYPTION_KEY |  
| EVENT |  
| MESSAGE |  
| PERSON |  
| SCHEMA_INFO |  
| SCRIPT |  
| TEMPLATE |  
+--------------------+  
[/sourcecode]

7\. Now you could connect to Mirth, go to http://localhost:8080, start Mirth Connect Administrator, the default user and password will be admin/admin, change it again.

[caption id="" align="alignnone" width="473" caption="Mirth Connect Administration"]![Mirth Connect Administration]({{ site.baseurl }}/assets/MirthConnectAdminLogin2.png)[/caption]

Regards.
