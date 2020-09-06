---
layout:     post
title:      "A Cloud IDE for the masses - AWS CDK implementation"
categories: ['cloud', 'devops'] 
tags:       ['aws', 'cdk', 'docker', 'git', 'linux', 'cloudformation']
date:       2020-09-06 11:00:00 +0100
permalink:  "/2020/09/06/a-cloud-ide-for-the-masses"
comments:   true
---

As I stated in the previous post "[A Cloud IDE for the masses](/2020/09/06/a-cloud-ide-for-the-masses)", in this post I will explain you how to deploy [Code-Server](https://github.com/cdr/code-server) on AWS. But to make it more interesting, I'm going to use [AWS Cloud Development Kit (AWS CDK)](https://aws.amazon.com/cdk/), it is a framework to model and provision your cloud applications and resources using knew programming language (TypeScript, Python, Java, etc.), no more YAML or Json.

We are going to deploy [Code-Server](https://github.com/cdr/code-server) (a NodeJS web app) into an EC2 instance, using Ubuntu AMI and provisioning all package through a bash scripts (UserData), as in the below diagram depict it. 

![](/assets/blog20200906_cloudidecdk/0-cloud-ide-aws-arch-cdk-ec2-ami-userdata-vscode-code-server.png){:style="display:block;margin:auto;"}

<!-- more -->

### Persisting the code 

Code-Server doesn't require a Database, the only thing to persist is the code. The EC2 instance has enough room to download and persist the code and its dependencies like you do it in your local computer. If you have completed your project, then you can push your code to your Git repository. 

### On-demand computing

If you want compile, run your code, or want to process long task, then you will need processing and computing capacity and you will get from EC2 instance you choose. If you need more just run again your IaC with different `EC2 Instance Type`. This example uses a `t2.micro`.

### Security

Code-Server is a web application and will require a secure configuration like a standard web application. By default, the only security control that Code-Server is authentication. A fresh Code-Server installation will generate during its installation a password. And considering that, we need implementing extra security around the web application like TLS and/or Mutual TLS Authentication, Whitelisting IP and only open the HTTP/HTTPS port. If you have more time, you could implement a Reverse Proxy with security capabilities or using additional AWS services like AWS API Gateway, AWS CloudFront (WAF), etc.

### I already have VSCode, can share my settings, theme and extensions?

Yes, you can do it.   
This is the part that I like the most, I'm in love with [Settings Sync](https://marketplace.visualstudio.com/items?itemName=Shan.code-settings-sync) Extension, only I do configure a Gist ID and Setting Sync automatically download previous VS Code' settings (theme, style, configuration, etc.) from Gist and install all VS Code extensions for you.   


Well, let's get to the point.  

### Getting started

#### 1. CDK installation

```sh
$ sudo apt install -y nodejs npm
$ sudo npm i -g aws-cdk
```

#### 2. Install Code-Server on AWS

You don't have to code anything, I've created the AWS CDK project in TypeScript for you, just clone the Git repo and run it.

Before anything configure your AWS account and region:   
```sh
$ export AWS_ACCESS_KEY_ID="abc"
$ export AWS_SECRET_ACCESS_KEY="pqr"
$ export AWS_DEFAULT_REGION="us-east-1"
```

Initialize the CDK project:   
```sh
$ git clone https://github.com/chilcano/aws-cdk-samples
$ cd aws-cdk-samples/code-server-ec2
$ nmp install
```

If you want to review the CloudFormation code that the CDK project generates, execute this command:   
```sh
$ cdk synth
```

And finally, deploy the CDK project:   

```sh
$ cdk deploy
```

You will get this output in your terminal. That means everything went well.
[![](/assets/blog20200906_cloudidecdk/1-cdk-deploy-output.png){:width="400"}{:style="display:block;margin:auto;"}](/assets/blog20200906_cloudidecdk/1-cdk-deploy-output.png)


#### 3. Calling the Code-Server

From your AWS Web Console, get the `Public DNS (IPv4)` or `IPv4 Public IP` about the EC2 instance created, with that information connect to the host through SSH and get the Code-Server's password that generates by default:   
```sh
$ ssh ubuntu@ec2-54-227-165-113.compute-1.amazonaws.com -i ~/Downloads/chilcan0.pem 

ubuntu@ip-10-0-0-117:~$ cat .config/code-server/config.yaml 
bind-addr: 127.0.0.1:8080
auth: password
password: 5995b51c25c621190abd55e8
cert: false
``` 

Now, create the SSH tunnel:   
```sh
$ ssh -nNT -L 8123:localhost:8080 ubuntu@ec2-54-227-165-113.compute-1.amazonaws.com -i ~/Downloads/chilcan0.pem
```  
Finally, open the URL [http://localhost:8123](http://localhost:8123) in your browser, you should see this:  

[![](/assets/blog20200906_cloudidecdk/2-app-2.png){:width="400"}{:style="display:block;margin:auto;"}](/assets/blog20200906_cloudidecdk/2-app-2.png)[![](/assets/blog20200906_cloudidecdk/2-app-3.png){:width="400"}{:style="display:block;margin:auto;"}](/assets/blog20200906_cloudidecdk/2-app-3.png)


#### 4. Restoring your VSCode' settings in Code-Server


I'm using a bash scripts as `userData` to install Code-Server and only the [Settings-Sync](https://marketplace.visualstudio.com/items?itemName=Shan.code-settings-sync) extension that will allow restore a previous Code-Server or VS Code settings, even other extensions. For that you need a Gist ID that your previous Code-Server or VS Code has generated. If you don't have one, you can use mine.  

> Settings-Sync Gist ID: b5f88127bd2d89289dc2cd36032ce856   
> Gist URL: [https://gist.github.com/chilcano/b5f88127bd2d89289dc2cd36032ce856](https://gist.github.com/chilcano/b5f88127bd2d89289dc2cd36032ce856)   


Once restored all Code-Server settings, you will see this:   

[![](/assets/blog20200906_cloudidecdk/2-app-3b.png){:width="400"}{:style="display:block;margin:auto;"}](/assets/blog20200906_cloudidecdk/2-app-3b.png)[![](/assets/blog20200906_cloudidecdk/2-app-3c.png){:width="400"}{:style="display:block;margin:auto;"}](/assets/blog20200906_cloudidecdk/2-app-3c.png)


That's it.
Hope this helps.

### References

1. [Building an affordable remote DevOps desktop on AWS | Apr 9, 2020](https://holisticsecurity.io/2020/04/09/building-an-affordable-remote-devops-desktop-on-aws)
2. [A Cloud IDE for the masses | Aug 5, 2020 ](https://holisticsecurity.io/2020/08/05/a-cloud-ide-for-the-masses)
