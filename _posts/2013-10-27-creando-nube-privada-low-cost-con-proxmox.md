---
layout:     post
title:      'Creando un nube privada "low cost" con Proxmox VE 3.0'
date:       2013-10-27 19:55:47
categories: ['Cloud', 'Linux', 'PaaS']
tags:       ['cloud', 'Debian Wheezy', 'proxmox', 'Security', 'virtualization', 'VM']
status:     publish 
permalink:  "/2013/10/27/creando-nube-privada-low-cost-con-proxmox/"
---
Actualmente existe una oferta muy rica de proveedores de infraestructura o hosting y proveedores de tecnología para sacar el máximo provecho de dicha infraestructura, aún más importante, a un coste razonablemente bajo usando tecnología free y open source.

[![proxmox-logo]({{ site.baseurl }}/assets/proxmox-logo.png)](http://holisticsecurity.files.wordpress.com/2013/10/proxmox-logo.png)
En mi caso, esta infraestructura la usaré para construir una plataforma donde provea servicios alrededor de productos como WSO2 ESB, Liferay Portal, Alfresco ECM, Drupal, etc., para ello he contratado un hosting medianamente decente en Hetzner (repito experiencia) y he empleado Proxmox VE.

# I. Por qué Proxmox VE ?
Principalmente, porque ya tenía experiencia previa con Proxmox con las versión 2.0 de años anteriores, aunque actualmente hay productos opensource y más sofisticados como Apache CloudStack, OpenStack y Eucalyptus, pero el conocimiento previo y su sencillez me hizo decidir su uso. Una cosa importante a destacar es el corto tiempo necesario para crear un nuevo servidor, por lo general me toma 10 minutos.

# II. Qué servicios ofreceremos desde nuestra nube privada?
Los servicios ofrecidos estarán alrededor del desarrollo de aplicaciones usando el Stack de WSO2, Liferay Portal, Alfresco ECM, Gestión de Identidades con WSO2 IS, etc. En otras palabras, nuestra nube privada será nuestra plataforma base para desarrollar y explotar nuestras aplicaciones construidas con las anteriores tecnologías mencionadas.

# III. Por qué no usar WSO2 Stratos, RedHat OpenShift u otro PaaS
Por la naturaleza del tipo de servicios a ofrecer, evidentemente lo mejor era usar algún PaaS stack y la más adecuada era WSO2 Stratos, aunque como indiqué en el punto anterior, el conocimiento previo de Proxmox me hizo decidir como primera opción.
Bueno, una vez puesto mis argumentos, paso a explicar todo el proceso que seguí para tener mi nube privada.

# IV. Defina una arquitectura de virtualización o mapa de VMs
Sí, es muy importante planear cómo quedará tu nube privada, IPs, Redes Virtuales, DMZ, Storage vs. Partitions, RAM / VM, Security y Firewalling, etc.
Aquí os muestro mi mapa de VMs.

[caption id="" align="alignnone" width="665"][![A example of Private Cloud Architecture with Proxmox]({{ site.baseurl }}/assets/blog20131027_privatecloud_proxmox_architecture.png)](https://dl.dropboxusercontent.com/u/2961879/blog.sec/blog20131027_privatecloud_proxmox/blog20131027_privatecloud_proxmox_architecture.png) A example of Private Cloud Architecture with Proxmox[/caption]
Después de planear la arquitectura de tu nube, los siguientes pasos están relacionados a la instalación y post-configuración de tu hosting.

# V. Preparando la infraestructura base
Algunos requisitos previos:
  * Debian 7.0 (wheezy) o CentOS 6.4
  * Proxmox VE 3.0
  * IP pública de tu hosting: 144.76.70.175
  * Nuevo SSH port: 4141 y 8181

## 1\. Instalar el S.O.
  * Ir a Robot > Linux y seleccionar Debian 7 - minimal, luego reboot desde ssh:

[sourcecode language="html" gutter="true" wraplines="false"]

# shutdown -rf now

[/sourcecode]
  * Verificar la versión de S.O. por defecto instalada en el Hosting

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # uname -a  
Linux chkry1 3.2.0-4-amd64 #1 SMP Debian 3.2.46-1 x86_64 GNU/Linux  

[/sourcecode]

## 2\. Preparación básica del S.O. (Debian Wheezy)

**1) Cambiar password de root**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # passwd root  

[/sourcecode]

**2) Actualizar Debian**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # apt-get update  

[/sourcecode]

**3) Renombrar host**

[sourcecode language="html" gutter="true" wraplines="false"]

# nano /etc/hostname

# nano /etc/hosts

[/sourcecode]
  * Reinicie el servicio

[sourcecode language="html" gutter="true" wraplines="false"]

# /etc/init.d/hostname.sh

[/sourcecode]
  * Verifique los cambios, salga y vuelva a entrar o:

[sourcecode language="html" gutter="true" wraplines="false"]

# hostname --fqdn

[/sourcecode]

**4) Cambiar puerto SSH**

  * http://ubuntu-tutorials.com/2008/01/12/disabling-ssh-connections-on-ipv6/

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/ssh/sshd_config  

[/sourcecode]
  * Reemplazar las líneas 'Port 22' y 'Port 222' por 'Port 4141' y añadir 'AddressFamily inet' (ipv4)

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # service ssh restart  

[/sourcecode]

**5) Desabilite IPV6**

  * http://forums.debian.net/viewtopic.php?f=16&t=85551
  * Verificar que realmente este funcionando ipv6

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # netstat -tunlp |grep p6 |wc -l  
5  

[/sourcecode]
  * Actualizar el kernel y hosts para no cargar ipv6

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # echo net.ipv6.conf.all.disable_ipv6=1 > /etc/sysctl.d/disableipv6.conf  
root@chkry1 ~ # sed '/::/s/^/#/' /etc/hosts >/etc/dipv6-tmp; cp -a /etc/hosts /etc/hosts-backup && mv /etc/dipv6-tmp /etc/hosts  

[/sourcecode]
  * Al no tener avahi (multi-cast) instalado, no ejecutar esto:

[sourcecode language="html" gutter="true" wraplines="true"]  
root@chkry1 ~ # sed '/ipv6=yes/s/yes/no/' /etc/avahi/avahi-daemon.conf > /etc/avahi/dipv6-tmp; cp -a /etc/avahi/avahi-daemon.conf /etc/avahi/avahi-daemon.conf-backup && mv /etc/avahi/dipv6-tmp /etc/avahi/avahi-daemon.conf  

[/sourcecode]
  * NTP

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/default/ntp  

[/sourcecode]
.. debería quedar así "NTPD_OPTS='-4 -g'", luego reiniciar:

[sourcecode language="html" gutter="true" wraplines="false"]

# service ntp restart

[/sourcecode]
  * RPCBIND (rpc.statd, rpc.mountd, verificar tambien HTTP)

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/netconfig  

[/sourcecode]
dejarlo así:

[sourcecode language="html" gutter="true" wraplines="false"]  
udp tpi_clts v inet udp - -  
tcp tpi_cots_ord v inet tcp - -  

#udp6 tpi_clts v inet6 udp - -  

#tcp6 tpi_cots_ord v inet6 tcp - -  
rawip tpi_raw - inet - - -  
local tpi_cots_ord - loopback - - -  
unix tpi_cots_ord - loopback - - -  

[/sourcecode]
  * Quitar la interface eth0 de ipv6

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/network/interfaces  

[/sourcecode]
  * Rebootear y probar si ipv6 aún funciona

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # shutdown -rf now
root@chkry1 ~ # netstat -tunlp
Active Internet connections (only servers)  
Proto Recv-Q Send-Q Local Address Foreign Address State PID/Program name  
tcp 0 0 0.0.0.0:4141 0.0.0.0:* LISTEN 2340/sshd  
tcp6 0 0 :::4141 :::* LISTEN 2340/sshd  
udp 0 0 144.76.70.175:123 0.0.0.0:* 2277/ntpd  
udp 0 0 127.0.0.1:123 0.0.0.0:* 2277/ntpd  
udp 0 0 0.0.0.0:123 0.0.0.0:* 2277/ntpd  

[/sourcecode]
  * Si observa que SSH está en ipv6 corriendo en el puerto 4141 (mi nuevo puerto SSH), si no hay nada de ipv6, pues eso quiere decir que ipv6 ha sido desabilitado.

## 3\. Preparación del S.O. para la instalación de Proxmox VE
  * http://www.wepoca.net/node/42
* Crear una carpeta destinada a alojar los backups (iso, config files, etc.) de PVE (lo haremos desde la Consola Web de Proxmox)

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # mkdir -p /datos_pve_backups (lo configuraremos desde PVE Web)  

[/sourcecode]
  * Verificar que esté usando GUID Partition Table (no MS-DOS) ya que permite usar más de 2T de disco usando gdisk (fdisk es para otro tipo de FS como MS-DOS) o df -h
* http://wiki.hetzner.de/index.php/Partitionsgr%C3%B6%C3%9Fenlimit_bei_gro%C3%9Fen_Festplatten/en

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # gdisk -l /dev/sda  
GPT fdisk (gdisk) version 0.8.5
Partition table scan:  
MBR: protective  
BSD: not present  
APM: not present  
GPT: present
Found valid GPT with protective MBR; using GPT.  
Disk /dev/sda: 5860533168 sectors, 2.7 TiB  
Logical sector size: 512 bytes  
Disk identifier (GUID): B2B420AE-59BE-44E7-ADCE-EA6EB3EB3C63  
Partition table holds up to 128 entries  
First usable sector is 34, last usable sector is 5860533134  
Partitions will be aligned on 2048-sector boundaries  
Total free space is 2014 sectors (1007.0 KiB)
Number Start (sector) End (sector) Size Code Name  
1 4096 33558527 16.0 GiB 8200  
2 33558528 34607103 512.0 MiB 8300  
3 34607104 2182090751 1024.0 GiB 8300  
4 2182090752 5860533134 1.7 TiB 8300  
5 2048 4095 1024.0 KiB EF02  

[/sourcecode]
y el otro disco:

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # gdisk -l /dev/sdb  
GPT fdisk (gdisk) version 0.8.5
Partition table scan:  
MBR: protective  
BSD: not present  
APM: not present  
GPT: present
Found valid GPT with protective MBR; using GPT.  
Disk /dev/sdb: 5860533168 sectors, 2.7 TiB  
Logical sector size: 512 bytes  
Disk identifier (GUID): 4B205997-E657-433A-8DC7-28B356D3656C  
Partition table holds up to 128 entries  
First usable sector is 34, last usable sector is 5860533134  
Partitions will be aligned on 2048-sector boundaries  
Total free space is 2014 sectors (1007.0 KiB)
Number Start (sector) End (sector) Size Code Name  
1 4096 33558527 16.0 GiB FD00  
2 33558528 34607103 512.0 MiB FD00  
3 34607104 2182090751 1024.0 GiB FD00  
4 2182090752 5860533134 1.7 TiB FD00  
5 2048 4095 1024.0 KiB EF02  

[/sourcecode]
y mostrando el disco principal:

[sourcecode language="html" gutter="true" wraplines="false"]  
S.ficheros Tamaño Usados Disp Uso% Montado en  
rootfs 1016G 260G 705G 27% /  
udev 10M 0 10M 0% /dev  
tmpfs 3,2G 316K 3,2G 1% /run  
/dev/disk/by-uuid/d85c9428-b39f-434c-8140-3e46f0ffe770 1016G 260G 705G 27% /  
tmpfs 5,0M 0 5,0M 0% /run/lock  
tmpfs 9,5G 22M 9,5G 1% /run/shm  
/dev/sda2 508M 87M 396M 18% /boot  
/dev/sda4 1,7T 25G 1,6T 2% /home  
/dev/fuse 30M 16K 30M 1% /etc/pve  

[/sourcecode]
  * Configurar el idioma:

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # dpkg-reconfigure locales  

[/sourcecode]
  * Configurar la zona horaria:

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # dpkg-reconfigure tzdata  

[/sourcecode]
  * Para evitar warnings como este:

[sourcecode language="html" gutter="true" wraplines="false"]  
perl: warning: Setting locale failed.  
perl: warning: Please check that your locale settings:  
LANGUAGE = (unset),  
LC_ALL = (unset),  
LC_CTYPE = "UTF-8",  
LANG = "C"  
are supported and installed on your system.  

[/sourcecode]
.. hay que regenerar los locales:

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # locale-gen es_ES.UTF-8  

[/sourcecode]
... o comentar en '/etc/ssh/sshd_config' la siguiente línea 'AcceptEnv LANG LC_*'

# VI. Instalación del software de virtualización

## 4\. Instalación de Promox VE
  * Añadir a la sourcelist de apt lo sgte:

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # echo "deb http://download.proxmox.com/debian wheezy pve" >> /etc/apt/sources.list  
root@chkry1 ~ # wget -O- "http://download.proxmox.com/debian/key.asc" | apt-key add -  
root@chkry1 ~ # apt-get upgrade  
root@chkry1 ~ # aptitude upgrade  
root@chkry1 ~ # aptitude full-upgrade  
root@chkry1 ~ # aptitude install proxmox-ve-2.6.32

[...]  
The following packages have unmet dependencies:  
pve-firmware : Conflicts: firmware-linux-free but 3.2 is installed.  
Conflicts: firmware-realtek but 0.36+wheezy.1 is installed.  
The following actions will resolve these dependencies:
Remove the following packages:  
1) firmware-linux-free  
2) firmware-realtek
Leave the following dependencies unresolved:  
3) linux-image-3.2.0-4-amd64 recommends firmware-linux-free (>= 3~)
Accept this solution? [Y/n/q/?] Y  

[/sourcecode]
==>> Postfix configuración => NO CONFIGURATION  
==>> Finalmente se instalará un Apache con PVE web: https://your-server.com:8006
  * Verificar que no esten abiertos puertos de IPV6
Basta con comentar los protocolos en /etc/netconfig

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/netconfig  

[/sourcecode]

# VII. Post configuración

## 5\. Configuración de Redes virtuales con Promox VE

**1) Crear 2 redes virtuales de tipo Bridge desde la consola de Proxmox.**

  * Name: vmbr0
  * IP address: 10.10.10.1
  * Subnet mask: 255.255.255.0
  * Autostart: checked
* Name: vmbr1
* IP address: 192.168.0.1
* Subnet mask: 255.255.255.0
* Autostart: checked
Reiniciar para que los cambios sean aplicados.
  * vmbr0, representará mi DMZ y estará NATeada. Publicaremos servidores web, balanceadores, proxies, etc.
  * vmbr1, representaráuna red privada y aislada de internet, aquí ubicaremos DB servers, LDAP servers, etc.
  * También es posible hacerlo editando /etc/network/interfaces.
  * Verificar la existencia de 3 interfaces de red después de reiniciar.

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # ifconfig  
eth0 Link encap:Ethernet HWaddr 08:60:6e:68:14:cc  
inet addr:144.76.70.175 Bcast:144.76.70.191 Mask:255.255.255.255  
UP BROADCAST RUNNING MULTICAST MTU:1500 Metric:1  
RX packets:67937 errors:0 dropped:2098 overruns:0 frame:0  
TX packets:48468 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:1000  
RX bytes:23352396 (22.2 MiB) TX bytes:37412958 (35.6 MiB)  
Interrupt:43 Base address:0x2000
lo Link encap:Local Loopback  
inet addr:127.0.0.1 Mask:255.0.0.0  
UP LOOPBACK RUNNING MTU:16436 Metric:1  
RX packets:9141 errors:0 dropped:0 overruns:0 frame:0  
TX packets:9141 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:0  
RX bytes:6859893 (6.5 MiB) TX bytes:6859893 (6.5 MiB)
vmbr0 Link encap:Ethernet HWaddr da:ec:a5:7f:fa:59  
inet addr:10.10.10.1 Bcast:10.10.10.255 Mask:255.255.255.0  
UP BROADCAST RUNNING MULTICAST MTU:1500 Metric:1  
RX packets:6 errors:0 dropped:0 overruns:0 frame:0  
TX packets:0 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:0  
RX bytes:384 (384.0 B) TX bytes:0 (0.0 B)
vmbr1 Link encap:Ethernet HWaddr 52:3d:18:b6:29:0a  
inet addr:192.168.0.1 Bcast:192.168.0.255 Mask:255.255.255.0  
UP BROADCAST RUNNING MULTICAST MTU:1500 Metric:1  
RX packets:0 errors:0 dropped:0 overruns:0 frame:0  
TX packets:0 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:0  
RX bytes:0 (0.0 B) TX bytes:0 (0.0 B)  

[/sourcecode]
  * Por cada VM creada en cualquier subred virtual, aparecerá una nueva interfaz de red virtual:

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # ifconfig
tap100i0 Link encap:Ethernet HWaddr da:ec:a5:7f:fa:59  
UP BROADCAST RUNNING PROMISC MULTICAST MTU:1500 Metric:1  
RX packets:0 errors:0 dropped:0 overruns:0 frame:0  
TX packets:0 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:500  
RX bytes:0 (0.0 B) TX bytes:0 (0.0 B)  

[/sourcecode]

**2) Verificar y configurar Kernel**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/sysctl.conf

### Hetzner Online AG installimage

# sysctl config
net.ipv4.ip_forward=1  
net.ipv4.conf.all.rp_filter=1  
net.ipv4.icmp_echo_ignore_broadcasts=1  
net.ipv4.conf.all.proxy_arp=1  
net.ipv4.conf.default.proxy_arp=1  

[/sourcecode]

**3) Configurar hostname, hosts y DNS**

[sourcecode language="html" gutter="true" wraplines="false"]  
nano /etc/hostname  
nano /etc/hosts  
nano /etc/resolv.conf (también es posible modificarlos desde Proxmox)  

[/sourcecode]

## 6\. Seguridad y Firewall

**1) Instalar ShoreWall**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # apt-get install shorewall  

[/sourcecode]

**2) Configurar ShoreWall para auto-inicio**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/default/shorewall
Cambiar:  
startup = 0  
a:  
startup = 1
<strong>3) Continuar configurando ShoreWall</strong>

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/shorewall/shorewall.conf  

[/sourcecode]
Cambiar:  
IP_FORWARDING=Keep  
DISABLE_IPV6=No  
a:  
IP_FORWARDING=On  
DISABLE_IPV6=Yes

**4) Configurar las Zones en ShoreWall**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/shorewall/zones

# http://linux.die.net/man/5/shorewall-zones

#ZONE TYPE OPTIONS IN OUT

# OPTIONS OPTIONS
fw firewall  
net ipv4  
loc ipv4  
dmz ipv4  

[/sourcecode]

**5) Configurar las Interfaces en ShoreWall**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/shorewall/interfaces

# http://linux.die.net/man/5/shorewall-interfaces

#ZONE INTERFACE BROADCAST OPTIONS  
net eth0 detect logmartians,tcpflags,nosmurfs  
dmz vmbr0 detect logmartians,bridge,routefilter  
loc vmbr1 detect logmartians,bridge,routefilter  

[/sourcecode]

**6) Configurar Policy en ShoreWall**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/shorewall/policy

# http://linux.die.net/man/5/shorewall-policy

#SOURCE DEST POLICY LOG LEVEL LIMIT:BURST

# 1\. fw - loc
fw loc ACCEPT  
loc fw ACCEPT

# 2\. fw - dmz
fw dmz ACCEPT  
dmz fw DROP info

# 3\. fw - net
fw net ACCEPT  
net fw DROP info

# 4\. dmz - net
dmz net ACCEPT  
net dmz DROP info

# 5\. loc - dmz
loc dmz ACCEPT  
dmz loc DROP info

# 6\. loc - net
loc net ACCEPT  
net loc DROP info

# THE FOLLOWING POLICY MUST BE LAST
all all REJECT info  

[/sourcecode]

**7) Configurar las Rules en ShoreWall**

  * http://www.shorewall.net/three-interface.htm

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/shorewall/rules

# http://linux.die.net/man/5/shorewall-rules

#ACTION SOURCE DEST PROTO DEST SOURCE ORIGINAL RATE

# PORT PORT(S) DEST LIMIT

#

# Accept particular connections from Internet

#

# Permit access to SSH port 4646

##ACCEPT net fw tcp 22 - - 6/min:5  
ACCEPT net fw tcp 8080 - - 6/min:5  
ACCEPT net fw tcp 4646 - - 6/min:5

#

# Permit access to Proxmox Manager and Console
ACCEPT net fw tcp 443,5900:5999,8006

#

# PING Rules
Ping/ACCEPT all all

#

# Permit traffic to - certain - VMs in DMZ (via DNAT)
DNAT net dmz:$MY_PROXYWEB_IP tcp 80  
DNAT net dmz:$MY_PROXYWEB_IP tcp 443

#

# LAST LINE -- DO NOT REMOVE

[/sourcecode]

**8) Configurar NAT en ShoreWall**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/shorewall/masq

# implements SNAT on vmbr0

#INTERFACE SOURCE ADDRESS PROTO PORT(S) IPSEC MARK  
eth0 10.10.10.0/24  

[/sourcecode]

**9) Definir la variable o parámetro MY_PROXYWEB_IP**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # nano /etc/shorewall/params
MY_PROXYWEB_IP=10.10.10.11  

#LAST LINE -- DO NOT REMOVE  

[/sourcecode]

**10) Validar y habilitar la configuración de ShoreWall**

Es muy útil para probar antes de aplicarlo permanentemente.
  * Verificar la configuración

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # shorewall check  

[/sourcecode]
  * Habilitar la configuración de ShoreWall por 120 segundos

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # shorewall try /etc/shorewall 120s  

[/sourcecode]

**11) Si todo está OK, reiniciar el servidor o Shorewall**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # shorewall stop  
root@chkry1 ~ # shorewall start  
root@chkry1 ~ # shorewall restart  

[/sourcecode]

# VIII. Virtualizando con Proxmox VE

## 7\. Crear una primera VM (Proxy Web)
Previamente hay que descargar una ISO del S.O. de la VM que vamos a crear.

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chkry1 ~ # cd /var/lib/vz/template/iso/  
root@chkry1 /var/lib/vz/template/iso # wget http://ftp.cica.es/CentOS/6.4/isos/x86_64/CentOS-6.4-x86_64-minimal.iso  

[/sourcecode]

**1) Configurar Red de la VM**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chky-apache1 ~]# vi /etc/sysconfig/network-scripts/ifcfg-eth0
DEVICE="eth0"  
HWADDR=0a:98:a1:3b:9a:48  
TYPE=Ethernet  
ONBOOT=yes  
NM_CONTROLLED="yes"  
BOOTPROTO=none  
IPADDR=10.10.10.11  
PREFIX=24  
GATEWAY=10.10.10.1  
DNS1=213.133.99.99  
IPV6INIT=no  

[/sourcecode]

**2) Configure default gateway y hostname**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@soadev1 ~]# vi /etc/sysconfig/network
NETWORKING=yes  
HOSTNAME=chky-apache1  
GATEWAY=10.10.10.1  

[/sourcecode]

**3) En el GUEST verificar que la MACs esté asociada a la interface eth0**

por ejemplo:

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chky-apache1 ~]# nano /etc/udev/rules.d/70-persistent-net.rules

# PCI device 0x8086:0x100e (e1000) (custom name provided by external tool)
SUBSYSTEM=="net", ACTION=="add", DRIVERS=="? _" , ATTR{address}=="0a:98:a1:3b:9a:48", ATTR{type}=="1", KERNEL=="eth_", NAME="eth0"  

[/sourcecode]

**3) Reinicie las interfases de red o la VM**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chky-apache1 ~]# /etc/init.d/network restart  

[/sourcecode]

**4) Instalar Apache**

  * http://dev.antoinesolutions.com/apache-server

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chky-apache1 ~]# yum install httpd  

[/sourcecode]

**5) Set the apache service to start on boot**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chky-apache1 ~]# chkconfig --levels 235 httpd on  

[/sourcecode]

**6) Habilite name-based virtual hosting on port 80**

No es necesario para proxy web:
"Open the httpd configuration file located at /etc/httpd/conf/httpd.conf  
Un-comment the line containing the text NameVirtualHost *:80  
Save the file"

**7) Restart the Apache HTTP Server daemon**

[sourcecode language="html" gutter="true" wraplines="false"]  
root@chky-apache0 ~]# service httpd restart  

[/sourcecode]
  * Ignore the "NameVirtualHost *:80 has no VirtualHosts" warning for now.

**8) Añadir la regla a iptables para aceptar tráfico HTTP**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-apache1 ~]# nano /etc/sysconfig/iptables  

[/sourcecode]
...deberia quedar así:

[sourcecode language="html" gutter="true" wraplines="false"]

# Firewall configuration written by system-config-firewall

# Manual customization of this file is not recommended.
*filter  
:INPUT ACCEPT [0:0]  
:FORWARD ACCEPT [0:0]  
:OUTPUT ACCEPT [0:0]  
-A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT  
-A INPUT -p icmp -j ACCEPT  
-A INPUT -i lo -j ACCEPT  
-A INPUT -m state --state NEW -m tcp -p tcp --dport 22 -j ACCEPT  
-A INPUT -m state --state NEW -m tcp -p tcp --dport 80 -j ACCEPT  
-A INPUT -j REJECT --reject-with icmp-host-prohibited  
-A FORWARD -j REJECT --reject-with icmp-host-prohibited  
COMMIT  

[/sourcecode]

**9) Reiniciar iptables**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-apache1 ~]# /etc/init.d/iptables restart  

[/sourcecode]

**10) Ahora desde un navegador probar lo sgte:**

[sourcecode language="html" gutter="true" wraplines="false"]  
http://your-server.com  

[/sourcecode]

**11) Configura Apache HTTP como Proxy Web**

Finalmente, deberías configurar este Apache HTTP para que haga de Proxy Web a las otras VM que expongan una interfase HTTP

## 8\. Crear una segunda VM (Liferay Portal)
Esta segunda VM será un clone de la primera VM, es posible configurar la RAM, HD, NICs, etc. fácilmente desde la Consola Web de Proxmox.

**1) Instalar Jdk**

  * http://wiki.centos.org/HowTos/JavaOnCentOS

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-lfry1 ~]# yum list *java-1* | grep open
java-1.6.0-openjdk.x86_64 1:1.6.0.0-1.62.1.11.11.90.el6_4 updates  
java-1.6.0-openjdk-demo.x86_64 1:1.6.0.0-1.62.1.11.11.90.el6_4 updates  
java-1.6.0-openjdk-devel.x86_64 1:1.6.0.0-1.62.1.11.11.90.el6_4 updates  
java-1.6.0-openjdk-javadoc.x86_64 1:1.6.0.0-1.62.1.11.11.90.el6_4 updates  
java-1.6.0-openjdk-src.x86_64 1:1.6.0.0-1.62.1.11.11.90.el6_4 updates  
java-1.7.0-openjdk.x86_64 1:1.7.0.25-2.3.10.4.el6_4 updates  
java-1.7.0-openjdk-demo.x86_64 1:1.7.0.25-2.3.10.4.el6_4 updates  
java-1.7.0-openjdk-devel.x86_64 1:1.7.0.25-2.3.10.4.el6_4 updates  
java-1.7.0-openjdk-javadoc.noarch 1:1.7.0.25-2.3.10.4.el6_4 updates  
java-1.7.0-openjdk-src.x86_64 1:1.7.0.25-2.3.10.4.el6_4 updates  

[root@chk-lfry1 ~]# yum install java-1.6.0-openjdk.x86_64  

[/sourcecode]

**2) Verificar instalación de JDK**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chky-apache0 ~]# java -version  
java version "1.6.0_24"  
OpenJDK Runtime Environment (IcedTea6 1.11.11.90) (rhel-1.62.1.11.11.90.el6_4-x86_64)  
OpenJDK 64-Bit Server VM (build 20.0-b12, mixed mode)  

[/sourcecode]

**3) Instalar liferay 6.1.1ga2**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-lfry1 tempo-files]# wget http://sourceforge.net/projects/lportal/files/Liferay%20Portal/6.1.1%20GA2/liferay-portal-tomcat-6.1.1-ce-ga2-20120731132656558.zip  

[/sourcecode]

**4) Añadir la regla a iptables para aceptar tráfico HTTP en el puerto 8080, 8009**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-lfry1 ~]# nano /etc/sysconfig/iptables  

[/sourcecode]
...deberia quedar así:

[sourcecode language="html" gutter="true" wraplines="false"]

# Firewall configuration written by system-config-firewall

# Manual customization of this file is not recommended.
*filter  
:INPUT ACCEPT [0:0]  
:FORWARD ACCEPT [0:0]  
:OUTPUT ACCEPT [0:0]  
-A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT  
-A INPUT -p icmp -j ACCEPT  
-A INPUT -i lo -j ACCEPT  
-A INPUT -m state --state NEW -m tcp -p tcp --dport 22 -j ACCEPT  
-A INPUT -m state --state NEW -m tcp -p tcp --dport 8080 -j ACCEPT  
-A INPUT -m state --state NEW -m tcp -p tcp --dport 8009 -j ACCEPT  
-A INPUT -j REJECT --reject-with icmp-host-prohibited  
-A FORWARD -j REJECT --reject-with icmp-host-prohibited  
COMMIT  

[/sourcecode]

**4) Reiniciar IPtables**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-lfry1 ~]# /etc/init.d/iptables restart  

[/sourcecode]

## 9\. Crear una tercera VM (MySQL Server)
De igual forma que la segunda VM, esta tercera VM es, o podría ser, un clone de la primera VM, evidentemente con tamaño de HD diferente, RAM, CPUs/Cores, IP (privada y no en la DMZ), ... unas configuraciones fácilmente de hacerlas a través de la Consola Web de Proxmox.
  * http://dev.antoinesolutions.com/mysql

**1) Instalar MySQL**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-mysql1 ~]# yum install mysql-server mysql  

[/sourcecode]

**2) Hacer que inicie cuando bootee la VM**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-mysql1 ~]# chkconfig --levels 235 mysqld on  

[/sourcecode]

**3) Iniciar y acceder a MySQL**

[sourcecode language="html" gutter="true" wraplines="false"]  
service mysqld start  
mysql -u root  

[/sourcecode]
  * Defina un password para el usuario root para todos los dominios locales:

[sourcecode language="html" gutter="true" wraplines="false"]  
SET PASSWORD FOR 'root'@'localhost' = PASSWORD('piscosour!!');  
SET PASSWORD FOR 'root'@'localhost.localdomain' = PASSWORD('piscosour!!');  
SET PASSWORD FOR 'root'@'127.0.0.1' = PASSWORD('piscosour!!');  

[/sourcecode]
  * Borre el "any user":

[sourcecode language="html" gutter="true" wraplines="false"]  
DROP USER ''@'localhost';  
DROP USER ''@'localhost.localdomain';  

[/sourcecode]
  * Salga de MySQL:

[sourcecode language="html" gutter="true" wraplines="false"]  
exit  

[/sourcecode]

**4) Añadir la regla a IPtables para aceptar tráfico MySQL desde chk-lfry1**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-mysql1 ~]# nano /etc/sysconfig/iptables  

[/sourcecode]
...deberia quedar así:

[sourcecode language="html" gutter="true" wraplines="false"]

# Firewall configuration written by system-config-firewall

# Manual customization of this file is not recommended.
*filter  
:INPUT ACCEPT [0:0]  
:FORWARD ACCEPT [0:0]  
:OUTPUT ACCEPT [0:0]  
-A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT  
-A INPUT -p icmp -j ACCEPT  
-A INPUT -i lo -j ACCEPT  
-A INPUT -m state --state NEW -m tcp -p tcp --dport 22 -j ACCEPT  
-A INPUT -m state --state NEW -m tcp -p tcp --dport 3306 -j ACCEPT  
-A INPUT -j REJECT --reject-with icmp-host-prohibited  
-A FORWARD -j REJECT --reject-with icmp-host-prohibited  
COMMIT  

[/sourcecode]

**4) Reiniciar iptables**

[sourcecode language="html" gutter="true" wraplines="false"]  

[root@chk-mysql1 ~]# /etc/init.d/iptables restart  

[/sourcecode]

## 10\. Referencias
  * Disabilitar IPV6: http://forums.debian.net/viewtopic.php?f=16&t=85551
  * Disabilitar SSH sobre IPV6: http://ubuntu-tutorials.com/2008/01/12/disabling-ssh-connections-on-ipv6/
  * Instalar Proxmox en Hetzner EX4 Server: http://www.wepoca.net/node/42
  * Instalar Apache HTTP: http://dev.antoinesolutions.com/apache-server
  * Instalar MySQL: http://dev.antoinesolutions.com/mysql
