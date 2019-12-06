---
layout: post
title: Autenticación NTLM en máquina virtuales Linux
date: 2009-11-13 14:01:39.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Linux
tags:
- cntlm
- NTLM
- proxy
- Virtual Box
meta:
  _edit_last: '578869'
  _wpas_skip_13849: '1'
  geo_public: '0'
  _wpas_skip_5053092: '1'
  _wpcom_is_markdown: '1'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2009/11/13/autenticacion-ntlm-en-maquina-virtuales-linux/"
---
Estoy creando muchas máquinas virtuales Linux con VirtualBox para mis demos y cursos, pero resulta que en nuestra organización tenemos como Proxy al MS ISA Proxy Server que requiere autenticación NTLM, pero parece que apt-get de mi Linux no sabe como hacer dicha autenticación.

  


Un colega [[^BgTA^]](http://blog.bgta.ne) usa [cntlm](http://cntlm.sourceforge.net) como primer proxy en lugar de usar el MS ISA Proxy logrando así actualizar su Linux sin problemas, además por otro factor muy importante, evitamos dejar en claro la contraseña del proxy ya que ahora con cntlm se configura con el hash.  
  
  
  
Pues listo, instalé cntlm en el S.O. Host (Windows XP) y en el Guest ([CrunchBang Linux](http://crunchbanglinux.org/)) configuré apt-get y Synaptic con el nuevo proxy basado en cntlm. Synaptic no tuvo problemas en la autenticación NTLM, o sea que no era necesario cntlm.

  


Para configurar el proxy para el apt-get hacer lo siguiente:

  


 **1\. Editar el siguiente fichero:**

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
$ sudo vi /et/bash.bashrc  
  
[/sourcecode]

  


 **2\. Añadir al final del fichero lo siguiente:**

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
export http_proxy=http://hostnamewinxp:port/  
  
export https_proxy=http://hostnamewinxp:port/  
  
export ftp_proxy=http://hostnamewinxp:port/  
  
export no_proxy=localhost,127.0.0.1  
  
[/sourcecode]

  


 **3\. Asegurarse de que los cambios se apliquen (por ejemplo, logout y login al Linux) y verificar con:**

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
$ echo $http_proxy  
  
[/sourcecode]

  


 **4\. probar el apt-get:**

  


[sourcecode language="text" gutter="true" wraplines="false"]  
  
$ sudo apt-get update  
  
[/sourcecode]

  


En fin, cntlm solucionó mi problema. Esto, en el mundillo de la seguridad es lo que se llama proxy-chaining, evidentemente, sólo hay 2 proxys y no son aleatorios y no garantizan anonimato.

  


Con cntlm también podemos permitir que otros PCs usen mi proxy, para evitar conexiones no deseas podemos filtrar la conexión en base a IPs, evidentemente no es muy seguro.

  


Bye.

  

