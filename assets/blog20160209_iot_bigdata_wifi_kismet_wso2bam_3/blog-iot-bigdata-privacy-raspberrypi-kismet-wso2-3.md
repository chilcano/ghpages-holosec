# Everything generates data: Capturing WIFI anonymous traffic using Raspberry Pi and WSO2 BAM (Part III)

======>>>> Tweet:  Everything generates data: Capturing WIFI traffic (Part III) #WSO2BAM #Dashboard #RealTime #BigData #Cassandra



After configuring the Raspberry Pi in monitor WIFI/802.11 mode ([first blog post](https://holisticsecurity.wordpress.com/2016/02/02/everything-generates-data-capturing-wifi-anonymous-traffic-raspberrypi-wso2-part-i)) and after configuring Raspberry Pi to send the 802.11 captured traffic to WSO2 BAM and Apache Thrift listener ([second blog post](https://holisticsecurity.wordpress.com/2016/02/04/everything-generates-data-capturing-wifi-anonymous-traffic-using-raspberry-pi-and-wso2-bam-part-ii)), now I will explain how to create a simple Dashboard showing the WIFI traffic captured in real-time. 


_Architecture IoT/BigData – Visualizing WIFI traffic in realtime from a WSO2 BAM Dashboard_
![Architecture IoT/BigData – Visualizing WIFI traffic in realtime from a WSO2 BAM Dashboard](https://raw.githubusercontent.com/chilcano/wso2bam-wifi-thrift-cassandra-poc/master/chilcano-wso2bam-wifi-thrift-cassandra-0-architecture.png "Architecture IoT/BigData – Visualizing WIFI traffic in realtime from a WSO2 BAM Dashboard")


Well, to make it easier, I created a Github repository ([wso2bam-wifi-thrift-cassandra-poc](https://github.com/chilcano/wso2bam-wifi-thrift-cassandra-poc)) where I copied a number of scripts required for this third blog post.
I encourage you to download and follow the instructions below.

## This repository ([wso2bam-wifi-thrift-cassandra-poc](https://github.com/chilcano/wso2bam-wifi-thrift-cassandra-poc)) contains

- A toolbox to view incoming Kismet traffic (802.11) in realtime valid for WSO2 BAM 2.5.0.
- A set of definitions to create Execution Plan (CEP Shiddi), Input and Output Stream Definitions (Apache Thrift), and Formatters.

## Considerations

- I've used WSO2 BAM 2.5.0 (standard configuration without changes and with offset 0)
- I've used a Raspberry Pi as agent to send captured 802.11 traffic to WSO2 BAM by using Apache Thrift. 
- I've used a Python Thrift and Kismet script to send the captured traffic. 

## How to use

__1) Send Kismet traffic to WSO2 BAM using Apache Thrift listener__

- Follow this first blog post to configure your Device to capture anonymous 802.11 traffic.
https://holisticsecurity.wordpress.com/2016/02/02/everything-generates-data-capturing-wifi-anonymous-traffic-raspberrypi-wso2-part-i

- Follow this second blog post to send 802.11 captured traffic to WSO2 BAM / Apache Thrift listener.
https://holisticsecurity.wordpress.com/2016/02/04/everything-generates-data-capturing-wifi-anonymous-traffic-using-raspberry-pi-and-wso2-bam-part-ii

- Check if the Kismet Input Stream was created in WSO2 BAM - Input Streams. If so, WSO2 BAM is receiving the incoming Kismet traffic by Thrift listener.


__2) Deploy the WSO2 BAM Kismet toolbox__

- Deploy the kismet_wifi_realtime_traffic.tbox in WSO2 BAM.
- Check if WSO2 BAM toolbox was deployed successfully.


![Kismet Real Time Toolbox for WSO2 BAM](https://raw.githubusercontent.com/chilcano/wso2bam-wifi-thrift-cassandra-poc/master/chilcano-wso2bam-wifi-thrift-cassandra-1-toolbox.png "Kismet Real Time Toolbox for WSO2 BAM")


__3) Deploy the set of Stream and Execution Plan definitions__

Copy the set of definitions to create Execution Plan (CEP Shiddi), Input and Output Stream Definitions (Apache Thrift), and Formatters to WSO2 BAM manually.
All files and directories to be copied are under `wso2bam-wifi-thrift-cassandra-poc/wso2bam_defns/` and have to be copied to `<WSO2BAM_HOME>/`.


_Structure of file definitions and directories_
![Input/Output Stream, Execution Plan and Formatters for WSO2 BAM](https://raw.githubusercontent.com/chilcano/wso2bam-wifi-thrift-cassandra-poc/master/chilcano-wso2bam-wifi-thrift-cassandra-2-dirs.png "Input/Output Stream, Execution Plan and Formatters for WSO2 BAM")

_Two Output Streams deployed into WSO2 BAM_
![Input/Output Stream, Execution Plan and Formatters for WSO2 BAM](https://raw.githubusercontent.com/chilcano/wso2bam-wifi-thrift-cassandra-poc/master/chilcano-wso2bam-wifi-thrift-cassandra-3-defns.png "Input/Output Stream, Execution Plan and Formatters for WSO2 BAM")


__4) Visualizing Kismet (802.11) traffic in WSO2 BAM Dashboard__

If everything is OK, then you can see the incoming traffic in realtime, to do that, you have to use the previously installed/deployed WSO2 BAM toolbox.
Then, login to WSO2 BAM Dashboard and select the `Kismet WIFI Realtime Monitoring` graphic. You should see the following.


_Visualizing Captured Kismet Traffic in Realtime from WSO2 BAM Dashboard_
![Visualizing Captured Kismet Traffic in Realtime](https://raw.githubusercontent.com/chilcano/wso2bam-wifi-thrift-cassandra-poc/master/chilcano-wso2bam-wifi-thrift-cassandra-4-kismet.png "Visualizing Captured Kismet Traffic in Realtime")

That's all.
In the next blogpost I will explain how to implement a Microservice to get the Manufacturer for each MAC address captured.

Regards.


