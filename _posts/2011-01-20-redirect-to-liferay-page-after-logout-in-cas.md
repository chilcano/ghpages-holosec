---
layout: post
title: Redirect to Liferay page after logout in CAS
date: 2011-01-20 11:27:27.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Portal
- Security
tags:
- Authentication
- CAS
- Liferay
meta:
  _edit_last: '578869'
  _oembed_1f4fc8cbfcc1edd1bb93472f269566cb: "{{unknown}}"
  _wpas_done_twitter: '1'
  _oembed_c325377b9817b827cf3aa49b37977e07: "{{unknown}}"
  _oembed_a69a0687248e1b1df7742b3fe340c4eb: "{{unknown}}"
  _oembed_67a121766ad368ac89a043a8edee0d9f: "{{unknown}}"
  _oembed_f365f044bf98b0e9128d79416e64bae8: "{{unknown}}"
  tagazine-media: a:7:{s:7:"primary";s:104:"http://dl.dropbox.com/u/2961879/blog20110120_tip_liferaycasredirect/20110120_tip_logoutcasredirect_1.png";s:6:"images";a:1:{s:104:"http://dl.dropbox.com/u/2961879/blog20110120_tip_liferaycasredirect/20110120_tip_logoutcasredirect_1.png";a:6:{s:8:"file_url";s:104:"http://dl.dropbox.com/u/2961879/blog20110120_tip_liferaycasredirect/20110120_tip_logoutcasredirect_1.png";s:5:"width";s:3:"591";s:6:"height";s:3:"416";s:4:"type";s:5:"image";s:4:"area";s:6:"245856";s:9:"file_path";s:0:"";}}s:6:"videos";a:0:{}s:11:"image_count";s:1:"1";s:6:"author";s:6:"578869";s:7:"blog_id";s:7:"2005905";s:9:"mod_stamp";s:19:"2011-01-20
    10:32:13";}
  _oembed_5fb747a7e438795667f5c75dca7e946f: "{{unknown}}"
  _oembed_93cfb5f9df83bf7260b223c652aa005f: "{{unknown}}"
  _oembed_1cfa55f0199ad7642f6021b37933ead7: "{{unknown}}"
  _oembed_e9ab090496c3df35f474b8cd764d0066: "{{unknown}}"
  _oembed_978d7474159f261b509d0d6c9aada4ad: "{{unknown}}"
  _oembed_8e9b1ee3623b3d8d8e155482bfe3429b: "{{unknown}}"
  _oembed_cbbbeced20675b47153c36430eef2df9: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2011/01/20/redirect-to-liferay-page-after-logout-in-cas/"
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
