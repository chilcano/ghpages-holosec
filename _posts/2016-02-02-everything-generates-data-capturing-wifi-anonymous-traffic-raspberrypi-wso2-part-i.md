---
layout:     post
title:      'Everything generates data: Capturing WIFI anonymous traffic using Raspberry Pi and WSO2 BAM (Part I)'
date:       2016-02-02 11:39:29
categories: ['Big Data', 'IoT', 'Security']
tags:       ['Apache Cassandra', 'Kismet', 'Privacy', 'Raspberry Pi']
status:     publish 
permalink:  "/2016/02/02/everything-generates-data-capturing-wifi-anonymous-traffic-raspberrypi-wso2-part-i/"
---
Yes, in this digital world, everything generates data, but before to do `BigData`, you have to follow these steps:
 **1\. Capture** : Acquires, Integrates data.  
 **2\. Store** : Classification, Consolidate, Transformation, Storage Design, etc.  
 **3\. Analysis** : Exploration, visualization, modeling, prediction, etc.

![Everything generates data - IoT, BigData, Privacy, Security]({{ site.baseurl }}/assets/chilcano-raspberrypi-bigdata-wifi-1-bigdata.jpg)  
 _Everything generates data - IoT, BigData, Privacy, Security_

<!-- more -->

In this first blog post I will explain how to capture anonymous [WIFI/802.11 traffic](https://en.wikipedia.org/wiki/IEEE_802.11) using a [Raspberry Pi 2 Model B](https://www.raspberrypi.org/products/raspberry-pi-2-model-b), Kismet ([An 802.11 layer2 wireless network detector, sniffer, and intrusion detection system](https://www.kismetwireless.net)) and in the second blog post I will use [WSO2 BAM 2.5.0](http://wso2.com/more-downloads/business-activity-monitor) to collect the anonymous WIFI traffic to generate a simple Dashboard showing data in live or real time.
The final idea is create a simple Dashboard showing the Mobile Devices as mobile phones identified around of the Raspberry Pi.  
Anyway, you can use this traffic for different purposes such as:  
* Monitor Shopping Activity  
* Vehicule Traffic Monitoring  
* Street Activity Monitoring, ...

![Architecture - Capturing WIFI anonymous traffic using Raspberry Pi and WSO2 BAM and WSO2 CEP]({{ site.baseurl }}/assets/chilcano-raspberrypi-bigdata-wifi-2-arch.png)  
 _Architecture - Capturing WIFI anonymous traffic using Raspberry Pi and WSO2 BAM and WSO2 CEP_
Well, now let's get down to work.

## I.- Enable `monitor` mode in Raspberry Pi

### 1\. Prepare the Raspberry Pi
Obviously, I have a clean image of Raspbian installed in my Raspberry Pi 2 Model B.  
The below steps explain how to prepare Raspberry Pi and install and configure Kismet to capture 802.11 anonymous traffic.
Before to do it, I have to prepare the Raspberry Pi, for example, configure a static IP address to Ethernet interface (`eth0`) to get SSH access remotely. After that, I can configure the Wireless interface (`wlan0`) and install Kismet.

**1.1) Get SSH access to Raspberry Pi**


```sh  
$ ssh pi@192.168.1.102  
pi@192.168.1.102's password:  
Linux rpi-chicha 3.18.11-v7+ #781 SMP PREEMPT Tue Apr 21 18:07:59 BST 2015 armv7l
The programs included with the Debian GNU/Linux system are free software;  
the exact distribution terms for each program are described in the  
individual files in /usr/share/doc/*/copyright.
Debian GNU/Linux comes with ABSOLUTELY NO WARRANTY, to the extent  
permitted by applicable law.  
Last login: Fri Jan 29 11:32:20 2016 from 192.168.1.43  
```  

**1.2) Connect the USB WIFI dongle**


```sh  
$ lsusb  
Bus 001 Device 002: ID 0424:9514 Standard Microsystems Corp.  
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub  
Bus 001 Device 003: ID 0424:ec00 Standard Microsystems Corp.  
Bus 001 Device 004: ID 148f:5370 Ralink Technology, Corp. RT5370 Wireless Adapter  
```  

Check if your WIFI dongle allows monitor mode.
_Note:_  
RTL8188CUS does not allow monitor mode.  
http://raspberrypi.stackexchange.com/questions/8578/enable-monitor-mode-in-rtl8188cus-realtek-wifi-usb-dongle

```sh  
$ ifconfig  
$ sudo ifconfig  
eth0 Link encap:Ethernet HWaddr b8:27:eb:1e:12:63  
inet addr:192.168.1.102 Bcast:192.168.1.255 Mask:255.255.255.0  
UP BROADCAST RUNNING MULTICAST MTU:1500 Metric:1  
RX packets:32177 errors:0 dropped:568 overruns:0 frame:0  
TX packets:1940 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:1000  
RX bytes:2495710 (2.3 MiB) TX bytes:187339 (182.9 KiB)
lo Link encap:Local Loopback  
inet addr:127.0.0.1 Mask:255.0.0.0  
UP LOOPBACK RUNNING MTU:65536 Metric:1  
RX packets:46 errors:0 dropped:0 overruns:0 frame:0  
TX packets:46 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:0  
RX bytes:4568 (4.4 KiB) TX bytes:4568 (4.4 KiB)
wlan0 Link encap:Ethernet HWaddr 00:13:ef:c0:21:2b  
UP BROADCAST MULTICAST MTU:1500 Metric:1  
RX packets:2394 errors:0 dropped:0 overruns:0 frame:0  
TX packets:29 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:1000  
RX bytes:207760 (202.8 KiB) TX bytes:3764 (3.6 KiB)  
```  

```sh  
$ sudo iwconfig wlan0  
wlan0 IEEE 802.11bgn ESSID:off/any  
Mode:Managed Access Point: Not-Associated Tx-Power=20 dBm  
Retry short limit:7 RTS thr:off Fragment thr:off  
Encryption key:off  
Power Management:off  

```

**1.3) Set static IP address to`eth0` and configure `wlan0` (optional)**

```sh  
$ sudo nano /etc/network/interfaces  
```  

Initial config.

```sh  
auto lo
iface lo inet loopback  
iface eth0 inet dhcp
allow-hotplug wlan0  
iface wlan0 inet manual  
wpa-roam /etc/wpa_supplicant/wpa_supplicant.conf  
iface default inet dhcp  
```  

Add and configure config for `eth0` and `wlan0`.

```sh  
auto lo
iface lo inet loopback
iface eth0 inet static  
address 192.168.1.102  
netmask 255.255.255.0  
network 192.168.1.0  
broadcast 192.168.1.255  
gateway 192.168.1.1
allow-hotplug wlan0  
auto wlan0  
iface wlan0 inet dhcp  
wpa-ssid "your-ssid"  
wpa-psk "your-password"  
```  

Reload the changes.

```sh  
$ sudo service networking reload  
```  

**1.4) Enable`wlan0` in monitor mode (option 1)**

Run these 2 commands together (*):

```sh  
$ sudo ifconfig wlan0 down;sudo iwconfig wlan0 mode monitor  
```  

Now, check if `wlan0` is working in mode monitor:

```sh  
$ sudo iwconfig wlan0  
wlan0 IEEE 802.11bgn Mode:Monitor Frequency:2.412 GHz Tx-Power=20 dBm  
Retry long limit:7 RTS thr:off Fragment thr:off  
Power Management:off
$ sudo ifconfig wlan0  
wlan0 Link encap:UNSPEC HWaddr 00-13-EF-C0-21-2B-70-78-00-00-00-00-00-00-00-00  
UP BROADCAST MULTICAST MTU:1500 Metric:1  
RX packets:764 errors:0 dropped:0 overruns:0 frame:0  
TX packets:8 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:1000  
RX bytes:81873 (79.9 KiB) TX bytes:1475 (1.4 KiB)  
```  

(*) The raspbian has a service called `ifplugd`. This `ifplugd` is a daemon which will automatically configure your ethernet device when it is plugged in and automatically unconfigure it if it's pulled.  
So, it does the device stay busy. Disabling it allow you to use ifconfig and iwconfig normally. Just use the comand:

```sh  
$ sudo service ifplugd stop  

[ ok ] Network Interface Plugging Daemon...stop eth0...stop wlan0...done.
$ sudo service ifplugd status  

[....] eth0: ifplugd not running.  

[....] wlan0: ifplugd not running.  

[info] all: device all is either not present or not functional.  
```  

**1.5) Enable`wlan0` in monitor mode (option 2)**

If above (option 1) configuration not worked, the you could try this alternative by using the `iw` scripts. Then, gonna try it.

```sh  
$ sudo apt-get install iw
$ sudo iw wlan0 info  
Interface wlan0  
ifindex 3  
type monitor  
wiphy 0  
```  

Add the `mon0` in `monitor` mode, a new network interface, instead of `wlan0`.

```sh  
$ sudo iw phy phy0 interface add mon0 type monitor  
```  

Check the interfaces associated to `phy0`.

```sh  
$ sudo iw dev  
phy#0  
Interface mon0  
ifindex 6  
wdev 0x4  
addr 74:f0:6d:4d:40:2f  
type monitor  
Interface wlan0  
ifindex 5  
wdev 0x3  
addr 74:f0:6d:4d:40:2f  
type managed  
channel 6 (2437 MHz), width: 20 MHz, center1: 2437 MHz  
```  

Now, we need to remove the `wlan0`. If you do that, proably the `mon0` interface will be restored to `managed` mode.

```sh  
$ sudo iw dev wlan0 del
$ sudo iw dev  
phy#0  
Interface mon0  
ifindex 8  
wdev 0x6  
addr 74:f0:6d:4d:40:2f  
type managed  
```  

But, to avoid above, you have to configure/set `monitor` mode properly with the `ifconfig` and `iwconfig` commands as follow.

```sh  
$ sudo ifconfig mon0 down  
$ sudo iwconfig mon0 mode monitor  
$ sudo ifconfig mon0 up  
```  

Now, if you check the interface in `monitor` mode, you should see this:

```sh  
$ sudo iw dev  
phy#0  
Interface mon0  
ifindex 8  
wdev 0x6  
addr 74:f0:6d:4d:40:2f  
type monitor  
channel 6 (2437 MHz), width: 20 MHz (no HT), center1: 2437 MHz  
```  

After that, check if `wlan0` or `mon0` are running in `monitor` mode, if so, then you are ready to start Kismet.

## II.- Install, configure and start Kismet

**2.1) Installation of Kismet**


```sh  
$ sudo apt-get update
$ sudo apt-get upgrade
$ sudo apt-get install libncurses5-dev libpcap-dev libpcre3-dev libnl-dev

# latest version - 2015.05.01  

$ wget http://www.kismetwireless.net/code/kismet-2013-03-R1b.tar.xz 
$ xz -d kismet-2013-03-R1b.tar.xz
$ tar -xf kismet-2013-03-R1b.tar
$ cd kismet-2013-03-R1b
$ ./configure
$ make
$ sudo make suidinstall
$ sudo usermod -a -G kismet pi
$ sudo reboot  
```  

**2.2) Configure Kismet**

Edit `/usr/local/etc/kismet.conf` to point at the WIFI adaptor configured in `monitor` mode, in this case to add `ncsource=mon0` or `ncsource=wlan0` and `hidedata=true`.

```sh  
$ sudo nano /usr/local/etc/kismet.conf  
```  

Download the manufacturer list. This is useful to identify the Wireless Interface Manufacturer.

```sh  
$ sudo mkdir -p /usr/share/wireshark/
$ cd /usr/share/wireshark/
$ sudo wget -O manuf http://anonsvn.wireshark.org/wireshark/trunk/manuf
$ sudo cp manuf /etc/manuf  
```  

**2.3) Start Kismet Server and Client**

If you have configured and updated `/usr/local/etc/kismet.conf`, then you can start Kismet running this command (without parameters):

```sh  
$ kismet_server  
```  

But, if you haven't configured `/usr/local/etc/kismet.conf` or you want to overwrite It, you can pass these parameters with below command, this will create a TCP listener on port `2501`:

```sh  
$ kismet_server -c wlan0
INFO: Not running as root - will try to launch root control binary (/usr/lo  
cal/bin/kismet_capture) to control cards.  
INFO: Started kismet_capture control binary successfully, pid 2517  
INFO: Reading from config file /usr/local/etc/kismet.conf  
debug - 2516 - child creating ipc fdfd  
INFO: No 'dronelisten' config line and no command line drone-listen  
argument given, Kismet drone server will not be enabled.  
INFO: Created alert tracker...  
INFO: Creating device tracker...  
INFO: Registered 80211 PHY as id 0  
INFO: Kismet will spend extra time on channels 1,6,11  
INFO: Kismet will attempt to hop channels at 3 channels per second unless  
overridden by source-specific options  
INFO: Matched source type 'rt2800usb' for auto-type source 'wlan0'  
INFO: Using hardware channel list 1:3,2,3,4,5,6:3,7,8,9,10,11:3,12,13,14,  
14 channels on source wlan0  
INFO: Source 'wlan0' will attempt to create and use a monitor-only VAP  
instead of reconfiguring the main interface  
ERROR: Detected the following processes that appear to be using the  
interface wlan0, which can cause problems with Kismet by changing  
the configuration of the network device: wpa_supplicant dhclient  
ifplugd. If Kismet stops running or stops capturing packets, try  
killing one (or all) of these processes or stopping the network for  
this interface.  
INFO: Created source wlan0 with UUID 52bee95c-c8df-11e5-9fa4-dc04bb23e201  
INFO: Will attempt to reopen on source 'wlan0' if there are errors  
INFO: Created TCP listener on port 2501  
INFO: Kismet drone framework disabled, drone will not be activated.  
INFO: Inserting basic packet dissectors...  
INFO: hidedata= set in Kismet config. Kismet will ignore the contents of  
data packets entirely  
INFO: Allowing Kismet frontends to view WEP keys  
INFO: Starting GPS components...  
INFO: Enabling reconnection to the GPS device if the link is lost  
INFO: Using GPSD server on localhost:2947  
INFO: Opened OUI file '/etc/manuf  
INFO: Indexing manufacturer db  
INFO: Completed indexing manufacturer db, 28150 lines 563 indexes  
INFO: Creating network tracker...  
INFO: Creating channel tracker...  
INFO: Registering dumpfiles...  
INFO: Pcap log in PPI format  
...  
```  

You can run Kismet Server as a Linux deamon.

```sh  
$ kismet_server -c wlan0 --daemonize
INFO: Not running as root - will try to launch root control binary (/usr/lo  
cal/bin/kismet_capture) to control cards.  
INFO: Started kismet_capture control binary successfully, pid 4027  
INFO: Reading from config file /usr/local/etc/kismet.conf  
Silencing output and entering daemon mode...  
debug - 4028 - child creating ipc fdfd  
```  

And now, start the Kismet Client. The Kismet Client will connect to Kismet Server automatically, because both are running in the same Raspberry Pi.

```sh  
$ kismet_client  
```  

_Kismet - Capturing 802.11 anonymous traffic using Raspberry Pi_  

![Kismet - Capturing 802.11 anonymous traffic using Raspberry Pi]({{ site.baseurl }}/assets/chilcano-raspberrypi-bigdata-wifi-3-kismet.png)  
_Kismet - Capturing 802.11 anonymous traffic using Raspberry Pi_

## III.- Common Kismet errors

_1) Error when start Kismet.`plugins` folder not found._

```sh  
ERROR: Failed to open primary plugin directory (/usr/local/lib/kismet/):  
No such file or directory  
ERROR: Failed to open user plugin directory (/home/pi/.kismet//plugins/):  
No such file or directory  
```  

```sh  
ERROR: Failed to open primary plugin directory (/usr/lib/kismet/): No such file or directory  
ERROR: Failed to open user plugin directory (/root/.kismet//plugins/): No such file or directory  

```

**Solution:**

```sh  
$ sudo mkdir -p /usr/local/lib/kismet/
$ mkdir -p /home/pi/.kismet/plugins/  
```  

```sh  
$ sudo mkdir -p /usr/lib/kismet/
$ mkdir -p /root/.kismet/plugins/  

```
_2) A process is using the wireless interface._
```sh  
ERROR: Didn't understand driver 'ath9k_htc' for interface 'mon0', but it  
looks like a mac80211 device so Kismet will use the generic options  
for it. Please post on the Kismet forum or stop by the IRC channel  
and report what driver it was.
ERROR: Detected the following processes that appear to be using the  
interface mon0, which can cause problems with Kismet by changing  
the configuration of the network device: ifplugd. If Kismet stops  
running or stops capturing packets, try killing one (or all) of  
these processes or stopping the network for this interface.  
```  

**Solution:**


```sh  
$ sudo pkill wpa_cli; sudo pkill ifplugd; sudo pkill wpa_supplicant  
```  

_3) The manufactur file doesn't exist._

```sh  
ERROR: Could not open OUI file '/etc/manuf': No such file or directory  
ERROR: Could not open OUI file '/usr/share/wireshark/wireshark/manuf': No  
such file or directory  
```  

**Solution:**


```sh  
$ sudo mkdir -p /usr/share/wireshark/
$ cd /usr/share/wireshark/
$ sudo wget -O manuf http://anonsvn.wireshark.org/wireshark/trunk/manuf
$ sudo cp manuf /etc/manuf  
```  

_4) VAP for mon0 wasn't created._

```sh  
ERROR: Not creating a VAP for mon0 even though one was requested, since  
the interface is already in monitor mode. Perhaps an existing  
monitor mode VAP was specified. To override this and create a new  
monitor mode vap no matter what, use the forcevap=true source option  
```  

**Solution:**  
Check if mon0 is being used for other process or restart and reconfigure your wireless interface.
