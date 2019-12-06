---
layout: post
title: Detener la sincronización de tiempo/fechas entre Host y Guest en Virtual Box
date: 2009-11-27 10:10:33.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Linux
tags:
- virtualbox
meta:
  _edit_last: '578869'
  _wpas_skip_13849: '1'
  _oembed_341dd1c7e9bdecfac2b63bc3695228dc: "{{unknown}}"
  _oembed_4a0cb52f5e242084b9ab7ec8fc2ed55e: "{{unknown}}"
  _oembed_89d487b1f6b02d10aaa375b8a6057b6c: "{{unknown}}"
  _oembed_e94f115cb62e3d647f0da6527b9a3bb9: "{{unknown}}"
  _oembed_a6bfdfafb590bc16793de774433c538c: "{{unknown}}"
  _oembed_e5eb140aa32ee41061eb32e591996d0d: "{{unknown}}"
  _oembed_1cf9c59d7d136d1a21a7ce58a3826112: "{{unknown}}"
  _oembed_6d5e7969628515544c4977a9595f31a5: "{{unknown}}"
  _oembed_1107578f523ed71bbf686b7dc27c497a: "{{unknown}}"
  _oembed_29a86cd67f3270ac8600018d6a75e73a: "{{unknown}}"
  geo_public: '0'
  _wpas_skip_5053092: '1'
  _wpcom_is_markdown: '1'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2009/11/27/detener-la-sincronizacion-de-tiempofechas-entre-host-y-guest-en-virtual-box/"
---
Desde la versión 3.0.6 de VirtualBox existe la posibilidad de sincronizar el tiempo entre el host y el guest, y si los "Guest Additions" están instalados es posible cambiar su comportamiento, inclusive detener el servicio. Si es linux es posible usar lo siguiente para detener el servicio de sincronización de tiempo.  
  
<!-- more -->  
  
Ejecutar lo siguiente desde el guest:

  
> /etc/init.d/vboxadd-timesync stop

  
Mientras que para guest Windows XP, debería ser:

  
> C:\WINDOWS\system32>VBoxService --disable-timesync

  
Sin embargo, esto parece que no funciona. Deteniendo o eliminando el servicio VBoxService en el guest tampoco funciona:

  
> C:\WINDOWS\system32>VBoxService -u

  
También probé lo siguiente desde el host, tampoco funcionó:

  
> VBoxManage setextradata <nombrevm> "VBoxInternal/Devices/VMMDev/0/Config/GetHostTimeDisabled" "1"

  
donde <nombrevm> es el nombre de la máquina virtual o guest.  
  
Parece que la sincronización entre el host y guest es necesaria, suena lógico. Finalmente, probé una recomendación del foro de VirtualBox, dice que existe una funcionalidad no documentada para hacer un offset de tiempos durante el booteo de la vm y parece que sí funciona. Desde el host hacer los siguiente:

  
> VBoxManage setextradata <nombrevm> "VBoxInternal/TM/UTCOffset" <nanoSeconds>

  
Para atrasar 1 día el tiempo del guest respecto al host, nanoSeconds = -86400000000000  
  
Para atrasar 365 (1 año) el tiempo, nanoSeconds = -31536000000000000

  
He probado hacer un offset y sí parece que funciona, tengo como Host a Windows XP, como Guest a otro Windows XP y en el Guest tengo los "Guest Additions" instalada.

  
Hasta el momento es la única alternativa que he encontrado que funciona ya que deshabilitar la sincronización de tiempos es imposible, al menos en este momento, aún no lo he probado en Linux como host.

  
Algunas referencias:

  
  

  * (2009.Nov.19) Disable Guest Clock Sync  
  
[http://forums.virtualbox.org/viewtopic.php?f=2&p=110547](http://forums.virtualbox.org/viewtopic.php?f=2&p=110547)
  

  * (2009.Jun.08) VirtualBox: Disable time sync between host and client  
  
<http://rickguyer.com/virtualbox-disable-time-sync-between-host-and-client/>
  

  * (2008.Ago.08) How to disable time sync between host and guest  
  
<http://forums.virtualbox.org/viewtopic.php?t=8535>
  

  * (2009.Nov.02) 3.0.10 can't change guest system time  
  
<http://forums.virtualbox.org/viewtopic.php?t=24057>
  

  
Bye.

  

