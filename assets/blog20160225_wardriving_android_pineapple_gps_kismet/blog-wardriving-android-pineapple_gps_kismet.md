
WIFI Pineapple Nano is a nice tiny device to do Wireless Security Auditing. It has [OpenWRT](https://openwrt.org) embedded as S.O. with 2 Wireless NIC pre-configured and a lot of Security tools pre-installed ready to perform a Security Wireless Auditing. For further details, you can check the Hak5 page and I encourage you to buy one (https://www.wifipineapple.com)!.

## Objetives

The idea is to do a quick wardriving around of the [Mobile World Congress at Barcelona](https://www.mobileworldcongress.com) to check if the attendants are aware about their Mobile Devices with the leak of information.

https://www.dropbox.com/s/mhju8245u82u26l/chilcano-00-android-gps-kismet-pineapple-adb.png

At the end, after wardriving, you will get the following files:

* xyz.alert
* xyz.gpsxml
* xyz.netxml
* xyz.pcapdump

With above files you will can identify the Manufacturer of the device (or model), the geo-position and route followed aproximadely and other information related signal quality.

## The arsenal

The software and devices I've used are the following:

* WIFI Pineapple Nano (https://www.wifipineapple.com/pages/nano)
* Android Mobile Phone's GPS (http://www.bq.com/es/aquaris-e6)
* Share GPS App for Android (https://play.google.com/store/apps/details?id=com.jillybunch.shareGPS)
* JuiceSSH App for Android (https://play.google.com/store/apps/details?id=com.sonelli.juicessh)
* Kismet (Client and Server) installed in WIFI Pineapple Nano (https://www.kismetwireless.net)
* Aukey 20000mAh Portable External Battery (http://www.amazon.es/dp/B00RJK8SH6)


https://www.dropbox.com/s/ny2zmz5gj3roywa/chilcano-01-android-mobile-pineapple-kismet.png


## Configuration


Obviously I have initialized my Pineapple Nano previously and have updated the firmware. But if you haven't already done so, I recommend the next guide: https://www.wifipineapple.com/pages/setup

The next steps are if you have initialized Pineapple Nano.


__1) Connect to WIFI Pineapple and prepare everything__


By using USB cable, connect the Pineapple Nano to your PC, in my case I'm using a Virtualbox VM with Kali Linux.
Then, from a Kali Linux ([the best Linux distro for Security Audit](https://www.kali.org)) terminal will get the `wp6.sh` script and will connect to Pineapple Nano.
The `wp6.sh` can be downloaded from here: https://github.com/hak5darren/wp6

https://www.dropbox.com/s/vf94bmpcixxj0r5/chilcano-02-config-1-connect-pineapple-wp6.png

After that, open your browser in your Kali Linux and connect to `http://172.16.42.1:1741`


From the Pineapple Web Admin console, insert the SD and format It.
After that, verify if SD was formatted suceesfully.
```
root@Pineapple:~# df -h
Filesystem                Size      Used Available Use% Mounted on
rootfs                    2.3M    900.0K      1.4M  39% /
/dev/root                12.5M     12.5M         0 100% /rom
tmpfs                    29.9M      3.6M     26.3M  12% /tmp
/dev/mtdblock3            2.3M    900.0K      1.4M  39% /overlay
overlayfs:/overlay        2.3M    900.0K      1.4M  39% /
tmpfs                   512.0K         0    512.0K   0% /dev
/dev/sdcard/sd1           6.2G     14.6M      5.9G   0% /sd
```

Now, open other Kali Linux terminal and get SSH access to Pinapple Nano and update the packages.

https://www.dropbox.com/s/ynbvvyzf4r8nu30/chilcano-02-config-2-connect-pineapple-ssh.png

```
root@Pineapple:~# opkg update

```

__2) Install Kismet in the Pineapple Nano__

Pineapple Nano has not enough space internally to install things. I recommend you to install your new applications or packages in the SD.

```
root@Pineapple:~# opkg list | grep kismet
kismet-client - 2013-03-R1b-1 - An 802.11 layer2 wireless network detector, sniffer, and intrusion detection system. This package contains the kismet text interface client.
kismet-drone - 2013-03-R1b-1 - An 802.11 layer2 wireless network detector, sniffer, and intrusion detection system. This package contains the kismet remote sniffing.and monitoring drone.
kismet-server - 2013-03-R1b-1 - An 802.11 layer2 wireless network detector, sniffer, and intrusion detection system. This package contains the kismet server.

root@Pineapple:~# # opkg --dest sd install kismet-server

root@Pineapple:~# # opkg --dest sd install kismet-client

```

__3) Sharing the Android's GPS with the WIFI Pineapple Nano__

To do this, we need to connect our Android Mobile to the Pineapple USB 2.0 Host port and from Android share the GPS signal by using ShareGPS app. Before, let's go to install ADB ([Android Debug Bridge](http://developer.android.com/tools/help/adb.html)) in the Pineapple.

```
root@Pineapple:~# opkg --dest sd install adb

Installing adb (android.5.0.2_r1-1) to sd...
Downloading https://www.wifipineapple.com/nano/packages/adb_android.5.0.2_r1-1_ar71xx.ipk.
Configuring adb.
```

Now, from your Pineapple SSH terminal start the ADB service and check if your Android Mobile is recognized for the Pineapple Nano. Before, to enable USB Debugging and enable USB Tethering in your Mobile.

My Android Mobile is recognized with `ID 2a47:0004`.
```
root@Pineapple:~# lsusb
Bus 001 Device 009: ID 2a47:0004
Bus 001 Device 004: ID 05e3:0745 Genesys Logic, Inc.
Bus 001 Device 003: ID 0cf3:9271 Atheros Communications, Inc. AR9271 802.11n
Bus 001 Device 002: ID 058f:6254 Alcor Micro Corp. USB Hub
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
```

Start ADB service in Pineapple Nano.
```
root@Pineapple:~# adb devices
* daemon not running. starting it now on port 5037 *
* daemon started successfully *
List of devices attached
AB010682	unauthorized
```

In your Android Mobile is propmted to accept connection from Pineapple. Accept It and run again `ADB`. You have to see the next as shown below.
```
root@Pineapple:~# adb devices
List of devices attached
AB010682	device
```

Now, in your Android install the ShareGPS app from Google Play Store. After that, start the app and share the GPS signal.

  ![Step 1](https://www.dropbox.com/s/igcoglt6n7cuf0c/chilcano-03-1-small-android-share-gps.png?raw=1)![Step 1](https://www.dropbox.com/s/o7pjrthh3eao4m4/chilcano-03-2-small-android-share-gps.png?raw=1)

  ![Step 1](https://www.dropbox.com/s/dtujytrac9owybg/chilcano-03-3-small-android-share-gps.png?raw=1)![Step 1](https://www.dropbox.com/s/2u1mw08gbl3bx0o/chilcano-03-4-small-android-share-gps.png?raw=1)

Finally, You are ready to use the shared Android's GPS signal. Let's to check it.
```
root@Pineapple:~# adb forward tcp:50000 tcp:50000

root@Pineapple:~# telnet localhost 50000

$GPGGA,181216.000,4138.5572,N,00221.8326,E,1,5,1.47,202.2,M,51.1,M,,*54
$GPGSA,A,3,19,25,24,15,12,,,,,,,,1.70,1.47,0.85*05
$GPGSV,3,1,10,12,65,031,34.4,25,61,286,31.7,24,54,116,14.2,14,43,295,*66
$GPGSV,3,2,10,29,26,197,25.8,02,21,102,26.9,19,17,041,13.4,06,16,061,17.8*7C
$GPGSV,3,3,10,15,09,174,20.0,31,03,303,*6A
$GPRMC,181216.000,A,4138.5572,N,00221.8326,E,0.000,104.39,250216,,,A*5B
$GPVTG,104.39,T,,M,0.000,N,0.000,K,A*32
$GPACCURACY,4.6*0A
$GPGGA,181217.000,4138.5572,N,00221.8326,E,1,5,1.47,202.2,M,51.1,M,,*55
$GPGSA,A,3,19,25,24,15,12,,,,,,,,1.70,1.47,0.85*05
$GPGSV,3,1,10,12,65,031,34.8,25,61,286,31.6,24,54,116,14.2,14,43,295,*6B
$GPGSV,3,2,10,29,26,197,23.7,02,21,102,26.9,19,16,041,13.4,06,16,061,17.8*74
$GPGSV,3,3,10,15,09,174,19.6,31,03,303,*66
$GPRMC,181217.000,A,4138.5572,N,00221.8326,E,0.000,104.39,250216,,,A*5A
$GPVTG,104.39,T,,M,0.000,N,0.000,K,A*32
$GPACCURACY,4.5*09
$GPGGA,181218.000,4138.5572,N,00221.8326,E,1,5,1.47,202.2,M,51.1,M,,*5A
$GPGSA,A,3,19,25,24,15,12,,,,,,,,1.70,1.47,0.85*05
$GPGSV,3,1,10,12,65,031,34.8,25,61,286,31.6,24,54,116,14.2,14,43,295,*6B
$GPGSV,3,2,10,29,26,197,21.6,02,21,102,26.4,19,16,041,13.4,06,16,061,13.8*7E
$GPGSV,3,3,10,15,09,174,19.3,31,03,303,*63
$GPRMC,181218.000,A,4138.5572,N,00221.8326,E,0.000,104.39,250216,,,A*55
$GPVTG,104.39,T,,M,0.000,N,0.000,K,A*32
^C
```

After running `telnet localhost 50000` you will see in `green` on the ShareGPS app connections "Connected" instead of "Listening". That verifies Pineapple connected to Android's GPS, also you will see some characters in your terminal demonstrating that everything works.


__4) Configuration of Kismet: `wlan1` in `monitor mode`__


The WIFI Pineapple Nano has 2 Wireless NICs: `wlan0` and `wlan1`. 
```
root@Pineapple:~# iwconfig
lo        no wireless extensions.

usb0      no wireless extensions.

wlan1     IEEE 802.11bgn  ESSID:off/any
          Mode:Managed  Access Point: Not-Associated   Tx-Power=20 dBm
          RTS thr:off   Fragment thr:off
          Encryption key:off
          Power Management:off

wlan0-1   IEEE 802.11bgn  Mode:Master  Tx-Power=17 dBm
          RTS thr:off   Fragment thr:off
          Power Management:off

wlan0     IEEE 802.11bgn  Mode:Master  Tx-Power=17 dBm
          RTS thr:off   Fragment thr:off
          Power Management:off

eth0      no wireless extensions.

br-lan    no wireless extensions.
```

I'm going to use `wlan1` to capture the 802.11 traffic, in fact, the `wlan1` will be configured in `monitor mode` as shown below:

```
root@Pineapple:~# ifconfig wlan1 down
root@Pineapple:~# iwconfig wlan1 mode monitor
root@Pineapple:~# ifconfig wlan1 up
```

Check again the wireless interfaces. The `wlan1` NIC should be in `monitor mode`.

```
root@Pineapple:~# iwconfig wlan1
wlan1     IEEE 802.11bgn  Mode:Monitor  Frequency:2.412 GHz  Tx-Power=20 dBm
          RTS thr:off   Fragment thr:off
          Power Management:off
```


__5) Configuration of Kismet: MAC Address Manufacturer__


Kismet by default hasn't the MAC Address Manufacturer database, I have to download it from the Wireshark web portal and copy it to `/sd`.

```
root@Pineapple:~# wget -O /sd/manuf http://anonsvn.wireshark.org/wireshark/trunk/manuf

root@Pineapple:~# ln -s /sd/manuf /etc/manuf
root@Pineapple:~# ln -s /sd/manuf /sd/etc/manuf
```

__6) Configuration of Kismet: Installing and configuring GPSd__

_`GPSd` is a service daemon that monitors one or more GPSes or AIS receivers attached to a host computer through serial or USB ports, making all data on the location/course/velocity of the sensors available to be queried on TCP port 2947 of the host computer._ (http://www.catb.org/gpsd)

`GPSd` is not available in the current repository of OpenWRT ([Chaos Calmer 15.05](https://downloads.openwrt.org/chaos_calmer/15.05)) used for WIFI Pineapple Nano. Not problem, we can install the all GPSd packages for an older version of OpenWRT.
The packages to be installed are:

* libgps_3.7-1_ar71xx.ipk
* libgpsd_3.7-1_ar71xx.ipk
* gpsd_3.7-1_ar71xx.ipk 
* gpsd-clients_3.7-1_ar71xx.ipk


Download and install them.
```
root@Pineapple:~# cd /sd
root@Pineapple:/sd# wget https://downloads.openwrt.org/attitude_adjustment/12.09/ar71xx/generic/packages/libgps_3.7-1_ar71xx.ipk
root@Pineapple:/sd# wget https://downloads.openwrt.org/attitude_adjustment/12.09/ar71xx/generic/packages/libgpsd_3.7-1_ar71xx.ipk
root@Pineapple:/sd# wget https://downloads.openwrt.org/attitude_adjustment/12.09/ar71xx/generic/packages/gpsd_3.7-1_ar71xx.ipk 
root@Pineapple:/sd# wget https://downloads.openwrt.org/attitude_adjustment/12.09/ar71xx/generic/packages/gpsd-clients_3.7-1_ar71xx.ipk

root@Pineapple:/sd# opkg --dest sd install libgps_3.7-1_ar71xx.ipk
root@Pineapple:/sd# opkg --dest sd install libgpsd_3.7-1_ar71xx.ipk
root@Pineapple:/sd# opkg --dest sd install gpsd_3.7-1_ar71xx.ipk 
root@Pineapple:/sd# opkg --dest sd install gpsd-clients_3.7-1_ar71xx.ipk
```

Just make sure what `gpsd` service not starting on boot.
You can edit `/etc/default/gpsd` and set everything to `false` and/or run `service gpsd stop`.
```
root@Pineapple:~# nano /etc/default/gpsd
```

```
# Default settings for the gpsd init script and the hotplug wrapper.

# Start the gpsd daemon automatically at boot time
START_DAEMON="false"

# Use USB hotplugging to add new USB devices automatically to the daemon
USBAUTO="false"

# Devices gpsd should collect to at boot time.
# They need to be read/writeable, either by user gpsd or the group dialout.
DEVICES=""

# Other options you want to pass to gpsd
GPSD_OPTIONS=""
```

Now, start the `GPSd` service. In debug mode:
```
root@Pineapple:~# gpsd -F /var/run/gpsd.sock -N tcp://localhost:50000
```
Or run it as daemon.
```
root@Pineapple:~# gpsd -F /var/run/gpsd.sock tcp://localhost:50000
```

Where:

* `-F /var/run/gpsd.sock` is the control socket file being used for GPSd.
* `tcp://localhost:50000` is the shared Andoid's GPS signal being listening on TCP port.
* `-N` don't daemonize, useful for debugging.

And to check if GPSd is working, just run this:
```
root@Pineapple:~# cgps
```

__7) Starting Kismet for the first time__

Now, we are ready to start Kismet.
Kismet is a Client/Server application, firstly, I will start the Kismet Server and secondly the Kismet Client.

```
root@Pineapple:~# kismet_server -p /sd/.kismet -c wlan1 --daemonize
```

Where:

* `-p /sd/.kismet` is the folder where Kismet Server will place the log files.
* `-c wlan1` is the NIC in `monitor mode` to be used.
* Kismet has by default the GPSd configured properly. Nothing to fo here.
* Kismet will look for the MAC Address Manufacturer file in this path `/etc/manuf`.


In other terminal run the next:

```
root@Pineapple:~# kismet_client
```

You should see the Kismet UI showning the Wireless Networks and connected clientes identified and reading the GPS coordinates in real-time.


__8) Running Kismet for the next times__


Now, if you want to avoid run all above commands one-by-one, you could create a shell script with all required commands.
Then, let's to create a simple and dirty shell script.

```
root@Pineapple:~# nano /root/run_wardriving.sh

#!/bin/bash

echo "==> Setting wlan1 in monitor mode"
ifconfig wlan1 down
iwconfig wlan1 mode monitor
ifconfig wlan1 up

echo "==> Enabling ADB Forwarding to tcp:50000"
adb forward tcp:50000 tcp:50000

echo "==> Refreshing NTP server"
killall ntpd
ntpd > /dev/null 2>&1 &
sleep 3

echo "==> Starting GPSD with tcp://localhost:50000"
service gpsd stop
killall gpsd
gpsd -F /var/run/gpsd.sock tcp://localhost:50000
sleep 3

echo "==> Starting Kismet server in background"
killall kismet_server
kismet_server -p /sd/.kismet -c wlan1 --daemonize
```

And executions privileges.
```
root@Pineapple:~# chmod +x /root/run_wardriving.sh
```

The `run_wardriving.sh` will be useful when starting Kismet for the next times, because you have to do from your Android mobile and don't from your PC or Kali Linux.
You will need something likes that shell script to start wardriving quickly and from your Android mobile. 
For that, you will need also [JuiceSSH App](https://play.google.com/store/apps/details?id=com.sonelli.juicessh) in your Android mobile to connect to WIFI Pineapple Nano and execute the `run_wardriving.sh` script.  ;)


## Some results

Below some screenshots taken from my Android mobile and Google Earth with the WIFI Networks placed.

__1) Kismet in action__

[![Kismet in action](https://www.dropbox.com/s/c2cyzuecz1gl8jr/chilcano-04-1-small-screenshot-kismet-in-action.png?raw=1)](https://www.dropbox.com/s/qu5vf4b2e4wj18e/__chilcano-04-1-screenshot-kismet-in-action.png)[![693 Wireless Networks indentified](https://www.dropbox.com/s/shtqyzmxkqu7zv2/chilcano-04-2-small-screenshot-kismet-693-networks.png?raw=1)](https://www.dropbox.com/s/imgoqoph36ecjca/__chilcano-04-2-screenshot-kismet-693-networks.png)[![Ops!, A rogue AP?](https://www.dropbox.com/s/ezyy5qeqhqywm7a/chilcano-04-3-small-screenshot-kismet-rogue-ap.png?raw=1)](https://www.dropbox.com/s/mjhaut8rmju9ibg/__chilcano-04-3-screenshot-kismet-rogue-ap.png)

__2) Creating Wireless Recon Maps with Google Earth and GISKismet__

_GISKismet is a wireless recon visualization tool to represent data gathered using Kismet in a flexible manner. GISKismet stores the information in a database so that the user can generate graphs using SQL. GISKismet currently uses SQLite for the database and GoogleEarth / KML files for graphing._ (http://git.kali.org/gitweb/?p=packages/giskismet.git)

[vimeo https://vimeo.com/156948434]

## Conclusions


* WIFI Pineapple has a tons of Security tools installed, but not tools to perform a Wardriving. For that Kismet and GPS were installed and configured. [Probably in the following blog post I will explain how to create a `Module`](https://www.wifipineapple.com/modules) for Wardriving.
* __Turn off your phone mobile__. The new mobiles or smartphones (Android, iOS, Windows, ...) have a Wireless Network Interface, they are constantly trying to connect to Wireless networks, to do that, all Mobiles send packets asking for Access Points. Kismet or other Tools taken advantage of that by capturing these packets. If you want to avoid that, just turn off your Wireless Network Interface and avoid to connect to Wireless Access Point unknown.
* __The tip of the iceberg__. The packets that your phone Mobile emits and captures Kismet is only basic information and does not represent any risk to you. This phase is called Security "Data gathering" or "Recognition". The problem comes later, there are other tools that allow a targeted attack to steal important information.
* __The business behind of IoT and BigData__. Actually, Marketing Companies and Telecoms are monetizing with the tracking information of phone mobiles. They are taking advantage of radio/cellular signal to track in all the time. With these information the Shopping Company could identify your pattern of behavior, which stores visited, what phone mobile model do you have, etc. Only search in Google "phone mobile wireless anonymous tracking" and will be aware of the industry behind and who is making money.
* __There isn't Security awareness at Mobile World Congress__. Large conferences where there are people crowd (and thousands of mobiles phone) are a breeding ground for scammers and thieves who take advantage of weaknesses or defects of the Organization, the Devices or the Apps. Then, my friend be careful with that.

