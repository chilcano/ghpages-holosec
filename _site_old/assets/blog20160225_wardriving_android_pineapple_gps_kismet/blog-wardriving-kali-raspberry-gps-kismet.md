

## I. Raspberry Pi


### I.1. ADB on Raspberry Pi


The Raspbian on my Raspberry Pi 2 B is based on Debian Wheezy, which comes with the stable but outdated GCC 4.6 as the default C and C++ compiler.

```
$ gcc --version
gcc (Debian 4.6.3-14+rpi1) 4.6.3
Copyright (C) 2011 Free Software Foundation, Inc.
This is free software; see the source for copying conditions.  There is NO
warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
```

If I download and compile the ADB source code on my Raspberry, I will get the below error:

```
$ git clone https://github.com/DeepSilence/adb-arm

$ cd adb-arm

$ sh adb-download-make.sh

>> >>> ADB for ARM <<<


>> Downloading necessay files (android-5.1.1_r33 branch)
...
...
>> Copying makefile into system/core/adb...


>> Make...

rm -rf adb.o fdevent.o adb_client.o commandline.o console.o file_sync_client.o get_my_path_linux.o services.o sockets.o transport.o transport_local.o transport_usb.o usb_linux.o usb_vendors.o adb_auth_host.o list.h socket_inaddr_any_server.o socket_local_client.o socket_local_server.o socket_loopback_client.o socket_loopback_server.o socket_network_client.o load_file.o centraldir.o zipfile.o adler32.o compress.o crc32.o deflate.o infback.o inffast.o inflate.o inftrees.o trees.o uncompr.o zutil.o
gcc -O2 -g -Wall -Wno-unused-parameter -DADB_HOST=1 -DHAVE_FORKEXEC=1 -DHAVE_SYMLINKS -DHAVE_TERMIO_H -DHAVE_SYS_SOCKET_H -D_GNU_SOURCE -D_XOPEN_SOURCE -D_FILE_OFFSET_BITS=64 -std=c++11  -I. -I../include -I../../../external/zlib -I../../../external/openssl/include -I../base/include   -c -o adb.o adb.c
cc1: error: unrecognized command line option ‘-std=c++11’
<builtin>: recipe for target 'adb.o' failed
make: *** [adb.o] Error 1

>> Copying adb back into current dir...

cp: cannot stat `adb': No such file or directory

>> FINISH!
```

To avoid this error we have to update the GCC compiler to a new version as GCC 4.9 which has a complete C++11 implementation and partial support for C++14. Then, to do that, follow the next instructions:
```
$ sudo apt-get update
$ sudo apt-get upgrade
```
Now, edit /etc/apt/sources.list` and replace Wheezy with Jessie.
```
$ sudo nano /etc/apt/sources.list
```

Now, update the packages, and after that, install GCC 4.9.
```
$ sudo apt-get update

$ sudo apt-get install gcc-4.9 g++-4.9
```

The last step is to revert back from Jessie to Wheezy. After that do an update of your package list again.
```
$ sudo apt-get update
```

Now, install a cross-compiler for your Rasbpberry Pi.
```
$ wget http://downloads.yoctoproject.org/releases/yocto/yocto-1.5/toolchain/x86_64/poky-eglibc-x86_64-core-image-sato-armv7a-vfp-neon-toolchain-1.5.sh
$ sudo chmod a+x poky-eglibc-x86_64-core-image-sato-armv7a-vfp-neon-toolchain-1.5.sh

```




Finally, You are ready to try again with ADB compilation for Raspberry Pi.
``
$ sh adb-download-make.sh

```




$ sudo apt-get install build-essential libsdl1.2-dev libbz2-dev zlib1g-dev liblua5.1.0-dev git scons libfftw3-dev


========================================================================================================
## II. Kali Linux
========================================================================================================

### II.1. Sharing Android's GPS__

__1) ADB (Android Debug Bridge) in Kali Linux__

# apt-get install android-tools-adb

# adb version
Android Debug Bridge version 1.0.31

Now, check if ADB sees to your Android Mobile. Connect your Android Mobile to your Kali Linux using a USB cable.
Active USB Debugging in your Android Mobile.

Your Android mobile (> Android 4.2.2) will ask to allow ADB to connect (Kali Linux). 
You have to confirm RSA fingerprint pop-up dialog on your Android to allow ADB connection.
After that, in your Kali Linux run the next:

# adb devices
List of devices attached 
AB010682	device


__2) GPSD tools in Kali Linux__

# apt-get install gpsd

Edit the `/etc/default/gpsd` and disable everything:

```
# Default settings for the gpsd init script and the hotplug wrapper.

# Start the gpsd daemon automatically at boot time
START_DAEMON="false"

# Use USB hotplugging to add new USB devices automatically to the daemon
USBAUTO="true"

# Devices gpsd should collect to at boot time.
# They need to be read/writeable, either by user gpsd or the group dialout.
DEVICES=""

# Other options you want to pass to gpsd
GPSD_OPTIONS=""
```

The GPSD deamon should be stopped. If GPSD is running, stop it or kill it.

```
# service gpsd status
● gpsd.service - GPS (Global Positioning System) Daemon
   Loaded: loaded (/lib/systemd/system/gpsd.service; static)
   Active: inactive (dead)
``

__3) Test your GPSD__

First of all, in your Android Mobile, install `Share GPS` app to share the GPS signal with Kali Linux.
Open `Share GPS` app, go to Connections and Add a new NMEA using USB. 
At the end, the GPS signal will be available by `tcp` over the `50000` port.

In your Kali Linux, run ADB to forward the `50000` port.

```
# adb forward tcp:50000 tcp:50000
* daemon not running. starting it now on port 5037 *
* daemon started successfully *
```

Now, the Android's GPS signal can be listened from your Kali Linux on `tcp://localhost:50000`.
To check if Kali Linux can listen for the GPS signal, run this:

```
# gpsd -N -n -D 4 tcp://localhost:50000
```

After testing, stop it.

## I.2. Set your Wireless NIC in monitor mode

__1) Set wlan0 in monitor mode__

# 


## I.3. Kismet

__1) Install Kismet__

__2) Download MAC Address Manufacture DB__


__3) Starting Kismet to capture 802.11 traffic__



## I.V. Final bash script

```bash
#!/bin/bash

echo "==> Setting wlan0 in monitor mode"
ifconfig wlan0 down
iwconfig wlan0 mode monitor
ifconfig wlan0 up

echo "==> Enabling ADB Forwarding to tcp:50000"
adb forward tcp:50000 tcp:50000

echo "==> Refreshing NTP server"
service ntp restart
sleep 5

echo "==> Starting GPSD with tcp://localhost:50000"
service gpsd stop
gpsd -N tcp://localhost:50000 -F /var/run/gpsd.sock &
sleep 5

echo "==> Starting Kismet server in background"
kismet_server -p /root/kismet_logs -c wlan0 --daemonize
```









----- Para Pinaple

```
#!/bin/bash

echo "==> Setting wlan1mon in monitor mode"
echo ""
#ifconfig wlan1mon down
#iwconfig wlan1mon mode monitor
#ifconfig wlan1mon up

echo "==> Enabling ADB Forwarding to tcp:50000"
echo ""
#killall adb
adb forward tcp:50000 tcp:50000

echo "==> Refreshing NTP server"
echo ""
killall ntpd
ntpd > /dev/null 2>&1 &
sleep 5

echo "==> Starting GPSD with tcp://localhost:50000"
echo ""
killall gpsd
gpsd -N tcp://localhost:50000 -F /var/run/gpsd.sock &
sleep 5

echo "==> Starting Kismet server in background"
echo ""
killall kismet_server
#kismet_server -p /sd/.kismet -c wlan1mon --daemonize
kismet_server -f /sd/kismet.conf --daemonize
```






