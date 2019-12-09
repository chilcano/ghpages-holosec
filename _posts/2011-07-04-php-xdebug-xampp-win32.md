---
layout:     post
title:      'PHP debugging with XDebug and XAMPP on Win32'
date:       2011-07-04 16:16:54
categories: ['Security']
tags:       ['apache', 'eclipse', 'php', 'xampp', 'xdebug']
status:     publish 
permalink:  "/2011/07/04/php-xdebug-xampp-win32/"
---
PHP is a powerful language for building from little web applications to bigger applications such as Facebook, Wordpress, Drupal, Joomla, Meneame, Elgg, ...  
and several times you need to solve many bugs and manage your source code, then in this case you will need powerful tools to trace errors.  
A text editor is not enough for this, Eclipse IDE and PHP Development Tools are a good option.
In this post, you learn how to set your debug environment with XDebug, that seems to be one of the more popular ones and Eclipse PDT already has support for it.

## Requisites:
1\. Download and install/unzip [XAMPP](http://www.apachefriends.org/en/xampp-windows.html) for win32 version 1.7.4-VC6.
This bundle comes with severals tools:

[sourcecode language="text" gutter="true" wraplines="false"]  

###### ApacheFriends XAMPP (Basispaket) version 1.7.4 ######
\+ Apache 2.2.17  
\+ MySQL 5.5.8 (Community Server)  
\+ PHP 5.3.5 (VC6 X86 32bit) + PEAR  
\+ XAMPP Control Version 2.5 from www.nat32.com  
\+ XAMPP Security  
\+ SQLite 2.8.15  
\+ OpenSSL 0.9.8o  
\+ phpMyAdmin 3.3.9  
\+ ADOdb 5.11  
\+ Mercury Mail Transport System v4.62  
\+ FileZilla FTP Server 0.9.37  
\+ Webalizer 2.01-10  
\+ Zend Optimizer 3.3.0  
\+ Perl 5.10.1  
\+ Mod_perl 2.0.4  
\+ Tomcat 7.0.3  

[/sourcecode]

[XDebug](http://xdebug.org) comes pre-installed in this bundle.
2\. Download Eclipse IDE (Helios SR2).
3\. Install from Eclipse Marketplace the PHP Development Tools (PDT) plugin (version 3.0.0.x).

## Apache and PHP debugging configuration
1\. Configure XDebug in XAMPP bundle.  
Edit **php.ini** and enable XDebug and remote debugging, make sure to uncomment the bellow lines and put to "1" the parameters " **xdebug.remote_autostart** " and " **xdebug.remote_enable** ".

[sourcecode language="text" gutter="true" wraplines="false"]  

[eAccelerator]  
;zend_extension = "C:\1bpms-demo\xampp-win32-1.7.4-VC6\php\ext\php_eaccelerator.dll"  
...

[XDebug]  
zend_extension = "C:\1bpms-demo\xampp-win32-1.7.4-VC6\php\ext\php_xdebug.dll"  
xdebug.auto_trace = 0  
xdebug.collect_includes = 1  
xdebug.collect_params = 0  
xdebug.collect_return = 0  
xdebug.collect_vars = "Off"  
xdebug.default_enable = "On"  
xdebug.dump.COOKIE = ""  
xdebug.dump.FILES = ""  
xdebug.dump.GET = ""  
xdebug.dump.POST = ""  
xdebug.dump.REQUEST = ""  
xdebug.dump.SERVER = ""  
xdebug.dump.SESSION = ""  
xdebug.dump_globals = 1  
xdebug.dump_once = 1  
xdebug.dump_undefined = 0  
xdebug.extended_info = 1  
xdebug.file_link_format = ""  
xdebug.idekey = ""  
xdebug.manual_url = "http://www.php.net"  
xdebug.max_nesting_level = 100  
xdebug.overload_var_dump = 1  
xdebug.profiler_append = 0  
xdebug.profiler_enable = 0  
xdebug.profiler_enable_trigger = 0  
xdebug.profiler_output_dir = "C:\1bpms-demo\xampp-win32-1.7.4-VC6\tmp"  
xdebug.profiler_output_name = "xdebug_profile.%R::%u"
; xdebug.remote_autostart  
; Type: boolean, Default value: 0  
; Normally you need to use a specific HTTP GET/POST variable to start remote debugging (see Remote  
; Debugging). When this setting is set to 'On' Xdebug will always attempt to start a remote debugging  
; session and try to connect to a client, even if the GET/POST/COOKIE variable was not present.  
xdebug.remote_autostart = 1
; xdebug.remote_enable  
; Type: boolean, Default value: 0  
; This switch controls whether Xdebug should try to contact a debug client which is listening on the  
; host and port as set with the settings xdebug.remote_host and xdebug.remote_port. If a connection  
; can not be established the script will just continue as if this setting was Off.  
xdebug.remote_enable = 1
xdebug.remote_handler = "dbgp"  
xdebug.remote_host = "localhost"  
xdebug.remote_log = "none"  
xdebug.remote_mode = "req"  
xdebug.remote_port = 9000  
xdebug.show_exception_trace = 0  
xdebug.show_local_vars = 0  
xdebug.show_mem_delta = 0  
xdebug.trace_format = 0  
xdebug.trace_options = 0  
xdebug.trace_output_dir = "C:\1bpms-demo\xampp-win32-1.7.4-VC6\tmp"  
xdebug.trace_output_name = "trace.%c"  
xdebug.var_display_max_children = 128  
xdebug.var_display_max_data = 512  
xdebug.var_display_max_depth = 3  
...  

[/sourcecode]
Make sure that XDebug dynamic extension is not loaded in **php.ini**.

[sourcecode language="text" gutter="true" wraplines="false"]  
;extension = php_xdebug.dll  

[/sourcecode]
Re-start apache and verify that XDebug is loaded.

[caption id="" align="alignnone" width="439" caption="XDebug library loaded in Apache HTTPD"]![XDebug library loaded in Apache HTTPD]({{ site.baseurl }}/assets/phpdebugging-1-xdebugloaded.png)[/caption]

## Eclipse & PDT configuration
1\. In Apache HTTP Server add a root folder as your base web for PHP projects.  
For example, my base folder is configured in httpd.conf file and looks like this:

[sourcecode language="xml" gutter="true" wraplines="false"]  
Alias /myphpprojects "C:/2workspace"  
<Directory "C:/2workspace">  
AllowOverride none  
Options None  
Order allow,deny  
Allow from all  
</Directory>  

[/sourcecode]
2\. In Eclipse, create a test php project in " **C:/2workspace** " and select PHP 5.3 runtime when creating. For example, create " **testdebug01** " php project.

[caption id="" align="alignnone" width="322" caption="Creating PHP Project from Eclipse IDE and PDT"]![Creating PHP Project from Eclipse IDE and PDT]({{ site.baseurl }}/assets/phpdebugging-2-eclipsecreateproj.png)[/caption]
3\. Create a **helloworld.php** as you test PHP script in above project, the script looks like this:

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?php  
$msg = "crazy world";  
echo "<h1>Test PHP Debugging #1 </h1>";  
echo "<hr>";  
echo "<ol>";  
for ($k = 0; $k < 10; $k++) {  
print("<li>Hello, $msg ($k)! <br>");  
}  
echo "</ol>";  
?>  

[/sourcecode]
4\. In Eclipse, go to **Windows > Preferences > PHP > Debug** and make sure XDebug is selected as default PHP Debugger.

[caption id="" align="alignnone" width="358" caption="Configuring PHP Debug in Eclipse IDE"]![Configuring PHP Debug in Eclipse IDE]({{ site.baseurl }}/assets/phpdebugging-3-eclipsephpdebug.png)[/caption]
5\. Now, you are ready for launching debugging from Eclipse. Firstly, put a breakpoint in **helloworld.php** and then go to left side on Navigator, right click on **helloworld.php** file and select " **Debug Configurations** ".  
In the next window configure following:
  * **Name** : Your debug profile configuration, in this example is "Test debug 01".
  * **Server Debugger** : XDebug
  * **File** : Your php script that trigger debug environment. This file has to a breakpoint. In this example is "/testdebug01/helloworld.php".
  * **Auto Generate** : Unchecked if you want change URL of your php application. In this example is "/myphpprojects/testdebug01/helloworld.php".

[caption id="" align="alignnone" width="481" caption="Launching debug from PHP Project in Eclipse IDE"]![Launching debug from PHP Project in Eclipse IDE]({{ site.baseurl }}/assets/phpdebugging-4-debugconf.png)[/caption]
Click in " **Run** " button and if all is OK, Eclipse switchs to Debug perspective where you can debug your PHP application.

[caption id="" align="alignnone" width="449" caption="Debugging PHP Project from Eclipse IDE"]![Debugging PHP Project from Eclipse IDE]({{ site.baseurl }}/assets/phpdebugging-5-debugging.png)[/caption]

## Conclusions:
  * With XDebug you can debug remotely from Eclipse and PDT. In this case you have to change localhost for you server-name in **php.ini** file and **Eclipse > Preferences > PHP > PHP Servers**.
  * If you set " **/** " as web base path instead of " **/myphpprojects** " you could avoid to configure special URL in Auto Generate box (Debug Configurations window). Always you URL will be " **http://localhost/your-php-project-name** ".
