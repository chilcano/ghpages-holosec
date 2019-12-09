---
layout:     post
title:      'Provisioning massively cross-compiled binaries to Raspberry Pi (arm) using Vagrant, VirtualBox, Ansible and Python'
date:       2016-04-12 15:46:31
categories: ['DevOps', 'IoT', 'Linux']
tags:       ['Ansible', 'ARM', 'Python', 'Raspberry Pi', 'Vagrant']
status:     publish 
permalink:  "/2016/04/12/provisioning-massively-crosscompiled-bin-rpi-armv7-using-vagrant-virtualbox-ansible-python/"
---
If you are involved in an IoT or Mobile Application provisioning Project you probably need build a mechanism to spread your application binaries to all Devices on stock and to all the rolled out Devices.
With this Proof-of-concept I will shown you how to build the app binary provisioning system for your custom platform, in this case I'm going to use Raspberry Pi (ARM processor) quickly avoiding perform unnecessary tasks and providing also an ARM cross-compiling platform.

![blog-cross-compiling-kismet-raspberrypi-arm.png]({{ site.baseurl }}/assets/blog-cross-compiling-kismet-raspberrypi-arm.png)

<!-- more -->

To implement this I will use Vagrant to create an Ubuntu VM mounts the Raspbian OS image internally ready to be used for ARM cross-compiling. There is a special part in this blog post where explains how to NFS mount to provide remote booting for all Raspberry Pi's connected to same network.
I provide a new Github repository with all the updated scripts required for this PoC. You can download from here:  
I would like to mention that this work is based on `https://github.com/twobitcircus/rpi-build-and-boot` where I've created a Vagrantfile for VirtualBox, tweaked the Ansible Playbook and I have documented the process I've followed to make it work successfully in my environment (VirtualBox instead of Parallels and booting from NFS).

## Requirements:
I'm using a Mac OS X (El Capitan - Version 10.11.3) with the next tools:
  * VirtualBox 5.0.16
  * Vagrant 1.8.1
  * Ansible 2.0.1.0 (installed via Pip)
  * Python 2.7.11
  * Raspberry Pi 2 Model B 
  * Raspbian OS (2015-09-24-raspbian-jessie.img)
  * OpenFramework for cross-compiling (http://openframeworks.cc)

### Why _Ansible_ instead of other configuration management tools ?
Why Ansible (http://docs.ansible.com/ansible/intro_installation.html) instead of other configuration management tools as Puppet, Chef, ...?. Because, Ansible is simple and agentless; you can use it with just with a simple SSH terminal, nothing special is required to be installed in the Host, also because it is written in Python and as you have seen in my previous post, I'm using intensively Python and it is becoming my favorite programming language. You can install Ansible using the same Python installation tools and obviously, you can `import ansible` from your Python scripts.  
To install Ansible on Mac OS X (El Capitan - Version 10.11.3) is easy, just follow these steps:

```sh  
$ sudo easy_install pip  
$ sudo pip install ansible --quiet
// upgrading Ansible and Pip  
$ sudo pip install ansible --upgrade  
$ sudo pip install --upgrade pip  
```


## Preparing the Raspberry Pi

**1\. Copy RPi image to SD**

Identify the disk (not partition) of your SD card, unmount and copy the image there:

```sh  
$ diskutil list  
$ diskutil unmountDisk /dev/disk2  
$ cd /Users/Chilcano/Downloads/@isos_vms/raspberrypi-imgs  
$ sudo dd bs=1m if=2015-09-24-raspbian-jessie.img of=/dev/rdisk2  
```

**2\. Connect the Raspberry Pi directly to your Host (MAC OS X)**

Using an ethernet cable, connect your Raspberry Pi to your Host, in my case I've a MAC OS X and I'm going to share my WIFI Network connection.  
Then, enabling `Internet Sharing` and the "Thunderbolt Ethernet" an IP address will be assigned to the Raspberry Pi, also Raspberry Pi will have Internet access/Network access and the MAC OS X can connect via SSH to the Raspberry Pi.  
All that will be possible without a hub, switch, router, screen or keyboard, etc. This will be useful, because we are going to install new software in Raspberry Pi.
After connect your Raspberry Pi to your MAC OS X, turn on by connecting an USB cable, in your MAC OS X open a Terminal and issue a SSH command, before re-generate the SSH keys.
Note that the default hostname of any Raspberry Pi is `raspberrypi.local`.

```sh  
// cleaning existing keys  
$ ssh-keygen -R raspberrypi.local
// connect to RPi using `raspberry` as default password  
$ ssh pi@raspberrypi.local  
```

After connecting, you will check the assigned IP address and the shared Internet Connection. Now, check out your connection.

```sh  
pi@raspberrypi:~ $ ping www.docker.com  
PING www.docker.com (104.239.220.248) 56(84) bytes of data.  
64 bytes from 104.239.220.248: icmp_seq=1 ttl=49 time=212 ms  
64 bytes from 104.239.220.248: icmp_seq=2 ttl=49 time=214 ms  
^C  
\--- www.docker.com ping statistics ---  
2 packets transmitted, 2 received, 0% packet loss, time 6970ms  
rtt min/avg/max/mdev = 207.205/213.294/217.893/3.513 ms  
```

**3\. Configure your RPi**

Boot your RPi and open a shell. Then enter:

```sh  
pi@raspberrypi:~ $ sudo raspi-config  
```

In the `raspi-config` menu, select `Option 1 Expand Filesystem`, change Keyboard layout, etc. and reboot.
Just if `mirrordirector.raspbian.org` mirror is not available, remove `http://mirrordirector.raspbian.org/raspbian/` repository and add a newest.

```sh  
pi@raspberrypi ~ $ sudo nano /etc/apt/sources.list

#deb http://mirrordirector.raspbian.org/raspbian/ jessie main contrib non-free rpi  
deb http://ftp.cica.es/mirrors/Linux/raspbian/raspbian/ jessie main contrib non-free rpi

# Uncomment line below then 'apt-get update' to enable 'apt-get source'  

#deb-src http://archive.raspbian.org/raspbian/ jessie main contrib non-free rpi  
```

**4\. Install OpenFrameworks tools and dependencies into Raspberry Pi**

Download and unzip OpenFrameworks into RPi under `/opt`.

```sh  
pi@raspberrypi:~ $ cd /opt  
pi@raspberrypi:/opt $ sudo wget http://openframeworks.cc/versions/v0.9.0/of_v0.9.0_linuxarmv7l_release.tar.gz  
pi@raspberrypi:/opt $ sudo tar -zxf of_v0.9.0_linuxarmv7l_release.tar.gz  
pi@raspberrypi:/opt $ sudo rm of_v0.9.0_linuxarmv7l_release.tar.gz  
```

Now, update the dependencies required when cross-compiling by running `install_dependencies.sh`.

```sh  
pi@raspberrypi:~ $ sudo /opt/of_v0.9.0_linuxarmv7l_release/scripts/linux/debian/install_dependencies.sh  
```

Now, compile oF, compile and execute an oF example.

```sh  
// compiling oF  
pi@raspberrypi:~ $ sudo make Release -C /opt/of_v0.9.0_linuxarmv7l_release/libs/openFrameworksCompiled/project  
...  
se/libs/openFrameworksCompiled/lib/linuxarmv7l/obj/Release/libs/openFrameworks/math/ofMatrix4x4.o /opt/of_v0.9.0_linuxarmv7l_release/libs/openFrameworksCompiled/lib/linuxarmv7l/obj/Release/libs/openFrameworks/math/ofQuaternion.o /opt/of_v0.9.0_linuxarmv7l_release/libs/openFrameworksCompiled/lib/linuxarmv7l/obj/Release/libs/openFrameworks/math/ofVec2f.o  
HOST_OS=Linux  
HOST_ARCH=armv7l  
checking pkg-config libraries: cairo zlib gstreamer-app-1.0 gstreamer-1.0 gstreamer-video-1.0 gstreamer-base-1.0 libudev freetype2 fontconfig sndfile openal openssl libpulse-simple alsa gtk+-3.0  
Done!  
make: Leaving directory '/opt/of_v0.9.0_linuxarmv7l_release/libs/openFrameworksCompiled/project'
// executing an example  
pi@raspberrypi:~ $ sudo make -C /opt/of_v0.9.0_linuxarmv7l_release/apps/myApps/emptyExample  
pi@raspberrypi:~ $ cd /opt/of_v0.9.0_linuxarmv7l_release/apps/myApps/emptyExample  
pi@raspberrypi /opt/of_v0.9.0_linuxarmv7l_release/apps/myApps/emptyExample $ bin/emptyExample  
```

**5\. Make an new image file from the existing and updated Raspberry Pi**

Remove the SD card from the Raspberry Pi, insert the SD card in your Host (in my case is MAC OS X) and use `dd` to make an new image file.

```sh  
$ diskutil list  
$ diskutil unmountDisk /dev/disk2  
$ sudo dd bs=1m if=/dev/rdisk2 of=2015-09-24-raspbian-jessie-of2.img
15279+0 records in  
15279+0 records out  
16021192704 bytes transferred in 381.968084 secs (41943799 bytes/sec)  
```

_Very important_ :
  * The `2015-09-24-raspbian-jessie-of.img` will be `shared` and after `mounted` from the guest VM, for that, set the user and permissions to `2015-09-24-raspbian-jessie-of.img` as shown below:

```sh  
$ sudo chmod +x 2015-09-24-raspbian-jessie-of2.img  
$ sudo chown Chilcano 2015-09-24-raspbian-jessie-of2.img
$ ls -la  
total 110439056  
drwxr-xr-x 33 Chilcano staff 1122 Apr 11 19:12 ./  
drwxr-xr-x 35 Chilcano staff 1190 Mar 23 19:26 ../  
-rwxr-xr-x 1 Chilcano staff 16021192704 Apr 11 17:29 2015-09-24-raspbian-jessie-of1.img*  
-rwxr-xr-x 1 Chilcano staff 16021192704 Apr 11 19:19 2015-09-24-raspbian-jessie-of2.img*  
-rwxr-xr-x 1 Chilcano staff 4325376000 Apr 11 17:02 2015-09-24-raspbian-jessie.img*  
-rwxr-xr-x 1 Chilcano staff 16021192704 Mar 31 12:31 2016-03-18-raspbian-jessie-of1.img*  
-rwxr-xr-x 1 Chilcano staff 4033871872 Apr 5 16:31 2016-03-18-raspbian-jessie.img*  
...  
```


## Building the Vagrant box

**1\. In your MAC OS X, to clone the`rpi-build-and-boot` github repository**

```sh  
$ git clone https://github.com/twobitcircus/rpi-build-and-boot  
$ cd rpi-build-and-boot  
```

Copy/Move the newest RPi image created above into `rpi-build-and-boot` folder.

```sh  
$ mv /Users/Chilcano/Downloads/@isos_vms/raspberrypi-imgs/2015-09-24-raspbian-jessie-of2.img .  
```

**2\. Install Vagrant and vbguest plugin into MAC OS X**

```sh  
$ wget https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1.dmg  
$ vagrant plugin install vagrant-vbguest  
```

**3\. Create a new`Vagrantfile` with VirtualBox as provider in the same folder `rpi-build-and-boot`**

```ruby  

# -*- mode: ruby -*-  

# vi: set ft=ruby :
Vagrant.configure(2) do |config|  

# https://atlas.hashicorp.com/ubuntu/boxes/trusty64 [Official Ubuntu Server 14.04 LTS (Trusty Tahr) builds]  
config.vm.box = "ubuntu/trusty64"  
config.vm.provider "virtualbox" do |vb|  
config.vbguest.auto_update = true  
vb.customize ["modifyvm", :id, "\--memory", "6144"]  
vb.customize ["modifyvm", :id, "\--cpus", "4"]  
end  

# If you want to use this system to netboot Raspberry Pi, then uncomment this line  

#config.vm.network "public_network", bridge: "en4: mac-eth0", ip: "10.0.0.1"  
config.vm.network "public_network", bridge: "ask", ip: "10.0.0.1"  
config.vm.provision "ansible" do |ansible|  
ansible.playbook = "playbook.yml"  
end  
end  
```

**4\. Getting`boot` and `root` partitions offsets to do loop mounting in Vagrant**

Using `./tool.py offsets` I will get the offsets of the `boot` and `root` partitions, after getting offset, copy the output of this tool to the top of `playbook.yml`.  
To run `tool.py` in MAC OS X, you will need `Python` configured.

```sh  
$ ./tool.py offsets 2015-09-24-raspbian-jessie-of2.img
image: 2015-09-24-raspbian-jessie-of2.img  
offset_boot: 4194304  
offset_root: 62914560  
```

The idea to loop-mount the RPi image is to create a full structure of directories and files of a Raspberry Pi distribution under a mounting-point in a Vagrant box. This structure is required to do `cross-compiling` and move/copy new binaries and ARM cross-compiled binaries.

**5\. Mounting Raspberry Pi image and booting from Vagrant using NFS**

Using `./tool.py netboot image.img /dev/rdiskX [--ip=10.0.0.Y]` you will copy just the `boot` partition in a new and tiny SD card.  
This new SD card with a fresh `boot` partition will be useful to boot from the network/remotely. The RPi will download the `root` partition from Vagrant, in fact, Vagrant will be sharing the custom RPi image (`2015-09-24-raspbian-jessie-of2.img`) via NFS to any Raspberry Pi connected to same network and having a pre-loaded `boot` partition.
The idea behind is to provision a custom RPi image massively avoiding to waste time copying and creating SD card for each Raspberry Pi. Also, this method is useful to provision software, configuration, packages, or in my case, provide cross-compiled software for ARM architectures massively.

```sh  
$ diskutil list
// a new SD on disk3 will be used  
$ diskutil unmountDisk /dev/disk3
$ ./tool.py netboot 2015-09-24-raspbian-jessie-of2.img /dev/rdisk3
2015-09-24-raspbian-jessie-of2.img /dev/rdisk3 10.0.0.101  
The following partitions will be destroyed  
/dev/disk3 (external, physical):  

#: TYPE NAME SIZE IDENTIFIER  
0: FDisk_partition_scheme *4.0 GB disk3  
1: Windows_FAT_32 boot 58.7 MB disk3s1  
2: Linux 3.9 GB disk3s2
are you sure? y  
OK  
Unmount of all volumes on disk3 was successful  
sudo dd if=2015-09-24-raspbian-jessie-of2.img of=/dev/rdisk3 bs=62914560 count=1  
Password:  
1+0 records in  
1+0 records out  
62914560 bytes transferred in 6.846875 secs (9188799 bytes/sec)  
Disk /dev/rdisk3 ejected  
```

Note that `tool.py netboot` automatically will assigns to RPi the `10.0.0.101` as IP address and `8.8.8.8` and `8.8.4.4` as DNS servers to `eth0`.  
You can check or modify previously these values by editing the `cmdline.txt` file placed in the `boot` RPi partition. You can edit it from a running Raspberry Pi or from a mounted partition.

**6\. Download and unzip oF (OpenFramework) into`rpi-build-and-boot` folder**

If you forgot copy OpenFramework in your RPi, you can do now. Using the Ansible `playbook.yml`, the `oF` will be copied to your RPi.

```sh  
$ cd rpi-build-and-boot  
$ wget http://openframeworks.cc/versions/v0.9.0/of_v0.9.0_linuxarmv7l_release.tar.gz  
$ tar -zxf of_v0.9.0_linuxarmv7l_release.tar.gz  
```

**7\. Update the Ansible`playbook.yml`**

I've had to tweak the `playbook.yml` to avoid warnings, add DNS to `cmdline.txt` and add `iptables` filters to get Internet access on RPi using Host shared NIC. Here the updated Ansible `playbook.yml`:

```python  
\---  
\- hosts: all  
remote_user: vagrant  
become: yes  
become_method: sudo  
vars:  
of_version: of_v0.9.0_linuxarmv7l_release  
raspbian_image: 2015-09-24-raspbian-jessie-of2.img  
offset_boot: 4194304  
offset_root: 62914560  
tasks:  
\- apt: upgrade=dist update_cache=yes  
\- file: path=/opt/raspberrypi state=directory
\- apt: name=nfs-kernel-server  
\- lineinfile: dest=/etc/exports line="/opt/raspberrypi/root 10.0.0.0/24(rw,sync,no_root_squash,no_subtree_check)"
\- lineinfile: dest=/etc/cron.d/opt_raspberrypi_root line="* * * * * root /bin/mount /opt/raspberrypi/root" create=yes
\- service: name=nfs-kernel-server state=restarted
\- apt: name=build-essential  
\- apt: name=pkg-config  
\- apt: name=git  
\- apt: name=python-pip  
\- apt: name=python-dev  
\- apt: name=unzip  
\- apt: name=gawk  
\- apt: name=libudev-dev
\- apt: name=sshpass
\- pip: name=ansible  
\- pip: name=paramiko  
\- pip: name=PyYAML  
\- pip: name=jinja2  
\- pip: name=httplib2
\- apt: name=tinyproxy  
\- lineinfile: dest="/etc/tinyproxy.conf" line="Allow 10.0.0.0/8"  
\- service: name=tinyproxy state=restarted
\- file: path=/opt/raspberrypi/boot state=directory  
\- file: path=/opt/raspberrypi/root state=directory
\- mount: src="/vagrant/{{raspbian_image}}" name="/opt/raspberrypi/boot" fstype="auto" opts="loop,offset={{offset_boot}},noauto" state="mounted"  
\- mount: src="/vagrant/{{raspbian_image}}" name="/opt/raspberrypi/root" fstype="auto" opts="loop,offset={{offset_root}},noauto" state="mounted"  
\- lineinfile: dest=/etc/rc.local line="mount /opt/raspberrypi/root" insertbefore="exit 0"  
\- lineinfile: dest=/etc/rc.local line="mount /opt/raspberrypi/boot" insertbefore="exit 0"

# the rpi is unbootable unless it is told not to mount the root filesystem from the card!. also added dns to cmdline.txt and iptables filter.  
\- lineinfile: dest=/opt/raspberrypi/root/etc/fstab regexp="^\/dev\/mmcblk0p2" state="absent"  
\- replace: dest=/opt/raspberrypi/boot/cmdline.txt regexp="rootwait$" replace="dwc_otg.lpm_enable=0 console=ttyAMA0,115200 kgdboc=ttyAMA0,115200 console=tty1 elevator=deadline root=/dev/nfs rootfstype=nfs nfsroot=10.0.0.1:/opt/raspberrypi/root,udp,vers=3 rw fsck.repair=no rootwait ip=10.0.0.101:10.0.0.1:10.0.0.1:255.255.255.0:rpi:eth0:off:8.8.4.4:8.8.8.8 smsc95xx.turbo_mode=N" backup="no"

# build helpies  
\- file: path=/opt/RPI_BUILD_ROOT state=directory  
\- file: src=/opt/raspberrypi/root/etc dest=/opt/RPI_BUILD_ROOT/etc state=link  
\- file: src=/opt/raspberrypi/root/lib dest=/opt/RPI_BUILD_ROOT/lib state=link  
\- file: src=/opt/raspberrypi/root/opt dest=/opt/RPI_BUILD_ROOT/opt state=link  
\- command: rsync -avz /opt/raspberrypi/root/usr/ /opt/RPI_BUILD_ROOT/usr
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libanl.so.1 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libanl.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libBrokenLocale.so.1 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libBrokenLocale.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libcidn.so.1 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libcidn.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libcrypt.so.1 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libcrypt.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libdbus-1.so.3.8.13 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libdbus-1.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libdl.so.2 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libdl.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libexpat.so.1.6.0 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libexpat.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libglib-2.0.so.0 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libglib-2.0.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/liblzma.so.5.0.0 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/liblzma.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libm.so.6 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libm.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libnsl.so.1 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libnsl.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libnss_compat.so.2 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libnss_compat.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libnss_dns.so.2 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libnss_dns.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libnss_files.so.2 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libnss_files.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libnss_hesiod.so.2 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libnss_hesiod.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libnss_nisplus.so.2 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libnss_nisplus.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libnss_nis.so.2 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libnss_nis.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libpcre.so.3 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libpcre.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libpng12.so.0 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libpng12.so.0 state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libresolv.so.2 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libresolv.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libthread_db.so.1 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libthread_db.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libusb-0.1.so.4 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libusb-0.1.so.4 state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libusb-1.0.so.0.1.0 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libusb-1.0.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libutil.so.1 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libutil.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libz.so.1.2.8 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libz.so state=link  
\- file: src=/opt/raspberrypi/root/lib/arm-linux-gnueabihf/libudev.so.1.5.0 dest=/opt/raspberrypi/root/usr/lib/arm-linux-gnueabihf/libudev.so state=link
\- file: path=/tmp/CROSS_BUILD_TOOLS state=directory  
\- copy: src=build_cross_gcc.sh dest=/tmp/CROSS_BUILD_TOOLS/build_cross_gcc.sh mode=0744  
\- shell: /tmp/CROSS_BUILD_TOOLS/build_cross_gcc.sh chdir=/tmp/CROSS_BUILD_TOOLS creates=/opt/cross/bin/arm-linux-gnueabihf-g++
\- lineinfile: dest="/home/vagrant/.profile" line="export GST_VERSION=1.0"  
\- lineinfile: dest="/home/vagrant/.profile" line="export RPI_ROOT=/opt/raspberrypi/root"  

#######- lineinfile: dest="/home/vagrant/.profile" line="export RPI_BUILD_ROOT=/opt/RPI_BUILD_ROOT"  
\- lineinfile: dest="/home/vagrant/.profile" line="export TOOLCHAIN_ROOT=/opt/cross/bin"  
\- lineinfile: dest="/home/vagrant/.profile" line="export PLATFORM_OS=Linux"  
\- lineinfile: dest="/home/vagrant/.profile" line="export PLATFORM_ARCH=armv7l"  
\- lineinfile: dest="/home/vagrant/.profile" line="export PKG_CONFIG_PATH=$RPI_ROOT/usr/lib/arm-linux-gnueabihf/pkgconfig:$RPI_ROOT/usr/share/pkgconfig:$RPI_ROOT/usr/lib/pkgconfig"
\- unarchive: src={{of_version}}.tar.gz dest=/opt/raspberrypi/root/opt creates=/opt/raspberrypi/root/opt/{{of_version}}  
\- file: src={{of_version}} dest=/opt/raspberrypi/root/opt/openframeworks state=link  
\- file: src=/opt/raspberrypi/root/opt/openframeworks dest=/opt/openframeworks state=link  
\- command: chown -R vagrant /opt/raspberrypi/root/opt/{{of_version}}

# forwarding traffic from eth0 (internet) to eth1 (rpi connection) with iptables  
\- replace: dest=/etc/sysctl.conf regexp="^#net.ipv4.ip_forward=1$" replace="net.ipv4.ip_forward=1"  
\- shell: /bin/echo 1 > /proc/sys/net/ipv4/ip_forward  
\- command: iptables -A FORWARD -o eth0 -i eth1 -m conntrack --ctstate NEW -j ACCEPT  
\- command: iptables -A FORWARD -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT  
\- command: iptables -A POSTROUTING -t nat -j MASQUERADE  
\- shell: /sbin/iptables-save | /usr/bin/tee /etc/iptables.backup  
\- service: name=ufw state=restarted  
handlers:
```

**8\. Create the Vagrant box**

```sh  
$ cd rpi-build-and-boot  
$ vagrant up --provider virtualbox
Bringing machine 'default' up with 'virtualbox' provider...  
==> default: Importing base box 'ubuntu/trusty64'...  
==> default: Matching MAC address for NAT networking...  
==> default: Checking if box 'ubuntu/trusty64' is up to date...  
==> default: A newer version of the box 'ubuntu/trusty64' is available! You currently  
==> default: have version '20160311.0.0'. The latest is version '20160406.0.0'. Run  
==> default: `vagrant box update` to update.  
==> default: Setting the name of the VM: rpi-build-and-boot_default_1460455393206_79951  
==> default: Clearing any previously set forwarded ports...  
==> default: Clearing any previously set network interfaces...  
==> default: Preparing network interfaces based on configuration...  
default: Adapter 1: nat  
default: Adapter 2: bridged  
==> default: Forwarding ports...  
default: 22 (guest) => 2222 (host) (adapter 1)  
...  
TASK [service] *****************************************************************

changed: [default]
PLAY RECAP *********************************************************************

default : ok=82 changed=76 unreachable=0 failed=0  
```

... let's have coffee ;)
After that, restart the Vagrant box recently created.

```sh  
$ vagrant halt  
==> default: Attempting graceful shutdown of VM...
$ vagrant up  
Bringing machine 'default' up with 'virtualbox' provider...  
==> default: Checking if box 'ubuntu/trusty64' is up to date...  
==> default: A newer version of the box 'ubuntu/trusty64' is available! You currently  
==> default: have version '20160311.0.0'. The latest is version '20160406.0.0'. Run  
==> default: `vagrant box update` to update.  
==> default: Clearing any previously set forwarded ports...  
==> default: Clearing any previously set network interfaces...  
==> default: Specific bridge 'ask' not found. You may be asked to specify  
==> default: which network to bridge to.  
==> default: Available bridged network interfaces:  
1) en0: Wi-Fi (AirPort)  
2) en1: Thunderbolt 1  
3) en2: Thunderbolt 2  
4) p2p0  
5) awdl0  
6) en4: mac-eth0  
==> default: When choosing an interface, it is usually the one that is  
==> default: being used to connect to the internet.  
default: Which interface should the network bridge to? 6  
==> default: Preparing network interfaces based on configuration...  
default: Adapter 1: nat  
default: Adapter 2: bridged  
==> default: Forwarding ports...  
default: 22 (guest) => 2222 (host) (adapter 1)  
==> default: Running 'pre-boot' VM customizations...  
==> default: Booting VM...  
==> default: Waiting for machine to boot. This may take a few minutes...  
default: SSH address: 127.0.0.1:2222  
default: SSH username: vagrant  
default: SSH auth method: private key  
==> default: Machine booted and ready!  
GuestAdditions 5.0.16 running --- OK.  
==> default: Checking for guest additions in VM...  
==> default: Configuring and enabling network interfaces...  
==> default: Mounting shared folders...  
default: /vagrant => /Users/Chilcano/1github-repo/rpi-build-and-boot  
==> default: Machine already provisioned. Run `vagrant provision` or use the `--provision`  
==> default: flag to force provisioning. Provisioners marked to run always will still run.  
```

Connect your Raspberry Pi -with the SD card and boot partition copied- using ethernet clable to your Host PC (in my case is a Mac OS X), wait some seconds and check if Raspberry Pi has started from the `root` partition shared by NFS from the Vagrant box.

```sh  
$ ping raspberrypi.local  
PING raspberrypi.local (10.0.0.101): 56 data bytes  
64 bytes from 10.0.0.101: icmp_seq=0 ttl=64 time=0.386 ms  
64 bytes from 10.0.0.101: icmp_seq=1 ttl=64 time=0.471 ms  
^C  
\--- raspberrypi.local ping statistics ---  
2 packets transmitted, 2 packets received, 0.0% packet loss  
round-trip min/avg/max/stddev = 0.386/0.428/0.471/0.042 ms
Chilcano@Pisc0 : ~/1github-repo/rpi-build-and-boot  
$ ping 10.0.0.101  
PING 10.0.0.101 (10.0.0.101): 56 data bytes  
64 bytes from 10.0.0.101: icmp_seq=0 ttl=64 time=0.450 ms  
64 bytes from 10.0.0.101: icmp_seq=1 ttl=64 time=0.591 ms  
^C  
\--- 10.0.0.101 ping statistics ---  
2 packets transmitted, 2 packets received, 0.0% packet loss  
round-trip min/avg/max/stddev = 0.450/0.520/0.591/0.071 ms  
```

And check if Raspberry Pi is running but from Vagrant box.

```sh  
$ vagrant ssh  
Welcome to Ubuntu 14.04.4 LTS (GNU/Linux 3.13.0-85-generic x86_64)
* Documentation: https://help.ubuntu.com/
System information as of Tue Apr 12 10:55:29 UTC 2016
System load: 0.07 Processes: 129  
Usage of /: 11.8% of 39.34GB Users logged in: 0  
Memory usage: 2% IP address for eth0: 10.0.2.15  
Swap usage: 0% IP address for eth1: 10.0.0.1
Graph this data and manage this system at:  
https://landscape.canonical.com/
Get cloud support with Ubuntu Advantage Cloud Guest:  
http://www.ubuntu.com/business/services/cloud
Last login: Tue Apr 12 10:27:47 2016 from 10.0.2.2
vagrant@vagrant-ubuntu-trusty-64:~$ ifconfig  
eth0 Link encap:Ethernet HWaddr 08:00:27:c9:24:d6  
inet addr:10.0.2.15 Bcast:10.0.2.255 Mask:255.255.255.0  
inet6 addr: fe80::a00:27ff:fec9:24d6/64 Scope:Link  
UP BROADCAST RUNNING MULTICAST MTU:1500 Metric:1  
RX packets:665 errors:0 dropped:0 overruns:0 frame:0  
TX packets:427 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:1000  
RX bytes:67162 (67.1 KB) TX bytes:54225 (54.2 KB)
eth1 Link encap:Ethernet HWaddr 08:00:27:b3:e9:a4  
inet addr:10.0.0.1 Bcast:10.0.0.255 Mask:255.255.255.0  
inet6 addr: fe80::a00:27ff:feb3:e9a4/64 Scope:Link  
UP BROADCAST RUNNING MULTICAST MTU:1500 Metric:1  
RX packets:29474 errors:0 dropped:0 overruns:0 frame:0  
TX packets:60947 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:1000  
RX bytes:5247033 (5.2 MB) TX bytes:70887820 (70.8 MB)
lo Link encap:Local Loopback  
inet addr:127.0.0.1 Mask:255.0.0.0  
inet6 addr: ::1/128 Scope:Host  
UP LOOPBACK RUNNING MTU:65536 Metric:1  
RX packets:0 errors:0 dropped:0 overruns:0 frame:0  
TX packets:0 errors:0 dropped:0 overruns:0 carrier:0  
collisions:0 txqueuelen:0  
RX bytes:0 (0.0 B) TX bytes:0 (0.0 B)
vagrant@vagrant-ubuntu-trusty-64:~$ ping 10.0.0.101  
PING 10.0.0.101 (10.0.0.101) 56(84) bytes of data.  
64 bytes from 10.0.0.101: icmp_seq=1 ttl=64 time=0.536 ms  
64 bytes from 10.0.0.101: icmp_seq=2 ttl=64 time=0.745 ms  
64 bytes from 10.0.0.101: icmp_seq=3 ttl=64 time=0.910 ms  
^C  
\--- 10.0.0.101 ping statistics ---  
3 packets transmitted, 3 received, 0% packet loss, time 1999ms  
rtt min/avg/max/mdev = 0.536/0.730/0.910/0.154 ms  
vagrant@vagrant-ubuntu-trusty-64:~$ ping google.com  
PING google.com (216.58.211.206) 56(84) bytes of data.  
64 bytes from mad01s25-in-f14.1e100.net (216.58.211.206): icmp_seq=1 ttl=63 time=14.1 ms  
64 bytes from mad01s25-in-f14.1e100.net (216.58.211.206): icmp_seq=2 ttl=63 time=13.5 ms  
64 bytes from mad01s25-in-f14.1e100.net (216.58.211.206): icmp_seq=3 ttl=63 time=13.9 ms  
^C  
\--- google.com ping statistics ---  
3 packets transmitted, 3 received, 0% packet loss, time 2008ms  
rtt min/avg/max/mdev = 13.521/13.883/14.137/0.296 ms  
```

**9\. Check if ARM cross-compiling works in the VirtualBox guest**

Check if the cross-compiling Variables have been defined.

```sh  
vagrant@vagrant-ubuntu-trusty-64:~$ cat /home/vagrant/.profile
...  
export GST_VERSION=1.0  
export RPI_ROOT=/opt/raspberrypi/root  
export TOOLCHAIN_ROOT=/opt/cross/bin  
export PLATFORM_OS=Linux  
export PLATFORM_ARCH=armv7l  
export PKG_CONFIG_PATH=$RPI_ROOT/usr/lib/arm-linux-gnueabihf/pkgconfig:$RPI_ROOT/usr/share/pkgconfig:$RPI_ROOT/usr/lib/pkgconfig  
```

Check if RPi has been mounted.

```sh  
vagrant@vagrant-ubuntu-trusty-64:~$ ll /opt/raspberrypi/boot/  
vagrant@vagrant-ubuntu-trusty-64:~$ ll /opt/raspberrypi/root/  
```

And check if oF works by compiling an example.

```sh  
$ make -C /opt/openframeworks/apps/myApps/emptyExample  
```


## Conclusions
  * As you have seen above, using Vagrant, Ansible and Python you can build easily a Provisioning system for massive delivery of binaries/packages for Raspberry Pi or Mobile Devices.
  * Also, you could replace OpenFramework tool (http://openframeworks.cc) used for ARM cross-compiling for other similar Tool if you have different target Device, to do that, just modify the part related to that in the Ansible Playbook.
Finally, in the next blog post, I will explain how to cross-compile the Kismet tool (https://www.kismetwireless.net/download.shtml) from source for Raspberry Pi (ARM).
I hope you have enjoyed.  
See you soon.

## References:
  1. Loop-mounting partitions from a disk image: 
    * http://www.andremiller.net/content/mounting-hard-disk-image-including-partitions-using-linux
    * http://madduck.net/blog/2006.10.20:loop-mounting-partitions-from-a-disk-image
  2. Ansible documentation: 
    * http://docs.ansible.com/ansible
  3. TCPDump cross-compiling for Android: 
    * http://www.androidtcpdump.com/android-tcpdump/compile
  4. ARM Cross Compiling with Mac OS X: 
    * http://www.welzels.de/blog/en/arm-cross-compiling-with-mac-os-x
  5. Pre-built environment for Raspberry Pi cross-compiling and NFS booting: 
    * https://forum.openframeworks.cc/t/pre-built-environment-for-raspberry-pi-cross-compiling-and-nfs-booting/16206/26
    * https://github.com/twobitcircus/rpi-build-and-boot
  6. How to Build a GCC Cross-Compiler: 
    * http://preshing.com/20141119/how-to-build-a-gcc-cross-compiler
  7. A Vagrant plugin to keep your VirtualBox Guest Additions up to date: 
    * https://github.com/dotless-de/vagrant-vbguest
  8. openFrameworks - an open source C++ toolkit: 
    * http://openframeworks.cc/download
  9. Vboxvfs lacks support for symbolic / hard links 
    * https://www.virtualbox.org/ticket/818
  10. Cross compiler for OF 0.9.0/Jessie/arm6/RPi1 
    * https://forum.openframeworks.cc/t/cross-compiler-for-of-0-9-0-jessie-arm6-rpi1/21336
  11. How to cross compile an application for OpenWRT 
    * https://blog.netbeast.co/app-openwrt
  12. Cross-Compiling or Building Android tcpdump? 
    * http://www.androidtcpdump.com/android-tcpdump/compile
