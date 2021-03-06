---
layout:     post
title:      'Install Mirth Connect 2.1 on Ubuntu 10.10 with MySQL Server'
date:       2011-05-09 16:02:49
categories: ['EIP', 'ESB', 'Health Care', 'Open Source', 'SOA']
tags:       ['HL7', 'Mirth Connect', 'Mule']
status:     publish 
permalink:  "/2011/05/09/install-mirth-connect-ubuntu-mysql/"
---
![Mirth Connect, SOA, ESB, HL7](/assets/blog20110509_mirth_connect/mirthconnect_logowide.png)

Mirth Connect is a Service Oriented Integration Platform based on Mule ESB. Mirth Connect is specifically designed for HL7 (Health Level Seven - set of standards for the health messages exchanging) message exchange.
Mirth Connect as a SOA Platform provides tools for developing, testing, deploying, and monitoring interfaces (channel for the exchange of HL7 messages).

![Mirth Connect Architecture](/assets/blog20110509_mirth_connect/mirthconnect_architecture.png)

Mirth Connect is a project Open Source with Mozilla Public License 1.1 (MPL 1.1) and can be downloaded from here [http://www.mirthcorp.com/community/downloads](http://www.mirthcorp.com/community/downloads)
Well, we will explain how to install on Ubuntu and how to switch Derby database server to MySQL server.

<!-- more -->

## Steps

### 1. Install Java

```sh
amawta@yachaywasi:~$ sudo apt-get install openjdk-6-jdk  
```

### 2. Install Mirth Connect

1. Download and setup execution permissions:

```sh
amawta@yachaywasi:~$ chmod +x mirthconnect-2.1.0.5389.b671-unix.sh  
amawta@yachaywasi:~$ sudo ./mirthconnect-2.1.0.5389.b671-unix.sh  
```

2\. Verify Mirth Connect installation:
Go to http://localhost:8080 where you can access to Mirth Connect Server via Java App Admin console.  
The administrator user and password are admin and admin, change it!.  
If you can not connect to server, then start it.
3\. Start Mirth Connect from Manager (mcmanager), Server (mcserver) or as Service (mcservice).

```sh
amawta@yachaywasi:/opt/mirthconnect$ sudo ./mcservice start  
```

verify again Mirth Connect is running.

### 3. Switch from Derby database to MySQL server

1. Install MySQL:

  ```sh
  amawta@yachaywasi:~$ sudo apt-get install mysql-server mysql-client  
  ```
  
  and verify if MySQL is running:
  
  ```sh
  amawta@yachaywasi:~$ netstat -tulpan  
  ```
  
  or starting:
  
  ```sh
  amawta@yachaywasi:~$ sudo service mysql start  
  ```
  
  after, connect to MySQL:
  
  ```sh
  amawta@yachaywasi:~$ mysql -u root -p  
  ```

2. Create an empty database and an user for Mirth Connect:

  ```sh
  CREATE DATABASE mirthdb DEFAULT CHARACTER SET utf8;  
  GRANT ALL ON mirthdb.* TO mirthuser@'localhost' IDENTIFIED BY 'demodemo' WITH GRANT OPTION;  
  GRANT ALL ON mirthdb.* TO mirthuser@'%' IDENTIFIED BY 'demodemo' WITH GRANT OPTION;  
  ```
  
  If you want to connect from other PC different to localhost, then edit /etc/mysql/my.cnf  
  and comment the line "bind-address = 127.0.0.1", after re-start MySQL.

3. Stop Mirth Connect:

  ```sh
  amawta@yachaywasi:/opt/mirthconnect$ sudo ./mcservice stop  
  ```

4. Edit the `%MIRTH_HOME%/conf/mirth.properties` file and set the “database” property to the database of your choice, for example “mysql” (no quotes).  
   
  Also, set the “database.url”, “database.username”, and “database.password” properties. In this case are:
  
  ```sh
  database = mysql  
  database.url =jdbc:mysql://localhost:3306/mirthdb  
  database.username = mirthuser  
  database.password = demodemo  
  ```

5. Restart the Mirth Connect Server and check logs.  

  Mirth Connect server will create a new database schema:
  
  ```sh
  amawta@yachaywasi:/opt/mirthconnect$ sudo ./mcserver  
  INFO 2011-05-09 16:52:13,769 [Thread-1] com.mirth.connect.server.Mirth: Mirth Connect 2.1.0.5389 (Built on April 25, 2011) server successfully started.  
  INFO 2011-05-09 16:52:13,771 [Thread-1] com.mirth.connect.server.Mirth: This product was developed by Mirth Corporation (http://www.mirthcorp.com) and its contributors (c)2005-2011.  
  INFO 2011-05-09 16:52:13,772 [Thread-1] com.mirth.connect.server.Mirth: Running OpenJDK Server VM 1.6.0_20 on Linux (2.6.35-22-generic, i386), mysql, with charset UTF-8.  
  ```
  
  To verify new schema do the following:
  
  ```sh
  amawta@yachaywasi:~$ mysqlshow -u mirthuser -p mirthdb  
  Enter password:  
  Database: mirthdb  
  +--------------------+  
  | Tables |  
  +--------------------+  
  | ALERT |  
  | ALERT_EMAIL |  
  | ATTACHMENT |  
  | CHANNEL |  
  | CHANNEL_ALERT |  
  | CHANNEL_STATISTICS |  
  | CODE_TEMPLATE |  
  | CONFIGURATION |  
  | ENCRYPTION_KEY |  
  | EVENT |  
  | MESSAGE |  
  | PERSON |  
  | SCHEMA_INFO |  
  | SCRIPT |  
  | TEMPLATE |  
  +--------------------+  
  ```

6. Now you could connect to Mirth, go to `http://localhost:8080`, start Mirth Connect Administrator, the default user and password will be `admin/admin`, change it again.

  ![Mirth Connect Administration](/assets/blog20110509_mirth_connect/MirthConnectAdminLogin2.png)


Regards.
