---
layout:     post
title:      'HTC Dream, jugando con Android'
date:       2009-08-11 06:48:23
categories: ['Misc']
tags:       ['ANDROID', 'FOSS', 'GOOGLE', 'JAVA']
status:     publish 
permalink:  "/2009/08/11/htc-dream-jugando-con-android/"
---
Por fin tengo un móvil ANDROID, después de unas horas de trasteo, lo he desbloqueado, le he actualizado la firmaware, la radio y ya estoy trabajando con el Android SDK desde Eclipse.

![Android HTM Dream]({{ site.baseurl }}/assets/htcdreamroger11.png)  
<!-- more -->  
Mi primera impresión es que como entorno de desarrollo, Google Android es equiparable a entornos de Microsoft, iPhone y Nokia.
Hay algunas cosas que mejorar, pero eso es cuestión de tiempo y cuestión de que los usuarios-técnicos conozcan esta plataforma.
Desde el punto de vista de usuario final, el teléfono/GPS/GPRS/3G/2G/WIFI/Bluetooth es muy completo. La batería se agota con un uso intensivo de WIFI, BT, 3G, esto es normal.
Por otro lado, he tenido que actualizar el S.O. de la versión 1.1 que Movistar tiene por defecto a la versión 1.5 (Cupcake) ya que sin esto no podría sincronizar Google App (mi dominio @acme) con el móvil (Contacts y Calendar).
De todas maneras, si el dispositivo no tiene la aplicación que necesitas, puedes descargarla gratis o comprarla (desde 1 a 10 euros) desde Android Market.
La abundancia de aplicaciones estriba en la facilidad para desarrollarlas (son aplicaciones Java), existe un SDK, emulador, debugger, acceso a Shell, acceso en modo root si lo necesitas, etc.
Actualmente estoy trabajando con cuestiones de firma digital (autenticación/validación/creación de firma de certificados X.509 desde Browser de Android), en ningún momento siento que estoy trabajando con el móvil. Ya os iré comentando de mis avances.
Os dejo con mi configuración y algunos links.

**Liberación de terminal:**

http://androidapps.es/2009/08/08/como-liberar-nuestros-terminales-android/

**S.O./ROM/Firmware:**
* AndroidApps v1.0 (Basada en la versión 3.6.5 de la [ROM CyanogenMod](http://forum.xda-developers.com/showthread.php?t=537204))
A su vez, CyanogenMod está basada en la oficial Android Cupcake (versión 1.5), se mejora la estabilidad, tiene agregado el teclado de HTC ([HTC_IME](http://androidapps.es/2009/06/28/instalar-teclado-htc-htc_ime/)), APNs (Las tres grandes estan, las OMVs sin confirmar) y otras apps: Teeter, Quickoffice, SpareParts, Superuser y Terminal Emulator.

**Radio:**

http://member.america.htc.com/download/RomCode/ADP/ota-radio-2_22_19_26I.zip

**Recovery Boot:**

http://android.smartphonefrance.info/download/recoveryBoot.rar
Agur!
