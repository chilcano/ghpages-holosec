---
layout:     post
title:      'WSO2 Message Broker vs. Apache Qpid - Messaging Integration Patterns'
date:       2014-12-02 23:17:51
categories: ['Big Data', 'Broker', 'SOA']
tags:       ['ActiveMQ', 'Apache Qpid', 'Message Broker', 'RabbitMQ', 'WSO2 ESB', 'WSO2 MB']
status:     publish 
permalink:  "/2014/12/03/wso2-message-broker-vs-apache-qpid-messaging-eip/"
---
If you want to define an Integration Architecture based on Messaging with WSO2, the only alternative you have is to do with WSO2 Message Broker and possibly also with Apache ActiveMQ. In earlier versions of WSO2 ESB, the WSO2 web had official information on how to integrate WSO2 ESB and Apache ActiveMQ, integrate both always was a common pattern, now it has been left in place of WSO2 Message Broker.

![Enabling JMS Transport]({{ site.baseurl }}/assets/blog20141122-qpid-01-jms-transport-wso2esb.png)  
 _Enabling JMS Transport_
Initially to use WSO2 ESB and Apache ActiveMQ was the de-facto solution to implement the Messaging Integration Architecture, but now all the focus are on WSO2 Message Broker. The product strategy for WSO2 Message Broker is being used in big and critical projects, for example, below some use cases:
  * Guarantee Delivery
  * Large volumes of messages
  * Persistence of several and big messages
  * High availability & scalability

<!-- more -->
Well, WSO2 Message Broker is perfect for BigData or IoT Projects, but what if we want implement and solve the same requirements but without losing functionalities. Then, What Message Broker or MOM tool should I use?.
Apache ActiveMQ is a good alternative, JBoss HornetQ, RabbitMQ and other. Some time ago I wrote about of this, here the blog post: <https://holisticsecurity.wordpress.com/2014/03/07/message-brokering-y-recoleccion-datos-big-data-wso2>
So what alternatives do we have to WSO2 Message Broker comparable to robustness, scalability, interoperability, lightweight without losing functionalities?. The answer is Apache Qpid. Now we will explain why.

## I. Why Apache Qpid is a good comparable alternative to WSO2 Message Broker?
  1. Earlier releases of WSO2 Message Broker had Apache Qpid embedded.
  2. Last release of WSO2 Message Broker uses WSO2 Andes, an extended version of Apache Qpid, to connect to Apache Cassandra as message store backend.
  3. WSO2 ESB solves the most common EIPs for messaging (guarantee delivery, persistence of messages, routing, selective channels, etc.) by developing 2 features as are the MessageStore and MessageProcessor. These features are implement on top of JMS and are technically agnostic. In other words, if the Message Broker implements JMS, then there are many possibilities that the Message Broker can be integrated to WSO2 ESB.
  4. Apache Qpid is a Message Broker lightweight, powerful, robust and has different message implementation, they are:  
    * In-memory message store.
    * Apache Derby store.
    * Message store based on Oracle Berkeley DB, suitable to create high available messaging platforms.
  5. Apache Qpid is compatible with: JMS 1.1, AMQP 1.0, 0-8, 0-9 and 0-9-1.
  6. If you want to play with WSO2 Message Broker, you should do it on an robust and powerful infraestructura, however, that is not necessary for Apache Qpid. You can run Apache Qpid in constrained infraestructure.
  7. Other alternatives to WSO2 Message Broker could be:  
    * Apache ActiveMQ, although is a project mature, it is big compared to Apache Qpid and its architecture is its limit.
    * RabbitMQ, it is bigger than Apache Qpid, but its architecture is based on plugins, it is faster but does not implement AMQP 1.0 natively, is necessary install an experimental plugin (https://www.rabbitmq.com/specification.html).
Well, after of this little introduction, I will explain step-by-step how to integrate quickly WSO2 ESB with Apache Qpid, how to enable JMS transport for Synapse Proxies and how to implement the EIP related to messaging.

## II. Installation of Apache Qpid
1.- Download Apache Qpid. The latest version is 0.30 (qpid-broker-0.30-bin.tar.gz) and you can download from here <http://qpid.apache.org/components/java-broker/index.html>
2.- Unzip and run it.
```sh  
$ ~/0dev-env/2srv/qpid-broker-0.30/bin/qpid-server  
System Properties set to -Damqj.logging.level=info -DQPID_HOME=/Users/Chilcano/0dev-env/2srv/qpid-broker-0.30 -DQPID_WORK=/Users/Chilcano/0dev-env/2srv/qpid-work-std  
QPID_OPTS set to -Damqj.read_write_pool_size=32 -DQPID_LOG_APPEND=  
Using QPID_CLASSPATH /Users/Chilcano/0dev-env/2srv/qpid-broker-0.30/lib/*:/Users/Chilcano/0dev-env/2srv/qpid-broker-0.30/lib/plugins/*:/Users/Chilcano/0dev-env/2srv/qpid-broker-0.30/lib/opt/*  
Info: QPID_JAVA_GC not set. Defaulting to JAVA_GC -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError  
Info: QPID_JAVA_MEM not set. Defaulting to JAVA_MEM -Xmx2g  
log4j:WARN No appenders could be found for logger (org.apache.qpid.server.plugin.QpidServiceLoader).  
log4j:WARN Please initialize the log4j system properly.  
log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.  

[Broker] BRK-1006 : Using configuration : /Users/Chilcano/0dev-env/2srv/qpid-work-std/config.json  

[Broker] BRK-1007 : Using logging configuration : /Users/Chilcano/0dev-env/2srv/qpid-broker-0.30/etc/log4j.xml  

[Broker] BRK-1001 : Startup : Version: 0.30 Build: Unversioned directory  

[Broker] BRK-1010 : Platform : JVM : Oracle Corporation version: 1.7.0_51-b13 OS : Mac OS X version: 10.10 arch: x86_64  

[Broker] BRK-1011 : Maximum Memory : 2,077,753,344 bytes  

[Broker] BRK-1002 : Starting : Listening on TCP port 5672  

[Broker] MNG-1001 : Web Management Startup  

[Broker] MNG-1002 : Starting : HTTP : Listening on port 8080  

[Broker] MNG-1004 : Web Management Ready  

[Broker] MNG-1001 : JMX Management Startup  

[Broker] MNG-1002 : Starting : RMI Registry : Listening on port 8999  

[Broker] MNG-1002 : Starting : JMX RMIConnectorServer : Listening on port 9099  

[Broker] MNG-1004 : JMX Management Ready  

[Broker] BRK-1004 : Qpid Broker Ready  
```
If you want change the working directory, then you should define "QPID_WORK" variable in 'qpid-server' file or as System Variable in your S.O.:
```sh  
export QPID_WORK=$HOME/0dev-env/2srv/qpid-work-std  
```
3.- If you want change ports, enable services, exchanges, enable plugins or replace default configuration, you have a lot of possibilities, just follow this indications:
<a href="https://qpid.apache.org/releases/qpid-0.30/java-broker/book/Java-Broker-Configuring-And-Managing.html">https://qpid.apache.org/releases/qpid-0.30/java-broker/book/Java-Broker-Configuring-And-Managing.html</a>
4.- If you want configure High Availability or configure a Cluster of Apache Qpid, then just follow this informations. You could configure Apache Qpid HA as Active-Pasive or Active-Active with automatic Failover.  
Check this information:
<a href="http://qpid.apache.org/releases/qpid-0.30/java-broker/book/Java-Broker-High-Availability.html">http://qpid.apache.org/releases/qpid-0.30/java-broker/book/Java-Broker-High-Availability.html</a>

## III. Enabling JMS transport in WSO2 ESB for Apache Qpid

![Enabling JMS Transport]({{ site.baseurl }}/assets/blog20141122-qpid-01-jms-transport-wso2esb.png)  
_Enabling JMS Transport_
1.- After of installation of WSO2 ESB, to edit the file %WSO2ESB_HOME%/repository/conf/axis2/axis2.xml and add the JMSSender and JMSListener for Apache Qpid as follow:
```xml  

[...]

<!-- ================================================= -->  

<!-- Transport Ins (Listeners) -->  

<!-- ================================================= -->

[...]

<!-- SAP Transport Listeners -->  

<!-- -->  

<!-- -->

<!-- **** JMS Transport LISTENER support with Apache Qpid **** -->
org.apache.qpid.jndi.PropertiesFileInitialContextFactory  
repository/conf/jndi.properties  
TopicConnectionFactory  
topic
org.apache.qpid.jndi.PropertiesFileInitialContextFactory  
repository/conf/jndi.properties  
QueueConnectionFactory  
queue
org.apache.qpid.jndi.PropertiesFileInitialContextFactory  
repository/conf/jndi.properties  
QueueConnectionFactory  
queue

<!-- ================================================= -->  

<!-- Transport Outs (Senders) -->  

<!-- ================================================= -->

[...]

<!-- SAP Transport Senders -->  

<!-- -->  

<!-- -->

<!-- **** JMS Transport SENDER support with Apache Qpid **** -->

<!-- ================================================= -->  

<!-- Global Engaged Modules -->  

<!-- ================================================= -->

[...]  
```
2.- Now, download the Apache Qpid Java Client (qpid-client-0.30-bin.tar) from http://qpid.apache.org/components/qpid-jms/index.html, unzip it and copy the qpid-client-0.30.jar and qpid-common-0.30.jar to %WSO2ESB_HOME%/repository/components/lib/ and restart WSO2 ESB.
3.- Edit the JNDI file (%WSO2ESB_HOME%/repository/conf/jndi.properties) as follow:
```sh  

# register some connection factories  

# connectionfactory.[jndiname] = [ConnectionURL]  

##connectionfactory.QueueConnectionFactory = amqp://admin:admin@clientID/test?brokerlist='tcp://localhost:5672'  
connectionfactory.QueueConnectionFactory = amqp://admin:admin@clientID/default?brokerlist='tcp://localhost:5672'  
connectionfactory.TopicConnectionFactory = amqp://admin:admin@clientID/default?brokerlist='tcp://localhost:5672'

# register some queues in JNDI using the form  

# queue.[jndiName] = [physicalName]  
queue.MyJNDIQueue02 = QPID_QUEUE_02

# register some topics in JNDI using the form  

# topic.[jndiName] = [physicalName]  

##topic.MyTopic = example.MyTopic  
```
Where:  
* 'clientID/default' is the identifier of client used to connect and the virtualhost respectively.  
* 'MyJNDIQueue02' is the JNDI name for my queue and 'QPID_QUEUE_02' is the physical queue name. They will used in the next section, when creating MessaeStore and MessaProcessor.
4.- Create a Synapse Proxy in WSO2 ESB to send a message to Apache Qpid.
```xml  
<br /><br /><br /><br /><br /><br /> <header />
Date $1
<address />
Send a message to Apache Qpid queue (QPID_QUEUE_01)
```
Now we can send messages using this Proxy and in the Apache Qpid side are as shown:

![inspecting the store messages in Apache Qpid]({{ site.baseurl }}/assets/blog20141122-qpid-02-message-in-broker.png)  
_Inspecting the store messages in Apache Qpid_
5.- Create a Synapse Proxy in WSO2 ESB to listen for messages from Apache Qpid.
```xml  
<br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /> contentType  
application/soap+xml
myQueueConnectionFactory  
QPID_QUEUE_01  
queue  
Message received from Apache Qpid queue (QPID_QUEUE_01)
```
6.- Now we can test this configuration using SoapUI and follow the logs in WSO2 ESB console.
```sh  

[...]  

[2014-11-27 22:13:29,000] INFO - LogMediator [ProxyQpidSender] BODY SENT = Date 11/27/14 10:13 PM  

[2014-11-27 22:13:29,021] INFO - LogMediator [ProxyQpidReceiver] BODY RECEIVED = Date 11/27/14 10:13 PM  

[2014-11-27 22:13:29,474] DEBUG - ServerWorker Starting a new Server Worker instance  

[2014-11-27 22:13:29,476] INFO - LogMediator [ProxyQpidSender] BODY SENT = Date 11/27/14 10:13 PM  

[2014-11-27 22:13:29,496] INFO - LogMediator [ProxyQpidReceiver] BODY RECEIVED = Date 11/27/14 10:13 PM  

[...]  
```
7.- Conclusions:
  * The first time you run the "ProxyQpidSender" proxy, the proxy will automatically create the "QPID_QUEUE_01" queue in Apache Qpid.
  * If you run several times the "ProxyQpidSender" proxy to send and store message in "QPID_QUEUE_01" queue, they will be processed quickly for the "ProxyQpidReceiver" proxy, because this proxy is listen permanently for messages in this queue. But, if you want inspect the payload of the messages, I recommend you disable the "ProxyQpidReceiver" proxy in WSO2 ESB. Of this way, using Apache Qpid Web Console you can review any aspect of message (header and payload).
  * The "ProxyQpidSender" proxy has as transport the HTTP protocol and "ProxyQpidReceiver" proxy is using JMS transport.

## IV. WSO2 ESB Message Store and Message Processor for Apache Qpid

### IV.1. Creating a Message Store in WSO2 ESB
1.- Create the WSO2 ESB Message Store pointing to QPID_QUEUE_02.
```xml  
<br /> org.apache.qpid.jndi.PropertiesFileInitialContextFactory  
repository/conf/jndi.properties  
MyJNDIQpidQueue02  
QueueConnectionFactory  
admin  
admin  
1.1
```
2.- Create a new WSO2 ESB Proxy to send messages to the new message store 'MsgStoreQpid02' using the 'Store Mediator'.
```xml  
<br /><br /><br /><br /><br /><br /> <header />
MsgStore - SysDate $1
Store the message in the WSO2 ESB Message Store (MsgStoreQpid02)
```

### IV.2. Creating Message Sampling Processor in WSO2 ESB

![Message Sampling Processor with WSO2 ESB and Apache Qpid]({{ site.baseurl }}/assets/blog20141122-qpid-03-msg-sampling-processor.png)  
_Message Sampling Processor with WSO2 ESB and Apache Qpid_
1.- Create a Sequence for the Sampling Processor
```xml  
<br /><br /><br /><br /><br /><br /><br />```
2.- Create the Message Sampling Processor
```xml  
<br /> 1000  
1  
SequenceQpidSampling  
0 0/1 * 1/1 * ? *  
true
```
3.- Send messages to Message Store and check the logs
```sh  

[2014-11-28 00:06:39,654] DEBUG - ServerWorker Starting a new Server Worker instance  

[2014-11-28 00:06:39,657] INFO - LogMediator [ProxyQpidSender2MsgStoreSampling] MSG STORED = MsgStore - SysDate 11/28/14 12:06 AM  

[2014-11-28 00:06:45,894] DEBUG - ServerWorker Starting a new Server Worker instance  

[2014-11-28 00:06:45,896] INFO - LogMediator [ProxyQpidSender2MsgStoreSampling] MSG STORED = MsgStore - SysDate 11/28/14 12:06 AM  

[2014-11-28 00:07:00,014] INFO - JmsConsumer [MsgStoreQpid02-C-1] reconnected to store.  

[2014-11-28 00:07:00,029] INFO - LogMediator [SequenceQpidSampling] MSG = MsgStore - SysDate 11/28/14 12:06 AM  

[2014-11-28 00:08:00,016] INFO - LogMediator [SequenceQpidSampling] MSG = MsgStore - SysDate 11/28/14 12:06 AM  
```

### IV.3. Creating Message Forwarding Processor in WSO2 ESB
**1.- Create a Message Forwarding Processor implementing the OUT-ONLY pattern**
In this case, if the backend service processes very well the request, then the message processor will delete the message from message store.

![Message Forwarding Processor with OUT-ONLY implemented]({{ site.baseurl }}/assets/blog20141122-qpid-04-msg-forwarding-processor-out-only.png)  
_Message Forwarding Processor with OUT-ONLY implemented_
Before, we have to disable the below MessageProcessor (MsgProcessorQpidSampling), just for testing purposes. Also, we have to create a new Proxy to send messages well formated to MsgStoreQpid02.
1.1. Create the Synapse Proxy with OUT-ONLY property enabled.
```xml  
<br /><br /><br /><br /><br /><br /> <header />
MySysDate $1
Store the message in the WSO2 ESB Message Store (MsgStoreQpid02)
```
Where:  
* Action is the header property of SOAP message that represents the operation to be executed.  
* OUT_ONLY: should be set to 'true' for OUT-ONLY pattern.
Also, as Endpoint I will use my custom echoPassThroughProxy, where the URL is: http://localhost:8310/services/echoPassThroughProxy
1.2. Create an Endpoint for our External backend service, in this case is my 'echoPassThroughProxy' will be created in WSO2 ESB.
```xml  
<br /> <address></address>
```
1.3. Create the Message Forwarding Processor (OUT-ONLY).
```xml  
<br /> 1000  
1000  
0 0/1 * 1/1 * ? *  
true
```
1.4. Check the logs
```sh  

[2014-11-27 23:25:25,003] INFO - LogMediator [ProxyQpidSender2MsgStoreFwdOutOnly] MSG STORED = MySysDate 11/27/14 11:25 PM  

[2014-11-27 23:25:26,062] DEBUG - ServerWorker Starting a new Server Worker instance  

[2014-11-27 23:25:26,064] INFO - LogMediator [ProxyQpidSender2MsgStoreFwdOutOnly] MSG STORED = MySysDate 11/27/14 11:25 PM  

[2014-11-27 23:25:27,056] DEBUG - ServerWorker Starting a new Server Worker instance  

[2014-11-27 23:25:27,058] INFO - LogMediator [ProxyQpidSender2MsgStoreFwdOutOnly] MSG STORED = MySysDate 11/27/14 11:25 PM  

[2014-11-27 23:26:00,004] DEBUG - ServerWorker Starting a new Server Worker instance  

[2014-11-27 23:26:00,005] INFO - LogMediator [echoPassThroughProxy] = ----- inSeq ----- = [echoPassThroughProxy]  

[2014-11-27 23:26:00,005] INFO - LogMediator To: /services/echoPassThroughProxy, WSAction: urn:echoString, SOAPAction: urn:echoString, MessageID: urn:uuid:08a5db99-c590-4352-8ced-1ae8908814c2, Direction: request, Envelope: MySysDate 11/27/14 11:25 PM  

[2014-11-27 23:26:00,008] DEBUG - ServerWorker Starting a new Server Worker instance  

[2014-11-27 23:26:00,012] INFO - LogMediator [echoPassThroughProxy] = ----- outSeq ----- = [echoPassThroughProxy]  

[2014-11-27 23:26:00,012] INFO - LogMediator To: http://www.w3.org/2005/08/addressing/anonymous, WSAction: , SOAPAction: , MessageID: urn:uuid:8916ed21-10a3-43ee-9d0e-6acf8a3f4de7, Direction: response, Envelope:  
```
**2.- Create a Message Forwarding Processor implementing IN-OUT pattern**
The only difference with the above is that it requires two Synapse Sequences (Reply and Fault sequences) that will post-process the SOAP response of Backend Service in a blocking-mode as It is a succesfully response or unseccsfully response, in other words, if everything goes well, the message processor will send an ACK to message store to consume (delete) the stored message and the message processor will trigger the reply sequence, otherwise the message processor will trigger the fault squence (the message is not consumed/deleted of message store).

![Message Forwarding Processor with IN-OUT implemented]({{ site.baseurl }}/assets/blog20141122-qpid-05-msg-forwarding-processor-in-out.png)  
_Message Forwarding Processor with IN-OUT implemented_
Before, disable the above message processor, just for testing purporses.
2.1. Create the Proxy with OUT_ONLY property set to false.
```xml  
<br /><br /><br /><br /><br /><br /> <header />
MySysDate $1
Store the message in the WSO2 ESB Message Store (MsgStoreQpid02)
```
Where:  
* Action is the header property of SOAP message that represents the operation to be executed.  
* OUT_ONLY: should be set to 'false' for IN-OUT pattern or to remove the entry.
2.2. Create the Reply Sequence and Fault Sequence
```xml  
<br /><br /><br /><br /><br /><br /><br />```
```xml  
<br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br />```
2.3.- Adjust the existing Message Processor to include new Sequences for the IN-OUT pattern.
```xml  
<br /> 1500  
1000  
SequenceQpidReplyFwdInOut  
SequenceQpidFaultFwdInOut  
0 0/1 * 1/1 * ? *  
true
```
Where:
  * SequenceQpidReplyFwdInOut will process Backend message response in blocking-mode (with ACK)
  * SequenceQpidFaultFwdInOut, if there is an exception or Backend service give an error (HTTP_SC error), then this sequence will be executed and any ACK will be send.
2.4.- Check the logs.
```sh  

[2014-11-27 23:46:26,401] INFO - LogMediator [ProxyQpidSender2MsgStoreFwdInOut] MSG STORED = MySysDate 11/27/14 11:46 PM  

[2014-11-27 23:47:00,006] DEBUG - ServerWorker Starting a new Server Worker instance  

[2014-11-27 23:47:00,007] INFO - LogMediator [echoPassThroughProxy] = ----- inSeq ----- = [echoPassThroughProxy]  

[2014-11-27 23:47:00,007] INFO - LogMediator To: /services/echoPassThroughProxy, WSAction: urn:echoString, SOAPAction: urn:echoString, MessageID: urn:uuid:fad450ca-19df-4e36-888d-eed3478e936e, Direction: request, Envelope: MySysDate 11/27/14 11:46 PM  

[2014-11-27 23:47:00,010] DEBUG - ServerWorker Starting a new Server Worker instance  

[2014-11-27 23:47:00,013] INFO - LogMediator [echoPassThroughProxy] = ----- outSeq ----- = [echoPassThroughProxy]  

[2014-11-27 23:47:00,014] INFO - LogMediator To: http://www.w3.org/2005/08/addressing/anonymous, WSAction: , SOAPAction: , MessageID: urn:uuid:b785f6d0-de3c-4941-a799-80a7cdc8c0d0, Direction: response, Envelope:  

[2014-11-27 23:47:00,016] INFO - LogMediator [SequenceQpidReplyFwdInOut] = ---------------------- = [SequenceQpidReplyFwdInOut]  

[2014-11-27 23:47:00,017] INFO - LogMediator To: /services/ProxyQpidSender2MsgStoreFwdInOut.ProxyQpidSender2MsgStoreFwdInOutHttpSoap12Endpoint, WSAction: urn:echoString, SOAPAction: urn:echoString, MessageID: urn:uuid:888E0D71F886F13CF71417131986402876015-666129391, Direction: request, Envelope:  
```

## V. Conclusions
  * Apache Qpid as good alternative to WSO2 Message Broker because you have robustness, integration patterns based messaging implemented, scalability, compatible with JMS and AMQP standard, everything in a lightweight bundle and easy to install it. 
  * WSO2 ESB is perfectly integrated to Apache Qpid, it is not necessary to program or extend some library, all integration can be achieved based on configurations only. No need to write any line of code.
