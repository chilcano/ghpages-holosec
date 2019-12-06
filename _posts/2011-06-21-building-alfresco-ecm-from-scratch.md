---
layout: post
title: Building Alfresco ECM from scratch
date: 2011-06-21 19:19:02.000000000 +02:00
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
  _wpas_done_twitter: '1'
  _edit_last: '578869'
  _oembed_adc105802cbb904e12c42ee8256047a1: "{{unknown}}"
  _oembed_8d9edb0c3aca3847d1ac031d3ca96130: "{{unknown}}"
  _oembed_4bf50b899ef931a9d47771094efe3821: "{{unknown}}"
  _oembed_325dbb0e9fa3c90c9aaed7809f919624: "{{unknown}}"
  _oembed_49873469c29b1c1221632a1e57631cd9: "{{unknown}}"
  _oembed_e047dc037b844596d9dc1219701dd631: "{{unknown}}"
  _oembed_0e2fe7e08422b81afa65edd93b4562e3: "{{unknown}}"
  _oembed_f6483fd8f0d7d49c81bf2efc134eefab: "{{unknown}}"
  _oembed_600089ff9b144b701e1f76b7ce8df4a2: "{{unknown}}"
  _oembed_f33b543289b0fdaa9a3df8cabff5d43f: "{{unknown}}"
  _oembed_b74b98d1477edf2c1f20e0629ed51e17: "{{unknown}}"
  _oembed_c8eac96088501cd5a0c911bf65c8c782: "{{unknown}}"
  _oembed_52e2d3fabe7885ec3778700189ff90aa: "{{unknown}}"
  _oembed_29689aa86f8b24adae4b66964f45474e: "{{unknown}}"
  _oembed_37ca4d56946be045222ce78f82acaee0: "{{unknown}}"
  _oembed_152a0f692b0693a2f350746fee67e1c1: "{{unknown}}"
  _oembed_716c9157191420d483c0be66f046cfdd: "{{unknown}}"
  _oembed_7757a64985bde1ee881a98b7d83be73c: "{{unknown}}"
  _oembed_f21d5388611fcc224539e490e79f5050: "{{unknown}}"
  _oembed_3f04099f1f59468591784a49be9d8184: "{{unknown}}"
  _oembed_0895d221fcd98a8b42a034e11a6b9321: "{{unknown}}"
  _oembed_a0069d2e65141752f7f11ae229477b39: "{{unknown}}"
  _oembed_0a280a1df9e0cf626213ef0153756016: "{{unknown}}"
  _oembed_93da8c652ff5eb369e12414e24544df2: "{{unknown}}"
  _oembed_1044c63077311ceaf5455a3130b8c215: "{{unknown}}"
  _oembed_69374b0c2e27be0c0c045b8ca57bdc58: "{{unknown}}"
  _oembed_72da392a37a31a78f4694d0fc294683e: "{{unknown}}"
  _oembed_7e8aad7f25fa8a0c4addad9af075466b: "{{unknown}}"
  _oembed_4cc198f9db0476be3174f6304f578013: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2011/06/21/building-alfresco-ecm-from-scratch/"
---
This article show us how to build Alfresco from scratch which is useful when extending or bug fixing.

  


[caption id="" align="aligncenter" width="236" caption="Alfresco ECM"]![Alfresco ECM]({{ site.baseurl }}/assets/logo_01_alfresco.png)[/caption]

  


## Requisites:

  


1\. Windows XP  
  
2\. Java/JDK 1.6.0_21 (<http://www.oracle.com/technetwork/java/javase/downloads/index.html>)  
  
3\. Eclipse EE Helios SR2 (<http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/heliossr2>)  
  
4\. Subversion plugin for Eclipse (downloaded from Eclipse Marketplace)  
  
5\. Apache Tomcat 6.0.32 (<http://tomcat.apache.org/download-60.cgi>)  
  
6\. MySQL (XAMPP - <http://www.apachefriends.org/en/xampp-windows.html>)  
  
7\. ImageMagick ﻿6.7.0-Q16 (<http://www.imagemagick.org/script/binary-releases.php#windows>)  
  
8\. SWFTools 0.9.1 (<http://www.swftools.org/download.html>)  
  
9\. OpenOffice 3.2 (<http://download.openoffice.org/index.html>)  
  
10\. Alfresco source code downloaded from SVN on 15/June.

  


[caption id="" align="alignnone" width="493" caption="Alfresco ECM: Community - version 4.0.0"]![Alfresco ECM: Community - version 4.0.0]({{ site.baseurl }}/assets/build_alfresco_scratch_0.png)[/caption]

  


## Setup development environment:

  


### JDK 

  


1\. Install JDK and set two system variables (JAVA_HOME and PATH):

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
JAVA_HOME=C:\1bpms-demo\jdk160_21  
  
PATH=%PATH%;%JAVA_HOME%\bin  
  
[/sourcecode]

  


### Apache Tomcat 

  


1\. Install/Unzip Apache Tomcat in the folder above, for example, **C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app**

  


2\. Create **setenv.bat** in **C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app\bin** file with the following content:

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
set JAVA_OPTS=-Xms256m -Xmx1024m -Xss96k -XX:MaxPermSize=256m -server  
  
set JAVA_OPTS=%JAVA_OPTS%  
  
[/sourcecode]

  


### Eclipse 

  


1\. Unzip Eclipse into **C:\1bpms-demo\eclipse-jee-helios-SR2**.

  


2\. Open Eclipse and install Subclipse plugin from Eclipse Marketplace.

  


3\. Create a folder where will download and build the Alfresco source code, for example﻿ **C:\1bpms-demo\alfresco_scratch**.

  


4\. Switch to "SVN Repository Exploring" perspective.  
Create a new SVN repository location, enter **<svn://svn.alfresco.com>** as the URL, then click on "alfresco" folder and do checkout as project in the workspace.

5\. Configure ANT runtime in Eclipse.  
Go to **Window > Preferences > Ant > Runtime > Properties** and add 3 properties:

[sourcecode language="text" gutter="true" wraplines="false"]  
env.APP_TOMCAT_HOME=C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app  
env.TOMCAT_HOME=C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app  
env.VIRTUAL_TOMCAT_HOME=C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_virtual  
[/sourcecode]

In my case, env.VIRTUAL_TOMCAT_HOME is an empty folder.

5\. Create an ANT project. 

\- Select New > Project in the Eclipse toolbar. Under "General" choose "Project".  
\- Right-click the project and choose New > File  
\- When the new file dialog box appears click the Advanced button and select "Link to file in the file system".  
\- Browse to the build.xml file located in the HEAD\root directory.

### MySQL, OpenOffice, SWFTools, ImageMagick and others 

1\. Install/Unzip MySQL (XAMPP) and create an empty database, for example:

[sourcecode language="text" gutter="true" wraplines="false"]  
create database alfresco;  
grant all on alfresco.* to alfresco@localhost identified by 'alfresco';  
grant all on alfresco.* to alfresco@localhost.localdomain identified by 'alfresco';  
[/sourcecode]

2\. Install ImageMagick and SWFTools. Make sure you both exist in PATH system variable and you can run them from any place.

3\. Install OpenOffice and make sure you can run it from any place.

4\. Setup the Windows NetBios DLLs for CIFS Server:  
Copy **Win32Utils.dll** and **Win32NetBIOS.dll** placed in (C:\2workspace\alfresco20110615\HEAD\root\projects\alfresco-jlan\jni) to the **env.TOMCAT_HOME/bin** folder.

## Pre-configure Alfresco before building

1\. Create the file **alfresco-global.properties** ﻿in **C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app\shared\classes** with the following content:

[sourcecode language="text" gutter="true" wraplines="false"]  
dir.root=C:/1bpms-demo/alfresco_scratch/tomcat-6.0.32_app/alf_data  
web.application.context.url=http://127.0.0.1:8080/alfresco  
### database connection properties ###  
db.driver=org.gjt.mm.mysql.Driver  
db.username=root  
db.password=  
db.name=alfresco  
db.url=jdbc:mysql://localhost/alfresco  
### External executable locations ###  
ooo.exe=C:/1bpms-demo/openoffice-3.2/App/openoffice/program/soffice.exe  
ooo.enabled=false  
img.root=C:/1bpms-demo/ImageMagick-6.7.0-Q16  
img.dyn=${img.root}/lib  
img.exe=${img.root}/convert  
swf.exe=C:/1bpms-demo/swftools0.9.1/pdf2swf.exe  
jodconverter.enabled=true  
jodconverter.officeHome=C:/1bpms-demo/openoffice-3.2/App/openoffice  
jodconverter.portNumbers=8101  
[/sourcecode]

2\. Copy **mysql-connector-java-5.1.13-bin.jar** to **C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app\lib**

3\. Modify **shared.loader** property of the catalina.properties file with following value:

[sourcecode language="text" gutter="true" wraplines="false"]  
shared.loader=${catalina.base}/shared/classes,${catalina.base}/shared/lib/*.jar  
[/sourcecode]

## Building:

All targets for building Alfresco are within **build.xml**.  
You can use ant -projecthelp to see all the available options. Here are a few of the common commands:

[sourcecode language="text" gutter="true" wraplines="false"]  
ant build-tomcat builds and deploys Alfresco for Tomcat  
ant incremental-tomcat incrementally builds and deploys Alfresco for Tomcat  
ant start-tomcat-application executes the Tomcat start up script  
ant build-jboss builds and deploys Alfresco for JBoss  
ant incremental-jboss incrementally builds and deploys Alfresco for JBoss  
ant start-jboss executes the JBoss start up script  
ant test runs unit tests for the entire project  
[/sourcecode]

1\. Run target "build-tomcat" from Eclipse.

2\. Eclipse will create alfresco.war and share.war, they will be copied to webapps folder in Tomcat. 

3\. Now, Alfresco now is ready to be deployed.

[caption id="" align="alignnone" width="631" caption="Alfresco ECM deployed from Eclipse"]![Alfresco ECM deployed from Eclipse]({{ site.baseurl }}/assets/build_alfresco_scratch.png)[/caption]

## Launch Tomcat/Alfresco 

1\. Run Tomcat from C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app\bin\startup.bat. Make sure MySQL is running before.

Alfresco database schema will be created and you will see all log of activity in the console.

2\. If you want deploy/run alfresco from Eclipse: 

In Eclipse add a new server instance from "C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app".  
Edit launch configuration properties of this server adding new VM arguments as:

[sourcecode language="text" gutter="true" wraplines="false"]  
-Dcatalina.base="C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app"  
-Dcatalina.home="C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app"  
-Dwtp.deploy="C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app\webapps"  
-Djava.endorsed.dirs="C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app\endorsed"  
-Xms256m -Xmx1024m -Xss96k -XX:MaxPermSize=256m -server  
-Djava.library.path="C:\1bpms-demo\alfresco_scratch\tomcat-6.0.32_app\bin"  
[/sourcecode]

Now, from Eclipse Server window run Tomcat instance.

## Test Alfresco

Open a browser and go to http://localhost:8080/alfresco or http://localhost:8080/share and enter admin/admin as user/password.

END.
