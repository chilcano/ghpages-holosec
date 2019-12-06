---
layout: post
title: Business Activity Monitoring aplicado a Openbravo ERP usando WSO2 ESB y WSO2
  BAM
date: 2014-03-20 18:16:03.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- ERP
- SOA
tags:
- BAM
- KPI
- WSO2
meta:
  _wpcom_is_markdown: '1'
  _edit_last: '578869'
  _publicize_pending: '1'
  publicize_google_plus_url: https://plus.google.com/113031469837757886298/posts/7T3TtrmMstR
  _wpas_done_5053089: '1'
  _publicize_done_external: a:1:{s:11:"google_plus";a:1:{s:21:"113031469837757886298";b:1;}}
  publicize_twitter_user: Chilcano
  publicize_twitter_url: http://t.co/wxF6QIbuqw
  _wpas_done_13849: '1'
  publicize_linkedin_url: ''
  _wpas_done_5053092: '1'
  _wpas_skip_5053089: '1'
  _wpas_skip_13849: '1'
  _wpas_skip_5053092: '1'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2014/03/20/business-activity-monitoring-aplicado-a-openbravo-erp-usando-wso2-esb-y-wso2-bam/"
---
Mi colega [Luis Peñarrubia](http://www.linkedin.com/in/luispenarrubia "Luis Peñarrubia @ LinkedIn") ha publicado un detallado [post de cómo usar WSO2 BAM, WSO2 ESB para monitorizar la capa de servicio de Openbravo ERP](http://luispenarrubia.wordpress.com/2014/03/15/monitorizar-business-services-usando-wso2-esb-y-wso2-bam/ "Monitorizar Business Services usando WSO2 ESB y WSO2 BAM") (Data Access Layer).

  


Aquí os dejo el documento que explica cómo hacerlo paso a paso:

  


[slideshare id=32323468&doc=chakray-monitorizacion-kpi-service-wso2esb-wso2bam-201403141-140314124059-phpapp02&type=d]

  


Por otro lado, al desplegarlo en mi entorno, WSO2 BAM mostraba errores de autenticación de Apache Thrift con Apache Cassandra, como este:

  


[sourcecode language="text" gutter="true" wraplines="false"] 

  


[2014-03-20 17:31:16,112] INFO {org.wso2.carbon.ntask.core.service.impl.TaskServiceImpl} - Task service starting in STANDALONE mode...  
  
[2014-03-20 17:31:16,285] ERROR {org.wso2.carbon.bam.notification.task.internal.NotificationDispatchComponent} - InvalidRequestException(why:You have not logged in)  
  
me.prettyprint.hector.api.exceptions.HInvalidRequestException: InvalidRequestException(why:You have not logged in)  
  
at me.prettyprint.cassandra.service.ExceptionsTranslatorImpl.translate(ExceptionsTranslatorImpl.java:45)  
  
at me.prettyprint.cassandra.service.ThriftCluster$6.execute(ThriftCluster.java:164)  
  
at me.prettyprint.cassandra.service.ThriftCluster$6.execute(ThriftCluster.java:151)  
  
at me.prettyprint.cassandra.service.Operation.executeAndSetResult(Operation.java:103)  
  
at me.prettyprint.cassandra.connection.HConnectionManager.operateWithFailover(HConnectionManager.java:258)  
  
at me.prettyprint.cassandra.service.ThriftCluster.addKeyspace(ThriftCluster.java:168)

  


[...]

  


[2014-03-20 17:31:16,471] INFO {org.wso2.carbon.databridge.core.DataBridge} - admin connected  
  
[2014-03-20 17:31:16,473] ERROR {org.wso2.carbon.databridge.core.internal.authentication.Authenticator} - wrong userName or password  
  
[2014-03-20 17:31:16,475] ERROR {org.wso2.carbon.bam.notification.task.NotificationDispatchTask} - Error executing notification dispatch task: Cannot borrow client for TCP,10.10.10.24:7613,TCP,10.10.10.24:7713  
  
org.wso2.carbon.databridge.agent.thrift.exception.AgentException: Cannot borrow client for TCP,10.10.10.24:7613,TCP,10.10.10.24:7713  
  
at org.wso2.carbon.databridge.agent.thrift.internal.publisher.authenticator.AgentAuthenticator.connect(AgentAuthenticator.java:58)  
  
at org.wso2.carbon.databridge.agent.thrift.DataPublisher.start(DataPublisher.java:273)  
  
at org.wso2.carbon.databridge.agent.thrift.DataPublisher.<init>(DataPublisher.java:211)  
  
at org.wso2.carbon.bam.notification.task.NotificationDispatchTask.initPublisherKS(NotificationDispatchTask.java:103)  
  
at org.wso2.carbon.bam.notification.task.NotificationDispatchTask.execute(NotificationDispatchTask.java:188)  
  
at org.wso2.carbon.ntask.core.impl.TaskQuartzJobAdapter.execute(TaskQuartzJobAdapter.java:67)  
  
at org.quartz.core.JobRunShell.run(JobRunShell.java:213)  
  
at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:471)  
  
at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:334)  
  
at java.util.concurrent.FutureTask.run(FutureTask.java:166)  
  
at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1146)  
  
at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)  
  
at java.lang.Thread.run(Thread.java:679)  
  
Caused by: org.wso2.carbon.databridge.commons.exception.AuthenticationException: Thrift Authentication Exception  
at org.wso2.carbon.databridge.agent.thrift.internal.publisher.authenticator.ThriftAgentAuthenticator.connect(ThriftAgentAuthenticator.java:49)  
[...]  
[/sourcecode]

Esto se debe a que no he actualizado los ficheros de conexión con Apache Cassandra con las nuevas credenciales de WSO2 BAM, en mi caso no es admin/admin. Entonces, hay actualizar los siguientes ficheros:

[sourcecode language="text" gutter="true" wraplines="false"]  
$ nano /opt/wso2bam-2.4.0/repository/conf/etc/cassandra-auth.xml  
$ nano /opt/wso2bam-2.4.0/repository/conf/datasources/master-datasources.xml  
[/sourcecode]

Saludos.
