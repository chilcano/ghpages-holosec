---
layout: post
title: 'Everything generates data: Capturing WIFI anonymous traffic using Raspberry
  Pi and WSO2 BAM (Part II)'
date: 2016-02-04 18:22:57.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Big Data
- IoT
- Security
tags:
- Cassandra
- CEP
- Kismet
- Privacy
- Raspberry Pi
meta:
  _wpcom_is_markdown: '1'
  _edit_last: '578869'
  geo_public: '0'
  _publicize_job_id: '19464055860'
  publicize_google_plus_url: https://plus.google.com/+RogerCarhuatocto/posts/FHhvLnyzkpw
  _publicize_done_5110107: '1'
  _wpas_done_5053089: '1'
  publicize_linkedin_url: https://www.linkedin.com/updates?discuss=&scope=6985267&stype=M&topic=6101062206772826113&type=U&a=OSXn
  _publicize_done_5110110: '1'
  _wpas_done_5053092: '1'
  _publicize_done_external: a:1:{s:7:"twitter";a:1:{i:13849;s:54:"https://twitter.com/Chilcano/status/695296591981256704";}}
  _publicize_done_17477: '1'
  _wpas_done_13849: '1'
  publicize_twitter_user: Chilcano
  _wpas_skip_5053089: '1'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2016/02/04/everything-generates-data-capturing-wifi-anonymous-traffic-using-raspberry-pi-and-wso2-bam-part-ii/"
---
After configuring the Raspberry Pi to capture WIFI/802.11 traffic ([first blog post](https://holisticsecurity.wordpress.com/2016/02/02/everything-generates-data-capturing-wifi-anonymous-traffic-raspberrypi-wso2-part-i)), we have to store this traffic in a Database (NoSQL and RDBMS).  
  
Because, the idea is to process in real-time and/or batch the stored data.

  
To capture this type of traffic (WIFI/802.11 traffic) is so difficult for next reasons:

  
  

  * Kismet captures 802.11 layer-2 wireless network traffic (Network IP blocks such as TCP, UDP, ARP, and DHCP packets) what should be decoded.
  

  * The traffic should be captured and stored in real-time, we have to use a protocol optimized to capture quickly and low latency.
  

  * The library that implements that protocol should have low memory footprint, because Kismet will run in a Raspberry Pi.
  

  * The protocol to be used should be developer-friendly in both sides (Raspberry Pi side and WSO2 BAM - Apache Cassandra side).
  

  
Well, in this second blog post I will explain how to solve above difficults.  
  
![Architecture IoT/BigData – Storing WIFI traffic in Apache Cassandra \(WSO2 BAM and Apache Thrift\)]({{ site.baseurl }}/assets/chilcano-02-raspberrypi-bigdata-wifi-thrift-1-architecture.png)  
  
 _Architecture IoT/BigData – Storing WIFI traffic in Apache Cassandra (WSO2 BAM and Apache Thrift)_

  
<!-- more -->

  
## I.- Looking for the Streaming and/or Communication Protocol

  
There are some stream and communication protocols and implementations

  
Really, there are many libraries and streaming protocols out there to solve the above issues, but if you are looking for a protocol/library open source, lightweight, low memory footprint and developer friendly there are a few. They are:

  
 **1) Elastic Logstash (https://www.elastic.co/products/logstash)**

  
Logstash is a set of tools to collect heterogeneous type of data and It's to used with Elasticsearch, It requires Java and for this reason It is too heavy to run in a Raspberry Pi. The best choice is to use only `Logstash Forwarder`.  
  
[`Logstash Forwarder` (a.k.a. `lumberjack`)](https://github.com/elastic/logstash-forwarder) is the protocol used to ship, parse and collect streams or log-events when using ELK.  
  
`Logstash Forwarder` can be downloaded and compiled using the Go compiler on your Raspberry Pi, [for further information you can use this link](http://michaelblouin.ca/blog/2015/06/08/build-run-logstash-forwarder-rasperry-pi).

  
 **2) Elastic Filebeat (https://github.com/elastic/beats/tree/master/filebeat)**

  
>   
>  Filebeat is a lightweight, open source shipper for log file data. As the next-generation [`Logstash Forwarder`](https://github.com/elastic/logstash-forwarder), Filebeat tails logs and  
>   
>  quickly sends this information to Logstash for further parsing and enrichment or to Elasticsearch for centralized storage and analysis.  
> 

  
Installing and configuring `Filebeat` is easy and you can use It with Logstash to perform additional processing on the data collected and the `Filebeat` replaces `Logstash Forwarder`.

  
 **3) Apache Flume (https://flume.apache.org)**

  
>   
>  Flume is a distributed, reliable, and available service for efficiently collecting, aggregating, and moving large amounts of  
>   
>  log data. It has a simple and flexible architecture based on streaming data flows. It is robust and fault tolerant with tunable  
>   
>  reliability mechanisms and many failover and recovery mechanisms. It uses a simple extensible data model that allows for online  
>   
>  analytic application.  
> 

`Apache Flume` used Java and requires high (memory and CPU) resources.

**4) Mozilla Heka (https://github.com/mozilla-services/heka)**

> Heka is an open source stream processing software system developed by Mozilla. Heka is a “Swiss Army Knife” type tool for data  
>  processing, useful for a wide variety of different tasks, such as:
> 
>   * Loading and parsing log files from a file system.
>   * Accepting statsd type metrics data for aggregation and forwarding to upstream time series data stores such as graphite or InfluxDB.
>   * Launching external processes to gather operational data from the local system.
>   * Performing real time analysis, graphing, and anomaly detection on any data flowing through the Heka pipeline.
>   * Shipping data from one location to another via the use of an external transport (such as AMQP) or directly (via TCP).
>   * Delivering processed data to one or more persistent data stores.
> 
`Mozilla Heka` is very similar to `Logstash Forwarder`, both are written in Go, but `Mozilla Heka` can process the log-events in real-time also Heka is also able to provide graphs of this data directly, those are great advantages. These graphs will be updated in real time, as the data is flowing through Heka, without the latency of the data store driven graphs.

**5) Fluentd (https://github.com/fluent/fluentd)**

> Fluentd is similar to Logstash in that there are inputs and outputs for a large variety of sources and destination. Some of it’s design tenets  
>  are easy installation and small footprint. It doesn’t provide any storage tier itself but allows you to easily configure where your logs should  
>  be collected. 

**6) Apache Thrift (https://thrift.apache.org)**

> Thrift is an interface definition language and binary communication protocol[1] that is used to define and create services for numerous languages.  
>  It is used as a remote procedure call (RPC) framework and was developed at Facebook for "scalable cross-language services development". It  
>  combines a software stack with a code generation engine to build services that work efficiently to a varying degree and seamlessly between C#,  
>  C++ (on POSIX-compliant systems), Cappuccino, Cocoa, Delphi, Erlang, Go, Haskell, Java, Node.js, OCaml, Perl, PHP, Python, Ruby and Smalltalk.  
>  Although developed at Facebook, it is now an open source project in the Apache Software Foundation. 

[`Facebook Scribe`](https://github.com/facebookarchive/scribe) is a project what uses the `Thrift` protocol and is a server for aggregating log data streamed in real time from a large number of servers.

In this Proof-of-Concept I will use Apache Thrift for these reasons:

  * Apache Thrift is embedded in WSO2 BAM 2.5.0.
  * The WSO2 BAM 2.5.0 is a very important component because also It embeds Apache Cassandra to persist the data stream/log-events. You don't need to do anything, all log-events captured will be stored automatically in Apache Cassandra.
  * There are lightweight Python libraries implementing the Apache Thrift protocol, this [Thrift Python Client](https://github.com/wso2-incubator/iot-server-appliances/tree/master/Arduino%20Robot/PC_Clients/PythonRobotController/DirectPublishClient/BAMPythonPublisher) is suitable to be used in a Raspberry Pi and publish events int WSO2 BAM (Apache Cassandra).
  * And finally, there is a [Python Client Library specific for Kismet](https://github.com/PaulMcMillan/kismetclient). This Python Kismet Client reads the traffic captured for Kismet.

## II.- Installing, configuring and running Python Kismet Client and Python Thrift library

I cloned the above repositories ([Thrift Python Client](https://github.com/chilcano/iot-server-appliances/tree/master/Arduino%20Robot/PC_Clients/PythonRobotController/DirectPublishClient/BAMPythonPublisher) and [Python Kismet Client](https://github.com/chilcano/kismetclient)).

```sh  
$ mkdir kismet_to_wso2bam  
$ cd kismet_to_wso2bam

// Install svn client, It's useful to download a folder from a Github repo  
$ sudo apt-get install subversion  
```

Replace `tree/master` for `trunk` in the URL and checkout the folder.

```sh  
// List files and subfolders  
$ svn ls https://github.com/chilcano/iot-server-appliances/trunk/Arduino%20Robot/PC_Clients/PythonRobotController/DirectPublishClient/BAMPythonPublisher  
.gitignore  
BAMPublisher.py  
Publisher.py  
PythonClient.py  
README.md  
gen-py/  
thrift/

// Download files and subfolder  
$ svn checkout https://github.com/chilcano/iot-server-appliances/trunk/Arduino%20Robot/PC_Clients/PythonRobotController/DirectPublishClient/BAMPythonPublisher  
```

Now, download the `kismetclient` repository.

```sh  
$ git clone https://github.com/chilcano/kismetclient  
Cloning into 'kismetclient'...  
remote: Counting objects: 100, done.  
remote: Total 100 (delta 0), reused 0 (delta 0), pack-reused 100  
Receiving objects: 100% (100/100), 15.84 KiB, done.  
Resolving deltas: 100% (57/57), done.  
```

**2.1) Creating a custom Python script to send the Kismet captured traffic to WSO2 BAM 2.5.0**

Under `kismet_to_wso2bam` folder create this Python ([sendTrafficFromKismetToWSO2BAM.py](https://github.com/chilcano/wso2bam-wifi-thrift-cassandra-poc/tree/master/raspberrypi_wifi_traffic_capture/sendTrafficFromKismetToWSO2BAM.py)) script.

```python  
#!/usr/bin/env python  
"""  
Python script to send 802.11 traffic captured for Kismet to WSO2 BAM 2.5.0.

Author: Chilcano  
Date: 2015/12/31  
Version: 1.0

Requires:  
\- Python Thrift Client (https://github.com/chilcano/iot-server-appliances/tree/master/Arduino%20Robot/PC_Clients/PythonRobotController/DirectPublishClient/BAMPythonPublisher)  
\- Python Kismet Client (https://github.com/chilcano/kismetclient)  
\- Place the 'sendTrafficFromKismetToWSO2BAM.py' in same level of 'BAMPythonPublisher' and 'kismetclient' folders.

Run:  
$ python sendTrafficFromKismetToWSO2BAM.py  
"""

import sys  
sys.path.append('kismetclient')  
sys.path.append('BAMPythonPublisher')  
sys.path.append('BAMPythonPublisher/gen-py')  
sys.path.append('BAMPythonPublisher/thrift')

from kismetclient import Client as KismetClient  
from kismetclient import handlers  
from Publisher import *  
from pprint import pprint

import logging  
import time

log = logging.getLogger('kismetclient')  
log.addHandler(logging.StreamHandler())  
log.setLevel(logging.DEBUG)

# Kismet server  
address = ('127.0.0.1', 2501)  
k = KismetClient(address)  
##k.register_handler('TRACKINFO', handlers.print_fields)

# BAM/CEP/Thrift Server  
cep_ip = '192.168.1.43' # IP address of the server  
cep_port = 7713 # Thrift listen port of the server  
cep_username = 'admin' # username  
cep_password = 'admin' # passowrd

# Initialize publisher with ip and port of server  
publisher = Publisher()  
publisher.init(cep_ip, cep_port)

# Connect to server with username and password  
publisher.connect(cep_username, cep_password)

# Define the Input Stream  
streamDefinition = "{ 'name':'rpi_kismet_stream_in', 'version':'1.0.0', 'nickName': 'rpi_k_in', 'description': '802.11 passive packet capture', 'tags': ['RPi 2 Model B', 'Kismet', 'Thrift'], 'metaData':[ {'name':'ipAdd','type':'STRING'},{'name':'deviceType','type':'STRING'},{'name':'owner','type':'STRING'}, {'name':'bssid','type':'STRING'}], 'payloadData':[ {'name':'macAddress','type':'STRING'}, {'name':'type','type':'STRING'}, {'name':'llcpackets','type':'STRING'}, {'name':'datapackets','type':'STRING'}, {'name':'cryptpackets','type':'STRING'},{'name':'signal_dbm','type':'STRING'}, {'name':'bestlat','type':'STRING'}, {'name':'bestlon','type':'STRING'}, {'name':'bestalt','type':'STRING'}, {'name':'channel','type':'STRING'}, {'name':'datasize','type':'STRING'}, {'name':'newpackets','type':'STRING'}] }";  
publisher.defineStream(streamDefinition)

def handle_client(client, bssid, mac, lasttime, type, llcpackets, datapackets, cryptpackets, signal_dbm, bestlat, bestlon, bestalt, channel, datasize, newpackets):  
publisher.publish(['rpi_chicha', 'RPi 2 Model B', 'chilcano.io', int(lasttime)], [bssid, mac, type, llcpackets, datapackets, cryptpackets, signal_dbm, bestlat, bestlon, bestalt, channel, datasize, newpackets])

k.register_handler('CLIENT', handle_client)

try:  
while True:  
k.listen()  
except KeyboardInterrupt:  
pprint(k.protocols)  
publisher.disconnect()  
log.info('Exiting...')

```

At the end, the structure of files and directories will be as shown below:

```sh  
$ ll  
total 20  
drwxr-xr-x 4 pi pi 4096 Feb 3 14:39 ./  
drwxr-xr-x 11 pi pi 4096 Feb 3 12:10 ../  
drwxr-xr-x 5 pi pi 4096 Feb 3 12:14 BAMPythonPublisher/  
drwxr-xr-x 4 pi pi 4096 Feb 3 12:11 kismetclient/  
-rw-r--r-- 1 pi pi 2552 Feb 3 12:14 sendTrafficFromKismetToWSO2BAM.py

$ tree -L 3  
.  
├── BAMPythonPublisher  
│ ├── BAMPublisher.py  
│ ├── gen-py  
│ │ ├── Data  
│ │ ├── Exception  
│ │ ├── __init__.py  
│ │ ├── ThriftEventTransmissionService  
│ │ └── ThriftSecureEventTransmissionService  
│ ├── Publisher.py  
│ ├── Publisher.pyc  
│ ├── PythonClient.py  
│ ├── README.md  
│ └── thrift  
│ ├── __init__.py  
│ ├── __init__.pyc  
│ ├── protocol  
│ ├── server  
│ ├── Thrift.py  
│ ├── Thrift.pyc  
│ ├── transport  
│ ├── TSCons.py  
│ ├── TSerialization.py  
│ └── TTornado.py  
├── kismetclient  
│ ├── kismetclient  
│ │ ├── client.py  
│ │ ├── client.pyc  
│ │ ├── exceptions.py  
│ │ ├── exceptions.pyc  
│ │ ├── handlers.py  
│ │ ├── handlers.pyc  
│ │ ├── __init__.py  
│ │ ├── __init__.pyc  
│ │ ├── utils.py  
│ │ └── utils.pyc  
│ ├── LICENSE  
│ ├── README.md  
│ ├── runclient.py  
│ └── setup.py  
└── sendTrafficFromKismetToWSO2BAM.py

12 directories, 28 files  
```

Notes:

  * You have to update the `sendTrafficFromKismetToWSO2BAM.py` with IP Address, Username, Password and Ports where WSO2 BAM is running.
  * The above Python script reads the captured traffic and defines previously a structure of data to be send to WSO2 BAM (Apache Thrift). You can modify that data structure by adding or removing 802.11 fields.

**2.2) Install and configure WSO2 BAM to receive the Kismet traffic**

Before you run the `sendTrafficFromKismetToWSO2BAM.py`, WSO2 BAM 2.5.0 should be running and the Thrift listener port should be open.  
The Thrift listener standard port is `7711`, in my case I have an offset of `+2`.

I recommend you [my Docker container created to get a fully functional WSO2 BAM 2.5.0](https://hub.docker.com/r/chilcano/wso2-bam/) ready for use in this PoC with Kismet.  
To do that, open a new terminal in your Host PC and execute the next commands:

Initialize the Docker environment.

```sh  
$ docker-machine ls

$ docker-machine start default

$ eval "$(docker-machine env default)"

$ docker login  
```

Download the WSO2 BAM Docker image from Docker Hub.

```sh  
$ docker pull chilcano/wso2-bam:2.5.0

2.5.0: Pulling from chilcano/wso2-bam  
9acb471e45a5: Pull complete  
...  
e12995f4907c: Pull complete  
77e4386b8b45: Pull complete  
Digest: sha256:64e40ea4ea6b89c7e1b08edeb43e31467196a11c9fe755c0026403780f9e24e1  
Status: Downloaded newer image for chilcano/wso2-bam:2.5.0

$ docker images  
REPOSITORY TAG IMAGE ID CREATED VIRTUAL SIZE  
chilcano/netcat jessie 302d06d998e6 5 days ago 135.1 MB  
chilcano/rtail-server latest cb313f9e2546 7 days ago 674.2 MB  
ubuntu wily d8a164f81acc 7 days ago 134.4 MB  
ubuntu vivid 99639e3e70c8 7 days ago 131.3 MB  
debian jessie 7a01cc5f27b1 8 days ago 125.1 MB  
node 0.12.9 d09c6f7639f7 13 days ago 637.1 MB  
ubuntu trusty 6cc0fc2a5ee3 2 weeks ago 187.9 MB  
ubuntu precise 6b4adea2c00e 2 weeks ago 137.5 MB  
sebp/elk latest 96f071b7a8e2 3 weeks ago 980.8 MB  
chilcano/wso2-bam 2.5.0 77e4386b8b45 7 weeks ago 1.65 GB  
chilcano/wso2-dss 3.2.1 acd92f55f678 7 weeks ago 1.383 GB  
chilcano/wiremock latest a3e4764483b9 7 weeks ago 597.3 MB  
java openjdk-7 e93dd201a77e 8 weeks ago 589.7 MB

$ docker run -d -t --name=wso2bam-kismet -p 9445:9443 -p 7713:7711 chilcano/wso2-bam:2.5.0  
fc9fb8368e7f4f24b01bc33f90122776b4c10d63d0e849073474a485700b6266

$ docker ps  
CONTAINER ID IMAGE COMMAND CREATED STATUS PORTS NAMES  
fc9fb8368e7f chilcano/wso2-bam:2.5.0 "/bin/sh -c 'sh ./wso" 9 seconds ago Up 8 seconds 7611/tcp, 9160/tcp, 9763/tcp, 21000/tcp, 0.0.0.0:7713->7711/tcp, 0.0.0.0:9445->9443/tcp wso2bam-kismet  
```

The `9445` port is for the WSO2 Carbon Admin Console and the `7713` port is the Thrift listener port.  
Now, let's verify that WSO2 BAM is running in the Docker container.

```sh  
$ docker exec -ti wso2bam-kismet bash

root@fc9fb8368e7f:/opt/wso2bam02a/bin# tail -f ../repository/logs/wso2carbon.log  
TID: [0] [BAM] [2016-02-03 16:38:10,482] INFO {org.wso2.carbon.ntask.core.service.impl.TaskServiceImpl} - Task service starting in STANDALONE mode... {org.wso2.carbon.ntask.core.service.impl.TaskServiceImpl}  
TID: [0] [BAM] [2016-02-03 16:38:10,664] INFO {org.apache.cassandra.net.OutboundTcpConnection} - Handshaking version with localhost/127.0.0.1 {org.apache.cassandra.net.OutboundTcpConnection}  
TID: [0] [BAM] [2016-02-03 16:38:10,672] INFO {org.apache.cassandra.net.OutboundTcpConnection} - Handshaking version with localhost/127.0.0.1 {org.apache.cassandra.net.OutboundTcpConnection}  
TID: [0] [BAM] [2016-02-03 16:38:11,127] INFO {org.wso2.carbon.ntask.core.impl.AbstractQuartzTaskManager} - Task scheduled: [-1234][BAM_NOTIFICATION_DISPATCHER_TASK][NOTIFIER] {org.wso2.carbon.ntask.core.impl.AbstractQuartzTaskManager}  
TID: [0] [BAM] [2016-02-03 16:38:11,232] INFO {org.wso2.carbon.core.init.JMXServerManager} - JMX Service URL : service:jmx:rmi://localhost:11111/jndi/rmi://localhost:9999/jmxrmi {org.wso2.carbon.core.init.JMXServerManager}  
TID: [0] [BAM] [2016-02-03 16:38:11,246] INFO {org.wso2.carbon.core.internal.StartupFinalizerServiceComponent} - Server : WSO2BAM02A-2.5.0 {org.wso2.carbon.core.internal.StartupFinalizerServiceComponent}  
TID: [0] [BAM] [2016-02-03 16:38:11,247] INFO {org.wso2.carbon.core.internal.StartupFinalizerServiceComponent} - WSO2 Carbon started in 41 sec {org.wso2.carbon.core.internal.StartupFinalizerServiceComponent}  
TID: [0] [BAM] [2016-02-03 16:38:14,044] INFO {org.wso2.carbon.dashboard.common.oauth.GSOAuthModule} - Using random key for OAuth client-side state encryption {org.wso2.carbon.dashboard.common.oauth.GSOAuthModule}  
TID: [0] [BAM] [2016-02-03 16:38:14,714] INFO {org.wso2.carbon.ui.internal.CarbonUIServiceComponent} - Mgt Console URL : https://172.17.0.2:9443/carbon/ {org.wso2.carbon.ui.internal.CarbonUIServiceComponent}  
TID: [0] [BAM] [2016-02-03 16:38:14,714] INFO {org.wso2.carbon.ui.internal.CarbonUIServiceComponent} - Gadget Server Default Context : http://172.17.0.2:9763/portal {org.wso2.carbon.ui.internal.CarbonUIServiceComponent}  
```

**2.3) Remote access of different network (i.e. Raspberry Pi) to the WSO2 BAM Docker container**

If you want to get access to WSO2 BAM from a web browser, to use this URL `https://192.168.99.100:9445/carbon/admin`, but if you want to connect to embedded Thrift listener, to use this IP Address `192.168.99.100` and this `7713` port.  
That is valid if you are in the same Host PC, but how to get access remotely, for example from the above Raspberry Pi, to the WSO2 BAM Docker Container?.  
To do that, follow this explanation ([Remote access to Docker with TLS](https://sheerun.net/2014/05/17/remote-access-to-docker-with-tls/)), as It is mentioned, there are 3 choices, as I'm running Docker deamon in a Mac OS X, the easy way to expose and to do available the Docker container to Raspberry Pi network is to do `port forwarding` or `SSH tunneling` using `docker-machine`.

In other words, follow these commands in your Host PC (Mac OS X):

```sh  
$ docker -v  
Docker version 1.9.1, build a34a1d5  
```

As WSO2 BAM opens `9445` and `7713` ports, then I will open/forward both ports.

```sh  
$ docker-machine ssh default -f -N -L 192.168.1.43:7713:localhost:7713

// Optional  
$ docker-machine ssh default -f -N -L 192.168.1.43:9445:localhost:9445  
```

Where:

  * `'-f'` requests SSH to go to background just before command execution.
  * `'-N'` allows empty command (useful here to forward ports only).
  * The user/password for `boot2docker` is `docker/tcuser`.

You also can do the same but using the `ssh` command:

```sh  
$ ssh docker@$(docker-machine ip default) -f -N -L 192.168.1.43:7713:localhost:7713  
```

Now, from the Raspberry Pi, check if WSO2 BAM is reachable.

```sh  
$ nc -vzw 3 192.168.1.43 7713  
Connection to 192.168.1.43 7713 port [tcp/*] succeeded!

// Optional  
$ nc -vzw 3 192.168.1.43 9445  
Connection to 192.168.1.43 9445 port [tcp/*] succeeded!  
```

Or check It by using `curl`.

```sh  
$ curl -Ivsk https://192.168.1.43:9445/carbon/admin/login.jsp -o /dev/null

...  
< HTTP/1.1 200 OK  
< Set-Cookie: JSESSIONID=601A0F02DCCB47B2685686A7042BBD8F; Path=/; Secure; HttpOnly  
< X-FRAME-OPTIONS: DENY  
< Content-Type: text/html;charset=UTF-8  
< Content-Language: en  
< Transfer-Encoding: chunked  
< Vary: Accept-Encoding  
< Date: Thu, 04 Feb 2016 12:14:09 GMT  
< Server: WSO2 Carbon Server  
<  
* Connection #0 to host 192.168.1.43 left intact  
* Closing connection #0  
* SSLv3, TLS alert, Client hello (1):  
} [data not shown]  
```

**2.4) Running the custom Python script to send the captured traffic by Kismet to WSO2 BAM**

Make sure that Python is installed, install It if It's not installed.

```sh  
$ python  
Python 2.7.3 (default, Mar 18 2014, 05:13:23)  
[GCC 4.6.3] on linux2  
Type "help", "copyright", "credits" or "license" for more information.  
>>> quit()  
```

After that, run the Python script, obviously, Kismet should be running.

```sh  
$ cd kismet_to_wso2bam/

$ python sendTrafficFromKismetToWSO2BAM.py

*KISMET: ['0.0.0', '1454495618', 'rpi-chicha', 'pcapdump,netxml,nettxt,gpsxml,alert', '1000']  
Server: 0.0.0 1454495618 rpi-chicha pcapdump,netxml,nettxt,gpsxml,alert 1000  
*PROTOCOLS: ['KISMET,ERROR,ACK,PROTOCOLS,CAPABILITY,TERMINATE,TIME,PACKET,STATUS,PLUGIN,SOURCE,ALERT,COMMON,TRACKINFO,WEPKEY,STRING,GPS,BSSID,SSID,CLIENT,BSSIDSRC,CLISRC,NETTAG,CLITAG,REMOVE,CHANNEL,INFO,BATTERY,CRITFAIL']  
!1 CAPABILITY KISMET  
!2 CAPABILITY ERROR  
!3 CAPABILITY ACK  
...  
```

In the WSO2 BAM side you will see the below log events where Raspberry Pi (Kismet) is connecting to WSO2 BAM (Thrift listener) successfully.

```sh  
...  
TID: [0] [BAM] [2016-02-04 12:27:40,542] INFO {org.wso2.carbon.core.services.util.CarbonAuthenticationUtil} - 'admin@carbon.super [-1234]' logged in at [2016-02-04 12:27:40,542+0000] {org.wso2.carbon.core.services.util.CarbonAuthenticationUtil}  
TID: [0] [BAM] [2016-02-04 12:29:20,334] INFO {org.wso2.carbon.databridge.core.DataBridge} - user admin connected {org.wso2.carbon.databridge.core.DataBridge}  
TID: [0] [BAM] [2016-02-04 12:29:20,416] INFO {org.wso2.carbon.databridge.streamdefn.registry.datastore.RegistryStreamDefinitionStore} - Stream definition added to registry successfully : rpi_kismet_stream_in:1.0.0 {org.wso2.carbon.databridge.streamdefn.registry.datastore.RegistryStreamDefinitionStore}  
TID: [0] [BAM] [2016-02-04 12:29:20,670] INFO {org.wso2.carbon.databridge.persistence.cassandra.datastore.ClusterFactory} - Initializing Event cluster {org.wso2.carbon.databridge.persistence.cassandra.datastore.ClusterFactory}  
TID: [0] [BAM] [2016-02-04 12:29:20,877] INFO {org.wso2.carbon.databridge.persistence.cassandra.datastore.ClusterFactory} - Initializing Event Index cluster {org.wso2.carbon.databridge.persistence.cassandra.datastore.ClusterFactory}  
```

## III.- Exploring the 802.11 captured traffic stored in Apache Cassandra (WSO2 BAM)

Remember, the WSO2 BAM 2.5.0 Docker Container is running locally with a internal Docker Machine IP Address (`192.168.99.100`), also is running with a public IP Address by using the Host IP Address (`192.168.1.43`) because the internal IP address was forwarded.  
In brief, WSO2 BAM has the below addresses:

  * From the Internal Docker Machine IP address: 
    * WSO2 BAM Admin Web Console: https://192.168.99.100:9445/carbon/admin
    * Thrift listener: tcp://192.168.99.100:7713
  * From the Public IP address: 
    * WSO2 BAM Admin Web Console: https://192.168.1.43:9445/carbon/admin
    * Thrift listener: tcp://192.168.1.43:7713

Then, let's go to explore the 802.11 traffic stored in Apache Cassandra.  
Below a set of images took when browsing the Apache Cassandra embedded in WSO2 BAM.

_01 / WSO2 BAM / Apache Cassandra - Key Spaces_  
![WSO2 BAM / Apache Cassandra 01]({{ site.baseurl }}/assets/chilcano-02-raspberrypi-bigdata-wifi-thrift-2-wso2bam-cassandra-keyspaces.png)

_02 / WSO2 BAM / Apache Cassandra - Event KS information_  
![WSO2 BAM / Apache Cassandra 01]({{ site.baseurl }}/assets/chilcano-02-raspberrypi-bigdata-wifi-thrift-3-wso2bam-event-ks-info.png)

_03 / WSO2 BAM / Apache Cassandra - Event KS information_  
![WSO2 BAM / Apache Cassandra 01]({{ site.baseurl }}/assets/chilcano-02-raspberrypi-bigdata-wifi-thrift-4-event-ks-info.png)

_04 / WSO2 BAM / Apache Cassandra - Connecting to explore KS_  
![WSO2 BAM / Apache Cassandra 01]({{ site.baseurl }}/assets/chilcano-02-raspberrypi-bigdata-wifi-thrift-5-connect-cassandra.png)

_05 / WSO2 BAM / Apache Cassandra - List of Key Spaces_  
![WSO2 BAM / Apache Cassandra 01]({{ site.baseurl }}/assets/chilcano-02-raspberrypi-bigdata-wifi-thrift-6-list-keyspaces.png)

_06 / WSO2 BAM / Apache Cassandra - Exploring the Kismet data_  
![WSO2 BAM / Apache Cassandra 01]({{ site.baseurl }}/assets/chilcano-02-raspberrypi-bigdata-wifi-thrift-7-kismet-data.png)

_07 / WSO2 BAM / Apache Cassandra - Exploring the Kismet data_  
![WSO2 BAM / Apache Cassandra 01]({{ site.baseurl }}/assets/chilcano-02-raspberrypi-bigdata-wifi-thrift-8-kismet-data.png)

In the blog post (Part III), I will explain how to create a simple Dashboard showing the WIFI traffic captured in real-time.  
See you soon.
