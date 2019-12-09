---
layout:     post
title:      'Open files in Ubuntu as ROOT from WinSCP remotely'
date:       2012-09-03 12:29:55
categories: ['Linux']
tags:       []
status:     publish 
permalink:  "/2012/09/03/open-files-ubuntu-root-from-winscp-remotely/"
---
I would like to share a tip, frequently we need modify protected files (i.e. in an Ubuntu box) from **WinSCP** remotely, but sometimes that is impossible.
If you have a non-privileged user and want to edit a root protected file of an Ubuntu box but with WinSCP, you can do it with a few changes.

**Requirements** :
1\. Server:
\- Ubuntu 11.04  
\- Non-privileged user: chilcano
2\. Client: 
\- Any S.O. but with WinSCP 4.3.6 (http://winscp.net)

**Steps** :
1\. In the server side add this line to /etc/sudoers

[sourcecode language="text" gutter="true" wraplines="false"]  
chilcano@sso1:/$ sudo nano /etc/sudoers  

[/sourcecode]
and

[sourcecode language="text" gutter="true" wraplines="false" highlight="3"]  

[...]  

#### added  
chilcano ALL=NOPASSWD: /usr/lib/openssh/sftp-server  

[/sourcecode]
2\. In the client side open WinSCP and do the following

[caption id="" align="alignnone" width="420" caption="Check SFTP protocol in WinSCP"][![Check SFTP protocol in WinSCP]({{ site.baseurl }}/assets/blog_20120104_tip_winscp1.png)](http://dl.dropbox.com/u/2961879/blog20120104_tip_winscp/blog_20120104_tip_winscp1.png)[/caption]
and

[caption id="" align="alignnone" width="422" caption="Add customized command to sftp-server in Ubuntu"][![Add customized command to sftp-server in Ubuntu]({{ site.baseurl }}/assets/blog_20120104_tip_winscp2.png)](http://dl.dropbox.com/u/2961879/blog20120104_tip_winscp/blog_20120104_tip_winscp2.png)[/caption]
3\. Test it, open a protected file and modify. You could do changes on it.

[caption id="" align="alignnone" width="465" caption="Open as ROOT from WinSCP"][![Open as ROOT from WinSCP]({{ site.baseurl }}/assets/blog_20120104_tip_winscp3.png)](http://dl.dropbox.com/u/2961879/blog20120104_tip_winscp/blog_20120104_tip_winscp3.png)[/caption]
End.
I hope this help you.
