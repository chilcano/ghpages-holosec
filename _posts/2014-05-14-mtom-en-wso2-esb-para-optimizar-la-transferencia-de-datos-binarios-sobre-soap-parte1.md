---
layout:     post
title:      'MTOM en WSO2 ESB para optimizar la transferencia de datos binarios sobre SOAP (Parte 1/2)'
date:       2014-05-14 19:14:42
categories: ['Middleware', 'SOA']
tags:       ['Apache Axiom', 'Axis2', 'MTOM', 'SOAP', 'soapUI', 'SwA', 'WSO2 ESB']
status:     publish 
permalink:  "/2014/05/14/mtom-en-wso2-esb-para-optimizar-la-transferencia-de-datos-binarios-sobre-soap-parte1/"
---
Hace poco estaba explorando las características de WSO2 ESB para el envío óptimo de datos binarios sobre nuestros servicios implementados en SOAP y me encontré que WSO2 ESB implementa MTOM y SwA. Hice unas pruebas rápidas y pude comprobar que la transferencia de datos binarios, que por lo general son más grandes que datos de tipo textual, usando SOAP con MTOM es realmente muy potente y veloz, luego me puse a profundizar y aquí os muestro mis resultados.

[![wso2esb-soapui-mtom]({{ site.baseurl }}/assets/wso2esb-soapui-mtom.png?w=300)](http://holisticsecurity.files.wordpress.com/2014/05/wso2esb-soapui-mtom.png)

<!-- more -->

## I. Casos de Uso MTOM en WSO2 ESB.
* * *
WSO2 ESB viene con más de 102 ejemplos de Proxy de Apache Synapse, cada uno de ellos implementa diferentes casos de usos necesarios para integrar aplicaciones con diferencias estrategias.
El ejemplo "Sample 51" implementa MTOM y SwA sobre SOAP y esta entrada al blog usa esta implementación.

**Requisitos:**
* Sample 51: MTOM and SwA Optimizations and Request/Response Correlation (<https://docs.wso2.org/pages/viewpage.action?pageId=33136025>)
* SoapUI (<http://www.soapui.org>)
* WSO2 ESB 4.8.1 (<http://wso2.com/products/enterprise-service-bus>)

## II. Desplegar los servicios de back-end (Sample 51) en Axis2 server.
* * *
Para trabajar con los ejemplos de cualquier producto WSO2 es necesario ANT. Basta con añadir ANT a la variable del sistema PATH, en mi caso será:

[sourcecode language="html" gutter="true" wraplines="false"]  
PATH = %PATH%;C:\01bizlife\apache-ant-1.9.3\bin  

[/sourcecode]
WSO2 ESB viene con Axis2 server y con los servicios de back-end ya implementados. Para sólo es necesario compilarlos y desplegarlos en el Axis2 server. Para ello hacer lo siguiente:

[sourcecode language="html" gutter="true" wraplines="false"]  
C:\> cd <ESB_HOME>/samples/axis2Server/src/MTOMSwASampleService  

[/sourcecode]

[sourcecode language="html" gutter="true" wraplines="false"]  
C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService>ant
Buildfile: C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService\build.xml  
clean:  
init:  

[mkdir] Created dir: C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService\temp  

[mkdir] Created dir: C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService\temp\classes  

[mkdir] Created dir: C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\repository\services  
compile-all:  

[javac] C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService\build.xml:47: warning: 'includeantruntime' was not set, defaulting to build.sysclasspath=last; set to false for repeatable builds  

[javac] Compiling 1 source file to C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService\temp\classes  
build-service:  

[mkdir] Created dir: C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService\temp\MTOMSwASampleService  

[mkdir] Created dir: C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService\temp\MTOMSwASampleService\META-INF  

[copy] Copying 1 file to C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService\temp\MTOMSwASampleService\META-INF  

[copy] Copying 1 file to C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\src\MTOMSwASampleService\temp\MTOMSwASampleService  

[jar] Building jar: C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server\repository\services\MTOMSwASampleService.aar
BUILD SUCCESSFUL  
Total time: 1 second  

[/sourcecode]
Sólo comentar que "MTOMSwASampleService" implementa el servicio de back-end y tiene 3 operaciones:
* uploadFileUsingMTOM (in-out): Acepta una imagen en binario desde el request SOAP como MTOM y devuelve esta misma imagen como response SOAP.
* uploadFileUsingSwA (in-out): Acepta una imagen en binario desde el request SOAP como SwA y devuelve esta misma imagen as response SOAP.
* oneWayUploadUsingMTOM (in-only): Guarda el mensaje de request al disco.

## III. Iniciar Axis2 server.
* * *
Axis2 server usa HTTP listener en el puerto 9000 y el HTTPS 9002 por defecto.

[sourcecode language="html" gutter="true" wraplines="false"]  
C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Server>axis2server.bat 
"Starting Sample Axis2 Server ..."  
Using AXIS2_HOME: C:\01BIZL~1\WSO2ES~1.1-D\samples\AXIS2S~1\  
Using JAVA_HOME: C:\Program Files\Java\jdk1.7.0_51  
14/04/30 12:31:36 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Starting  

[SimpleAxisServer] Using the Axis2 Repository : C:\01BIZL~1\WSO2ES~1.1-D\samples\AXIS2S~1\repository  

[SimpleAxisServer] Using the Axis2 Configuration File : C:\01BIZL~1\WSO2ES~1.1-D\samples\AXIS2S~1\repository\conf\axis2.xml  
14/04/30 12:31:36 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/repository/modules/addressing.mar  
14/04/30 12:31:36 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/repository/modules/rampart.mar  
14/04/30 12:31:36 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/repository/modules/sandesha2.mar  
14/04/30 12:31:36 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.addressing_4.2.0.jar  
14/04/30 12:31:36 INFO deployment.ModuleDeployer: Deploying module: wso2caching - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.caching.core_4.2.0.jar  
14/04/30 12:31:36 INFO deployment.ModuleDeployer: Deploying module: ComponentMgtModule - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.feature.mgt.services_4.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: wso2mex - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.mex_4.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: pagination - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.registry.server_4.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: relay - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.relay.module_4.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.rm_4.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: POXSecurityModule - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.security.mgt_4.2.2.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: ServerAdminModule - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.server.admin_4.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: wso2statistics - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.statistics_4.2.2.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: wso2throttle - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.throttle.core_4.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: usagethrottling - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.throttling.agent_2.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: wso2tracer - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.tracer_4.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: metering - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.usage.agent_2.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: wso2xfer - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/org.wso2.carbon.xfer_4.2.0.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/rampart-core_1.6.1.wso2v12.jar  
14/04/30 12:31:37 INFO deployment.ModuleDeployer: Deploying module: rahas - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/./../../repository/components/plugins/rampart-trust_1.6.1.wso2v12.jar  
14/04/30 12:31:37 ERROR sandesha2.SandeshaModule: Could not load module policies. Using default values.  
14/04/30 12:31:37 INFO config.ClientConnFactoryBuilder: HTTPS Loading Identity Keystore from : ../../repository/resources/security/wso2carbon.jks  
14/04/30 12:31:37 INFO config.ClientConnFactoryBuilder: HTTPS Loading Trust Keystore from : ../../repository/resources/security/client-truststore.jks  
14/04/30 12:31:37 INFO nhttp.HttpCoreNIOSender: HTTPS Sender starting  
14/04/30 12:31:37 INFO nhttp.HttpCoreNIOSender: HTTP Sender starting  
14/04/30 12:31:37 INFO jms.JMSSender: JMS Sender started  
14/04/30 12:31:37 INFO jms.JMSSender: JMS Transport Sender initialized...  
14/04/30 12:31:37 INFO deployment.DeploymentEngine: Deploying Web service: MTOMSwASampleService.aar - file:/C:/01BIZL~1/WSO2ES~1.1-D/samples/AXIS2S~1/repository/services/MTOMSwASampleService.aar  
14/04/30 12:31:37 INFO nhttp.HttpCoreNIOListener: HTTPS Listener started on 0.0.0.0:9002  
14/04/30 12:31:37 INFO nhttp.HttpCoreNIOListener: HTTP Listener started on 0.0.0.0:9000  
14/04/30 12:31:37 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Started  

[/sourcecode]
Si es necesario abrir varias instancias de Axis2 server, hay que iniciarlos en puertos diferentes como se indica a continuación:
./axis2server.sh -http 9001 -https 9005 -name MyServer1  
./axis2server.sh -http 9002 -https 9006 -name MyServer2  
./axis2server.sh -http 9003 -https 9007 -name MyServer3

## IV. Cargar la configuración synapse de "Sample 51" en WSO2 ESB.
* * *
Es posible cargar "Sample 51" por defecto cuando WSO2 ESB inicie, esto hará que se cargue el fichero synapse, mientras que los proxies antes creados no se cargarán, al volver a iniciar WSO2 ESB de la forma normal dichos Proxies volverán a cargarse.
Iniciemos WSO2 ESB con `<ESB_HOME>\repository\samples\synapse_sample_51.xml` pre-cargado, de esta forma:

[sourcecode language="html" gutter="true" wraplines="false"]  
C:\01bizlife\wso2esb-4.8.1-dev\bin>wso2esb-samples.bat -sn 51
JAVA_HOME environment variable is set to C:\Program Files\Java\jdk1.7.0_51  
CARBON_HOME environment variable is set to C:\01BIZL~1\WSO2ES~1.1-D\bin\\..  

[2014-04-30 12:53:51,708] INFO - CarbonCoreActivator Starting WSO2 Carbon...  

[2014-04-30 12:53:51,708] INFO - CarbonCoreActivator Operating System : Windows XP 5.1, x86  

[2014-04-30 12:53:51,708] INFO - CarbonCoreActivator Java Home : C:\Program Files\Java\jdk1.7.0_51\jre  

[2014-04-30 12:53:51,708] INFO - CarbonCoreActivator Java Version : 1.7.0_51  

[2014-04-30 12:53:51,718] INFO - CarbonCoreActivator Java VM : Java HotSpot(TM) Client VM 24.51-b03,Oracle Corporation  

[...]  

[2014-04-30 12:54:08,162] INFO - PassThroughHttpSSLListener Pass-through HTTPS Listener started on 0.0.0.0:8246  

[2014-04-30 12:54:08,162] INFO - PassThroughHttpListener Starting Pass-through HTTP Listener...  

[2014-04-30 12:54:08,172] INFO - PassThroughHttpListener Pass-through HTTP Listener started on 0.0.0.0:8283  

[2014-04-30 12:54:08,172] INFO - JMSListener JMS listener started  

[2014-04-30 12:54:08,172] INFO - NioSelectorPool Using a shared selector for servlet write/read  

[2014-04-30 12:54:08,382] INFO - NioSelectorPool Using a shared selector for servlet write/read  

[2014-04-30 12:54:08,412] INFO - RegistryEventingServiceComponent Successfully Initialized Eventing on Registry  

[2014-04-30 12:54:08,462] INFO - JMXServerManager JMX Service URL : service:jmx:rmi://localhost:11114/jndi/rmi://localhost:10002/jmxrmi  

[2014-04-30 12:54:08,462] INFO - StartupFinalizerServiceComponent Server : WSO2 ESB (DEV)-4.8.1  

[2014-04-30 12:54:08,462] INFO - StartupFinalizerServiceComponent WSO2 Carbon started in 24 sec  

[2014-04-30 12:54:08,853] INFO - CarbonUIServiceComponent Mgt Console URL : https://10.0.2.15:9446/carbon/  

[/sourcecode]

## V. Ejecutar el ejemplo desde un cliente Axis2.
* * *
Para ejecutar este ejemplo debemos emplear un cliente Axis2 e invocar el Proxy correspondiente al "Sample 51".  
WSO2 ESB viene con cliente Axis2 implementado (OptimizeClient) para este ejemplo. Este cliente enviará una petición a WSO2 ESB en el puerto 8280, pero mi ESB tiene el offset puesto a 3 por lo que tendré que redirigir las peticiones del puerto 8280 al 8283, para ello usaré TCPMon, una herramienta muy útil para debugging que hará todo este trabajo, además podemos inspeccionar los mensajes SOAP de request y response, y modificarlos en tiempo real.
Entonces, iniciar TCPmon y crear un Listener en 8280 y reenviarlo a 8283:

[sourcecode language="html" gutter="true" wraplines="false"]  
C:\> cd <ESB_HOME>\bin  
C:\01bizlife\wso2esb-4.8.1-dev\bin>tcpmon.bat  

[/sourcecode]
Usar estos valores en TCPmon:
Listen Port #: 8280  
Target Hostname: 127.0.0.1  
Target Port #: 8283
Ahora, ejecutar el cliente Axis2. Seguir estos pasos para ejecutarlo:

[sourcecode language="html" gutter="true" wraplines="false"]  
C:\> cd <ESB_HOME>\samples\axis2Client  
C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Client>ant optimizeclient -Dopt_mode=mtom
Buildfile: C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Client\build.xml
init:  

[jar] Building jar: C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Client\pathing.jar
compile:
optimizeclient:  

[java] Sending file : ./../../repository/samples/resources/mtom/asf-logo.gif as MTOM  

[java] 14/04/30 13:57:33 INFO deployment.DeploymentEngine: No services directory was found under C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Client\client_repo.  

[java] 14/04/30 13:57:33 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/C:/01bizlife/wso2esb-4.8.1-dev/samples/axis2Client/client_repo/modules/addressing.mar  

[java] 14/04/30 13:57:33 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/C:/01bizlife/wso2esb-4.8.1-dev/samples/axis2Client/client_repo/modules/rampart.mar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/C:/01bizlife/wso2esb-4.8.1-dev/samples/axis2Client/client_repo/modules/sandesha2.mar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.addressing_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: wso2caching - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.caching.core_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: ComponentMgtModule - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.feature.mgt.services_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: wso2mex - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.mex_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: pagination - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.registry.server_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: relay - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.relay.module_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.rm_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: POXSecurityModule - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.security.mgt_4.2.2.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: ServerAdminModule - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.server.admin_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: wso2statistics - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.statistics_4.2.2.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: wso2throttle - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.throttle.core_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: usagethrottling - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.throttling.agent_2.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: wso2tracer - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.tracer_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: metering - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.usage.agent_2.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: wso2xfer - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/org.wso2.carbon.xfer_4.2.0.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/rampart-core_1.6.1.wso2v12.jar  

[java] 14/04/30 13:57:34 INFO deployment.ModuleDeployer: Deploying module: rahas - file:/C:/01bizlife/wso2esb-4.8.1-dev/repository/components/plugins/rampart-trust_1.6.1.wso2v12.jar  

[java] 14/04/30 13:57:34 ERROR sandesha2.SandeshaModule: Could not load module policies. Using default values.  

[java] 14/04/30 13:57:34 INFO mail.MailTransportSender: MAILTO Sender started  

[java] 14/04/30 13:57:34 INFO jms.JMSSender: JMS Sender started  

[java] 14/04/30 13:57:34 INFO jms.JMSSender: JMS Transport Sender initialized...  

[java] Saved response to file : C:\01bizlife\wso2esb-4.8.1-dev\samples\axis2Client\\.\\..\\..\tmp\sampleClient\mtom-7597224514139522188.gif
BUILD SUCCESSFUL  
Total time: 3 seconds  

[/sourcecode]
Según los logs, se ha podido enviar satisfactoriamente una imagen ubicada en `C:>1bizlife\wso2esb-4.8.1-dev\repository\samples\resources\mtom\asf-logo.gif`, además, la imagen recibida ha sido guardada en el lado servidor en esta ruta `C:>1bizlife\wso2esb-4.8.1-dev\tmp\sampleClient\mtom-7597224514139522188.gif`  

## VI. Revisando el request y response SOAP MTOM.
* * *
Gracias al uso de TCPmon hemos podido interceptar el request y el response, aunque sólo serán legibles las cabeceras SOAP mientras que parte de los payloads no ya que contienen información en formato binario.
**MTOM SOAP request message** :

[sourcecode language="html" gutter="true" wraplines="false"]  
POST /services/MTOMSwASampleService HTTP/1.1  
Content-Type: multipart/related; boundary="MIMEBoundary_6b43ce2cbb2f537bc614ca2667f80dd0906a861a70931f26"; type="application/xop+xml"; start="<0.1b43ce2cbb2f537bc614ca2667f80dd0906a861a70931f26@apache.org>"; start-info="text/xml"  
SOAPAction: "urn:uploadFileUsingMTOM"  
User-Agent: Axis2  
Host: 127.0.0.1:8280  
Transfer-Encoding: chunked
210e  
\--MIMEBoundary_6b43ce2cbb2f537bc614ca2667f80dd0906a861a70931f26  
Content-Type: application/xop+xml; charset=UTF-8; type="text/xml"  
Content-Transfer-Encoding: binary  
Content-ID: <0.1b43ce2cbb2f537bc614ca2667f80dd0906a861a70931f26@apache.org>
<?xml version="1.0" encoding="UTF-8"?>  
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">  
<soapenv:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">  
<wsa:To>http://localhost:8280/services/MTOMSwASampleService</wsa:To>  
<wsa:MessageID>urn:uuid:526d38f6-510c-4e9e-9a0b-93ff70c8d0eb</wsa:MessageID>  
<wsa:Action>urn:uploadFileUsingMTOM</wsa:Action>  
</soapenv:Header>  
<soapenv:Body>  
<m0:uploadFileUsingMTOM xmlns:m0="http://services.samples">  
<m0:request>  
<m0:image>  
<xop:Include xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:1.0b43ce2cbb2f537bc614ca2667f80dd0906a861a70931f26@apache.org">  
</xop:Include>  
</m0:image>  
</m0:request>  
</m0:uploadFileUsingMTOM>  
</soapenv:Body>  
</soapenv:Envelope>  
\--MIMEBoundary_6b43ce2cbb2f537bc614ca2667f80dd0906a861a70931f26  
Content-Type: application/octet-stream  
Content-Transfer-Encoding: binary  
Content-ID: <1.0b43ce2cbb2f537bc614ca2667f80dd0906a861a70931f26@apache.org>
GIF89a ...<<binary-content>>  
...  
\--MIMEBoundary_6b43ce2cbb2f537bc614ca2667f80dd0906a861a70931f26--
0  

[/sourcecode]

**MTOM SOAP response message:**


[sourcecode language="html" gutter="true" wraplines="false"]  
HTTP/1.1 200 OK  
messageType: multipart/related  
Content-Type: multipart/related; boundary="MIMEBoundary_1773747b8c9584bd1c67c2029eb620e46671585360cbb85a"; type="application/xop+xml"; start="<0.0773747b8c9584bd1c67c2029eb620e46671585360cbb85a@apache.org>"; start-info="text/xml"  
Date: Wed, 30 Apr 2014 11:57:35 GMT  
Server: WSO2-PassThrough-HTTP  
Transfer-Encoding: chunked
1ff4  
\--MIMEBoundary_1773747b8c9584bd1c67c2029eb620e46671585360cbb85a  
Content-Type: application/xop+xml; charset=UTF-8; type="text/xml"  
Content-Transfer-Encoding: binary  
Content-ID: <0.0773747b8c9584bd1c67c2029eb620e46671585360cbb85a@apache.org>
<?xml version="1.0" encoding="UTF-8"?><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"><soapenv:Body><m0:uploadFileUsingMTOMResponse xmlns:m0="http://services.samples"><m0:response><m0:image><xop:Include xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:1.3773747b8c9584bd1c67c2029eb620e46671585360cbb85a@apache.org"></xop:Include></m0:image></m0:response></m0:uploadFileUsingMTOMResponse></soapenv:Body></soapenv:Envelope>  
\--MIMEBoundary_1773747b8c9584bd1c67c2029eb620e46671585360cbb85a  
Content-Type: application/octet-stream  
Content-Transfer-Encoding: binary  
Content-ID: <1.3773747b8c9584bd1c67c2029eb620e46671585360cbb85a@apache.org>
GIF89a ...<<binary-content>>  
...  
\--MIMEBoundary_1773747b8c9584bd1c67c2029eb620e46671585360cbb85a--
0  

[/sourcecode]

## VII. Usando SoapUI para envio de mensajes MTOM SOAP directamente a Axis2 server.
* * *
Para poder usar un cliente SOAP como SoapUI en este ejemplo, necesitamos el endpoint que representa al servicio expuesto a través del Axis2 server, la acción/operación a invocar y un mensaje SOAP MTOM de ejemplo. En otras palabras, necesitamos el WSDL.  
Sabemos que los servicios de back-end están desplegados sobre Axis2 server, pues si accedemos a la web de Axis2 server nos mostrará los servicios expuestos y sus respectivos WSDL, si los tienen.
Las URLs de la web de Axis2 server son:  
* HTTP: http://localhost:9000/services  
* HTTPS: https://localhost:9002/services
En el caso que no exista WSDL o necesitemos exponer el mismo servicio usando otro contrato, pues podemos crear un Proxy nuevo de tipo Pass Through en WSO2 ESB a partir del fichero synapse `<ESB_HOME>\repository\samples\synapse_sample_51.xml`, en dicho Proxy nuevo debemos indicarle el nuevo WSDL que vamos a emplear.
Entonces, el WSDL para el back-end servicio MTOMSwASampleService estará en esta URL:

[sourcecode language="html" gutter="true" wraplines="false"]  
http://localhost:9000/services/MTOMSwASampleService?wsdl  

[/sourcecode]
Recordar que deberá existir 3 operaciones disponibles y que SoapUI deberá generar automáticamente 3 request:  
* oneWayUploadUsingMTOM  
* uploadFileUsingMTOM  
* uploadFileUsingSwA

[caption id="" align="aligncenter" width="551"][![MTOMSwASampleService operaciones]({{ site.baseurl }}/assets/wso2-esb-mtom-MTOMSwASampleService-soapui-01.png)](https://dl.dropboxusercontent.com/u/2961879/blog.sec/blog20140430_wso2_mtom/wso2-esb-mtom-MTOMSwASampleService-soapui-01.png) MTOMSwASampleService operaciones[/caption]
Desde SoapUI hacemos "request SOAP" para la operación uploadFileUsingMTOM indicando como endpoint a <http://localhost:9000/services/MTOMSwASampleService>, el mensaje SOAP es el siguiente:

[sourcecode language="html" gutter="true" wraplines="false"]  
<soapenv:Envelope  
xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"  
xmlns:ser="http://services.samples">  
<soapenv:Header/>  
<soapenv:Body>  
<ser:uploadFileUsingMTOM>  
<ser:request>  
<ser:image>cid:chilcano.jpg</ser:image>  
</ser:request>  
</ser:uploadFileUsingMTOM>  
</soapenv:Body>  
</soapenv:Envelope>  

[/sourcecode]
Debemos habilitar en SoapUI las "Request Properties" para MTOM:

[sourcecode language="html" gutter="true" wraplines="false"]  
Enable MTOM = true  

[/sourcecode]
Luego en la pestaña "Attachments" (ubicado al pie de la ventana Request en el SoapUI) anexar el fichero que deseamos enviar junto al mensaje SOAP.  
En este caso el fichero será "chilcano.jpg".
Ahora, ya estamos listos para lanzar la petición a la implementación del servicio "MTOMSwASampleService" alojado en Axis2 server.  
Al final, siguiendo el comportamiento de la acción/operación "uploadFileUsingMTOM", el fichero "chilcano.jpg" debe ser enviado por SoapUI y recibido por Axis2 server, el mismo fichero debe ser retornado como fichero anexado al response MTOM SOAP message, para verificarlo basta con comprobar si existe algún fichero anexado en la pestaña "Attachments" del Response.

[caption id="" align="alignnone" width="2354"][![Haciendo MTOM SOAP request a uploadFileUsingMTOM]({{ site.baseurl }}/assets/wso2-esb-mtom-MTOMSwASampleService-soapui-02-uploadFileUsingMTOM.png)](https://dl.dropboxusercontent.com/u/2961879/blog.sec/blog20140430_wso2_mtom/wso2-esb-mtom-MTOMSwASampleService-soapui-02-uploadFileUsingMTOM.png) Haciendo MTOM SOAP request a uploadFileUsingMTOM[/caption]

## VIII. Usando SoapUI para envio de mensajes MTOM SOAP a través de un Pass Through Proxy en WSO2 ESB.
* * *
En este caso implementaremos un Proxy, pero para fines demostrativos mantendremos el contrato original.  
Y según dicho contrato, el servicio tendrá 3 acciones/operaciones (oneWayUploadUsingMTOM, uploadFileUsingMTOM y uploadFileUsingSwA).
El Proxy que crearemos en WSO2 ESB será el siguiente y usará el WSDL expuesto desde Axis2:

[sourcecode language="html" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<proxy xmlns="http://ws.apache.org/ns/synapse"  
name="proxy_MTOMSwASampleService"  
transports="https,http"  
statistics="disable"  
trace="disable"  
startOnLoad="true">  
<target>  
<inSequence>  
<log level="custom">  
<property name="====== Proxy-MTOMSwASampleService" value="====== inSeq - ini"/>  
</log>  
<property name="POST_TO_URI" value="true" scope="default" type="STRING"/>  
<filter source="get-property('Action')" regex="urn:uploadFileUsingMTOM">  
<then>  
<header name="Action" scope="default" action="remove"/>  
<property name="SOAPAction" scope="default" action="remove"/>  
<property name="example" value="mtom"/>  
<property name="Action" value="urn:uploadFileUsingMTOM" scope="default"/>  
<header name="Action" scope="default" value="urn:uploadFileUsingMTOM"/>  
<send>  
<endpoint>  
<address uri="http://localhost:9001/services/MTOMSwASampleService"  
optimize="mtom"/>  
</endpoint>  
</send>  
</then>  
<else/>  
</filter>  
<filter source="get-property('Action')" regex="urn:uploadFileUsingSwA">  
<then>  
<property name="example" value="swa"/>  
<send>  
<endpoint>  
<address uri="http://localhost:9000/services/MTOMSwASampleService"  
optimize="swa"/>  
</endpoint>  
</send>  
</then>  
<else/>  
</filter>  
<filter source="get-property('Action')" regex="urn:oneWayUploadUsingMTOM">  
<then>  
<property name="OUT_ONLY" value="true"/>  
<property name="FORCE_SC_ACCEPTED" value="true" scope="axis2"/>  
<property name="example" value="onewaymtom"/>  
<send>  
<endpoint>  
<address uri="http://localhost:9001/services/MTOMSwASampleService"  
optimize="mtom"/>  
</endpoint>  
</send>  
</then>  
<else/>  
</filter>  
<log level="custom">  
<property name="Proxy MTOMSwASampleService" value="================ inSeq - fin"/>  
</log>  
</inSequence>  
<outSequence>  
<log level="custom">  
<property name="====== Proxy MTOMSwASampleService" value="====== outSeq - ini"/>  
</log>  
<filter source="get-property('example')" regex="onewaymtom">  
<property name="enableMTOM" value="true" scope="axis2"/>  
</filter>  
<filter source="get-property('example')" regex="mtom">  
<property name="enableMTOM" value="true" scope="axis2"/>  
</filter>  
<filter source="get-property('example')" regex="swa">  
<property name="enableSwA" value="true" scope="axis2"/>  
</filter>  
<send/>  
<log level="custom">  
<property name="====== Proxy MTOMSwASampleService" value="====== outSeq - fin"/>  
</log>  
</outSequence>  
</target>  
<publishWSDL uri="http://localhost:9000/services/MTOMSwASampleService?wsdl"/>  
<parameter name="enableMTOM">true</parameter>  
<description>MTOMSwASampleService (Sample 51)</description>  
</proxy>  

[/sourcecode]
En este caso, el nuevo WSDL estará servido desde el WSO2 ESB cuando despleguemos el Proxy, será la siguiente URL:

[sourcecode language="html" gutter="true" wraplines="false"]  
http://localhost:8283/services/proxy_MTOMSwASampleService?wsdl  

[/sourcecode]
La ventaja de usar un Pass Through Proxy es porder mediar (manipular, transformar, ruteo, etc.) el request/response, y también para un debugging más exacto empleando el mediador de LOG.
Si ahora creamos un nuevo proyecto con el Proxy, el proyecto sería el siguiente:

[caption id="" align="aligncenter" width="458"][![Proyecto SoapUI: Proxy MTOMSwASampleService]({{ site.baseurl }}/assets/wso2-esb-mtom-MTOMSwASampleService-soapui-03-proxy.png)](https://dl.dropboxusercontent.com/u/2961879/blog.sec/blog20140430_wso2_mtom/wso2-esb-mtom-MTOMSwASampleService-soapui-03-proxy.png) Proyecto SoapUI: Proxy MTOMSwASampleService[/caption]
Los request message para cada operación son los siguientes.
**Request message para oneWayUploadUsingMTOM** :

[sourcecode language="html" gutter="true" wraplines="false"]  
<soapenv:Envelope  
xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"  
xmlns:ser="http://services.samples">  
<soapenv:Header/>  
<soapenv:Body>  
<ser:oneWayUploadUsingMTOM>cid:chilcano.jpg</ser:oneWayUploadUsingMTOM>  
</soapenv:Body>  
</soapenv:Envelope>  

[/sourcecode]

**Request message SOAP para uploadFileUsingMTOM:**


[sourcecode language="html" gutter="true" wraplines="false"]  
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">  
<soapenv:Body>  
<m0:uploadFileUsingMTOMResponse xmlns:m0="http://services.samples">  
<m0:response>  
<m0:image>  
<xop:Include href="cid:1.c83f1f7b0058d225ca82b4f9a5875be65b9d20debd92abe8@apache.org" xmlns:xop="http://www.w3.org/2004/08/xop/include"/>  
</m0:image>  
</m0:response>  
</m0:uploadFileUsingMTOMResponse>  
</soapenv:Body>  
</soapenv:Envelope>  

[/sourcecode]

**El mensaje request SOAP para uploadFileUsingSwA usado es:**


[sourcecode language="html" gutter="true" wraplines="false"]  
<soapenv:Envelope  
xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"  
xmlns:ser="http://services.samples">  
<soapenv:Header/>  
<soapenv:Body>  
<ser:uploadFileUsingSwA>  
<ser:request>  
<ser:imageId>tux-mario.jpg</ser:imageId>  
</ser:request>  
</ser:uploadFileUsingSwA>  
</soapenv:Body>  
</soapenv:Envelope>  

[/sourcecode]
Y en el lado de Axis2 Server veremos las siguientes trazas:

[sourcecode language="html" gutter="true" wraplines="false"]  
$ ./axis2server.sh  
Using JAVA_HOME: /Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home  
Using AXIS2 Repository : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository  
Using AXIS2 Configuration : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/conf/axis2.xml  
14/05/12 15:46:47 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Starting  

[...]  
14/05/12 15:46:48 INFO nhttp.HttpCoreNIOListener: HTTPS Listener started on 0:0:0:0:0:0:0:0:9002  
14/05/12 15:46:48 INFO nhttp.HttpCoreNIOListener: HTTP Listener started on 0:0:0:0:0:0:0:0:9000  
14/05/12 15:46:48 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Started  

[...]  
Wrote to file : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/../../tmp/sampleServer/mtom-6291717480475442754.gif  
Wrote MTOM content to temp file : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/../../tmp/sampleServer/mtom-7105409971701841709.gif  
Wrote SwA attachment to temp file : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/../../tmp/sampleServer/swa-5320928313738320843.gif  

[/sourcecode]

## IX. Enviando BinaryData desde un Form HTML a servicio SOAP.
* * *
SOAP usa comúnmente HTTP como transporte, SOAP MTOM también usa lo mismo y el ejemplo "Sample 51" también. En lugar de usar SoapUI por qué no empleamos un formulario HTML para enviar el un fichero pero usando un formulario del tipo **`multipart/form-data`**?.  
Creo que sería la forma más natural para enviar ficheros, además sabiendo que la especificación HTTP desde inicios implementa el envío vía **`multipart/form-data`** , por qué no seguir haciéndolo?.
Dicho esto, vamos a implementar un formulario HTML para este propósito, luego, aprovechando que tenemos desplegado el "Sample 51" en WSO2 ESB y "MTOMSwASampleService" desplegado en Axis2 Server, vamos a crear un simple Form HTML con "mutipart/form-data", crearemos un nuevo Pass Through Proxy que recibirá la petición del Form HTML y reenviará una petición SOAP al Proxy creado al inicio de esta entrada pero con unas pequeñas modificaciones.
También será necesario modificar la implementación de "MTOMSwASampleService" para implementar una nueva operación que sepa decondificar la nueva petición efectuada desde un Form HTML.
El siguiente gráfico representa la situación final:
    [HTML Form]=>[ msg HTTP]=>[Proxy 01]=>[msg-soap]=>[Proxy 02]=>[msg SOAP]=>[  SOAP req  ]
                 |multipart|  [WSO2 ESB]  [ base64 ]  [WSO2 ESB]  | base64 |  [Axis2 Server]
                 [form-data]                                      [or MTOM ]  
Pues los proxies de WSO2 ESB nuevos y actualizados, el Form HTML, el servicio "MTOMSwASampleService" modificado **[los publicaré en un segundo post en este blog](http://holisticsecurity.wordpress.com/2014/05/14/mtom-en-wso2-e…re-soap-parte1)**.  
Asi que siga sintonizado :)

## X. Conclusiones.
* * *
* En un escenario SOA lo más probable es que todo sean servicios implementados en SOAP y si se requiere enviar de manera optimizada ficheros en formato binario de manera óptima y de una forma estándar, lo recomendable es usar MTOM sobre SOAP.
* Si estamos fuera de un proyecto SOA, podemos disponer de opciones como FTP, VFS o Formulario HTML con CGI en el lado servidor.
* Hay que destacar que el uso de MTOM sobre SOAP es un forma de envio de ficheros en binario de una manera óptima y eficiente, además el envío de ficheros grandes también sigue siendo de una manera óptima. Si estáis buscando el envío de grandes volúmenes de ficheros grandes, considerar MTOM sobre SOAP como una buena opción.

## XI. Referencias.
* * *
* WSO2 ESB: Sample 51: MTOM and SwA Optimizations and Request/Response Correlation   
<https://docs.wso2.org/pages/viewpage.action?pageId=33136025>
* SoapUI: Adding Headers and Attachments   
<http://www.soapui.org/SOAP-and-WSDL/adding-headers-and-attachments.html>
