---
layout:     post
title:      'Liferay Portal LDAP Authentication with Penrose Server'
date:       2010-12-22 11:11:11
categories: ['Portal', 'Security']
tags:       ['Authentication', 'LDAP', 'Penrose', 'Virtual Directory']
status:     publish 
permalink:  "/2010/12/22/authentication-penrose-directory-liferay/"
---

We explained in a previous post [Identity Management (IdM) in Portal, ECM and BPM Projects](http://holisticsecurity.wordpress.com/2010/11/17/identity-management-portal-ecm-bpm-projects/) how important is having a Corporate Directory (LDAP server) that serves as a repository for different types of identities and roles that will require for our business application in authentication and authorization processes. 

![](/assets/blog20101203_virtualdirectory_portal/0_vds_penrose_server.png)  
_Fig 0\. Penrose Server, a Java free open source Virtual Directory_

We also explained the importance of using a Virtual Directory as a natural evolution of the classic Directory and Meta Directory. We also highlight its functionality, scalability and ability to integrate different sources of identity information regardless of the type of source, may be other LDAP servers, Database servers, even Webservices. Well, in this post will explain how to deploy and configure Penrose Server (Virtual Directory free / open source) to store user identity information from a MySQL table so we can use them as users of Liferay Portal without having to program or modify any adapter or hook Liferay. Penrose Server has, by default, OpenDS as backend, then any LDAP objectClass that exists in OpenDS exists in Penrose Server. Said that, come on with installation.

## 1. Pre-requisites

**1. Download Java Development Kit (>=1.5)**, for example `./jdk-1_5_0_17-linux-amd64.bin` 
 
**2. CentOS already has OpenJDK. You can verify it:**

```sh
[root@directorysrv1 /]# java -version
java version "1.6.0"
OpenJDK  Runtime Environment (build 1.6.0-b09)
OpenJDK Client VM (build 1.6.0-b09, mixed mode)
```

## 2. Installing Penrose Server (Virtual Directory)

A virtual directory maps information from disparate data sources, such as LDAP services and Database, into a single location for users to access. 

**1. Download the RPM** from here [http://penrose.redhat.com/display/PENROSE20/Penrose+2.0+Release](http://penrose.redhat.com/display/PENROSE20/Penrose+2.0+Release)  

**2. Install the package(s):**

```sh
[root@directorysrv1 tempo]# rpm -i vd-server-2.0-1.i386.rpm
VD Server 2.0 has been installed in /opt/vd-server-2.0.
```

**3. Make sure that JAVA is configured:** Edit `/opt/vd-server-2.0/vd.conf` file.

```sh
[root@directorysrv1 /]# vim /opt/vd-server-2.0/etc/vd.conf
```

Add the `JAVA_HOME` variable, pointing to your JDK. For example, in CentOS is:

```sh
JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk
```

After editing the vd.conf file, copy it into the host's /etc directory.

```sh
[root@directorysrv1 /]# cp /opt/vd-server-2.0/etc/vd.conf /etc
```

**4. Run a configuration script** to reset the server hostname, give the admin username and password, and set the port numbers and other information for the associated LDAP and JMX services of the Virtual Directory. For example:

```sh
[root@directorysrv1 /]# cd /opt/vd-server-2.0/bin/
[root@directorysrv1 bin]# ./vd-config.sh
Configuring VD Server:
----------------------

Hostname [directorysrv1.intix.info]:
Root DN [uid=admin,ou=system]:
Root Password [*****]:
User account [root]:
Group account [root]: 

Configuring JMX Service:
------------------------

RMI Port [1099]:
RMI Transport Port [40888]: 

Configuring OpenDS Service:
---------------------------

LDAP Enabled [true]:
LDAP Port [10389]:
Secure LDAP Enabled [false]:
Secure LDAP Port [10636]:
SSL Certificate Name [server-cert]:
Key Store Type (JKS/PKCS12) [JKS]:
Key Store File [config/keystore]:
Key Store PIN File [config/keystore.pin]: 

[root@directorysrv1 bin]#
```

## 3. Installing additional libraries

You can install on Virtual Directory Server libraries (jar files) to extend functionalities. These libraries cover a range of different functions, including JDBC drivers, custom adapters, custom modules, and other third party libraries.  

**1. Copy the JAR files** into the `/opt/vd-server-2.0/lib/ext/` directory; for example:

```sh
[root@directorysrv1 /]# cp /export/myjdbc.jar /opt/vd-server-2.0/lib/ext/myjdbc.jar
```

**2. Is necessary restart** the Virtual Directory Server.

## 4. Uninstalling Penrose Server

The Virtual Directory Server packages can be uninstalled using package management tools, the same as used to install it. To remove the Server, use the -e option with rpm:

```sh
[root@directorysrv1 bin]# rpm -ev vd-server-2.0-1.i386.rpm
```

## 5. Starting Penrose Server

**1. Virtual Directory is started by running a shell script** `/opt/vd-server-2.0/bin/vd-server.sh`. For example:

```sh
[root@directorysrv1 /]# cd /opt/vd-server-2.0/bin
[root@directorysrv1 bin]# ./vd-server.sh
[12/02/2010 01:40:11.693] VD Server is ready.
```

**2. To stop the server**, simply close the script.

## 6. Starting Penrose Server as a linux service

The Virtual Directory can be stopped, started, and restarted using system tools on CentOS. Init scripts are included with the configuration files with Virtual Directory Server.  

**1. Log into the Virtual Directory Server** host machine as root user.  
**2. Open the Virtual Directory init script directory**.

```sh
[root@directorysrv1 /]# cd /opt/vd-server-2.0/etc/init.d
```

**3. Edit the `/opt/vd-server-2.0/etc/init.d/vd-server` script** so that the Virtual Directory Server home and script locations are correct. For example:

```sh
VD_SERVER_HOME=/opt/vd-server-2.0
VD_SERVER_SCRIPT=$VD_SERVER_HOME/bin/vd-server.sh
```

**4. Copy the init file to the `/etc/init.d/` directory**.

```sh[root@directorysrv1 /]# cp /opt/vd-server-2.0/etc/init.d/vd-server /etc/init.d/```

**5. Make the init script executable**.

```sh
[root@directorysrv1 /]# chmod +x /etc/init.d/vd-server
```

**6. Test the new Virtual Directory service**.

```sh
[root@directorysrv1 /]# service vd-server start
Starting vd-server:                                        [  OK  ]
[root@directorysrv1 init.d]# [12/03/2010 10:24:31.782] VD Server is ready.
[root@directorysrv1 init.d]#
```

After setting Virtual Directory Server up as a service, it can be managed using the service on CentOS:

```sh
[root@directorysrv1 /]# service vd-server {start|stop|restart}
```

## 7. Connect to Penrose Server LDAP interface

It is necessary download and install any LDAP client, for example, Apache Directory Studio.  

**1. Execute any LDAP client**, for example [Apache Directory Studio](http://directory.apache.org/studio/).  

**2. Add new LDAP connection with these values**:

*   hostname or ip : directorysrv1 (or directorysrv1.intix.info)
*   port : 10389
*   user credentials: uid=admin,ou=system
*   password: secret

**3. Now you can browse on existing LDAP entries or to create a new partition (LDAP tree)**. 

![](/assets/blog20101203_virtualdirectory_portal/1_vds_add_conn.png "Fig. 1\. Apache Directory Studio - Add LDAP connection"){:width="350"} ![](/assets/blog20101203_virtualdirectory_portal/2_vds_add_conn_auth.png "Fig. 2\. Apache Directory Studio - Add LDAP conn, authentication"){:width="350"}  

![](/assets/blog20101203_virtualdirectory_portal/3_vds_explore_ldap_tree.png "Fig. 3\. Apache Directory Studio - Explore LDAP tree"){:width="500"} 

## 8. Create a new virtual LDAP tree binding MySQL Server

**1. Install MySQL Server**, in my case I will install MySQL Server in the same CentOS host where Virtual Directory has already installed. It is just for testing purposes.

```sh
[root@directorysrv1 /]# yum --disablerepo=\* --enablerepo=c5-media -y install mysql-server
Loaded plugins: fastestmirror
[...]
Running Transaction
  Installing     : perl-DBD-MySQL                       1/2
  Installing     : mysql-server                         2/2 

Installed:
  mysql-server.i386 0:5.0.77-4.el5_4.2                                                                                        

Dependency Installed:
  perl-DBD-MySQL.i386 0:3.0007-2.el5                                                                                          

Complete!
[root@directorysrv1 /]#
```

**2. Start MySQL Server as service**.

```sh
[root@directorysrv1 init.d]# service mysqld start
Initializing MySQL database:  Installing MySQL system tables...
[...]
                                                           [  OK  ]
Starting MySQL:                                            [  OK  ]
[root@directorysrv1 init.d]#
```

**3. Update root password in MySQL Server**:

```sh
[root@directorysrv1 /]# mysqladmin -u root password "demodemo"
```

**4. Enable remote access to MySQL** for root user on all database and tables. Remote access is necessary when you want to connect to your MySQL from a different computer.

```sh
[root@directorysrv1 /]# mysql -u root -p
Enter password:
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 5
Server version: 5.0.77 Source distribution

Type 'help;' or '\h' for help. Type '\c' to clear the buffer.

mysql> GRANT ALL ON *.* TO root@'%' IDENTIFIED BY 'demodemo';
Query OK, 0 rows affected (0.00 sec)

mysql> quit;
Bye
```

**5. Create a new Database and tables that will store identities (user credentials)**. 

These user credentials stored in tables will be accessible as a LDAP tree by the Virtual Directory Server. This new LDAP tree can be used to configure the login and authentication process required for different applications/products such as Liferay, Alfresco, Intalio, etc.. In this example we will explain how to do it for Liferay Portal. We will use an existing `MySQL Database (Employee DB)` for testing purposes. Download existing DB from here ([http://datacharmer.blogspot.com/2008/07/dont-guess-test-sample-database-with.html](http://datacharmer.blogspot.com/2008/07/dont-guess-test-sample-database-with.html)), then install it in our current MySQL server: 

`-- unzip DB`

```sh
[root@directorysrv1 temp]# tar -xjf /temp/employees_db-full-1.0.4.tar.bz2
[root@directorysrv1 temp]# cd /temp/employees_db/
```

`-- create schema and load data`

```sh[root@directorysrv1 temp]#  mysql -u root -p -t < employees.sql```

`-- test integrity of loaded data with SHA1`

```sh
[root@directorysrv1 employees_db]# time mysql -u root -p -t < test_employees_sha.sql
Enter password:
+----------------------+
| INFO                 |
+----------------------+
| TESTING INSTALLATION |
+----------------------+
+--------------+------------------+------------------------------------------+
| table_name   | expected_records | expected_crc                             |
+--------------+------------------+------------------------------------------+
| employees    |           300024 | 4d4aa689914d8fd41db7e45c2168e7dcb9697359 |
| departments  |                9 | 4b315afa0e35ca6649df897b958345bcb3d2b764 |
| dept_manager |               24 | 9687a7d6f93ca8847388a42a6d8d93982a841c6c |
| dept_emp     |           331603 | d95ab9fe07df0865f592574b3b33b9c741d9fd1b |
| titles       |           443308 | d12d5f746b88f07e69b9e36675b6067abb01b60e |
| salaries     |          2844047 | b5a1785c27d75e33a4173aaa22ccf41ebd7d4a9f |
+--------------+------------------+------------------------------------------+
+--------------+------------------+------------------------------------------+
| table_name   | found_records    | found_crc                                |
+--------------+------------------+------------------------------------------+
| employees    |           300024 | 4d4aa689914d8fd41db7e45c2168e7dcb9697359 |
| departments  |                9 | 4b315afa0e35ca6649df897b958345bcb3d2b764 |
| dept_manager |               24 | 9687a7d6f93ca8847388a42a6d8d93982a841c6c |
| dept_emp     |           331603 | d95ab9fe07df0865f592574b3b33b9c741d9fd1b |
| titles       |           443308 | d12d5f746b88f07e69b9e36675b6067abb01b60e |
| salaries     |          2844047 | b5a1785c27d75e33a4173aaa22ccf41ebd7d4a9f |
+--------------+------------------+------------------------------------------+
+--------------+---------------+-----------+
| table_name   | records_match | crc_match |
+--------------+---------------+-----------+
| employees    | OK            | ok        |
| departments  | OK            | ok        |
| dept_manager | OK            | ok        |
| dept_emp     | OK            | ok        |
| titles       | OK            | ok        |
| salaries     | OK            | ok        |
+--------------+---------------+-----------+

real    0m59.756s
user    0m0.011s
sys     0m0.057s
```

The final Employees DB schema/model that we will use to create LDAP tree is the following:  ![](/assets/blog20101203_virtualdirectory_portal/5_vds_employees_db_model.png "Fig. 4\. Employees MySQL schema")  

**6. Download MySQL JDBC library and copy to Virtual Directory Server**, in this case to `/opt/vd-server-2.0/lib/ext/` folder.

```sh
[root@directorysrv1 temp]# cp mysql-connector-java-5.1.13-bin.jar /opt/vd-server-2.0/lib/ext/
```

**7. Create a new Partition in our Virtual Directory Server**. A Partition in our Virtual Directory contains all relationships beetwen:

*   Connections: data servers such as DB servers or other LDAP servers
*   Sources: applications such as for Liferay, for Alfresco, for Intalio, for Windows Authentication, ...
*   Identities: individual entries and
*   Mappings: links between entities.

It is necessary to add new namingContexts to existing **Root DSE** in `/opt/vd-server-2.0/conf/directory.xml`. This file will be like: [<PENROSE_HOME>/conf/directory.xml](/assets/blog20101203_virtualdirectory_portal/directory.xml)  

**8. Map tables-fields with attributes of the new LDAP tree in our Virtual Directory**.

*   Liferay Portal domain: new LDAP tree/domain, in this example is "@intix.info".
*   Liferay Portal Users: the employees table of MySQL DB maps to inetOrgPerson (or organizationalPerson or other similar) entries in LDAP.
*   Liferay Portal Groups: the departments table of MySQL DB maps to organizationalUnit (or other similar) entries in LDAP, in this example we will not use Groups.
*   Additional fields required for Liferay Portal such email, title will be obtained by joining fields values such employees.first_name, employees.last_name with "@intix.info", and title will be obtained of titles.title and so on. In this example, "title" LDAP attribute of inetOrgPerson will be compose with differents values of the Employees table.
*   The password to log into Liferay Portal will be stored as SHA1 in a new field created in table Employees. For our convenience, all user passwords will be equal to "function_sha1('test') =qUqP5cyxm6YcTAhz05Hph5gvu9M=".
*   Only allow access to Liferay Portal to users (Employees) hired in August 1999\. In this case we will use this sentence: SELECT emp_no FROM employees WHERE hire_date BETWEEN '1999-08-01' AND '1999-08-31' . This constrain will be a filter in our Virtual Directory Partition.

For your convenience, I include all files that are part of the new Partition (connections, sources, mapping and constrains) created into Virtual Directory. You can download it from here: [Penrose Server partition intix.info](/assets/blog20101203_virtualdirectory_portal/intix_info_liferay.zip)  

**9. Create new partition ("intix_info_liferay" folder) in Virtual Directory**.

```sh
[root@directorysrv1 /]# mkdir /opt/vd-server-2.0/partitions/intix_info_liferay/DIR-INF
```

**10. Copy all files** (connections.xml, directory.xml, mappings.xml, modules.xml, partition.xml and sources.xml) to `/opt/vd-server-2.0/partitons/intix_info_liferay/DIR-INF`  

**11. Restart Virtual Directory**:

```sh
[root@directorysrv1 /]# service vd-server restart
```

**12. Browse into the new LDAP tree created (partition) in the Virtual Directory binding the new database created in MySQL Server**. 

![](/assets/blog20101203_virtualdirectory_portal/6_vds_browsing_new_ldap_tree.png "Fig. 5\. Browsing the new LDAP tree of Penrose Server"){:width="500"}

## 9. Configure LDAP Authentication in Liferay Portal

Now, We have a LDAP server (Penrose) with user credentials (identities) loaded. The next step is to configure Liferay Portal with these LDAP tree to do log in to Portal. If you have already a Liferay installed, you have to make sure that Liferay can resolve the Virtual Directory. To check it:

```sh
[chilcano@lfry01 /]# ping directorysrv1
```

Then, from Liferay > Control Panel, configure LDAP authentication with Email Address as credentials to login. 

 ![](/assets/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_0.png "Fig 6\. Liferay LDAP AuthN: Email Address to access to Portal"){:width="300"}  
 
 Add and configure a new LDAP server (Penrose Server) in Liferay. 

 ![](/assets/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_1.png "Fig 7\. Liferay LDAP AuthN: Add Penrose Server as LDAP "){:width="350"}. ![](/assets/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_2.png "Fig 8\. Liferay LDAP AuthN: User and password to connect to Penrose Server"){:width="350"}


 ![](/assets/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_3.png "Fig 8\. Liferay LDAP AuthN: Map and filter user credentials"){:width="350"}. ![](/assets/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_4.png "Fig 9\. Liferay LDAP AuthN: Map group and Export with empty values"){:width="350"} 


## 10. Testing LDAP Authentication from Liferay Portal

Now you can use any user credential (any value of Employee table) to login Liferay. 

 ![](/assets/blog20101203_virtualdirectory_portal/8_vds_liferay_login.png "Fig 9\. Liferay Portal login"){:width="300"} ![](/assets/blog20101203_virtualdirectory_portal/9_vds_liferay_welcomepage.png "Fig 10\. Login successfully and welcome page in Liferay"){:width="300"}

 ![](/assets/blog20101203_virtualdirectory_portal/10_vds_liferay_myaccount.png "Fig 11\. Liferay My Account (User details)"){:width="300"}

## 11. Testing LDAP Authentication from Apache Directory Studio

The user "aamod.wroclawski@intix.info" with password "test" can be verified. You can do from apache Directory Studio. Open Apache Directory Studio, connect to Penrose Server, then go to entry "uid=480838,ou=Employees,dc=intix,dc=info", click on password attribute, then open a windows where you can verify password. Follow the figures: 

 ![](/assets/blog20101203_virtualdirectory_portal/11_vds_verify_account_apache_directory1.png "Fig 12\. Explore attributes of authenticated User from Apache Directory Studio"){:width="350"} ![](/assets/blog20101203_virtualdirectory_portal/11_vds_verify_account_apache_directory2.png "Fig 13\. Verify password (SHA1) from Apache Directory Studio"){:width="350"}  

If you are planning to install other product or application in your organization, you could create a new LDAP tree under Root DSE `intix.info` as `ou=Alfresco Users, dc=intix, dc=info` and to select or filter existing users from Employee table. Well, Penrose Server (Virtual Directory) has several applications and is easy adaptable to any Security User Schema. Any questions, do not hesitate to emailme. Bye.

### References

* [Remote access to MySQL](http://www.cyberciti.biz/tips/how-do-i-enable-remote-access-to-mysql-database-server.html)
* [Sample database with test suite for MySQL Server](http://datacharmer.blogspot.com/2008/07/dont-guess-test-sample-database-with.html)