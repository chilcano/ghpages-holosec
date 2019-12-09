---
layout:     post
title:      'Install packages from CentOS 5.5 ISO CDROM images'
date:       2010-08-06 19:56:49
categories: ['Linux', 'Security']
tags:       ['CentOS']
status:     publish 
permalink:  "/2010/08/06/install-packages-from-centos-5-5-iso-cdrom-images/"
---
[![]({{ site.baseurl }}/assets/blog_centos55.png)](http://holisticsecurity.files.wordpress.com/2010/08/blog_centos55.png)
  1. Upload CentOS's ISO files (i have 7 iso files) into **/centos5.5_iso_files** directory.  

<!-- more -->  
2\. It is probably that you do not have **createrepo** rpm package, then download it from CentOS RPM repository.  
I've found it in <http://mirror.centos.org/centos/5.5/os/i386/CentOS/createrepo-0.4.11-3.el5.noarch.rpm>
  2.   
Install **createrepo-0.4.11-3.el5.noarch.rpm** :
>     
>     # rpm -ihv createrepo-0.4.11-3.el5.noarch.rpm
... and check installation:
>     
>     # rpm -qa | grep createrepo
  1. Mount the 7 ISO files:
>     
>     # mkdir -p /mnt/iso/{1,2,3,4,5,6,7}  
>     > # mount -o loop /root/centos55-i386-isos/CentOS-5.5-i386-bin-1of7.iso /mnt/iso/1  
>     > # mount -o loop /root/centos55-i386-isos/CentOS-5.5-i386-bin-2of7.iso /mnt/iso/2  
>     > # mount -o loop /root/centos55-i386-isos/CentOS-5.5-i386-bin-3of7.iso /mnt/iso/3  
>     > # mount -o loop /root/centos55-i386-isos/CentOS-5.5-i386-bin-4of7.iso /mnt/iso/4  
>     > # mount -o loop /root/centos55-i386-isos/CentOS-5.5-i386-bin-5of7.iso /mnt/iso/5  
>     > # mount -o loop /root/centos55-i386-isos/CentOS-5.5-i386-bin-6of7.iso /mnt/iso/6  
>     > # mount -o loop /root/centos55-i386-isos/CentOS-5.5-i386-bin-7of7.iso /mnt/iso/7
  1. Create a new Yum's repository:
>     # cd /mnt/iso
>     # createrepo .
>     2598/2598 - 4/CentOS/zsh-4.2.6-3.el5.i386.rpm                                   rpmm6.rpm
>     Saving Primary metadata
>     Saving file lists metadata
>     Saving other metadata
>     # yum clean all
  1. Create repository configuration file:
>     # nano /etc/yum.repos.d/centos55_i386_iso.repo
append:
>     [My-CentOS5.5-i386-ISO-repo]
>     name=CentOS5.5-isos
>     baseurl=file:///mnt/iso
>     enabled=1
  1. Save and close the changes, now you can use Yum to install, search packages from ISO images, for example:
>     # yum --nogpgcheck install my-package-name
>     # yum search my-package-name
  1. Troubleshooting:
  * Sometimes when installing, searching or updating packages, Yum shows errors as "Bad id for repo: ....", for solve it you could to delete strange chars or blank spaces in **/etc/yum.repos.d/centos55_i386_iso.repo**
* When yum shows following error:
>     Could not retrieve mirrorlist http://mirrorlist.centos.org/?release=5&arch=i386&repo=addons error was
>     [Errno 4] IOError:
>     Error: Cannot find a valid baseurl for repo: addons
you had to disable repositories in **/etc/yum.repos.d/CenOS-Base.repo** , make sure of save a backup
append to each section:
>     [any-section]
>     ...
>     enabled=0
then, clean cache again:
>     # yum clean all
**References:**  
* Howto Setup yum repositories to update or install package from ISO CDROM Image  

[ http://www.cyberciti.biz/tips/redhat-centos-fedora-linux-setup-repo.html](http://www.cyberciti.biz/tips/redhat-centos-fedora-linux-setup-repo.html)
Bye.
