---
layout:     post
title:      'WSO2 ESB MessageStore and MessageProcessor implementation for RabbitMQ'
date:       2015-07-14 04:51:58
categories: ['Broker', 'EIP', 'SOA']
tags:       ['RabbitMQ', 'WSO2 ESB']
status:     publish 
permalink:  "/2015/07/14/wso2-esb-messagestore-messageprocessor-rabbitmq/"
---
In this [post](https://holisticsecurity.wordpress.com/2015/01/25/evaluacion-nivel-madurez-integracion-message-brokers-opensource-wso2-esb/) I evaluated the level of integration of WSO2 ESB with different opensource message brokers such as Qpid, RabbitMQ, ActiveMQ and WSO2 Message Broker. At the end, we got the conclusion that RabbitMQ is / was the most used message broker, poorly integrated and not easily integrable with WSO2 ESB.

[![WSO2 MessageStore and MessageProcessor approach for resilient messaging]({{ site.baseurl }}/assets/wso2esb-rabbitmq-message-store-architecture-jms-amqp.png)](https://holisticsecurity.files.wordpress.com/2015/07/wso2esb-rabbitmq-message-store-architecture-jms-amqp.png)  
 _WSO2 MessageStore and MessageProcessor approach for resilient messaging_

<!-- more -->

I ask myself, why is not RabbitMQ integrated to WSO2 ESB?. The answer is simple, RabbitMQ is a Message Broker with strong focus in [AMQP (Advanced Message Queuing Protocol)](https://www.amqp.org) and not in the JMS (Java Messaging Service). JMS as a reference protocol in the Message Brokers is not "complete"; AMQP tries to cover that gap what JMS does not cover. Also, RabbitMQ, by default uses AMQP 0-9-1 and not the latest version AMQP 1.0. Yes, there is an experimental [AMQP 1.0 pluging](http://www.rabbitmq.com/plugins.html) for RabbitMQ and is not suitable for production, also I do not see that it will change soon.
There is an Axis2 Transport Module available what implements the AMQP 0-9-1 protocol for RabbitMQ. This is a "beta" version because is not included as an official Axis2 Transport Module for WSO2 ESB 4.8.1.
But, if you want to install and use it, I highly recommend the serie of post about of Axis2 AMQP Transport published in the Luis Peñarrubia's blog. In that blog is explained how to patch, deploy and use the Axis2 AMQP transport module in your WSO2 ESB 4.8.1:
* https://luispenarrubia.wordpress.com/2014/12/10/integrate-wso2-esb-and-rabbitmq-using-amqp-transport/ 
* https://luispenarrubia.wordpress.com/2015/05/04/how-to-integrate-wso2-esb-and-rabbitmq-using-amqp-transport-part-2/ 

## I. Scope and objetives

The idea of this post is to explain how to integrate WSO2 ESB with RabbitMQ to work together in a "resilient" way and implementing the Enterprise Integration Patterns (EIPs - https://docs.wso2.com/display/IntegrationPatterns/Enterprise+Integration+Patterns+with+WSO2+ESB) related to messaging. Then, I would like to implement different messaging integration patterns as:
* Dead Letter Queue
* Wire Tap
* Retry Queue
* Throttling
For more information about Messaging Advanced EIPs, check this link: https://docs.wso2.com/display/IntegrationPatterns/Messaging+Channels

## II. Implementation

### II.1. The strategia: WSO2 ESB Message Store & Message Processor
The Message Store & Message Processor is the way as the integration between WSO2 ESB and RabbitMQ should be realized. If you want to know what is WSO2 ESB Message Store & Message Processor, I invite you to read my blog post about this subject (https://holisticsecurity.wordpress.com/2014/12/03/wso2-message-broker-vs-apache-qpid-messaging-eip)

### II.2. The strategia: Apache Qpid client library as bridge between JMS 1.1 and AMQP 0-9-1

The strategia is to improve the existing implementation of WSO2 ESB Message Store & Message Processor (MS&MP) to make it compatible with RabbitMQ (AMQP 0-9-1). Create the WSO2 ESB MS&MP for RabbitMQ from scratch is not viable, because the current WSO2 ESB MS&MP implementation uses JMS and is not ready to be extended to AMQP 0-9-1. Then, We need some magical library that can resolve the problem of translating the JMS (WSO2 ESB) protocol to AMQP 0-9-1 (RabbitMQ), I tried different libraries to solve this issue such as:
1. Spring JMS library (https://spring.io/guides/gs/messaging-jms). This requires to refactor big part of the WSO2 MS&MP's source code.
2. Spring JMS and RabbitMQ's JMS connector (http://blog.pivotal.io/pivotal/products/messaging-with-jms-and-rabbitmq). This avoids to refactor part of the source code, but the RabbitMQ's JMS connector (vFabric RabbitMQ's JMS - https://www.vmware.com/support/vfabric-rabbitmq/doc/vfabric-rabbitmq-jms-client-compatibility.html) is not available for my purposes because It is commercial.
3. OpenAMQ JMS client library (http://www.openamq.org). This library implements AMQP and JMS, but sadly is not available any more in GitHub (https://github.com/pieterh/openamq-jms/tree/master) and OpenAMQ web (http://www.openamq.org/forum/t-7591/openamq-jms-released). After digging in internet I think found the new GitHub repository (https://github.com/imatix/openamq-jms). The source code has more than 6 years without being updated.
4. Apache Qpid client library (http://qpid.apache.org/download.html). Was a great surprise found Qpid library, great documentation and great support in their forums. Apache Qpid has 2 client libraries, the firt one (apache-qpid-jms-0.1.0-bin.tar.gz) gives JMS 1.1 and AMQP 1.0 support and the second one (qpid-client-0.32-bin.tar.gz) gives support to JMS 1.1 and AMQP 0-10, 0-9-1, 0-9, and 0-8.
At the end, I used the Apache Qpid client library for its compatibility with JMS 1.1 and AMQP 0-9-1, this is very important because WSO2 ESB MS&MP follow the JMS and this solves the compatibility with RabbitMQ.

### II.3. Understanding how to work Apache Qpid library with RabbitMQ

The idea is to create a simple sender(JMS publisher) and receiver(JMS consumer) client using Apache Qpid library for a RabbitMQ's queue (AMQP). After the great support in the [Apache Qpid's forum](http://mail-archives.apache.org/mod_mbox/qpid-users/), the conclusions were:
1. This sample works if sender(JMS publisher) and receiver(JMS consumer) share the same connection with RabbitMQ but using different URI string definitions. Check below my JNDI configuration file.  
2. JMS Publisher and Consumer should declare the server, virtual host, queue, exchange and routing key following this definition. Why?. After of several definitions, this combination worked !!. Remember, RabbitMQ adds "/" if your Virtual Host is "empty", for this reason I define my Virtual Host as "/" or "/my_virtual_host".

```text  
java.naming.factory.initial = org.apache.qpid.jndi.PropertiesFileInitialContextFactory  

# RabbitMQ v3.4.4  

connectionfactory.myRabbitMQConnectionFactory1 = amqp://usr_chk:CHANGE_ME@clientid/CHAKRAY_POC?brokerlist='tcp://your.rabbitmq.host.name:5672' destination.myJndiDestQueuePublisher1 = BURL:direct://chk.direct//?routingkey='rk.hello_1'  
destination.myJndiDestQueueConsumer1 = BURL:direct://chk.direct/rk.hello_1/q.hello_1  
```

Now If runs this sample JMS client, you should see a message created in RabbitMQ and after 10 seconds this message will be consumed.

```text  
X:\@chilcano\\_workspace\wso2esb-rabbitmq-message-store>mvn exec:java -Dexec.mainClass="com.chakray.chilcano.wso2.rabbitmq.sample.HelloRabbitMQ"  

[INFO] Scanning for projects...  

[INFO]  

[INFO] ------------------------------------------------------------------------  

[INFO] Building wso2esb-rabbitmq-msmp 0.1  

[INFO] ------------------------------------------------------------------------  

[INFO]  

[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ wso2esb-rabbitmq-msmp ---  
SLF4J: The requested version 1.5.6 by your slf4j binding is not compatible with [1.6]  
SLF4J: See http://www.slf4j.org/codes.html#version_mismatch for further details.  
log4j:WARN No appenders could be found for logger (org.apache.qpid.jndi.PropertiesFileInitialContextFactory).  
log4j:WARN Please initialize the log4j system properly.  
Hello RabbitMQ 3.4.4 !!  

[INFO] ------------------------------------------------------------------------  

[INFO] BUILD SUCCESS  

[INFO] ------------------------------------------------------------------------  

[INFO] Total time: 21.290 s  

[INFO] Finished at: 2015-07-05T16:56:31+01:00  

[INFO] Final Memory: 16M/177M  

[INFO] ------------------------------------------------------------------------  
```


[![Publishing and consuming message to/from RabbitMQ from a JMS client]({{ site.baseurl }}/assets/wso2esb-rabbitmq-publish-consume-sample-message.png)](https://holisticsecurity.files.wordpress.com/2015/07/wso2esb-rabbitmq-publish-consume-sample-message.png)_Publishing and consuming message to/from RabbitMQ from a JMS client_

### II.4. First contact with the current WSO2 ESB MS&MP implementation source code

We will use the current source code of the MS&MP for WSO2 ESB 4.8.1. This MS&MP uses JMS and works with JMS MEssage Brokers (ActiveMQ, Qpid, etc.) You can review/download from here <https://svn.wso2.org/repos/wso2/carbon/platform/branches/turing/dependencies/synapse/2.1.2-wso2v6>. I have cloned the current sourcecode of the JMS MessageStore&MessageProcessor implementation, the java classes cloned are:

```text  
synapse/2.1.2-wso2v6/modules/core/src/main/java/org/apache/synapse/message/store/impl/jms/JmsStore.java  
synapse/2.1.2-wso2v6/modules/core/src/main/java/org/apache/synapse/message/store/impl/jms/JmsProducer.java  
synapse/2.1.2-wso2v6/modules/core/src/main/java/org/apache/synapse/message/store/impl/jms/JmsConsumer.java  
```


### II.5. The new WSO2 ESB MS&MP implementation for RabbitMQ

After of download the Apache Qpid library, I used the above sample source code to publish and consume a messages to/from RabbitMQ. This exercise Was necessary to do for a simple reason, because similar changes on the sample will be applied again in the new implementation of WSO2 ESB MS&MP for RabbitMQ. Was necessary to modify just one class (org.apache.synapse.message.store.impl.jms.JmsStore) and at the end, to do easily, I added a Publisher and Consumer instead of using only a Publisher in the new Message Store. I have uploaded my implementation in GitHub, you can use from here <https://github.com/Chilcano/wso2esb-rabbitmq-message-store>. Remember, this implementation is valid for:
* WSO2 ESB 4.8.1
* RabbitMQ 3.4.4
* Java 1.7.0_75 / OpenJDK Runtime Environment (rhel-2.5.4.0.el6_6-x86_64 u75-b13)
* All stuff are installed on CentOS 6.x
**** Disclaimer: This implementation is not suitable for Production. This is just a PoC. ****
I have created a Synapse MessageStore and Synapse API, they are the best way to test this MS&MP implementation. Obviously you need a RabbitMQ configured, do not worry, I have included a json file with the queue, routing keys and exchanges definition required to run this code. https://gist.github.com/4b51ef6b4421328ccc9f 

## III. ToDo

Yes, there are somethings to be improved.
* This code needs be reviewed. This is not suitable to be used in Production.
* Is very imporant to implement stress testing and load testing.
* Documentation.
* The messages stored in RabbitMQ via WSO2 ESB MessageStore mediator will be encoded in "application/java-object-stream".
* User/password used to connect to RabbitMQ should be kept in safe place. Here, WSO2 SecureVault is the perfect tool.

## IV. Conclusions
* The Apache Qpid project provides, IMHO, of a good, robust and open source Message Broker, where is possible implements the different integration patterns based messaging, also, Apache Qpid compatible with JMS and AMQP standard, everything in a lightweight bundle and easy to install it.
* Also, The Apache Qpid project provide of mature client libraries for JMS and different versions of AMQP.
* The MessageStore & MessageProcessor approach is the best way to integrate WSO2 ESB and any Message Broker because with this is possible to implement different integration patterns, suitable for reliable and resilient Critical Messaging Systems.
