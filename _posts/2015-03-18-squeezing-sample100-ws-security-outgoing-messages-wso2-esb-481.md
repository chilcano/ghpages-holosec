---
layout:     post
title:      'Squeezing the Sample 100 (Using WS-Security for Outgoing Messages) of WSO2 ESB 4.8.1'
date:       2015-03-18 00:35:59
categories: ['Security', 'SOA']
tags:       ['WS-Policy', 'WS-Security', 'WSO2 ESB']
status:     publish 
permalink:  "/2015/03/18/squeezing-sample100-ws-security-outgoing-messages-wso2-esb-481/"
---
The [Sample 100: Using WS-Security for Outgoing Messages](https://docs.wso2.com/display/ESB481/Sample+100%3A+Using+WS-Security+for+Outgoing+Messages "Sample 100: Using WS-Security for Outgoing Messages") is a great example to start learning with WS-Security, WS-Policy and how to deal with the security of SOAP webservices ..... but the problem is that this example does not work, also that this example is not very well explained. You could get run successfully this example and not understand what happen behind of it.

![WSO2 ESB 4.8.1 - Sample 100: Using WS-Security for Outgoing Messages]({{ site.baseurl }}/assets/wso2esb-sample100-secure-backend-axis2-01-architecture.png)

<!-- more -->

Anyway, here I will explain how to fix it and I will review each followed step to get it.

## I. The problem.

In the 21st century we still have restricted access to strong cryptography (<http://en.wikipedia.org/wiki/Export_of_cryptography_from_the_United_States>), for this reason the Java compiler required for our applications can not use strong cryptography (large key length, new algorithms or cryptographic providers). In this example we need 2 things:
* To install the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files in our box. If this is done, then my applications (my service or backend implementation in Axis2 and the WSO2 ESB) will work perfectly.
* To share the Bouncy Castle library (cryptographic provider) existing in WSO2 ESB with the Axis2 Client application (not share with Axis2 Server because Axis2 Server and WSO2 ESB already use).
If We do apply the above points, might only execute the secure service deployed in the Axis2 Server with the Axis2 client for this backend. But if We want to expose a unsecure service interface pointing to the secure backend, then we should deploy the WSO2 ESB Proxy correctly. To do that, We should deploy this Proxy in WSO2 ESB and load correctly the policy file (policy_3.xml) using Local 2Entry or the WSO2 ESB Registry. Then, come to do it.

## II. Fixing the sample.

### II.1. Installing Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
I am using `Java 1.7.0_51`, then I should download this JCE files from <http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html>, after that, unzip It and copy (or overwrite) the `local_policy.jar` and `US_export_policy.jar` to `$JAVA_HOME/jre/lib/security`.

```sh  
$ java -version  
java version "1.7.0_51"  
Java(TM) SE Runtime Environment (build 1.7.0_51-b13)  
Java HotSpot(TM) 64-Bit Server VM (build 24.51-b03, mixed mode)
$ echo $JAVA_HOME  
/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home  
```

Remember, by installing the JCE, the WSO2 ESB and Axis2 Server or any Java Application in the same box will not have cryptographic restrictions.

### II.2. Install the Bouncy Castle library required for the Axis2 Client.

By default, the Axis2 Client does not work because the Rampart (Axis2 module) requires an encryption algorithm provided for Bouncy Castle library (`bcprov-jdk15.jar`). To solve it, copy the `$WSO2ESB_HOME/repository/axis2/client/lib/bcprov-jdk15.jar` to `$WSO2ESB_HOME/repository/components/plugins/` folder. If you do not copy this library, probably you get this error when executing the Axis2 Client `StockQuoteClient.java`.

```sh  

[...]  

[java] Using WS-Security  

[java] org.apache.axis2.AxisFault: Error in encryption  

[java] at org.apache.rampart.handler.RampartSender.invoke(RampartSender.java:76)  

[java] at org.apache.axis2.engine.Phase.invokeHandler(Phase.java:340)  

[java] at org.apache.axis2.engine.Phase.invoke(Phase.java:313)  

[java] at org.apache.axis2.engine.AxisEngine.invoke(AxisEngine.java:261)  

[java] at org.apache.axis2.engine.AxisEngine.send(AxisEngine.java:426)  

[java] at org.apache.axis2.description.OutInAxisOperationClient.send(OutInAxisOperation.java:430)  

[java] at org.apache.axis2.description.OutInAxisOperationClient.executeImpl(OutInAxisOperation.java:225)  

[java] at org.apache.axis2.client.OperationClient.execute(OperationClient.java:149)  

[java] at org.apache.axis2.client.ServiceClient.sendReceive(ServiceClient.java:554)  

[java] at org.apache.axis2.client.ServiceClient.sendReceive(ServiceClient.java:530)  

[java] at samples.userguide.StockQuoteClient.executeClient(Unknown Source)  

[java] at samples.userguide.StockQuoteClient.main(Unknown Source)  

[java] Caused by: org.apache.rampart.RampartException: Error in encryption  

[java] at org.apache.rampart.builder.AsymmetricBindingBuilder.doSignBeforeEncrypt(AsymmetricBindingBuilder.java:612)  

[java] at org.apache.rampart.builder.AsymmetricBindingBuilder.build(AsymmetricBindingBuilder.java:97)  

[java] at org.apache.rampart.MessageBuilder.build(MessageBuilder.java:147)  

[java] at org.apache.rampart.handler.RampartSender.invoke(RampartSender.java:65)  

[java] ... 11 more  

[java] Caused by: org.apache.ws.security.WSSecurityException: An unsupported signature or encryption algorithm was used (unsupported key transport encryption algorithm: No such algorithm: http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p); nested exception is:  

[java] java.security.NoSuchAlgorithmException: Cannot find any provider supporting RSA/ECB/OAEPPadding  

[java] at org.apache.ws.security.util.WSSecurityUtil.getCipherInstance(WSSecurityUtil.java:785)  

[java] at org.apache.ws.security.message.WSSecEncryptedKey.prepareInternal(WSSecEncryptedKey.java:205)  

[java] at org.apache.ws.security.message.WSSecEncrypt.prepare(WSSecEncrypt.java:259)  

[java] at org.apache.rampart.builder.AsymmetricBindingBuilder.doSignBeforeEncrypt(AsymmetricBindingBuilder.java:578)  

[java] ... 14 more  

[java] Caused by: java.security.NoSuchAlgorithmException: Cannot find any provider supporting RSA/ECB/OAEPPadding  

[java] at javax.crypto.Cipher.getInstance(Cipher.java:524)  

[java] at org.apache.ws.security.util.WSSecurityUtil.getCipherInstance(WSSecurityUtil.java:777)  

[java] ... 17 more
BUILD SUCCESSFUL  
Total time: 4 seconds  
```


### II.3. Deploying the Proxy Service and Policy correctly in WSO2 ESB.

The Proxy Service used for the Sample 100 is placed in `$WSO2ESB_HOME/repository/samples/synapse_sample_100.xml` and if you open the `synapse_sample_100.xml` you can check that it uses `policy_3.xml` as definition of the Security Policy.  
To deploy it and start the WSO2 ESB we have to execute from `$WSO2ESB_HOME/bin/` the following:

```sh  
$ ./wso2esb-samples.sh -sn 100  
```

After of deploying it, we can check if the Proxy Service was deployed successfully in WSO2 ESB. Open `https://localhost:9443/carbon` and go to Service Bus > Source View, there is an unique Synapse definitions and its content is:

```xml  
<?xml version="1.0" encoding="UTF-8"?>  
<definitions xmlns="http://ws.apache.org/ns/synapse">  
<localEntry key="sec_policy"  
src="file:repository/samples/resources/policy/policy_3.xml"/>  
<sequence name="fault">  
<log level="full">  
<property name="MESSAGE" value="Executing default "fault" sequence"/>  
<property name="ERROR_CODE" expression="get-property('ERROR_CODE')"/>  
<property name="ERROR_MESSAGE" expression="get-property('ERROR_MESSAGE')"/>  
</log>  
<drop/>  
</sequence>  
<sequence name="main">  
<in>  
<send>  
<endpoint name="secure">  
<address uri="http://localhost:9000/services/SecureStockQuoteService">  
<enableAddressing/>  
<enableSec policy="sec_policy"/>  
</address>  
</endpoint>  
</send>  
</in>  
<out>  
<header xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"  
name="wsse:Security"  
action="remove"/>  
<send/>  
</out>  
</sequence>  
</definitions>  
```

Where:  
1\. There is a `localEntry`, where `sec_policy` is the key of the security policy placed in `file:repository/samples/resources/policy/policy_3.xml`.  
2\. The backend service is `http://localhost:9000/services/SecureStockQuoteService`, has `enableAddressing` enabled and also requires the security policy enabled (sec_policy).  
3\. The Proxy Service removes the special Headers related to encryption in the Out Sequence. This is perfect for us because we will see in text plain the SOAP response message.

## III. Executing the Sample 100.

Now, we are ready to run the sample 100.

### III.1. Compile and deploy the SecureStockQuoteService (backend service)


```sh  
$ cd ~/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService  
$ ant  
Buildfile: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/build.xml
clean:  

[delete] Deleting directory /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/temp
init:  

[mkdir] Created dir: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/temp  

[mkdir] Created dir: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/temp/classes
compile-all:  

[javac] /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/build.xml:47: warning: 'includeantruntime' was not set, defaulting to build.sysclasspath=last; set to false for repeatable builds  

[javac] Compiling 9 source files to /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/temp/classes
build-service:  

[mkdir] Created dir: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/temp/SecureStockQuoteService  

[mkdir] Created dir: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/temp/SecureStockQuoteService/META-INF  

[copy] Copying 1 file to /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/temp/SecureStockQuoteService/META-INF  

[copy] Copying 1 file to /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/temp/SecureStockQuoteService  

[copy] Copying 9 files to /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/SecureStockQuoteService/temp/SecureStockQuoteService  

[jar] Building jar: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/SecureStockQuoteService.aar
BUILD SUCCESSFUL  
Total time: 1 second  
```


### III.2. Run the Axis2 Server


```sh  
$ cd ~/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server  
$ ./axis2server.sh  
Using JAVA_HOME: /Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home  
Using AXIS2 Repository : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository  
Using AXIS2 Configuration : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/conf/axis2.xml  
15/03/06 20:38:40 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Starting  

[SimpleAxisServer] Using the Axis2 Repository : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository  

[SimpleAxisServer] Using the Axis2 Configuration File : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/conf/axis2.xml  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/modules/addressing.mar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/modules/rampart.mar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/modules/sandesha2.mar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.addressing_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: wso2caching - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.caching.core_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: ComponentMgtModule - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.feature.mgt.services_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: wso2mex - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.mex_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: pagination - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.registry.server_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: relay - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.relay.module_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.rm_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: POXSecurityModule - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.security.mgt_4.2.2.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: ServerAdminModule - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.server.admin_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: wso2statistics - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.statistics_4.2.2.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: wso2throttle - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.throttle.core_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: usagethrottling - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.throttling.agent_2.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: wso2tracer - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.tracer_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: metering - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.usage.agent_2.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: wso2xfer - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.xfer_4.2.0.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/rampart-core_1.6.1.wso2v12.jar  
15/03/06 20:38:41 INFO deployment.ModuleDeployer: Deploying module: rahas - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/rampart-trust_1.6.1.wso2v12.jar  
15/03/06 20:38:41 ERROR sandesha2.SandeshaModule: Could not load module policies. Using default values.  
15/03/06 20:38:41 INFO config.ClientConnFactoryBuilder: HTTPS Loading Identity Keystore from : ../../repository/resources/security/wso2carbon.jks  
15/03/06 20:38:41 INFO config.ClientConnFactoryBuilder: HTTPS Loading Trust Keystore from : ../../repository/resources/security/client-truststore.jks  
15/03/06 20:38:41 INFO nhttp.HttpCoreNIOSender: HTTPS Sender starting  
15/03/06 20:38:41 INFO nhttp.HttpCoreNIOSender: HTTP Sender starting  
15/03/06 20:38:41 INFO jms.JMSSender: JMS Sender started  
15/03/06 20:38:41 INFO jms.JMSSender: JMS Transport Sender initialized...  
15/03/06 20:38:41 INFO deployment.DeploymentEngine: Deploying Web service: FastStockQuoteService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/FastStockQuoteService.aar  
15/03/06 20:38:41 INFO deployment.DeploymentEngine: Deploying Web service: LBService1.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/LBService1.aar  
15/03/06 20:38:41 INFO deployment.DeploymentEngine: Deploying Web service: LBService2.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/LBService2.aar  
15/03/06 20:38:41 INFO deployment.DeploymentEngine: Deploying Web service: MTOMSwASampleService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/MTOMSwASampleService.aar  
15/03/06 20:38:41 INFO deployment.DeploymentEngine: Deploying Web service: ReliableStockQuoteService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/ReliableStockQuoteService.aar  
15/03/06 20:38:41 INFO deployment.DeploymentEngine: Deploying Web service: SecureStockQuoteService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/SecureStockQuoteService.aar  
15/03/06 20:38:41 INFO deployment.DeploymentEngine: Deploying Web service: SimpleStockQuoteService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/SimpleStockQuoteService.aar  
15/03/06 20:38:42 INFO nhttp.HttpCoreNIOListener: HTTPS Listener started on 0:0:0:0:0:0:0:0:9002  
15/03/06 20:38:42 INFO nhttp.HttpCoreNIOListener: HTTP Listener started on 0:0:0:0:0:0:0:0:9000  
15/03/06 20:38:42 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Started  
```


### III.3. Check if backend was deployed successfully in Axis2 Server

Open Axis2 Server (`http://localhost:9000/services`) to list the deployed services. You should see the following: ![SecureStockQuoteService deployed successfully on Axis2 Server]({{ site.baseurl }}/assets/wso2esb-sample100-secure-backend-axis2-02-axis2server.png) SecureStockQuoteService deployed successfully on Axis2 Server Also, check the WSDL: `http://localhost:9000/services/SecureStockQuoteService?wsdl`

### III.4. Run the Axis2 Client for the Sample 100

The idea behind of this sample is that the Axis2 Client calls to Proxy Service (the `StockQuoteProxy` in WSO2 ESB) and sends a SOAP unsecure message, the Proxy Service receives the SOAP message and adds specials Headers required for the backend and also defined for the `policy_3.xml`.

```sh  
$ cd ~/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client  
$ ant stockquote -Dtrpurl=http://localhost:8280/  
Buildfile: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/build.xml
init:
compile:
stockquote:  

[java] 15/03/07 19:23:46 INFO deployment.DeploymentEngine: No services directory was found under /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/client_repo.  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/client_repo/modules/addressing.mar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/client_repo/modules/rampart.mar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/client_repo/modules/sandesha2.mar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.addressing_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: wso2caching - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.caching.core_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: ComponentMgtModule - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.feature.mgt.services_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: wso2mex - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.mex_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: pagination - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.registry.server_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: relay - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.relay.module_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.rm_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: POXSecurityModule - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.security.mgt_4.2.2.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: ServerAdminModule - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.server.admin_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: wso2statistics - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.statistics_4.2.2.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: wso2throttle - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.throttle.core_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: usagethrottling - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.throttling.agent_2.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: wso2tracer - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.tracer_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: metering - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.usage.agent_2.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: wso2xfer - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.xfer_4.2.0.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/rampart-core_1.6.1.wso2v12.jar  

[java] 15/03/07 19:23:46 INFO deployment.ModuleDeployer: Deploying module: rahas - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/rampart-trust_1.6.1.wso2v12.jar  

[java] 15/03/07 19:23:46 ERROR sandesha2.SandeshaModule: Could not load module policies. Using default values.  

[java] 15/03/07 19:23:46 INFO mail.MailTransportSender: MAILTO Sender started  

[java] 15/03/07 19:23:46 INFO jms.JMSSender: JMS Sender started  

[java] 15/03/07 19:23:46 INFO jms.JMSSender: JMS Transport Sender initialized...  

[java] Standard :: Stock price = $93.14887399516581
BUILD SUCCESSFUL  
Total time: 3 seconds  
```

Lines above you can see the next:

```sh  

[...]  

[java] Standard :: Stock price = $93.14887399516581
BUILD SUCCESSFUL  
Total time: 3 seconds  
```

That indicates that everything goes well.
Now What?. Well, we are ready to understand what are happening behind of this Sample 100.

## IV. What use case are we implementing?

Above, in the first diagram we can see the scenario being implemented with the Sample 100, where we are using a WSO2 Synapse Proxy to hide the complex part related with cryptography. But if we uncover this scenario and do see in depth, then we could play further more. Below in the diagram you will see what we can do further with this sample. ![Going beyond with WSO2 ESB Sample 100]({{ site.baseurl }}/assets/wso2esb-sample100-secure-backend-axis2-03-extended-scenarios.png) Going beyond with WSO2 ESB Sample 100 We could try 3 new flows:
1. Send a SOAP request to SecureStockQuoteService using the new standalone Service Proxy (MyProxySample100) instead of the Synapse Sample 100 definition.
2. Send a SOAP request to SecureStockQuoteService using the existing Axis2 Client (stockquote) using the new Proxy EndPoint.
3. Send a Secure SOAP request directly to SecureStockQuoteService using the existing Axis2 Client (stockquote) using the client_policy_3.xml.
But even you do want to go further, I recommend the blog post of Sagara Gunathunga (Technical Leader of WSO2 Inc). There, Sagara explains in the first part the different use cases to apply WS-Security in Backend: http://ssagara.blogspot.co.uk/2013/07/wso2-esb-set-ws-security-ut-user-names.html I think that this post is great to get starting with WS-Security and WS-Policy in WSO2 ESB and hope this is also useful for you.

## V. Using a standalone Service Proxy instead of run WSO2 ESB with the Synapse definition

WSO2 ESB has a lot of samples, more than 100 synapse samples, you can check them here `$WSO2ESB_HOME/repository/samples/` and here `https://docs.wso2.com/display/ESB481/Samples` for further details. But what I hate about this is run every time WSO2 ESB to load a single sample. Said that, why not convert the existing synapse-sample to a standalone synapse proxy where it can be deployed in runtime. This will avoid restart the WSO2 ESB and update the standalone directly on WSO2 ESB. Well, doing this is easy, below you can see the existing synapse proxy definition (check point II.3 or check synapse_sample_100.xml) and a little below the final standalone proxy.

```xml  
<!-- synapse_sample_100.xml -->  
<!-- Using WS-Security for outgoing messages -->  
<?xml version="1.0" encoding="UTF-8"?>  
<definitions xmlns="http://ws.apache.org/ns/synapse">  
<localEntry key="sec_policy" src="file:repository/samples/resources/policy/policy_3.xml"/>  
<sequence name="main">  
<in>  
<send>  
<endpoint name="secure">  
<address uri="http://localhost:9000/services/SecureStockQuoteService">  
<enableAddressing/>  
<enableSec policy="sec_policy"/>  
</address>  
</endpoint>  
</send>  
</in>  
<out>  
<header xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"  
name="wsse:Security"  
action="remove"/>  
<send/>  
</out>  
</sequence>  
</definitions>  
```

This Synapse Proxy is the new standalone Proxy create from `synapse_sample_100.xml`.

```xml  
<!-- MyProxySample100 created from synapse_sample_100.xml -->  
<!-- Using WS-Security for outgoing messages -->  
<?xml version="1.0" encoding="UTF-8"?>  
<proxy xmlns="http://ws.apache.org/ns/synapse"  
name="MyProxySample100"  
transports="http"  
statistics="disable"  
trace="disable"  
startOnLoad="true">  
<target>  
<inSequence>  
<log level="custom">  
<property name="[MyProxySample100]" value="===== inSeq ===== [MyProxySample100]"/>  
</log>  
<log level="custom">  
<property name="[Body IN]" expression="$body/*[1]"/>  
</log>  
<header name="Action" value="urn:getQuote"/>  
<send>  
<endpoint>  
<address uri="http://localhost:9000/services/SecureStockQuoteService">  
<enableAddressing/>  
<enableSec policy="my_sec_policy"/>  
</address>  
</endpoint>  
</send>  
</inSequence>  
<outSequence>  
<log level="custom">  
<property name="[MyProxySample100]" value="===== outSeq ===== [MyProxySample100]"/>  
</log>  
<log level="custom">  
<property name="[Header OUT]" expression="$header/*[1]"/>  
</log>  
<log level="custom">  
<property name="[Body OUT]" expression="$body/*[1]"/>  
</log>  
<header xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"  
name="wsse:Security"  
action="remove"/>  
<send/>  
</outSequence>  
</target>  
<description>Proxy created from synapse_sample_100.xml</description>  
</proxy>  
```

I have changed/updated the following:
* `in` for `inSequence`.
* `out` for `outSequence`.
* Added a reference for Security Policy definition to be used for `MyProxySample100.xml`. In this Proxy, the Security Policy is loaded as a Local Entry called `my_sec_policy`.

```xml  
<localEntry key="my_sec_policy" src="file:repository/samples/resources/policy/policy_3.xml"/>  
```

Also this could be also loaded as a resource stored in WSO2 ESB Local Registry.  
* Added a Header Property for SOAP Action.

```xml  
<header name="Action" value="urn:getQuote"/>  
```
* Added a Log Mediator for tracking purposes.

## VI. Calling to the Backend from SoapUI using the new Proxy

Using the new proxy, we gain flexibility and reduce complexity when creating SOAP messages where the backend requires WS-Security. Now, we can create a simple SOAP request message and the WSO2 ESB will add the required WS-Security headers and also will remove WS-Security headers to the SOAP response message. Then, create a new Project in SoapUI using the WSDL of Backend, remember, the backend is deployed in Axis2 Server and its WSDL is `http://localhost:9000/services/SecureStockQuoteService?wsdl`, for further details check the point III.3. (Check if backend was deployed successfully in Axis2 Server). The idea is to know how to create SOAP request messages to be sent to backend (Axis2 Server). ![]({{ site.baseurl }}/assets/wso2esb-sample100-secure-backend-axis2-04-soapui-axis2.png) Below you can see the request and response messages sent and received from backend.

![]({{ site.baseurl }}/assets/wso2esb-sample100-secure-backend-axis2-05-soapui-axis2-req-resp.png)
And in the Axis2 Server side you will get an error as shown below:

```sh  
15/03/15 12:42:18 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Started  
15/03/15 12:42:26 ERROR engine.AxisEngine: Missing wsse:Security header in request  
org.apache.axis2.AxisFault: Missing wsse:Security header in request  
at org.apache.rampart.handler.RampartReceiver.setFaultCodeAndThrowAxisFault(RampartReceiver.java:180)  
at org.apache.rampart.handler.RampartReceiver.invoke(RampartReceiver.java:99)  
at org.apache.axis2.engine.Phase.invokeHandler(Phase.java:340)  
at org.apache.axis2.engine.Phase.invoke(Phase.java:313)  
at org.apache.axis2.engine.AxisEngine.invoke(AxisEngine.java:261)  
at org.apache.axis2.engine.AxisEngine.receive(AxisEngine.java:167)  
at org.apache.axis2.transport.http.HTTPTransportUtils.processHTTPPostRequest(HTTPTransportUtils.java:172)  
at org.apache.synapse.transport.nhttp.ServerWorker.processEntityEnclosingMethod(ServerWorker.java:459)  
at org.apache.synapse.transport.nhttp.ServerWorker.run(ServerWorker.java:279)  
at org.apache.axis2.transport.base.threads.NativeWorkerPool$1.run(NativeWorkerPool.java:172)  
at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)  
at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)  
at java.lang.Thread.run(Thread.java:744)  
Caused by: org.apache.rampart.RampartException: Missing wsse:Security header in request  
at org.apache.rampart.RampartEngine.process(RampartEngine.java:146)  
at org.apache.rampart.handler.RampartReceiver.invoke(RampartReceiver.java:92)  
... 11 more  
15/03/15 12:42:26 ERROR nhttp.ServerWorker: Error processing POST request  
org.apache.axis2.AxisFault: Missing wsse:Security header in request  
at org.apache.rampart.handler.RampartReceiver.setFaultCodeAndThrowAxisFault(RampartReceiver.java:180)  
at org.apache.rampart.handler.RampartReceiver.invoke(RampartReceiver.java:99)  
at org.apache.axis2.engine.Phase.invokeHandler(Phase.java:340)  
at org.apache.axis2.engine.Phase.invoke(Phase.java:313)  
at org.apache.axis2.engine.AxisEngine.invoke(AxisEngine.java:261)  
at org.apache.axis2.engine.AxisEngine.receive(AxisEngine.java:167)  
at org.apache.axis2.transport.http.HTTPTransportUtils.processHTTPPostRequest(HTTPTransportUtils.java:172)  
at org.apache.synapse.transport.nhttp.ServerWorker.processEntityEnclosingMethod(ServerWorker.java:459)  
at org.apache.synapse.transport.nhttp.ServerWorker.run(ServerWorker.java:279)  
at org.apache.axis2.transport.base.threads.NativeWorkerPool$1.run(NativeWorkerPool.java:172)  
at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)  
at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)  
at java.lang.Thread.run(Thread.java:744)  
Caused by: org.apache.rampart.RampartException: Missing wsse:Security header in request  
at org.apache.rampart.RampartEngine.process(RampartEngine.java:146)  
at org.apache.rampart.handler.RampartReceiver.invoke(RampartReceiver.java:92)  
... 11 more  
```

This happens because we are sending a SOAP request message without WS-Security Header well formatted. Keys to hashing, timestamps, etc. are missing, this is the reason of the Proxy in WSO2 ESB, it solves this cryptography issues.
Well, now we will use SoapUI to send a SOAP request to backend via Synapse Proxy. Before, we will add the WSO2 ESB Proxy EndPoint or WSDL to SoapUI project and send and receive the messages.  
Remember, the new WSO2 ESB Proxy is `MyProxySample100` and has this WSDL `http://localhost:8280/services/MyProxySample100?wsdl` and this EndPoint `http://localhost:8280/services/MyProxySample100`.

![]({{ site.baseurl }}/assets/wso2esb-sample100-secure-backend-axis2-06-soapui-proxy.png)  
After adding a new EndPoint / WSDL, now if you send a
SOAP request from SoapUI, you will receive a plain (unsecure) SOAP response from the new Proxy, as shown below:  

![]({{ site.baseurl }}/assets/wso2esb-sample100-secure-backend-axis2-07-soapui-proxy-ok.png)
The successfully messages in WSO2 ESB side:

```sh  
2015-03-18 00:02:22,752] INFO - LogMediator [MyProxySample100] = ===== inSeq ===== [MyProxySample100]  

[2015-03-18 00:02:22,754] INFO - LogMediator [Body IN] = <m0:getQuote xmlns:m0="http://services.samples">  
<m0:request>  
<m0:symbol>IBM</m0:symbol>  
</m0:request>  
</m0:getQuote>  

[2015-03-18 00:02:22,807] INFO - LogMediator [MyProxySample100] = ===== outSeq ===== [MyProxySample100]  

[2015-03-18 00:02:22,808] INFO - LogMediator [Header OUT] = <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" soapenv:mustUnderstand="1"><wsu:Timestamp xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="Timestamp-49"><wsu:Created>2015-03-18T00:02:22.787Z</wsu:Created><wsu:Expires>2015-03-18T00:07:22.787Z</wsu:Expires></wsu:Timestamp><xenc:EncryptedKey xmlns:xenc="http://www.w3.org/2001/04/xmlenc#" Id="EncKeyId-6AFA2EA8E349AA0F54142663694279285"><xenc:EncryptionMethod Algorithm="http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p"></xenc:EncryptionMethod><ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">  
<wsse:SecurityTokenReference><wsse:KeyIdentifier EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary" ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509SubjectKeyIdentifier">Xeg55vRyK3ZhAEhEf+YT0z986L0=</wsse:KeyIdentifier></wsse:SecurityTokenReference>  
</ds:KeyInfo><xenc:CipherData><xenc:CipherValue>MKh6EptMTgOBuw8eMwGpx+g2tzI2JjeBXGsjNCp5q4V9PC1mP0KxiHc4Kw9kF3iycKY9+Xfo+SMLbt7cVumAxylpiNy4lrlNwBDL/P3dBzDIi/mPG3OkuGS6TD/qz0v3ggQDi6QnzvJ97ZvGEUX+szQ/onnsAmtjIVSuOoYBehU=</xenc:CipherValue></xenc:CipherData><xenc:ReferenceList><xenc:DataReference URI="#EncDataId-51"></xenc:DataReference></xenc:ReferenceList></xenc:EncryptedKey><ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#" Id="Signature-50">  
<ds:SignedInfo>  
<ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"></ds:CanonicalizationMethod>  
<ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"></ds:SignatureMethod>  
<ds:Reference URI="#Id-1182881922">  
<ds:Transforms>  
<ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"></ds:Transform>  
</ds:Transforms>  
<ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"></ds:DigestMethod>  
<ds:DigestValue>81sPsUuNFAhrmLmb4nbfBDbNuj0=</ds:DigestValue>  
</ds:Reference>  
<ds:Reference URI="#Timestamp-49">  
<ds:Transforms>  
<ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"></ds:Transform>  
</ds:Transforms>  
<ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"></ds:DigestMethod>  
<ds:DigestValue>UlzoM6XGNsWviJUaLGYjj/WqaGk=</ds:DigestValue>  
</ds:Reference>  
</ds:SignedInfo>  
<ds:SignatureValue>  
QAkjh4QqmkGNdjg1kGphtKtpDXPy9DOfA8e53bCU4h7jc+bi6tKTxJ1Ld3N7rfWxCb0dNnkB/2Mt  
P3ZrLqFBUSOrZUVx4faHBoX2Wyd0CVDMgT+S9XxDN+y88Bkyftep1I3j77rvX0eoTwbCrcoVB/KY  
OruuM37y92qDjrI6sew=  
</ds:SignatureValue>  
<ds:KeyInfo Id="KeyId-6AFA2EA8E349AA0F54142663694278882">  
<wsse:SecurityTokenReference xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="STRId-6AFA2EA8E349AA0F54142663694278883"><wsse:KeyIdentifier EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary" ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509SubjectKeyIdentifier">CuJdE1B2dUFd1dkLZSzQ5vj6MYg=</wsse:KeyIdentifier></wsse:SecurityTokenReference>  
</ds:KeyInfo>  
</ds:Signature></wsse:Security>  

[2015-03-18 00:02:22,809] INFO - LogMediator [Body OUT] = <ns:getQuoteResponse xmlns:ns="http://services.samples"><ns:return xmlns:ax23="http://services.samples/xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ax23:GetQuoteResponse"><ax23:change>-2.427092238886098</ax23:change><ax23:earnings>-8.594640198504722</ax23:earnings><ax23:high>-85.41697924761516</ax23:high><ax23:last>85.45769312029174</ax23:last><ax23:lastTradeTimestamp>Wed Mar 18 00:02:22 GMT 2015</ax23:lastTradeTimestamp><ax23:low>88.84901052393411</ax23:low><ax23:marketCap>-5312065.376447097</ax23:marketCap><ax23:name>IBM Company</ax23:name><ax23:open>89.06565721533335</ax23:open><ax23:peRatio>-17.80304620448777</ax23:peRatio><ax23:percentageChange>3.0694536514961186</ax23:percentageChange><ax23:prevClose>-79.07245114136454</ax23:prevClose><ax23:symbol>IBM</ax23:symbol><ax23:volume>9040</ax23:volume></ns:return></ns:getQuoteResponse>  
```

And the successfully messages in Axis2 Server side:

```sh  
Wed Mar 18 00:02:22 GMT 2015 SecureStockQuoteService :: Generating quote for : IBM  
```


## VII. Calling to Backend directly from the Axis2 Client

Sometimes It is necessary to call directly to the secured backend without using WSO2 Proxy Service because maybe I like to know how to compose a request message using WS-Security or to check the health of this service or simply to learn how to create a Axis2 Client using WS-Security.  
So if that is the case, below I explain how to run the Axis2 Client to call to the secured backend and setting a new security policy and EndPoint.
It is very important for this section you have deployed the Bouncly Castle library (cryptographic provider) to avoid errors.  
After that, run the Axis2 Server.

```sh  
$ cd ~/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server  
$ ./axis2server.sh  
```

...and now, run the Axis2 client.

```sh  
$ cd ~/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client  
$ ant stockquote -Daddurl=http://localhost:9000/services/SecureStockQuoteService -Dmode=quote -Dsymbol=IBM -Dpolicy=../../repository/samples/resources/policy/client_policy_3.xml  
```

Where:
* `-Daddurl=http://localhost:9000/services/SecureStockQuoteService` is the secured backend deployed in Axis2 Server. 
* `-Dmode=quote` with this parameter the Axis2 Client will call the SOAP Action / Operation correctly. 
* `-Dsymbol=IBM` with this the Axis2 Client will create a adequate payload expected for the backend. 
* `-Dpolicy=../../repository/samples/resources/policy/client_policy_3.xml` is the new security policy suitable for this example because has the correct paths a configurations. 
Above you can see `client_policy_3.xml`, where the unique difference with `policy_3.xml` is the line 62 (the path `repository/samples/resources/security/store.jks`).

```xml  
<wsp:Policy wsu:Id="SigEncr"  
xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"  
xmlns:wsp="http://schemas.xmlsoap.org/ws/2004/09/policy">  
<wsp:ExactlyOne>  
<wsp:All>  
<sp:AsymmetricBinding xmlns:sp="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy">  
<wsp:Policy>  
<sp:InitiatorToken>  
<wsp:Policy>  
<sp:X509Token  
sp:IncludeToken="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/AlwaysToRecipient">  
<wsp:Policy>  
<sp:WssX509V3Token10/>  
</wsp:Policy>  
</sp:X509Token>  
</wsp:Policy>  
</sp:InitiatorToken>  
<sp:RecipientToken>  
<wsp:Policy>  
<sp:X509Token  
sp:IncludeToken="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/Never">  
<wsp:Policy>  
<sp:WssX509V3Token10/>  
</wsp:Policy>  
</sp:X509Token>  
</wsp:Policy>  
</sp:RecipientToken>  
<sp:AlgorithmSuite>  
<wsp:Policy>  
<sp:Basic256/>  
</wsp:Policy>  
</sp:AlgorithmSuite>  
<sp:Layout>  
<wsp:Policy>  
<sp:Strict/>  
</wsp:Policy>  
</sp:Layout>  
<sp:IncludeTimestamp/>  
<sp:OnlySignEntireHeadersAndBody/>  
</wsp:Policy>  
</sp:AsymmetricBinding>  
<sp:Wss10 xmlns:sp="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy">  
<wsp:Policy>  
<sp:MustSupportRefKeyIdentifier/>  
<sp:MustSupportRefIssuerSerial/>  
</wsp:Policy>  
</sp:Wss10>  
<sp:SignedParts xmlns:sp="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy">  
<sp:Body/>  
</sp:SignedParts>  
<sp:EncryptedParts xmlns:sp="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy">  
<sp:Body/>  
</sp:EncryptedParts>  
<ramp:RampartConfig xmlns:ramp="http://ws.apache.org/rampart/policy">  
<ramp:user>alice</ramp:user>  
<ramp:encryptionUser>bob</ramp:encryptionUser>  
<ramp:passwordCallbackClass>samples.userguide.PWCallback</ramp:passwordCallbackClass>  
<ramp:signatureCrypto>  
<ramp:crypto provider="org.apache.ws.security.components.crypto.Merlin">  
<ramp:property name="org.apache.ws.security.crypto.merlin.keystore.type">JKS</ramp:property>  
<ramp:property name="org.apache.ws.security.crypto.merlin.file">  
../../repository/samples/resources/security/store.jks  
</ramp:property>  
<ramp:property name="org.apache.ws.security.crypto.merlin.keystore.password">password  
</ramp:property>  
</ramp:crypto>  
</ramp:signatureCrypto>  
<ramp:encryptionCypto>  
<ramp:crypto provider="org.apache.ws.security.components.crypto.Merlin">  
<ramp:property name="org.apache.ws.security.crypto.merlin.keystore.type">JKS</ramp:property>  
<ramp:property name="org.apache.ws.security.crypto.merlin.file">  
../../repository/samples/resources/security/store.jks  
</ramp:property>  
<ramp:property name="org.apache.ws.security.crypto.merlin.keystore.password">password  
</ramp:property>  
</ramp:crypto>  
</ramp:encryptionCypto>  
</ramp:RampartConfig>  
</wsp:All>  
</wsp:ExactlyOne>  
</wsp:Policy>  
```

If everything goes well, the following is shown:

```sh  
Buildfile: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/build.xml
init:
compile:
stockquote:  

[java] 15/03/09 23:15:57 INFO deployment.DeploymentEngine: No services directory was found under /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/client_repo.  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/client_repo/modules/addressing.mar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/client_repo/modules/rampart.mar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Client/client_repo/modules/sandesha2.mar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: addressing - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.addressing_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: wso2caching - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.caching.core_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: ComponentMgtModule - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.feature.mgt.services_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: wso2mex - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.mex_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: pagination - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.registry.server_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: relay - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.relay.module_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: sandesha2 - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.rm_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: POXSecurityModule - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.security.mgt_4.2.2.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: ServerAdminModule - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.server.admin_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: wso2statistics - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.statistics_4.2.2.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: wso2throttle - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.throttle.core_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: usagethrottling - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.throttling.agent_2.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: wso2tracer - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.tracer_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: metering - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.usage.agent_2.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: wso2xfer - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/org.wso2.carbon.xfer_4.2.0.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: rampart - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/rampart-core_1.6.1.wso2v12.jar  

[java] 15/03/09 23:15:57 INFO deployment.ModuleDeployer: Deploying module: rahas - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/repository/components/plugins/rampart-trust_1.6.1.wso2v12.jar  

[java] 15/03/09 23:15:57 ERROR sandesha2.SandeshaModule: Could not load module policies. Using default values.  

[java] Using WS-Security  

[java] 15/03/09 23:15:57 INFO mail.MailTransportSender: MAILTO Sender started  

[java] 15/03/09 23:15:57 INFO jms.JMSSender: JMS Sender started  

[java] 15/03/09 23:15:57 INFO jms.JMSSender: JMS Transport Sender initialized...  

[java] Standard :: Stock price = $67.01769988611447
BUILD SUCCESSFUL  
Total time: 3 seconds  
```

And in the Axis2 Server side is shown:

```sh  
$ ./axis2server.sh  
Using JAVA_HOME: /Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home  
Using AXIS2 Repository : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository  
Using AXIS2 Configuration : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/conf/axis2.xml  
15/03/09 23:15:19 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Starting  

[SimpleAxisServer] Using the Axis2 Repository : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository  

[SimpleAxisServer] Using the Axis2 Configuration File : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/conf/axis2.xml  

[...]  
15/03/09 23:15:19 INFO config.ClientConnFactoryBuilder: HTTPS Loading Identity Keystore from : ../../repository/resources/security/wso2carbon.jks  
15/03/09 23:15:19 INFO config.ClientConnFactoryBuilder: HTTPS Loading Trust Keystore from : ../../repository/resources/security/client-truststore.jks  
15/03/09 23:15:20 INFO nhttp.HttpCoreNIOSender: HTTPS Sender starting  
15/03/09 23:15:20 INFO nhttp.HttpCoreNIOSender: HTTP Sender starting  
15/03/09 23:15:20 INFO jms.JMSSender: JMS Sender started  

[...]  
15/03/09 23:15:20 INFO deployment.DeploymentEngine: Deploying Web service: SecureStockQuoteService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/SecureStockQuoteService.aar  
15/03/09 23:15:21 INFO nhttp.HttpCoreNIOListener: HTTPS Listener started on 0:0:0:0:0:0:0:0:9002  
15/03/09 23:15:21 INFO nhttp.HttpCoreNIOListener: HTTP Listener started on 0:0:0:0:0:0:0:0:9000  
15/03/09 23:15:21 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Started  
Mon Mar 09 23:15:59 GMT 2015 SecureStockQuoteService :: Generating quote for : IBM  
```


## IIX. Conclusions

I hope this has been useful and you were able to understand what is happening behind the execution of this example and can improve the security of their web services.  
Now, you are ready to play with:
* WS-Security with UserToken over Transport.
* Creation of WS-Policy for other scenarios: digital signature, encryption, etc.
* Security in REST Services.
* Learn about the importance of the HandlerCallbacks for Apache Rampart.
