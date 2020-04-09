---
layout: post
title:  "Building an affordable remote DevOps desktop on AWS"
date:   2020-04-09 10:00:00 +0100
categories: ['cloud', 'devops'] 
tags: ['remote', 'aws', 'docker', 'git', 'vs code', 'terraform']
permalink: "/2020/04/09/building-an-affordable-remote-devops-desktop-on-aws"
comments: true
---
If you're going to work 100% remotely or are just tired of carrying a heavy laptop while commuting, why not spin up a DevOps Desktop PC in a public cloud and work from anywhere you want?
So, if you like the idea, this post for you.
In this post I'll explain you how to build your own Remote DevOps Desktop on AWS and configure your thinner Local PC to connect to remote one. 

[![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-0-arch.png)](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-0-arch.png)

<!-- more -->

This article uses Terraform to provision a cheap AWS EC2 Spot instance. The approximate cost will be less of 10 euros/month for a `m1.small` instance located in `us-east-1` region. Also I'll share the Github repository with all Terraform scripts where you will be able to change the configuration.

## Getting started

You have to clone the **[Affordable Remote DevOps Desktop](https://github.com/chilcano/affordable-remote-desktop)** and run through all steps explained there. Next a compacted list of commands:

```sh
$ git clone https://github.com/chilcano/affordable-remote-desktop
$ cd affordable-remote-desktop

$ terraform init

$ terraform plan \
  -var devenv_name="cheapdevops" \
  -var ssh_key="remotedevenv" \
  -var developer_cidr_blocks="83.32.214.211/32" 

$ terraform apply \
  -var devenv_name="cheapdevops" \
  -var ssh_key="remotedevenv" \
  -var developer_cidr_blocks="83.32.214.211/32" 
```

After a few minutes, you will be able to connect to your remote DevOps Desktop through SSH.

```sh
$ ssh ubuntu@$(terraform output remotedevenv_fqdn) -i ~/.ssh/remotedevenv
```

## Software installed in the remote DevOps Desktop

In the moment of writting this post, the software base and tools installed are:

1. Ubuntu 18.04 server (`ubuntu/images/hvm-instance/ubuntu-bionic-18.04-amd64-server`)
2. [XFCE4 Desktop](https://www.xfce.org)
3. [X2Go](https://wiki.x2go.org)
4. EC2 Spot Instance
   - `m1.small` (default)
   - `us-east-1` (default)

| Tools installed | Installed | Version 
| ---             | ---       | ---
| 1) Chromium     | Yes       | 80.0.3987.163
| 2) VS Code      | Yes       | 1.43.2 
| 3) Terraform    | Yes       | 0.12.24 
| 4) AWS CLI      | Yes       | 1.14.44  
| 5) Git          | Yes       | 2.17.1
| 6) Python       | Yes       | 3.6.9 
| 7) Java         | Yes       | 11
| 8) Docker       | Yes       | 19.03.6

## Why XFCE4 and X2Go?

The answer is simple, XFCE4 is a lightweight Linux Desktop Environment and works flawlessly so far with X2Go.
X2Go is a X-windows remote access software that has clients for Windows, Mac, and Linux and provides the best performance compared to [X11 - X Window System](https://en.wikipedia.org/wiki/X_Window_System), VNC and XRDP (X-windows version of the Windows Remote Desktop Protocol).

## Things to improve

1. Instead of using a standard Ubuntu AMI, prepare a custom AMI with pre-installed XFCE4 and X2Go (getting this EC2 instance created takes 25 minutes!), and moving all DevOps Tooling installation to other separate script.
2. Use Ansible instead of Bash script to provision the DevOps tools such as VS Code, Java, Terraform, Git, etc.
3. Backup and restoring of critical, sensitive information and custom configuration into AWS S3 or X2Go capabilities to mount remote disks over SSH.
4. Vertical Autoscaling (increase RAM, CPU or change type of instance).
5. Metrics (performance when using ADSL, WAN, 4G, etc.)

## Connecting to the remote DevOps Desktop

Below some screenshots if you want to know how looks like in Ubuntu 19.10 and if you want configure your X2Go Client.
Before lets get the FQDN of your EC2 instance.  

```sh
chilcano@inti:~/git-repos/affordable-remote-devenv$ terraform output remotedevenv_fqdn
ec2-100-26-48-80.compute-1.amazonaws.com
```
![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-1.png){:width="300"}![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-2.png){:width="300"}
![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-3.png){:width="300"}![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-4.png){:width="300"}

And finally here below the Remote DevOps Desktop.   

![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-5.png){:width="300"}[![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-6b.png){:width="400"}](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-6b.png)

And here the configuration of a great feature: Sharing files between local and remote instance over SSH using X2Go Client.   

![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-7.png){:width="300"}[![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-7b.png){:width="400"}](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-7b.png)


## Conclusions

1. AWS EC2 Spot is still the cheaper Public Cloud resource in the market, although you could get cheaper one in [Digital Ocean](https://www.digitalocean.com) and [Google Cloud](https://cloud.google.com).
2. XFCE4 and X2Go, one is lightweight and the other is faster. Although there are not good X2Go client for iOS, both together are still the best option.
3. Creating an EC2 Spot instance with XFCE4 and X2Go takes around 20 minutes and that is not good from the developer experience point of view and neither resilience point of view. The idea is to provide an OS bundle (AMI) that has, as a minimum, XFCE4 and X2Go already pre-installed.
4. Backup and restoration is very important if we are using ephemeral resources, its implementation is considered as a critical functionality and is included in this project, and it will be implemented using AWS S3 or simply X2Go and its ability to sync files remotely over SSH .