---
layout:     post
title:      'Web-SSO between Liferay and Alfresco with CAS and Penrose (part 1/2)'
date:       2011-01-15 21:57:59
categories: ['ECM', 'IAM', 'Portal', 'Security']
tags:       ['Alfresco', 'LDAP', 'Penrose', 'SSO']
status:     publish 
permalink:  "/2011/01/15/sso-liferay-alfresco-cas-penrose-part-12/"
---
I know it, It is nothing new. But I always encounter this situation and I have always come back to explain again and again.
The requirements are:
1. CAS for Authentication and SSO.
2. Web application to do SSO between they: Liferay Portal 6.0.5 CE and Alfresco 3.2 CE.
3. Penrose Virtual Directory with OpenDS as backend to store user credentials and to get a LDAP interface.
This post is based on a previous one about [Liferay Portal Server LDAP Authentication with Penrose Server](http://holisticsecurity.wordpress.com/2010/12/22/authentication-penrose-directory-liferay), I recommend you read it for it will be easier to follow.

## I. Install and configure CAS server

**Note:**
* CAS server v3.3.5 comes with appropriate libraries for Tomcat 5 and OpenJDK bundled in CentOS. Otherwise you will have to recompile and / or include some libraries more.
1\. See preview post on "Liferay Portal Server LDAP Authentication with Penrose Server" ([here](http://holisticsecurity.wordpress.com/2010/12/22/authentication-penrose-directory-liferay))
2\. Download CAS server (http://www.jasig.org/cas/download/cas-server-335-final) and deploy cas-server-webapp-3.3.5.war into any Java Web Server, in this case we will deploy into Tomcat server previuosly installed in CentOS box.
In my case, CentOS has installed Penrose Virtual Directory Server and has already loaded a LDAP tree with several users/identities (see [details](Liferay Portal Server LDAP Authentication with Penrose Server) in last blog post).
3\. Verify if Tomcat is installed into CentOS:

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  

[root@directorysrv1 /]# rpm -ql tomcat5  
/etc/logrotate.d/tomcat5  
/etc/rc.d/init.d/tomcat5  
/etc/sysconfig/tomcat5  
/etc/tomcat5  
/etc/tomcat5/Catalina  
/etc/tomcat5/Catalina/localhost  
...  
/var/log/tomcat5  
/var/log/tomcat5/catalina.out  

[root@directorysrv1 /]#  

[/sourcecode]
If tomcat is not installed, you can download RPM packages and then install it:

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  

[root@directorysrv1 /]# yum install tomcat5 tomcat5-webapps tomcat5-admin-webapps  

[/sourcecode]
We are using OpenJDK (this is the CentOS Java by default):

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  

[root@directorysrv1 /]# java -version  
java version "1.6.0"  
OpenJDK Runtime Environment (build 1.6.0-b09)  
OpenJDK Client VM (build 1.6.0-b09, mixed mode)  

[root@directorysrv1 /]#  

[/sourcecode]
4\. Copy CAS server (cas-server-webapp-3.3.5.war) in Tomcat and start the server:

[sourcecode language="text" gutter="true" wraplines="false" highlight="1,10"]  

[root@directorysrv1 /]# cp /temp/cas-server-webapp-3.3.5.war /usr/share/tomcat5/webapps/  

[root@directorysrv1 /]# ll /usr/share/tomcat5/webapps/  
total 19248  
-rw-r--r-- 1 root root 19658857 Dec 31 11:00 cas-server-webapp-3.3.5.war  
drwxrwxr-x 21 root tomcat 4096 Aug 13 11:35 jsp-examples  
drwxrwxr-x 4 root tomcat 4096 Aug 13 11:35 ROOT  
drwxrwxr-x 4 root tomcat 4096 Aug 13 11:35 servlets-examples  
drwxrwxr-x 12 root tomcat 4096 Aug 13 11:35 tomcat-docs  
drwxrwxr-x 3 root tomcat 4096 Aug 13 11:35 webdav  

[root@directorysrv1 /]# service tomcat5 start  
Starting tomcat5: [ OK ]  

[root@directorysrv1 /]#  

[/sourcecode]
To have tomcat start automatically when the system boots, use "chkconfig tomcat5 on".
5\. Verify that CAS server has been deployed successfully. Open a browser with this  
url: <http://directorysrv1:8080/cas-server-webapp-3.3.5>
6\. To avoid errors, it's vital that you ensure the Tomcat process owner (user tomcat) has write privileges to the path where cas.log and/or perfStats.log would be written.  
Then, edit CAS's log4j.xml or log4j.properties and add a valid path (for example: /usr/share/tomcat5/logs/) to these log (cas.log and/or perfStats.log) files:

[sourcecode language="text" gutter="true" wraplines="false" highlight="2"]  

[root@directorysrv1 /]# cd /usr/share/tomcat5/webapps/cas-server-webapp-3.3.5/WEB-INF/classes  

[root@directorysrv1 /]# nano log4j.properties  

[/sourcecode]
Add a valid path to log file.

[sourcecode language="text" gutter="true" wraplines="false" highlight="3"]  
...  
log4j.appender.logfile=org.apache.log4j.RollingFileAppender  
log4j.appender.logfile.File=/usr/share/tomcat5/logs/cas.log  
log4j.appender.logfile.MaxFileSize=512KB  

# Keep three backup files.  

log4j.appender.logfile.MaxBackupIndex=3  

# Pattern to output: date priority [category] - message  

...  

[/sourcecode]
7\. After the changes in log4j.xml or log4j.properties, restart the Tomcat server and open the CAS login page: http://directorysrv1:8080/cas-server-webapp-3.3.5/login
If everything is ok, you should see the following:

[caption id="" align="alignnone" width="473" caption="Login page in CAS Server"]![Login page in CAS Server]({{ site.baseurl }}/assets/01sso-cas_login.png)[/caption]
8\. By default, CAS server has enable basic authentication based in userid/password where any userid is equal to password, for example, test with rogerc/rogerc, you should see the message of "log in successful".

[caption id="" align="alignnone" width="474" caption="Successfully log into CAS with default authentication model"]![Successfully log into CAS with default authentication model]({{ site.baseurl }}/assets/02sso-cas_login-ok.png)[/caption]

## II. Configure CAS server with Penrose Virtual Directory Server

Now we have to change simple test authentication (userid = pwd) model for the LDAP authentication (existing users and password stored in LDAP tree "ou=Employees,dc=intix,dc=info" previously loaded - [see last blog post here ](http://holisticsecurity.wordpress.com/2010/12/22/authentication-penrose-directory-liferay/)-).
In other words, instead of authenticating with userid=rogerc/password=rogerc we will use  
userid=roger@intix.info and password=xxxx in CAS.
1\. Edit the deployerConfigContext.xml file:

[sourcecode language="text" gutter="true" wraplines="false" highlight="2"]  

[root@directorysrv1 /]# cd /usr/share/tomcat5/webapps/cas-server-webapp-3.3.5/WEB-INF  

[root@directorysrv1 /]# nano deployerConfigContext.xml  

[/sourcecode]
.. comment SimpleTestUsernamePasswordAuthenticationHandler and add these lines:

[sourcecode language="xml" wraplines="false" highlight="3,6,7,8,15" padlinenumbers="2"]  
<!-- step 1 SimpleTestUsernamePasswordAuthenticationHandler disabled -->  
<!-- bean class="org.jasig.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler" /--></p>  
<p><!-- step 2 Add new AuthN handler for Penrose Virtual Directory Server -->  
<bean class="org.jasig.cas.adaptors.ldap.BindLdapAuthenticationHandler" >  
<property name="filter" value="mail=%u" />  
<property name="searchBase" value="ou=Employees,dc=intix,dc=info" />  
<property name="contextSource" ref="contextSource" />  
</bean>  
</list>  
</property>  
</bean></p>  
<p><!-- step 3 Add LDAP tree of Penrose Virtual Directory server -->  
<bean id="contextSource" class="org.springframework.ldap.core.support.LdapContextSource">  
<property name="pooled" value="false"/>  
<property name="urls">  
<list>  
<value>ldap://directorysrv1:10389/</value>  
</list>  
</property>  
<property name="userDn" value="uid=admin,ou=system"/>  
<property name="password" value="secret"/>  
<property name="baseEnvironmentProperties">  
<!-- Set the LDAP connect and read timeout(in ms) for the java ldap class See http://java.sun.com/products/jndi/tutorial/ldap/connect/create.html -->  
<map>  
<entry>  
<key>  
<value>java.naming.security.authentication</value>  
</key>  
<value>simple</value>  
</entry>  
</map>  
</property>  
</bean>  

[/sourcecode]
You can download [deployerConfigContext.xml file from here](http://dl.dropbox.com/u/2961879/blog20101231_sso_alfresco_liferay/deployerConfigContext.xml).
2\. Start Tomcat. You will see in catalina.out

[sourcecode language="text" gutter="true" wraplines="false" highlight="14"]  
...  
INFO: SessionListener: contextInitialized()  
Dec 31, 2010 2:05:16 PM org.apache.coyote.http11.Http11BaseProtocol start  
INFO: Starting Coyote HTTP/1.1 on http-8080  
Dec 31, 2010 2:05:16 PM org.apache.jk.common.ChannelSocket init  
INFO: JK: ajp13 listening on /0.0.0.0:8009  
Dec 31, 2010 2:05:16 PM org.apache.jk.server.JkMain start  
INFO: Jk running ID=0 time=0/15 config=null  
Dec 31, 2010 2:05:16 PM org.apache.catalina.storeconfig.StoreLoader load  
INFO: Find registry server-registry.xml at classpath resource  
Dec 31, 2010 2:05:16 PM org.apache.catalina.startup.Catalina start  
INFO: Server startup in 4294 ms  
...  
2010...,660 INFO [org.jasig.cas.web.flow.InitialFlowSetupAction] - <Setting path for ...>  
2010...,877 INFO [org.jasig.cas.ticket.registry.support.DefaultTicketRegistryCleaner] - <Starting...  
2010...,878 INFO [org.jasig.cas.ticket.registry.support.DefaultTicketRegistryCleaner] - <0 found ...  
2010...,878 INFO [org.jasig.cas.ticket.registry.support.DefaultTicketRegistryCleaner] - <Finished...  

[/sourcecode]
3\. Test CAS server with BindLdapAuthenticationHandler. Open a browser, go to the CAS login page and enter any usr/pwd that exists in the LDAP tree "ou=Employees,dc=intix,dc=info", for example: userid= Aamod.Wroclawski@intix.info with password=test

[caption id="" align="alignnone" width="287" caption="Successfully log into CAS with userid=Aamod.Wroclawski@intix.info with password=test"]![Successfully log into CAS with userid=Aamod.Wroclawski@intix.info with password=test]({{ site.baseurl }}/assets/03sso-cas_login_ldap_mail.png)[/caption]
In the catalina.out you can see the following:

[sourcecode language="text" gutter="true" wraplines="false" highlight="2"]  
...  
2010...,575 INFO [...successfully authenticated ... [username: Aamod.Wroclawski@intix.info]>  
2010...,984 INFO [... ] - <Reloading registered services.>  

[/sourcecode]

## III. Enable HTTPS and configure SSL Certificate on Tomcat server that contains CAS server

**Note:**
* SSL Certificate is used to enable secure channel by communication between CAS server and any Webapp that does the authentication and Web-SSO with CAS.
* It is necessary to install the Root SSL Certificate of the CAS server for each trusted certificate repository of Web Server container (or Java Virtual Machine).
* All certificates will be selfsigned, only for testing purposes.
1\. Create a key pairs for the new SSL certificate for CAS server with 730 days of validity:

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
keytool -genkey -alias tomcat -keypass <CERT_PWD> -keyalg RSA -keystore ./<MY_KEYSTORE> -validity 730</p>  
<p>Where:  
CERT_PWD is "changeit"  
CAS_KEYSTORE is "cas-3_3_5.keystore"  

[/sourcecode]
.. create self signed SSL certificate:

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  

[root@directorysrv1 /]# keytool -genkey -alias tomcat -keypass changeit -keyalg RSA -keystore /usr/share/tomcat5/cas-3_3_5.keystore -validity 730  
Enter keystore password:  
Re-enter new password:  
What is your first and last name?  

[Unknown]: directorysrv1  
What is the name of your organizational unit?  

[Unknown]: INTIX I+D  
What is the name of your organization?  

[Unknown]: INTIX.info  
What is the name of your City or Locality?  

[Unknown]: BARCELONA  
What is the name of your State or Province?  

[Unknown]: CATALUNYA  
What is the two-letter country code for this unit?  

[Unknown]: ES  
Is CN=directorysrv1, OU="INTIX I+D", O=INTIX.info, L=BARCELONA, ST=CATALUNYA, C=ES correct?  

[no]: yes</p>  
<p>[root@directorysrv1 bin]#  

[/sourcecode]
2\. Export the SSL certificate:

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  

[root@directorysrv1 /]# keytool -export -alias tomcat -keypass changeit -keystore /usr/share/tomcat5/cas-3_3_5.keystore -storepass changeit -file /usr/share/tomcat5/directorysrv1_730days.crt  
Certificate stored in file </usr/share/tomcat5/directorysrv1_730days.crt>  

[root@directorysrv1 /]#  

[/sourcecode]
3\. Remove comments in /usr/share/tomcat5/conf/server.xml and enable HTTPS:

[sourcecode language="xml" gutter="true" wraplines="false" highlight="4,10,11"]  
...  
<!-- Define a SSL HTTP/1.1 Connector on port 8443 --></p>  
<p><!-- sept 1, SSL in CAS server -->  
<Connector port="8443" maxHttpHeaderSize="8192" maxThreads="150" minSpareThreads="25" maxSpareThreads="75" enableLookups="false" disableUploadTimeout="true" acceptCount="100" scheme="https" secure="true" clientAuth="false" sslProtocol="TLS" keystoreFile="/usr/share/tomcat5/cas-3_3_5.keystore" keystorePass="changeit" /></p>  
<p><!-- Define an AJP 1.3 Connector on port 8009 -->  
...  

[/sourcecode]
You can download [server.xml from here](http://dl.dropbox.com/u/2961879/blog20101231_sso_alfresco_liferay/server.xml).
5\. Now you can test CAS server on SSL, in this case you have to open a browser with this URL: <https://directorysrv1:8443/cas-server-webapp-3.3.5/login>

[caption id="" align="alignnone" width="320" caption="CAS login on SSL"]![CAS login on SSL]({{ site.baseurl }}/assets/04sso-cas_login_SSL.png)[/caption]

## IV. Configure Liferay with CAS and LDAP Authentication

1\. Import CAS server SSL public certificate in the JVM/JRE where Liferay is running, in my case I have Liferay running in WinXP box called "lfry01".

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
c:\>keytool -import -alias tomcat -file c:\0share1\cas-3.3.5_cert\directorysrv1_730days.crt -keystore c:\1bpms-demo\liferay-portal-6.0.5\tomcat-6.0.26\jre1.6.0_21\win\lib\security\cacerts  
Enter keystore password:  
Owner: CN=directorysrv1, OU="INTIX I+D", O=INTIX.info, L=BARCELONA, ST=CATALUNYA, C=ES  
Issuer: CN=directorysrv1, OU="INTIX I+D", O=INTIX.info, L=BARCELONA, ST=CATALUNYA, C=ES  
Serial number: 4d1df9bc  
Valid from: Fri Dec 31 16:41:48 GMT+01:00 2010 until: Sun Dec 30 16:41:48 GMT+01:00 2012  
Certificate fingerprints:  
MD5: 11:4D:72:BB:80:42:EE:F7:4A:CA:E9:EA:F6:4F:86:8D  
SHA1: 7F:6B:12:64:31:8B:47:4E:11:33:D7:FE:EF:C6:D4:65:12:59:8D:2E  
Signature algorithm name: SHA1withRSA<br />  
Version: 3  
Trust this certificate? [no]: yes  
Certificate was added to keystore</p>  
<p>c:\>  

[/sourcecode]
2\. Configure Liferay CAS and LDAP Authentication:
In last blog post We configured LDAP Authentication in Liferay, in this new example we just should add CAS server configuration in Liferay.

[sourcecode language="text" gutter="true" wraplines="false"]  
* Enabled: Yes  
* Import from LDAP: Yes  
* Login URL: https://directorysrv1:8443/cas-server-webapp-3.3.5/login  
* Logout URL: https://directorysrv1:8443/cas-server-webapp-3.3.5/logout  
* Server Name: lfry01:8080  
* Server URL: https://directorysrv1:8443/cas-server-webapp-3.3.5  
* Service URL: http://lfry01:8080/c/portal/login  

[/sourcecode]

[caption id="" align="alignnone" width="494" caption="CAS configuration in Liferay Control Panel"]![CAS configuration in Liferay Control Panel]({{ site.baseurl }}/assets/05sso-liferay_ldap_cas_authn_config.png)[/caption]
3\. Test LDAP Authentication and CAS with Liferay:
* Go to Liferay http://lfry01:8080
* Click on "Sign in" link located on the top right 

[caption id="" align="alignnone" width="190" caption="Click on "Sign in" (top right on guest page of Liferay)"]![Click on "Sign in" \(top right on guest page of Liferay\)]({{ site.baseurl }}/assets/06sso-liferay_ldap_cas_authn_signin.png)[/caption]
* CAS login form appears, enter with userid=Aamod.Wroclawski@intix.info and pwd=test 

[caption id="" align="alignnone" width="484" caption="Login page when requesting a protected resource in Liferay"]![Login page when requesting a protected resource in Liferay]({{ site.baseurl }}/assets/06sso-liferay_ldap_cas_authn_signin2.png)[/caption]
* If authentication is OK, then you will be redirected to the Aamod.Wroclawski's page within liferay 

[caption id="" align="alignnone" width="466" caption="When doing a successful logon in CAS, we are redirected to the requested page in Liferay"]![When doing a successful logon in CAS, we are redirected to the requested page in Liferay]({{ site.baseurl }}/assets/06sso-liferay_ldap_cas_authn_signin3.png)[/caption]

## V. Install and configure Alfresco with CAS and LDAP Authentication

In the next post will explain how to configure Alfresco with CAS to do SSO and Authentication.
We also will see the importance of using an LDAP for supplying identidates and verify the SSO between Liferay and Alfresco.
See you soon.

**References:**
* Alfresco Authentication Subsystems:  
http://wiki.alfresco.com/wiki/Alfresco_Authentication_Subsystems
* External authentication subsystem:  
http://wiki.alfresco.com/wiki/Alfresco_Authentication_Subsystems#External
* Alfresco With mod auth CAS:  
http://wiki.alfresco.com/wiki/Alfresco_With_mod_auth_cas
