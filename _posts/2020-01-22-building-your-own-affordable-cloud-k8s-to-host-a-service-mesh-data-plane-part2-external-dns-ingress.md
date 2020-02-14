---
layout: post
title:  "Building your own affordable K8s to host a Service Mesh - Part 2: External DNS and Ingress"
date:   2020-01-22 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh'] 
tags: ['aws', 'docker', 'kubernetes', 'data plane', 'microservice', 'external dns', 'ingress', 'nginx']
permalink: "/2020/01/22/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-external-dns-ingress"
comments: true
---
In order to get an affordable Kubernetes, every part we're going to use should be affordable too, and ones of the expensive and tricky things are the [AWS Elastic Load Balancing (ELB)](https://aws.amazon.com/elasticloadbalancing){:target="_blank"} and the [AWS Route 53 (DNS)](https://aws.amazon.com/route53){:target="_blank"}. Fortunately, Kubernetes SIGs are working to address this gap with the [Kubernetes ExternalDNS](https://github.com/kubernetes-sigs/external-dns){:target="_blank"}.

**But what is the problem?**

Apart of it is expensive, the problem is every time I deploy a `Service` in Kubernetes I have to update and add a new DNS entry in the Cloud Provider's DNS manually. Yes, of course, the process can be automated, but the idea is doing it during the provisioning time. In other words, every developer can publish theirs services adding the DNS name as annotation for that services can be called over Internet.
Yes, [Kubernetes brings by default a DNS](https://github.com/kubernetes/dns){:target="_blank"} but this is an internal one and it is only to work resolving DNS names over the Kubernetes Network, not for internet facing services.

**The Solution**

The Kubernetes ExternalDNS will run a program in our affordable K8s which it will synchronize exposed Kubernetes Services and Ingresses with the Cloud Provider's DNS Service, in this case with AWS Route 53. Below you can view a high level diagram and current status of my [Affordable Kubernetes Data Plane, I recommend look at first post about it](http://holisticsecurity.io/2020/01/16/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-data-plane){:target="_blank"}.

[![Service Mesh hosted using AWS Spot Instances](/assets/img/20200122-service-mesh-01-affordablek8s-aws-arch.png "Service Mesh using AWS Spot Instances")](/assets/img/20200122-service-mesh-01-affordablek8s-aws-arch.png){:target="_blank"}

<!-- more -->

Then, let's do it.

## Steps

### 1. Create a Hosted Zone in AWS Route 53

I'm going to register the subdomain `cloud.holisticsecurity.io` of existing Root domain name `holisticsecurity.io` into AWS Route 53. I'll follow the below AWS Route 53 explanation.

* [Using Amazon Route 53 as the DNS Service for Subdomains Without Migrating the Parent Domain](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/creating-migrating.html){:target="_blank"}

You can create subdomain records using either the Amazon Route 53 console or the Route 53 API. Since I have already `AWS CLI` configured in my PC, then let's use it.

**1) Create a Hosted Zone in AWS 53 for the Subdomain**

```sh
# Create a DNS zone which will contain the managed DNS records.
$ aws route53 create-hosted-zone --name "cloud.holisticsecurity.io." --caller-reference "cloud-holosec-io-$(date +%s)" --hosted-zone-config "Comment='HostedZone for subdomain',PrivateZone=false"

# Get the Hosted Zone ID (HZ_ID) of the hosted zone I just created, which will serve as the value for my-hostedzone-identifier.
$ export HZ_ID=$(aws route53 list-hosted-zones-by-name --output json --dns-name "cloud.holisticsecurity.io." | jq -r '.HostedZones[0].Id')

# Make a note of the nameservers that were assigned to my new zone.
$ aws route53 list-resource-record-sets --output json --hosted-zone-id "${HZ_ID}" --query "ResourceRecordSets[?Type == 'NS']" | jq -r '.[0].ResourceRecords[].Value'
ns-1954.awsdns-52.co.uk.
ns-157.awsdns-19.com.
ns-1053.awsdns-03.org.
ns-789.awsdns-34.net.
```

**2) Add `Name Server Records` for the specified Subdomain in the DNS Service Provider Console**

 After changes to Amazon Route 53 records have propagated, the next step is to update the DNS service for the parent domain by adding `NS` type records for the specified subdomain. This is known as delegating responsibility for the subdomain to Route 53. 

I will need the above four nameserver that I got querying with `AWSCLI`. Note that those nameservers are for my subdomain, likely you got others.

```
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

1. Clone the Affordable K8s Cluster Github repo

   > If you want a cheap K8s Infrastructure on AWS, I recommend to clone this GitHub repo I've updated for you.
   >  
   > [https://github.com/chilcano/kubeadm-aws/tree/0.2.1-chilcano](https://github.com/chilcano/kubeadm-aws/tree/0.2.1-chilcano){:target="_blank"}
   >  
   
   Once cloned, first of all run `terraform destroy` to remove all AWS resources provisioned previously. TThat will avoid increasing your bill.
   After cleaning up, reprovision a fresh Kubernetes Cluster.
   
   ```sh
   $ terraform plan \
     -var cluster-name="cheapk8s" \
     -var k8s-ssh-key="ssh-key-for-us-east-1" \
     -var admin-cidr-blocks="83.50.9.220/32" \
     -var region="us-east-1" \
     -var kubernetes-version="1.14.3" \
     -var external-dns-enabled="1" \
     -var nginx-ingress-enabled="1" \
     -var nginx-ingress-domain="ingress-nginx.cloud.holisticsecurity.io" 
   
   $ terraform apply \
     -var cluster-name="cheapk8s" \
     -var k8s-ssh-key="ssh-key-for-us-east-1" \
     -var admin-cidr-blocks="83.50.9.220/32" \
     -var region="us-east-1" \
     -var kubernetes-version="1.14.3" \
     -var external-dns-enabled="1" \
     -var nginx-ingress-enabled="1" \
     -var nginx-ingress-domain="ingress-nginx.cloud.holisticsecurity.io" 
   ```

2. Clean up unwanted Name Server Records under the AWS Route 53 Hosted Zone for the specified Subdomain.

   If you have been playing with AWS Route 53 Hosted Zone for the specified Subdomain (`cloud.holisticsecurity.io`), it's likely you have added records and require removing them before creating fresh records. Then, below I explain you how to do:
   
   ```sh
   # A fresh AWS Route 53 Hosted Zone has 2 records: Record Type NS and Record Type SOA.
   $ export MY_SUBDOMAIN="cloud.holisticsecurity.io"
   $ export HZ_ID=$(aws route53 list-hosted-zones-by-name --dns-name "${MY_SUBDOMAIN}." | jq -r '.HostedZones[0].Id')
   $ aws route53 list-resource-record-sets --hosted-zone-id $HZ_ID --query "ResourceRecordSets[?Name == '${MY_SUBDOMAIN}.'].{Name:Name,Type:Type}" | jq -c '.[]'
   {"Name":"cloud.holisticsecurity.io.","Type":"NS"}
   {"Name":"cloud.holisticsecurity.io.","Type":"SOA"}
   
   # I should remove those 10 records (of type A, TXT and SRV) 
   $ aws route53 list-resource-record-sets --hosted-zone-id $HZ_ID --query "ResourceRecordSets[?Name != '${MY_SUBDOMAIN}.'].{Name:Name,Type:Type}" | jq -c '.[]'
   {"Name":"hello-svc-np.cloud.holisticsecurity.io.","Type":"A"}
   {"Name":"hello-svc-np.cloud.holisticsecurity.io.","Type":"TXT"}
   {"Name":"_http._tcp.hello-svc-np.cloud.holisticsecurity.io.","Type":"SRV"}
   {"Name":"_http._tcp.hello-svc-np.cloud.holisticsecurity.io.","Type":"TXT"}
   {"Name":"ingress-nginx.cloud.holisticsecurity.io.","Type":"A"}
   {"Name":"ingress-nginx.cloud.holisticsecurity.io.","Type":"TXT"}
   {"Name":"_http._tcp.ingress-nginx.cloud.holisticsecurity.io.","Type":"SRV"}
   {"Name":"_http._tcp.ingress-nginx.cloud.holisticsecurity.io.","Type":"TXT"}
   {"Name":"_https._tcp.ingress-nginx.cloud.holisticsecurity.io.","Type":"SRV"}
   {"Name":"_https._tcp.ingress-nginx.cloud.holisticsecurity.io.","Type":"TXT"}
   
   # Removing those unwanted records.
   $ aws route53 list-resource-record-sets --hosted-zone-id $HZ_ID --query "ResourceRecordSets[?Name != '${MY_SUBDOMAIN}.']" | jq -c '.[]' |
     while read -r RRS; do
       read -r name type <<< $(jq -jr '.Name, " ", .Type' <<< "$RRS") 
       CHG_ID=$(aws route53 change-resource-record-sets --hosted-zone-id $HZ_ID --change-batch '{"Changes":[{"Action":"DELETE","ResourceRecordSet": '"$RRS"' }]}' --output text --query 'ChangeInfo.Id')
       echo " - DELETING: $type $name - CHANGE ID: $CHG_ID"    
     done
   
    - DELETING: TXT ccc.cloud.holisticsecurity.io. - CHANGE ID: /change/CMCJ8CXRBIZ7M
    - DELETING: SRV ddd.cloud.holisticsecurity.io. - CHANGE ID: /change/C2KU4TEHWEDV2Y
   ```
   
   Only if it is required, you can delete the AWS Hosted Zone in this way:
   ```sh
   $ aws route53 delete-hosted-zone --id $HZ_ID --output text --query 'ChangeInfo.Id'
   ```

**2) Verify ExternalDNS has synchronized the Ingress' subdomain with AWS Route 53**

The domain name that the Ingress' subdomain will request is `ingress-nginx.cloud.holisticsecurity.io`, that domain name has been created during the Affordable K8s Cluster creation. Then, let's check it.

```sh
$ export MY_SUBDOMAIN="cloud.holisticsecurity.io"
$ export INGRESS_NS="ingress-nginx.${MY_SUBDOMAIN}"

# Get the Hosted Zone (HZ_ID) ID of the hosted zone I just created.
$ export HZ_ID=$(aws route53 list-hosted-zones-by-name --dns-name "${MY_SUBDOMAIN}." | jq -r '.HostedZones[0].Id')

# Get all nameservers that were assigned initially and recently synchronized by ExternalDNS to my new zone.
$ aws route53 list-resource-record-sets --output text --hosted-zone-id "${HZ_ID}" --query "ResourceRecordSets[?Name == '${INGRESS_NS}.'].{Name:Name,Type:Type}"

ingress-nginx.cloud.holisticsecurity.io.	A
ingress-nginx.cloud.holisticsecurity.io.	TXT
```

Or if you are of the old-school, you can ask to any of four AWS Route 53's DNS server if the subdomain has been created and updated.

```sh
$ dig +short @ns-1954.awsdns-52.co.uk. ingress-nginx.cloud.holisticsecurity.io.
174.129.123.159
54.159.75.179

$ dig +short @ns-157.awsdns-19.com. ingress-nginx.cloud.holisticsecurity.io.
174.129.123.159
54.159.75.179

$ dig +short @ns-1053.awsdns-03.org. ingress-nginx.cloud.holisticsecurity.io.
174.129.123.159
54.159.75.179

$ dig +short @ns-789.awsdns-34.net. ingress-nginx.cloud.holisticsecurity.io.
174.129.123.159
54.159.75.179
```

Both above IP addresses are the `IPv4 Public IP` addresses assigned to Kubernetes Master Node and Kubernetes Worker Node. If I add a new Node to existing Kubernetes Cluster, the `NGINX Ingress Controller` will be installed in the new Node and its new `IPv4 Public IP` address will resolve to `ingress-nginx.cloud.holisticsecurity.io`, that is why the `NGINX Ingress Controller` was deployed into Kubernetes as a [`DaemonSet`](https://kubernetes.io/docs/concepts/workloads/controllers/daemonset/){:target="_blank"}. Let's to verify it.


```sh
# Get SSH access to K8s master node
$ ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem

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


Since the `CheapK8s` only exposes RESTful services over `80` and `443` ports, then to verify that I need to call the `Health Check` service of my [NGINX Ingress Controller](https://github.com/chilcano/kubeadm-aws/blob/0.2.1-chilcano/manifests/nginx-ingress-mandatory.yaml){:target="_blank"} deployed through Terraform in previous step. This procedure also verify that the `NGINX Ingress Controller` has got a DNS name (subdomain `ingress-nginx.cloud.holisticsecurity.io`) from `ExternalDNS` successfully. This part has been configured in the file [`manifests/nginx-ingress-nodeport.yaml.tmpl`](https://github.com/chilcano/kubeadm-aws/blob/0.2.1-chilcano/manifests/nginx-ingress-nodeport.yaml.tmpl){:target="_blank"}.


```sh
$ curl -X GET http://ingress-nginx.cloud.holisticsecurity.io/healthz -v

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

**4) Verify ExternalDNS and NGINX Ingress work together (Service example)**


1. Deploy Hello Microservice and check the deployment status

   ```sh
   # Get SSH access to K8s master node
   $ ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem
   
   # Deploy Hello microservices
   ubuntu@ip-10-0-100-4:~$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-app.yaml
   namespace/hello created
   serviceaccount/hello-sa created
   deployment.extensions/hello-v1 created
   deployment.extensions/hello-v2 created
   
   # Create ClusterIP, LoadBalancer and NodePort Services for above Hello microservices
   ubuntu@ip-10-0-100-4:~$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-svc.yaml
   service/hello-svc-cip created
   service/hello-svc-lb created
   service/hello-svc-np created
   
   # Create 2 Ingress Resources for above ClusterIP and NodePort Services
   ubuntu@ip-10-0-100-4:~$ kubectl apply -f https://raw.githubusercontent.com/chilcano/kubeadm-aws/0.2.1-chilcano/examples/hello-cheapk8s-ingress.yaml
   ingress.extensions/hello-ingress-cip created
   ingress.extensions/hello-ingress-np created
   
   # Get status
   ubuntu@ip-10-0-100-4:~$ kubectl get pod,svc,ingress -n hello -o wide
   NAME                            READY   STATUS    RESTARTS   AGE   IP            NODE                          NOMINATED NODE   READINESS GATES
   pod/hello-v1-66fc9c7d98-7b4b5   1/1     Running   0          32m   10.244.1.16   ip-10-0-100-22.ec2.internal   <none>           <none>
   pod/hello-v1-66fc9c7d98-kb2kn   1/1     Running   0          32m   10.244.1.17   ip-10-0-100-22.ec2.internal   <none>           <none>
   pod/hello-v2-845749f774-fzg5f   1/1     Running   0          32m   10.244.1.18   ip-10-0-100-22.ec2.internal   <none>           <none>
   pod/hello-v2-845749f774-q9bk5   1/1     Running   0          31m   10.244.1.19   ip-10-0-100-22.ec2.internal   <none>           <none>
   
   NAME                    TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE   SELECTOR
   service/hello-svc-cip   ClusterIP      10.108.175.6    <none>        5080/TCP         22m   app=hello
   service/hello-svc-lb    LoadBalancer   10.102.10.180   <pending>     5080:32379/TCP   22m   app=hello
   service/hello-svc-np    NodePort       10.105.22.106   <none>        5080:31002/TCP   22m   app=hello
   
   NAME                                   HOSTS                                     ADDRESS   PORTS   AGE
   ingress.extensions/hello-ingress-cip   ingress-nginx.cloud.holisticsecurity.io             80      17m
   ingress.extensions/hello-ingress-np    hello-svc-np.cloud.holisticsecurity.io              80      17m
   ```

2. Understanding how works microservice exposition and how they should be called

   Since the `ExternalDNS` and `NGINX Ingress Controller` have been configured in the `CheapK8s` Cluster, the only way to call the [Hello Microservices](https://github.com/chilcano/kubeadm-aws/blob/0.2.1-chilcano/examples/hello-cheapk8s-app.yaml){:target="_blank"} is through their [`Ingress Resources`](https://github.com/chilcano/kubeadm-aws/blob/0.2.1-chilcano/examples/hello-cheapk8s-ingress.yaml){:target="_blank"} and their [`Services`](https://github.com/chilcano/kubeadm-aws/blob/0.2.1-chilcano/examples/hello-cheapk8s-svc.yaml){:target="_blank"}.
   
   It is very important to understand how Kubernetes exposes our microservices. Next, I copy some concepts (Kubernetes' primitives) and references to understand the whole operation.
   
   >  
   > * `ClusterIP`: Exposes the Service on a cluster-internal IP. Choosing this value makes the Service only reachable from within the cluster. This is the default `ServiceType`.
   > * `LoadBalancer`: Exposes the Service externally using a cloud provider’s load balancer. `NodePort` and `ClusterIP` Services, to which the external load balancer routes, are automatically created.
   > * `NodePort`: Exposes the Service on each Node’s IP at a static port (the `NodePort`). A `ClusterIP` Service, to which the `NodePort` Service routes, is automatically created. You’ll be able to contact the `NodePort` Service, from outside the cluster, by requesting `<NodeIP>:<NodePort>`.
   >  
   > Info: [Kubernetes - Publishing Services (ServiceTypes)](https://kubernetes.io/docs/concepts/services-networking/service/#publishing-services-service-types){:target="_blank"}
   >  
   
   And this is my favorite one.
   > 
   > [The Hardest Part of Microservices: Calling Your Services by Christian Posta, 2017/April/25](https://blog.christianposta.com/microservices/the-hardest-part-of-microservices-calling-your-services){:target="_blank"}
   >  
   
3. Calling Hello Microservices

   
   Calling through Services (ClusterIP, LoadBalancer and NodePort) from inside of Kubernetes Cluster. Although below I'm using `ClusterIP`, you can repeat similar process using the `LoadBalancer` and `NodePort`.
   
   ```sh
   $ kubectl get svc/hello-svc-cip -o jsonpath='{.spec.clusterIP}'
   $ kubectl get svc/hello-svc-cip -o jsonpath='{.spec.ports[0].port}'
   $ export HELLO_SVC_CIP=$(kubectl get svc/hello-svc-cip -n hello -o jsonpath='{.spec.clusterIP}'):$(kubectl get svc/hello-svc-cip -n hello -o jsonpath='{.spec.ports[0].port}')
   $ echo $HELLO_SVC_CIP
   
   $ curl http://${HELLO_SVC_CIP}/hello
   Hello version: v1, instance: hello-v1-5cb886df9d-k7rcq
   
   $ curl http://${HELLO_SVC_CIP}/hello
   Hello version: v2, instance: hello-v2-6c7fbbb654-kq6sq
   
   $ curl http://${HELLO_SVC_CIP}/hello
   Hello version: v1, instance: hello-v1-5cb886df9d-k7rcq
   
   $ kubectl logs -f -l app=hello -n hello
    * Running on http://0.0.0.0:5000/ (Press CTRL+C to quit)
    * Running on http://0.0.0.0:5000/ (Press CTRL+C to quit)
   10.244.0.0 - - [22/Jan/2020 11:20:45] "GET /hello HTTP/1.1" 200 -
   10.244.0.0 - - [22/Jan/2020 11:22:27] "GET /hello HTTP/1.1" 200 -
   10.244.0.0 - - [22/Jan/2020 11:22:33] "GET /hello HTTP/1.1" 200 -
   ```
   
   Calling from Internet through Kubernetes Ingress Controller and its Fully Qualified Domain Name (`FQDN`).
   
   ```sh
   $ curl http://ingress-nginx.cloud.holisticsecurity.io/hello
   Hello version: v2, instance: hello-v2-845749f774-q9bk5
   
   $ curl http://hello-svc-np.cloud.holisticsecurity.io/hello
   Hello version: v1, instance: hello-v1-66fc9c7d98-7b4b5
   ```

## References

1. [Kubernetes SIGs ExternalDNS's github repo](https://github.com/kubernetes-sigs/external-dns){:target="_blank"}
2. [The missing piece - Kubernetes ExternalDNS by Lachlan Evenson, 2017/Aug/09](https://www.youtube.com/watch?v=9HQ2XgL9YVI){:target="_blank"}
3. [The NGINX Ingress Controller](https://github.com/kubernetes/ingress-nginx){:target="_blank"}
4. [Kubernetes concepts - Service](https://kubernetes.io/docs/concepts/services-networking/service/){:target="_blank"}
5. [Kubernetes concepts - Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/){:target="_blank"}
6. [The Hardest Part of Microservices: Calling Your Services by Christian Posta, 2017/April/25](https://blog.christianposta.com/microservices/the-hardest-part-of-microservices-calling-your-services){:target="_blank"}

In the next blog post I'll explain how to generate TLS Certificates for your Microservices.
Stay tuned.