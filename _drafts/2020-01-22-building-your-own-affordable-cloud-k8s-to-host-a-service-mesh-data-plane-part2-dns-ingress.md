---
layout: post
title:  "Building your own affordable Cloud (K8s) to host a Service Mesh - Part2 DNS to Ingress"
date:   2020-01-22 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh'] 
tags: ['aws', 'docker', 'kubernetes', 'data plane', 'dns', 'ingress']
permalink: "/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-dns-ingress"
comments: true
---
In order to get an affordable Kubernetes, every part we're going to use should be affordable too, and ones of the expensive and tricky things are the [AWS Elastic Load Balancing](https://aws.amazon.com/elasticloadbalancing){:target="_blank"} (ELB) and the [AWS Route 53](https://aws.amazon.com/route53){:target="_blank"} (DNS). Fortunately, Kubernetes SIGs are working to address this gap with the [Kubernetes ExternalDNS](https://github.com/kubernetes-sigs/external-dns){:target="_blank"}.

**But what is the problem?**

Apart of it is expensive, the problem is every time I deploy a `Service` in Kubernetes I have to update and add a new DNS entry in the Cloud Provider's DNS manually. Yes, of course, the process can be automated, but the idea is doing it during the provisioning time. In other words, every developer can publish theirs services adding the DNS name as annotation for that services can be called over Internet.
Yes, [Kubernetes brings by default a DNS](https://github.com/kubernetes/dns){:target="_blank"} but this is an internal one and it is only to work resolving DNS names over the Kubernetes Network, not for internet facing services.

**The Solution**

The Kubernetes ExternalDNS will run a program in our affordable K8s wich it will synchronize exposed Kubernetes Services and Ingresses with the Cloud Provider's DNS Service, in this case with AWS Route 53. Below you can view a high level diagram and current status of my [Affordable Kubernetes Data Plane, I recommend look at first post about it](http://holisticsecurity.io/2020/01/16/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-data-plane){:target="_blank"}.

[![Service Mesh hosted using AWS Spot Instances](/assets/img/20200122-service-mesh-01-affordableK8s-aws-arch.png "Service Mesh using AWS Spot Instances")](/assets/img/20200122-service-mesh-01-affordableK8s-aws-arch){:target="_blank"}

<!-- more -->

Then, let's do it.

## Steps

### 1. Create a Hosted Zone in AWS Route 53

I'm going to register the subdomain `cloud.holisticsecurity.io` of existing Root domain name `holisticsecurity.io` into AWS Route 53. I'll follow the below AWS Route 53 explanation.

* [Using Amazon Route 53 as the DNS Service for Subdomains Without Migrating the Parent Domain](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/creating-migrating.html){:target="_blank"}

You can create subdomain records using either the Amazon Route 53 console or the Route 53 API. Since I have already `AWS CLI` configured, then let's use it.

**1) Create a Hosted Zone in AWS 53 for the Subdomain**

```sh
# Create a DNS zone which will contain the managed DNS records.
chilcano@inti:~/git-repos/affordable-k8s-tf$ aws route53 create-hosted-zone \
  --name "cloud.holisticsecurity.io." \
  --caller-reference "cloud-holosec-io-$(date +%s)" \
  --hosted-zone-config "Comment='HostedZone for subdomain',PrivateZone=false"

# Make a note of the ID of the hosted zone I just created, which will serve as the value for my-hostedzone-identifier.
chilcano@inti:~/git-repos/affordable-k8s-tf$ aws route53 list-hosted-zones-by-name \
  --output json \
  --dns-name "cloud.holisticsecurity.io." | jq -r '.HostedZones[0].Id'

/hostedzone/Z3O9PQMEP4619Y

# Make a note of the nameservers that were assigned to my new zone.
chilcano@inti:~/git-repos/affordable-k8s-tf$ aws route53 list-resource-record-sets \
  --output json --hosted-zone-id "/hostedzone/Z3O9PQMEP4619Y" \
  --query "ResourceRecordSets[?Type == 'NS']" | jq -r '.[0].ResourceRecords[].Value'

ns-1954.awsdns-52.co.uk.
ns-157.awsdns-19.com.
ns-1053.awsdns-03.org.
ns-789.awsdns-34.net.
```

**2) Update the DNS Service with Name Server Records for the Subdomain**

After changes to Amazon Route 53 records have propagated, the next step is to update the DNS service for the parent domain by adding `NS` type records for the subdomain. This is known as delegating responsibility for the subdomain to Route 53. 

I will need the above four nameserver that I got querying with `AWSCLI`. Note that those nameservers are for my subdomain, likely you got others.

```sh
ns-1954.awsdns-52.co.uk.
ns-157.awsdns-19.com.
ns-1053.awsdns-03.org.
ns-789.awsdns-34.net.
```
Finally, for my subdomain `cloud.holisticsecurity.io`, you should have as shown below:

```
[...]
cloud 1800 IN NS ns-1053.awsdns-03.org.
cloud 1800 IN NS ns-157.awsdns-19.com.
cloud 1800 IN NS ns-1954.awsdns-52.co.uk.
cloud 1800 IN NS ns-789.awsdns-34.net.
[...]
```

Ah, also you should wait some minutes or hours to propagate these changes. That depends on your DNS Service Provider.


### 2. Provision of Kubernetes Cluster with ExternalDNS through Terraform


If you have read the first post about how to create an affordable Kubernetes Data Plane, then you will know that I used Terraform to provision it. I'm using the [Really cheap Kubernetes cluster on AWS with kubeadm](https://github.com/cablespaghetti/kubeadm-aws){:target="_blank"} Guide's [Sam Weston](https://cablespaghetti.github.io){:target="_blank"} which already uses Kubernetes ExternalDNS, then I'm going to re-apply the Terraform scripts activating the installation of ExternalDNS.


**1) Create a fresh affordable Kubernetes Cluster**

>
> If you want a cheap K8s Infrastructure on AWS, I recommend to clone this GitHub repo I've updated for you.
>  
> [https://github.com/chilcano/kubeadm-aws/tree/0.2.1-chilcano](https://github.com/chilcano/kubeadm-aws/tree/0.2.1-chilcano){:target="_blank"}
>  

Once cloned, first of all run `terraform destroy .....` to remove all AWS resources provisioned previously. TThat will avoid increasing your bill.
After cleaning up, re aprovision a fresh Kubernetes Cluster.

```sh
chilcano@inti:~/git-repos/affordable-k8s-tf$ terraform plan \
  -var cluster-name="cheapk8s" \
  -var k8s-ssh-key="ssh-key-for-us-east-1" \
  -var admin-cidr-blocks="83.50.9.220/32" \
  -var region="us-east-1" \
  -var kubernetes-version="1.14.3" \
  -var external-dns-enabled="1" \
  -var nginx-ingress-enabled="1" \
  -var nginx-ingress-domain="ingress-nginx.cloud.holisticsecurity.io" 

chilcano@inti:~/git-repos/affordable-k8s-tf$ terraform apply \
  -var cluster-name="cheapk8s" \
  -var k8s-ssh-key="ssh-key-for-us-east-1" \
  -var admin-cidr-blocks="83.50.9.220/32" \
  -var region="us-east-1" \
  -var kubernetes-version="1.14.3" \
  -var external-dns-enabled="1" \
  -var nginx-ingress-enabled="1" \
  -var nginx-ingress-domain="ingress-nginx.cloud.holisticsecurity.io" 
```


**2) Verify ExternalDNS has synchronized subdomain in AWS Route 53**

Get the Hosted Zone ID of the hosted zone above I just created.
```sh
chilcano@inti:~/git-repos/affordable-k8s-tf$ aws route53 list-hosted-zones-by-name \
  --output json \
  --dns-name "cloud.holisticsecurity.io." | jq -r '.HostedZones[0].Id'

/hostedzone/Z3O9PQMEP4619Y
```

Get all nameservers that were assigned initially and recently synchronized by ExternalDNS to my new zone.
```sh
chilcano@inti:~/git-repos/affordable-k8s-tf$ aws route53 list-resource-record-sets \
  --output json --hosted-zone-id "/hostedzone/Z3O9PQMEP4619Y" \
  --query "ResourceRecordSets[?Type == 'A']" | jq -r '.[0].ResourceRecords[].Value'


chilcano@inti:~/git-repos/affordable-k8s-tf$ aws route53 list-resource-record-sets \
  --output json --hosted-zone-id "/hostedzone/Z3O9PQMEP4619Y" \
  --query "ResourceRecordSets[?Name == 'ingress-nginx.cloud.holisticsecurity.io.'].{Name:Name,Type:Type,ResourceRecords:ResourceRecords}" 

[
    {
        "Name": "ingress-nginx.cloud.holisticsecurity.io.",
        "Type": "A",
        "ResourceRecords": [
            {
                "Value": "174.129.123.159"
            },
            {
                "Value": "54.159.75.179"
            }
        ]
    },
    {
        "Name": "ingress-nginx.cloud.holisticsecurity.io.",
        "Type": "TXT",
        "ResourceRecords": [
            {
                "Value": "\"heritage=external-dns,external-dns/owner=k8s,external-dns/resource=service/ingress-nginx/ingress-nginx\""
            }
        ]
    }
]
```

Or if you are of the old-school, you can ask to any of four AWS Route 53's DNS server if the subdomain has been created and updated.
```sh
chilcano@inti:~/git-repos/affordable-k8s-tf$ dig +short @ns-1954.awsdns-52.co.uk. ingress-nginx.cloud.holisticsecurity.io.
174.129.123.159
54.159.75.179

chilcano@inti:~/git-repos/affordable-k8s-tf$ dig +short @ns-157.awsdns-19.com. ingress-nginx.cloud.holisticsecurity.io.
174.129.123.159
54.159.75.179

chilcano@inti:~/git-repos/affordable-k8s-tf$ dig +short @ns-1053.awsdns-03.org. ingress-nginx.cloud.holisticsecurity.io.
174.129.123.159
54.159.75.179

chilcano@inti:~/git-repos/affordable-k8s-tf$ dig +short @ns-789.awsdns-34.net. ingress-nginx.cloud.holisticsecurity.io.
174.129.123.159
54.159.75.179
```

Both above IP addresses are the `IPv4 Public IP` addresses assigned to Kubernetes Master Node and Kubernetes Worker Node. If I add a new Node to existing Kubernetes Cluster, the `NGINX Ingress Controller` will be installed in the new Node and its new `IPv4 Public IP` address will resolve to `ingress-nginx.cloud.holisticsecurity.io`, that is why the `NGINX Ingress Controller` was deployed into Kubernetes as a [`DaemonSet`](https://kubernetes.io/docs/concepts/workloads/controllers/daemonset/){:target="_blank"}. Let's to verify it.


```sh
chilcano@inti:~/git-repos/affordable-k8s-tf$ ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem

ubuntu@ip-10-0-100-4:~$ kubectl get daemonset -n ingress-nginx
NAME                       DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR   AGE
nginx-ingress-controller   2         2         2       2            2           <none>          14h


ubuntu@ip-10-0-100-4:~$ kubectl get pods -n ingress-nginx -o wide
NAME                                    READY   STATUS    RESTARTS   AGE   IP            NODE                          NOMINATED NODE   READINESS GATES
default-http-backend-5c9bb94849-pf5pj   1/1     Running   0          14h   10.244.1.3    ip-10-0-100-22.ec2.internal   <none>           <none>
nginx-ingress-controller-bwhdp          1/1     Running   0          14h   10.0.100.22   ip-10-0-100-22.ec2.internal   <none>           <none>
nginx-ingress-controller-q4bgh          1/1     Running   0          14h   10.0.100.4    ip-10-0-100-4.ec2.internal    <none>           <none>
```


**3) Verify ExternalDNS and NGINX Ingress work together (Health Check example)**


Since the `CheapK8s` only exposes RESTful services over `80` and `443` ports, then to verify I need to call the `Health Check` service of my [NGINX Ingress Controller](https://github.com/chilcano/kubeadm-aws/blob/0.2.1-chilcano/manifests/nginx-ingress-mandatory.yaml){:target="_blank"} deployed through Terraform in previous step.


```sh
chilcano@inti:~/git-repos/affordable-k8s-tf$ curl -X GET http://ingress-nginx.cloud.holisticsecurity.io/healthz -v
Note: Unnecessary use of -X or --request, GET is already inferred.
*   Trying 174.129.123.159:80...
* TCP_NODELAY set
* Connected to ingress-nginx.cloud.holisticsecurity.io (174.129.123.159) port 80 (#0)
> GET /healthz HTTP/1.1
> Host: ingress-nginx.cloud.holisticsecurity.io
> User-Agent: curl/7.65.3
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 OK
< Server: nginx/1.15.5
< Date: Tue, 21 Jan 2020 21:24:45 GMT
< Content-Type: text/html
< Content-Length: 0
< Connection: keep-alive
< 
* Connection #0 to host ingress-nginx.cloud.holisticsecurity.io left intact
```


**4) Verify ExternalDNS and NGINX Ingress work together (Ingress resource example)**

xxxx

**5) Verify ExternalDNS and NGINX Ingress work together (Service example)**










**1. Deploy microservices**

You can clone the Hello Microservices available in the [Service Mesh Workshop](https://github.com/chilcano/service-mesh-workshop){:target="_blank"} GitHub repo I published many months ago.

```sh
$ git clone https://github.com/chilcano/service-mesh-workshop
$ cd $PWD/service-mesh-workshop/labs
$ kubectl apply -f 01-delivering-on-k8s/hello-app.yaml 
```

Or just deploy the [`hello-app.yaml` Kubernetes Deployment file](https://raw.githubusercontent.com/chilcano/service-mesh-workshop/master/labs/01-delivering-on-k8s/hello-app.yaml){:target="_blank"}.

```sh
$ kubectl apply -f https://raw.githubusercontent.com/chilcano/service-mesh-workshop/master/labs/01-delivering-on-k8s/hello-app.yaml
```

**2. Check microservices deployment**

 Once completed the deployment, check if the microservices were deployed successfully.

```sh
$ kubectl get pods,svc -n hello -o wide
NAME                            READY   STATUS    RESTARTS   AGE   IP           NODE                           NOMINATED NODE   READINESS GATES
pod/hello-v1-5cb886df9d-nvb9j   1/1     Running   0          21s   10.244.1.4   ip-10-0-100-154.ec2.internal   <none>           <none>
pod/hello-v2-6c7fbbb654-hdqfr   1/1     Running   0          21s   10.244.1.5   ip-10-0-100-154.ec2.internal   <none>           <none>

NAME                    TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE   SELECTOR
service/hello-svc-cip   ClusterIP      10.104.114.129   <none>        5010/TCP         20s   app=hello
service/hello-svc-lb    LoadBalancer   10.98.9.14       <pending>     5020:31653/TCP   18s   app=hello
service/hello-svc-np    NodePort       10.109.14.191    <none>        5030:30796/TCP   17s   app=hello
```

### Call the Hello Microservices


**1. Calling Hello Microservice through ClusterIP SVC**

```sh
$ kubectl get svc/hello-svc-cip -o jsonpath='{.spec.clusterIP}'
$ kubectl get svc/hello-svc-cip -o jsonpath='{.spec.ports[0].port}'

$ export HELLO_SVC_CIP=$(kubectl get svc/hello-svc-cip -n hello -o jsonpath='{.spec.clusterIP}'):$(kubectl get svc/hello-svc-cip -n hello -o jsonpath='{.spec.ports[0].port}')
$ curl http://${HELLO_SVC_CIP}/hello
```

**2. Calling Hello Microservice through LoadBalancer SVC**

```sh
$ export HELLO_SVC_LB=$(ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem -- kubectl get svc hello-svc-lb -n hello -o jsonpath='{.spec.ports[0].nodePort}')
$ curl -s http://$(terraform output master_dns):${HELLO_SVC_LB}/hello
```

**3. Calling Hello Microservice through NodePort SVC**

```sh
$ export HELLO_SVC_NP=$(ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem -- kubectl get svc hello-svc-np -n hello -o jsonpath='{.spec.ports[0].nodePort}')
$ curl -s http://$(terraform output master_dns):${HELLO_SVC_NP}/hello
```


## References

1. [Kubernetes SIGs ExternalDNS's github repo](https://github.com/kubernetes-sigs/external-dns){:target="_blank"}
2. [The missing piece - Kubernetes ExternalDNS by Lachlan Evenson, 9 Aug 2017](https://www.youtube.com/watch?v=9HQ2XgL9YVI){:target="_blank"}
3. [The NGINX Ingress Controller](https://github.com/kubernetes/ingress-nginx){:target="_blank"}
4. [Kubernetes concepts - Service](https://kubernetes.io/docs/concepts/services-networking/service/){:target="_blank"}
5. [Kubernetes concepts - Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/){:target="_blank"}

