---
layout:     post
title:      "A Cloud IDE for the masses"
categories: ['cloud', 'devops'] 
tags:       ['aws', 'k8s', 'docker', 'git', 'wsl', 'linux', 'raspberry pi']
date:       2020-08-05 18:00:00 +0100
permalink:  "/2020/08/05/a-cloud-ide-for-the-masses"
comments:   true
---
Nowadays, you likely are involved in development/devops tasks for the cloud and you are using a [Cloud-based IDE (Integrated Development Environment)](https://en.wikipedia.org/wiki/Online_integrated_development_environment) to accomplish your job, but if you don't have any or you are not happy with yours, then this blog post will explain you how to get one, opensource and in a few minutes.

![](/assets/blog20200805_cloud_ide/cloud-ide-1-evolution.png){:width="400"}{:style="display:block;margin:auto;"}

<!-- more -->

### Is there any white-label Cloud IDE?

Who has used [Eclipse IDE](https://www.eclipse.org/eclipseide/)?, I think many of us, and Who thought was using it when using another different purpose IDE?, e.g. While drawing BPEL processes, while [configuring the Apache Directory Server](https://directory.apache.org/studio/) or while [designing Integration Patterns](https://wso2.com/integration/integration-studio/). Well, your thoughts are right, all these IDEs were built on top of Eclipse IDE.
And nowadays, in the Cloud Native Application world, likely your Cloud-based IDE is built on top of some below:  

1. [Eclipse Che](https://www.eclipse.org/che/). It is based on GWT and Java till version 6. E.g. Codenvy IDE.
2. [Cloud9](https://github.com/c9). E.g. AWS Cloud9 IDE.
3. [GitHub Electron (Atom Shell)](https://www.electronjs.org/). E.g. Visual Studio Code. 

With this information, now we can answer the question "Is there any white-label Cloud IDE?". And the answer is yes and it is GitHub Electron, but to be exact there are two and are Visual Studio Code and Eclipse Theia.

1. [Visual Studio Code](https://github.com/microsoft/vscode). E.g. Eclipse Theia, VSCodium, Code-Server, Facebook Nuclide, Microsoft VS Codespaces, etc. 
2. [Eclipse Theia](https://theia-ide.org/). E.g. Eclipse Che 7, Gitpod, Google Cloud Shell, Arduino Pro IDE, SAP Web IDE, ARM Mbed Studio, etc.

### Getting a Cloud IDE for the masses

To be honest, MS VS Code is the IDE most used as it provides us a good developer experience, a great ecosystem of extensions and its community, but since the VSCode is free and open source, its source code is available to be downloaded. Many persons and organizations have created their own IDEs taking the advantadge of benefits of VSCode ecosystem, as like as VSCode did with GitHub Electron.

I would like to mention 2 forked projects based on VSCode, they are:

1. [VSCodium](https://vscodium.com): VSCodium is a community-driven, freely-licensed binary distribution of Microsoft’s editor VSCode. Microsoft’s vscode source code is open source (MIT-licensed), but the product available for download (Visual Studio Code) is licensed under this not-FLOSS license and contains telemetry/tracking. 
2. [Code-Server](https://github.com/cdr/code-server): It runs VSCode on any machine anywhere and access it in the browser.

I've been playing with __Code-Server__ for a while, it is mature and ready to use, and in this blog post I will explain you how to get running easily it in less than 5 minutes. In fact, it is as easy and friendly as VSCode that I'm writting this blog post using Code-Server and [Jekyll](https://jekyllrb.com).

![](/assets/blog20200805_cloud_ide/cloud-ide-2-using-code-server-and-jekyll-on-wsl2.png){:width="400"}{:style="display:block;margin:auto;"}

#### Installing and running Code-Server in Linux, FreeBSD and Mac OSX

Installing Code-Server in Linux (Debian, Ubuntu, Fedora, CentOS, RHEL, SUSE and Arch Linux) and Mac OSX is stright forward. Just go through the [Installation guide](https://github.com/cdr/code-server/blob/master/doc/install.md) where you will be able to use the installation script `install.sh`.

#### Installing and running Code-Server in Raspberry Pi (ARM based) 

The ARM64 package available in [Code-Server - Releases](https://github.com/cdr/code-server/releases) didn't work in my Raspberry Pi 3 Model B+ with Raspbian 10 (buster), I had to install it via `npm`. No worries, I've prepared a bash script to do that.

Installing Code-Server in Raspberry Pi (arm).
```sh
$ wget -q https://raw.githubusercontent.com/chilcano/how-tos/master/resources/code_server_install_rpi.sh
$ chmod +x code_server_install_rpi.sh
$ . code_server_install_rpi.sh
```
This process will install Code-Server and only one VSCode Extension called [`Shan.code-settings-sync`](https://marketplace.visualstudio.com/items?itemName=Shan.code-settings-sync). This extension will allow us to sync configuration, themes, extensions between multiple IDEs using GitHub Gist.  

Removing Code-Server installation in Raspberry Pi (arm).
```sh
$ wget -q https://raw.githubusercontent.com/chilcano/how-tos/master/resources/code_server_remove_rpi.sh
$ chmod +x code_server_remove_rpi.sh
$ . code_server_remove_rpi.sh
```

#### Installing and running Code-Server in Windows 10 with [WSL 2](https://docs.microsoft.com/en-us/windows/wsl/) (Ubuntu 20.04)

Unfortunately Code-Server doesn't have a native installation for Windows 10. In my case I'm using __Windows Subsystem for Linux (WSL 2)__ to get running Linux in Windows 10 and finally get Code-Server installed.
A Linux running through WSL 2 has a few limitations like no `systemd` and so on. No worries again, I've prepared a bash script to go through the installation process smoothly. 

Installing Code-Server in [WSL 2](https://docs.microsoft.com/en-us/windows/wsl/) (Ubuntu 20.04).
```sh
$ wget -q https://raw.githubusercontent.com/chilcano/how-tos/master/resources/code_server_install_wsl2.sh
$ wget -q https://raw.githubusercontent.com/chilcano/how-tos/master/resources/code_server_remove_wsl2.sh
$ chmod +x code_server_install_wsl2.sh code_server_remove_wsl2.sh
$ . code_server_install_wsl2.sh
$ . code_server_remove_wsl2.sh
```
Again, this script will install Code-Server, an extension called [`Shan.code-settings-sync`](https://marketplace.visualstudio.com/items?itemName=Shan.code-settings-sync) and it will suggest you to use my shared [Gist ID b5f88127bd2d89289dc2cd36032ce856](https://gist.github.com/chilcano/b5f88127bd2d89289dc2cd36032ce856). Also, this script will install [`mkcert`](https://github.com/FiloSottile/mkcert) to issue a TLS certificate for the Code-Server.

![](/assets/blog20200805_cloud_ide/cloud-ide-3-code-server-settings-sync-ext-mkcert-tls.png){:width="400"}{:style="display:block;margin:auto;"}

Removing Code-Server installation in WSL2 (Ubuntu 20.04).
```sh
$ wget -q https://raw.githubusercontent.com/chilcano/how-tos/master/resources/code_server_install_wsl2.sh
$ wget -q https://raw.githubusercontent.com/chilcano/how-tos/master/resources/code_server_remove_wsl2.sh
$ chmod +x code_server_install_wsl2.sh code_server_remove_wsl2.sh
$ . code_server_install_wsl2.sh
$ . code_server_remove_wsl2.sh
```

### But... Code-Server isn't installed on the Cloud 

It will be on your favorite Cloud Provider. I'm going to use similar approach I used in [Building an affordable remote DevOps Desktop on AWS](https://holisticsecurity.io/2020/04/09/building-an-affordable-remote-devops-desktop-on-aws), but in this new post I will use [Amazon Elastic Container Service (ECS)](https://aws.amazon.com/ecs/) and [AWS CloudFormation](https://aws.amazon.com/cloudformation/).

Stay tuned.


### References

- [Building an affordable remote DevOps desktop on AWS](https://holisticsecurity.io/2020/04/09/building-an-affordable-remote-devops-desktop-on-aws)
- [GitHub Pages and Jekyll on Windows 10](https://holisticsecurity.io/2020/03/30/github-pages-and-jekyll-on-windows-10)
- [Simple Windows 10 Environment for DevOps Engineers](https://holisticsecurity.io/2020/03/29/simple-windows10-env-with-vscode-git-terraform-awscli-for-devops-engineers)
