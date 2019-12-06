---
layout: post
title: Remote access to CentOS with VNC Viewer
date: 2010-08-16 16:56:44.000000000 +02:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Linux
- Security
tags:
- CentOS
meta:
  _edit_last: '578869'
  _oembed_2989affeb4b2433fa0c24424a60d5109: "{{unknown}}"
  _oembed_a7b3230cd8a962f4170075d21049a159: "{{unknown}}"
  _oembed_89d48096ebc492d84cd61e3d51037973: "{{unknown}}"
  _wp_old_slug: ''
  _oembed_b94301bbd84fab4657e9e953ab68e629: "{{unknown}}"
  _oembed_fb7d1f7ac9bc37def426ee16c7dc5d8e: "{{unknown}}"
  _oembed_3e120380974de7336a5cdea2ee96ddc6: "{{unknown}}"
  _oembed_9c84542b9813dad92f175f84179a1357: "{{unknown}}"
  _oembed_b73f606364a8ebf76c97fcd6c8c9953f: "{{unknown}}"
  tagazine-media: a:7:{s:7:"primary";s:78:"http://dl.dropbox.com/u/2961879/blog20100816_vcncentos/vnc_centos-2-centos.png";s:6:"images";a:3:{s:80:"http://dl.dropbox.com/u/2961879/blog20100816_vcncentos/vnc_centos-3-real-vnc.png";a:6:{s:8:"file_url";s:80:"http://dl.dropbox.com/u/2961879/blog20100816_vcncentos/vnc_centos-3-real-vnc.png";s:5:"width";s:3:"489";s:6:"height";s:3:"432";s:4:"type";s:5:"image";s:4:"area";s:6:"211248";s:9:"file_path";s:0:"";}s:76:"http://dl.dropbox.com/u/2961879/blog20100816_vcncentos/vnc_centos-1-open.png";a:6:{s:8:"file_url";s:76:"http://dl.dropbox.com/u/2961879/blog20100816_vcncentos/vnc_centos-1-open.png";s:5:"width";s:3:"381";s:6:"height";s:3:"368";s:4:"type";s:5:"image";s:4:"area";s:6:"140208";s:9:"file_path";s:0:"";}s:78:"http://dl.dropbox.com/u/2961879/blog20100816_vcncentos/vnc_centos-2-centos.png";a:6:{s:8:"file_url";s:78:"http://dl.dropbox.com/u/2961879/blog20100816_vcncentos/vnc_centos-2-centos.png";s:5:"width";s:4:"1039";s:6:"height";s:3:"404";s:4:"type";s:5:"image";s:4:"area";s:6:"419756";s:9:"file_path";s:0:"";}}s:6:"videos";a:0:{}s:11:"image_count";s:1:"3";s:6:"author";s:6:"578869";s:7:"blog_id";s:7:"2005905";s:9:"mod_stamp";s:19:"2010-08-16
    15:56:44";}
  _oembed_7f088755dbb8534adb3db03cd8e6504e: "{{unknown}}"
  _oembed_c72d3237451ec8538234ae2d71c55347: "{{unknown}}"
  _oembed_59ef8c16c7bb29deaaee819fda2378b4: "{{unknown}}"
  _oembed_5a6828fda47eb3a485e384ce431de878: "{{unknown}}"
  _oembed_401ed3171109b8cdf8af255f4ccac1f6: "{{unknown}}"
  _oembed_1d70232f650b54464b8bef556d9fa24e: "{{unknown}}"
  _oembed_22fb289cd25f9636362f874c46783054: "{{unknown}}"
  _oembed_32e17b47230f2b20f7911bf6809bcee1: "{{unknown}}"
  _oembed_47c0b8e48507f4fbdb16b832574ca03b: "{{unknown}}"
  _oembed_7b230eea9e7ec2a01cdaf34d5752302b: "{{unknown}}"
  _oembed_9ca9dd1adebfe6060eb99106df095580: "{{unknown}}"
  _oembed_a09e6667c82fa6035e52bf6e59af45fe: "{{unknown}}"
  _oembed_35d8a3382ddc95f0f9f855c75a0b15fa: "{{unknown}}"
  _oembed_34eb7909fa117a6a7eb884202569b214: "{{unknown}}"
  _oembed_0899b76eb6ac648683526c4888c9cf44: "{{unknown}}"
  _oembed_0cc2448337d0b9b4f6ae10ed1cee3725: "{{unknown}}"
  geo_public: '0'
  _wpas_skip_13849: '1'
  _wpas_skip_5053092: '1'
  _wpcom_is_markdown: '1'
  _oembed_0ecb2e28e4b1861445bc29dcac9f76c5: "{{unknown}}"
  _oembed_c6176cbc9429ae97ebc5ee9dfc7b2832: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/08/16/remote-access-to-centos-with-vnc-viewer/"
---
![]({{ site.baseurl }}/assets/vnc_centos-0-real-vnc-1.png)

  


  
  
First, install VNC in CentOS:

  

    
    
    [root@svdapp95 ~]# yum  --nogpgcheck install vnc

  


  

  1. Create another user:
  

  

    
    
    [root@svdapp95 ~]# useradd roger  
    [root@svdapp95 ~]# su - roger  
    [roger@svdapp95 ~]$ vncpasswd  
    Password:  
    Verify:  
    [roger@svdapp95 ~]$ cd .vnc  
    [roger@svdapp95 .vnc]$ ll  
    total 8  
    -rw------- 1 roger roger 8 Aug  6 17:04 passwd

  


  

  1. Create vnc password for root too:
  

  

    
    
    [root@svdapp95 ~]# vncpasswd  
    Password:  
    Verify:  
    [root@svdapp95 ~]# ls -la  
    [...]  
    [root@svdapp95 ~]# cd .vnc/  
    [root@svdapp95 .vnc]# ll  
    total 8  
    -rw------- 1 root root 8 Aug  6 16:45 passwd  
    [root@svdapp95 .vnc]#

  


  

  1. Edit configuration of VNC server:
  

  

    
    
    [root@svdapp95 ~]# nano /etc/sysconfig/vncservers
    
    
    VNCSERVERS="1:roger 2:root"
    VNCSERVERARGS[1]="-geometry 800x600"
    VNCSERVERARGS[2]="-geometry 1024x800"

  1. Create xstartup script with root:


    
    
    [root@svdapp95 .vnc]# /sbin/service vncserver start
    Starting VNC server: 1:roger xauth:  creating new authority file /home/roger/.Xauthority
    
    New 'svdapp95:1 (roger)' desktop is svdapp95:1
    
    Creating default startup script /home/roger/.vnc/xstartup
    Starting applications specified in /home/roger/.vnc/xstartup
    Log file is /home/roger/.vnc/svdapp95:1.log
    
    2:root xauth:  creating new authority file /root/.Xauthority
    
    New 'svdapp95:2 (root)' desktop is svdapp95:2
    
    Creating default startup script /root/.vnc/xstartup
    Starting applications specified in /root/.vnc/xstartup
    Log file is /root/.vnc/svdapp95:2.log
    
                                                               [  OK  ]
    [root@svdapp95 .vnc]# /sbin/service vncserver stop
    Shutting down VNC server: 1:roger 2:root                   [  OK  ]
    [root@svdapp95 .vnc]#

  1. Edit xstartup config file created in step 4 for each user added in vncservers in step 3:


    
    
    [root@svdapp95 ~]# nano /root/.vnc/xstartup

Initial file for root:
    
    
    #!/bin/sh
    
    # Uncomment the following two lines for normal desktop:
    # unset SESSION_MANAGER
    # exec /etc/X11/xinit/xinitrc
    
    [ -x /etc/vnc/xstartup ] && exec /etc/vnc/xstartup
    [ -r $HOME/.Xresources ] && xrdb $HOME/.Xresources
    xsetroot -solid grey
    vncconfig -iconic &
    xterm -geometry 80x24+10+10 -ls -title "$VNCDESKTOP Desktop" &
    twm &

New file for root:
    
    
    #!/bin/sh
    
    # Add the following line to ensure you always have an xterm available.
    ( while true ; do xterm ; done ) &
    
    # Uncomment the following two lines for normal desktop:
    unset SESSION_MANAGER
    exec /etc/X11/xinit/xinitrc
    
    [ -x /etc/vnc/xstartup ] && exec /etc/vnc/xstartup
    [ -r $HOME/.Xresources ] && xrdb $HOME/.Xresources
    xsetroot -solid grey
    vncconfig -iconic &
    xterm -geometry 80x24+10+10 -ls -title "$VNCDESKTOP Desktop" &
    twm &

... repeat for "roger" user too.

  1. Start VNC server:


    
    
    [root@svdapp95 ~]# /sbin/service vncserver start

  1. Login to VNC server from web browser:



From another PC (you connect to VNC server with both users configured, for example:  
\- Open web browser  
\- Go to http://<ip-vncserver>:5801 for user "roger" and http://<ip-vncserver>:5802 for user "root".  
\- In my case: http://172.23.3.197:5801

![]({{ site.baseurl }}/assets/vnc_centos-3-real-vnc.png)

  1. Login to VNC server from any vcn client, for example, vnc viewer:


  * Open VNC Viewer
  * Go to http://<ip-vncserver>:1 for user "roger" and http://<ip-vncserver>:2 for user "root".



Open connection:  
![]({{ site.baseurl }}/assets/vnc_centos-1-open.png)

Remote access in CentOS:  
![]({{ site.baseurl }}/assets/vnc_centos-2-centos.png)

**References:**  
* VNC ( Virtual Network Computing )  
http://wiki.centos.org/es/HowTos/VNC-Server

  * Conexión a VNC a través de firewalls y proxys utilizando túneles SSH  
http://www.eslomas.com/index.php/archives/2006/07/05/conexion-remota-vnc-proxy-firewall-tunel-ssh/


