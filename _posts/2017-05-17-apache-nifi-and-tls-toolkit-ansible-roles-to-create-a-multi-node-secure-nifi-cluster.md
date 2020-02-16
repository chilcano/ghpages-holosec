---
layout:     post
title:      'Apache NiFi and TLS Toolkit Ansible Roles to create a multi-node secure NiFi cluster'
date:       2017-05-16 23:24:52
categories: ['Big Data', 'DevOps', 'Security']
tags:       ['Ansible', 'Apache NiFi', 'TLS', 'Vagrant']
status:     publish 
permalink:  "/2017/05/17/apache-nifi-and-tls-toolkit-ansible-roles-to-create-a-multi-node-secure-nifi-cluster/"
comments:   true
---
I've created 2 Ansible Roles ([chilcano.apache-nifi](https://galaxy.ansible.com/chilcano/apache-nifi) and [chilcano.apache-nifi-toolkit](https://galaxy.ansible.com/chilcano/apache-nifi-toolkit)) to automate the creation of a multi-node and secure NiFi cluster. The [chilcano.apache-nifi](https://galaxy.ansible.com/chilcano/apache-nifi) Ansible Role doesn't implement [Cluster State coordination](https://nifi.apache.org/docs/nifi-docs/html/administration-guide.html#state_management) through Apache ZooKeeper, and the [TLS Toolkit Standalone mode](https://nifi.apache.org/docs/nifi-docs/html/administration-guide.html#tls-generation-toolkit) has been implemented by the [chilcano.apache-nifi-toolkit](https://galaxy.ansible.com/chilcano/apache-nifi-toolkit) Ansible Role.

[![Automated provisioning Apache NiFi multi-node cluster with Ansible and Vagrant](/assets/blog20170517_nifi_multi_node_ansible/nifi-multi-node-ansible-automation.png){:width="70%"}](/assets/blog20170517_nifi_multi_node_ansible/nifi-multi-node-ansible-automation.png){:target="_blank"}

The purpose of this blog post is explaining how to automate the creation of 5 instances of Apache NiFi, secure and not secure.

<!-- more -->

 The first NiFi instance `nf1` will be a standalone instance running over HTTP. The second instance will will be a customized instance running over HTTPS with Client Certificate authentication. The third, fourth and fifth instances will run over HTTPS with Client Certificate authentication with configuration provided for NiFi TLS Toolkit. The configuration (key-pair, Java key stores and certificates) will be generated in other VM instance provided for `chilcano.apache-nifi-toolkit Ansible Role`.

![Apache NiFi Toolkit - folder structure and files generated](/assets/blog20170517_nifi_multi_node_ansible/nifi-multi-node-toolkit-files-generated.png){:width="70%"}

I've created an Ansible Playbook for you, you can download from this [Git repository](https://github.com/chilcano/ansible-apache-nifi-multi-nodes).

## How to use it - steps

**1. Clone the Ansible playbooks**


```sh  
$ git clone https://github.com/chilcano/ansible-apache-nifi-multi-nodes  
```  

**2. Install `chilcano.apache-nifi`and `chilcano.apache-nifi-toolkit` Ansible Roles**


```sh  
$ cd ansible-apache-nifi-multi-nodes  
$ ansible-galaxy install -r playbooks/requirements.yml  
```  

**3\. Create all VMs with Vagrant**

Create all 6 VMs by using Vagrant.

```sh  
$ cd infra/Vagrant  
$ vagrant up  
```  

**4\. Ansible provisioning with Vagrant**

Now, I'm going to provision (run Ansible Playbooks) through Vagrant. This step will install Apache NiFi TLS Toolkit in the `nftk1` VM, once provisioned, Vagrant will provision 5 VMs following the Ansible Playbook `playbooks/main.yml`. It is very important to start the provision of all NiFi instances after provisioning `nftk1`. In the `playbooks/main.yml` you will see the `nftk1` is declared on top and after `nf1, nf2, nf3, nf4` and `nf5`.

```sh  
$ vagrant provision
$ vagrant status  
Current machine states:
nftk1 running (virtualbox)  
nf5 running (virtualbox)  
nf4 running (virtualbox)  
nf3 running (virtualbox)  
nf2 running (virtualbox)  
nf1 running (virtualbox)
This environment represents multiple VMs. The VMs are all listed  
above with their current state. For more information about a specific  
VM, run `vagrant status NAME`.  
```  

Now we can verify if all instances are running as expected, before we have to install the `Client Certificate` ( _CN=chilcano_OU=INTIX.p12_ ) generated in our browser.  

![Install the Client Certificate](/assets/blog20170517_nifi_multi_node_ansible/nifi-multi-node-client-cert-1install.png){:width="70%"}

The `Client Certificate` only is required when connecting to `nf2, nf3, nf4` and `nf5` because these instances are running over HTTPS with Mutual SSL/TLS Authentication (based on Client Certificate).  

![Select the Client Certificate](/assets/blog20170517_nifi_multi_node_ansible/nifi-multi-node-client-cert-1select.png){:width="50%"}

Open the URL (`http://nf1:8080/nifi`, `http://nf2:9443/nifi`, `http://nf3:9443/nifi`, `http://nf4:9443/nifi` and `http://nf5:9443/nifi`) from Firefox. Instead of hostname you can use the IP address (see `inventory` file).  

![Open NiFi from Firefox](/assets/blog20170517_nifi_multi_node_ansible/nifi-multi-node-browser-all.png){:width="100%"}

## ToDo
1. Improve the Ansible Role `chilcano.apache-nifi` to implement Cluster Status coordination through `Apache ZooKeeper`.
2. Improve the Ansible Role `chilcano.apache-nifi-toolkit` to implement Client/Server mode.
3. Deploy a sample DataFlow in NiFi. 


End.
