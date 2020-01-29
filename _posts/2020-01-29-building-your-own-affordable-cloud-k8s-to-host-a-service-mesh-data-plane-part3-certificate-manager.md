---
layout: post
title:  "Building your own affordable K8s to host a Service Mesh - Part 3: Certificate Manager"
date:   2020-01-29 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh'] 
tags: ['aws', 'docker', 'kubernetes', 'data plane', 'microservice', 'x509', 'certificate', 'pki', 'tls']
permalink: "/2020/01/29/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part3-certificate-manager"
comments: true
---

In this blog post I'll explain how to get a X.509 TLS Certificate from [Let's Encrypt](https://letsencrypt.org){:target="_blank"} automatically during the Terraform provision time, in this way now we can now invoke the services additionally on port 443 (HTTPS / TLS). During the Terraform execution, inmeditely after Kubernetes Cluster creation the [JetStack Cert-Manager](https://github.com/jetstack/cert-manager){:target="_blank"} is deployed in a Pod, it is who will request to [Let's Encrypt](https://letsencrypt.org){:target="_blank"} service a X.509 TLS Certificate, once completed, the [JetStack Cert-Manager](https://github.com/jetstack/cert-manager){:target="_blank"} inject the X.509 Certificate as a Kubernetes Secret into NGINX Ingress Controller to enbale TLS.

At this point you must have created a Kubernetes Cluster with ExternalDNS and NGÏNX as Ingress Controller. In other words, I should have followed the previous post:

* [Part 1 - Building your own affordable K8s to host a Service Mesh](http://holisticsecurity.io/2020/01/16/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-data-plane){:target="_blank"}.
* [Part 2 - Building your own affordable K8s - ExternalDNS and NGINX as Ingress](http://holisticsecurity.io/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-external-dns-ingress){:target="_blank"}.

[![K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png "K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt")](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png){:target="_blank"}

<!-- more -->

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
$ export MY_SUBDOMAIN="cloud.holisticsecurity.io"
$ export HZ_ID=$(aws route53 list-hosted-zones-by-name --dns-name "${MY_SUBDOMAIN}." | jq -r '.HostedZones[0].Id')
$ aws route53 list-resource-record-sets --hosted-zone-id $HZ_ID --query "ResourceRecordSets[?Name != '${MY_SUBDOMAIN}.']" | jq -c '.[]' |
  while read -r RRS; do
    read -r name type <<< $(jq -jr '.Name, " ", .Type' <<< "$RRS") 
    CHG_ID=$(aws route53 change-resource-record-sets --hosted-zone-id $HZ_ID --change-batch '{"Changes":[{"Action":"DELETE","ResourceRecordSet": '"$RRS"' }]}' --output text --query 'ChangeInfo.Id')
    echo " - DELETING: $type $name - CHANGE ID: $CHG_ID"    
  done
```

For further details and an explanation about above step review this post:
* [Part 2 - Building your own affordable K8s - ExternalDNS and NGINX as Ingress](http://holisticsecurity.io/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-external-dns-ingress){:target="_blank"}.


### 2) Create a fresh K8s Cluster with JetStack Cert-Manager installed

Note the `cert-manager-enabled="1"` and `cert-manager-email="cheapk8s@holisticsecurity.io"` parameters which are required to create a Kubernetes Cluster with the [JetStack Cert-Manager](https://github.com/jetstack/cert-manager){:target="_blank"} installed.

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

**1. Exploring the JetStack Cert-Manager resources created in the K8s Cluster**

```sh
# List all namespaces
ubuntu@ip-10-0-100-4:~$ kubectl get ns
NAME              STATUS   AGE
cert-manager      Active   159m
default           Active   161m
ingress-nginx     Active   158m
kube-node-lease   Active   161m
kube-public       Active   161m
kube-system       Active   161m

# Listing all resources under namespace 'cert-manager'
ubuntu@ip-10-0-100-4:~$ kubectl get all -n cert-manager
NAME                                READY   STATUS    RESTARTS   AGE
pod/cert-manager-54d94bb6fc-9zchz   1/1     Running   0          5h22m

NAME                           READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/cert-manager   1/1     1            1           5h22m

NAME                                      DESIRED   CURRENT   READY   AGE
replicaset.apps/cert-manager-54d94bb6fc   1         1         1       5h22m
```


**2. Calling Ingress' `health check` over TLS.**

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

**3. Getting the TLS Certificate using `openssl`**

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

### 4) Calling a Microservice over HTTPS (port 443)

Since all Microservices and RESTful API were configured to be invoked over 80/HTTP and 443/HTTPS and routed through the NGINX Ingress Controller. The only thing to do is call them through their FQDN (Fully Qualified Domain Name) and the Microservices' FQDN could be `https://ingress-nginx.cloud.holisticsecurity.io/<MICROSERVICE_NAME>`.

Then, let's try it using the `Hello Microservice`.

```sh
# Get SSH access to K8s master node
$ ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem
   
# Deploy Hello microservices, services and ingress
ubuntu@ip-10-0-100-4:~$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-app.yaml
ubuntu@ip-10-0-100-4:~$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-svc.yaml
ubuntu@ip-10-0-100-4:~$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-ingress.yaml
ubuntu@ip-10-0-100-4:~$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-ingress-tls.yaml
   
# Get status
ubuntu@ip-10-0-100-4:~$ kubectl get pod,svc,ingress -n hello -o wide
[...]
NAME                                       HOSTS                                     ADDRESS   PORTS     AGE
ingress.extensions/hello-ingress-cip       ingress-nginx.cloud.holisticsecurity.io             80        16m
ingress.extensions/hello-ingress-cip-tls   ingress-nginx.cloud.holisticsecurity.io             80, 443   15s
ingress.extensions/hello-ingress-np        hello-svc-np.cloud.holisticsecurity.io              80        16m
ingress.extensions/hello-ingress-np-tls    hello-svc-np.cloud.holisticsecurity.io              80, 443   15s
```

Now, from any computer in Internet execute this command:

```sh
# Calling Hello Microservices over HTTP
$ curl http://ingress-nginx.cloud.holisticsecurity.io/hello
$ curl http://hello-svc-np.cloud.holisticsecurity.io/hello 

# Calling Hello Microservices over HTTPS/TLS through `hello-ingress-cip-tls`
$ curl https://ingress-nginx.cloud.holisticsecurity.io/hello -v -k

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
*  start date: Jan 29 17:23:09 2020 GMT
*  expire date: Jan 28 17:23:09 2021 GMT
*  issuer: O=Acme Co; CN=Kubernetes Ingress Controller Fake Certificate
*  SSL certificate verify result: unable to get local issuer certificate (20), continuing anyway.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* Using Stream ID: 1 (easy handle 0x5558d18e71d0)
> GET /hello HTTP/2
> Host: ingress-nginx.cloud.holisticsecurity.io
> User-Agent: curl/7.65.3
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS == 128)!
< HTTP/2 200 
< server: nginx/1.15.5
< date: Wed, 29 Jan 2020 17:45:08 GMT
< content-type: text/html; charset=utf-8
< content-length: 55
< strict-transport-security: max-age=15724800; includeSubDomains
< 
Hello version: v1, instance: hello-v1-66fc9c7d98-tljkw
* Connection #0 to host ingress-nginx.cloud.holisticsecurity.io left intact

# Calling Hello Microservices over HTTPS/TLS through `hello-ingress-np-tls`
$ curl https://hello-svc-np.cloud.holisticsecurity.io/hello -v -k

*   Trying 34.236.145.206:443...
* TCP_NODELAY set
* Connected to hello-svc-np.cloud.holisticsecurity.io (34.236.145.206) port 443 (#0)
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
*  start date: Jan 29 17:23:09 2020 GMT
*  expire date: Jan 28 17:23:09 2021 GMT
*  issuer: O=Acme Co; CN=Kubernetes Ingress Controller Fake Certificate
*  SSL certificate verify result: unable to get local issuer certificate (20), continuing anyway.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* Using Stream ID: 1 (easy handle 0x55d3ae3fb1d0)
> GET /hello HTTP/2
> Host: hello-svc-np.cloud.holisticsecurity.io
> User-Agent: curl/7.65.3
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS == 128)!
< HTTP/2 200 
< server: nginx/1.15.5
< date: Wed, 29 Jan 2020 17:45:32 GMT
< content-type: text/html; charset=utf-8
< content-length: 55
< strict-transport-security: max-age=15724800; includeSubDomains
< 
Hello version: v2, instance: hello-v2-845749f774-tft56
* Connection #0 to host hello-svc-np.cloud.holisticsecurity.io left intact

```

## Conclusions

1. NGINX Ingress Controller routes HTTP and HTTPS/TLS traffic to Hello Microservices.
2. NGINX Ingress Controller manages TLS Termination, that means that Hello Microservices don't require X.509 TLS Certificate. In other words, the NGINX Ingress Controller redirect the ingress traffic to downstream microservice over HTTP standard.
3. Hello Microservices are exposed through NGINX Ingress enabling TLS and requesting X.509 TLS Certificate. Note the `annotations` used in the `Ingress Resource` definition. Below details:

```yaml
---
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: hello-ingress-cip-tls
  annotations:
    kubernetes.io/ingress.class: "nginx"
    certmanager.k8s.io/issuer: "letsencrypt-prod"
    certmanager.k8s.io/acme-challenge-type: http01
  namespace: hello
spec:
  tls:
  - hosts:
    - ingress-nginx.cloud.holisticsecurity.io
    secretName: ingress-nginx-cloud-holisticsecurity-io-https
  rules:
  - host: ingress-nginx.cloud.holisticsecurity.io
    http:
      paths:
      - path: /
        backend:
          serviceName: hello-svc-cip
          servicePort: 5080
---
```

In the next post I'll explain how to enable Mutual TLS Authentication for Microservices.
Stay tuned.

## References

1. [JetStack Cert Manager - x.509 Certs for Kubernetes](https://github.com/jetstack/cert-manager){:target="_blank"}
2. [Part 1 - Building your own affordable K8s to host a Service Mesh](http://holisticsecurity.io/2020/01/16/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-data-plane){:target="_blank"}.
3. [Part 2 - Building your own affordable K8s - ExternalDNS and NGINX as Ingress](http://holisticsecurity.io/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-external-dns-ingress){:target="_blank"}.
