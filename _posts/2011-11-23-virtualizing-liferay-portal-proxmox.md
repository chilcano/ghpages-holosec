---
layout: post
title: Virtualizing Liferay Portal with Proxmox VE
date: 2011-11-23 18:50:25.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Security
tags: []
meta:
  _wpas_done_twitter: '1'
  _edit_last: '578869'
  _oembed_134b02cdd756a5004754470bdbbdcc33: "{{unknown}}"
  _oembed_c98faa8352096ff682c31154d0833882: "{{unknown}}"
  _oembed_f8f6062966e5732814a9b0e67e8cabeb: "{{unknown}}"
  _oembed_03f54d97db98aa23799079e00e27cd15: "{{unknown}}"
  _oembed_42acac702323b1ab1b6569b3d60f1804: "{{unknown}}"
  _oembed_8418bc9ea51d714baa1d3c8ccdedc897: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2011/11/23/virtualizing-liferay-portal-proxmox/"
---
Some days ago I had to deploy a Liferay Portal in a hosting provider where Proxmox Virtual Environment (PVE) was necessary to use.

  
[Proxmox Virtual Environment](http://pve.proxmox.com/wiki/Network_Model) is a Debian distro with pre-installed packages ready to use when you want to create virtualization platform for running Virtual Appliances and Virtual Machines based on OpenVZ and KVM.

  
But, What is difference between tradicional hosting and hosting based on Proxmox VE?, really does not exist differences because with Proxmox VE you can virtualize all (network, server, vlan, etc.).

  
Then, I did create a virtualized network with 2 servers based on Proxmox, the first server runs a Linux Debian with Apache HTTP server as web-proxy, the second server will be a private server with Liferay Portal.

  
The architecture final is as follow:

  
[caption id="" align="alignnone" width="462" caption="Architecture - Liferay in an environment virtualized with Proxmox"][![Architecture - Liferay in an environment virtualized with Proxmox]({{ site.baseurl }}/assets/www.intix.info_proxmox_liferay_arquitectura.png)](http://dl.dropbox.com/u/2961879/blog20111108_proxmoxliferay/www.intix.info_proxmox_liferay_arquitectura.png)[/caption]

  
## 1\. Installation first server with Proxmox / Apache2 HTTP Server (In Host)

  
When installing Proxmox in your hosting provider you will have as principal public server a first server with Debian Linux,  
  
its initial configuration is as follow:

  
[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
  
root@kns ~ # nano /etc/network/interfaces

  
### Hetzner Online AG - installimage  
  
# Loopback device:  
  
auto lo  
  
iface lo inet loopback

  
# device: eth0  
  
auto eth0  
  
iface eth0 inet static  
  
address 176.9.30.36  
  
broadcast 176.9.30.63  
  
netmask 255.255.255.224  
  
gateway 176.9.30.33  
  
pointopoint 176.9.30.33  
  
post-up mii-tool -F 100baseTx-FD eth0

  
# default route to access subnet  
  
up route add -net 176.9.30.32 netmask 255.255.255.224 gw 176.9.30.33 eth0  
  
[/sourcecode]  
  
[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
  
root@kns ~ # more /etc/resolv.conf

  
search  
  
nameserver 213.133.100.100  
  
nameserver 213.133.98.98  
  
nameserver 213.133.99.99  
[/sourcecode]

After of installing Proxmox, you have to create virtual network with a private IP range, in this case I will use 10.10.10.x.

### 1.1. Creating Virtual Network with Proxmox

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
root@kns ~ # nano /etc/network/interfaces

# Loopback device:  
auto lo  
iface lo inet loopback

# device: eth0  
auto eth0  
iface eth0 inet static  
address 176.9.30.36  
broadcast 176.9.30.63  
netmask 255.255.255.224  
gateway 176.9.30.33  
pointopoint 176.9.30.33  
post-up mii-tool -F 100baseTx-FD eth0  
post-up echo 1 > /proc/sys/net/ipv4/conf/eth0/proxy_arp

#### my new virtual network  
auto vmbr0  
iface vmbr0 inet static  
address 10.10.10.1  
netmask 255.255.255.0  
bridge_ports none  
bridge_stp off  
bridge_fd 0  
post-up echo 1 > /proc/sys/net/ipv4/ip_forward  
post-up iptables -t nat -A POSTROUTING -s '10.10.10.0/24' -o eth0 -j MASQUERADE  
post-down iptables -t nat -D POSTROUTING -s '10.10.10.0/24' -o eth0 -j MASQUERADE  
[/sourcecode]  
[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
root@kns ~ # nano /etc/sysctl.d/10-no-icmp-redirects.conf

#### my bridge to VM  
net.ipv4.conf.all.send_redirects = 0  
[/sourcecode]  
[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
root@kns ~ # nano /etc/sysctl.conf

### Hetzner Online AG installimage  
# sysctl config  
net.ipv4.conf.all.rp_filter=1  
net.ipv4.icmp_echo_ignore_broadcasts=1  
net.ipv4.ip_forward=1  
[/sourcecode]

## 2\. Installation of second server with Liferay 6.0.6 CE (a Guest VM)

### 2.1. Install your prefered Linux distro in your new VM

With Proxmox VE is easy and quickly to create VM from ISO image.  
You can change VM parameters as RAM, HD, type of Network interface, MAC, etc. all from Proxmox VE web interface, or if you know Proxmox's commands you could do from SSH terminal.

### 2.2. Configure eth0 in Guest

After of creating your VM with Proxmox, you will need to asign private IP or customize your VM, in this case you will need a VNC terminal to apply these changes.  
With Proxmox you can get a shell/terminal to Guest VM without connection to virtualized network.

Then, from Proxmox VE (interface web) go to your recently VM created and open a VNC terminal, then here runs following commands:

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
[root@pisco ~]# nano /etc/sysconfig/network-scripts/ifcfg-eth0

DEVICE="eth0"  
NM_CONTROLLED="yes"  
ONBOOT=yes  
HWADDR=3E:DB:61:48:63:88  
TYPE=Ethernet  
BOOTPROTO=none  
IPADDR=10.10.10.12  
PREFIX=24  
GATEWAY=10.10.10.1  
DNS1=213.133.99.99  
DEFROUTE=yes  
IPV4_FAILURE_FATAL=yes  
IPV6INIT=no  
NAME="System eth0"  
UUID=5fb06bd0-0bb0-7ffb-45f1-d6edd65f3e03  
[/sourcecode]  
[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
[root@pisco ~]# more /etc/resolv.conf

search local  
nameserver 213.133.99.99  
[/sourcecode]

...add gateway

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
[root@pisco ~]# ip route add 176.9.30.36 dev eth0  
[/sourcecode]

..add default path

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
[root@pisco ~]# ip route add default via 176.9.30.36  
[/sourcecode]

### 2.3. Re-start VM and test configuration

[sourcecode language="text" gutter="true" wraplines="false" highlight="1"]  
[root@pisco ~]# ping www.google.com

PING www.l.google.com (74.125.39.103) 56(84) bytes of data.  
64 bytes from fx-in-f103.1e100.net (74.125.39.103): icmp_seq=1 ttl=55 time=6.77 ms  
64 bytes from fx-in-f103.1e100.net (74.125.39.103): icmp_seq=2 ttl=53 time=6.81 ms  
[/sourcecode]

### 2.4. Install Liferay, then move Liferay to non-root context

Liferay will be running with this URL: http://10.10.10.12:8080/kns

## 3\. Move Proxmox Virtual Environment 1.9 (proxmox's website) from 80 port to 8080 port.

Next blog post.

## 4\. Configure first server as Reverse Web Proxy

Next blog post.

## 5\. Test installation

From Internet you can call this URL: **http://myliferay.intix.info/kns** , **http://176.9.30.36/kns** or http://${YOUR-PUBLIC-IP}/kns

Bye.

## References

  * Proxmox - Network model:  
<http://pve.proxmox.com/wiki/Network_Model>