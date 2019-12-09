---
layout:     post
title:      'Web-SSO between a CAS-ified Java webapp and Liferay using CAS'
date:       2011-07-28 22:12:48
categories: ['Portal', 'Security']
tags:       ['CAS', 'Liferay', 'SSO']
status:     publish 
permalink:  "/2011/07/29/sso-cas-ified-java-webapp-liferay/"
---
## 1\. Introduction
When working a Liferay Portal in Organizations with existing web applications, generally new web applications will need to be integrated in current Authentication and Web-SSO service.
This document explains how to create new java web applications knowing that they will use the AuthN service and will Web-SSO with Liferay.
Although, this document is for new java web applications, existing web applications (based in java, php,. net, ruby, ...) can also use it as this document explains the most important steps to perform.  
This document can be taken as a set of best practices (including source code) when you want to integrate with CAS.

**Note:**
* This document does not say how to do Single-Sign Out or logout, only Single-Sign On or login.  

## 2\. Use cases when CAS-ifying web applications

### Use case #1: Authentication

[caption id="" align="alignnone" width="487" caption="Use case #1: Authentication"][![Use case #1: Authentication]({{ site.baseurl }}/assets/sso-liferay-casified-webapp_1usecaseauth.png)](http://dl.dropbox.com/u/2961879/blog20110728_sso_liferay_cas_ified_javawebapps/sso-liferay-casified-webapp_1usecaseauth.png)[/caption]

### Use case #2: Web-SSO

To perform Web-SSO, we will need 2 webapps, the first one is Liferay, and the second one is webappA. Both should be authenticated with CAS-server.  
The main difference between authentication and web-sso processes is that CAS shares authenticated session through CAS Service Manager.

### Use case #3: Web Single Sign Off or Single Log Out

Single Sign Out or Single Log Out means that CAS-server contacts each webapp and notifies them that you have logged out. Then you should invalidate or delete all cookies stored in your web browser.  
This protocol is implemented in Java CAS client library only.

### Use case #4: Logout

To perform a logout means (involves) to close authenticated session in CAS-server side. Afterwards, you will need to make sure that the cookie does not exist in your web browser.

## 3\. Process of installation and deployment of a CAS-ified webapp

When you install and deploy any web application in an environment where Web-SSO is enabled, you should to following steps.

[caption id="" align="alignnone" width="507" caption="Task to do with/in CAS-ified webapp"][![Task to do with/in CAS-ified webapp]({{ site.baseurl }}/assets/sso-liferay-casified-webapp_2process.png)](http://dl.dropbox.com/u/2961879/blog20110728_sso_liferay_cas_ified_javawebapps/sso-liferay-casified-webapp_2process.png)[/caption]  
Generally, the steps 1, 2 and 3 have already been made and it is only pending to how to configure CAS-client, CAS-service manager, java webapp, etc.

### CAS service manager

It is necessary to define an URL for identify the new web application. This URL will be called the “URL Service”:
* Identify the CAS-ified webapp trying to authenticate in CAS-server. In almost all cases, this will be the URL of the web application.  
* In a successfully login process is used to redirect web browser to URL specified.  
* It is used as filter for CAS Services Management in Web-SSO process allowing to do SSO between webapps registered. 

[caption id="" align="alignnone" width="511" caption="Define URL for CAS-ified webapp"][![Define URL for CAS-ified webapp]({{ site.baseurl }}/assets/sso-liferay-casified-webapp_3cassrvmngr.png)](http://dl.dropbox.com/u/2961879/blog20110728_sso_liferay_cas_ified_javawebapps/sso-liferay-casified-webapp_3cassrvmngr.png)[/caption]
To replace the URL/IP for yours.

[caption id="" align="alignnone" width="590" caption="Define URL to filter webapp to perform SSO"][![Define URL to filter webapp to perform SSO]({{ site.baseurl }}/assets/sso-liferay-casified-webapp_4cassrvmngr.png)](http://dl.dropbox.com/u/2961879/blog20110728_sso_liferay_cas_ified_javawebapps/sso-liferay-casified-webapp_4cassrvmngr.png)[/caption]

### Develop your Java Webapp and configure Tomcat

Firstly, you have to design your scenario to do Web-SSO. In this case, We have 3 servers:
* lfry01 or svdapp85 (IP 192.168.56.101, HTTP port 6060): Liferay Portal 
* lfry02 (IP 192.168.56.102 or IP 10.16.111.135, HTTP port 8080): Tomcat server hosting new CAS-ified Java Web App 
* blcr00 or svdapp85 (IP 192.168.56.103, SSL port 6443): Tomcat server hosting CAS server and CAS service manager 
Our scenario will look like to following diagram.

[caption id="" align="alignnone" width="523" caption="Scenario where we will deploy our CAS-ified webapp."][![Scenario where we will deploy our CAS-ified webapp.]({{ site.baseurl }}/assets/sso-liferay-casified-webapp_5scenarioauthn.png)](http://dl.dropbox.com/u/2961879/blog20110728_sso_liferay_cas_ified_javawebapps/sso-liferay-casified-webapp_5scenarioauthn.png)[/caption]
1\. Install Tomcat, enable SSL ( **lfry02** ) using _server.xml_ sample:

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version='1.0' encoding='utf-8'?>  
<Server port="8005" shutdown="SHUTDOWN">  
<Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />  
<Listener className="org.apache.catalina.core.JasperListener" />  
<Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />  
<Listener className="org.apache.catalina.mbeans.ServerLifecycleListener" />  
<Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />  
<GlobalNamingResources>  
<Resource name="UserDatabase" auth="Container"  
type="org.apache.catalina.UserDatabase"  
description="User database that can be updated and saved"  
factory="org.apache.catalina.users.MemoryUserDatabaseFactory"  
pathname="conf/tomcat-users.xml" />  
</GlobalNamingResources>  
<Service name="Catalina">  
<Connector port="8080" protocol="HTTP/1.1"  
connectionTimeout="20000"  
redirectPort="8443" />  
<Connector protocol="org.apache.coyote.http11.Http11NioProtocol"  
port="8443" maxHttpHeaderSize="8192" SSLEnabled="true"  
maxThreads="150"  
enableLookups="false" disableUploadTimeout="true"  
acceptCount="100" scheme="https" secure="true"  
clientAuth="false" sslProtocol="TLS"  
keystoreFile="D:\devel\usr-dev\apache-tomcat-6.0.29\webapps\mywebappssotest1.jks"  
keystorePass="changeit"  
truststoreFile="D:\devel\usr-dev\jdk_6u21\Java\lib\security\cacerts" />  
<!-- My CAS URL service = https://localhost:8443/webssotest1/protected/ -->  
<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />  
<Engine name="Catalina" defaultHost="localhost">  
<Realm className="org.apache.catalina.realm.UserDatabaseRealm"  
resourceName="UserDatabase"/>  
<Host name="localhost" appBase="webapps"  
unpackWARs="true" autoDeploy="true"  
xmlValidation="false" xmlNamespaceAware="false">  
</Host>  
</Engine>  
</Service>  
</Server>  

[/sourcecode]
2\. Generate key pair and export certificate SSL of this Tomcat ( **lfry02** ).
3\. Install CAS root certificate ( **blcr00** ) as trusted cert into JVM cacert repository of Tomcat ( **lfry02** ).
4\. Use this Java webapp ( **[webssotest1.war](http://dl.dropbox.com/u/2961879/blog20110728_sso_liferay_cas_ified_javawebapps/webssotest1.war)** ), deploy this webapp into Java Web Server ( **lfry02** ).
5\. Test this web application in HTTP and HTTPS mode.

### Connect your Java Webapp to CAS

_We have created a basic sample of java web application based in JSP ( **webssptest1.war** ). We recommend that you try to deploy and configure this web application before trying something more complex._
1\. In **blcr00** use existing standard model of authentication based on usr/pwd where both are equals. If Liferay has already used this model, this webapp should follow too.
2\. In **lfry02** (tomcat **web.xml** ) configure CAS server, to use this sample file:

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<web-app id="webssotest1"  
version="2.4"  
xmlns="http://java.sun.com/xml/ns/j2ee"  
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">  
<display-name>webssotest1</display-name>  
<description>  
WebSSO sample, how to use CAS Java Client 3.x.  
In this sample exists a public area (/)  
and a private area (/protected/*).  
</description>
<filter>  
<filter-name>CAS Authentication Filter</filter-name>  
<filter-class>org.jasig.cas.client.authentication.AuthenticationFilter</filter-class>  
<init-param>  
<param-name>casServerLoginUrl</param-name>  
<param-value>https://svdapp85:6443/cas-server-webapp-3.3.5/login</param-value>  
</init-param>  
<init-param>  
<param-name>serverName</param-name>  
<param-value>https://10.16.111.135:8443</param-value>  
</init-param>  
<init-param>  
<param-name>renew</param-name>  
<param-value>false</param-value>  
</init-param>  
<init-param>  
<param-name>gateway</param-name>  
<param-value>false</param-value>  
</init-param>  
</filter>  
<filter>  
<filter-name>CAS Validation Filter</filter-name>  
<filter-class>org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter</filter-class>  
<init-param>  
<param-name>casServerUrlPrefix</param-name>  
<param-value>https://svdapp85:6443/cas-server-webapp-3.3.5/</param-value>  
</init-param>  
<init-param>  
<param-name>serverName</param-name>  
<param-value>https://10.16.111.135:8443</param-value>  
</init-param>  
<init-param>  
<param-name>proxyCallbackUrl</param-name>  
<param-value>https://svdapp85:6443/cas-server-webapp-3.3.5/proxyCallback</param-value>  
</init-param>  
<init-param>  
<param-name>proxyReceptorUrl</param-name>  
<param-value>/mywebappssotest1/proxyCallback</param-value>  
</init-param>  
</filter>  
<filter>  
<filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>  
<filter-class>org.jasig.cas.client.util.HttpServletRequestWrapperFilter</filter-class>  
</filter>  
<filter>  
<filter-name>CAS Assertion Thread Local Filter</filter-name>  
<filter-class>org.jasig.cas.client.util.AssertionThreadLocalFilter</filter-class>  
</filter>  
<filter-mapping>  
<filter-name>CAS Authentication Filter</filter-name>  
<url-pattern>/protected/*</url-pattern>  
</filter-mapping>  
<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/*</url-pattern>  
</filter-mapping>  
<filter-mapping>  
<filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>  
<url-pattern>/*</url-pattern>  
</filter-mapping>  
<filter-mapping>  
<filter-name>CAS Assertion Thread Local Filter</filter-name>  
<url-pattern>/*</url-pattern>  
</filter-mapping>  
<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/proxyCallback</url-pattern>  
</filter-mapping>
<welcome-file-list>  
<welcome-file>index.jsp</welcome-file>  
</welcome-file-list>  
</web-app>  

[/sourcecode]
3\. Make sure that exists CAS-client java libraries into **WEB-INF/lib** directory, as follow:

[caption id="" align="alignnone" width="331" caption="CAS-client java libraries to be included in webssotest1.war"][![CAS-client java libraries to be included in webssotest1.war]({{ site.baseurl }}/assets/sso-liferay-casified-webapp_6caslibs.png)](http://dl.dropbox.com/u/2961879/blog20110728_sso_liferay_cas_ified_javawebapps/sso-liferay-casified-webapp_6caslibs.png)[/caption]
4\. Test login and SSO with liferay.
Login to Liferay (lfry01), go to following URL, for example: http://svdapp85:6060/en/group/intix/home  
You will be redirected to CAS login form, to entry an user/password valid in CAS-server. 

[caption id="" align="alignnone" width="562" caption="First login (CAS) when trying log into Liferay"][![First login \(CAS\) when trying log into Liferay]({{ site.baseurl }}/assets/sso-liferay-casified-webapp_7caslogin.png)](http://dl.dropbox.com/u/2961879/blog20110728_sso_liferay_cas_ified_javawebapps/sso-liferay-casified-webapp_7caslogin.png)[/caption]
Now, from same opened browser, switch to the CAS-ified webapp. Open following protected URL: http://10.16.111.135:8080/webssotest1/protected/  
If all is OK, then you can see URL without user/pwd prompted.

[caption id="" align="alignnone" width="491" caption="Successfully Web-SSO in CAS-ified webapp (webssotest1.war)"][![Successfully Web-SSO in CAS-ified webapp \(webssotest1.war\)]({{ site.baseurl }}/assets/sso-liferay-casified-webapp_8ssook.png)](http://dl.dropbox.com/u/2961879/blog20110728_sso_liferay_cas_ified_javawebapps/sso-liferay-casified-webapp_8ssook.png)[/caption]
The same applies if you do log into webssotest1.war first and then go to liferay.  
The CAS login form is the first page displayed when trying to login from any application. This form is shared by any web application you want to do Web-SSO.
END.

**Reference:**
* [JA-SIG Java Client Simple WebApp Sample](https://wiki.jasig.org/display/CASC/JA-SIG+Java+Client+Simple+WebApp+Sample)
* Web-SSO between Liferay and Alfresco with CAS and Penrose [part 1](http://holisticsecurity.wordpress.com/2011/01/15/sso-liferay-alfresco-cas-penrose-part-12/) and [part 2](http://holisticsecurity.wordpress.com/2011/02/19/web-sso-between-liferay-and-alfresco-with-cas-and-penrose-part-22/)
