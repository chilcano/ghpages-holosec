---
layout:     post
title:      'Mass provisioning of Kismet and Apache MiNiFi in Raspberry Pi using Ansible'
date:       2017-03-20 21:35:53
categories: ['Big Data', 'DevOps', 'IoT', 'Security']
tags:       ['Ansible', 'Apache MiNiFi', 'Apache NiFi', 'Data Ingestion', 'Kismet', 'Raspberry Pi']
status:     publish 
permalink:  "/2017/03/20/mass-provisioning-of-kismet-and-apache-minifi-in-raspberry-pi-using-ansible/"
---
Lately I'm focusing on Automation in Big-Data Projects, and with my experience in Cyber Security I can bring new approaches and ideas to those Big-Data Projects are related to IT Security aspects (Threat Analisys, Privacy, Intrusion Detection, etc.).  
With this post I will start a serie of articles explaining how to do "Data Ingestion/Capture" in the `Edge` by using:  
\- Ansible to do `provisioning`.  
\- Multiple and remote devices in the `Edge` (Raspberry Pi).  
\- Kismet to capture WIFI Anonymous traffic in the `Edge`.  
\- Apache MiNiFi to enrich and route the captured traffic in the `Edge`.  
\- Apache NiFi to collect the enriched traffic and forward to Solr / ElasticSearch.  
...everything, near to real-time ;)

<!-- more -->
And if you want to explore in deep other use cases, this article is a must read: (EDGE INTELLIGENCE FOR IOT WITH APACHE MINIFI)[https://hortonworks.com/blog/edge-intelligence-iot-apache-minifi/]
In short, I will explain in this post the following:  
\- Manage several devices in the `Edge` by using Ansible.  
\- Mass provision of Kismet and Apache MiNiFi.  
\- Performing operational tasks in several devices.

![https://raw.githubusercontent.com/chilcano/ansible-raspberrypi-wardriving/master/images/mass-provisioning-kismet-minifi-raspberrypi-ansible-1-arch.png]({{ site.baseurl }}/assets/mass-provisioning-kismet-minifi-raspberrypi-ansible-1-arch.png)

## 1\. Preparing your Raspberry Pi.

### 1.1. Prepare your MicroSD cards with the latest Raspbian image.
I'm going to use Raspbian Jessie Lite 2017-01-11 (http://director.downloads.raspberrypi.org/raspbian_lite/images/raspbian_lite-2017-01-10/2017-01-11-raspbian-jessie-lite.zip).
```text  
$ diskutil list
$ diskutil unmountDisk /dev/disk3
$ sudo dd bs=1m if=2017-01-11-raspbian-jessie-lite.img of=/dev/rdisk3
$ touch /Volumes/boot/ssh
$ diskutil unmountDisk /dev/disk3  
```
... repeat this process as times as Raspberry Pis you have.

### 1.2. Connect all Raspberry Pis to the Network.
Connect all Raspberry Pi to your Router, now your PC and all Pis are connected to same network, and if you have DHCP enabled your Raspberry Pi will have an IP address automatically.  
Now, we have to get all IP addresses assigned to each Raspberry Pi. You could use Fing App in Android or install Fing in your PC.  
I'm going to use [Fing on Mac OSX](https://www.fing.io/download-free-ip-scanner-for-desktop-linux-windows-and-osx).
```text  
$ sudo fing  
Password:
19:58:18 > Discovery profile: Default discovery profile  
19:58:18 > Discovery class: data-link (data-link layer)  
19:58:18 > Discovery on: 192.168.0.0/24  
...  
19:58:18 > Host is up: 192.168.0.17  
HW Address: B8:27:EB:1E:12:63 (Raspberry Pi Foundation)
19:58:18 > Host is up: 192.168.0.18  
HW Address: B8:27:EB:9C:C2:E3 (Raspberry Pi Foundation)
19:58:18 > Host is up: 192.168.0.19  
HW Address: B8:27:EB:F0:F3:EA (Raspberry Pi Foundation)
19:58:18 > Host is up: 192.168.0.20  
HW Address: B8:27:EB:5A:B5:5D (Raspberry Pi Foundation)  
...  
```
You could try this too from your Mac OSX:
```text  
$ arp -a -i en0
? (192.168.0.1) at 90:21:6:89:82:a9 on en0 ifscope [ethernet]  
? (192.168.0.5) at c4:57:6e:94:91:c2 on en0 ifscope [ethernet]  
? (192.168.0.10) at 44:65:d:9d:4e:3e on en0 ifscope [ethernet]  
? (192.168.0.17) at b8:27:eb:1e:12:63 on en0 ifscope [ethernet]  
? (192.168.0.18) at b8:27:eb:9c:c2:e3 on en0 ifscope [ethernet]  
? (192.168.0.19) at b8:27:eb:f0:f3:ea on en0 ifscope [ethernet]  
? (192.168.0.20) at b8:27:eb:5a:b5:5d on en0 ifscope [ethernet]  
? (192.168.0.255) at (incomplete) on en0 ifscope [ethernet]  
? (224.0.0.251) at 1:0:5e:0:0:fb on en0 ifscope permanent [ethernet]  
? (239.255.255.250) at 1:0:5e:7f:ff:fa on en0 ifscope permanent [ethernet]  
broadcasthost (255.255.255.255) at (incomplete) on en0 ifscope [ethernet]  
```
Now, open a Terminal and connect through SSH to each Raspberry Pi just to verify the connection with your devices.
```text  
$ ssh pi@192.168.0.17  
```
With this information (MAC and IP addresses) I'm ready to start with Ansible to do automation on my Raspberry Pi Cluster.

### 1.3. Initial configuration and provision for all Raspberry Pi through Ansible.
To do the initial configuration in all Raspberry Pis I've used the next:  
\- The [Raspberry Pi Dramble Ansible Git repository](https://github.com/geerlingguy/raspberry-pi-dramble). Although these Ansible Playbooks are to provision an Apache HTTPd, Drupal, MySQL Cluster, our intention is to use it initially to manage the Raspberry Pi cluster at infrastructure level.  
\- The [mikolak.raspi-config](https://github.com/mikolak-net/ansible-raspi-config) Ansible Role to configure each Raspberry Pi like if used [raspi-config](https://www.raspberrypi.org/documentation/configuration/raspi-config.md) tool.
That means:
  * Resize SD, update Raspbian and manage shutdown and reboot operations.
  * Set a proper Hostname, configure Network interfaces (eth0 and wlan0), DNS, etc.
I've updated and extended the [Raspberry Pi Dramble Ansible Git repository](https://github.com/geerlingguy/raspberry-pi-dramble) to reset the Network configuration and to build from source code, to install and to start Kismet (http://www.kismetwireless.net).
You can download my Ansible Playbooks from here: https://github.com/chilcano/ansible-raspberrypi-wardriving
```text  
$ git clone https://github.com/chilcano/ansible-raspberrypi-wardriving
$ cd ansible-raspberrypi-wardriving  
```
Now update `networking/inventory`. You will need to use all MAC Addresses and IP assigned to each Raspberry Pi collected in previous step. The final file look like:
```text  
$ nano setup/networking/inventory

[pis]  
192.168.0.17  
192.168.0.18  
192.168.0.19  
192.168.0.20

[pis:vars]  
ansible_ssh_user=pi  
ansible_ssh_user_new=picuy  
path_to_ssh_key=/Users/Chilcano/.ssh/id_rsa.pub  
```
And update `networking/vars.yml`.
```text  
$ nano setup/networking/vars.yml
\---  

# Mapping of what hardware MAC addresses should be configured with specific IPs.  
mac_address_mapping:  
"b8:27:eb:1e:12:63":  
name: rpi17.intix.info  
ip: "192.168.0.17"  
"b8:27:eb:9c:c2:e3":  
name: rpi18.intix.info  
ip: "192.168.0.18"  
"b8:27:eb:f0:f3:ea":  
name: rpi19.intix.info  
ip: "192.168.0.19"  
"b8:27:eb:5a:b5:5d":  
name: rpi20.intix.info  
ip: "192.168.0.20"

# Nameservers to use in resolv.conf.  
dns_nameservers:  
\- "8.8.8.8"  
\- "8.8.4.4"
gateway:  
eth0: "192.168.0.1"  
wlan0: "192.168.0.1"
rpi_nic_static:  
eth0: false  
wlan0: false  
```
Finally, It is time to run the Ansible Playbook to provision the initial configuration.
```text  
$ cd setup/networking
$ ansible-playbook -i inventory main.yml -k
SSH password:
PLAY ***************************************************************************
TASK [setup] *******************************************************************
paramiko: The authenticity of host '192.168.0.17' can't be established.  
The ssh-rsa key fingerprint is 66cf86cb30b2ca92658808f26d453c31.  
Are you sure you want to continue connecting (yes/no)?  
yes
paramiko: The authenticity of host '192.168.0.20' can't be established.  
The ssh-rsa key fingerprint is 7d259fe2fcf0b36164441bd4dbee321e.  
Are you sure you want to continue connecting (yes/no)?  
yes
paramiko: The authenticity of host '192.168.0.18' can't be established.  
The ssh-rsa key fingerprint is de91abe013a8af491d0c8aa5b5d6a9aa.  
Are you sure you want to continue connecting (yes/no)?  
yes
paramiko: The authenticity of host '192.168.0.19' can't be established.  
The ssh-rsa key fingerprint is 447a195e33bbee9d4479ed46110dd3d4.  
Are you sure you want to continue connecting (yes/no)?  
yes  
ok: [192.168.0.17]  
ok: [192.168.0.18]  
ok: [192.168.0.20]  
ok: [192.168.0.19]
TASK [Set the current MAC address for eth0.] ***********************************  
ok: [192.168.0.17]  
ok: [192.168.0.18]  
ok: [192.168.0.19]  
ok: [192.168.0.20]
TASK [Set variables based on eth0 MAC address.] ********************************  
ok: [192.168.0.17]  
ok: [192.168.0.18]  
ok: [192.168.0.19]  
ok: [192.168.0.20]
TASK [Set up networking-related files.] ****************************************  
changed: [192.168.0.18] => (item={u'dest': u'/etc/hostname', u'template': u'hostname.j2'})  
changed: [192.168.0.18] => (item={u'dest': u'/etc/hosts', u'template': u'hosts.j2'})  
changed: [192.168.0.18] => (item={u'dest': u'/etc/network/interfaces', u'template': u'interfaces.j2'})  
changed: [192.168.0.18] => (item={u'dest': u'/etc/resolv.conf', u'template': u'resolv.conf.j2'})  
changed: [192.168.0.18] => (item={u'dest': u'/etc/dhcpcd.conf', u'template': u'dhcpcd.conf.j2'})  
changed: [192.168.0.17] => (item={u'dest': u'/etc/hostname', u'template': u'hostname.j2'})  
changed: [192.168.0.17] => (item={u'dest': u'/etc/hosts', u'template': u'hosts.j2'})  
changed: [192.168.0.17] => (item={u'dest': u'/etc/network/interfaces', u'template': u'interfaces.j2'})  
changed: [192.168.0.17] => (item={u'dest': u'/etc/resolv.conf', u'template': u'resolv.conf.j2'})  
changed: [192.168.0.17] => (item={u'dest': u'/etc/dhcpcd.conf', u'template': u'dhcpcd.conf.j2'})  
changed: [192.168.0.19] => (item={u'dest': u'/etc/hostname', u'template': u'hostname.j2'})  
changed: [192.168.0.19] => (item={u'dest': u'/etc/hosts', u'template': u'hosts.j2'})  
changed: [192.168.0.19] => (item={u'dest': u'/etc/network/interfaces', u'template': u'interfaces.j2'})  
changed: [192.168.0.19] => (item={u'dest': u'/etc/resolv.conf', u'template': u'resolv.conf.j2'})  
changed: [192.168.0.19] => (item={u'dest': u'/etc/dhcpcd.conf', u'template': u'dhcpcd.conf.j2'})  
changed: [192.168.0.20] => (item={u'dest': u'/etc/hostname', u'template': u'hostname.j2'})  
changed: [192.168.0.20] => (item={u'dest': u'/etc/hosts', u'template': u'hosts.j2'})  
changed: [192.168.0.20] => (item={u'dest': u'/etc/network/interfaces', u'template': u'interfaces.j2'})  
changed: [192.168.0.20] => (item={u'dest': u'/etc/resolv.conf', u'template': u'resolv.conf.j2'})  
changed: [192.168.0.20] => (item={u'dest': u'/etc/dhcpcd.conf', u'template': u'dhcpcd.conf.j2'})
RUNNING HANDLER [update hostname] **********************************************  
changed: [192.168.0.19]  
changed: [192.168.0.18]  
changed: [192.168.0.17]  
changed: [192.168.0.20]
RUNNING HANDLER [delete dhcp leases] *******************************************  
changed: [192.168.0.19] => (item=/var/lib/dhcp/dhclient.leases)  
ok: [192.168.0.19] => (item=/var/lib/dhcpcd5/dhcpcd-eth0.lease)  
changed: [192.168.0.17] => (item=/var/lib/dhcp/dhclient.leases)  
ok: [192.168.0.17] => (item=/var/lib/dhcpcd5/dhcpcd-eth0.lease)  
changed: [192.168.0.18] => (item=/var/lib/dhcp/dhclient.leases)  
ok: [192.168.0.18] => (item=/var/lib/dhcpcd5/dhcpcd-eth0.lease)  
changed: [192.168.0.20] => (item=/var/lib/dhcp/dhclient.leases)  
ok: [192.168.0.20] => (item=/var/lib/dhcpcd5/dhcpcd-eth0.lease)
PLAY RECAP *********************************************************************  
192.168.0.17 : ok=6 changed=3 unreachable=0 failed=0  
192.168.0.18 : ok=6 changed=3 unreachable=0 failed=0  
192.168.0.19 : ok=6 changed=3 unreachable=0 failed=0  
192.168.0.20 : ok=6 changed=3 unreachable=0 failed=0  
```
The above result means you have connected to each Raspberry Pi and a proper Hostname based on each MAC Address have been assigned successfully.  
First of all, I will check that using Ansible.
```text  
$ cd ansible-raspberrypi-wardriving/setup/networking
$ ansible pis -i inventory -m ping -k
SSH password:  
192.168.0.19 | SUCCESS => {  
"changed": false,  
"ping": "pong"  
}  
192.168.0.18 | SUCCESS => {  
"changed": false,  
"ping": "pong"  
}  
192.168.0.17 | SUCCESS => {  
"changed": false,  
"ping": "pong"  
}  
192.168.0.20 | SUCCESS => {  
"changed": false,  
"ping": "pong"  
}  
```
Now, you can use SSH to connect to all Raspberry Pi and check the hostname assigned.
```text  
$ ssh picuy@192.168.0.17  
picuy@192.168.0.17's password:
The programs included with the Debian GNU/Linux system are free software;  
the exact distribution terms for each program are described in the  
individual files in /usr/share/doc/*/copyright.
Debian GNU/Linux comes with ABSOLUTELY NO WARRANTY, to the extent  
permitted by applicable law.  
Last login: Tue Feb 14 22:02:13 2017 from 192.168.0.3
SSH is enabled and the default password for the 'pi' user has not been changed.  
This is a security risk - please login as the 'pi' user and type 'passwd' to set a new password.
picuy@rpi17:~ $ exit  
```
In this point, you will be able to continue configuring your Raspberry Pis through Ansible Playbook performing Linux commands remotely.

### 1.4. Performing Linux commands remotely through Ansible.
_Get free memory available in each Raspberry Pi._
```text  
$ ansible pis -i inventory -a "free -m" -k  
SSH password:  
192.168.0.18 | SUCCESS | rc=0 >>  
total used free shared buffers cached  
Mem: 925 87 837 6 9 43  
-/+ buffers/cache: 35 890  
Swap: 99 0 99
192.168.0.17 | SUCCESS | rc=0 >>  
total used free shared buffers cached  
Mem: 925 87 838 6 9 43  
-/+ buffers/cache: 34 890  
Swap: 99 0 99
192.168.0.19 | SUCCESS | rc=0 >>  
total used free shared buffers cached  
Mem: 925 89 835 6 9 43  
-/+ buffers/cache: 37 888  
Swap: 99 0 99
192.168.0.20 | SUCCESS | rc=0 >>  
total used free shared buffers cached  
Mem: 434 74 359 4 9 37  
-/+ buffers/cache: 27 406  
Swap: 99 0 99  
```
_Performing different Linux commands._
```text  
$ ansible pis -i inventory -a "cat /etc/hostname" -k
$ ansible pis -i inventory -a "cat /etc/hosts" -k
$ ansible pis -i inventory -a "ping -c 2 pi17.intix.info" -k
$ ansible pis -i inventory -a "ping -c 3 holisticsecurity.io" -k
$ ansible pis -i inventory -a "df -h" -k  
```
But, if some command require `sudo` you can provide it using `-s` flag, although it is deprecated, it is still valid.
```text  
$ ansible pis -i inventory -a "ifconfig wlan0" -s -k  
```
...or rebooting all Raspberry Pi.
```text  
$ ansible pis -i inventory -a "shutdown -r now" -s -k
SSH password:  
192.168.0.18 | SUCCESS | rc=0 >>
192.168.0.17 | SUCCESS | rc=0 >>
192.168.0.19 | SUCCESS | rc=0 >>
No handlers could be found for logger "paramiko.transport"  
192.168.0.20 | SUCCESS | rc=0 >>  
```

### 1.5. Restoring the network configuration.
If you try to connect to one Raspberry Pi and the SSH connection is taking a few seconds, or if you are running ping www.google.com and Raspberry Pi is not reaching that. Then, you probably are facing issues with gateway and networking configuration in your Raspberry Pi and need restore or enable default configuration.  
Then, let's go to check the network configuration, basically you have to check these files in each Raspberry Pi:
  * /etc/dhcpcd.conf
  * /etc/hosts
  * /etc/network/interfaces
I've updated above cloned Ansible scripts for you with the right configuration. Basically I have updated the Ansible templates (Jinja2) to do:
  * Restoring `etc/network/interfaces` to get IP address automatically through DHCP.
  * Configuring `/etc/dhcpcd.conf` with our default gateway on eth0.
Also I have updated the `setup/networking/main.yml` Ansible Playbook and the `setup/networking/vars.yml` to restore default network configuration.
Again, run the updated Ansible Playbook and verify if the changes with the right gateway, DNS, etc. were applied.
```text  
$ ansible-playbook -i inventory main.yml -k  
```

### 1.6. Set IP address automatically through DHCP on eth0.
In previous step I updated the Ansible Playbooks and Jinja2 templates to restore default assignation of IP addresses through DHCP.  
Well, now I'm going to use it. Just update `setup\networking\vars.yml` setting `rpi_nic_static.eth0` to `false`.
Run `fing` to get all IP addresses re-assigned to all Raspberry Pis and update the `setup\networking\inventory` and provision your new configuration.
```text  
$ ansible-playbook -i inventory main.yml -k  
```

## 2\. Massive provisioning of Kismet through Ansible on multiples Raspberry Pi.
I'm going to create an Ansible Playbooks to:
  * Enable WIFI interface in `monitor mode` on each Raspberry Pi.
  * Download Kismet source code and build it for Raspberry Pi (ARM chipset).
  * Install and configure Kismet on each Raspberry Pi.
To do that I will follow my previous blog posts ([Capturing WIFI anonymous traffic using Raspberry Pi and WSO2 BAM - Part I](https://holisticsecurity.io/2016/02/02/everything-generates-data-capturing-wifi-anonymous-traffic-raspberrypi-wso2-part-i)) where I explained step by step all commands to be performed in order to run Kismet on Raspberry Pi.  
The result final is a set of Ansible Playbooks located under `ansible-raspberrypi-wardriving/playbooks/kismet` in the Git repo (https://github.com/chilcano/ansible-raspberrypi-wardriving), and they are:
```text  
$ tree ansible-raspberrypi-wardriving/playbooks/kismet
ansible-raspberrypi-wardriving/playbooks/kismet  
├── main_build.yml  
├── main_clean.yml  
├── main_install.yml  
├── tasks  
│ ├── build.yml  
│ ├── clean_installation.yml  
│ ├── install_from_deb.yml  
│ ├── kill_process.yml  
│ ├── localmirror.yml  
│ └── run_as_systemd.yml  
├── templates  
│ ├── description-pak.j2  
│ ├── kismet.conf.j2  
│ ├── kismet_drone.conf.j2  
│ ├── warpi.service.j2  
│ └── warpi.sh.j2  
└── vars.yml
2 directories, 15 files  
```
Now, to run these Ansible Playbooks I have to follow the same steps above explained:  
\- Get all IP addresses and MAC addresses.  
\- Automatic assignation of IP addresses (DHCP).  
\- Assignation of a proper hostname.  
\- Change default username and password.
To do that, just follow the steps in section `1.3. Initial configuration and provision for all Raspberry Pi through Ansible` of this post.  
After that, You have to update your `ansible-raspberrypi-wardriving/inventory` and `ansible-raspberrypi-wardriving/playbooks/kismet/vars.yml` files.
Now, You are ready to run these Kismet Ansible Playbooks, then let's do it:
```text  
$ cd ansible-raspberrypi-wardriving  
$ ansible-playbook -i inventory main_kismet_install.yml -k
```
And if you get the below message, then you have already provisioned successfully Kismet in all Raspberry Pi configured in your `ansible-raspberrypi-wardriving/inventory`.
```text  
...  
...  
TASK [Copying 'warpi.sh.j2' template.] *****************************************  
changed: [192.168.0.19]  
changed: [192.168.0.18]
TASK [Copying 'warpi.service.j2' template.] ************************************  
changed: [192.168.0.18]  
changed: [192.168.0.19]
TASK [Reloading 'warpi' systemd service.] **************************************  
changed: [192.168.0.18]  
changed: [192.168.0.19]
TASK [Starting 'warpi' systemd service.] ***************************************  
changed: [192.168.0.19]  
changed: [192.168.0.18]
TASK [debug] *******************************************************************  
ok: [192.168.0.18] => {  
"msg": "Service 'warpi' started successfully. Now Kismet is sending events on 2501 port ( nc -vz localhost 2501 )"  
}  
ok: [192.168.0.19] => {  
"msg": "Service 'warpi' started successfully. Now Kismet is sending events on 2501 port ( nc -vz localhost 2501 )"  
}
PLAY RECAP *********************************************************************  
192.168.0.18 : ok=34 changed=17 unreachable=0 failed=0  
192.168.0.19 : ok=37 changed=14 unreachable=0 failed=0  
```
And if you connect to your Raspberry Pi through SSH you can the status of Kismet there:
```text  
$ ssh picuy@192.168.0.19
...
picuy@rpi19:~ $ sudo systemctl status warpi  
● warpi.service - Enable monitor mode and manage Kismet Server as service  
Loaded: loaded (/etc/systemd/system/warpi.service; enabled)  
Active: active (running) since Fri 2017-03-17 11:25:17 UTC; 3min 39s ago  
Main PID: 9023 (warpi.sh)  
CGroup: /system.slice/warpi.service  
├─9023 /bin/sh /home/picuy/warpi.sh  
└─9028 kismet_server
Mar 17 11:25:22 rpi19.intix.info warpi.sh[9023]: INFO: Detected new managed network "BTWifi-with-FON", BSSID 02:03:D8:4D:17:  
Mar 17 11:25:22 rpi19.intix.info warpi.sh[9023]: 72, encryption no, channel 11, 144.40 mbit  
Mar 17 11:25:22 rpi19.intix.info warpi.sh[9023]: INFO: Detected new managed network "EE-ej96p2", BSSID E4:3E:D7:DB:9E:53,  
Mar 17 11:25:22 rpi19.intix.info warpi.sh[9023]: encryption yes, channel 11, 144.40 mbit  
Mar 17 11:25:41 rpi19.intix.info warpi.sh[9023]: INFO: Detected new probe network "", BSSID 88:32:9B:69:6F:3A,  
Mar 17 11:25:41 rpi19.intix.info warpi.sh[9023]: encryption no, channel 0, 72.20 mbit  
Mar 17 11:28:21 rpi19.intix.info warpi.sh[9023]: INFO: Detected new managed network "BTHomeHub2-SS4C", BSSID 00:26:44:19:7B:  
Mar 17 11:28:21 rpi19.intix.info warpi.sh[9023]: CD, encryption yes, channel 7, 130.00 mbit  
Mar 17 11:28:28 rpi19.intix.info warpi.sh[9023]: INFO: Detected new managed network "BTWiFi-with-FON", BSSID 02:26:44:19:7B:  
Mar 17 11:28:28 rpi19.intix.info warpi.sh[9023]: CF, encryption no, channel 7, 130.00 mbit  
```

## 3\. Massive provisioning of Apache MiNiFi through Ansible on multiples Raspberry Pi.
If you have not read my [previous post about Apache NiFi](https://holisticsecurity.io/2016/12/02/data-routing-transformation-and-system-mediation-in-big-data-iot-scenarios-with-apache-nifi), well I can say that is a Data Mediator Engine and ETL with steroids suitable for BigData Projects and Apache MiNiFi is the perfect complement to it for IoT Projects.  
Well, the scenario where I want to use Apache NiFi and Apache MiNiFi is in IoT, Security/Privacy space and the best way to validate this approach is using Ansible to do automation `massively` (Raspberry Pi in the `edge`) without pain.  
I've created Ansible Playbooks to manage the installation and configuration of Apache MiNiFi in Raspberry Pi, they are located under `ansible-raspberrypi-wardriving/playbooks/minifi` in the Git repo (https://github.com/chilcano/ansible-raspberrypi-wardriving), and they are:
```text  
$ tree ansible-raspberrypi-wardriving/playbooks/minifi
ansible-raspberrypi-wardriving/playbooks/minifi  
├── main_clean.yml  
├── main_install.yml  
├── tasks  
│ ├── clean.yml  
│ ├── install.yml  
│ └── run.yml  
├── templates  
│ └── minifipi.service.j2  
└── vars.yml
2 directories, 7 files  
```
The same Ansible Playbooks should work in other devices too.  
I'm going to repeat the same previous steps before running MiNiFi Ansible Playbooks. Check the section `2. Massive provisioning of Kismet through Ansible on multiples Raspberry Pi.` for further details.  
Now, You are ready to run these MiNiFi Ansible Playbooks, then let's do it:
```text  
$ cd ansible-raspberrypi-wardriving
$ ansible-playbook -i inventory main_minifi_install.yml -k  
```
And if you get the below message, then you have already provisioned successfully Apache MiNiFi in all Raspberry Pi configured in your `ansible-raspberrypi-wardriving/inventory`.
```text  
...  
...  
TASK [debug] *******************************************************************  
ok: [192.168.0.18] => {  
"msg": "The MiNiFi service 'minifipi.service' has started successfully."  
}
PLAY RECAP *********************************************************************  
192.168.0.18 : ok=22 changed=8 unreachable=0 failed=0  
```
Remember that you can execute the command remotely via Ansible, commands like `ping`, `shutdown`, `free`:
```text  
$ cd ansible-raspberrypi-wardriving/setup/networking
$ ansible pis -i inventory -m ping -k
$ ansible pis -i inventory -a "free -m" -k  
```
...and/or check the current status of MiNiFi and/or Kismet as below I explain:
```text  
$ ansible pis -i inventory -a "systemctl -p MainPID,ControlGroup,Id,Description,FragmentPath show *pi" -k
192.168.0.17 | SUCCESS | rc=0 >>
192.168.0.44 | SUCCESS | rc=0 >>  
MainPID=685  
ControlGroup=/system.slice/warpi.service  
Id=warpi.service  
Description=Enable monitor mode and manage Kismet Server as service  
FragmentPath=/etc/systemd/system/warpi.service
MainPID=5490  
ControlGroup=/system.slice/minifipi.service  
Id=minifipi.service  
Description=Apache MiNiFi as service  
FragmentPath=/etc/systemd/system/minifipi.service  
```
This results means:
  * In RPi `192.168.0.17` the `warpi` (Kismet) and `minifipi` (MiNiFi) services are not running.
  * In RPi `192.168.0.18` the `warpi` (Kismet) and `minifipi` (MiNiFi) services are running.
And if you want further details about the Apache MiNiFi running in RPi `192.168.0.18`, just execute this command:
```sh  
$ ansible 192.168.0.18 -i inventory -a "systemctl status minifipi" -k
SSH password:  
192.168.0.18 | SUCCESS | rc=0 >>  
● minifipi.service - Apache MiNiFi as service  
Loaded: loaded (/etc/systemd/system/minifipi.service; enabled)  
Active: active (running) since Mon 2017-03-20 16:56:59 UTC; 42min ago  
Process: 5474 ExecStart=/home/picuy/minifi-0.1.0/bin/minifi.sh start (code=exited, status=0/SUCCESS)  
Main PID: 5490 (minifi.sh)  
CGroup: /system.slice/minifipi.service  
├─5490 /bin/sh /home/picuy/minifi-0.1.0/bin/minifi.sh start  
├─5491 /usr/bin/java -cp /home/picuy/minifi-0.1.0/conf:/home/picuy/minifi-0.1.0/lib/bootstrap/*:/home/picuy/minifi-0.1.0/lib/* -Xms12m -Xmx24m -Dorg.apache.nifi.minifi.bootstrap.config.log.dir=/home/picuy/minifi-0.1.0/logs -Dorg.apache.nifi.minifi.bootstrap.config.pid.dir=/home/picuy/minifi-0.1.0/run -Dorg.apache.nifi.minifi.bootstrap.config.file=/home/picuy/minifi-0.1.0/conf/bootstrap.conf org.apache.nifi.minifi.bootstrap.RunMiNiFi start  
└─5510 java -classpath /home/picuy/minifi-0.1.0/./conf:/home/picuy/minifi-0.1.0/./lib/jetty-util-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/httpcore-nio-4.4.5.jar:/home/picuy/minifi-0.1.0/./lib/jsr311-api-1.1.1.jar:/home/picuy/minifi-0.1.0/./lib/commons-codec-1.10.jar:/home/picuy/minifi-0.1.0/./lib/httpasyncclient-4.1.1.jar:/home/picuy/minifi-0.1.0/./lib/jcl-over-slf4j-1.7.12.jar:/home/picuy/minifi-0.1.0/./lib/nifi-framework-core-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jetty-server-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/nifi-security-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jetty-schemas-3.1.jar:/home/picuy/minifi-0.1.0/./lib/curator-recipes-2.11.0.jar:/home/picuy/minifi-0.1.0/./lib/jetty-security-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/nifi-framework-authorization-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-api-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-properties-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/spring-core-4.2.4.RELEASE.jar:/home/picuy/minifi-0.1.0/./lib/nifi-properties-loader-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/antlr-runtime-3.5.2.jar:/home/picuy/minifi-0.1.0/./lib/json-path-2.0.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-web-utils-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/commons-lang3-3.4.jar:/home/picuy/minifi-0.1.0/./lib/nifi-security-utils-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-site-to-site-reporting-task-1.0.0.jar:/home/picuy/minifi-0.1.0/./lib/org.eclipse.jdt.core-3.8.2.v20130121.jar:/home/picuy/minifi-0.1.0/./lib/jersey-client-1.19.jar:/home/picuy/minifi-0.1.0/./lib/jul-to-slf4j-1.7.12.jar:/home/picuy/minifi-0.1.0/./lib/ecj-4.4.2.jar:/home/picuy/minifi-0.1.0/./lib/guava-18.0.jar:/home/picuy/minifi-0.1.0/./lib/jetty-webapp-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/lucene-core-4.10.4.jar:/home/picuy/minifi-0.1.0/./lib/commons-collections4-4.0.jar:/home/picuy/minifi-0.1.0/./lib/apache-jsp-8.0.33.jar:/home/picuy/minifi-0.1.0/./lib/nifi-site-to-site-client-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-site-to-site-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/httpclient-4.4.1.jar:/home/picuy/minifi-0.1.0/./lib/jackson-jaxrs-1.9.2.jar:/home/picuy/minifi-0.1.0/./lib/jetty-xml-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/nifi-data-provenance-utils-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/curator-framework-2.11.0.jar:/home/picuy/minifi-0.1.0/./lib/curator-client-2.11.0.jar:/home/picuy/minifi-0.1.0/./lib/javax.servlet-api-3.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jersey-core-1.19.jar:/home/picuy/minifi-0.1.0/./lib/jetty-servlet-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/jetty-servlets-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/nifi-runtime-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jackson-annotations-2.6.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-framework-api-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/apache-jsp-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/nifi-persistent-provenance-repository-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/javax.el-api-3.0.0.jar:/home/picuy/minifi-0.1.0/./lib/jetty-jsp-jdt-2.3.3.jar:/home/picuy/minifi-0.1.0/./lib/minifi-framework-core-0.1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-socket-utils-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/javax.json-1.0.4.jar:/home/picuy/minifi-0.1.0/./lib/nifi-ssl-context-service-api-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-user-actions-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/logback-core-1.1.7.jar:/home/picuy/minifi-0.1.0/./lib/jasypt-1.9.2.jar:/home/picuy/minifi-0.1.0/./lib/nifi-processor-utils-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-logging-utils-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-administration-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/httpcore-4.4.1.jar:/home/picuy/minifi-0.1.0/./lib/minifi-runtime-0.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jackson-databind-2.6.1.jar:/home/picuy/minifi-0.1.0/./lib/quartz-2.2.1.jar:/home/picuy/minifi-0.1.0/./lib/apache-el-8.0.33.jar:/home/picuy/minifi-0.1.0/./lib/jetty-continuation-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/commons-logging-1.2.jar:/home/picuy/minifi-0.1.0/./lib/nifi-framework-cluster-protocol-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/javax.servlet.jsp-api-2.3.1.jar:/home/picuy/minifi-0.1.0/./lib/javax.json-api-1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-client-dto-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/nifi-framework-core-api-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/asm-1.0.2.jar:/home/picuy/minifi-0.1.0/./lib/json-smart-2.1.1.jar:/home/picuy/minifi-0.1.0/./lib/javax.servlet.jsp-2.3.2.jar:/home/picuy/minifi-0.1.0/./lib/asm-3.3.1.jar:/home/picuy/minifi-0.1.0/./lib/javax.el-3.0.1-b08.jar:/home/picuy/minifi-0.1.0/./lib/jackson-xc-1.9.2.jar:/home/picuy/minifi-0.1.0/./lib/slf4j-api-1.7.12.jar:/home/picuy/minifi-0.1.0/./lib/minifi-nar-utils-0.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jersey-json-1.19.jar:/home/picuy/minifi-0.1.0/./lib/nifi-schema-utils-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jackson-mapper-asl-1.9.13.jar:/home/picuy/minifi-0.1.0/./lib/logback-classic-1.1.7.jar:/home/picuy/minifi-0.1.0/./lib/minifi-utils-0.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jettison-1.1.jar:/home/picuy/minifi-0.1.0/./lib/bcprov-jdk15on-1.54.jar:/home/picuy/minifi-0.1.0/./lib/javax.servlet.jsp.jstl-api-1.2.1.jar:/home/picuy/minifi-0.1.0/./lib/jetty-http-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/nifi-expression-language-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jetty-io-9.3.9.v20160517.jar:/home/picuy/minifi-0.1.0/./lib/jackson-core-2.6.1.jar:/home/picuy/minifi-0.1.0/./lib/nifi-utils-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/bcpkix-jdk15on-1.54.jar:/home/picuy/minifi-0.1.0/./lib/nifi-write-ahead-log-1.1.0.jar:/home/picuy/minifi-0.1.0/./lib/jackson-core-asl-1.9.13.jar -Dorg.apache.jasper.compiler.disablejsr199=true -Xmx256m -Xms256m -Dsun.net.http.allowRestrictedHeaders=true -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -Djava.protocol.handler.pkgs=sun.net.www.protocol -Dnifi.properties.file.path=/home/picuy/minifi-0.1.0/./conf/nifi.properties -Dnifi.bootstrap.listen.port=38084 -Dapp=MiNiFi -Dorg.apache.nifi.minifi.bootstrap.config.log.dir=/home/picuy/minifi-0.1.0/logs org.apache.nifi.minifi.MiNiFi  
```

## 4\. Conclusions.
  * You can see the really value of using Ansible when you are provisioning multiple devices. Just execute Ansible command to start performing Linux command and create your own Ansible Playbooks to provision software like Kismet and MiNiFi.
  * Remember I never provisioned / installed an Ansible agent in the Device side, just download my Playbooks in my PC and ready !.
  * For other side you can tweak your Playbooks in order to deploy your services with restricted Linux privileged users. That is required when you are doing Automation in a constrained / restricted devices or VMs.
  * The Kismet and MiNiFi Ansible Playbooks are ready to be used in a PoC, but I don't recommend to use it in PROD because they need to be improved. For example I have to: 
    * Implement them as Ansible Roles.
    * Implement Ansible Tasks to start Kismet and MiNiFi as `systemd` services with restricted Linux user, no `root`.
    * Implement Ansible Tasks to read and send in batch the logs or event files for Kismet and MiNiFi to external system as Syslog Server or Solr or Elasticsearch.
In the next blog post I will explain how to integrate/connect each Raspberry Pi (Kismet and MiNiFi) to a centralized Apache NiFi by using Ansible, of course!.

## 5\. References.
Using Ansible with Raspberry Pi cluster.  
http://www.pidramble.com
Setup a Headless Raspberry Pi with Raspbian Jessie on OS X.  
http://blog.smalleycreative.com/linux/setup-a-headless-raspberry-pi-with-raspbian-jessie-on-os-x/
macOS Sierra SSH “Permission Denied”.  
https://rolfje.wordpress.com/2016/11/12/macos-sierra-ssh-permission-denied/
