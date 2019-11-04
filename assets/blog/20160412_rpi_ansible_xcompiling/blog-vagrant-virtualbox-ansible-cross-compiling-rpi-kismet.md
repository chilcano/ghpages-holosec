
## Compiling Kismet for Raspberry Pi (ARMv7)


1. Download and compile Kismet using the Vagrant box

```bash
$ mkdir -p /opt/openframework/apps/kismet_rpi; cd /opt/openframework/apps/kismet_rpi
$ wget https://www.kismetwireless.net/code/kismet-2016-01-R1.tar.xz
$ tar xvfJ kismet-2016-01-R1.tar.tar.xz

### Troubleshooting

1. I want to use a newest RPi image.


```bash
$ vagrant ssh

vagrant@vagrant-ubuntu-trusty-64:/vagrant$ sudo parted 2016-02-09-raspbian-jessie.img
GNU Parted 2.3
Using /vagrant/2016-02-09-raspbian-jessie.img
Welcome to GNU Parted! Type 'help' to view a list of commands.
(parted) unit
Unit?  [compact]? B
(parted) print
Model:  (file)
Disk /vagrant/2016-02-09-raspbian-jessie.img: 4127195136B
Sector size (logical/physical): 512B/512B
Partition Table: msdos

Number  Start      End          Size         Type     File system  Flags
 1      4194304B   67108863B    62914560B    primary  fat16        lba
 2      67108864B  4127195135B  4060086272B  primary  ext4

(parted) quit
```

Then, the new values are:

- `offset_boot`: 4194304
- `offset_root`: 67108864


2. I want to use a newest oF.


3. 



---.----.---.---
    offset_boot: 4194304
    offset_root: 62914560
mount: src="/vagrant/{{raspbian_image}}" name="/opt/raspberrypi/boot" fstype="auto"  opts="loop,offset={{offset_boot}},noauto" state="mounted"
sudo mount -o loop,offset=4194304 /vagrant/2015-09-24-raspbian-jessie-of.img /opt/raspberrypi/boot
sudo mount -o loop,offset=62914560 /vagrant/2015-09-24-raspbian-jessie-of.img /opt/raspberrypi/root
---
offset_boot : 4194304
offset_root : 67108864

mount: src="/vagrant/{{raspbian_image}}" name="/opt/raspberrypi/boot" fstype="auto"  opts="loop,offset={{offset_boot}},noauto" state="mounted"
sudo mount -o loop,offset=4194304 /vagrant/2016-03-18-raspbian-jessie.img /opt/raspberrypi/boot
sudo mount -o loop,offset=67108864 /vagrant/2016-03-18-raspbian-jessie.img /opt/raspberrypi/root

-----------.------.----.----

RPi - boot : dwc_otg.lpm_enable=0 console=ttyAMA0,115200 console=tty1 root=/dev/mmcblk0p2 rootfstype=ext4 elevator=deadline rootdelay=10 rootwait ip=10.0.0.101:10.0.0.1:10.0.0.1:255.255.255.0:rpi:eth0:off root=/dev/nfs rootfstype=nfs nfsroot=10.0.0.1:/opt/raspberrypi/root,udp,vers=3 smsc95xx.turbo_mode=N


---
### eth0: internet, eth1: rpi connection
sudo sh -c "echo 1 > /proc/sys/net/ipv4/ip_forward"
###sudo iptables -A FORWARD -o eth0 -i eth1 -s 192.168.0.0/24 -m conntrack --ctstate NEW -j ACCEPT
sudo iptables -A FORWARD -o eth0 -i eth1 -m conntrack --ctstate NEW -j ACCEPT
sudo iptables -A FORWARD -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT
sudo iptables -A POSTROUTING -t nat -j MASQUERADE
sudo iptables-save | sudo tee /etc/iptables.sav
sudo service ufw restart

edit /etc/sysctl.conf

net.ipv4.ip_forward=1


--
    ### forwarding traffic from eth0 (internet) to eth1 (rpi connection) with iptables
    - command: "echo 1 > /proc/sys/net/ipv4/ip_forward"
    - iptables: chain=FORWARD in_interface=eth1 out_interface=eth0 ctstate=NEW jump=ACCEPT
    #sudo iptables -A FORWARD -i eth1 -o eth0 -m conntrack --ctstate NEW -j ACCEPT
    - iptables: chain=FORWARD ctstate=ESTABLISHED,RELATED jump=ACCEPT
    #sudo iptables -A FORWARD -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT
    - iptables: chain=POSTROUTING table=NAT jump=MASQUERADE
    #sudo iptables -A POSTROUTING -t nat -j MASQUERADE
    - command: iptables-save | sudo tee /etc/iptables.sav
    - service: name=ufw state=restarted
--

    ### copy/overwrite '<RPi>/proc/net/pnp' to '<RPi>/etc/resolv.conf'
    $ sudo cat /proc/net/pnp
#MANUAL
nameserver 8.8.4.4
nameserver 8.8.8.8
bootserver 10.0.0.1    
    $ sudo cp /proc/net/pnp /etc/resolv.conf
    --
    sudo apt-get install resolvconf
    sudo apt-get purge dhcpcd5
    sudo aptitude search resolvconf
    sudo apt-get install dhcpcd5
    --

-.-.-.-.-.-.-.-.-.-.
mount: src="/vagrant/{{raspbian_image}}" name="/opt/raspberrypi/boot" fstype="auto"  opts="loop,offset={{offset_boot}},noauto" state="mounted"

# fdisk -lu 2015-09-24-raspbian-jessie.img

Disk 2015-09-24-raspbian-jessie.img: 4 GiB, 4325376000 bytes, 8448000 sectors
Units: sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disklabel type: dos
Disk identifier: 0xba2edfb9

Device                          Boot  Start     End Sectors Size Id Type
2015-09-24-raspbian-jessie.img1        8192  122879  114688  56M  c W95 FAT32 (LBA)
2015-09-24-raspbian-jessie.img2      122880 8447999 8325120   4G 83 Linux

# fdisk -lu 2015-09-24-raspbian-jessie-of2.img

Disk 2015-09-24-raspbian-jessie-of2.img: 14.9 GiB, 16021192704 bytes, 31291392 sectors
Units: sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disklabel type: dos
Disk identifier: 0xba2edfb9

Device                              Boot  Start      End  Sectors  Size Id Type
2015-09-24-raspbian-jessie-of2.img1        8192   122879   114688   56M  c W95 FAT32 (LBA)
2015-09-24-raspbian-jessie-of2.img2      122880 31291391 31168512 14.9G 83 Linux

->   8192 * 512 =  4194304
-> 122880 * 512 = 62914560

$ sudo mount -o loop,noauto,offset=4194304 2015-09-24-raspbian-jessie.img /rpi_2015/boot
$ sudo mount -o loop,noauto,offset=62914560 2015-09-24-raspbian-jessie.img /rpi_2015/root

$ sudo mount -o loop,noauto,offset=4194304 2015-09-24-raspbian-jessie-of2.img /rpi_2015_of2/boot
$ sudo mount -o loop,noauto,offset=62914560 2015-09-24-raspbian-jessie-of2.img /rpi_2015_of2/root

$ sudo losetup /dev/loop5 2015-09-24-raspbian-jessie.img -o 4194304
$ sudo losetup /dev/loop6 2015-09-24-raspbian-jessie.img -o 62914560

$ sudo losetup /dev/loop0 
/dev/loop0: [001a]:113 (/vagrant/2016-03-18-raspbian-jessie.img), offset 4194304

$ sudo losetup /dev/loop1
/dev/loop1: [001a]:113 (/vagrant/2016-03-18-raspbian-jessie.img), offset 67108864

$ file -s /dev/loop1
/dev/loop1: Linux rev 1.0 ext4 filesystem data, UUID=ec2aa3d2-eee7-454e-8260-d145df5ddcba (extents) (large files)

$ losetup -d /dev/loop0
$ losetup -d /dev/loop1


---.----.----.----

$ sudo apt-get install kpartx

$ sudo kpartx -lv 2015-09-24-raspbian-jessie-of2.img
2015-09-24-raspbian-jessie-of2.img: Operation not permitted
can't set up loop

# kpartx -lv 2015-09-24-raspbian-jessie.img
loop0p1 : 0 114688 /dev/loop0 8192
loop0p2 : 0 8325120 /dev/loop0 122880
loop deleted : /dev/loop0

-----.------

$ diskutil list
$ diskutil unmountDisk /dev/disk2
$ sudo dd bs=1m if=/dev/rdisk2 of=2015-09-24-raspbian-jessie-bs1m.img
15279+0 records in
15279+0 records out
16021192704 bytes transferred in 369.766849 secs (43327823 bytes/sec)


$ sudo kpartx -lv 2015-09-24-raspbian-jessie-of2.img

-----.----
Issue:
- https://www.virtualbox.org/ticket/10085
- https://forums.virtualbox.org/viewtopic.php?f=3&t=47014&start=15

$ VBoxManage setextradata kali-linux-puma VBoxInternal2/SharedFoldersEnableSymlinksCreate/rpi-build-and-boot 1

$ ln -s 2015-09-24-raspbian-jessie.img 2015-09-24-raspbian-jessie.img.link
$ ln -s 2015-09-24-raspbian-jessie-of2.img 2015-09-24-raspbian-jessie-of2.img.link

$ ln 2015-09-24-raspbian-jessie.img 2015-09-24-raspbian-jessie.img.link
$ ln 2015-09-24-raspbian-jessie-of2.img 2015-09-24-raspbian-jessie-of2.img.link

ln: failed to create hard link ‘2015-09-24-raspbian-jessie.img.link’ => ‘2015-09-24-raspbian-jessie.img’: Operation not permitted
ln: failed to create hard link ‘2015-09-24-raspbian-jessie-of2.img.link’ => ‘2015-09-24-raspbian-jessie-of2.img’: Operation not permitted
-----.-----

$ sudo mount -t vboxsf -o uid=$UID,gid=$(id -g) rpi /rpi-share
$ sudo mount -t vboxsf rpi /rpi-share

$ nano /etc/fstab
rpi   /rpi-share   vboxsf   defaults   0   0