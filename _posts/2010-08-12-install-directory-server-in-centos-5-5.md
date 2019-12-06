---
layout: post
title: Install Directory Server in CentOS 5.5
date: 2010-08-12 16:13:06.000000000 +02:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Linux
- Portal
- Security
tags:
- LDAP
meta:
  _edit_last: '578869'
  _oembed_e9b6b5609b1c06be96620270007694f1: "{{unknown}}"
  _wpas_done_twitter: '1'
  _oembed_0005d878ebc0f1e7bef1335db8baa84e: "{{unknown}}"
  _oembed_4b762c254ab4891f8e8a92b6218e01cf: "{{unknown}}"
  _wp_old_slug: ''
  _oembed_2ea80159b58f974fc7cdfbbb97742bdb: "{{unknown}}"
  _oembed_e080f6cee377ddad59881ffbe75f0bf9: "{{unknown}}"
  _oembed_8cc4786932e1b7c30347a84044dde057: "{{unknown}}"
  _oembed_ccbc1e72d070ab29020e961bf5b058b2: "{{unknown}}"
  _oembed_e144a7a96d8c18062be5c4c9f27c15f4: "{{unknown}}"
  _oembed_c92e87e17737e92de5d2286d5e535f5e: "{{unknown}}"
  _oembed_4085947dfca65a94139dce6f19d5793b: "{{unknown}}"
  _oembed_1e4661449a490a43c405311244ed7136: "{{unknown}}"
  _oembed_1cf64cd747bcc04196a13c081c735489: "{{unknown}}"
  _oembed_4184f70ea8f09ed3c8efb26d340535da: "{{unknown}}"
  _oembed_52a374e9eaf32b683c61c0b042ccdd05: "{{unknown}}"
  _oembed_6a41599ac86fd7c66d6c88b45645e3cb: "{{unknown}}"
  _oembed_abf11e51ee110317f56d1e7f447b87b4: "{{unknown}}"
  tagazine-media: a:7:{s:7:"primary";s:91:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_5_ApacheDirectoryStudio.png";s:6:"images";a:6:{s:81:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_1_run_console.png";a:6:{s:8:"file_url";s:81:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_1_run_console.png";s:5:"width";s:3:"475";s:6:"height";s:3:"375";s:4:"type";s:5:"image";s:4:"area";s:6:"178125";s:9:"file_path";s:0:"";}s:77:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_2_console.png";a:6:{s:8:"file_url";s:77:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_2_console.png";s:5:"width";s:3:"787";s:6:"height";s:3:"504";s:4:"type";s:5:"image";s:4:"area";s:6:"396648";s:9:"file_path";s:0:"";}s:84:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_3_addusr_console.png";a:6:{s:8:"file_url";s:84:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_3_addusr_console.png";s:5:"width";s:3:"882";s:6:"height";s:3:"608";s:4:"type";s:5:"image";s:4:"area";s:6:"536256";s:9:"file_path";s:0:"";}s:91:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_4_ApacheDirectoryStudio.png";a:6:{s:8:"file_url";s:91:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_4_ApacheDirectoryStudio.png";s:5:"width";s:3:"606";s:6:"height";s:3:"351";s:4:"type";s:5:"image";s:4:"area";s:6:"212706";s:9:"file_path";s:0:"";}s:91:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_5_ApacheDirectoryStudio.png";a:6:{s:8:"file_url";s:91:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_5_ApacheDirectoryStudio.png";s:5:"width";s:3:"918";s:6:"height";s:3:"684";s:4:"type";s:5:"image";s:4:"area";s:6:"627912";s:9:"file_path";s:0:"";}s:77:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_6_dsktune.png";a:6:{s:8:"file_url";s:77:"http://dl.dropbox.com/u/2961879/blog20100812_centosds/CentOS-DS_6_dsktune.png";s:5:"width";s:3:"515";s:6:"height";s:3:"279";s:4:"type";s:5:"image";s:4:"area";s:6:"143685";s:9:"file_path";s:0:"";}}s:6:"videos";a:0:{}s:11:"image_count";s:1:"6";s:6:"author";s:6:"578869";s:7:"blog_id";s:7:"2005905";s:9:"mod_stamp";s:19:"2010-08-13
    10:44:13";}
  _oembed_e3ef4b7ed1392d9c2f06f326014f8be5: "{{unknown}}"
  _oembed_27baf699b3c11af751a6a31df4dae771: "{{unknown}}"
  _oembed_30f23cf6edd6429afc99585328b61a50: "{{unknown}}"
  _oembed_cfa5371d4939449b27f3bad3dcf4d8bc: "{{unknown}}"
  _oembed_44d8efbf4a9c9911a25fccd90cc70df6: "{{unknown}}"
  _oembed_3661c6e35d1b0a43aa6c9c2319b798f6: "{{unknown}}"
  _oembed_5abe03ab8b331129688ba4964289bc44: "{{unknown}}"
  _oembed_8692f45a78bb4a5953e9c4a960d07120: "{{unknown}}"
  _oembed_91c629bd0b6760eecd7ffde732e8d1b5: "{{unknown}}"
  _oembed_5475a91c065343b444e5458080daaed8: "{{unknown}}"
  _oembed_8e6271d207009aded302e303fc65095c: "{{unknown}}"
  _oembed_5bb28cfe6ce39da4e42bec074d9473c5: "{{unknown}}"
  _oembed_6bd38f0b69410bf98975191918b652fe: "{{unknown}}"
  _oembed_cfa70b6cdecdfdc96b116beda72ac6fd: "{{unknown}}"
  _oembed_5770f00526d034d8be0988d95f6bd597: "{{unknown}}"
  _oembed_5d0a7a240cdc91ccbeb651d28393ea3f: "{{unknown}}"
  _oembed_851a9c69e4fc205af2fee5c735bb24a8: "{{unknown}}"
  _oembed_938b0cc21d44d693f3c8a1345dedf839: "{{unknown}}"
  _oembed_8f6be6d9314947340283426fa2ce633e: "{{unknown}}"
  _oembed_20ab489319cfd898ef9a326e1cda217c: "{{unknown}}"
  twitter_cards_summary_img_size: a:6:{i:0;i:475;i:1;i:375;i:2;i:3;i:3;s:24:"width="475"
    height="375"";s:4:"bits";i:8;s:4:"mime";s:9:"image/png";}
  _oembed_20ad839b40ccedac4c763f050789e1fd: "{{unknown}}"
  _oembed_57e712611667920a63704f65080c54f9: "{{unknown}}"
  _oembed_37155771b09a3d1bc5daf0c9a786d0c1: "{{unknown}}"
  _oembed_e4c6b2617e3c690fca4c329679d8a6d5: "{{unknown}}"
  _oembed_0ff68a6be319de769b84256d2445f1c7: "{{unknown}}"
  _oembed_52dd319978b034ebd0476302feae4cf6: "{{unknown}}"
  _oembed_a442e256c99d475f6acbee5ba3f24661: "{{unknown}}"
  _oembed_88903179c67a951d8f3bf5ccb1919a6b: "{{unknown}}"
  _oembed_5182e561a5a2144ac9cb1acb65425774: "{{unknown}}"
  _oembed_ceaf90f04585cf5703b871b7b70e2b8d: "{{unknown}}"
  _oembed_5ff53d525d51644d912d2f458499db90: "{{unknown}}"
  _oembed_91fff5ff6d1fbba43aba6bdc259d745e: "{{unknown}}"
  geo_public: '0'
  _wpas_skip_5053092: '1'
  _wpcom_is_markdown: '1'
  _oembed_7115c1aa64fe9a9f156eec00981a971d: "{{unknown}}"
  _oembed_d0270393a2650ec0e03d9338de53311e: "{{unknown}}"
  _oembed_ed018acc49d2a5df029a9b0af08962ea: "{{unknown}}"
  _oembed_69af3076f1fdab7d4ae1bada41f97e5b: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/08/12/install-directory-server-in-centos-5-5/"
---
[![]({{ site.baseurl }}/assets/blog_centos55.png)](http://holisticsecurity.files.wordpress.com/2010/08/blog_centos55.png)

  
  

  1. Download all RPM extra packages from: http://mirrors.nfsi.pt/CentOS/5.5/extras/i386
  

  

    
    
    [root@svdapp95 ~]# wget -m http://mirrors.nfsi.pt/CentOS/5.5/extras/i386

  
or

  

    
    
    [root@svdapp95 ~]# wget -m http://mirror.centos.org/centos/5.5/extras/i386

  
<!-- more -->

  
  

  1. Upload RPMs packages to you Linux box, for example to /centos-ds_rpm_tmp
  

  2.   
Verify that you have installed Java:

  

  

  

    
    
    [root@svdapp95 ~]# rpm -qa | grep jdk  
    java-1.6.0-openjdk-1.6.0.0-1.7.b09.el5

  
  

  1. Install required packages from mounted ISO images:
  

  
 _Note_ :  
  
* Append " **\--disablerepo= * \--enablerepo=c5-media install -y**" to yum command if you want install from **DVD Iso image** , for example:

  

    
    
    # yum --disablerepo=\* --enablerepo=c5-media -y install <package>

  

    
    
    [root@svdapp95 ~]# yum install cyrus-sasl-gssapi db4-utils mozldap-tools perl-Mozilla-LDAP mozldap-devel

  
[see installation output log 1](http://db.tt/iEFnwp)

  
  

  1. Install required supported packages from mounted ISO:
  

  

    
    
    # yum install xorg-x11-xauth bitstream-vera-fonts dejavu-lgc-fonts urw-fonts

  
  

  1. Install CentOS Directory Server
  

  

    
    
    [root@svdapp95 ~]# cd /centos-ds_rpm_tmp  
    [root@svdapp95 ~]# yum --nogpgcheck localinstall centos-ds-8.1.0-1.el5.centos.2.i386.rpm \
        centos-ds-base-8.1.0-0.14.el5.centos.2.i386.rpm \
        centos-ds-base-devel-8.1.0-0.14.el5.centos.2.i386.rpm \
        centos-ds-console-8.1.0-5.el5.centos.2.noarch.rpm \
        centos-ds-admin-8.1.0-9.el5.centos.1.i386.rpm \
        adminutil-1.1.8-2.el5.centos.0.i386.rpm \
        centos-admin-console-8.1.0-2.el5.centos.2.noarch.rpm \
        centos-idm-console-1.0.1-1.el5.centos.2.i386.rpm \
        idm-console-framework-1.1.3-9.el5.centos.2.noarch.rpm \
        jss-4.2.5-1.el5.centos.1.i386.rpm

[see installation output log 2](http://db.tt/6lC55l)

  1. Verify FQDN (fully qualified domain name):
    
    
    [root@svdapp95 ~]# hostname
    svdapp95-ldap
    [root@svdapp95 ~]# hostname -f
    svdapp95-ldap.ohim-pre.eu

_Note_ :  
If you have some troubles, edit /etc/hosts and update it, and that looks like this:
    
    
    127.0.0.1 svdapp95.ohim-pre.eulocalhost
    ::1 localhost6.localdomain6 localhost6

... and change your hostname to "svdapp95.ohim-pre.eu" with system-config-network command, go to DNS > Hostname and change it.

  1. Create and configure Directory Server and Administration Server instances through of **setup-ds-admin.pl** :
    
    
    [root@svdapp95 ~]# /usr/sbin/setup-ds-admin.pl

[see installation output log 3](http://db.tt/oYdawR)

## Verify installation

  1. Verify if directory service is running:
    
    
    [root@svdapp95 ~]# /etc/init.d/dirsrv status
    dirsrv svdapp95-ldap (pid 4556) is running...

  1. Verify if admin directory service is running:
    
    
    [root@svdapp95 ~]# /etc/init.d/dirsrv-admin status
    dirsrv-admin (pid 3308) is running...

  1. Verify if 389 and 9830 ports are opened:
    
    
    [root@svdapp95 ~]# netstat -tln | grep 389
    tcp        0      0 :::389                      :::*                        LISTEN
    [root@svdapp95 ~]# netstat -tln | grep 9830
    tcp        0      0 0.0.0.0:9830                0.0.0.0:*                   LISTEN
    [root@svdapp95 ~]# lsof -i:389,9830
    COMMAND    PID   USER   FD   TYPE DEVICE SIZE NODE NAME
    ns-slapd  3047 nobody    6u  IPv6  13523       TCP *:ldap (LISTEN)
    httpd.wor 3133   root    3u  IPv4  13659       TCP *:9830 (LISTEN)
    httpd.wor 3136 nobody    3u  IPv4  13659       TCP *:9830 (LISTEN)

  1. Start directory instance if It is not running:
    
    
    [root@svdapp95 ~]# /etc/init.d/dirsrv-admin start
    Starting dirsrv-admin:
                                                              [  OK  ]
    [root@svdapp95 ~]# /etc/init.d/dirsrv start svdapp95-ldap
    Starting dirsrv:
        svdapp95-ldap...                                       [  OK  ]

## Delete all files related to directory instance for installing a new instance

  1. Start all services:
    
    
    [root@svdapp95 ~]# /etc/init.d/dirsrv-admin start
    [root@svdapp95 ~]# /etc/init.d/dirsrv start svdapp95-ldap

  1. Delete all files or remove instance with **ds_remove** script:
    
    
    [root@svdapp95 ~]# rm -rf /var/lib/dirsrv/slapd-<ldap-instance-id>
    [root@svdapp95 ~]# /usr/sbin/ds_removal -s <ldap-instance-id> -w <admin-pwd> [-f]

  * In my case <ldap-instance-id> = svdapp95-ldap
  * -f: if ds_removal fails, use -f to force the removal process.
  * Each Directory Server instance service must be running for the remove script to access it.
  1. Re-run script **setup-ds-admin.pl**

## Launch admin directory console

X Server and GDM runing in the linux box is required and X client in the client side. In Windows as client can you use Xming.

  1. Open a **xterm** or console in your linux box.
* Run centos-idm-console:
    
    
    [root@svdapp95 ~]# centos-idm-console -a http://localhost:9830

User ID: admin  
Password: <your-pwd>  
Administration URL: http://localhost:9830

## Add an object in Directory Server by centos-idm-console

  1. Run Console

![]({{ site.baseurl }}/assets/centos-ds_1_run_console.png)

  1. Open Directory Server console

![]({{ site.baseurl }}/assets/centOS-ds_2_console.png)

  1. Add an entry to Directory Server

![]({{ site.baseurl }}/assets/centOS-ds_3_addusr_console.png)

## Connection to CentOS Directory Server from Apache Directory Studio

  1. Create a connection to CentOS Directory Server from Apache Directory Studio

![]({{ site.baseurl }}/assets/centOS-ds_4_ApacheDirectoryStudio.png)

  1. Browse LDAP

![]({{ site.baseurl }}/assets/centOS-ds_5_ApacheDirectoryStudio.png)

## Add an object in Directory Server by ldapmodify command
    
    
    [root@svdapp95 ~]# /usr/lib/mozldap/ldapmodify -D "cn=Alba C.,ou=People,dc=ohim-pre,dc=eu" -w liferay -f addUsr-roger.ldif

Where addUsr-roger.ldif file contains:
    
    
    dn: cn=roger,ou=People,dc=ohim-pre, dc=eu
    changetype: add
    objectClass: person
    objectClass: organizationalPerson
    objectClass: inetOrgPerson
    objectClass: top
    cn: roger
    sn: CARHUATOCTO
    facsimileTelephoneNumber: 0034 123456678
    givenName: ROGER
    mail: roger@foo-bar.info
    preferredLanguage: es
    telephoneNumber: 0034 123456678
    title: ICEfaces Programmer
    uid: 333333
    userPassword:: e1NTSEF9Q0N4SmxGRUpFZmxyVVYvbXk2cUJVV1N3QWdJbVcxbWtDbUkxdFE9P
    Q==

## Authenticate and Search an object in CentOS Directory Server

In this case, the ldap operations that can be used are "bind operation" and "search operation".  
CentOS Directory Server uses Mozilla LDAP tools — such as **ldapsearch, ldapmodify** , and **ldapdelete** — for command-line operations.  
The MozLDAP tools are installed with Directory Server and are located in the **/usr/lib/mozldap**.  
When running any LDAP command, make sure that you are using the **MozLDAP** utilities, otherwise the command will return errors.
    
    
    [root@svdapp95 ~]# /usr/lib/mozldap/ldapsearch -x -D <binddn> -w <bindpwd> -b <basedn> <searchfilter>

  * -x: performing sorting on server
  * -D: bind dn (for authentication)
  * -w: bind passwd (for authentication)
  * -b: base dn
  * <searchfilter>: RFC-2254 compliant LDAP search filter
    
    
    [root@svdapp95 ~]# /usr/lib/mozldap/ldapsearch -x -D "cn=lluis,ou=People,dc=ohim-pre,dc=eu" -w liferay -b "dc=ohim-pre,dc=eu" "objectclass=inetOrgPerson"

## Tuning CentOS Directory Server

  1. Using dsktune:
    
    
    [root@svdapp95 ~]# dsktune

![]({{ site.baseurl }}/assets/centOS-ds_6_dsktune.png)

**References**

  * CentOS Directory Server, Basic Install  
http://wiki.centos.org/HowTos/DirectoryServerSetup
* CentOS Directory Server On CentOS 5.2  
http://www.howtoforge.com/centos-directory-server-on-centos5.2

* Index of /CentOS/5.5/extras/i386/RPMS  
http://mirrors.nfsi.pt/CentOS/5.5/extras/i386/RPMS/

* Red Hat Directory Server - manual installation and configuration  
http://www.redhat.com/docs/manuals/dir-server/8.1/install/index.html

Bye.
