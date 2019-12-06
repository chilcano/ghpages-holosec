---
layout: post
title: Starting with WSO2 Identity Server (Security as Service)
date: 2013-11-28 18:48:40.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- PaaS
- Security
- SOA
tags:
- IdM
- LDAP
- SSO
- WSO2
- WSO2IS
meta:
  _wpcom_is_markdown: '1'
  _edit_last: '578869'
  _oembed_97ae9477f51fb72a6d5679ca6fa45264: "{{unknown}}"
  _publicize_pending: '1'
  _oembed_1d7935b90dcbf282372771f7a1dfd295: "{{unknown}}"
  _oembed_3977adaa278e407fc0bbe0ce6ab3b1ea: "{{unknown}}"
  _oembed_7e8ac4c6c129de1ced1a1560920dd2bb: "{{unknown}}"
  _oembed_f4a421f6862dd22f2ed23809cd2f5a12: "{{unknown}}"
  _oembed_2971f102357dedf67e8de44f6efb560c: "{{unknown}}"
  _oembed_57106b20a0abdbb1c67aebeca42fa52f: "{{unknown}}"
  publicize_google_plus_url: https://plus.google.com/113031469837757886298/posts/ZY5kH2xgJiJ
  _wpas_done_5053089: '1'
  _publicize_done_external: a:1:{s:11:"google_plus";a:1:{s:21:"113031469837757886298";b:1;}}
  publicize_twitter_user: Chilcano
  publicize_twitter_url: http://t.co/geZxvczjUf
  _wpas_done_13849: '1'
  publicize_linkedin_url: http://www.linkedin.com/updates?discuss=&scope=6985267&stype=M&topic=5811883154654777344&type=U&a=ZzXi
  _wpas_done_5053092: '1'
  _oembed_6297c631dce942f8409fbd58304d060c: "{{unknown}}"
  _oembed_fab894d493b1b18725157a87e9135192: "{{unknown}}"
  _oembed_cd4c41731e6d0985c1f69dd4d1341343: "{{unknown}}"
  _oembed_6ef7c4f59525ca737018d8140dff5518: "{{unknown}}"
  _oembed_b058ca939fff555d3e8f6c7011ea2101: "{{unknown}}"
  _oembed_fb85a56a164c4cc4b9cf45dcfd518530: "{{unknown}}"
  _oembed_dfd3137c420c040d0b8a9e34749ccbe4: "{{unknown}}"
  _oembed_e46242231d8f444cc441f9b7df07d1e6: "{{unknown}}"
  _oembed_16a5a656f12fc370c6d32b04142331b7: "{{unknown}}"
  _oembed_f7a04da7fa0ea2c54c10ac03d89f822f: "{{unknown}}"
  _oembed_438b16965e0f8c7c4902ba847906aa5c: "{{unknown}}"
  _oembed_b7bed1bb9b70e6ec310128cb13181331: "{{unknown}}"
  _oembed_ed14ece2dc1ef73822033874249ac612: "{{unknown}}"
  _oembed_afd0417535f609aaa5cd265638ed93cb: "{{unknown}}"
  _oembed_6ba5f351de6f0f1b7413a1e1a187a935: "{{unknown}}"
  _oembed_a426082fc2d4a0ede88b6980de8ecf04: "{{unknown}}"
  _oembed_498217f8a5b44b4c04f52c23d2c37fd7: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2013/11/28/starting-wso2-identity-server/"
---
Some years ago I have used many open source tools to cover identity management projects, there was not a unique tool that allows me to cover the entire life cycle of identity management projects.

  


Only as a sample, I list of tools that I used in my last projects:

  


1.- Directory or LDAP servers:

  


  

  * Apache DS
  

  * OpenLDAP
  

  * OpenDS
  

  * CentOS Directory Server
  

  * Fedora DS
  

  


2.- Virtual Directory or Proxy LDAP servers:

  


  

  * [Penrose Virtual Directory (http://holisticsecurity.wordpress.com/2010/12/22/authentication-penrose-directory-liferay)](http://holisticsecurity.wordpress.com/2010/12/22/authentication-penrose-directory-liferay "Penrose Virtual Directory and Liferay Portal")
  

  * MyVD
  

  


3.- PKI

  


  

  * OpenSSL
  

  * OpenCA
  

  * EJBCA
  

  * DogTag
  

  * TinyCA
  

  


4.- AuthN/AuthZ Servers

  


  

  * CAS
  

  * OpenAM
  

  * OpenSSO
  

  * JOSSO
  

  * Shibboleth
  

  * SimpleSAMLphp
  

  


For further information, you can see my [post on Identity Management  
(http://holisticsecurity.wordpress.com/2010/11/17/identity-management-portal-ecm-bpm-projects)](http://holisticsecurity.wordpress.com/2010/11/17/identity-management-portal-ecm-bpm-projects "Identity Management in BPM and SOA Projects").

Well, now developing projects related to identity management or authentication and authorization services to business applications is easier thanks to many of the security and management of identities features that are required are included in an unique product called WSO2 Identity Server.

[caption id="attachment_1038" align="aligncenter" width="150"][![WSO2 Identity Server]({{ site.baseurl }}/assets/wso2-identity-server-2.png?w=150)](http://wso2.com/products/identity-server/) WSO2 Identity Server[/caption]

The [WSO2 Identity Server ](http://wso2.com/products/identity-server/ "WSO2 Identity Server")(aka WSO2 IS) is a WSO2's product oriented to Identity Management (IdM) perfectly suitable for SOA/BPM projects, SaaS and PaaS projects.

WSO2 IS is a free and open source product and helps us to manage all life cycle of IdM's projects, its main functionalities for the current version ([4.5.0](http://dist.wso2.org/products/identity-server/4.5.0/release-notes-is.html "WSO2 IS 4.5.0 release notes")) are:

1.- Identity Management

  * Multifactor Authentication, Credentials Management (Provisioning via SCIM, User Storage Management using ApacheDS, Multi Users Storage, ), SSO (Kerberos, SAML2, OpenID), Federation (OpenID, SAML2, WS-Trust STS), Delegation (OAuth, WS-Trust), REST security (OAuth, XACML), XKMS (Key Storage and distribution), Account Management (Password Policies, account locking, customizable login pages, account recovery) and out-of-box integration with SaaS apps as Google Apps and Salesforce.



2.- Entitlement Management

  * RBAC, XACML (attribute or claim based access control), WS-Trust, OpenID.
  * Fine-graned policy based access control via XACML.
  * Authorization for any REST or SOAP calls.



3.- Integrable and/or Developer friendly

  * Many IdM functionalities are exposed as API (SOAP and REST calls).
  * Clustering for high available deployment.
  * Integrated to [WSO2 Enterprise Service Bus](http://wso2.com/products/enterprise-service-bus/ "WSO2 ESB") for AuthZ and AuthN.



4.- Managed

  * Integrated with [WSO2 Business Activity Monitoring](http://wso2.com/products/business-activity-monitor/ "WSO2 BAM") for operational auditing and KPI.
  * JMX MBeans for metrics and management.
  * Integrated with [WSO2 Governance Registry ](http://wso2.com/products/governance-registry/ "WSO2 GREG")for configuration management (multiple deployments, versioning of configurations).
  * Logging support.



Well, WSO2 IS is constantly evolving, although still is in the version 4.5.0, many of its attractive features are 1-2 years old. The big advantage is that WSO2's engineers are working hard and WSO2 has an active community supporting it.

In this blog we will be publishing a series of articles related to WSO2IS for anyone interested in IdM and security can begin rapidly.

Then I leave the official source WSO2 IS resources:

  * WSO2 IS - Product Documentation  
http://docs.wso2.org/display/IS450/WSO2+Identity+Server+Documentation
  * WSO2 IS - Support in StackOverflow  
http://stackoverflow.com/questions/tagged/wso2is

  * WSO2 IS - Source code  
https://svn.wso2.org/repos/wso2/carbon




I hope you have been useful.
