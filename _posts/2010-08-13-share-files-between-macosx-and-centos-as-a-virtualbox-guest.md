---
layout:     post
title:      'Share Files between MacOSX and CentOS as a VirtualBox guest'
date:       2010-08-13 13:04:51
categories: ['Linux', 'Security']
tags:       ['CentOS', 'virtualbox']
status:     publish 
permalink:  "/2010/08/13/share-files-between-macosx-and-centos-as-a-virtualbox-guest/"
---
Share files between two SO is easy because there are different protocols for sharing resources (NFS, CIFS/SMB/Samba, FTP, ...), but sharing files between two PCs with OS Linux based, and one of them as virtualmachine guest of another one is a bit more complicated. Here's how to do it when we Mac OSX as HOST and CentOS as GUEST:

<!-- more -->


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
