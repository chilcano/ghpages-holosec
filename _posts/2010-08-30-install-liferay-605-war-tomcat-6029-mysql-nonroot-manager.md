---
layout:     post
title:      'Install Liferay 6.0.5 CE WAR on Tomcat 6.0.29 and MySQL as non root webapp and without Tomcat Manager'
date:       2010-08-30 15:24:49
categories: ['Linux', 'Portal']
tags:       ['Liferay Portal']
status:     publish 
permalink:  "/2010/08/30/install-liferay-605-war-tomcat-6029-mysql-nonroot-manager/"
---
We will explain how to install Liferay 6.0.5 CE WAR bundle in an existing Apache Tomcat 6.0.29 and MySQL Server as non ROOT webapp and without Tomcat Manager.

![]({{ site.baseurl }}/assets/liferay605war_1login.png)  

<!-- more -->

1.- Download and install [Apache Tomcat 6.0.29](http://apache.rediris.es//tomcat/tomcat-6/v6.0.29/bin/apache-tomcat-6.0.29-windows-x86.zip) standard bundle (with Tomcat Manager included).
 ~~2.- Copy **%TOMCAT_HOME%/webapps/ROOT** folder to **%TOMCAT_HOME%/webapps/liferay605** folder.~~
3.- Download **liferay-portal-6.0.5.war** and **liferay-portal-dependencies-6.0.5.zip**
4.- Unzip **liferay-portal-6.0.5.war** file and copy all content into the new Tomcat Context, in this example will be %TOMCAT_HOME%/webapps/ **liferay605** folder.
5.- Unzip and copy all dependencies to ~~**%TOMCAT_HOME%/lib**~~ **%TOMCAT_HOME%/lib/ext**
6.- Download and copy **02** extra libraries ( **jta.jar** and **mail.jar** ) to ~~**%TOMCAT_HOME%/lib**~~ **%TOMCAT_HOME%/lib/ext**
7.- Create **liferay605.xml** file into **%TOMCAT_HOME%/conf/Catalina/localhost** folder. It looks like this:

```text  
<Context path="" crossContext="true">  
<Resource  
name="jdbc/LiferayPool"  
auth="Container"  
type="javax.sql.DataSource"  
driverClassName="com.mysql.jdbc.Driver"  
url="jdbc:mysql://localhost:3306/lportal605_db?useUnicode=true&characterEncoding=UTF-8"  
username="root"  
password=""  
maxActive="20"  
/>  
</Context>  
```

In my case, DB user is "root" with empty password. You must create an empty DB in MySQL before, for example "lportal605_db".
8.- Create **portal-ext.properties** into **%TOMCAT_HOME%/webapps/liferay605/WEB-INF/classes** folder. It looks like this:
    jdbc.default.jndi.name=jdbc/LiferayPool  
    portal.ctx=/liferay605  
9.- If you are using MySQL as database server instead of HSQL, you must copy _mysql jdbc lib_ into ~~**%TOMCAT_HOME%/lib**~~ **%TOMCAT_HOME%/lib/ext** folder.
10.- Add the code below to **%TOMCAT_HOME%/lib/bin/startup.bat** or **startup.sh** :
    set JAVA_OPTS=%JAVA_OPTS% -Xmx1024m -XX:MaxPermSize=256m
11.- Run **startup.bat/sh** , you will see in console when liferay creates and populates the portal database automatically. Now, you can open browser window and enter the folow URL _http://localhost:8080/liferay605_. Use " _test@liferay.com_ " and " _test_ " as user and password respectively.

## Configure Tomcat Manager
Sometimes you need to deploy liferay in your favorite hosting provider but It is impossible because you can not change files of root context, in this case you can use only control panel of you ISP o Tomcat Manager.
Tomcat Manager is included in standard Tomcat bundle but by default It is disabled. If you want to enable it, follow the next steps:

![]({{ site.baseurl }}/assets/liferay605war_2tomcatmanager.png)
1.- Edit %TOMCAT_HOME%/conf/ **tomcat-users.xml**
2.- Add user, for example:

```text  
<?xml version='1.0' encoding='utf-8'?>  
<tomcat-users>  
<role rolename="tomcat"/>  
<role rolename="manager"/>  
<user username="roger" password="roger" roles="tomcat,manager"/>  
</tomcat-users>  
```

3.- Run Tomcat and go to http://localhost:8080/manager
4.- Enter user and password configured in step 2.
5.- Log in to Liferay for example; in Tomcat Manager now you can see an opened session in liferay605 webapp context and to explore values stored in current session.

![]({{ site.baseurl }}/assets/liferay605war_3tomcatmanagerliferay.png)
6.- End.
**References** :
* [Installing 5.2 SP3 WAR on Tomcat 6](http://www.liferay.com/es/community/wiki/-/wiki/Main/Installing+5.2+SP3+WAR+on+Tomcat+6)
* [Execute Liferay Portal on non ROOT context](http://holisticsecurity.wordpress.com/2010/07/19/execute-liferay-root-context)
