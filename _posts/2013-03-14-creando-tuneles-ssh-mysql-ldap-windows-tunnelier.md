---
layout: post
title: Creando túneles SSH a SSH Server, MySQL Server y a LDAP Server desde Windows
  y con Tunnelier
date: 2013-03-14 19:54:56.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Linux
- Security
tags:
- LDAP
- mysql
- port forwarding
- ssh
- tunnel
- windows
meta:
  publicize_reach: a:2:{s:7:"twitter";a:1:{i:13849;i:439;}s:2:"wp";a:1:{i:0;i:20;}}
  _edit_last: '578869'
  tagazine-media: a:7:{s:7:"primary";s:0:"";s:6:"images";a:0:{}s:6:"videos";a:0:{}s:11:"image_count";i:0;s:6:"author";s:6:"578869";s:7:"blog_id";s:7:"2005905";s:9:"mod_stamp";s:19:"2013-03-14
    18:54:56";}
  publicize_twitter_user: Chilcano
  _wpas_done_13849: '1'
  _publicize_done_external: a:1:{s:7:"twitter";a:1:{i:91440493;b:1;}}
  twitter_cards_summary_img_size: a:6:{i:0;i:819;i:1;i:460;i:2;i:3;i:3;s:24:"width="819"
    height="460"";s:4:"bits";i:8;s:4:"mime";s:9:"image/png";}
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2013/03/14/creando-tuneles-ssh-mysql-ldap-windows-tunnelier/"
---
He tenido que administrar varios servidores virtuales (Proxmox) que exponen diferentes servicios como SSH server, MySQL server y LDAP server, pero al que no podemos acceder directamente, lo tuve que hacer accediendo remotamente desde un cliente SSH y luego haciendo el "salto" hacia el servidor deseado. Esto es lo que se llama "tunel ssh". Evidentemente, es un proceso muy lento sobretodo cuando necesitamos operar dichos servicios de manera rápida, crear un usuario, ver logs, clonar una VM, desplegar nuevos servicios, en fin. Esta situación se complica aún más si desde el servidor que nos sirve de puente tiene un Firewall que cierra o filtra muchos de los puertos, como es natural en un entorno productivo.

  
En fin, la herramienta más usada para hacer esto en Windows es Putty, además de ser un cliente SSH, permite crear túneles (port forwarding) de manera fácil.

  
Quizás lo más difícil es entender cómo se crean los túneles, aquí lo explicaremos y además emplearemos [Tunnelier](http://www.bitvise.com/tunnelier "Tunnelier"), otro cliente SSH de Btivise que además tiene unas características mejoradas en comparación a Putty.

  
 **1\. Escenario donde crearemos túneles SSH**

  
[caption id="" align="alignnone" width="491"][![Escenario - Creando 3 túneles SSH hacia SSH server, MySQL server y LDAP server]({{ site.baseurl }}/assets/env-tunnel-ssh-1.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/env-tunnel-ssh-1.png) Escenario - Creando 3 túneles SSH hacia SSH server, MySQL server y LDAP server[/caption]

  
  

  *  **kns** : representa al servidor SSH público que expone el servicio en el puerto **2121**. También podéis usar el FQDN o la IP.  
  

  

  *  **kns-lfry0, kns-db1, kns-ldap0** : representan los Hostnames o IPs privados o internos. Sólo pueden ser resueltos desde **kns**.
  

  
 **2\. Creando túnel hacia SSH server**

  
Una vez descargado e instalado Tunnelier, configurémoslo de esta forma.

  
  

  * En la pestaña " **C2S** " crear un tunel hacia el servidor **kns-lfry0:22** , tal como se indica en la figura.
  

  
[caption id="" align="alignnone" width="452"][![Creando tunel SSH]({{ site.baseurl }}/assets/env-tunnel-ssh-2.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/env-tunnel-ssh-2.png) Creando tunel SSH[/caption]

  
  

  * En la pestaña " **Options** ", bajo " **On Login** " deshabilitar " **Open Termina** l" y " **Open SFTP** " (opcional, esto hace que no se abran consolas ssh ni el cliente SFTP).
  

  * En la pestaña **Login** introducir las credenciales para hacer SSH a **kns** bajo el puerto **2121** , tal como se muestra en la figura.
  

  
[caption id="" align="alignnone" width="452"][![Creando tunel SSH - haciendo Login]({{ site.baseurl }}/assets/env-tunnel-ssh-3.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/env-tunnel-ssh-3.png) Creando tunel SSH - haciendo Login[/caption]

  
  

  * Luego hacemos " **Login** ", con ello nos traemos el puerto **22** remoto al puerto **2122** del **localhost**.
  

  
En estos momentos ya hemos creado el túnel, ahora como estamos haciendo "port forwarding" hacia **kns-lfry0** que tiene abierto el puerto **22** del SSH server, entonces podemos usar cualquier cliente SSH para conectarnos a dicho servidor remoto como si fuese un servicio local, pero en este caso debemos conectarnos a **localhost** en el puerto **2122**. Podemos usar Putty o abrir nuevamente el programa Tunnelier (no añadir nuevos túneles, sólo usarlo como cliente SSH para conectarnos a **localhost:2122** ).

  
[caption id="" align="alignnone" width="452"][![Abriendo 2 sesiones de Tunnelier, el primero para crear el tunel y el segundo para hacer la conexión SSH a kns-lfry0:22 \(localhost:2122\)]({{ site.baseurl }}/assets/env-tunnel-ssh-4.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/env-tunnel-ssh-4.png) Abriendo 2 sesiones de Tunnelier, el primero para crear el tunel y el segundo para hacer la conexión SSH a kns-lfry0:22 (localhost:2122)[/caption]

  
 **3\. Creando túnel hacia MySQL server**

  
Este escenario es algo más difícil que el anterior, pero se resuelve haciendo un segundo túnel sobre el anterior, esto hace que cuando nos conectemos con un cliente MySQL, MySQL server no nos deniegue la conexión. Esto se debe a que por defecto MySQL deniega la conexión si lo hacemos desde un Host diferente al localhost. Al hacer el segundo túnel, nos conectamos directamente al MySQL server y establecemos la conexión desde el mismo host donde está instalado MySQL server.

  
 **Primer túnel ssh-mysql:**

  
[caption id="" align="alignnone" width="452"][![Primer túnel SSH-MySQL \(1/2\)]({{ site.baseurl }}/assets/tunnel-mysql-1-ssh-01-.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/tunnel-mysql-1-ssh-01-.png) Primer túnel SSH-MySQL (1/2)[/caption]

  
[caption id="" align="alignnone" width="452"][![Primer túnel SSH-MySQL \(2/2\)]({{ site.baseurl }}/assets/tunnel-mysql-1-ssh-02-.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/tunnel-mysql-1-ssh-02-.png) Primer túnel SSH-MySQL (2/2)[/caption]

  
Una vez configurado el primer túnel, hacer click en Login.

  
 **Segundo túnel SSH-MySQL:**

[caption id="" align="alignnone" width="452"][![Segundo túnel SSH-MySQL \(1/2\)]({{ site.baseurl }}/assets/tunnel-mysql-2-01-.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/tunnel-mysql-2-01-.png) Segundo túnel SSH-MySQL (1/2)[/caption]

[caption id="" align="alignnone" width="452"][![Segundo túnel SSH-MySQL \(2/2\)]({{ site.baseurl }}/assets/tunnel-mysql-2-02-.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/tunnel-mysql-2-02-.png) Segundo túnel SSH-MySQL (2/2)[/caption]

Una vez configurado el segundo túnel, hacer click en Login.

Ahora, usar cualquier cliente MySQL para conectarse al MySQL server, yo suelo usar [HeidiSQL](http://www.heidisql.com/download.php "HeidiSQL"). Configurarlo de esta forma:

[caption id="" align="alignnone" width="371"][![Usando HeidiSQL para conectarse a MySQL a través de un túnel SSH]({{ site.baseurl }}/assets/tunnel-mysql-3-cnx.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/tunnel-mysql-3-cnx.png) Usando HeidiSQL para conectarse a MySQL a través de un túnel SSH[/caption]

**4\. Creando túnel hacia LDAP server**

Este escenario es similar al primer escenario donde nos conectábamos a un segundo SSH server llamado " **kns-lfry0** ", puerto " **22** ". Ahora nos conectaremos a " **kns-ldap0** ", puerto " **389** ", según la primera figura mostrada líneas arriba.

La diferencia estriba en que en el primer escenario usábamos un cliente SSH (Putty o Tunnerlier) para conectarnos al servidor, mientras que en este nuevo escenario usaremos un cliente LDAP ([LDAP Browser Editor](http://www.novell.com/communities/node/8652/gawors-excellent-ldap-browsereditor-v282 "LDAP Browser Editor")).

Otra pequeña diferencia con respecto a los anteriores escenarios es que usaremos un sólo Tunnelier para configurar todos los primeros túneles, aunque sólo vayamos a usar el relacionado a **kns-ldap0:389**. Esto es sólo por comodidad.

Túnel SSH-LDAP

[caption id="" align="alignnone" width="452"][![Túnel SSH-LDAP \(1/2\)]({{ site.baseurl }}/assets/tunnel-ldap-01.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/tunnel-ldap-01.png) Túnel SSH-LDAP (1/2)[/caption]

[caption id="" align="alignnone" width="452"][![Túnel SSH-LDAP \(2/2\)]({{ site.baseurl }}/assets/tunnel-ldap-02.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/tunnel-ldap-02.png) Túnel SSH-LDAP (2/2)[/caption]

Una vez configurado el túnel, usando LDAP Browser Editor crear una conexión hacia el túnel LDAP, es decir, **localhost:2124**.

[caption id="" align="alignnone" width="531"][![LDAP Browser Editor - conectándonos al túnel SSH-LDAP]({{ site.baseurl }}/assets/tunnel-ldap-03-cnx.png)](https://dl.dropbox.com/u/2961879/blog20130314_ssh_tunnel_win/tunnel-ldap-03-cnx.png) LDAP Browser Editor - conectándonos al túnel SSH-LDAP[/caption]

**5\. Conclusiones**

  * El servicio de SSH abre un el puerto **22** por defecto, muchos ports scanners o durante el proceso de pen-testing, el puerto 22 es uno de los más atacados. Lo recomendable es cambiar el puerto, no permitir conectarse usando usuario ROOT y aplicar alguna técnica de ocultamiento de servicio como [Port-Knocking](http://en.wikipedia.org/wiki/Port_knocking "Port Knocking"). Crear túneles en este escenario no cambia nada, únicamente hay que conocer el nuevo puerto del SSH server.
  * Algunos servicios como SSH, MySQL, etc. permite incorporar mecanismos extras de seguridad basadas en IP/Hostname desde donde te conectas. Además de restringir desde dónde se establece la conexión usando Firewalling con iptables. Pues, lo recomiendo usar. En este post, MySQL tiene ese comportamiento y para acceder a él desde un cliente MySQL nos obliga crear dos túneles.
  * Es importante que el servidor SSH desde donde hacemos el salto tenga visibilidad al resto de servidores pero desde la interfaz de red interna.
  * Si quieres crear túneles desde Linux, lo puedes hacer desde línea de comando, ya que por defecto Linux tiene cliente y servidor SSH instalado o también puedes usar [gSTM (Gnome SSH Tunnel Manager)](http://sourceforge.net/projects/gstm/ "Gnome SSH Tunnel Manager"), es un front-end para gestionar tus túneles.
  * Y para Mac OSX, de manera similar a Linux, puedes hacer uso del cliente SSH de línea de comandos, aunque habrá que leer la ayuda del cliente ya que algunos parámetros cambian. Por otro lado, también puedes usar cualquier aplicación/front-end que te facilite crear uno o muchos túneles. Yo solía usar [STM (SSH Tunnel Manager)](http://projects.tynsoe.org/en/stm/ "SSH Tunnel Manager para Mac OSX").

End.
