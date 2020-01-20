---
layout: post
title:  "Setting a Python 3 local programming environment"
date:   2019-12-08 10:00:00 +0100
categories: development 
tags: python
permalink: "/2019/12/08/python3-setting-a-local-env"
comments: false
---

This post will guide you through installing Python 3, and Python 2.x, on your local Linux machine and setting up a programming environment via the command line. This post will explicitly cover the installation procedures for Ubuntu 18.04 or above, but the general principles apply to any other distribution of Debian Linux.

![Python 3](/assets/img/20191208-python.png "Setting a Python 3 local programming environment")


<!-- more -->


## Step 1 — Setting Up Python 3

```sh
$ python3 -V

$ sudo apt install -y python-pip
$ sudo apt install -y python3-pip

// way to install python modules
$ pip install package_name
$ pip3 install package_name

$ sudo apt install build-essential libssl-dev libffi-dev python-dev
```


## Step 2 — Setting Up a Virtual Environment

```sh
// using Apt
$ sudo apt install -y virtualenv
$ sudo apt install -y python3-venv

// using Pip
$ pip install virtualenv
$ pip3 install virtualenv
```

With this installed, we are ready to create environments. Let’s either choose which directory we would like to put our Python programming environments in, or create a new directory with mkdir, as in:

```sh
$ mkdir abc
$ cd abc
```

Once you are in the directory where you would like the environments to live, you can create an environment by running the following command:

```sh
$ virtualenv -p /usr/bin/python myenv27
$ python3 -m venv myenv3
```

Essentially, this sets up a new directory that contains a few items which we can view with the ls command:

```sh
$ ls myenv27/
bin  include  lib  local  share

$ ls myenv3/
bin  include  lib  lib64  pyvenv.cfg  share
```

## Step 3 — Activate the Python Environment

To use this environment, you need to activate it, which you can do by typing the following command that calls the activate script:

```sh
$ source myenv27/bin/activate
(myenv27) chilcano@inti:~/git-repos/abc$ 

$ source myenv3/bin/activate
(myenv3) chilcano@inti:~/git-repos/abc$ 
```

> Note: Within the virtual environment, you can use the command python instead of python3, and pip instead of pip3 if you would prefer. If you use Python 3 on your machine outside of an environment, you will need to use the python3 and pip3 commands exclusively.

## Step 4 — Creating a Python3 Program


```sh
(myenv27) chilcano@inti:~/git-repos/abc$ pip install pylint python-dateutil BeautifulSoup4 html2text slugify wget

(myenv3) chilcano@inti:~/git-repos/abc$ python3 -m pip install -U pylint
(myenv3) chilcano@inti:~/git-repos/abc$ pip install pylint python-dateutil BeautifulSoup4 html2text slugify
(myenv3) chilcano@inti:~/git-repos/abc$ pip install pyyaml
```

> To leave the environment, simply type the command `deactivate` and you will return to your original directory.


## References

- [https://www.digitalocean.com/community/tutorials/how-to-install-python-3-and-set-up-a-local-programming-environment-on-ubuntu-18-04](https://www.digitalocean.com/community/tutorials/how-to-install-python-3-and-set-up-a-local-programming-environment-on-ubuntu-18-04)
- [https://www.digitalocean.com/community/tutorials/how-to-import-modules-in-python-3](https://www.digitalocean.com/community/tutorials/how-to-import-modules-in-python-3)
- [https://help.dreamhost.com/hc/en-us/articles/215489338-Installing-and-using-virtualenv-with-Python-2](https://help.dreamhost.com/hc/en-us/articles/215489338-Installing-and-using-virtualenv-with-Python-2)