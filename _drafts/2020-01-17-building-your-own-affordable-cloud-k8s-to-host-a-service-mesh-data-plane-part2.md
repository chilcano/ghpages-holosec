---
layout: post
title:  "Building your own affordable Cloud (K8s) to host a Service Mesh - Part2 Ingress"
date:   2020-01-16 10:00:00 +0100
categories: ['cloud', 'apaas', 'service mesh'] 
tags: ['aws', 'gcp', 'azure', 'docker', 'kubernetes', 'data plane', 'ingress']
permalink: "/2020/01/16/building-your-own-affordable-cloud-k8s-to-host-a-service-mesh-part2-ingress"
comments: true
---

## Deployment of Hello Microservices on a cheap K8s hosted on AWS


**Check the K8s Cluster status**

```sh
chilcano@inti:~/git-repos/affordable-k8s-tf$ ssh ubuntu@$(terraform output master_dns) -i ~/Downloads/ssh-key-for-us-east-1.pem -- kubectl get nodes

NAME                           STATUS   ROLES    AGE   VERSION
ip-10-0-100-154.ec2.internal   Ready    <none>   47h   v1.14.3
ip-10-0-100-4.ec2.internal     Ready    master   47h   v1.14.3
```


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

