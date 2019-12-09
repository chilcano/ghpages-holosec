---
layout:     post
title:      'Evaluación del nivel y madurez de integración de Message Brokers opensource con WSO2 ESB'
date:       2015-01-25 19:16:31
categories: ['Broker', 'EIP']
tags:       ['ActiveMQ', 'HornetQ', 'QPid', 'RabbitMQ', 'WSO2 ESB', 'WSO2 MB']
status:     publish 
permalink:  "/2015/01/25/evaluacion-nivel-madurez-integracion-message-brokers-opensource-wso2-esb/"
---
En mi anterior post expliqué como usar Apache Qpid junto con WSO2 ESB (https://holisticsecurity.wordpress.com/2014/12/03/wso2-message-broker-vs-apache-qpid-messaging-eip), un message broker ligero, potente y compatible con JMS y AMQP, en lugar de WSO2 Message Broker, un message broker potente, rápido, estándar y perfectamente integrable a WSO2 ESB. Pues ahora, me gustaría compartir la comparación que hice previamente para seleccionar [Apache Qpid](http://qpid.apache.org "Apache Qpid").

![Integración de Message Brokers opensource con WSO2 ESB]({{ site.baseurl }}/assets/msgbroker-00-foss-brokers-wso2-esb-activemq-qpid-rabbitmq-hornetq.png)  
 _Integración de Message Brokers opensource con WSO2 ESB_

<!-- more -->
Claro, vosotros preguntaréis, porqué no usar Apache ActiveMQ sabiendo que es un producto opensource, maduro, muy bien integrado a WSO2 ESB y a otros ESB y Mediators Engines como Apache Camel?, pues, en base a mis requerimientos en proyectos, buscaba por algún message broker fiel a los estándares y con una arquitectura capaz de crear complejos y críticos escenarios, pero muy orientado a los usos clásicos del message broker (comunicación asíncrona, desacoplamiento, integrable con legacy systems), y en este caso Apache Qpid y Apache ActiveMQ lo son, sin embargo me incliné por Apache Qpid porque pude integrarlo en poco minutos con WSO2 ESB e implementar los EIPs otros minutos más.

![Google Trends 2015: Apache ActiveMQ, RabbitMQ, Apache Qpid y JBoss HornetQ]({{ site.baseurl }}/assets/msgbroker-02-foss-brokers-wso2-esb-2015-google-trends.png)  
 _Google Trends 2015: Apache ActiveMQ, RabbitMQ, Apache Qpid y JBoss HornetQ_
Revisando alguna información de Apache ActiveMQ, me encontré con algunos comentarios indicando que su arquitectura estaba quedando rezagada imposibilitando su uso en escenarios complejos como BigData e IoT, sofisticados brokers como Apache Kafka y ZeroMQ, por ejemplo. Aquí unos comentarios.
>  Disadvantages in ActiveMQ:  
>  * Complex algorithmic architecture for design and implement. Large cluster configuration for invoking simple message passing services.  
>  * Message blocks happens when the duplex network connected in between two JMS Borker.  
>  * When we want to set up interoperable network we should have to do alot of configuration due to heavy traffic flow.  
>  Referencia:  
>  http://bipinkunjumon.blogspot.co.uk/2013/05/zeromq-and-activemq-comparison.html  
Y
>  ActiveMQ is still a very good choice for Enterprise Messaging. But ActiveMQ has gotten some competition, which is arriving with newer architectures, better performance, and with the support of standardized protocols in the area of the space deer.  
>  Referencia:  
>  http://predic8.com/activemq-hornetq-rabbitmq-apollo-qpid-comparison.htm  

## I. Message Brokers
La lista de Message Brokers opensource a considerar son los siguientes:  

![Lista de Message Brokers opensource a considerar \(Qpid, ActiveMQ, RabbitMQ, HornetQ, WSO2 MB, ZeroMQ, Apache Kafka, Apache Storm, OpenAMQP\)]({{ site.baseurl }}/assets/msgbroker-01-foss-brokers-wso2-esb-activemq-qpid-rabbitmq-hornetq-zeromq-kafka-storm.png)  
 _Lista de Message Brokers opensource a considerar (Qpid, ActiveMQ, RabbitMQ, HornetQ, WSO2 MB, ZeroMQ, Apache Kafka, Apache Storm, OpenAMQP)_
 **Standard Message Brokers**  
1\. RabbitMQ: https://www.rabbitmq.com  
2\. Apache Qpid: https://qpid.apache.org  
3\. WSO2 Message Broker: http://wso2.com/products/message-broker  
4\. Apache ActiveMQ: http://activemq.apache.org  
5\. JBoss HornetQ: http://hornetq.jboss.org  
6\. Apache Kafka: http://kafka.apache.org  
7\. Apache Storm: http://storm.apache.org  
8\. OpenAMQP: http://www.openamq.org
**Especialized Message Brokers**  
9\. OpenMAMA: http://www.openmama.org  
10\. ZebroGaMQ: https://github.com/simatic/ZebroGaMQ  
11\. ZeroMQ: http://zeromq.org

## II. Criterios de evaluación
Dentro de los criterios de evaluación considero el nivel de integración con WSO2 ESB. Para mi fue de vital importancial su facilidad de integración. No quería "matar moscas a cañonazos", usar WSO2 Message Broker o Apache ActiveMQ lo eran.
A continuación listo los criterios de evaluación.
No. | Features/functionalities to be considered | Weight  
---|---|---  
1. | Support of JMS | 5/5  
2. | Support of AMQP | 5/5  
3. | Message Store backend | 3/5  
4. | Reliability & Clustering | 3/5  
**5.** | **Maturity level of WSO2 integration** | **5/5**  
6. | Performance | 3/5  
7. | Quality of documentation | 4/5  
8. | Community behind | 4/5  
9. | Successfully cases/Where is used? | 4/5  

## III. Resultados
Los resultados son los siguientes:
Order | Message Broker | Total  
---|---|---  
1 | RabbitMQ | 28  
2 | Apache Qpid | 38  
3 | ZeroMQ | 23  
4 | OpenAMQP | 22  
5 | JBoss HornetMQ | 27  
6 | Apache ActiveMQ | 40  

![Nivel de madurez e integración con WSO2 ESB - resultados]({{ site.baseurl }}/assets/msgbroker-04-foss-brokers-wso2-esb-maturity-level-result.png)  
_Nivel de madurez e integración con WSO2 ESB - resultados_
Y el análisis a mayor detalle aquí:  

![Comparación entre RabbitMQ, Apache Qpid, ZeroMQ, OpenAMQP, JBoss HornetQ y Apache ActiveMQ - detalles]({{ site.baseurl }}/assets/msgbroker-03-foss-brokers-wso2-esb-maturity-level-list.png)  
_Comparación entre RabbitMQ, Apache Qpid, ZeroMQ, OpenAMQP, JBoss HornetQ y Apache ActiveMQ - detalles_

## IV. Conclusiones
  * Resultado de la comparación, en la introducción ya os adelanté, Apache Qpid fue el broker ideal que cumplía mis requerimientos: facilidad de integración con WSO2 ESB y facilidad de implementar los EIP relacionados a mensajería.
  * WSO2 ESB como motor de mediación y Apache Qpid como motor de enrutamiento puedo implementar de manera fácil y de manera rápida todos [los escenarios y patrones de integración](https://docs.wso2.com/display/IntegrationPatterns/Enterprise+Integration+Patterns+with+WSO2+ESB "Enterprise Integration Patterns with WSO2 ESB"), siguiendo las recientes tendencias de composición de Microservicios. Esto sí que es ágil y "ligero", es una mínima infraestructura para poder implementar y gobernar [microservices](http://www.slideshare.net/wso2.org/microservices-20140915v11 "Merging microservices architecture with SOA Practices").
  * Apache Kafka, ZeroMQ y WSO2 Message Broker, son message brokers de "alto rendimiento", cada uno resuelve la complejidad a su manera, ellos fueron sacados de esta lista. Están pensados para escenarios complejos, críticos y de grandes volúmenes de mensajes y grandes tamaños. Más adelante escribiré sobre Apache Kafka.
  * Apache ActiveMQ sigue siendo el message broker más usado, sin embargo su uso ha decrecido mucho en el último año, parece que RabbitMQ, aunque sólo implemente AMQP y no JMS, tiene más demanda. El gráfico de Google Trends lo corrobora.
  * RabbitMQ tiene un gran interés, se ve en las tendencias, y realmente es muy robusto, pero para mis requerimientos de integrabilidad con WSO2 ESB no está al nivel de Apache ActiveMQ y Apache Qpid, además el que no implemente AMQP 1.0 o JMS 1.1 dificulta mucho más su integración con WSO2 ESB. En un posterior artículo os explicaré cómo lo resolví y publiqué el código fuente resultado de esa integración. Ah, no me refiero a una integración como lo explica Luis en su [entrada](https://luispenarrubia.wordpress.com/2014/12/10/integrate-wso2-esb-and-rabbitmq-using-amqp-transport), me refiero a una integración siguiendo los EIPs ([Dead Letter Queue, Retry Queue, Wire Tap, Throttling, etc.](https://docs.wso2.com/display/IntegrationPatterns/Messaging+Channels "Messaging EIP")).
  * Apache Qpid tiene un buen nivel de soporte, lo viví en carne propia, los foros son bien activos, para mi fue una sorpresa muy grata. Por otro lado también pude comprobar que el producto y los clientes son muy buenos, ellos son usados por otros proyectos, desde el mismo Apache ActiveMQ, WSO2 Message Broker y RedHat Enterprise MRTG.
  * Sobre JBoss HornetQ, pues muy poco tengo que decir, su interés según Google Trends es muy bajo, sin embargo tiene mucha información, incluso he visto un libro sobre él, pero por otro lado, RedHat usa Apache ActiveMQ en lugar de HornetQ, a pensar de ser un proyecto patrocinado por ellos.
  * La integración de Apache Qpid y WSO2 ESB sería total si desde WSO2 ESB pudiese también gestionar las colas y los mensajes, pero esto no es posible ni con Apache ActiveMQ, hay algunos ["workarounds" pero no va bien completamente](http://stackoverflow.com/questions/21761265/cant-browse-messages-in-an-activemq-based-message-store-with-wso2-esb-console). En otro artículo escribiré sobre ello y también compartiré el código.

## V. Referencias
  * [Broker wars: ActiveMQ, Qpid, HornetQ and RabbitMQ in Comparison](http://predic8.com/activemq-hornetq-rabbitmq-apollo-qpid-comparison.htm)
  * [A quick message queue benchmark: ActiveMQ, RabbitMQ, HornetQ, QPID, Apollo…](http://blog.x-aeon.com/2013/04/10/a-quick-message-queue-benchmark-activemq-rabbitmq-hornetq-qpid-apollo)
  * [JMS speed test: ActiveMQ vs HornetQ](http://integr8consulting.blogspot.co.uk/2011/02/jms-speed-test-activemq-vs-hornetq.html)
  * Middleware trends and market leaders 2011: 
    * <http://zeromq.wdfiles.com/local--files/intro%3Aread-the-manual/Middleware%20Trends%20and%20Market%20Leaders%202011.pdf>
    * <https://www-acc.gsi.de/wiki/pub/Frontend/MeetingCMW130425/CHEP-2012-NewCERNcontrolsMiddleware.pdf>
  * [Apache Kafka: Next Generation Distributed Messaging System - comparison with RabbitMQ and ActiveMQ](http://www.infoq.com/articles/apache-kafka)
  * [Apache Kafka: powered by ](https://cwiki.apache.org/confluence/display/KAFKA/Powered+By)
