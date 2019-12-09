---
layout:     post
title:      'My 3 Ansible Roles to mass provision Kismet and Apache MiNiFi for wardriving at scale'
date:       2017-03-29 19:10:25
categories: ['Big Data', 'DevOps', 'IoT', 'Security']
tags:       ['Ansible', 'Apache MiNiFi', 'Apache NiFi', 'Raspberry Pi']
status:     publish 
permalink:  "/2017/03/29/my-3-ansible-roles-to-mass-provision-kismet-and-apache-minifi-for-wardriving-at-scale/"
---
This blog post is implementing the same scenario used in the previous post [Mass provisioning of Kismet and Apache MiNiFi in Raspberry Pi using Ansible](https://holisticsecurity.io/2017/03/20/mass-provisioning-of-kismet-and-apache-minifi-in-raspberry-pi-using-ansible). The unique difference is this new blog post I'm using [Ansible Roles](http://docs.ansible.com/ansible/playbooks_roles.html) instead of Ansible Playbooks where all automated operations as install, configure, run, etc. were implemented in Ansible Tasks.

![https://holisticsecurity.files.wordpress.com/2017/03/mass-provisioning-kismet-minifi-raspberrypi-ansible-2-pkg.png]({{ site.baseurl }}/assets/mass-provisioning-kismet-minifi-raspberrypi-ansible-2-pkg.png)

<!-- more -->

Then, I've refactored all Ansible Tasks and I've created 3 Ansible Roles. They are in Ansible Galaxy and are:

**Ansible Role Kismet RPi Build (https://galaxy.ansible.com/chilcano/kismet-rpi-build)**

An Ansible Role that builds / compiles from scratch and packs (Debian/Raspbian binary) Kismet on a Raspberry Pi. This Role provides the following features:
  * Download the Kismet source code.
  * Compile the source code in a Raspberry Pi.
  * Generate a Kismet Debian/Raspbian package suitable for Raspberry Pi (ARMv7).

**Ansible Role Kismet RPi Wardriving (https://galaxy.ansible.com/chilcano/kismet-rpi-wardriving)**

An Ansible Role that installs, configures and runs Kismet on a Raspberry Pi. This Role provides the following features:
  * Install Kismet and dependencies.
  * Configure Kismet by deploying an customized `kismet.conf`.
  * Download MAC Addresses Manufacturer List.
  * Enable monitor mode in the Raspberry Pi before starting Kismet.
  * Run Kismet as a `systemd` service.

**Ansible Role Apache MiNiFi (https://galaxy.ansible.com/chilcano/apache-minifi)**

An Ansible Role that installs, configures and runs Apache MiNiFi in tiny devices like a Raspberry Pi, although you can use it on any distro. This Role provides the following features:
  * Install Apache MiNiFi and Java SDK.
  * Configure Apache MiNiFi.
  * Run Apache MiNiFi as a `systemd` service.

## How to use everything
Well, I've updated and deleted Ansible Tasks not used in the existing [Ansible Raspberry Pi Wardriving Github repo](https://github.com/chilcano/ansible-raspberrypi-wardriving) because now I'm using 3 Ansible Roles. Just download the same Github repo with `Tag 2.0.0` and execute the new Ansible Playbook with the same `inventory` file.
Install all Ansible Roles needed as below or using `requirements.yml` file:

```text  
$ sudo ansible-galaxy install geerlingguy.apache  
$ sudo ansible-galaxy install chilcano.kismet-rpi-build  
$ sudo ansible-galaxy install chilcano.kismet-rpi-wardriving  
$ sudo ansible-galaxy install chilcano.apache-minifi
$ ansible-galaxy list
\- chilcano.apache-minifi, 1.0.1  
\- chilcano.kismet-rpi-build, 1.0.4  
\- chilcano.kismet-rpi-wardriving, 1.1.1  
\- geerlingguy.apache, 2.0.1  
\- knopki.locale, a1232f836b5514c58da381d9479640e40d874906  
\- Stouts.hostname, 1.1.0  
\- Stouts.timezone, 2.2.0  
```

...and continue with this:

```text  
$ git clone https://github.com/chilcano/ansible-raspberrypi-wardriving --branch 2.0.0 --single-branch
$ cd ansible-raspberrypi-wardriving
$ git tag -l  
1.0.0  
2.0.0
$ git checkout tags/2.0.0 -b 2.0.0
$ ansible-playbook -i inventory main_all.yml -k  
```

And that's it !.  
I hope It helps.  
Bye.
