---
layout:     post
title:      'Detener la sincronización de tiempo/fechas entre Host y Guest en Virtual Box'
date:       2009-11-27 09:10:33
categories: ['Open Source']
tags:       ['Virtual Box']
status:     publish 
permalink:  "/2009/11/27/detener-la-sincronizacion-de-tiempofechas-entre-host-y-guest-en-virtual-box/"
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
