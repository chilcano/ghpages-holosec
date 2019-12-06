---
layout: post
title: How to set back the clock of Mac OS X host in Virtual Box
date: 2011-02-20 11:38:18.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Security
tags:
- virtualbox
meta:
  _edit_last: '578869'
  _wpas_done_twitter: '1'
  _oembed_681371de4c3047ebab8b85e866682ea5: "{{unknown}}"
  _oembed_572c6119946ae43aed99018cf88eee53: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2011/02/20/offset-clock-virtualbox-macosx-winxp/"
---
I have a Mac OS X as host and Windows XP as guest virtualbox machine. I need to set back the clock 2 years in the guest.

  


I used to use this command, but it does not work with Mac OS X as host.

  


[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
  
VBoxManage setextradata <nameVm> “VBoxInternal/TM/UTCOffset” <nanoSeconds>  
  
[/sourcecode]

  


I tried this and it seems that it works.

  


[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
  
pisco:~ chilcano$ vboxmanage modifyvm intix_winxp01 --biossystemtimeoffset -63072000000  
  
Oracle VM VirtualBox Command Line Management Interface Version 3.2.12  
  
(C) 2005-2010 Oracle Corporation  
  
All rights reserved.

  


pisco:~ chilcano$  
  
[/sourcecode]

  


Notes:

  


  

  *  _ **intix_winxp**_ is the name of guest vm.  

  

  *  _ **-63072000000**_ are milisecs (365 + 365 days).  

  

  * The command _**vboxmanage**_ is in lowercase.  

  

  


and if you want to reset time offset you have to run this:

  


[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
  
pisco:~ chilcano$ vboxmanage modifyvm intix_winxp01 --biossystemtimeoffset 0  
  
Oracle VM VirtualBox Command Line Management Interface Version 3.2.12  
  
(C) 2005-2010 Oracle Corporation  
  
All rights reserved.

  


pisco:~ chilcano$  
  
[/sourcecode]

  


 **References**

  


  

  1. About the clock of a VM (http://forum.virtualbox.org/viewtopic.php?f=7&p=150430) 
  2. Detener la sincronización de tiempo/fechas entre Host y Guest en Virtual Box (http://holisticsecurity.wordpress.com/2009/11/27/detener-la-sincronizacion-de-tiempofechas-entre-host-y-guest-en-virtual-box) 


