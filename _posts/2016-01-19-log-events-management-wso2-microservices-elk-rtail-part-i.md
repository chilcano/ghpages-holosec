---
layout:     post
title:      'Log Events Management in WSO2 (Micro)services: ELK & rTail (Part I)'
date:       2016-01-19 16:49:31
categories: ['Microservices', 'SOA']
tags:       ['Docker', 'ELK', 'Logging', 'rTail', 'Vagrant', 'Wiremock', 'WSO2']
status:     publish 
permalink:  "/2016/01/19/log-events-management-wso2-microservices-elk-rtail-part-i/"
---
The log event management is a task very important when working with (Micro)services. If you collect-store-index all logs, then you will be able to create your business metrics (KPI). You only must understand what by collecting logs you have to use special tools with technical special requirements:  
* Huge amout of logs could transform in your `BigData` asset.  
* You have to collect and query your logs in real time if you think your Application is critical.  
* You have to `manage` in agile way the life cycle of your data (your logs), they are an asset very important for your Organization.

![Kibana - Viewing WSO2 and Wiremock raw log events]({{ site.baseurl }}/assets/blog-chilcano-logs-wso2-docker-elk-rtail-0-architecture.png)Collecting WSO2 and Wiremock log events with ELK and rTail

<!-- more -->

You should be agile, for this reason, [Elasticsearch-Logstash-Kibana](https://www.elastic.co) is the perfect Stack to do that in an agile way.  
Really, there are many tools out there, opensource, commercial, on-cloud, such as log.io, Clarity, rTail, Tailon, frontail, etc. In my opinion, for a development environment, the most simple, fresh and lightweight tool is rTail (http://rtail.org), with rTail I can collect different log files, track and visualize them from a Browser in real time. rTail is very easy to use, just install NodeJS and deploy rTail application and you will be ready to send any type of traces to Browser directly avoiding store/persist logs, index and parse/filter them.  
Well, this first blog post I will explain how to use ELK to collect, store and view the different log event of WSO2 ESB, API Manager, DSS, GREG and Wiremock.

## Part I: ELK (Elasticsearch, Logstash, Kibana)

### 1\. Starting with ELK Docker Container
 **1) Prepare the ELK container**
We gonna use an existing Docker Image with ELK created by [Sébastien Pujadas](https://github.com/spujadas) `(http://elk-docker.readthedocs.org)` previously configured ready to be used. This Docker Image contains:  
\- Elasticsearch (version 2.1.1)  
\- Logstash (version 2.1.1)  
\- Kibana (version 4.3.1)
Then, let's do it. Start the Docker daemon and login Docker Hub:
```sh  
$ docker login  
Username (chilcano):  
WARNING: login credentials saved in /Users/Chilcano/.docker/config.json  
Login Succeeded
$ docker pull sebp/elk  
Using default tag: latest  
latest: Pulling from sebp/elk  
de9c48daf08c: Pull complete  
...  
96f071b7a8e2: Pull complete  
Digest: sha256:ce7b3a1dfe285d1d9b862905bf0ee6df951f1a035120b92af71280217b6f3422  
Status: Downloaded newer image for sebp/elk:latest  
```
 **2) Run the container**
```sh  
$ docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -p 5000:5000 -it --name elk sebp/elk  
* Starting Elasticsearch Server  
sysctl: setting key "vm.max_map_count": Read-only file system logstash started. [ OK ]
waiting for Elasticsearch to be up (1/30)  
waiting for Elasticsearch to be up (2/30)  
waiting for Elasticsearch to be up (3/30)  
waiting for Elasticsearch to be up (4/30)  
waiting for Elasticsearch to be up (5/30)  
waiting for Elasticsearch to be up (6/30)  
waiting for Elasticsearch to be up (7/30)  
* Starting Kibana4 [ OK ]  

[2016-01-14 08:48:01,215][INFO ][node ] [Zero] initialized  

[2016-01-14 08:48:01,216][INFO ][node ] [Zero] starting ...  

[2016-01-14 08:48:01,259][WARN ][common.network ] [Zero] publish address: {0.0.0.0} is a wildcard address, falling back to first non-loopback: {172.17.0.2}  

[2016-01-14 08:48:01,259][INFO ][transport ] [Zero] publish_address {172.17.0.2:9300}, bound_addresses {[::]:9300}  

[2016-01-14 08:48:01,297][INFO ][discovery ] [Zero] elasticsearch/vyxjAXavQbOnh7uA7HO_7g  

[2016-01-14 08:48:04,341][INFO ][cluster.service ] [Zero] new_master {Zero}{vyxjAXavQbOnh7uA7HO_7g}{172.17.0.2}{172.17.0.2:9300}, reason: zen-disco-join(elected_as_master, [0] joins received)  

[2016-01-14 08:48:04,365][WARN ][common.network ] [Zero] publish address: {0.0.0.0} is a wildcard address, falling back to first non-loopback: {172.17.0.2}  

[2016-01-14 08:48:04,365][INFO ][http ] [Zero] publish_address {172.17.0.2:9200}, bound_addresses {[::]:9200}  

[2016-01-14 08:48:04,365][INFO ][node ] [Zero] started  

[2016-01-14 08:48:04,384][INFO ][gateway ] [Zero] recovered [0] indices into cluster_state  

[2016-01-14 08:48:13,180][INFO ][cluster.metadata ] [Zero] [.kibana] creating index, cause [api], templates [], shards [1]/[1], mappings [config]  
```
**3) Check the status of ELK in the running container**
In other Terminal/Shell execute the next:
```sh  
$ docker-machine ls  
NAME ACTIVE DRIVER STATE URL SWARM ERRORS  
default - virtualbox Running tcp://192.168.99.100:2376  
machine-dev - virtualbox Stopped  
machine-test - virtualbox Stopped
$ docker-machine env default  
export DOCKER_TLS_VERIFY="1"  
export DOCKER_HOST="tcp://192.168.99.100:2376"  
export DOCKER_CERT_PATH="/Users/Chilcano/.docker/machine/machines/default"  
export DOCKER_MACHINE_NAME="default"  

# Run this command to configure your shell:  

# eval "$(docker-machine env default)"
$ eval "$(docker-machine env default)"
$ docker ps  
CONTAINER ID IMAGE COMMAND CREATED STATUS PORTS NAMES  
788b97b04e9b sebp/elk "/usr/local/bin/start" 9 minutes ago Up 9 minutes 0.0.0.0:5000->5000/tcp, 0.0.0.0:5044->5044/tcp, 0.0.0.0:5601->5601/tcp, 0.0.0.0:9200->9200/tcp, 9300/tcp elk  
```
The ports opened are:
  * 5601 (Kibana web interface).
  * 9200 (Elasticsearch JSON interface).
  * 5044 (Logstash Beats interface, receives logs from Beats such as Filebeat).
  * 5000 (Logstash Lumberjack interface, receives logs from Logstash forwarders).
**4) Check Elasticsearch server**
```sh  
$ curl -H "User-Agent: Mozilla" -H "Origin: http://example.com" -i 192.168.99.100:9200  
HTTP/1.1 200 OK  
Content-Type: application/json; charset=UTF-8  
Content-Length: 313
{  
"name" : "Zero",  
"cluster_name" : "elasticsearch",  
"version" : {  
"number" : "2.1.1",  
"build_hash" : "40e2c53a6b6c2972b3d13846e450e66f4375bd71",  
"build_timestamp" : "2015-12-15T13:05:55Z",  
"build_snapshot" : false,  
"lucene_version" : "5.3.1"  
},  
"tagline" : "You Know, for Search"  
}  
```
And if you want to stop and start again the container, just execute the next:
```sh  
$ docker stop elk  
elk
$ docker start elk  
elk  
```
**5) Sending a dummy logs to ELK**
In another terminal execute the next:
```sh  
$ docker exec -it elk /bin/bash
$ docker exec -it elk /bin/bash  
root@788b97b04e9b:/# /opt/logstash/bin/logstash -e 'input { stdin { } } output { elasticsearch { hosts => ["localhost"] } }'  
Settings: Default filter workers: 1  
Logstash startup completed  
Hola Chilcano!!  
^CSIGINT received. Shutting down the pipeline. {:level=>:warn}
Logstash shutdown completed  
root@788b97b04e9b:/#  
```
In other terminal using cURL or from a browser:
```sh  
$ curl http://192.168.99.100:9200/_search?pretty  
{  
"took" : 5,  
"timed_out" : false,  
"_shards" : {  
"total" : 6,  
"successful" : 6,  
"failed" : 0  
},  
"hits" : {  
"total" : 2,  
"max_score" : 1.0,  
"hits" : [ {  
"_index" : ".kibana",  
"_type" : "config",  
"_id" : "4.3.1",  
"_score" : 1.0,  
"_source":{"buildNum":9517}  
}, {  
"_index" : "logstash-2016.01.14",  
"_type" : "logs",  
"_id" : "AVJA8Na_dQWajjStHmJN",  
"_score" : 1.0,  
"_source":{"message":"Hola Chilcano!!","@version":"1","@timestamp":"2016-01-14T16:21:11.231Z","host":"788b97b04e9b"}  
} ]  
}  
}  
```
... and from the Kibana web console (`http://192.168.99.100:5601`) to view the incoming dummy log event. Before configure Kibana by creating a `Index Pattern` with `logstash-*` and `Time-field name: @timestamp`, as shown below:
_Kibana - Creating a Index Pattern_  

![Kibana - Creating a Index Pattern]({{ site.baseurl }}/assets/blog-chilcano-logs-wso2-docker-elk-rtail-1-kibana.png)  
_Kibana - Selecting the field of the new Index Pattern_  

![Kibana - Selecting the field of the new Index Pattern]({{ site.baseurl }}/assets/blog-chilcano-logs-wso2-docker-elk-rtail-2-kibana-dummy-logs.png)  
_Kibana - Viewing a dummy log event_  

![Kibana - Viewing a dummy log event]({{ site.baseurl }}/assets/blog-chilcano-logs-wso2-docker-elk-rtail-3-kibana-dummy-logs-discovery.png)

### 2\. Sending WSO2 logs to the ELK Docker container
For this part I will use a Vagrant box with several WSO2 products pre-installed and pre-configured. I explained how to use It in this blog, for further details check that post <https://holisticsecurity.wordpress.com/2015/11/11/creating-a-vm-with-wso2-servers-for-development>.
> Filebeat is a lightweight, open source shipper for log file data. As the next-generation  
>  Logstash Forwarder, Filebeat tails logs and quickly sends this information to Logstash  
>  for further parsing and enrichment or to Elasticsearch for centralized storage and analysis.  
>  _[www.elastic.co/products/beats/filebeat](https://www.elastic.co/products/beats/filebeat)_
**1) Start the WSO2 Vagrant box**
```sh  
$ git clone https://github.com/chilcano/box-vagrant-wso2-dev-srv.git
$ cd box-vagrant-wso2-dev-srv
$ vagrant up  
```
```sh  
$ vagrant ssh  
Welcome to Ubuntu 14.04.3 LTS (GNU/Linux 3.13.0-67-generic i686)
* Documentation: https://help.ubuntu.com/
System information as of Tue Jan 12 10:41:00 UTC 2016
System load: 1.76 Processes: 108  
Usage of /: 33.9% of 39.34GB Users logged in: 0  
Memory usage: 42% IP address for eth0: 10.0.2.15  
Swap usage: 0% IP address for eth1: 192.168.11.20
Graph this data and manage this system at:  
https://landscape.canonical.com/
Get cloud support with Ubuntu Advantage Cloud Guest:  
http://www.ubuntu.com/business/services/cloud
9 packages can be updated.  
7 updates are security updates.
Last login: Tue Jan 12 10:41:01 2016 from 10.0.2.2

[11:04 AM]-[vagrant@wso2-dev-srv-01]-[~]  
$ cd /opt/

[11:04 AM]-[vagrant@wso2-dev-srv-01]-[/opt]  
$ ll  
total 40  
drwxr-xr-x 11 vagrant vagrant 4096 Nov 30 23:43 activemq/  
drwxr-xr-x 2 vagrant vagrant 4096 Jan 12 10:39 rtail/  
drwxr-xr-x 9 root root 4096 Nov 18 18:18 VBoxGuestAdditions-4.3.10/  
drwxr-xr-x 4 vagrant vagrant 4096 Nov 30 15:41 wiremock/  
drwxr-xr-x 15 vagrant vagrant 4096 Nov 30 15:41 wso2am02a/  
drwxr-xr-x 10 vagrant vagrant 4096 Jan 12 10:40 wso2dss01a/  
drwxr-xr-x 11 vagrant vagrant 4096 Nov 23 12:44 wso2esb01a/  
drwxr-xr-x 11 vagrant vagrant 4096 Nov 23 12:45 wso2esb02a/  
drwxr-xr-x 10 vagrant vagrant 4096 Nov 30 23:14 wso2esb490/  
drwxr-xr-x 13 vagrant vagrant 4096 Jan 12 10:41 wso2greg01a/  
```
If you already are running this Vagrant box with WSO2 ESB, WSO2 API Manager, WSO2 DSS, Wiremock, etc. then the next step is configure It to send the different generated logs to ELK Docker Container.  
In the documentation explains that It will be used [Filebeat](https://www.elastic.co/products/beats/filebeat), for that we have to install and configure Filebeat in this Vagrant box.
**2) Install Filebeat into Vagrant box**
```sh  
$ sudo curl -L -O https://download.elastic.co/beats/filebeat/filebeat_1.0.1_i386.deb  
$ sudo dpkg -i filebeat_1.0.1_i386.deb  
$ sudo rm filebeat_1.0.1_i386.deb  
```
**3) Configure Filebeat to forward WSO2 logs to ELK Docker Container**
```sh  
$ ll /etc/filebeat/  
total 20  
-rw-r--r-- 1 root root 814 Dec 17 13:26 filebeat.template.json  
-rw-r--r-- 1 root root 14541 Dec 17 13:26 filebeat.yml  
```
```sh  
$ sudo nano filebeat.yml
output:  
logstash:  
enabled: true  
hosts: ["elk-docker:5044"]  
timeout: 15  
tls:  
certificate_authorities: ["/etc/pki/tls/certs/logstash-beats.crt"]
filebeat:  
prospectors:  
-  
paths:  
\- /opt/wso2esb01a/repository/logs/wso2carbon.log  
\- /opt/wso2esb02a/repository/logs/wso2carbon.log  
\- /opt/wso2am02a/repository/logs/wso2carbon.log  
\- /opt/wso2dss01a/repository/logs/wso2carbon.log  
\- /opt/wso2greg01a/repository/logs/wso2carbon.log  
document_type: log  
-  
paths:  
\- /opt/wiremock/wiremock.log  
document_type: log  
logging:  
level: warning  
to_files: true  
to_syslog: false
files:  
path: /var/log/filebeat  
name: filebeat.log  
keepfiles: 7
```
Where `elk-docker:5044` is the hostname for the `192.168.99.100` added to `/etc/hosts` of Vagrant box.
Copy the `/etc/pki/tls/certs/logstash-beats.crt` file from Logstash Beats input plugin (ELK Docker Container) into `/etc/pki/tls/certs/logstash-beats.crt` (Vagrant box).
```text  
$ sudo mkdir -p /etc/pki/tls/certs/  
$ cd /etc/pki/tls/certs/  
$ sudo wget https://raw.githubusercontent.com/spujadas/elk-docker/master/nginx-filebeat/logstash-beats.crt

### or from ELK container SCP the *.crt file to Vagrant (open 2222 port).  
$ scp -P 2222 /etc/pki/tls/certs/logstash-beats.crt vagrant@192.168.1.43:/etc/pki/tls/certs/logstash-beats.crt  
```
To avoid the below error, to update the `host` with a hostname (not IP address) as `elk-docker` in `/etc/filebeat/filebeat.yml` file, after that update also `/etc/hosts` with the appropiate IP Address.
```sh  
$ sudo /etc/init.d/filebeat start  
2016/01/15 17:30:22.910725 transport.go:125: ERR SSL client failed to connect with: x509: cannot validate certificate for 192.168.99.100 because it doesn't contain any IP SANs  
```
**4) Loading the Index Template in Elasticsearch**
Before starting Filebeat for the first time, run this command to load the default index template in Elasticsearch from the Vagrant box:
```sh  
$ curl -XPUT 'http://192.168.99.100:9200/_template/filebeat?pretty' -d@/etc/filebeat/filebeat.template.json  
{  
"acknowledged" : true  
}  
```
**5) Start Filebeat daemon**
```sh  
$ sudo /etc/init.d/filebeat restart  
* Restarting Sends log files to Logstash or directly to Elasticsearch. filebeat  
2016/01/19 10:55:36.335563 beat.go:97: DBG Initializing output plugins  
2016/01/19 10:55:36.337701 geolite.go:24: INFO GeoIP disabled: No paths were set under output.geoip.paths  
2016/01/19 10:55:36.406510 outputs.go:111: INFO Activated logstash as output plugin.  
2016/01/19 10:55:36.406694 publish.go:198: DBG create output worker: 0x0, 0x0  
2016/01/19 10:55:36.406901 publish.go:235: DBG No output is defined to store the topology. The server fields might not be filled.  
2016/01/19 10:55:36.407160 publish.go:249: INFO Publisher name: wso2-dev-srv-01  
2016/01/19 10:55:36.407587 async.go:95: DBG create bulk processing worker (interval=1s, bulk size=200)  
2016/01/19 10:55:36.408164 beat.go:107: INFO Init Beat: filebeat; Version: 1.0.1  
...done.  
```
or if You have created `filebeat.yml` in a different folder.
```sh  
$ sudo ./filebeat -e -c /myfolder/filebeat.yml  
```
**6) Check if Filebeat is running**
I have created a `filebeat.yml` file with the logs section enabled. That's suitable to verify of everything is OK.
```sh  
$ sudo tail -10000f /var/log/filebeat/filebeat.log  
...  
2016-01-19T10:56:16Z DBG Start next scan  
2016-01-19T10:56:16Z DBG scan path /opt/wso2esb01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Check file for harvesting: /opt/wso2esb01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Update existing file for harvesting: /opt/wso2esb01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Not harvesting, file didn't change: /opt/wso2esb01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG scan path /opt/wso2esb02a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Check file for harvesting: /opt/wso2esb02a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Update existing file for harvesting: /opt/wso2esb02a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Not harvesting, file didn't change: /opt/wso2esb02a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG scan path /opt/wso2am02a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Check file for harvesting: /opt/wso2am02a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Update existing file for harvesting: /opt/wso2am02a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Not harvesting, file didn't change: /opt/wso2am02a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG scan path /opt/wso2dss01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Check file for harvesting: /opt/wso2dss01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Update existing file for harvesting: /opt/wso2dss01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Not harvesting, file didn't change: /opt/wso2dss01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG scan path /opt/wso2greg01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Check file for harvesting: /opt/wso2greg01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Update existing file for harvesting: /opt/wso2greg01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:16Z DBG Not harvesting, file didn't change: /opt/wso2greg01a/repository/logs/wso2carbon.log  
2016-01-19T10:56:24Z DBG Flushing spooler because of timemout. Events flushed: 2  
2016-01-19T10:56:24Z DBG send event  
2016-01-19T10:56:24Z DBG Start Preprocessing  
2016-01-19T10:56:24Z DBG Publish: {  
"@timestamp": "2016-01-19T10:56:21.533Z",  
"beat": {  
"hostname": "wso2-dev-srv-01",  
"name": "wso2-dev-srv-01"  
},  
"count": 1,  
"fields": null,  
"input_type": "log",  
"message": "",  
"offset": 973,  
"source": "/opt/wiremock/wiremock.log",  
"type": "log"  
}  
2016-01-19T10:56:24Z DBG Publish: {  
"@timestamp": "2016-01-19T10:56:21.533Z",  
"beat": {  
"hostname": "wso2-dev-srv-01",  
"name": "wso2-dev-srv-01"  
},  
"count": 1,  
"fields": null,  
"input_type": "log",  
"message": "",  
"offset": 974,  
"source": "/opt/wiremock/wiremock.log",  
"type": "log"  
}  
2016-01-19T10:56:24Z DBG Forward preprocessed events  
2016-01-19T10:56:24Z DBG output worker: publish 2 events  
2016-01-19T10:56:24Z DBG Try to publish %!s(int=2) events to logstash with window size %!s(int=10)  
2016-01-19T10:56:24Z DBG %!s(int=2) events out of %!s(int=2) events sent to logstash. Continue sending ...  
2016-01-19T10:56:24Z INFO Events sent: 2  
2016-01-19T10:56:24Z DBG Processing 2 events  
2016-01-19T10:56:24Z DBG Write registry file: /.filebeat  
2016-01-19T10:56:24Z INFO Registry file updated. 6 states written.  
...  
```
The filebeat.log indicates that Filebeat daemon is sending the events to Logstash (ELK container).
**7) Viewing the raw log events from Kibana**
I am not filtering the log events, Logstash will be received the informations as it is.  
In a next blog post I will explain how to visualize the log events using filters, queries and graphs.
To view the raw log events, just open Kibana from a browser, in my case is in `http://192.168.99.100:5601`.  
Go to `Kibana &gt; Settings`, add and create a new `Index Pattern` using the next:  
* Index name or pattern : `filebeat-*` (instead of `logstash-*`)  
* Time-field name: `@timestamp`
After that, go to `Kibana &gt; Discover` and select the recently created `filebeat-*` Index Pattern and You will see your logs/events.
_Kibana - Viewing WSO2 and Wiremock raw log events_  

![Kibana - Viewing WSO2 and Wiremock raw log events]({{ site.baseurl }}/assets/blog-chilcano-logs-wso2-docker-elk-rtail-4-kibana-esb-am-dss-wiremock-logs-discovery.png)
In the above figure, a raw log event for WSO2 API Manager in JSON format is like as below:
```sh  
{  
"_index": "filebeat-2016.01.19",  
"_type": "log",  
"_id": "AVJZhXK2D7hOMrzuotcM",  
"_score": null,  
"_source": {  
"message": "TID: [0] [AM] [2016-01-19 10:54:12,666] INFO {org.wso2.carbon.core.internal.permission.update.PermissionUpdater} - Permission cache updated for tenant -1234 {org.wso2.carbon.core.internal.permission.update.PermissionUpdater}",  
"@version": "1",  
"@timestamp": "2016-01-19T10:54:16.057Z",  
"beat": {  
"hostname": "wso2-dev-srv-01",  
"name": "wso2-dev-srv-01"  
},  
"count": 1,  
"fields": null,  
"input_type": "log",  
"offset": 0,  
"source": "/opt/wso2am02a/repository/logs/wso2carbon.log",  
"type": "log",  
"host": "wso2-dev-srv-01"  
},  
"fields": {  
"@timestamp": [  
1453200856057  
]  
},  
"sort": [  
1453200856057  
]  
}  
```
As I said above, in a next blog post I will explain how to visualize the log events using filters to parse simple event and multiple events.
That's all.  
I hope you enjoyed it.
**References:**  
* Vagrant box with WSO2 stack and Wiremock (https://github.com/Chilcano/box-vagrant-wso2-dev-srv)  
* Elasticsearch, Logstash, Kibana (ELK) Docker image by Sébastien Pujadas (https://github.com/spujadas/elk-docker)
