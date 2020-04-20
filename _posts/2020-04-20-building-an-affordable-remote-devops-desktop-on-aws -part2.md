---
layout: post
title:  "Building an affordable remote DevOps desktop on AWS - Part2 (custom AMI with Packer)"
date:   2020-04-20 10:00:00 +0100
categories: ['cloud', 'devops'] 
tags: ['remote', 'aws', 'docker', 'git', 'vs code', 'terraform', 'packer']
permalink: "/2020/04/20/building-an-affordable-remote-devops-desktop-on-aws-part2"
comments: true
---
In the previous post [Building an affordable remote DevOps desktop on AWS](/2020/04/09/building-an-affordable-remote-devops-desktop-on-aws) I shown you how to build a cheaper remote DevOps Desktop on AWS, now I'll explain you how to do that in approximately 3 minutes, instead of 25 minutes, using [Hashicorp Packer](https://www.packer.io) to pre-bake an AWS AMI. 

[![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-1-arch-packer.png)](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/imgs/remote-devops-desktop-x2go-client-1-arch-packer.png)

<!-- more -->

By default, this article uses Terraform and Packer to provision a cheap AWS EC2 Spot instance. The approximate cost will be less of 10 euros/month for a `m1.small` instance located in `us-east-1` region. Also I share the [GitHub repository with all Terraform and Packer scripts](https://github.com/chilcano/affordable-remote-desktop) where you will be able to change the configuration.

## Getting started

### Clone this repository

```sh
$ git clone https://github.com/chilcano/affordable-remote-desktop
$ cd affordable-remote-desktop
```

### Execute Terraform plan using customized AMI (by default)

This process uses my customized public AMI (Name `chilcano/images/hvm-instance/ubuntu-bionic-18.04-amd64-gui` and Owner `Chilcano`). This customized AMI with `XFCE4` and `X2Go Server` pre-installed has been created using Hashicorp Packer.

```sh
$ terraform init

$ terraform plan \
  -var node_name="devops1" \
  -var ssh_key="remotedesktop" \
  -var developer_cidr_blocks="83.32.214.211/32" 

$ terraform apply \
  -var node_name="devops1" \
  -var ssh_key="remotedesktop" \
  -var developer_cidr_blocks="83.32.214.211/32" 
```

### Execute Terraform plan providing a customized AMI (using Packer.io)

The Terraform plan I share here detects if the base AMI used to build the EC2 Instance has `XFCE4` and `X2Go Server` pre-installed, if so Terraform will install both packages taking ~20 minutes more. In the below example, the Terraform plan execution will install both packages because the `ubuntu/images/hvm-ssd/ubuntu-bionic-18.04-amd64-server` AMI owned by `099720109477` (Ubuntu) doesn't include any GUI Desktop Environment installed.

```sh
$ terraform init

$ terraform plan \
  -var node_name="devops2" \
  -var ssh_key="remotedesktop" \
  -var developer_cidr_blocks="83.32.214.211/32" \
  -var ami_name_filter="ubuntu/images/hvm-ssd/ubuntu-bionic-18.04-amd64-server-*"\
  -var ami_owner="099720109477" 

$ terraform apply \
  -var node_name="devops2" \
  -var ssh_key="remotedesktop" \
  -var developer_cidr_blocks="83.32.214.211/32" \
  -var ami_name_filter="ubuntu/images/hvm-ssd/ubuntu-bionic-18.04-amd64-server-*"\
  -var ami_owner="099720109477" 
```

#### Crerating a custom AMI with Packer

Finally, if you don't have any customized AMI with `XFCE4` and `X2Go Server` pre-installed, and you want one but `private`, then you are lucky because [I've shared Packer scripts to cook your own](https://github.com/chilcano/affordable-remote-desktop/tree/master/resources/packer). Then the steps are:

1. Install and configure Packer.
2. Download Packer (`ubuntu_gui.json`) script.
   ```sh
   $ cd affordable-remote-desktop/resources/packer
   $ export AWS_ACCESS_KEY_ID="your-access-key-id"; export AWS_SECRET_ACCESS_KEY="your-secret-access-key"
   $ export AWS_VPC_ID="your-vpc-id-07c2fc78af4aca574"; export AWS_SUBNET_ID="your-subnet-id-00096b5a3329dd4b2" 

   $ packer validate ubuntu_gui.json
   $ packer build ubuntu_gui.json
   ```
2. Once created the custom AMI, you are able to provision your Remote DevOps Desktop on AWS using Terraform.
   ```sh
   $ terraform init
   
   $ terraform plan \
     -var node_name="devops2" \
     -var ssh_key="remotedesktop" \
     -var developer_cidr_blocks="83.32.214.211/32" \
     -var ami_name_filter="your-ami-name-filter"\
     -var ami_owner="your-ami-owner" 
   
   $ terraform apply \
     -var node_name="devops2" \
     -var ssh_key="remotedesktop" \
     -var developer_cidr_blocks="83.32.214.211/32" \
     -var ami_name_filter="your-ami-name-filter"\
     -var ami_owner="your-ami-owner" 
   ```

### Verifying the process

### Check the AMI and Snapshot created

[![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/resources/packer/imgs/packer-ubuntu-ami-gui-1-create-default-vpc.png){:width="300"}](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/resources/packer/imgs/packer-ubuntu-ami-gui-1-create-default-vpc.png)[![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/resources/packer/imgs/packer-ubuntu-ami-gui-2-create-default-vpc.png){:width="300"}](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/resources/packer/imgs/packer-ubuntu-ami-gui-2-create-default-vpc.png)

[![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/resources/packer/imgs/packer-ubuntu-ami-gui-3-snapshot-ebs.png){:width="300"}](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/resources/packer/imgs/packer-ubuntu-ami-gui-3-snapshot-ebs.png)[![](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/resources/packer/imgs/packer-ubuntu-ami-gui-4-ec2-ami.png){:width="300"}](https://raw.githubusercontent.com/chilcano/affordable-remote-desktop/master/resources/packer/imgs/packer-ubuntu-ami-gui-4-ec2-ami.png)

### Check the EC2 Instance created

After a few minutes, connect to EC2 instance created above.

```sh
$  terraform output remotedesktop_fqdn
ec2-54-160-183-171.compute-1.amazonaws.com

$ ssh ubuntu@$(terraform output remotedesktop_fqdn) -i ~/.ssh/remotedevenv

// Checking Cloud-Init 
ubuntu@ip-10-0-100-4:~$ tail -f /var/log/cloud-init-output.log

// Checking the bash scripts created by Cloud-Init
ubuntu@ip-10-0-100-4:~$ ls -la /var/lib/cloud/instance/scripts/
total 16
drwxr-xr-x 2 root root 4096 Apr 15 18:28 .
drwxr-xr-x 5 root root 4096 Apr 15 18:35 ..
-rwx------ 1 root root 2651 Apr 15 18:28 install_devops.sh
-rwx------ 1 root root 1149 Apr 15 18:28 install_gui.sh
```

The `install_devops.sh` and `install_gui.sh` were created by Terraform during provisioning, both bash scripts install and configure the DevOps tools and GUI tools respectively.

### Further information

1. [Creating an Ubuntu-based AMI with minimum GUI based on XFCE4 and X2Go](https://github.com/chilcano/affordable-remote-desktop/tree/master/resources/packer)

