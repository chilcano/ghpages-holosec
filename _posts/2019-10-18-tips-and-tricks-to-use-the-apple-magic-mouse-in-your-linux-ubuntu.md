---
layout: post
title:  "Tips & Tricks to use the Apple Magic Mouse in your Linux Ubuntu"
date:   2019-10-18 00:00:00 +0200
categories: misc ubuntu linux apple magic mouse apple bluetooth
tags: misc ubuntu linux apple magic mouse apple bluetooth 
permalink: "/2019/10/18/tips-and-tricks-to-use-the-apple-magic-mouse-in-your-linux-ubuntu"
---

This a set of resources (reported issues, blogs, drivers, tips and tricks) to eager to use your Apple Magic Mouse in your Linux Ubuntu.
These resources are recommedations of many people whom tried and got successfully the Apple Magic Mouse working in Linux. 
Next you can review documentation about how to configure it in your Linux PC:
<!-- more -->
- Apple magic mouse 2 does not work by default:  
  [https://bugs.launchpad.net/ubuntu/+source/linux-signed-hwe/+bug/1822770](https://bugs.launchpad.net/ubuntu/+source/linux-signed-hwe/+bug/1822770)
- Apple Magic Mouse 2 and Trackpad Driver for Linux:  
  [https://github.com/robotrovsky/Linux-Magic-Trackpad-2-Driver](https://github.com/robotrovsky/Linux-Magic-Trackpad-2-Driver)
- Using the Apple Magic Mouse with Ubuntu (16.0.4):  
  [http://sneclacson.blogspot.com/2016/09/using-apple-magic-mouse-with-ubuntu-1604.html](http://sneclacson.blogspot.com/2016/09/using-apple-magic-mouse-with-ubuntu-1604.html)
- Archlinux - Configuration & troubleshooting steps to Bluetooth mice:  
  [https://wiki.archlinux.org/index.php/Bluetooth_mouse](https://wiki.archlinux.org/index.php/Bluetooth_mouse)
- How to Install Blueman Manager on Ubuntu Based OSes:  
  [https://tutorialforlinux.com/2018/04/14/how-to-install-blueman-manager-on-ubuntu-based-oses](https://tutorialforlinux.com/2018/04/14/how-to-install-blueman-manager-on-ubuntu-based-oses)
- What does this error mean? (Trying to reduce mouse sensitivity):  
  [reddit.com/r/linux4noobs/comments/758tjp/what_does_this_error_mean_trying_to_reduce_mouse/do4a9sh](reddit.com/r/linux4noobs/comments/758tjp/what_does_this_error_mean_trying_to_reduce_mouse/do4a9sh)


> Just mention that since Ubuntu 19.04 (kernel 5.0) the Magic Mouse works out of the box and not tweaking were needed.

> In Ubuntu 19.10 I installed and uninstalled `blueman`, if you did that, you had to restart/enable the bluetooth service. Here that is explained: [https://medium.com/@djorborn/bluetoothctl-failed-to-connect-org-bluez-error-failed-54040f12b4f8](https://medium.com/@djorborn/bluetoothctl-failed-to-connect-org-bluez-error-failed-54040f12b4f8)

```sh
chilcano@inti:~$ sudo systemctl start bluetooth.service
chilcano@inti:~$ sudo systemctl enable bluetooth.service
Synchronizing state of bluetooth.service with SysV service script with /lib/systemd/systemd-sysv-install.
Executing: /lib/systemd/systemd-sysv-install enable bluetooth
```
> Finally I made the decision to buy the [Logitech M590 Multi-Device Silent](https://www.logitech.com/en-us/product/m590-silent-wireless-mouse) mouse instead of trying using the Apple Magic Mouse with Ubuntu 19.10 because I was getting scrolling lag, anoying behaviour with scrolling and clicking, and continuous bluetooth disconnection.

> The Logitech M590 mouse can be use through bluetooth and unify receiver. It works in Ubuntu 19.10 and bluetooth by default, but not with unify receiver. To fix it I had to follow this information about [Solaar and Unify technology](https://lekensteyn.nl/logitech-unifying.html) in AskUbuntu forum: [Is Logitech's Unifying receiver supported?](https://askubuntu.com/questions/113984/is-logitechs-unifying-receiver-supported)

I hope this helps you.