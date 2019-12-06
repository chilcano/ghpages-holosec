---
layout: post
title: Web-SSO between Liferay and Alfresco with CAS and Penrose (part 2/2)
date: 2011-02-19 23:45:14.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- ECM
- Portal
tags:
- Alfresco
- LDAP
- Liferay
- SSO
meta:
  _edit_last: '578869'
  _oembed_a9b8c3a206fd5fe789493b068b0c3a78: "{{unknown}}"
  _oembed_43f68a3061b09bdbfaaab584cec808d7: "{{unknown}}"
  _wpas_done_twitter: '1'
  _oembed_e3f751c6281300a863957dcae316b52a: "{{unknown}}"
  _oembed_4a619cb3d98246e029c7a2a7cd16ed16: "{{unknown}}"
  _oembed_579511cb5932773985eb2ebeff0d011e: "{{unknown}}"
  _oembed_45a254a389adda0285cff4c8dbe9bdb0: "{{unknown}}"
  _oembed_9601ace4f297bb770e92667b99a90f99: "{{unknown}}"
  _oembed_e872e146163213b9e94601df858875f5: "{{unknown}}"
  _oembed_ba576b5d75a14d668591eb3d3efdcac2: "{{unknown}}"
  _oembed_bb91fa985d51d556420619d243a78f59: "{{unknown}}"
  _oembed_c7f7ede747e8da235b85ef437eee6a1d: "{{unknown}}"
  _oembed_a32f44fb82e8494115dec9f245d10c84: "{{unknown}}"
  _oembed_78c2e8ddd456e75dee6f6cae6fb1be78: "{{unknown}}"
  _oembed_cb46d178a6031b82c290062f09fe38f1: "{{unknown}}"
  _oembed_f07109bac5c753e8f8d81cb3668a2e46: "{{unknown}}"
  _oembed_d5dbc2a5ac85f1b7a9ee436eded3c5d8: "{{unknown}}"
  _oembed_fbec511aa7154bf6178b2e329d780527: "{{unknown}}"
  _oembed_c8dd80c96abe10a1e05d9c9510877e2b: "{{unknown}}"
  _oembed_6fb43a053aa432e5cb0d671af7fc0c15: "{{unknown}}"
  _oembed_25cf813f83e1de04239e71235b386841: "{{unknown}}"
  _oembed_8bbacbe54a162b07a0e4fafdcb8b31d7: "{{unknown}}"
  _oembed_ddf490c35ec4eb69ef47c30c446b2538: "{{unknown}}"
  _oembed_c538002f68b1e2c72947e1eca992eef8: "{{unknown}}"
  _oembed_3ea4ed007a7ed4e2ac6995da084fa5a1: "{{unknown}}"
  _oembed_5e418f63221a3e0c258673db14cd6279: "{{unknown}}"
  _oembed_c852e92c5ce2aaaa795684a98a598902: "{{unknown}}"
  _oembed_528f316ce51705cf605089df43a5fb84: "{{unknown}}"
  _oembed_ab2fd026b3dd3c5e4b0cbed14a582e7e: "{{unknown}}"
  _oembed_f6484336aba0620560dc9addeb57a831: "{{unknown}}"
  _oembed_1441e06addae9466d8e5a0980c805adc: "{{unknown}}"
  _oembed_af34d41e411ee4f4d4785aa1f13d31e7: "{{unknown}}"
  _oembed_3f81bcd935235f76e632b237a2cc52a3: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2011/02/19/web-sso-between-liferay-and-alfresco-with-cas-and-penrose-part-22/"
---
The aims are to do authentication and web-sso between liferay and alfresco using CAS.  
  
In this blog post we will explain how to configure Alfresco to enable LDAP authentication and users syncronization, also we will explain how to configure CAS Authentication Filter to do Web-SSO with automatic/transparent login.

  
Firstly, we will follow this technical design for authentication and sso.

  
[caption id="" align="alignnone" width="461" caption="Authentication and SSO architectura"]![Authentication and SSO architectura between Liferay, Alfresco and CAS]({{ site.baseurl }}/assets/sso_alfr_lfry-00-architectura.png)  
  
[/caption]

  
## Requirements

  
  

  1. Virtual Directory Server (Penrose server 2.0) and CAS-server (tested with version 3.3.5)I will use existing CentOS VirtualBox VM with CAS and Penrose Server pre-configured (Virtual Directory/LDAP) named "directorysrv1" of last blog post ([Web-SSO between Liferay and Alfresco with CAS and Penrose (part 1/2)](http://holisticsecurity.wordpress.com/2011/01/15/sso-liferay-alfresco-cas-penrose-part-12)) but with a few changes: [sourcecode language="text" gutter="true" wraplines="false"]  
  
A sample DN:  
  
uid=480838,ou=Employees,dc=intix,dc=info  
  
cn=aamodwroclawski  
  
[/sourcecode]

  
You can download this new Penrose partition [here](http://dl.dropbox.com/u/2961879/blog20110215_sso_alfresco_liferay_2/intix_info_liferay.zip).

  
[caption id="" align="alignnone" width="483" caption="LDAP tree"]![LDAP tree]({{ site.baseurl }}/assets/sso_alfr_lfry-00-LDAP-DN.png)[/caption]
  

  2. Alfresco 3.4c CE:We are using a new WinXP VirtualBox VM with Alfresco and MySQL installed named "alfr01".
  

  3. Liferay 6.0.5 with LDAP and CAS enabled:We are using a WinXP VirtualBox VM with Liferay 6.0.5 CE installed named "lfry01". See before post [here](http://holisticsecurity.wordpress.com/2011/01/15/sso-liferay-alfresco-cas-penrose-part-12).  
[caption id="" align="alignnone" width="400"  
  
caption="LDAP and CAS configuration in Liferay"]

  
  
  
  
![1]({{ site.baseurl }}/assets/sso_alfr_lfry-00-ldap1.png)  
  
|   
![2]({{ site.baseurl }}/assets/sso_alfr_lfry-00-ldap2.png)  
  
  
---|---  
  
  
  
![3]({{ site.baseurl }}/assets/sso_alfr_lfry-00-ldap3.png)  
  
|   
![4]({{ site.baseurl }}/assets/sso_alfr_lfry-00-ldap4.png)  
![5]({{ site.baseurl }}/assets/sso_alfr_lfry-00-ldap5cas.png)  
  
[/caption] 
  4. CAS-client (3.1.10)

## I. Enable LDAP Authentication and LDAP users import in Alfresco

To do Web-SSO is not necessary this step, but i recommend to do it because you can do users management from Alfresco Admin Console (Browser/Explorer or Share) (edit, delete, to do groups and give permissions).

1\. Create the following folders in "\subsystems\Authentication\ldap\ldap1" in ﻿${ALF_HOME}\tomcat\shared\classes\alfresco\extension

2\. Copy the file ﻿${ALF_HOME}\tomcat\webapps\alfresco\WEB-INF\classes\alfresco\subsystems\Authentication\ldap\﻿ldap-authentication.properties in the folder before created.

3\. Modify ldap-authentication.properties enabling LDAP authN and sync. For example, you can use my file (This only works for my LDAP tree with UID as RDN and authN with CN. See my LDAP tree):

[sourcecode language="text" gutter="true" wraplines="false"]  
﻿# This flag enables use of this LDAP subsystem for authentication. It may be  
# that this subsytem should only be used for synchronization, in which case  
# this flag should be set to false.  
ldap.authentication.active=true

#  
# This properties file brings together the common options for LDAP authentication rather than editing the bean definitions  
#  
ldap.authentication.allowGuestLogin=true  
# How to map the user id entered by the user to that passed through to LDAP  
# - simple  
# - this must be a DN and would be something like  
# uid=%s,ou=People,dc=company,dc=com  
# - digest  
# - usually pass through what is entered  
# %s  
# If not set, an LDAP query involving ldap.synchronization.personQuery and ldap.synchronization.userIdAttributeName will  
# be performed to resolve the DN dynamically. This allows directories to be structured and doesn't require the user ID to  
# appear in the DN.  
### intix: always search DN by RDN attribute, in my case uid (see ldap tree)  
### ldap.authentication.userNameFormat=cn=%s,ou=Employees,dc=intix,dc=info  
### intix: this config is better than above, because i want to searh by CN.  
### It is necessary set ldap.synchronization.personQuery=inetOrgPerson and ldap.synchronization.userIdAttributeName=cn  
ldap.authentication.userNameFormat=

# The LDAP context factory to use  
ldap.authentication.java.naming.factory.initial=com.sun.jndi.ldap.LdapCtxFactory

# The URL to connect to the LDAP server  
ldap.authentication.java.naming.provider.url=ldap://directorysrv1:10389

# The authentication mechanism to use for password validation  
ldap.authentication.java.naming.security.authentication=simple

# Escape commas entered by the user at bind time  
# Useful when using simple authentication and the CN is part of the DN and contains commas  
ldap.authentication.escapeCommasInBind=false

# Escape commas entered by the user when setting the authenticated user  
# Useful when using simple authentication and the CN is part of the DN and contains commas, and the escaped \, is  
# pulled in as part of an LDAP sync  
# If this option is set to true it will break the default home folder provider as space names can not contain \  
ldap.authentication.escapeCommasInUid=false

# Comma separated list of user names who should be considered administrators by default  
### intix: administration user (CN) when ldap authN is enabled.  
### The "admin" user is valid when alfrescoNtlm authN is enabled.  
ldap.authentication.defaultAdministratorUserNames=aamodwroclawski

# This flag enables use of this LDAP subsystem for user and group  
# synchronization. It may be that this subsytem should only be used for  
# authentication, in which case this flag should be set to false.  
ldap.synchronization.active=true

# The authentication mechanism to use for synchronization  
ldap.synchronization.java.naming.security.authentication=simple

# The default principal to use (only used for LDAP sync)  
ldap.synchronization.java.naming.security.principal=uid\=admin,ou\=system

# The password for the default principal (only used for LDAP sync)  
ldap.synchronization.java.naming.security.credentials=secret

# If positive, this property indicates that RFC 2696 paged results should be  
# used to split query results into batches of the specified size. This  
# overcomes any size limits imposed by the LDAP server.  
ldap.synchronization.queryBatchSize=0

# If positive, this property indicates that range retrieval should be used to fetch  
# multi-valued attributes (such as member) in batches of the specified size.  
# Overcomes any size limits imposed by Active Directory.  
ldap.synchronization.attributeBatchSize=0

# The query to select all objects that represent the groups to import.  
### ldap.synchronization.groupQuery=(objectclass\=groupOfNames)  
ldap.synchronization.groupQuery=(objectclass\=groupOfUniqueNames)

# The query to select objects that represent the groups to import that have changed since a certain time.  
### ldap.synchronization.groupDifferentialQuery=(&(objectclass\=groupOfNames)(!(modifyTimestamp<\={0})))  
ldap.synchronization.groupDifferentialQuery=(&(objectclass\=groupOfUniqueNames)(!(modifyTimestamp<\={0})))

# The query to select all objects that represent the users to import.  
ldap.synchronization.personQuery=(objectclass\=inetOrgPerson)

# The query to select objects that represent the users to import that have changed since a certain time.  
ldap.synchronization.personDifferentialQuery=(&(objectclass\=inetOrgPerson)(!(modifyTimestamp<\={0})))

# The group search base restricts the LDAP group query to a sub section of tree on the LDAP server.  
ldap.synchronization.groupSearchBase=ou\=Groups,dc\=intix,dc\=info

# The user search base restricts the LDAP user query to a sub section of tree on the LDAP server.  
ldap.synchronization.userSearchBase=ou\=Employees,dc\=intix,dc\=info

# The name of the operational attribute recording the last update time for a group or user.  
ldap.synchronization.modifyTimestampAttributeName=modifyTimestamp

# The timestamp format. Unfortunately, this varies between directory servers.  
ldap.synchronization.timestampFormat=yyyyMMddHHmmss'Z'

# The attribute name on people objects found in LDAP to use as the uid in Alfresco  
### ldap.synchronization.userIdAttributeName=uid  
### intix: CN is necessary to authN by this attribute when searching LDAP  
ldap.synchronization.userIdAttributeName=cn

# The attribute on person objects in LDAP to map to the first name property in Alfresco  
ldap.synchronization.userFirstNameAttributeName=givenName

# The attribute on person objects in LDAP to map to the last name property in Alfresco  
ldap.synchronization.userLastNameAttributeName=sn

# The attribute on person objects in LDAP to map to the email property in Alfresco  
ldap.synchronization.userEmailAttributeName=mail

# The attribute on person objects in LDAP to map to the organizational id property in Alfresco  
ldap.synchronization.userOrganizationalIdAttributeName=o

# The default home folder provider to use for people created via LDAP import  
ldap.synchronization.defaultHomeFolderProvider=userHomesHomeFolderProvider

# The attribute on LDAP group objects to map to the authority name property in Alfresco  
ldap.synchronization.groupIdAttributeName=cn

# The attribute on LDAP group objects to map to the authority display name property in Alfresco  
ldap.synchronization.groupDisplayNameAttributeName=description

# The group type in LDAP  
### ldap.synchronization.groupType=groupOfNames  
ldap.synchronization.groupType=groupOfUniqueNames

# The person type in LDAP  
ldap.synchronization.personType=inetOrgPerson

# The attribute in LDAP on group objects that defines the DN for its members  
### ldap.synchronization.groupMemberAttributeName=member  
ldap.synchronization.groupMemberAttributeName=uniqueMember

# If true progress estimation is enabled. When enabled, the user query has to be run twice in order to count entries.  
ldap.synchronization.enableProgressEstimation=true  
[/sourcecode]

4\. Re-start Alfresco.

5\. Check LDAP authN and import of users in Alfresco.

[caption id="" align="alignnone" width="517" caption="Imported users from LDAP tree in Alfresco"]![Imported users from LDAP tree in Alfresco]({{ site.baseurl }}/assets/sso_alfr_lfry-04-showusers.png)[/caption]

## II. Configure CAS in Alfresco

We are setting up Alfresco so that when someone log into Alfresco it is redirected to CAS for authentication.

Through the CAS filter, Alfresco catchs any request to access and these are redirected to CAS-login.

When you has successfully authenticated with CAS, after you will be redirected to the My Alfresco Dashboard, then Alfresco will need to retrieve the values of session which is placed there by the CAS Filter.

If you want to do SSO and automatic redirection when login to Alfresco Explorer after authentication in CAS, you should create a CAS Authentication Filter as Aksels Architecture Blog show us [here](http://akselsarchitecture.blogspot.com/2010/09/cas-sso-for-alfresco-33-and-share.html) and test with version 3.4c.

To do this you have to create/modify the Java code (CasAuthenticationFilter.java) that is executed when enter to Alfresco page.

1\. Edit the alfresco web.xml to modify Authentication Filter and to add the CAS filters.

[sourcecode language="text" gutter="true" wraplines="false"]  
﻿c:\>notepad++ C:\1bpms-demo\alfr34c_1\tomcat\webapps\alfresco\WEB-INF\web.xml  
[/sourcecode]

... modify web.xml

[sourcecode language="xml" gutter="true" wraplines="false"]  
[...]  
﻿ <context-param>  
<param-name>rootPath</param-name>  
<param-value>/app:company_home</param-value>  
</context-param>

<!--filter>  
<filter-name>Authentication Filter</filter-name>  
<description>Authentication filter mapped only to faces URLs. Other URLs generally use proprietary means to talk to the AuthenticationComponent</description>  
<filter-class>org.alfresco.repo.web.filter.beans.BeanProxyFilter</filter-class>  
<init-param>  
<param-name>beanName</param-name>  
<param-value>AuthenticationFilter</param-value>  
</init-param>  
</filter-->  
<!-- ******* INTIX, Step 1 of 3: Comment above 'Authentication Filter' filter and add a CAS modified filter below -->  
<filter>  
<filter-name>Authentication Filter</filter-name>  
<description>INTIX - Authentication Filter</description>  
<filter-class>org.jasig.cas.client.authentication.AuthenticationFilter</filter-class>  
<init-param>  
<param-name>casServerLoginUrl</param-name>  
<param-value>https://directorysrv1:8443/cas-server-webapp-3.3.5/login</param-value>  
</init-param>  
<init-param>  
<param-name>serverName</param-name>  
<param-value>http://alfr01:8080</param-value>  
</init-param>  
</filter>  
<!-- End new CAS filter -->

<filter>  
<filter-name>Global Authentication Filter</filter-name>  
<description>Authentication filter mapped to all authenticated URLs. Mainly for SSO support</description>  
<filter-class>org.alfresco.repo.web.filter.beans.BeanProxyFilter</filter-class>  
<init-param>  
<param-name>beanName</param-name>  
<param-value>GlobalAuthenticationFilter</param-value>  
</init-param>  
</filter>  
[...]  
﻿ ﻿<!-- ******* INTIX, Step 2 of 3: Add all CAS urls -->  
<filter>  
<filter-name>CAS Validation Filter</filter-name>  
<filter-class>org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter</filter-class>  
<init-param>  
<param-name>casServerUrlPrefix</param-name>  
<param-value>https://directorysrv1:8443/cas-server-webapp-3.3.5</param-value>  
</init-param>  
<init-param>  
<param-name>serverName</param-name>  
<param-value>http://alfr01:8080</param-value>  
</init-param>  
</filter>

<filter>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<filter-class>info.intix.alfresco.CasAuthenticationFilter</filter-class>  
</filter>

<filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/faces/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/faces/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<url-pattern>/faces/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/navigate/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/navigate/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<url-pattern>/navigate/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/command/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/command/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<url-pattern>/command/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/download/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/download/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<url-pattern>/download/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/template/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/template/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<url-pattern>/template/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/n/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/n/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<url-pattern>/n/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/c/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/c/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<url-pattern>/c/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/t/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/t/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<url-pattern>/t/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/d/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>CAS Validation Filter</filter-name>  
<url-pattern>/d/*</url-pattern>  
</filter-mapping>

<filter-mapping>  
<filter-name>Alfresco CAS Authentication Filter</filter-name>  
<url-pattern>/d/*</url-pattern>  
</filter-mapping>  
<!-- ******* End of CAS urls -->

<filter-mapping>  
<filter-name>Global Localization Filter</filter-name>  
<url-pattern>/*</url-pattern>  
</filter-mapping>  
[...]  
﻿ <filter-mapping>  
<filter-name>Global Authentication Filter</filter-name>  
<url-pattern>/faces/*</url-pattern>  
</filter-mapping>  
<!-- ******* INTIX, Step 3 of 3: Comment this, it is a duplicated -->  
<!--filter-mapping>  
<filter-name>Authentication Filter</filter-name>  
<url-pattern>/faces/*</url-pattern>  
</filter-mapping-->

<filter-mapping>  
<filter-name>WebDAV Authentication Filter</filter-name>  
<url-pattern>/webdav/*</url-pattern>  
</filter-mapping>  
[...]  
[/sourcecode]

2\. Copy the CAS client jar file into the alfresco webapp lib folder.

[sourcecode language="text" gutter="true" wraplines="false"]  
﻿c:\>  
c:\>copy C:\0share1\cas-client-core-3.1.10.jar C:\1bpms-demo\alfr34c_1\tomcat\webapps\alfresco\WEB-INF\lib\cas-client-core-3.1.10.jar  
1 archivos copiados.

c:\>  
[/sourcecode]

3\. Modify and compile CasAuthenticationFilter.java (http://akselsarchitecture.googlegroups.com/web/CasAuthenticationFilter-Alfresco.java) and copy .jar into the alfresco webapp lib folder.

[sourcecode language="text" gutter="true" wraplines="false"]  
﻿c:\>  
c:\>copy C:\0share1\www.intix.info-casauthnfilter-0.1.jar c:\1bpms-demo\alfr34c_1\tomcat\webapps\alfresco\WEB-INF\lib\www.intix.info-casauthnfilter-0.1.jar  
1 archivos copiados.

c:\>  
[/sourcecode]

You can download my **www.intix.info-casauthnfilter-0.1.jar** file from [**here**](http://dl.dropbox.com/u/2961879/blog20110215_sso_alfresco_liferay_2/www.intix.info-casauthnfilter-0.1.jar).  
4\. Re-start Alfresco.

5\. Test CAS configuration.

Try opening an Alfresco's page, for example: http://alfr01:8080/alfresco in a browser. You should be redirected to the CAS login page, and when you log in (for example with aamodwroclawski/test) you should be redirected back to the My Alfresco Dashboard.

6\. If you have get this error (see figure below) is because you have not installed the CAS root SSL Cert as a trusted certificate in Alfresco (JRE's cacert store). Alfresco 3.4c CE has JRE's cacert store in ${ALF_HOME}/java/jre/lib/sec, then install the certificate there.

[caption id="" align="alignnone" width="468" caption="CAS server SSL certificate no installed in Alfresco"]![CAS server SSL certificate no installed in Alfresco]({{ site.baseurl }}/assets/sso_alfr_lfry-01-Alfresco-SSL-Cert-validateProxyTicketValidator-error.png)[/caption]

To solve it, you should import CAS server SSL public certificate in the JRE's cacerts where Alfresco is running, in my case I have Alfresco running in WinXP box called “alfr01″.

[sourcecode language="text" gutter="true" wraplines="false"]  
c:\>keytool -import -alias tomcat -file c:\0share1\directorysrv1_730days.crt -keystore C:\1bpms-demo\alf34c_1\java\jre\lib\sec  
y\cacerts  
Enter keystore password:  
Owner: CN=directorysrv1, OU="INTIX I+D", O=INTIX.info, L=BARCELONA, ST=CATALUNYA, C=ES  
Issuer: CN=directorysrv1, OU="INTIX I+D", O=INTIX.info, L=BARCELONA, ST=CATALUNYA, C=ES  
Serial number: 4d1df9bc  
Valid from: Fri Dec 31 16:41:48 GMT+01:00 2010 until: Sun Dec 30 16:41:48 GMT+01:00 2012  
Certificate fingerprints:  
MD5: 11:4D:72:BB:80:42:EE:F7:4A:CA:E9:EA:F6:4F:86:8D  
SHA1: 7F:6B:12:64:31:8B:47:4E:11:33:D7:FE:EF:C6:D4:65:12:59:8D:2E  
Signature algorithm name: SHA1withRSA  
Version: 3  
Trust this certificate? [no]: yes  
Certificate was added to keystore  
[/sourcecode]

## III. Tuning Authentication in Alfresco

Right now, we have configured Alfresco and CAS, where the user management can be done syncronizing or importing users stored in LDAP tree.

We can do user, groups and roles management via Alfresco LDAP subsystem and Authentication-SSO via EXTERNAL subsystem. To do this, we must modify the file alfresco-global.properties.

[sourcecode language="text" gutter="true" wraplines="false"]  
[...]  
﻿### authentication.chain=alfrescoNtlm1:alfrescoNtlm,ldap1:ldap  
﻿authentication.chain=external1:external,ldap1:ldap  
[/sourcecode]

## IV. Test Web Single Sign On between Liferay 6.0.5 CE and Alfresco 3.4.c CE

Open a browser with http://alfr01:8080/alfresco, you get redirected to CAS-login page. Enter aamodwroclawski/test, then you should be redirected to Alfresco My Dashboard page (authenticated).

In this time you should see Logout (aamodwroclawski) in the top right of the Alfresco page indicating that you have sucessfully logged in.

[caption id="" align="alignnone" width="476" caption="User properly authenticated in Alfresco"]![User properly authenticated in Alfresco]({{ site.baseurl }}/assets/sso_alfr_lfry-05-userauthenticated2.png)[/caption]

Then, open other browser with http://lfry01:8080/intixportal/user/aamodwroclawski, you get redirected to Liferay private and authenticated page for the user "aamodwroclawski".

[caption id="" align="alignnone" width="442" caption="The same user authenticated and with SSO in Liferay"]![The same user authenticated and with SSO in Liferay]({{ site.baseurl }}/assets/sso_alfr_lfry-05-userauthenticated2lfry.png)[/caption]

In other direction (Liferay to Alfresco) it does work too.

## V. Conclusions

1\. Authentication and users sync in Alfresco 3.4c does work with authentication subsystem LDAP.

2\. SSO with CAS in Alfresco 3.4c does work by enabling authentication subsystem EXTERNAL.

3\. There is an issue when importing users from LDAP tree in Liferay. The passwords are created with random value and no with "test".

**END**

**References:**

1\. CAS in Alfresco

http://wiki.alfresco.com/wiki/Central_Authentication_Service_Configuration

2\. CAS SSO for Alfresco 3.3 and Share

http://akselsarchitecture.blogspot.com/2010/09/cas-sso-for-alfresco-33-and-share.html
