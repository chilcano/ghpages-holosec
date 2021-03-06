Running multi-container (WSO2 BAM & MAC Address Lookup) Docker Application using Docker Compose

In my 4 previous blog post I explained each part of this Proof-of-concept, they are:

1. Analysing Wireless traffic in real time with WSO2 BAM, Apache Cassandra, Complex Event Processor (CEP Siddhi), Apache Thrift and Python:
  * Part I (http://wp.me/p8pPj-pE)
  * Part II (http://wp.me/p8pPj-pW)
  * Part III (http://wp.me/p8pPj-qe)
2. A Python Microservice in a Docker Container (MAC Address Manufacturer Lookup):
  * http://wp.me/p8pPj-qG

Now, in this blog post I'm going to explain how to run two Docker Containers, the WSO2 BAM and the MAC Address Manufacturer Lookup containers, by using Docker Compose.

```
// clone 2 repositories
$ git clone https://github.com/chilcano/docker-wso2bam-kismet-poc.git
$ cd docker-wso2bam-kismet-poc
$ git clone https://github.com/chilcano/wso2bam-wifi-thrift-cassandra-poc.git

// run docker compose
$ docker-compose up -d

Starting dockerwso2bamkismetpoc_mac-manuf_1
Starting dockerwso2bamkismetpoc_wso2bam-dashboard-kismet_1
```

Below a diagram explaining this.

![802.11 traffic capture PoC - Docker Compose](https://github.com/chilcano/docker-wso2bam-kismet-poc/blob/master/chilcano-wso2bam-cep-siddhi-wifi-kismet-thrift-cassandra-docker-compose.png?raw=1 "802.11 traffic capture PoC - Docker Compose")

Now, if you want to run all together in a few minutes, just runs the Docker Compose Yaml file.
For a deeply explanation, follow the instructions on README.md (https://github.com/chilcano/docker-wso2bam-kismet-poc).

If everything is OK, you will get a huge amount of data (WIFI traffic) stored in Apache Cassandra and a simple Dashboard showing captured MAC Addresses and Manufacturer of the Wireless Devices (PC, Mobiles, WIFI Access Points, Tablets, etc..) around of your Raspberry Pi.

![Visualising 802.11 captured traffic with the MAC Address Manufacturer](https://github.com/chilcano/docker-wso2bam-kismet-poc/blob/master/chilcano-wso2bam-wifi-thrift-cassandra-5-kismet-toolbox-docker-manuf.png?raw=1 "Visualising 802.11 captured traffic with the MAC Address Manufacturer")

I hope you will find this blog posts useful. 
Bye.