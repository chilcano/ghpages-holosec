---
layout:     post
title:      'Message Brokering y recolección de datos para Big Data con WSO2'
date:       2014-03-07 17:35:56
categories: ['Big Data', 'Broker', 'IoT', 'SOA']
tags:       ['AMQP', 'Apache Cassandra', 'QPid', 'WSO2 ESB', 'WSO2 MB']
status:     publish 
permalink:  "/2014/03/07/message-brokering-y-recoleccion-datos-big-data-wso2/"
---
Message Brokering es el conjunto de mecanismos por el cual se gestiona la recepción y la entrega de mensajes entre sistemas distribuidos. El propósito final con los Message Broker Systems son:
  *  **Desacoplar** : En tiempo, en espacio y a nivel de sincronización.
  *  **Confiabilidad** : A nivel de transacción y de persistencia.

## 1\. Casos de uso y beneficios
Los beneficios son palpables, como por ejemplo:
  *  **Comunicación Síncrona/Asíncrona/Colas** : Hacer una solicitud (enviar un mensaje) y que te respondan después de cierto tiempo.
  *  **Persistencia de mensajes** : Hacer una solicitud y que la petición siga viva a pesar de que los sistemas no hayan estado operativos después de haber realizado la solicitud, almacenar de manera persistente los mensajes de las solicitudes excedentes efectuadas a un servicio o simplemente, almacenar con gran rapidez y garantía los mensajes de las solicitudes realizadas.
  *  **Publicación/Suscripción** : Publicar un mensaje de cierto tópico y que sólo ciertos clientes reciban el mensaje.
Hay diferentes casos de uso o escenarios donde son necesarios los Message Brokers, pero los más representativos están definidos en los Enterprise Integration Patterns, estos son:
  *  **Message Store EIP** (http://www.eaipatterns.com/MessageStore.html): Que serviría para hacer dar persistencia a los mensajes (Reliability), Colas (Queues), etc.

[caption id="" align="alignnone" width="700"]![Message Broker - Colas y Persistencia de mensajes]({{ site.baseurl }}/assets/chakray-message-broker-queues-store.png) Message Broker - Colas y Persistencia de mensajes[/caption]
  *  **Publish-Subscribe Channel EIP** (http://www.eaipatterns.com/PublishSubscribeChannel.html): Que serviría para hacer Monitoring, Tracking, Broadcasting, etc.

[caption id="" align="alignnone" width="751"]![Message Broker - Publicación y Suscripción]({{ site.baseurl }}/assets/chakray-message-broker-pubsub-store.png) Message Broker - Publicación y Suscripción[/caption]

## 2\. Message Brokers en el mundo real: Big Data
Dependiendo del tipo de mensaje y del tipo de clientes existen muchos tipos de Message Brokers, algunos por ejemplo:
  * Sensores (clientes MB)
  * Monitorización (cliente suscriptor MB)
  * Vigilancia (cliente suscriptor MB)
  * Business Activity Monitoring (BAM)
  * Scheduling Systems
  * Redes Sociales
Hay que destacar que su uso ahora es más frecuente en sectores inimaginables, sobretodo en los _Sistemas de Información Críticos_ , donde fallar no es una opción, son escenarios muy diferentes al tradicional, en otras palabras, son escenarios relacionados a _Internet of Things_ y comunicación _Machine-to-Machine_ que con mucha innovación promueve el uso de este tipo de Message Brokers y por consecuencia la re-definición de protocolos y especificaciones, esto se da por los mismos beneficios que arriba indicábamos.
Entre los productos free/open source más usados tenemos:
  1. WSO2 Message Broker: basado en Apache Qpid (http://qpid.apache.org) e implementa el "Advanced Message Queueing Protocol" (AMQP - http://www.amqp.org) y otros estándares relacionados como JMS y WS-Eventing. Es rápido, potente y muy ligero, la persistencia de mensaje se logra con el uso de Apache Cassandra y en combinación con Apache Zookeper nos permite coordinar colas distribuidas. Los complementos perfectos son WSO2 ESB (mediation), WSO2 BAM (streams) y WSO2 CEP (eventing).
  2. Storm MQ (http://stormmq.com): implementa también AMQP, provee un Message Broker y Message Store en la nube accesible a través de un API.
  3. ActiveMQ (http://activemq.apache.org): proyecto de Apache, implementa muchos Enterprise Integration Patterns (EIP) a través de Apache Camel (http://camel.apache.org). Implementa una variedad de protocolos como JMS y MQTT (machine-to-machine / "Internet of Things" connectivity binary protocol - http://mqtt.org).
  4. Rabbit MQ (http://www.rabbitmq.com): a diferencia de los anteriores implementa muchos protocolos a nivel de transporte y mensaje, entre ellos AMQP, STOMP (text-based messaging protocol emphasising simplicity - http://stomp.github.io), MQTT y HTTP.
  5. Mosquitto (http://mosquitto.org): Message Broker para el protocolo MQTT muy potente y veloz, muy usado en IoT / M2M.
  6. Paho (http://eclipse.org/paho): altamente escalable, optimizado para equipos de reducida potencia y para redes muy restringidas, junto a Mosquitto son los brokers más usados en IoT / M2M, ambos poseen muchos clientes desde C++ a JavaScript, como tal implementa también el protocolo MQTT.
  7. ActiveMQ Apollo (http://activemq.apache.org/apollo): es la nueva generación de Apache ActiveMQ que soporte protocolos como STOMP, AMQP, MQTT, Openwire, SSL y WebSockets.

## 3\. Escalando los Message Broker Systems
Para escalar un Broker debemos considerar 3 dimensiones:
  * Número de mensajes soportados
  * Número de colas soportadas
  * Tamaño de los mensajes
Considerando estas dimensiones, podríamos indicar que nuestro Broker es potencialmente escalable, aunque su implementación, sobretodo para Colas Distribuidas, se convierte en un reto.
Más información aquí:
  * Scalable Persistent Message Brokering with WSO2 Message Broker by Srinath Perera: <http://www.slideshare.net/hemapani/scalable-persistent-message-brokering-with-wso2-message-broker>

## 4\. Implementando Message Store EIP con WSO2
En mi opinión, el uso de Message Broker para controlar la persistencia de mensajes es vital en Sistemas de Información Críticos. ¿De qué sirve si podemos recibir y/o entregar mensajes de manera muy rápida, si por algún problema de conectividad algunos mensajes se perderían?. Como indiqué líneas arriba, esto no es admitible en Sistemas de Información Críticos, por lo que lo el uso de Message Broker y sus características como la persistencia de mensajes es vital.
En fin, a continuación implementaremos Message Store EIP con WSO2.

[caption id="" align="alignnone" width="643"][![Enterprise Integration Pattern - Message Store]({{ site.baseurl }}/assets/chakray-message-broker-eip-messagestore.gif)](http://www.eaipatterns.com/MessageStore.html) Enterprise Integration Pattern - Message Store[/caption]

## 5\. Arquitectura de Referencia para Message Brokering y estrategias de integración
Existen dos posibilidades de implementar Message Store EIP con WSO2, la primera únicamente con WSO2 ESB y la segunda con WSO2 ESB y WSO2 MB (Message Broker). En ambas configuraciones podemos implementar Message Store EIP con algunas pequeñas diferencias técnicas, aprovechando que WSO2 ESB está basado en Apache Synapse (https://synapse.apache.org), podemos mediar las peticiones y los mensajes haciendo que se persistan en diferentes tipos de medios ([WSO2 ESB Store - Mediator](https://docs.wso2.org/display/ESB481/Store+Mediator "WSO2 ESB - Store Mediator")), como:
  * In Memory Message Store (https://docs.wso2.org/display/ESB481/In+Memory+Message+Store): Es el almacenamiento por defecto, al reiniciar el ESB los mensajes se perderán. Al ser en memoria, es el más rápido que todos. Muy usado si queremos almacenamiento y entrega de alta-velocidad.
  * JMS Message Store (https://docs.wso2.org/display/ESB481/JMS+Message+Store)
  * Custom Message Store (https://docs.wso2.org/display/ESB481/Custom+Message+Store)
Durante mis pruebas con WSO2 ESB 4.8.1 me he encontrado que la funcionalidad de explorar los mensajes almacenados en el Message Store cuando usamos cualquier JMS Broker ha sido quitada desde la versión 4.7.0 (más información aquí: <https://wso2.org/jira/browse/ESBJAVA-2529>), así que implementaremos 3 escenarios para explorar los mensajes almacenados en el lado del WSO2 ESB y del WSO2 MB:
  * Escenario 1: Hacer una petición a un HTTP Proxy y almacenar el mensaje en un Message Store de tipo in memory. 
  * Escenario 2: Hacer una petición a un HTTP Proxy y almacenar el mensaje en una Cola del WSO2 MB. 
  * Escenario 3: Hacer una petición a un HTTP Proxy que hace de interfaz de un JMS Proxy. 
Antes de desarrollar cada escenarios, vamos a explicar cómo configurar WSO2 ESB con WSO2 MB.

###  5.1. Instalación y configuración de WSO2 ESB y WSO2 MB
Para poder conectar WSO2 ESB con los Brokers, es necesario que estos implementen Java Message Service (JMS), conectando un Broker facilita comunicación desacoplada, confiable y asíncrona entre los diferentes componentes de un sistema distribuido.
WSO2 ESB puede conectarse con:
  * ActiveMQ
  * IBM WebSphere MQ
  * IBM WebSphere Application Server
  * JBossMQ
  * Microsoft Message Queuing (MSMQ)
  * Tibco EMS
  * SwiftMQ
  * Oracle WebLogic 10.3.4.0
  * Y WSO2 Message Broker
Emplearemos WSO2 MB 2.1.0 (con offset 4) y WSO2 ESB 4.8.1 (con offset 2), ambos sobre Mac OSX 10.9.1 y Java 7.x. Descargarlos y descomprimirlos. Luego configurar WSO2 MB para que no tenga conflictos con WSO2 ESB, para ello cambiar los puertos (offset) de ambas aplicaciones.
**Configuración de WSO2 MB:**
1) Actualizar offset en \repository\conf\carbon.xml
2) Actualizar puertos de Apache Cassandra en \repository\conf\advanced\qpid-virtualhosts.xml
3) Con Mac OS X (Mavericks) y Java 7.x hay que actualizar la librería Java Snappy ya que si no se hace, WSO2 MB o WSO2 BAM no funcionaría ya que ambos embeben Apache Cassandra (https://code.google.com/p/snappy-java/issues/detail?id=39). Sólo hay que descargar snappy-java-1.0.5.jar, luego extraer snappy-java-1.0.5/org/xerial/snappy/native/Mac/x86_64/libsnappyjava.jnilib y copiarlo a /Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/ con este nombre libsnappyjava.dylib. Más información aquí: <https://github.com/thinkaurelius/titan/issues/62>

[sourcecode language="xml" gutter="true" wraplines="false"]  
$ sudo cp libsnappyjava.jnilib /Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/libsnappyjava.dylib  

[/sourcecode]
4) Iniciar WSO2 MB
5) Finalmente, no funcionará WSO2 MB ya que después de solucionar este error salen 2 más, uno durante el inicio y otro relacionado a JMX:

[sourcecode language="xml" gutter="true" wraplines="false"]  

[2014-03-05 14:14:24,270] INFO {org.wso2.carbon.core.transports.http.HttpTransportListener} - HTTP port : 9763  
java(682,0x1111b8000) malloc: *** error for object 0x1111a3ff0: pointer being freed was not allocated  
*** set a breakpoint in malloc_error_break to debug  
./wso2server.sh: line 299: 682 Abort trap: 6 $JAVACMD -Xbootclasspath/a:"$CARBON_XBOOTCLASSPATH" -Xms256m -Xmx1024m -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath="$CARBON_HOME/repository/logs/heap-dump.hprof" -javaagent:"$CARBON_HOME/repository/components/plugins/jamm_0.2.5.wso2v2.jar" $JAVA_OPTS -DandesConfig=qpid-config.xml -Ddisable.cassandra.server.startup=true -Dcom.sun.management.jmxremote -classpath "$CARBON_CLASSPATH" -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -Djava.io.tmpdir="$CARBON_HOME/tmp" -Dcatalina.base="$CARBON_HOME/lib/tomcat" -Dwso2.server.standalone=true -Dcarbon.registry.root=/ -Djava.command="$JAVACMD" -Dcarbon.home="$CARBON_HOME" -Djava.util.logging.config.file="$CARBON_HOME/repository/conf/log4j.properties" -Dcarbon.config.dir.path="$CARBON_HOME/repository/conf" -Dcomponents.repo="$CARBON_HOME/repository/components/plugins" -Dconf.location="$CARBON_HOME/repository/conf" -Dcom.atomikos.icatch.file="$CARBON_HOME/lib/transactions.properties" -Dcom.atomikos.icatch.hide_init_file_path=true -Dorg.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER=true -Dcom.sun.jndi.ldap.connect.pool.authentication=simple -Dcom.sun.jndi.ldap.connect.pool.timeout=3000 -Dorg.terracotta.quartz.skipUpdateCheck=true -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF8 org.wso2.carbon.bootstrap.Bootstrap $*  

[/sourcecode]

[sourcecode language="xml" gutter="true" wraplines="false"]  

[2014-03-05 15:26:15,064] INFO {org.wso2.carbon.core.transports.http.HttpTransportListener} - HTTP port : 9763  

[2014-03-05 15:27:15,492] ERROR {org.wso2.carbon.core.init.JMXServerManager} - Could not initialize RMI server  
java.io.IOException: Cannot bind to URL [rmi://localhost:9999/jmxrmi]: javax.naming.CommunicationException [Root exception is java.rmi.ConnectIOException: error during JRMP connection establishment; nested exception is:  
java.net.SocketTimeoutException: Read timed out]  

[/sourcecode]
6) Dejo Mac OS X y paso a Windows. En este caso no es necesario actualizar la librería java snappy, sólo cambiar los puertos.
**Configuración de WSO2 ESB:**
1) Actualizar offset en \repository\conf\carbon.xml
2) ~~Habilite JMS para configurarlo con el WSO2 MB externo editando el fichero /repository/conf/axis2/axis2.xml. Luego identifique el bloque para WSO2 MB 2.x y descomente el bloque~~.
3) ~~En el mismo fichero, descomente el bloque para JMSSender~~.
4) Copiar los JAR bajo /clent-lib/ al directorio /repository/components/lib/ :
  * andes-client-0.13.wso2v8.jar 
  * geronimo-jms_1.1_spec-1.1.0.wso2v1.jar 
5) Una vez WSO2 ESB y WSO2 MB (con Apache Cassandra embebida) iniciados, explore ambos servicios. Más información sobre configuración seguir la documentación oficial: <https://docs.wso2.org/display/ESB481/Configure+with+WSO2+Message+Broker>

###  5.2. Implementación de escenarios
Los escenarios a implementar serán:
  * Escenario #1: Hacer una petición a un HTTP Proxy y almacenar el mensaje en un Message Store de tipo in memory. 
  * Escenario #2: Hacer una petición a un HTTP Proxy y almacenar el mensaje en una Cola del WSO2 MB. 
  * Escenario #3: Hacer una petición a un HTTP Proxy que hace de interfaz de un JMS Proxy. 
WSO2 ESB viene con muchos ejemplos, entre ellos los relacionados a persistencia y entrega de mensajes. Toda la información para ejecutar los ejemplos puede ser encontrado aquí: <https://docs.wso2.org/display/ESB481/Store+and+Forward+Messaging+Patterns+with+Message+Stores+and+Processors>.

####  5.2.1. Escenario #1: Petición a un HTTP Proxy y almacenamiento del mensaje en un In Memory Message Store
En este escenario almacenaremos el mensajes en un Message Store de tipo In Memory, es un almacenamiento muy rápido pero tiene la desventaja de que es volatil, es decir, si reiniciamos WSO2 ESB, los mensajes se perderán.

[caption id="" align="alignnone" width="698"][![Message Store EIP with WSO2 ESB]({{ site.baseurl }}/assets/chakray-messagebroker-messagestore-arq1.png)](http://chakray.com) Message Store EIP with WSO2 ESB[/caption]
Pasos:  
1) En WSO2 ESB crear un Message Store de tipo In Memory, tal como se muestra en la figura siguiente:

[caption width="1024" align="alignnone"]![WSO2 ESB - Creating Message Store]({{ site.baseurl }}/assets/chakray-wso2mb-ob-inmemory.png) WSO2 ESB - Creating Message Store[/caption]
2) Crear un Address Endpoint:

[sourcecode language="xml" gutter="true" wraplines="false"]  
<endpoint name="openbravo1_dal_soap_ep_external">  
<address uri="http://api.bizlife.org/services/proxy_openbravo1.proxy_openbravo1HttpSoap12Endpoint"/>  
</endpoint>  

[/sourcecode]
3) Crear un HTTP Proxy Service que recoja el mensaje enviado al Address Endpoint antes creado y luego almacenarlo en el In Memory Message Store.

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<proxy xmlns="http://ws.apache.org/ns/synapse"  
name="proxy_openbravo_msgstore_inmemory"  
transports="http"  
statistics="disable"  
trace="disable"  
startOnLoad="true">  
<target>  
<inSequence>  
<property name="FORCE_SC_ACCEPTED" value="true" scope="axis2"/>  
<property name="OUT_ONLY" value="true"/>  
<property name="target.endpoint" value="openbravo1_dal_soap_ep_external"/>  
<log level="full"/>  
<store messageStore="messagestorage_openbravo_inmemory"/>  
</inSequence>  
</target>  
<description>HTTP Proxy para almacenar msg en Message Store In Memory</description>  
</proxy>  

[/sourcecode]
4) Invocar el HTTP Proxy antes creado (para fines demostrativos usar cualquier mensaje XML SOAP) varias veces, usar TryIt o SoapUI.

[caption width="1024" align="alignnone"]![WSO2 ESB - Calling Proxy using TryIt]({{ site.baseurl }}/assets/chakray-wso2mb-ob-inmemory-tryit.png) WSO2 ESB - Calling Proxy using TryIt[/caption]
5) En WSO2 ESB explorar los mensajes almacenados en el Message Store.

[caption width="1024" align="alignnone"]![WSO2 ESB - Browsing stored messages]({{ site.baseurl }}/assets/chakray-wso2mb-ob-inmemory-storemsgs.png) WSO2 ESB - Browsing stored messages[/caption]

####  5.2.2. Escenario #2: Petición a un HTTP Proxy y almacenamiento del mensaje en WSO2 MB
En este escenario introduciremos WSO2 MB para que sea él que gestione los mensajes, peticiones y entrega. En este caso configuraremos WSO2 ESB indicándole que el nuevo Broker será WSO2 MB.

[caption id="" align="alignnone" width="698"][![Message Store EIP with WSO2 ESB and WSO2 Message Broker]({{ site.baseurl }}/assets/chakray-messagebroker-messagestore-arq2.png)](http://chakray.com) Message Store EIP with WSO2 ESB and WSO2 Message Broker[/caption]
Pasos:  
1) Detenga WSO2 ESB.
2) Edite el fichero /repository/conf/jndi.properties y apunte al WSO2 MB (considere el port offset). Usar carbon como virtualhost en lugar de test. Comente el topic si no es usado, pero para evitar el error javax.naming.NameNotFoundException: TopicConnectionFactory, también configurar TopicConnectionFactory apuntando al Message Broker de igual forma que QueueConnectionFactory. El fichero quedaría así:

[sourcecode language="xml" gutter="true" wraplines="false"]  

# register some connection factories  

# connectionfactory.[jndiname] = [ConnectionURL]  
connectionfactory.QueueConnectionFactory = amqp://admin:admin@clientID/carbon?brokerlist='tcp://localhost:5677'

# register some queues in JNDI using the form  

# queue.[jndiName] = [physicalName]  
queue.proxy_openbravo1_qpid_queue = proxy_openbravo1_qpid_queue  
queue.msgstore_openbravo_wso2mb = msgstore_openbravo_wso2mb

# register some topics in JNDI using the form  

# topic.[jndiName] = [physicalName]  
connectionfactory.TopicConnectionFactory = amqp://admin:admin@clientID/carbon?brokerlist='tcp://localhost:5677'  

# topic.MyTopic = example.MyTopic  

[/sourcecode]
Donde:
  * proxy_openbravo1_qpid_queue es una cola que usaremos en el escenario #3 
  * msgstore_openbravo_wso2mb es la cola que nos permitirá almacenar los mensajes en WSO2 MB 
3) Iniciar WSO2 ESB.
4) En WSO2 ESB crear un Message Store de tipo In Memory, tal como se muestra en la figura siguiente:

[caption width="1024" align="alignnone"]![WSO2 ESB - Creating a JMS Message Store]({{ site.baseurl }}/assets/chakray-escenario2-1-wso2esb-msgstore.png) WSO2 ESB - Creating a JMS Message Store[/caption]
5) Si no lo habéis creado antes, crear un Address Endpoint, como sigue:

[sourcecode language="xml" gutter="true" wraplines="false"]  
<endpoint name="openbravo1_dal_soap_ep_external">  
<address uri="http://api.bizlife.org/services/proxy_openbravo1.proxy_openbravo1HttpSoap12Endpoint"/>  
</endpoint>  

[/sourcecode]
6) Crear un HTTP Proxy como sigue:

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<proxy xmlns="http://ws.apache.org/ns/synapse"  
name="proxy_openbravo_msgstore_wso2mb"  
transports="https,http"  
statistics="disable"  
trace="disable"  
startOnLoad="true">  
<target>  
<inSequence>  
<log level="full"/>  
<property name="FORCE_SC_ACCEPTED" value="true" scope="axis2"/>  
<property name="OUT_ONLY" value="true"/>  
<property name="target.endpoint" value="openbravo1_dal_soap_ep_external"/>  
<store messageStore="msgstore_openbravo_wso2mb"/>  
<log level="custom">  
<property name="==[ proxy_openbravo_msgstore_wso2mb ]" value="+++++++++"/>  
</log>  
</inSequence>  
</target>  
<description>HTTP Proxy - envia msg al Message Store</description>  
</proxy>  

[/sourcecode]
7) Crear un Message Processor, éste servirá para consumir los mensajes alojados en la cola de WSO2 MB y enviarlas al Endpoint correspondiente. Por el momento, la crearemos en estado inactivo y así poder observar el contenido de estos mensajes.

[sourcecode language="xml" gutter="true" wraplines="false"]  
<messageProcessor name="proxy_openbravo_processor" class="org.apache.synapse.message.processor.impl.forwarder.ScheduledMessageForwardingProcessor" targetEndpoint="openbravo1_dal_soap_ep_external" messageStore="msgstore_openbravo_wso2mb" xmlns="http://ws.apache.org/ns/synapse">  
<parameter name="interval">1000</parameter>  
<parameter name="client.retry.interval">1000</parameter>  
<parameter name="is.active">false</parameter>  
</messageProcessor>  

[/sourcecode]
8) Ya estamos listos para invocar al Proxy. Usar TryIt o SoapUI. Cualquier tipo de mensaje SOAP es válido, se trata de ver que el mensaje se almacena en la cola del WSO2 MB.

[caption width="1024" align="alignnone"]![WSO2 ESB - Calling HTTP Proxy using TryIt]({{ site.baseurl }}/assets/chakray-escenario2-2-wso2esb-tryit.png) WSO2 ESB - Calling HTTP Proxy using TryIt[/caption]
9) Ahora, si activamos el Message Processor antes creado, veremos que el contador de mensajes se decrementa y en el lado del Servicio de backend (en mi caso Openbravo ERP) se verá que el servicio se está siendo consultado.

[caption width="1024" align="alignnone"]![WSO2 MB - Queue storting messages]({{ site.baseurl }}/assets/chakray-escenario2-2-wso2mb-queue-browse.png) WSO2 MB - Queue storting messages[/caption]
Si exploramos algún mensaje almacenado en esta cola, veremos que no podemos visualizarlo correctamente ya que dicho mensaje está serializado. Será el Message Processor del WSO2 ESB quién lo deserializará para enviarlo al Endpoint.

[caption width="1024" align="alignnone"]![WSO2 MB - Browsing stored messages in Queue]({{ site.baseurl }}/assets/chakray-escenario2-2-wso2mb-queue-msgs-list.png) WSO2 MB - Browsing stored messages in Queue[/caption]

####  5.2.3. Escenario #3: Petición a un HTTP Proxy que hace de interfaz de un JMS Proxy.
Este escenario nos permite usar WSO2 ESB como un cliente JMS, es decir, que si queremos interactuar con un JMS Broker, basta con crear un JMS Proxy en WSO2 ESB, luego desde cualquier cliente SOAP (TryIt o SoapUI) podemos interactuar con el JMS Broker sin necesidad de programar un cliente JMS.
Pasos a seguir:
1) En el escenario #2 ya creamos una cola llamada proxy_openbravo1_qpid_queue que usaremos ahora para crear un JMS Proxy.
2) Detener WSO2 ESB, luego habilitar el transport JMS (sender y receiver), para ello edite el fichero /repository/conf/axis2/axis2.xml. Luego identifique el bloque para WSO2 MB 2.x y descomente el bloque. En el mismo fichero, descomente el bloque para JMSSender.
3) Reiniciar WSO2 ESB, luego crear 2 Proxies en WSO2 ESB (HTTP Proxy que recibirá petición SOAP, que luego reenviará el mismo mensaje al Queue Proxy usando transporte JMS).
proxy_openbravo1_qpid_proxy:  

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<proxy xmlns="http://ws.apache.org/ns/synapse"  
name="proxy_openbravo1_qpid_proxy"  
transports="https,http"  
statistics="disable"  
trace="disable"  
startOnLoad="true">  
<target>  
<inSequence>  
<log level="custom">  
<property name="==[ proxy_openbravo1_qpid_proxy ]==" value="ini - inSequence"/>  
</log>  
<log level="full"/>  
<property name="FORCE_SC_ACCEPTED" value="true" scope="axis2"/>  
<property name="OUT_ONLY" value="true"/>  
<send>  
<endpoint>  
<address uri="jms:/proxy_openbravo1_qpid_queue?transport.jms.ConnectionFactoryJNDIName=QueueConnectionFactory&amp;java.naming.factory.initial=org.wso2.andes.jndi.PropertiesFileInitialContextFactory&amp;java.naming.provider.url=repository/conf/jndi.properties&amp;transport.jms.DestinationType=queue"/>  
</endpoint>  
</send>  
<log level="custom">  
<property name="==[ proxy_openbravo1_qpid_proxy ]==" value="fin - inSequence"/>  
</log>  
</inSequence>  
</target>  
<description>HTTP Proxy - envia msg al Queue Proxy via JMS transport</description>  
</proxy>  

[/sourcecode]
proxy_openbravo1_qpid_queue:  

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<proxy xmlns="http://ws.apache.org/ns/synapse"  
name="proxy_openbravo1_qpid_queue"  
transports="jms"  
statistics="disable"  
trace="disable"  
startOnLoad="true">  
<target>  
<inSequence>  
<log level="custom">  
<property name="==[ proxy_openbravo1_qpid_queue ]==" value="ini - inSequence"/>  
</log>  
<log level="full"/>  
<property name="FORCE_SC_ACCEPTED" value="true" scope="axis2"/>  
<property name="OUT_ONLY" value="true"/>  
<send>  
<endpoint>  
<address uri="http://api.bizlife.org/services/proxy_openbravo1.proxy_openbravo1HttpSoap12Endpoint"/>  
</endpoint>  
</send>  
<log level="custom">  
<property name="==[ proxy_openbravo1_qpid_queue ]==" value="fin - inSequence"/>  
</log>  
</inSequence>  
</target>  
<description>JMS Proxy - recibe msg y lo envia a proxy_openbravo1_qpid_queue</description>  
</proxy>  

[/sourcecode]
4) Ahora, usando cualquier cliente SOAP hacer una consulta al proxy.

[caption width="1024" align="alignnone"]![WSO2 ESB - Calling JMS Proxy]({{ site.baseurl }}/assets/chakray-escenario3-1-wso2esb-tryit.png) WSO2 ESB - Calling JMS Proxy[/caption]
En las trazas de WSO2 ESB veremos los log de la ejecución de los 2 proxies anteriores.

[caption width="1024" align="alignnone"]![WSO2 ESB - Logs when calling JMS Proxy]({{ site.baseurl }}/assets/chakray-escenario3-2-wso2esb-logs.png) WSO2 ESB - Logs when calling JMS Proxy[/caption]

## Conclusiones
  * Si requieres una comunicación desacoplada, confiable, con capacidad de persistencia de los mensajes, pues el uso de un Message Broker es la mejor opción. 
  * Para entornos o Sistemas de Información Críticos es recomendable el uso de Message Brokers por su capacidad de gestionar grandes volúmenes de mensajes y su capacidad de persistencia de mensajes. 
  * Para entornos Big Data, WSO2 Message Broker es capaz de escalar en las 3 dimensiones siguientes: volumen de mensajes, tamaño de mensajes y número de colas. 
  * Hay una tendencia en el uso de Message Brokers para entornos Internet of Things (IoT), por ser entornos muy restrictivos (red lentas o con mucha latencia, potencia de los sensores, mensajes pequeños, etc.) se opta por protocolos más adecuados como MQTT en lugar de JMS/AMQP. Se suele usar MQTT para la "primera milla", mientras que AMQP se usa para mover los datos (también es óptimo, preparado para mensajes grandes, etc.) por resto de los sistemas distribuídos. 
  * El complemento perfecto para WSO2 ESB es WSO2 Message Broker, mientras que el ESB crea interfases para nuestros servicios, WSO2 Message Broker se encarga de dar persistencia y entrega de los mensajes de manera eficiente. 
  * Java Message Service (JMS) es el estándar más usado a pesar de que no define el formato del mensaje a emplear o cómo hay que interactuar con el Message Broker. AMQP es el estándar que cubre dicho "gap". 
