---
layout: post
title:  "Building your own affordable K8s to host a Service Mesh - Part 3: Certificate Manager"
date:   2020-01-29 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh'] 
tags: ['aws', 'docker', 'kubernetes', 'data plane', 'microservice', 'x509', 'pki']
permalink: "/2020/01/29/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part3-certificate-manager"
comments: true
---

In this blog post I'll explain how to get a X.509 TLS Certificate from [Let's Encrypt](https://letsencrypt.org){:target="_blank"} automatically during the Terraform provision time, in this way we can now invoke the services additionally on port 443 (HTTPS/TLS).  
During the Terraform execution, immediately after Kubernetes Cluster creation, the [JetStack Cert-Manager](https://github.com/jetstack/cert-manager){:target="_blank"} is deployed in a Pod, it is who will request to [Let's Encrypt](https://letsencrypt.org){:target="_blank"} service a X.509 TLS Certificate, once completed, the [JetStack Cert-Manager](https://github.com/jetstack/cert-manager){:target="_blank"} will inject the X.509 Certificate as a Kubernetes Secret into NGINX Ingress Controller to enbale TLS.

At this point you must have created a Kubernetes Cluster with ExternalDNS and NGINX as Ingress Controller. If you don't know how to achieve that, I recommend to follow these posts:

* [Part 1 - Building your own affordable K8s to host a Service Mesh](http://holisticsecurity.io/2020/01/16/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-data-plane){:target="_blank"}.
* [Part 2 - Building your own affordable K8s - ExternalDNS and NGINX as Ingress](http://holisticsecurity.io/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-external-dns-ingress){:target="_blank"}.

[![K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png "K8s Cluster created using AWS Spot Instances - Cert-Manager and Let's Encrypt")](/assets/img/20200129-affordablek8s-aws-01-arch-ingress-dns-tls-cert-manager.png){:target="_blank"}

<!-- more -->

## Steps

### 1. Cleaning everything

If you are using the Terraform scripts to create an affordable K8s Cluster I've forked and updated for you, then first of all you should clean up to start creating a new fresh K8s Cluster for following this scenario.

> If you want a cheap K8s Infrastructure on AWS, I recommend to use this GitHub repo I've updated for you:
> [https://github.com/chilcano/kubeadm-aws/tree/0.2.1-chilcano](https://github.com/chilcano/kubeadm-aws/tree/0.2.1-chilcano)


Removing existing K8s Cluster.
```sh
$ terraform destroy \
  -var cluster-name="cheapk8s" \
  -var k8s-ssh-key="ssh-key-for-us-east-1" \
  -var admin-cidr-blocks="83.50.13.174/32" \
  -var region="us-east-1" \
  -var kubernetes-version="1.14.3" \
  -var external-dns-enabled="1" \
  -var nginx-ingress-enabled="1" \
  -var nginx-ingress-domain="ingress-nginx.cloud.holisticsecurity.io" 
```

If you have destroyed the K8s Cluster with `terraform destroy`, likely you have to remove unwanted records (created for your services) in the AWS Hosted Zone.
```sh
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


### 2. Create a fresh K8s Cluster with JetStack Cert-Manager installed

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

### 3. Checking recently created K8s Cluster and JetStack Cert-Manager

**1. Exploring the JetStack Cert-Manager resources created in the K8s Cluster**

```sh
# Get SSH access to K8s master node
$ ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem

# List all namespaces
$ kubectl get ns
NAME              STATUS   AGE
cert-manager      Active   159m
default           Active   161m
ingress-nginx     Active   158m
kube-node-lease   Active   161m
kube-public       Active   161m
kube-system       Active   161m

# Listing all resources under namespace 'cert-manager'
$ kubectl get all -n cert-manager
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
[...]
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
```

### 4. Calling a Microservice over HTTPS (port 443)

Since all Microservices and RESTful API were configured to be invoked over 80/HTTP and 443/HTTPS and routed through the NGINX Ingress Controller. The only thing to do is call them through their FQDN (Fully Qualified Domain Name) and the Microservices' FQDN could be `https://ingress-nginx.cloud.holisticsecurity.io/<MICROSERVICE_NAME>`.

Then, let's try it using the `Hello Microservice`.

```sh
# Get SSH access to K8s master node
$ ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem
   
# Deploy Hello microservices, services and ingress
$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-app.yaml
$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-svc.yaml
$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-ingress.yaml
$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-ingress-tls.yaml
   
# Get status
$ kubectl get pod,svc,ingress -n hello -o wide
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
[...]
Hello version: v1, instance: hello-v1-66fc9c7d98-tljkw
* Connection #0 to host ingress-nginx.cloud.holisticsecurity.io left intact

# Calling Hello Microservices over HTTPS/TLS through `hello-ingress-np-tls`
$ curl https://hello-svc-np.cloud.holisticsecurity.io/hello -v -k
[...]
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
