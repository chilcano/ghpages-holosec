---
layout: post
title:  "Building your own affordable K8s to host a Service Mesh - Part 3: Certificate Manager"
date:   2020-01-22 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh'] 
tags: ['aws', 'docker', 'kubernetes', 'data plane', 'microservice', 'x509', 'certificate', 'pki', 'tls']
permalink: "/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part3-certificate-manager"
comments: true
---

xyz xyz xyz 


## Steps

### 1) Cleaning everything

```sh
# Removing existing CheapK8s Cluster
$ terraform destroy \
  -var cluster-name="cheapk8s" \
  -var k8s-ssh-key="ssh-key-for-us-east-1" \
  -var admin-cidr-blocks="83.50.13.174/32" \
  -var region="us-east-1" \
  -var kubernetes-version="1.14.3" \
  -var external-dns-enabled="1" \
  -var nginx-ingress-enabled="1" \
  -var nginx-ingress-domain="ingress-nginx.cloud.holisticsecurity.io" 

# Removing unwanted records in our AWS Hosted Zone


```

### 2) Create a fresh K8s Cluster with JetStack Cert-Manager installed

```sh

```

### 3) Checking recently created K8s Cluster and JetStack Cert-Manager

```sh

```

### 4) Using JetStack Cert-Manager

**1. Enabling TLS for a Microservice**
```sh

```

**2. Enabling Mutual TLS Authentication for a Web Application**
```sh

```

## Conclusions

1. xxx
2. yyy
3. zzz

## References

1. [JetStack Cert Manager - x.509 Certs for Kubernetes](https://github.com/jetstack/cert-manager){:target="_blank"}
