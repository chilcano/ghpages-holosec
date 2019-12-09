---
layout:     post
title:      'Install Directory Server in CentOS 5.5'
date:       2010-08-12 14:13:06
categories: ['Linux', 'Portal', 'Security']
tags:       ['LDAP']
status:     publish 
permalink:  "/2010/08/12/install-directory-server-in-centos-5-5/"
---
[![]({{ site.baseurl }}/assets/blog_centos55.png)](http://holisticsecurity.files.wordpress.com/2010/08/blog_centos55.png)
1. Download all RPM extra packages from: http://mirrors.nfsi.pt/CentOS/5.5/extras/i386
    [root@svdapp95 ~]

# wget -m http://mirrors.nfsi.pt/CentOS/5.5/extras/i386
or
    [root@svdapp95 ~]

# wget -m http://mirror.centos.org/centos/5.5/extras/i386

<!-- more -->
1. Upload RPMs packages to you Linux box, for example to /centos-ds_rpm_tmp
2.   
Verify that you have installed Java:
    [root@svdapp95 ~]

# rpm -qa | grep jdk  
    java-1.6.0-openjdk-1.6.0.0-1.7.b09.el5
1. Install required packages from mounted ISO images:
 _Note_ :  
* Append " **\--disablerepo= * \--enablerepo=c5-media install -y**" to yum command if you want install from **DVD Iso image** , for example:

# yum --disablerepo=\* --enablerepo=c5-media -y install <package>
    [root@svdapp95 ~]

# yum install cyrus-sasl-gssapi db4-utils mozldap-tools perl-Mozilla-LDAP mozldap-devel

[see installation output log 1](http://db.tt/iEFnwp)
1. Install required supported packages from mounted ISO:

# yum install xorg-x11-xauth bitstream-vera-fonts dejavu-lgc-fonts urw-fonts
1. Install CentOS Directory Server
    [root@svdapp95 ~]

# cd /centos-ds_rpm_tmp  
    [root@svdapp95 ~]

# yum --nogpgcheck localinstall centos-ds-8.1.0-1.el5.centos.2.i386.rpm \
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
    [root@svdapp95 ~]

# hostname
    svdapp95-ldap
    [root@svdapp95 ~]

# hostname -f
    svdapp95-ldap.ohim-pre.eu
_Note_ :  
If you have some troubles, edit /etc/hosts and update it, and that looks like this:
    127.0.0.1 svdapp95.ohim-pre.eulocalhost
    ::1 localhost6.localdomain6 localhost6
... and change your hostname to "svdapp95.ohim-pre.eu" with system-config-network command, go to DNS > Hostname and change it.
1. Create and configure Directory Server and Administration Server instances through of **setup-ds-admin.pl** :
    [root@svdapp95 ~]

# /usr/sbin/setup-ds-admin.pl

[see installation output log 3](http://db.tt/oYdawR)

## Verify installation
1. Verify if directory service is running:
    [root@svdapp95 ~]

# /etc/init.d/dirsrv status
    dirsrv svdapp95-ldap (pid 4556) is running...
1. Verify if admin directory service is running:
    [root@svdapp95 ~]

# /etc/init.d/dirsrv-admin status
    dirsrv-admin (pid 3308) is running...
1. Verify if 389 and 9830 ports are opened:
    [root@svdapp95 ~]

# netstat -tln | grep 389
    tcp        0      0 :::389                      :::*                        LISTEN
    [root@svdapp95 ~]

# netstat -tln | grep 9830
    tcp        0      0 0.0.0.0:9830                0.0.0.0:*                   LISTEN
    [root@svdapp95 ~]

# lsof -i:389,9830
    COMMAND    PID   USER   FD   TYPE DEVICE SIZE NODE NAME
    ns-slapd  3047 nobody    6u  IPv6  13523       TCP *:ldap (LISTEN)
    httpd.wor 3133   root    3u  IPv4  13659       TCP *:9830 (LISTEN)
    httpd.wor 3136 nobody    3u  IPv4  13659       TCP *:9830 (LISTEN)
1. Start directory instance if It is not running:
    [root@svdapp95 ~]

# /etc/init.d/dirsrv-admin start
    Starting dirsrv-admin:
                                                              [  OK  ]
    [root@svdapp95 ~]

# /etc/init.d/dirsrv start svdapp95-ldap
    Starting dirsrv:
        svdapp95-ldap...                                       [  OK  ]

## Delete all files related to directory instance for installing a new instance
1. Start all services:
    [root@svdapp95 ~]

# /etc/init.d/dirsrv-admin start
    [root@svdapp95 ~]

# /etc/init.d/dirsrv start svdapp95-ldap
1. Delete all files or remove instance with **ds_remove** script:
    [root@svdapp95 ~]

# rm -rf /var/lib/dirsrv/slapd-<ldap-instance-id>
    [root@svdapp95 ~]

# /usr/sbin/ds_removal -s <ldap-instance-id> -w <admin-pwd> [-f]
* In my case <ldap-instance-id> = svdapp95-ldap
* -f: if ds_removal fails, use -f to force the removal process.
* Each Directory Server instance service must be running for the remove script to access it.
1. Re-run script **setup-ds-admin.pl**

## Launch admin directory console
X Server and GDM runing in the linux box is required and X client in the client side. In Windows as client can you use Xming.
1. Open a **xterm** or console in your linux box.
* Run centos-idm-console:
    [root@svdapp95 ~]

# centos-idm-console -a http://localhost:9830
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
    [root@svdapp95 ~]

# /usr/lib/mozldap/ldapmodify -D "cn=Alba C.,ou=People,dc=ohim-pre,dc=eu" -w liferay -f addUsr-roger.ldif
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
    [root@svdapp95 ~]

# /usr/lib/mozldap/ldapsearch -x -D <binddn> -w <bindpwd> -b <basedn> <searchfilter>
* -x: performing sorting on server
* -D: bind dn (for authentication)
* -w: bind passwd (for authentication)
* -b: base dn
* <searchfilter>: RFC-2254 compliant LDAP search filter
    [root@svdapp95 ~]

# /usr/lib/mozldap/ldapsearch -x -D "cn=lluis,ou=People,dc=ohim-pre,dc=eu" -w liferay -b "dc=ohim-pre,dc=eu" "objectclass=inetOrgPerson"


## Tuning CentOS Directory Server
1. Using dsktune:
    [root@svdapp95 ~]

# dsktune

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
