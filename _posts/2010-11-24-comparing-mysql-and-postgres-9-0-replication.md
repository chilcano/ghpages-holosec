---
layout: post
title: Comparing MySQL and Postgres 9.0 Replication
date: 2010-11-24 23:34:15.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Security
tags: []
meta:
  _edit_last: '578869'
  _wp_old_slug: ''
  _wpas_done_twitter: '1'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/11/24/comparing-mysql-and-postgres-9-0-replication/"
---
Comparing MySQL and Postgres 9.0 Replication  
  
By By Robin Schumacher and Gary Carter, EnterpriseDB  
  
TheServerSide.com

  
## Comparing MySQL and Postgres 9.0 Replication

  
 _By Robin Schumacher and Gary Carter, www.enterprisedb.com_

  
> Replication is one of the most popular features used in RDBMS’s today. Replication is used for disaster recovery purposes (i.e. backup or warm stand-by servers), reporting systems where query activity is offloaded onto another machine to conserve resources on the transactional server, and scale-out architectures that use sharding or other methods to increase overall query performance and data throughput.
> 
>   
> 
> 
> Replication is not restricted to only the major proprietary databases; open source databases such as MySQL and PostgreSQL also offer replication as a feature. While MySQL has offered built-in replication for a number of years, PostgreSQL replication used to be accomplished via community software that was an add-on to the core Postgres Server. That all changed with the release of version 9.0 of PostgreSQL, which now offers built-in streaming replication that is based on its proven write ahead log technology.
> 
>   
> 
> 
> With the two most popular open source databases now providing built-in replication, questions are being asked about how they differ in their replication technologies. What follows is a brief overview of both MySQL and PostgreSQL replication, with a brief compare and contrast of the implementations being performed immediately afterwards.
> 
>   
> 
> 
> ![mysql replication.jpg]({{ site.baseurl }}/assets/postgres01.jpg)
> 
>   
> 
> 
> ![]({{ site.baseurl }}/assets/postgres02.jpg)

  
 _via[A Quick Comparison of MySQL and Postgres 9.0 Replication](http://www.theserverside.com/feature/Comparing-MySQL-and-Postgres-90-Replication)._

  
