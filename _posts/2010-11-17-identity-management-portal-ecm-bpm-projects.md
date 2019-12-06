---
layout: post
title: Identity Management (IdM) in Portal, ECM and BPM Projects
date: 2010-11-17 03:40:58.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- BPM
- ECM
- Security
tags:
- FOSS
- IdM
- Portal
- SSO
meta:
  _edit_last: '578869'
  _wpas_done_twitter: '1'
  _oembed_db5a9753dbd73f6c7cdfecbdce16363a: "{{unknown}}"
  _oembed_06eebf68bd096dab6c92b5f9e26979c3: "{{unknown}}"
  _oembed_d5098cef5b364ee6cc73529ed55bfd3f: "{{unknown}}"
  tagazine-media: a:7:{s:7:"primary";s:87:"http://dl.dropbox.com/u/2961879/blog20101117_ids_vd/2.virtualdirectory_idm_products.png";s:6:"images";a:2:{s:83:"http://dl.dropbox.com/u/2961879/blog20101117_ids_vd/1.virtualdirectory_services.png";a:6:{s:8:"file_url";s:83:"http://dl.dropbox.com/u/2961879/blog20101117_ids_vd/1.virtualdirectory_services.png";s:5:"width";s:3:"802";s:6:"height";s:3:"497";s:4:"type";s:5:"image";s:4:"area";s:6:"398594";s:9:"file_path";s:0:"";}s:87:"http://dl.dropbox.com/u/2961879/blog20101117_ids_vd/2.virtualdirectory_idm_products.png";a:6:{s:8:"file_url";s:87:"http://dl.dropbox.com/u/2961879/blog20101117_ids_vd/2.virtualdirectory_idm_products.png";s:5:"width";s:4:"1040";s:6:"height";s:3:"935";s:4:"type";s:5:"image";s:4:"area";s:6:"972400";s:9:"file_path";s:0:"";}}s:6:"videos";a:0:{}s:11:"image_count";s:1:"2";s:6:"author";s:6:"578869";s:7:"blog_id";s:7:"2005905";s:9:"mod_stamp";s:19:"2010-11-17
    02:40:58";}
  _oembed_ccbc1e72d070ab29020e961bf5b058b2: "{{unknown}}"
  _oembed_8cc4786932e1b7c30347a84044dde057: "{{unknown}}"
  _oembed_4b762c254ab4891f8e8a92b6218e01cf: "{{unknown}}"
  _oembed_e9b6b5609b1c06be96620270007694f1: "{{unknown}}"
  _oembed_ef2d8ff31d88eab7f167bab341ea5e7b: "{{unknown}}"
  _oembed_e28235c05d7087a24559e620db156b4f: "{{unknown}}"
  _oembed_da68b7f98b4c10ba264413483c778040: "{{unknown}}"
  _oembed_1ce3fc03ed2982ac70eb787f0b9ce840: "{{unknown}}"
  _oembed_8d227053958bc848c77312c6eb4bf1eb: "{{unknown}}"
  _oembed_bd477f8caa3e486f2b50da93cd39d660: "{{unknown}}"
  _oembed_a0ea373c79c4d8b600555977925f0140: "{{unknown}}"
  _oembed_f776949f77e5522fdd73645489a22aa8: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/11/17/identity-management-portal-ecm-bpm-projects/"
---
When developing projects Portal, ECM or BPM, often tend to ignore authentication and authorization solution, and a solution that will store user identities.

  
The Authentication, Authorization and Web-SSO solutions require that we have an infrastructure for Identity Management already installed and configured, often commonly use a Meta Directory or LDAP server, but experience leads us to have to use something more dynamic and scalable, especially in projects where it is not clear the initial source of users, roles and hierarchy within the organization.

  
In this scenario, I usually recommend a solution of Virtual Directory to store the identities of our users and CAS as a service and Web-SSO authentication. Both are fully complementary to each other and lead to very quickly build an identity management solution robust and "lowcost" in our organization.

  
## Services in Identity Management Systems (IdM)

  
If we have already an Identity Management infrastructure involves:

  
### Directory or Repository Centralized for Identities.

  
Corporate Directory is an LDAP server that allows to store user or identity information to the applications in the Organization. Organizations should have 2 directories, one for internal services (windows or intranet login, kerberos authentication, etc..) And other web applications or standalone as Liferay Portal.

  
### Mechanisms of replication, synchronization and consolidation of directories or repositories.

  
Some organizations usually have several offices around the world, each has a Directory or LDAP server that allows us to provide services to the office to which belongs, while through the LDAP Directory Server or Consolidated Global can offer services such as search people from other offices through a single Address Book. To do this, we need mechanisms to consolidate data from LDAP Server and data from different sources such as DBMS regardless of where and how them are stored, etc..

  
### Identities Lifecycle Management.

  
This basically is to create, read, update and delete identities or any of its attributes. Some solutions include services such as Rollout, Renewal,Forgotten password, ...

  
### Single Authentication Service or Identity Validation.

  
Authentication is only identity validation, that is, when we make a validation request, the validation authority or authentication service responds by saying that the details of the person or users (data credentials or identity) exist or not in the LDAP server.

  
In addition to the centralized storage of all identities in the Directory or LDAP Server, you need a validation service that can respond if the credentials are correct to requests for validation made by the standalone application, portal, login windows, etc.. of the organization. Such service must know how to respond to different requests of different types of applications and protocols.Typically different protocols are often defined, as many as types of requests for validation, for example, a protocol could be a "bind ldap", "soap" or a simple "https" request.

  
### Authorization Service.

  
Authorization is the process of decision based on certain attributes by which allows a person, machine or server to access a particular resource.

  
### "Single Sing On" Service.

  
This service is logs in once and gains access to all systems without being prompted to log in again at each of them. Single sign-off is the reverse property whereby a single action of signing out terminates access to multiple software systems.

  
Solutions Free/Open Source most used are:

  
  

  * CAS (Central Authentication Service) - <http://www.jasig.org/cas>
  

  * OpenSSO (now of Oracle, it has an uncertain future) - <http://opensso.dev.java.net>
  

  * OpenAM (a branch of OpenSSO) - <http://forgerock.com/openam.html>
  

  
[caption id="" align="alignnone" width="449" caption="Virtual Directory services"]![Virtual Directory services]({{ site.baseurl }}/assets/1.virtualdirectory_services.png)[/caption]

  
## What is the difference between Virtual and Meta Directory?

  
Virtual Directory is a service that operates between applications and identity data as a real directory. A virtual directory receives queries and directs them to the appropriate data sources.

  
  

  * Virtual Directory loosely couple identity data and applications.
  

  * Virtual and Meta Directory provide a consolidated view of identity data by adding a layer on native repositories (ldap, rdbms, ...).
  

  * Meta Directory draw identity data from native repositories and store it in a new consolidated real directory that faces enterprise applications.
  

  * Meta Directory (tight coupling) is a good in which identity data is not updated frequently.
  

  * Virtual Directory offers a way to provide that consolidated view of identity data without having to reconstruct an entire real directory infrastructure.

> "[...]Instead of creating new identity repositories, virtual directory handle identity queries on a case-by-case basis, drawing the required, authorized data (and only the required data) in real time from its native repositories around a network and presenting it to an enterprise application as needed. When the query is complete the virtual directory disappears; once again, the data exists only in its native repositories, under the control of the original owner."

(Penrose FAQ - <http://docs.safehaus.org/display/PENROSE/FAQ>)

There are few solutions for Virtual Directory, here are some FOSS and Commercial:

  * Penrose - http://penrose.redhat.com/display/PENROSE/Home
  * Atlassian Crowd - http://www.atlassian.com/software/crowd/
  * Radiant Logic VDS - http://www.radiantlogic.com/main/products_vds.html

## List of FOSS products and technologies for IdM

[caption id="" align="alignnone" width="459" caption="Virtual Directory and IdM products"]![Virtual Directory and IdM products]({{ site.baseurl }}/assets/2.virtualdirectory_idm_products.png)[/caption]

Bye.
