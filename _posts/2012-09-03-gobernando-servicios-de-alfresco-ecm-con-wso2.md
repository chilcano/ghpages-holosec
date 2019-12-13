---
layout:     post
title:      'Gobernando Servicios de Alfresco ECM con WSO2'
date:       2012-09-03 16:57:49
categories: ['ECM', 'SOA']
tags:       ['Alfresco', 'soapUI', 'WSDL', 'WSO2']
status:     publish 
permalink:  "/2012/09/03/gobernando-servicios-de-alfresco-ecm-con-wso2/"
---
El beneficio principal de hacer SOA sobre hacer Integración es la capacidad de gobierno que tenemos sobre los servicios, es decir, la capacidad de control que alcanzamos al aplicar los principios SOA.
En la integración o construcción de aplicaciones muchas veces tenemos que integrar o consumir servicios externos “no gobernados”, hacerlo sin control es siempre un riesgo, por ejemplo, si el contrato (WSDL) cambia, nuestra aplicación integrada se quedaría al margen de los cambios y sin soporte.

[caption id="" align="alignnone" width="497"][![Gobierno de los servicio de Alfresco con WSO2]({{ site.baseurl }}/assets/alfresco-wso2-logos.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-logos.png) Gobierno de los servicio de Alfresco con WSO2[/caption]
En esta post aprenderemos cómo registrar servicios externos en WSO2 Registry, concretamente aquellos servicios SOAP de Alfresco ECM, y luego consumirlos usando el mismo WSDL (contrato) desde nuestras aplicaciones de negocio usando WSO2 ESB.

**I. Estrategia:**

Para iniciar con el Gobierno SOA es necesario gestionar los servicios que vamos a consumir, eso implica registrar los servicios en WSO2 Registry, una vez realizado esto alcanzaríamos otros beneficios como: capacidad de medir, ofrecer otra interficie SOA, controlar la QoS, mejorar la Seguridad, gestionar versiones, encadenar servicios, etc.
Por estos motivos, WSO2 Registry es una herramienta fundamental para hacer Gobierno SOA y a partir de este sencillo principio SOA se abren infinitos escenarios de aplicación y de negocio.
En este post sólo registraremos un servicio de Alfresco ECM en WSO2 Registry, pero podría ser posible extender de manera rápida y fácil al resto de servicios SOAP, incluso al resto de servicios REST de Alfresco, además aprovechar la capacidad integradora de WSO2 ESB para “jugar” un poco con los servicios de Alfresco llegando incluso a brindar una nueva capa “gobernada” de servicios de gestión documental, es decir, un API completo de gestión documental.
 **II. Objetivos** :
1. Saber identificar Aplicación externa y servicios externos (endpoints, WSDLs,..) a usar (integrarlos) en nuestra aplicación interna.
2. Aprender a registrar servicios externos en WSO2 Registry.
3. Aprender a crear EndPoint interno en WSO2 ESB.
4. Aprender a crear Proxy para dicho servicio manteniendo mismo “contrato” y usando nuevo EndPoint desde WSO2 ESB.
5. Saber consumir el nuevo servicio recientemente registrado con WSO2 TryIt y con soapUI.
 **III. Requerimientos** :
Emplearemos WSO2 Stratos Live, para ello debemos crearenos una cuenta y nos asegurarmos de que WSO2 ESB y WSO2 Registry estén habilitados.
 **IV. Pasos** :

**1\. Identificar Aplicación externa y sus servicios externos (Endpoints, WSDLs,..)**

En este caso usaremos Alfresco ECM como aquella aplicación que queremos integrar a la nuestra interna pero consumiendo únicamente sus servicios. Alfresco brinda una serie de servicios de gestión documental, esto son:
* Servicios SOAP ad-hoc
* Servicios basados en CMIS

[caption id="" align="alignnone" width="394"][![WSDLs de Alfresco ECM]({{ site.baseurl }}/assets/alfresco-wso2-00.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-00.png) WSDLs de Alfresco ECM[/caption]
* Servicios REST ad-hoc

[caption id="" align="alignnone" width="416"][![Servicios REST de Alfresco ECM]({{ site.baseurl }}/assets/alfresco-wso2-01.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-01.png) Servicios REST de Alfresco ECM[/caption]

**2\. Desde WSO2 Registry, añadir servicios a partir de WSDLs.**

En este caso usaremos únicamente el servicio de autenticación de alfresco (SOAP), a modo de ejemplo, aunque dejamos también el servicio de Repositorio:
* <http://www.konosys.es:8014/alfresco/api/AuthenticationService?wsdl>
* <http://www.konosys.es:8014/alfresco/api/RepositoryService?wsdl>

[caption id="" align="alignnone" width="548"][![Registrando los WSDL en WSO2 Registry]({{ site.baseurl }}/assets/alfresco-wso2-02.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-02.png) Registrando los WSDL en WSO2 Registry[/caption]
En WSO2 Registry aparecerán 2 servicios registrados (uno por cada WSDL).

[caption id="" align="alignnone" width="565"][![Servicios registrados]({{ site.baseurl }}/assets/alfresco-wso2-03.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-03.png) Servicios registrados[/caption]

**3\. En WSO2 ESB, crear un nuevo “Address Endpoint” y guardarlo en el Governance Registry.**


[caption id="" align="alignnone" width="545"][![Creando EndPoints]({{ site.baseurl }}/assets/alfresco-wso2-04.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-04.png) Creando EndPoints[/caption]
Crear “Address Endpoint” con los sgtes datos:
    Nombre Endpoint: “kns.alfr.authn.ep0”
    Valor: “http://www.konosys.es:8014/alfresco/api/AuthenticationService” (verlo en el WSDL, como se muestra en la figura de abajo).
    Key: “gov:/ kns.alfr.authn.ep0”

[caption id="" align="alignnone" width="546"][![Obteniendo EndPoint desde el WSDL]({{ site.baseurl }}/assets/alfresco-wso2-05.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-05.png) Obteniendo EndPoint desde el WSDL[/caption]
Durante el proceso de creación del Endpoint hacer click en el botón “Save As”, te solicitará dónde guardar dicha entrada. Elegir Governance Registry con el nombre de la entrada por defecto (mismo nombre del endpoint).

[caption id="" align="alignnone" width="531"][![Asignando EndPoint a servicio]({{ site.baseurl }}/assets/alfresco-wso2-06.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-06.png) Asignando EndPoint a servicio[/caption]
Finalmente, si todo ha ido bien, se mostrarán los Dynamic Endpoints, verificar que nuestro endpoint esté en la lista.

[caption id="" align="alignnone" width="495"][![Dynamic EndPoint creado]({{ site.baseurl }}/assets/alfresco-wso2-07.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-07.png) Dynamic EndPoint creado[/caption]

**4\. Desde WSO2 ESB, crear Proxy para dicho servicio manteniendo mismo WSDL y usando nuevo Endpoint.**

    Nombre Proxy: “kns.alfr.authn.ep0”
    WSDL: seleccionarlo desde Governance Registry.
    Endpoint: seleccionarlo desde Governance Registry.

[caption id="" align="alignnone" width="577"][![WSO2 ESB - Creando un Custom Proxy]({{ site.baseurl }}/assets/alfresco-wso2-08.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-08.png) WSO2 ESB - Creando un Custom Proxy[/caption]
Luego, asignarle un nombre al proxy y seleccionar el WSDL alojado en el Registry.

[caption id="" align="alignnone" width="558"][![WSO2 ESB - Seleccionando el WSDL desde el registro]({{ site.baseurl }}/assets/alfresco-wso2-09.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-09.png) WSO2 ESB - Seleccionando el WSDL desde el registro[/caption]
Luego, hacer click sobre el botón “Next” para continuar con la configuración del Proxy. En la nueva pantalla tendremos la oportunidad de seleccionar el Endpoint que habiamos creado inicialmente.

[caption id="" align="alignnone" width="536"][![WSO2 ESB - Seleccionando el EndPoint desde el Registry]({{ site.baseurl }}/assets/alfresco-wso2-10.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-10.png) WSO2 ESB - Seleccionando el EndPoint desde el Registry[/caption]
Haga click en “Next” y en el último paso hacer click sobre “Finish”.

[caption id="" align="alignnone" width="536"][![WSO2 ESB - Finalizando la creación del Proxy]({{ site.baseurl }}/assets/alfresco-wso2-11.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-11.png) WSO2 ESB - Finalizando la creación del Proxy[/caption]
Si hemos llegado a este punto, ya habremos creado un Proxy con el que ya podemos invocar el servicio desde cualquier aplicación cliente SOAP. En este caso para probarlo usaremos el servicio TryIt de WSO2, para ello hacer click sobre “Try this service”.

[caption id="" align="alignnone" width="495"][![WSO2 ESB - Listo para probar el Proxy]({{ site.baseurl }}/assets/alfresco-wso2-12.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-12.png) WSO2 ESB - Listo para probar el Proxy[/caption]

**5\. Probando el Servicio con WSO2 TryIt**

WSO2 viene con una herramienta para probar servicios, se llama WSO2 TryIt. En este ejemplo usaremos esta herramienta, aunque también explicamos qué hay que hacer para emplear soapUI.

[caption id="" align="alignnone" width="598"][![WSO2 TryIt - Probando Servicios]({{ site.baseurl }}/assets/alfresco-wso2-13.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-13.png) WSO2 TryIt - Probando Servicios[/caption]

**6\. Probando el Servicio con soapUI.**

En este caso asegurarse de que estemos usando el Endpoint correcto, para ello podéis copiar el Endpoint que se muestra en WSO2 TryIt, en nuestro caso sólo son válidos las implementaciones de SOAP 1.2 y SOAP 1.1, estas son:
* <http://esb.stratoslive.wso2.com:8280/services/t/konosys.es/kns.alfr.proxy.authn0.kns.alfr.proxy.authn0HttpSoap12Endpoint>
* <http://esb.stratoslive.wso2.com:8280/services/t/konosys.es/kns.alfr.proxy.authn0.kns.alfr.proxy.authn0HttpSoap11Endpoint>

[caption id="" align="alignnone" width="581"][![Probando Servicio con soapUI]({{ site.baseurl }}/assets/alfresco-wso2-14.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-14.png) Probando Servicio con soapUI[/caption]

**V. Observaciones**
* Si se muestra este error, es probable que hayas configurado incorrectamente el Proxy. Para solucionarlo prueba revisando la configuración del Proxy, concretamente el Endpoint. Una forma rápida de probarlo sería reemplazar el Endpoint que se muestra en TryIt por el real (no pasamos por WSO2 ESB), concretamente usar: 
* <http://www.konosys.es/alfresco/api/AuthenticationService>

[caption id="" align="alignnone" width="617"][![WSO2 TryIt - Error]({{ site.baseurl }}/assets/alfresco-wso2-15.png)](https://dl.dropbox.com/u/2961879/blog20120903_gov_alfresco_services_with_wso2/alfresco-wso2-15.png) WSO2 TryIt - Error[/caption]
* Durante el desarrollo de este blog hicimos unos cambios en nuestra web de demos, reemplazamos el puerto 8014 por 80, asi que si algo no funciona, intenta cambiar los puertos.
**VI. Recursos usados** :
* WSO2 Stratos Live:  
<https://stratoslive.wso2.com/home/index.html>
* Alfresco Web Services:  
<http://wiki.alfresco.com/wiki/Alfresco_Content_Management_Web_Services>
* Alfresco Authentication y Repository Services WSDL:  
<http://www.konosys.es/alfresco/api/AuthenticationService?wsdl>  
<http://www.konosys.es/alfresco/api/RepositoryService?wsdl>
* Alfresco CMIS services:  
<http://www.konosys.es/alfresco/service/cmis/index.html>
* Alfresco REST services:  
<http://www.konosys.es/alfresco/service/>
Fin.
Espero que esto os haya servido.
