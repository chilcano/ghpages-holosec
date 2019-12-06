---
layout: post
title: Share Files between MacOSX and CentOS as a VirtualBox guest
date: 2010-08-13 15:04:51.000000000 +02:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Linux
- Security
tags:
- CentOS
- virtualbox
meta:
  _edit_last: '578869'
  _oembed_92b57fbd9a7e7ea58265479679522320: "{{unknown}}"
  _wpas_done_twitter: '1'
  _oembed_ae122ad87397f0522ea0a0f6056d176c: "{{unknown}}"
  _wp_old_slug: ''
  _oembed_7be6aefb2298070817710e0255fbf904: "{{unknown}}"
  _oembed_318be37c0b0d72f1c0153260c0ed1dbf: "{{unknown}}"
  _oembed_9b9ceda76b26ae729117eb2050c90657: "{{unknown}}"
  _oembed_3292709792d34cfc1ba15700b954b069: "{{unknown}}"
  _oembed_8ea2e88639ebde013f544ad8d05e457c: "{{unknown}}"
  _oembed_9a28bdefa2fed74a6244aad59c22691f: "{{unknown}}"
  _oembed_1f20fc27b83216f5058403b16d8d392c: "{{unknown}}"
  _oembed_079b5af662d397153a67f68b2b288b96: "{{unknown}}"
  _oembed_1267b516a6232d5035c81785d3979181: "{{unknown}}"
  _oembed_12d016b02a0402c727784d6c6cdcab7e: "{{unknown}}"
  tagazine-media: a:7:{s:7:"primary";s:116:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-4-verify-mounted-host.png";s:6:"images";a:5:{s:109:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-1-mount-dvdiso.png";a:6:{s:8:"file_url";s:109:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-1-mount-dvdiso.png";s:5:"width";s:3:"459";s:6:"height";s:3:"354";s:4:"type";s:5:"image";s:4:"area";s:6:"162486";s:9:"file_path";s:0:"";}s:111:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-2-mount-vboxadds.png";a:6:{s:8:"file_url";s:111:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-2-mount-vboxadds.png";s:5:"width";s:3:"525";s:6:"height";s:3:"175";s:4:"type";s:5:"image";s:4:"area";s:5:"91875";s:9:"file_path";s:0:"";}s:114:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-3-share-host-folder.png";a:6:{s:8:"file_url";s:114:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-3-share-host-folder.png";s:5:"width";s:3:"415";s:6:"height";s:3:"301";s:4:"type";s:5:"image";s:4:"area";s:6:"124915";s:9:"file_path";s:0:"";}s:116:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-4-verify-mounted-host.png";a:6:{s:8:"file_url";s:116:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-4-verify-mounted-host.png";s:5:"width";s:3:"769";s:6:"height";s:3:"342";s:4:"type";s:5:"image";s:4:"area";s:6:"262998";s:9:"file_path";s:0:"";}s:117:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-5-verify-mounted-guest.png";a:6:{s:8:"file_url";s:117:"http://dl.dropbox.com/u/2961879/blog20100813_sharefilesmacosxcentos/sharefilescentosmacosx-5-verify-mounted-guest.png";s:5:"width";s:3:"459";s:6:"height";s:3:"305";s:4:"type";s:5:"image";s:4:"area";s:6:"139995";s:9:"file_path";s:0:"";}}s:6:"videos";a:0:{}s:11:"image_count";s:1:"5";s:6:"author";s:6:"578869";s:7:"blog_id";s:7:"2005905";s:9:"mod_stamp";s:19:"2010-12-16
    11:28:28";}
  _oembed_18b38ec83ecdd50b0426559dff75ae65: "{{unknown}}"
  _oembed_75d7fc13e677758695f6b68fa4a90bed: "{{unknown}}"
  _oembed_d7ed845a51f380e201a252c70da216a5: "{{unknown}}"
  _oembed_f95ff04dc6b9b23d3f68099ba83be73a: "{{unknown}}"
  _oembed_95e56408797636dbd7718ccdc9dcfa3c: "{{unknown}}"
  _oembed_3c83971ae00533baf9b48098a159ceeb: "{{unknown}}"
  _oembed_ee97bc9c433b484d27fcf176e99945ee: "{{unknown}}"
  _oembed_4a0904f146cbf675f8f85c06d2f280f4: "{{unknown}}"
  _oembed_f40d15b7d30aa2c02cee070b40908fe4: "{{unknown}}"
  _oembed_52db8c605766dffd8a045562a63e010c: "{{unknown}}"
  _oembed_428475bc4430b3a9506ef97c9105bf54: "{{unknown}}"
  _oembed_dc39b2c52eded1343d5eb8b67c0e6628: "{{unknown}}"
  _oembed_3be298ea8c869ba458001ad8829b1752: "{{unknown}}"
  _oembed_4bb11834614723bfdd57092a3874f79b: "{{unknown}}"
  _oembed_767c48cd12875be93a9c866be82f3a72: "{{unknown}}"
  _oembed_c8c31fcbe51d56d8b80240caf32bb27e: "{{unknown}}"
  _oembed_1f0cc9ff67c8eeba1cd447c91692ab10: "{{unknown}}"
  _oembed_d858ad7ccb720dd90ddde858a5c59570: "{{unknown}}"
  _oembed_d819f0ce7ac8ae3bd3462fd46c0f24af: "{{unknown}}"
  _oembed_6d292180d6cc9bb7c9ff85cacb477da0: "{{unknown}}"
  _oembed_ff891ef3288d03e5ae9ea1267b279fb0: "{{unknown}}"
  _oembed_42d860443dd97764d862bd3d0ef1186f: "{{unknown}}"
  _oembed_48e8f0e1ed73608b8c0f73d927a10538: "{{unknown}}"
  _oembed_b621b3868be7e51d42fbb6b38927534e: "{{unknown}}"
  twitter_cards_summary_img_size: a:6:{i:0;i:459;i:1;i:354;i:2;i:3;i:3;s:24:"width="459"
    height="354"";s:4:"bits";i:8;s:4:"mime";s:9:"image/png";}
  _oembed_5e11b65446976a9d44ab32d4e7be57a6: "{{unknown}}"
  _oembed_aca5dcf6ffedacafe5ff3813e7e97363: "{{unknown}}"
  _oembed_2680b097cbb87cdb03ab350476be47f5: "{{unknown}}"
  _oembed_cf7d66f503fb73f4139105f3492a782b: "{{unknown}}"
  _oembed_9ce66d66b8a058888767f1cb528ea73d: "{{unknown}}"
  _oembed_c276ead17cb3f6ec500d2eb0dddf5fe8: "{{unknown}}"
  _oembed_3574b60fc76793e5628c3a316d6c7bca: "{{unknown}}"
  _oembed_8cb7045c0dcf8f99ff2b88a6096fc324: "{{unknown}}"
  geo_public: '0'
  _wpas_skip_5053092: '1'
  _wpcom_is_markdown: '1'
  _oembed_770f35f550924f88444d45a7384778c3: "{{unknown}}"
  _oembed_99a26a083a98cad454ff2a4fd1249bce: "{{unknown}}"
  _oembed_5193ea37169a923a89fcbe73e766a312: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/08/13/share-files-between-macosx-and-centos-as-a-virtualbox-guest/"
---
Share files between two SO is easy because there are different protocols for sharing resources (NFS, CIFS/SMB/Samba, FTP, ...), but sharing files between two PCs with OS Linux based, and one of them as virtualmachine guest of another one is a bit more complicated. Here's how to do it when we Mac OSX as HOST and CentOS as GUEST:

  


  


## Mount DVD ISO image in CentOS VirtualBox guest

  


1.- Previously, I have download DVD ISO image, then I have copied to my Mac OSX Host.

  


2.- From Virtual Box menu, goes to Devices > CD/DVD Devices and mount selecting CentOS's DVD ISO image (ISO previuosly added to VBox's "Virtual Media Manager").

  


![]({{ site.baseurl }}/assets/sharefilescentosmacosx-1-mount-dvdiso.png)

  


3.- In CentOS guest now you can see DVD iso mounted as **/media/CentOS_5.5_Final**

  


4.- You can install packages directly off the mounted DVD ISO. Now, modify **/etc/yum.repos.d/CentOS-Media.repo** file, enter:

  

    
    
    [root@localhost ~]# vi /etc/yum.repos.d/CentOS-Media.repo

  


5.- Make sure enabled is set to 1:

  

    
    
    enabled=1

  


6.- Save and close the file. To install packages from only DVD media repo, do this:

  

    
    
    [root@localhost ~]# yum --disablerepo=\* --enablerepo=c5-media -y install <package-name>

  


 _  
  
Notes:  
  
* --disablerepo=*: disable all yum repo  
  
* -y: assume yes to any question which would be asked  
  
* --enablerepo=c5-media: enable c5-media repo  
  
_

  


## Install Virtual Box Additions in CentOS guest

  


Before anything, It is necessary install some packages as compiler gcc, kernel libs and sources.

  


1.- Install the following packages with CentOS DVD ISO image mounted:

  

    
    
    [root@localhost ~]# yum --disablerepo=\* --enablerepo=c5-media -y install gcc  
    [root@localhost ~]# yum --disablerepo=\* --enablerepo=c5-media -y install kernel sources  
    [root@localhost ~]# yum --disablerepo=\* --enablerepo=c5-media -y install kernel-devel

  


2.- Unmount CentOS DVD ISO image and mount Virtual Box Guest Additions in your CentOS guest:

  


![]({{ site.baseurl }}/assets/sharefilescentosmacosx-2-mount-vboxadds.png)

  


3.- Restart CentOS guest

  


4.- Now, install VBoxGuestAdditions in CentOS guest:

  

    
    
    [root@localhost ~]# cd /media/VBOXADDITIONS_3.2.6_63112/  
    [root@localhost VBOXADDITIONS_3.2.6_63112]# sh VBoxLinuxAdditions.run

  


5.- Restart CentOS guest again

  


6.- Now you could use special functionalities as resize the guest windows, share files, etc...

## Share files beetwen Mac OS X as Host and CentOS as Guest

1.- In your Host Linux box:
    
    
    $ mkdir /Users/chilcano/files2share

2.- In main menu of your Linux Guest's Virtual Box:

  * Go to **Devices** > **Shared Folders** ...
  * In popup, add shared folder and select Host's folder ( **/Users/chilcano/files2share** ), set up a name as **files2share** and make permanent.



![]({{ site.baseurl }}/assets/sharefilescentosmacosx-3-share-host-folder.png)

3.- In your Guest Linux box opens a terminal window and create a new directory where you'll mount shared folder
    
    
    [root@localhost /]# mkdir /vbox_shared

4.- In this terminal, enter the id command and look the output. We will use uid and gid:
    
    
    [root@localhost /]# id
    uid=0(root) gid=0(root) groups=0(root),1(bin),2(daemon),3(sys),4(adm),6(disk),10(wheel) context=root:system_r:unconfined_t:SystemLow-SystemHigh

5.- Now, we will mount shared folder from Host to Guest. From terminal in Guest Linux:
    
    
    [root@localhost /]# mount -t vboxsf -o uid=0,gid=0 files2share /vbox_shared

## Make the Mounted Share permanent

You can make the shared folder mount automatically each time you start the CentOS guest by making an entry in **/etc/fstab**.

1.- From the CentOS guest edit fstab:
    
    
    [root@localhost ~]# sudo gedit /etc/fstab

2.- Add a line at the bottom of file, and that looks like this:
    
    
    files2share    /vbox_shared    vboxsf  rw,uid=0,gid=0  0   0

3.- Verify mounted folder

In Mac OS X (host):  
![]({{ site.baseurl }}/assets/sharefilescentosmacosx-4-verify-mounted-host.png)

In CentOS (guest):  
![]({{ site.baseurl }}/assets/sharefilescentosmacosx-5-verify-mounted-guest.png)

4.- End.

**References** :

  * CentOS: Install Packages Via yum Command Using DVD / CD as Repo  
http://www.cyberciti.biz/faq/centos-linux-install-packages-from-dvd-using-yum/
  * Implementing virtualbox shared folders between a Mac OS X host and Fedora guest  
http://davidherron.com/content/implementing-virtualbox-shared-folders-between-mac-os-x-host-and-fedora-guest




http://forums.virtualbox.org/viewtopic.php?t=4960&highlight=install+guest+addition  
* How to install Guest Additions in CentOS 5.1

  * Share Folders Between a Linux Host and Linux Virtual Machine on VirtualBox  
http://tuxtweaks.com/2009/06/share-folders-linux-host-linux-virtual-machine-virtualbox/


