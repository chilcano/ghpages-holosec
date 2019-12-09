---
layout:     post
title:      'Remote debugging of Liferay portlets'
date:       2011-09-08 22:20:08
categories: ['Portal']
tags:       ['debug', 'eclipse', 'ide', 'Liferay']
status:     publish 
permalink:  "/2011/09/09/remote-debugging-liferay-portlets/"
---
Debugging is a task very important when building software, this allows software development with high level of quality (without bugs) because "debugging" enable you repeat cycles of "to do and to test" several time.
When developing Liferay portlets, Liferay Portal server will contain the portlets in a PC different of the PC of developer.
For this reason, debugging is an important task when working in distributed project teams with a single and/or centralized development server.
Then, remote debugging means you could debug an application or portlet on server from a different computer.
This post discusses how to use the Eclipse IDE for remote debugging on Liferay.

## Versions used
  * Liferay Portal bundled with Tomcat: liferay-portal-tomcat-6.0.6-20110225.zip
  * Liferay IDE 1.2

## Remote Debugging configuration
1\. In your Liferay server side, if you have installed Tomcat bundle, to add this line into $TOMCAT_HOME/bin/setenv.bat or setenv.sh before you set any JAVA_OPTS:
In windows:

[sourcecode language="text" gutter="true" wraplines="false"]  
set JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n  

[/sourcecode]
In Mac OSX the file will be as follow:

[sourcecode language="text" gutter="true" wraplines="false"]  

## intix - remote debugging  
JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"  
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF8 -Duser.timezone=GMT -Xmx1024m -XX:MaxPermSize=256m"  

[/sourcecode]
2\. Restart Tomcat server. You should see in log (catalina.out) a message showing that port 8000 is opened and ready to use.

[sourcecode language="text" gutter="true" wraplines="false"]  

[...]  
Listening for transport dt_socket at address: 8000  
Sep 8, 2011 9:55:02 PM org.apache.catalina.core.AprLifecycleListener init  
INFO: The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: .:/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java  
Sep 8, 2011 9:55:02 PM org.apache.coyote.http11.Http11Protocol init  
INFO: Initializing Coyote HTTP/1.1 on http-8080  
Sep 8, 2011 9:55:02 PM org.apache.catalina.startup.Catalina load  
INFO: Initialization processed in 456 ms  

[...]  

[/sourcecode]
Note:  
* If you have Liferay Portal configured as server into your Liferay IDE, do not launch Tomcat from this IDE. It is possible that remote debugging port is not opened.  
* For a successfully debugging, run Tomcat from a shell as follow:

[sourcecode language="text" gutter="true" wraplines="false"]  
chilcano$ sh ./startup.sh ; tail -f ../logs/catalina.out  

[/sourcecode]
3\. In your PC of development, to open Eclipse, Liferay IDE or Liferay Studio, in top menu to do click on "Run > Debug Configurations...", will appear a dialog setup window where you have to create a new "Remote Java Application".  
Follow the image bellow:

[caption id="" align="alignnone" width="505" caption="The Debugging Configuration window"][![The Debugging Configuration window]({{ site.baseurl }}/assets/remotedebuglfry1_debugconfiguration.png)](http://dl.dropbox.com/u/2961879/blog20110908_eclipseremotedebugliferay/remotedebuglfry1_debugconfiguration.png)[/caption]
4\. Under "Remote Java Application" to add a entry, add values of hostname and port of remote server.  
Follow the image bellow:

[caption id="" align="alignnone" width="481" caption="Debug Configuration - set hostname and port of remote server"][![Debug Configuration - set hostname and port of remote server]({{ site.baseurl }}/assets/remotedebuglfry2_debugconfiguration.png)](http://dl.dropbox.com/u/2961879/blog20110908_eclipseremotedebugliferay/remotedebuglfry2_debugconfiguration.png)[/caption]
5\. In "Debug Configurations..." dialog window, click "Source" and to add source code of your project to be debugged. At end, start debugging by click on "Debug" button.

[caption id="" align="alignnone" width="481" caption="Debug Configuration - attach source code and start debugging"][![Debug Configuration - attach source code and start debugging]({{ site.baseurl }}/assets/remotedebuglfry3_debugconfiguration.png)](http://dl.dropbox.com/u/2961879/blog20110908_eclipseremotedebugliferay/remotedebuglfry3_debugconfiguration.png)[/caption]
Your IDE will ask you if you want to change to "Debug perspective". You accept it.
6\. In your IDE, put some "breakpoints" on your code.
7\. Open your browser and go to your webapp's URL, if you put breakpoints are in appropriate places, then Eclipse will show the line of code being executed.
You have nice debug on Liferay!.
Bye.

**References** :
  * <http://www.liferay.com/community/wiki/-/wiki/Main/Eclipse>
