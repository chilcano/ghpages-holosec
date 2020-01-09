---
layout: post
title:  "Building your own affordable Cloud to host a Service Mesh - Part 1"
date:   2019-12-20 10:00:00 +0100
categories: cloud apaas "service mesh" 
tags: aws gcp azure docker kubernetes 
permalink: "/2019/12/20/building-your-own-affordable-cloud-to-host-a-service-mesh-part1"
---
I want to build a Container-based Cloud to deploy any kind of workload (RESTful API, Microservices, Event-Driven, Functions, etc.) but it should be affordable, ready to use, reliable, secure and productionable. This means:

- Productionable: ready to be used as a production environment.
- Reliable and Secure: at least reliable because we will improve the security level implementing more security controls.
- Affordable: cheaper.
- Ready to use: able to be automated (DevOps and IaC) with a mature management API.

![Service Mesh hosted in a Container-based Cloud](/assets/img/20191220-service-mesh-01-reference-architecture.png "Service Mesh hosted in a Container-based Cloud")

These requeriments restric some options, all of them using any Public Cloud Provider, but considering the [AWS Spot Instances](https://aws.amazon.com/ec2/spot) and [Google Cloud Preemptible VM Instances](https://cloud.google.com/preemptible-vms). Unfortunately Microsoft Azure only provides Low-Priority VMs to be used from Azure Batch Service. But if you are new user, you could apply for using the Free Tier in all of 3 Cloud Providers. 

<!-- more -->

For further information, you can read these articles:

* [Understanding Excess Cloud Capacity: Amazon EC2 Spot vs. Azure Low-Priority VM vs. Google Preemptible VM vs IBM Transient Servers by Zev Schonberg, 2019-Mar](https://spotinst.com/blog/amazon-ec2-spot-vs-azure-lpvms-vs-google-pvms-vs-ibm-transient-servers/)
* [Cloud vendor free tiers compared: AWS vs Azure vs Google Cloud Platform by Scott Carey, 2018-May](https://www.computerworld.com/article/3427685/cloud-vendor-free-tiers-compared--aws-vs-azure-vs-google-cloud-platform.html)

The automation can be achieved using Terraform, Python, Bash, etc. and the 2 Cloud Providers (AWS and Google Cloud) choosen for this project have a mature Management API which can be used to automatization and configuration.


## What about Digital Ocean, Hetzner Cloud, Scaleway and other Providers?

Yes, all of them are worthy of being considered too and will do it in next blog posts, but let's start with AWS and Google first. 
Ah, if you don't want to wait, I suggest you read the [Kubernetes clusters for the hobbyist](https://github.com/hobby-kube/guide) guide written by Patrick Stadler. This guide explains how to build a Kubernetes Cluster on [Digital Ocean](https://www.digitalocean.com), [Hetzner Cloud](https://www.hetzner.com/) and/or [Scaleway](https://www.scaleway.com), and this guide is accompanied by a fully automated cluster setup based on Terraform recipes. The guide suggests that [Linode](https://www.linode.com/), [Vultr](https://www.vultr.com) are other viable options. 

  
  If you know a reliable, secure and affordable Cloud Provider, don't hesitate to drop me a line.
  


## Cheap doesn't mean unreliable



## Cheap doesn't mean unsecure

yyyy

## Let's do it

```sh
chilcano@inti:~/git-repos/ghpages-holosec$ bundle add jekyll-paginate
```


## References

- [Kubernetes: The Surprisingly Affordable Platform for Personal Projects](https://www.doxsey.net/blog/kubernetes--the-surprisingly-affordable-platform-for-personal-projects) By Caleb Doxsey - Sep 30, 2018
- [Affordable Kubernetes Cluster](https://devonblog.com/containers/affordable-kubernetes-cluster/) By Remko Seelig o - March 12, 2019
- [Kubernetes clusters for the hobbyist](https://github.com/hobby-kube/guide) By Patrick Stadler - Latest commit on Oct 11, 2019
- [Kubernetes on the cheap](https://software.danielwatrous.com/kubernetes-on-the-cheap/) By Daniel Watrous - Feb 9, 2019
- [Kubernetes for poor - How to run your cluster and not go bankrupt](https://itsilesia.com/kubernetes-for-poor-how-to-run-your-cluster-and-not-go-bankrupt/) By Aleksander Grzybowski - Nov 13, 2018
- [Diary of a GKE Journeyman - Running your personal Kubernetes Cluster for (almost) free on GKE](https://mehdi.me/running-a-personal-kubernetes-cluster-for-almost-from-on-gke/) By Mehdi El Gueddari - Mar 22, 2019 

### Kubernetes' distributions

- [MicroK8s - Zero-ops Kubernetes for workstations and edge / IoT](https://microk8s.io/) By Ubuntu
- [K3s - Lightweight Kubernetes for IoT & Edge computing](https://k3s.io/) By Rancher
- [KinD - Kubernetes in Docker](https://github.com/kubernetes-sigs/kind) By Kubernetes SIGs
- [Typhoon - Minimal and free Kubernetes distribution](https://github.com/poseidon/typhoon) By poseidon
