---
layout:     post
title:      'Gestión de Identidades y Accesos en las Organizaciones con WSO2 IS'
date:       2014-01-13 11:51:46
categories: ['BPM', 'CRM', 'ECM', 'ERP', 'Portal', 'Security', 'SOA']
tags:       ['Authentication', 'Authorization', 'IAM', 'LDAP', 'SSO', 'WSO2']
status:     publish 
permalink:  "/2014/01/13/iam-organizaciones-con-wso2is/"
---
# Introducción a IAM (Identity and Access Management)
La Gestión de Usuarios siempre es una funcionalidad requerida en las Aplicaciones Empresariales, el objetivo es crear, renovar, borrar, actualizar las credenciales con el cual un usuario puede acceder a la Aplicación y ejecutar o realizar ciertas acciones que quizás otros usuario no puedan hacer (gestión de privilegios).
Estas funcionalidades son siempre requeridas por cada Aplicación a menos que no exista tal restricción y uso sea completamente abierto. En el fondo, no sólo se trata de un única funcionalidad, se trata de un conjunto de funcionalidades:
1. Gestión del ciclo de vida de usuarios:
* Nos permitirá crear, borrar, bloquear, actualizar credenciales de usuarios.
* Definir un modelo jerárquico de usuario que plasme la jerarquía real de la organización, es decir, jefes, empleados, externos, etc.
* Gestión de privilegios.
1. Autenticación
* Proceso por el cual se validan las credenciales.
1. Autorización
* Proceso por el cual se da acceso a un recurso después del proceso de Autenticación.
Otras funcionalidades que suelen incluir en los IAM son:
1. Single Sign-On (SSO)
2.   
Single Sign-Off
3.   
Federated Identity
(*) Para mayor información remitirse a [Wikipedia - Identity Management](http://en.wikipedia.org/wiki/Identity_management "Wikipedia - Identity Management").

# _"Identity and Access Management"_ con WSO2 IS

Ya hemos hablado de [WSO2 IS y qué ofrece](http://holisticsecurity.wordpress.com/2013/11/28/starting-wso2-identity-server/ "STARTING WITH WSO2 IDENTITY SERVER \(SECURITY AS SERVICE\)"), aquí básicamente listaremos las estrategias de integración que seguiremos cuando queremos hacer autenticación o autorización con el ERP, CRM, ECM, etc.

[caption id="" align="aligncenter" width="396"][![Identity and Access Management with WSO2 IS and Penrose Virtual Directory]({{ site.baseurl }}/assets/1-wso2is-penrose-iam.png)](https://dl.dropboxusercontent.com/u/2961879/blog.sec/blog20131231_iam_wso2is/1-wso2is-penrose-iam.png) Identity and Access Management with WSO2 IS and Penrose Virtual Directory[/caption]

# Un completo Escenario con _"Identity and Access Management"_

Parto de un escenario hipotético que solemos encontrarnos en las Organizaciones, en dicho escenario trasladamos los productos y tecnologías que nos ayudarán a aplicar seguridad, principalmente WSO2 IS y [Penrose Virtual Directory](https://github.com/Chilcano/penrose-server "Penrose Virtual Directory").

[caption id="" align="aligncenter" width="726"][![A complete Enterprise Ecosystem - Business Applications and IAM]({{ site.baseurl }}/assets/2-soi-iam.png)](https://dl.dropboxusercontent.com/u/2961879/blog.sec/blog20131231_iam_wso2is/2-soi-iam.png) A complete Enterprise Ecosystem - Business Applications and IAM[/caption]

# _"Identity and Access Management"_ es Integración (SOI)

SOI es "Integración basado en Servicio", es un término muy usado en los modernos ESB cuando las interfases a conectar son ofrecidos como "servicios" y por lo general los conectores implementan SOAP y/o REST interfaces. Más información sobre SOA/SOI [aquí](http://en.wikipedia.org/wiki/Service-oriented_architecture_implementation_framework#Service-oriented_integration "SOA and SOI").
Como véis en el escenario anterior, añadir Autenticación y Autorización es hacer integración a través de la interfase existente basada en "servicio" de WSO2 IS. Es esto la realmente una de las funcionalidades más importantes de WSO2 IS, muy alineado a los principios SOA (desacoplamiento, multi-layer, etc.), además que nos permite integrar aplicaciones existente tan variadas gracias a que WSO2 IS expone las funcionalidades de IAM como API.

[caption id="" align="aligncenter" width="726"][![Spreading Security \(WSO2IS and Penrose\) in the Organization using SOI]({{ site.baseurl }}/assets/3-spreading-security-using-soi.png)](https://dl.dropboxusercontent.com/u/2961879/blog.sec/blog20131231_iam_wso2is/3-spreading-security-using-soi.png) Spreading Security (WSO2IS and Penrose) in the Organization using SOI[/caption]
Pues os dejo con el listado de las estrategias de integración usadas para cada caso:

[slideshare id=29953609&doc=chakray-enterprise-security-soi-portunoidm-140113053031-phpapp01]
Espero que os haya servido.
-Fin
