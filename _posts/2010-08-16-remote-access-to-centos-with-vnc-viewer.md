---
layout:     post
title:      'Remote access to CentOS with VNC Viewer'
date:       2010-08-16 14:56:44
categories: ['Open Source', 'Security']
tags:       ['Linux']
status:     publish 
permalink:  "/2010/08/16/remote-access-to-centos-with-vnc-viewer/"
---
![](/assets/blog20100816_vcncentos/vnc_centos-0-real-vnc-1.png)

<!-- more -->  

First, install VNC in CentOS:

```sh
[root@svdapp95 ~]# yum  --nogpgcheck install vnc
```

1. Create another user:
  ```sh   
  [root@svdapp95 ~]# useradd roger  
  [root@svdapp95 ~]# su - roger  
  [roger@svdapp95 ~]$ vncpasswd  
  Password:  
  Verify:  
  [roger@svdapp95 ~]$ cd .vnc  
  [roger@svdapp95 .vnc]$ ll  
  total 8  
  -rw------- 1 roger roger 8 Aug  6 17:04 passwd
  ```
2. Create vnc password for root too:
  ```sh
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
  ```
3. Edit configuration of VNC server:
  ```sh
  [root@svdapp95 ~]# nano /etc/sysconfig/vncservers
  VNCSERVERS="1:roger 2:root"
  VNCSERVERARGS[1]="-geometry 800x600"
  VNCSERVERARGS[2]="-geometry 1024x800"
  ```
4. Create xstartup script with root:
  ```sh
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
  ```
5. Edit xstartup config file created in step 4 for each user added in vncservers in step 3:
  ```sh
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
  ```
  
  New file for root:
  ```sh
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
  ```
  ... repeat for "roger" user too.
6. Start VNC server:
  ```sh
  [root@svdapp95 ~]# /sbin/service vncserver start
  ```
7. Login to VNC server from web browser:
  From another PC (you connect to VNC server with both users configured, for example:  
  - Open web browser  
  - Go to http://<ip-vncserver>:5801 for user "roger" and http://<ip-vncserver>:5802 for user "root".  
  - In my case: http://172.23.3.197:5801
  
  ![](/assets/blog20100816_vcncentos/vnc_centos-3-real-vnc.png)
8. Login to VNC server from any vcn client, for example, vnc viewer:
  * Open VNC Viewer
  * Go to http://<ip-vncserver>:1 for user "roger" and http://<ip-vncserver>:2 for user "root".
  Open connection:  
  ![](/assets/blog20100816_vcncentos/vnc_centos-1-open.png)
  
  Remote access in CentOS:  
  ![](/assets/blog20100816_vcncentos/vnc_centos-2-centos.png)

**References:**  
* VNC ( Virtual Network Computing )  
  - http://wiki.centos.org/es/HowTos/VNC-Server
* Conexión a VNC a través de firewalls y proxys utilizando túneles SSH  
  - http://www.eslomas.com/index.php/archives/2006/07/05/conexion-remota-vnc-proxy-firewall-tunel-ssh/
