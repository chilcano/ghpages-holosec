---
layout:     post
title:      'Redirect to Liferay page after logout in CAS'
date:       2011-01-20 10:27:27
categories: ['Portal', 'Security']
tags:       ['Authentication', 'CAS', 'Liferay']
status:     publish 
permalink:  "/2011/01/20/redirect-to-liferay-page-after-logout-in-cas/"
---
To redirect any webpage after logout in CAS, follow the instructions below:
1\. Change Liferay CAS configuration. Go to  
Control Panel > Settings > Authentication > CAS
2\. Update "Logout URL" property:

[sourcecode language="text" gutter="true" wraplines="false"]  
https://directorysrv1:8443/cas-server-webapp-3.3.5/logout?service=${my-logout-page}
Where:  
${my-logout-page}: http://lfry01:8080/web/guest/lfry01-logout-page  

[/sourcecode]
3\. Change CAS configuration. Set the "followServiceRedirects" property to "true"  
on the "logoutController" bean defined in the cas-servlet.xml.

[sourcecode language="text" gutter="true" wraplines="false"]  

[root@directorysrv1 ~]# vim /usr/share/tomcat5/webapps/cas-server-webapp-3.3.5/WEB-INF/cas-servlet.xml  

[/sourcecode]

[sourcecode language="xml" wraplines="false" highlight="7" padlinenumbers="2"]  

[...]  
<bean id="logoutController" class="org.jasig.cas.web.LogoutController"  
p:centralAuthenticationService-ref="centralAuthenticationService"  
p:logoutView="casLogoutView"  
p:warnCookieGenerator-ref="warnCookieGenerator"  
p:ticketGrantingTicketCookieGenerator-ref="ticketGrantingTicketCookieGenerator"  
p:followServiceRedirects="true"/>  

[...]  

[/sourcecode]
4\. Re-start Tomcat (CAS-server):

[sourcecode language="text" gutter="true" wraplines="false"]  

[root@directorysrv1 ~]# service tomcat5 restart  
Stopping tomcat5: [ OK ]  
Starting tomcat5: [ OK ]  

[root@directorysrv1 ~]#  

[/sourcecode]
5\. Test redirect.

[caption id="" align="alignnone" width="414" caption="Page redirected by CAS"]![Page redirected by CAS]({{ site.baseurl }}/assets/20110120_tip_logoutcasredirect_1.png)[/caption]
