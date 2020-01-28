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
$ terraform plan \
  -var cluster-name="cheapk8s" \
  -var k8s-ssh-key="ssh-key-for-us-east-1" \
  -var admin-cidr-blocks="83.50.13.174/32" \
  -var region="us-east-1" \
  -var kubernetes-version="1.14.3" \
  -var external-dns-enabled="1" \
  -var nginx-ingress-enabled="1" \
  -var nginx-ingress-domain="ingress-nginx.cloud.holisticsecurity.io" \
  -var cert-manager-enabled="1" \
  -var cert-manager-email="cheapk8s@holisticsecurity.io" 

$ terraform apply \
  -var cluster-name="cheapk8s" \
  -var k8s-ssh-key="ssh-key-for-us-east-1" \
  -var admin-cidr-blocks="83.50.13.174/32" \
  -var region="us-east-1" \
  -var kubernetes-version="1.14.3" \
  -var external-dns-enabled="1" \
  -var nginx-ingress-enabled="1" \
  -var nginx-ingress-domain="ingress-nginx.cloud.holisticsecurity.io" \
  -var cert-manager-enabled="1" \
  -var cert-manager-email="cheapk8s@holisticsecurity.io" 
```

### 3) Checking recently created K8s Cluster and JetStack Cert-Manager


Calling `health check` over TLS.
```sh
$ curl https://ingress-nginx.cloud.holisticsecurity.io/healthz -v -k

*   Trying 34.236.145.206:443...
* TCP_NODELAY set
* Connected to ingress-nginx.cloud.holisticsecurity.io (34.236.145.206) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* successfully set certificate verify locations:
*   CAfile: none
  CApath: /etc/ssl/certs
* TLSv1.3 (OUT), TLS handshake, Client hello (1):
* TLSv1.3 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS change cipher, Change cipher spec (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS handshake, Finished (20):
* SSL connection using TLSv1.2 / ECDHE-RSA-AES256-GCM-SHA384
* ALPN, server accepted to use h2
* Server certificate:
*  subject: O=Acme Co; CN=Kubernetes Ingress Controller Fake Certificate
*  start date: Jan 28 12:31:05 2020 GMT
*  expire date: Jan 27 12:31:05 2021 GMT
*  issuer: O=Acme Co; CN=Kubernetes Ingress Controller Fake Certificate
*  SSL certificate verify result: unable to get local issuer certificate (20), continuing anyway.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* Using Stream ID: 1 (easy handle 0x5598ae6d01d0)
> GET /healthz HTTP/2
> Host: ingress-nginx.cloud.holisticsecurity.io
> User-Agent: curl/7.65.3
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS == 128)!
< HTTP/2 200 
< server: nginx/1.15.5
< date: Tue, 28 Jan 2020 15:23:26 GMT
< content-type: text/html
< content-length: 0
< 
* Connection #0 to host ingress-nginx.cloud.holisticsecurity.io left intact
```

Showing TLS Certificate using `openssl`.
```sh
$ echo | openssl s_client -showcerts -servername ingress-nginx.cloud.holisticsecurity.io -connect ingress-nginx.cloud.holisticsecurity.io:443 2>/dev/null | openssl x509 -inform pem -noout -text

Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number:
            4a:2e:7a:bc:14:6b:ef:2d:29:e8:06:56:38:6f:7b:08
        Signature Algorithm: sha256WithRSAEncryption
        Issuer: O = Acme Co, CN = Kubernetes Ingress Controller Fake Certificate
        Validity
            Not Before: Jan 28 12:31:05 2020 GMT
            Not After : Jan 27 12:31:05 2021 GMT
        Subject: O = Acme Co, CN = Kubernetes Ingress Controller Fake Certificate
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                RSA Public-Key: (2048 bit)
                Modulus:
                    00:ad:d9:47:e9:4c:dd:1a:c5:b7:60:11:04:cb:fc:
                    81:51:e3:3a:f6:3f:59:83:28:48:52:74:f2:65:55:
                    58:59:11:39:84:20:65:82:97:e2:ed:79:1d:21:15:
                    7a:10:d8:53:b2:01:a0:9d:b8:ef:f4:de:2b:a8:69:
                    1d:92:10:60:b9:1f:41:9b:c2:8b:b3:0d:3b:94:79:
                    f4:0e:82:be:d6:ea:54:32:27:00:55:e3:8a:89:c7:
                    56:b9:67:50:c4:5c:76:b2:7b:50:c9:80:e4:78:1c:
                    21:b5:ea:8e:97:fd:24:76:a9:87:00:47:32:2f:dc:
                    f7:38:99:37:86:ab:fb:37:65:3d:99:fb:d9:89:db:
                    15:0a:10:9c:19:92:45:ff:10:99:9d:18:0a:d9:85:
                    36:7b:50:b1:11:1c:e0:33:30:51:86:30:e6:24:44:
                    a3:76:e9:c6:19:55:44:15:4d:1b:8a:dd:ac:27:4b:
                    cd:bf:68:74:35:db:52:4a:5f:7b:f5:2c:88:81:10:
                    7d:13:bc:96:cb:3f:fd:2b:d2:cc:d0:0f:0b:f3:c4:
                    4a:57:07:8d:27:02:60:d0:2f:5a:2d:d6:fe:d5:0f:
                    87:22:29:d0:68:98:66:6d:d5:1e:a9:15:85:08:72:
                    da:9b:56:4e:13:77:0a:0f:58:a2:1d:2a:6f:02:31:
                    90:0d
                Exponent: 65537 (0x10001)
        X509v3 extensions:
            X509v3 Key Usage: critical
                Digital Signature, Key Encipherment
            X509v3 Extended Key Usage: 
                TLS Web Server Authentication
            X509v3 Basic Constraints: critical
                CA:FALSE
            X509v3 Subject Alternative Name: 
                DNS:ingress.local
    Signature Algorithm: sha256WithRSAEncryption
         40:0d:65:50:11:88:cc:f5:99:43:1a:81:a0:c9:ec:10:13:73:
         7c:c9:18:5e:ac:82:94:dd:d1:b0:5b:f4:ef:a7:9a:a0:c0:9a:
         b3:a1:da:15:6d:ce:15:c4:a3:c0:e2:e1:af:e9:e1:1d:69:7d:
         9c:0f:66:0c:7d:d1:c0:da:58:f0:be:5c:34:ed:fb:9d:50:ed:
         e0:18:99:32:81:ee:7a:c8:b2:be:63:a0:ca:e9:2c:a8:f5:21:
         85:93:25:ff:0a:13:40:72:b9:25:aa:be:30:d1:dd:26:17:7d:
         fe:4b:6a:4b:d8:1f:9e:f6:01:f2:1f:cf:bc:2c:53:b4:32:f5:
         c6:7c:00:ac:c0:61:a2:ac:13:8d:dd:b6:55:7c:b9:7e:43:e6:
         16:ba:3d:5b:50:c2:7a:3e:b4:22:bd:01:f2:36:44:ac:4f:3e:
         20:d1:4d:ff:4e:d6:20:71:28:cb:69:1d:ad:40:93:69:7c:e7:
         33:75:bb:9b:1c:51:f3:a3:3e:93:c9:39:0e:28:78:1b:6b:97:
         2f:e7:39:92:91:99:e4:5e:b7:89:03:b1:f1:c2:9a:9b:97:d5:
         03:c1:de:bc:fa:ef:84:23:8b:a6:ae:18:da:a1:0b:58:5b:4d:
         70:27:bd:b2:18:8a:7f:bb:6c:90:e6:76:f6:ff:81:1a:4d:30:
         2b:63:38:22
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
