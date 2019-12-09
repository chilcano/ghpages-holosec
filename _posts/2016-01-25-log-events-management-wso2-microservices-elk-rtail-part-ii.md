---
layout:     post
title:      'Log Events Management in WSO2 (Micro)services: ELK & rTail (Part II)'
date:       2016-01-25 16:39:08
categories: ['Microservices', 'Observability', 'SOA']
tags:       ['Docker', 'ELK', 'Logging', 'rTail', 'Vagrant', 'Wiremock', 'WSO2']
status:     publish 
permalink:  "/2016/01/25/log-events-management-wso2-microservices-elk-rtail-part-ii/"
---
Trailing and checking the performance and the health of (micro)services are important tasks to be accomplished. The logging is a time consuming process and we have to prepare before in order to be more productive. There are many tools out there, opensource, commercial, on-cloud, such as log.io, ELK, Clarity, rTail, Tailon, frontail, etc. In my opinion, for a VM used to development the most simple, fresh and lightweight tool is rTail (http://rtail.org).

![rTail - Viewing WSO2 and Wiremock raw log events]({{ site.baseurl }}/assets/chilcano-logs-rtail-microservices-1-arch-rtail.png)rTail - Viewing WSO2 and Wiremock raw log events

<!-- more -->
With rTail I can collect different log files, track and visualize them from a Browser in **real time**. rTail is very easy to use, just install NodeJS and deploy rTail application and you will be ready to send any type of traces to Browser directly avoiding store/persist logs, index and parse/filter them.
In this second blog post I will explain how to use rTail to view all streams/log-events from a Browser in real time. For that, we require:  
* rTail Server Docker Container who will centralize and view all stream/log-events.  
* Vagrant box (with WSO2 stack and Wiremock) what will send log events to above rTail Server Docker Container

## Part II: rTail (a node.js application to debug and monitor in realtime)

### 1\. Starting with rTail Server Docker Container
 **1) Prepare the rTail Server Docker Container**
I have created and published a rTail Docker Image in Docker Hub ready to use it.  
Just download and run it.
```sh  
$ docker login  
Username (chilcano):  
WARNING: login credentials saved in /Users/Chilcano/.docker/config.json  
Login Succeeded
$ docker search rtail-server  
NAME DESCRIPTION STARS OFFICIAL AUTOMATED  
chilcano/rtail-server rTail is a realtime debugging and monitori... 1 [OK]  
maluuba/rtail-server 0 [OK]
$ docker pull chilcano/rtail-server  
Using default tag: latest  
latest: Pulling from chilcano/rtail-server  
523ef1d23f22: Pull complete  
140f9bdfeb97: Pull complete  
5c63804eac90: Pull complete  
ce2b29af7753: Pull complete  
5c2bdca41b86: Pull complete  
f417df1119e6: Pull complete  
d36821cb651a: Pull complete  
48d9fce985a8: Pull complete  
d09c6f7639f7: Pull complete  
46a67992ee2a: Pull complete  
78642d9272ea: Pull complete  
d95ea484c076: Pull complete  
d55510bfe660: Pull complete  
2cc39298d465: Pull complete  
bd885c733a0a: Pull complete  
f8fa62532424: Pull complete  
Digest: sha256:ebb137e20fd3eb404b57620e14a355d7bdc635ebab237719ba41e19c1fa8928b  
Status: Downloaded newer image for chilcano/rtail-server:latest
$ docker images  
REPOSITORY TAG IMAGE ID CREATED VIRTUAL SIZE  
chilcano/rtail-server latest f8fa62532424 2 days ago 663.8 MB  
sebp/elk latest 96f071b7a8e2 2 weeks ago 980.8 MB  
chilcano/wso2-dss 3.2.1 acd92f55f678 5 weeks ago 1.383 GB  
chilcano/wiremock latest a3e4764483b9 6 weeks ago 597.3 MB  
java openjdk-7 e93dd201a77e 7 weeks ago 589.7 MB
$ docker run -d -t --name=rtail-srv -p 8181:8181 -p 9191:9191/udp chilcano/rtail-server  
4d0c897e9741342dfc7c8ca9d95dc8144f56f21954baf9170f593585181bd469
$ docker ps  
CONTAINER ID IMAGE COMMAND CREATED STATUS PORTS NAMES  
4d0c897e9741 chilcano/rtail-server "/bin/sh -c 'rtail-se" 15 seconds ago Up 16 seconds 0.0.0.0:8181->8181/tcp, 9191/tcp, 0.0.0.0:9191->9191/udp rtail-srv
```
Or download the Dockerfile, build it and run it.
```sh  
$ git clone https://github.com/chilcano/docker-rtail-server
$ docker build --rm -t chilcano/rtail-server docker-rtail-server/
$ docker images  
REPOSITORY TAG IMAGE ID CREATED VIRTUAL SIZE  
chilcano/rtail-srv latest 93b7a3c76c6a 7 seconds ago 664.8 MB  
sebp/elk latest 96f071b7a8e2 2 weeks ago 980.8 MB  
chilcano/wso2-dss 3.2.1 acd92f55f678 5 weeks ago 1.383 GB  
chilcano/wiremock latest a3e4764483b9 5 weeks ago 597.3 MB  
java openjdk-7 e93dd201a77e 6 weeks ago 589.7 MB  
node 0.12.6 77d70f920fa3 6 months ago 638.1 MB
$ docker run -d -t --name=rtail-srv -p 8181:8181 -p 9191:9191/udp chilcano/rtail-server  
bdbb0476fa201f5114355a636b01ea165335398b50865c6e58f1716931b2c779
$ docker ps  
CONTAINER ID IMAGE COMMAND CREATED STATUS PORTS NAMES  
bdbb0476fa20 chilcano/rtail-srv "/bin/sh -c 'rtail-se" 5 seconds ago Up 5 seconds 9191/tcp, 0.0.0.0:9191->9191/udp, 0.0.0.0:8181->8181/tcp rtail-srv  
```
**2) Check if the rTail Server Docker Container is working**
Just open the rTail Server Web Console from a browser using this URL `http://192.168.99.100:8181`.  
But if you want check if rTail Server Container is reacheable remotely (from other VM) to send log events, just execute this:
```sh  

# use netcat instead of telnet, because telnet doesn't use UDP  
$ nc -vuzw 3 <IP_ADDRESS_RTAIL_CONTAINER> 9191  
Connection to 192.168.99.100 9191 port [udp/*] succeeded!  
```
To stop, start or restart rTail Server just stop, start or restart the Docker container
**3) Get Shell access to rTail Server Container**
```sh  
$ docker exec -i -t rtail-srv bash  
```
Where:  
\- `8181` port is running a HTTP server. It is useful to view the log events from a web browser.  
\- `9191` port is listening for UDP traffic (log events).

### 2\. Send log events to rTail Server Docker Container
You can send any type of log events, from a syslog event, an echo message or a log by tailing. Before, you have to install rTail application again in the box/VM from where you want send log events.  
I have created a [Puppet module for rTail](https://github.com/chilcano/vagrant-wso2-dev-srv/tree/master/provision/wso2-stack-srv/puppet/modules/rtail_sender) and I have included It to the Vagrant box to have the rTail (client) ready to be used.
**1) Using rTail (client) to send log events to rTail Server**
To get a Vagrant box with rTail (client) pre-installed, you could use these Vagrant scripts (<https://github.com/chilcano/vagrant-wso2-dev-srv>).
```sh  
$ git clone https://github.com/chilcano/vagrant-wso2-dev-srv.git
$ cd ~/github-repo/vagrant-wso2-dev-srv

# start  
$ vagrant up

# re-load and provision  
$ vagrant reload --provision  
```
**2) Check if rTail (as Client) is working in the Vagrant box and if can reach to Docker Container**
To check if rTail was installed/provisioned properly, get SSH access, try to reach and send some traces to the existing rTail Server Docker Container.
```sh  
$ vagrant ssh

# use netcat instead of telnet, because telnet doesn't use UDP  
$ nc -vuzw 3 9191  
Connection to 192.168.99.100 9191 port [udp/*] succeeded!

# send ping events to IP address  
$ ping 8.8.4.4 | rtail --id logs-ping --host 192.168.99.100 --port 9191 --mute  
$  
```
_rTail - Browsing log events_  

![rTail - Browsing log events]({{ site.baseurl }}/assets/chilcano-logs-rtail-microservices-2-ping-tail.png)
**3) Send log events to rTail Server Docker Container from the Vagrant box**
Wiremock is a mock server that should be running in the box. Then, we will send the Wiremock traces/events to the rTail server.
```sh  

# start wiremock  
$ sudo service wiremock start  

[wiremock] server starting ... success (pid 15601)

# tailing a log file  
$ tail -f /opt/wiremock/wiremock.log | rtail --id wiremock --host 192.168.99.100 --port 9191 --mute  
```
Now, to send the multiple log events of multiple log files to unique merged stream we will use in this case the `multitail`.
```sh  

# install 'multitail'  
$ sudo apt-get install multitail

# test 'multitail' (merge the output of 2 commands)  
$ multitail -l "ping 8.8.8.8" -L "ping 8.8.4.4"

# send 2 ping output to rTail  
$ multitail -l "ping 8.8.8.8" -L "ping 8.8.4.4" | rtail --id logs-ping --host 192.168.99.100 --port 9191 --mute  
```
Now, to send 3 log file to rTail Server to an unique merged stream using this process/pattern, i.e.: WSO2 API Manager, WSO2 ESB and as backend Wiremock (`wso2am02a -&gt; wso2esb02a -&gt; wiremock`), then you should `multitail` the 3 log files
```sh  

# tailing the flow 'wso2am02a -> wso2esb02a -> wiremock'  
$ multitail -ke "[ \t]+$" /opt/wso2am02a/repository/logs/wso2carbon.log -I /opt/wso2esb02a/repository/logs/wso2carbon.log -I /opt/wiremock/wiremock.log | rtail --id logs-wso2-01 --host 192.168.99.100 --port 9191 --tty --mute  
```
If you use `tail` instead of `multitail` you will see all log events merged but with a mark/header. You could create a shell script to remove these headers.
```sh  
$ tail -f /opt/wso2am02a/repository/logs/wso2carbon.log -f /opt/wso2esb02a/repository/logs/wso2carbon.log -f /opt/wiremock/wiremock.log | rtail --id logs-wso2-02 --host 192.168.99.100 --port 9191 --tty --mute
```
Where:
  * `-ts` add a timestamp (format configurable in `multitail.conf`) before each line
  * `-ke "[ \t]+$"` remove TABs and blankspaces in every line.
  * `-I` merge the log file.
  * `--tty` keeps ansi colors.
Observations:
  * `multitail` consolidate multiple log lines in on2 line associated a to timestamp (`date+hh:mm:ss`), but doesn't accept milliseconds.
  * Using `tail` you require create a shell script to remove header or apply filters to standarize Date formats, etc.
_rTail - Multiple log tailing using`multitail`_  

![rTail - Multiple log tailing using 'multitail']({{ site.baseurl }}/assets/chilcano-logs-rtail-microservices-3-multitail.png)
_rTail - Multiple log tailing using`tail`_  

![rTail - Multiple log tailing using 'tail']({{ site.baseurl }}/assets/chilcano-logs-rtail-microservices-4-multiple-tail.png)
**4) Shell scripts to send multiple WSO2 log files**
I have created a bash script to send all log events to the rTail server. You can find the bash script under `/etc/init.d/rtail-send-logs` and can run it whenever.
```sh  

# initial status of rtail scripts  
$ service --status-all  
...  

[ - ] rtail-server  

[ - ] rtail-send-logs  
...

# start rTail Server, useful just for rTail Server Docker Container  
$ sudo service rtail-server status  

[rTail] server is running (pid 1234)  
```
There is a rTail Puppet module to enable the rTail server to start automatically when booting the VM.  
In other words, rTail server always is listening in the port UDP to receive events and logs.
```sh  

# start, stop and status of WSO2 log files simultaneously (not merged)  
$ sudo service rtail-send-logs status  

[wso2am02a] is sending logs to rTail.  

[wso2esb01a] is sending logs to rTail.  

[wso2esb02a] is sending logs to rTail.  

[wso2dss01a] is sending logs to rTail.  

[wso2greg01a] is sending logs to rTail.  

[wiremock] is sending logs to rTail.
$ sudo service rtail-send-logs stop  

[wso2am02a] is stopping sending logs to rTail ... success  

[wso2esb01a] is stopping sending logs to rTail ... success  

[wso2esb02a] is stopping sending logs to rTail ... success  

[wso2dss01a] is stopping sending logs to rTail ... success  

[wso2greg01a] is stopping sending logs to rTail ... success  

[wiremock] is stopping sending logs to rTail ... success
$ sudo service rtail-send-logs start  

[wso2am02a] is starting sending logs to rTail ... success  

[wso2esb01a] is starting sending logs to rTail ... success  

[wso2esb02a] is starting sending logs to rTail ... success  

[wso2dss01a] is starting sending logs to rTail ... success  

[wso2greg01a] is starting sending logs to rTail ... success  

[wiremock] is starting sending logs to rTail ... success  
```
That’s all.
