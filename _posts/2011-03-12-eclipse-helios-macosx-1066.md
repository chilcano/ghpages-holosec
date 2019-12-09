---
layout:     post
title:      'Eclipse Helios in Mac OS X 10.6.6'
date:       2011-03-12 11:41:25
categories: ['Linux']
tags:       []
status:     publish 
permalink:  "/2011/03/12/eclipse-helios-macosx-1066/"
---
I recently bought a MacBook Pro (2 GHz Intel Core i7 with 64-bit kernel and extensions enabled) to develop with Java. First thing to do was updating Java and installing Eclipse IDE, but everything about Eclipse was very slow.
So I started googling and found the solution here:

[http://www.eclipse.org/forums/index.php?t=tree&th=205238](http://www.eclipse.org/forums/index.php?t=tree&th=205238)
The problem was related to 64-bit, Java and Eclipse are ready for working on 64-bit but you have to configure them.
Then, open eclipse.ini and update of this way (lines updated are 12, 16, 19, 20 and 21):

[sourcecode language="text" gutter="true" wraplines="false" highlight="12,16,19,20,21"]  
-startup  
../../../plugins/org.eclipse.equinox.launcher_1.1.1.R36x_v20101122_1400.jar  
\--launcher.library  
../../../plugins/org.eclipse.equinox.launcher.cocoa.macosx.x86_64_1.1.2.R36x_v20101019_1345  
-product  
org.eclipse.epp.package.jee.product  
\--launcher.defaultAction  
openFile  
-showsplash  
org.eclipse.platform  
\--launcher.XXMaxPermSize  
512m  
\--launcher.defaultAction  
openFile  
-vmargs  
-Dosgi.requiredJavaVersion=1.6  
-XstartOnFirstThread  
-Dorg.eclipse.swt.internal.carbon.smallFonts  
-XX:MaxPermSize=512m  
-Xms80m  
-Xmx1024m  
-Xdock:icon=../Resources/Eclipse.icns  
-XstartOnFirstThread  
-Dorg.eclipse.swt.internal.carbon.smallFonts  

[/sourcecode]
Regards.
