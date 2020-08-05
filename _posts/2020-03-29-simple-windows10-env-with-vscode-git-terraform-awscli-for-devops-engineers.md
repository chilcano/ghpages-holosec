---
layout:     post
title:      "Simple Windows 10 Environment for DevOps Engineers"
categories: ['cloud', 'devops'] 
tags:       ['awscli', 'k8s', 'terraform', 'windows 10', 'git']
date:       2020-03-30 18:00:00 +0100
permalink:  "/2020/03/29/simple-windows10-env-with-vscode-git-terraform-awscli-for-devops-engineers"
comments:   true
---
If you are working as a DevOps Engineer and want to automate the creation of your infrastructure on AWS from Windows 10, then you should install and configure a minimalist toolset to do [Infrastructure as Code (IaC)](https://en.wikipedia.org/wiki/Infrastructure_as_code) tasks. Since I'm using an older Surface 3 Pro (Windows 10 with 4GB RAM and 64GB SSD), I'm going to focus on Terraform coding, leaving out Docker, K8s, Jenkins, etc. for another article.

![](/assets/blog20200330b/20200330b-simple-windows10-env-with-vscode-git-terraform-awscli-for-devops-1.png){:width="400"}{:style="display:block;margin:auto;"}

<!-- more -->

### Steps

#### 1. Install VS Code

Download and install VS Code manually from here [https://code.visualstudio.com/download](https://code.visualstudio.com/download).

__Recommended extensions:__
1. [https://github.com/oderwat/vscode-indent-rainbow](https://github.com/oderwat/vscode-indent-rainbow)
2. [https://github.com/usernamehw/vscode-indent-one-space](https://github.com/usernamehw/vscode-indent-one-space)
3. [https://github.com/mauve/vscode-terraform](https://github.com/mauve/vscode-terraform)
4. [https://github.com/yzhang-gh/vscode-markdown](https://github.com/yzhang-gh/vscode-markdown)

#### 2. Install GIT, AWS Client and Terraform

Open CMD as Admin user and execute below commands.

```ps
// Install Git
C:\WINDOWS\system32> choco install git -y

// Terraform (no latest version)
C:\WINDOWS\system32> choco install terraform --version 0.11.14 -y

// Install AWS Client
C:\WINDOWS\system32> choco install awscli -y
```

Close the CMD and open it again but as Standard user, after that check all versions installed.

```ps
C:\Users\rmce> git version
git version 2.26.0.windows.1

C:\Users\rmce> terraform version
Terraform v0.11.14

Your version of Terraform is out of date! The latest version
is 0.12.24. You can update by downloading from www.terraform.io/downloads.html

C:\Users\rmce> aws --version
aws-cli/2.0.5 Python/3.7.5 Windows/10 botocore/2.0.0dev9
```

#### 3. Set up Git in VS Code

Open CMD and run the following commands.

```ps
C:\Users\rmce> git config --global user.email "you@example.com"
C:\Users\rmce> git config --global user.name "Your Name"
```

This optional. Make Git store the username and password and it will never ask for them.

```ps
C:\Users\rmce> git config --global credential.helper store

// Save the username and password for a session
C:\Users\rmce> git config --global credential.helper cache

// Also set a timeout for the above setting
C:\Users\rmce> git config --global credential.helper 'cache --timeout=600'
```

Create a folder to allocate the Github repos. In this case I'm going to use my [Affordable-K8s](https://github.com/chilcano/affordable-k8s) Repo. It is a Terraform plan to create a K8s Cluster in AWS using Spot Instances.

```ps
C:\Users\rmce> y:
Y:\> mkdir __gitrepos
Y:\> cd __gitrepos
Y:\__gitrepos> git clone https://github.com/chilcano/affordable-k8s
Y:\__gitrepos> cd affordable-k8s
```

Modify any file and push your changes.

```ps
Y:\__gitrepos\affordable-k8s> git add .
Y:\__gitrepos\affordable-k8s> git commit -m "README updated"
Y:\__gitrepos\affordable-k8s> git push
```

#### 4. Open an existing Terraform project from VS Code

Once cloned [Affordable-K8s](https://github.com/chilcano/affordable-k8s) Repo into a local folder, now I'll open it executing this command.

```ps
Y:\__gitrepos\affordable-k8s> code .
```

![](/assets/blog20200330b/20200330b-simple-windows10-env-with-vscode-git-terraform-awscli-for-devops-2.png){:width="400"}{:style="display:block;margin:auto;"}