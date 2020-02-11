---
layout:     post
title:      'Starting with WSO2 Identity Server (Security as Service)'
date:       2013-11-28 17:48:40
categories: ['IAM', 'PaaS', 'Security', 'SOA']
tags:       ['LDAP', 'SSO', 'WSO2 Identity Server']
status:     publish 
permalink:  "/2013/11/28/starting-wso2-identity-server/"
---
Some years ago I have used many open source tools to cover identity management projects, there was not a unique tool that allows me to cover the entire life cycle of identity management projects.
Only as a sample, I list of tools that I used in my last projects:

| Directory or LDAP servers | Virtual Directory or Proxy LDAP servers | PKI | AuthN/AuthZ Servers |
|---------------------------|-----------------------------------------|-----|---------------------|
| Apache DS | [Penrose Virtual Directory (http://holisticsecurity.wordpress.com/2010/12/22/authentication-penrose-directory-liferay)](http://holisticsecurity.wordpress.com/2010/12/22/authentication-penrose-directory-liferay "Penrose Virtual Directory and Liferay Portal") | OpenSSL | CAS |
| OpenLDAP                 | MyVD | OpenCA | OpenAM        |
| OpenDS                   |      | EJBCA  | OpenSSO       |
| CentOS Directory Server  |      | DogTag | JOSSO         |
| Fedora DS                |      | TinyCA | Shibboleth    |
|                          |      |        | SimpleSAMLphp |

For further information, you can see my [post on Identity Management (http://holisticsecurity.wordpress.com/2010/11/17/identity-management-portal-ecm-bpm-projects)](http://holisticsecurity.wordpress.com/2010/11/17/identity-management-portal-ecm-bpm-projects "Identity Management in BPM and SOA Projects").

<!-- more -->

Well, now developing projects related to identity management or authentication and authorization services to business applications is easier thanks to many of the security and management of identities features that are required are included in an unique product called WSO2 Identity Server.

[caption id="attachment_1038" align="aligncenter" width="150"][![WSO2 Identity Server]({{ site.baseurl }}/assets/wso2-identity-server-2.png?w=150)](http://wso2.com/products/identity-server/) WSO2 Identity Server[/caption]
The [WSO2 Identity Server ](http://wso2.com/products/identity-server/ "WSO2 Identity Server")(aka WSO2 IS) is a WSO2's product oriented to Identity Management (IdM) perfectly suitable for SOA/BPM projects, SaaS and PaaS projects.
WSO2 IS is a free and open source product and helps us to manage all life cycle of IdM's projects, its main functionalities for the current version ([4.5.0](http://dist.wso2.org/products/identity-server/4.5.0/release-notes-is.html "WSO2 IS 4.5.0 release notes")) are:

1. Identity Management
* Multifactor Authentication, Credentials Management (Provisioning via SCIM, User Storage Management using ApacheDS, Multi Users Storage, ), SSO (Kerberos, SAML2, OpenID), Federation (OpenID, SAML2, WS-Trust STS), Delegation (OAuth, WS-Trust), REST security (OAuth, XACML), XKMS (Key Storage and distribution), Account Management (Password Policies, account locking, customizable login pages, account recovery) and out-of-box integration with SaaS apps as Google Apps and Salesforce.

2. Entitlement Management
* RBAC, XACML (attribute or claim based access control), WS-Trust, OpenID.
* Fine-graned policy based access control via XACML.
* Authorization for any REST or SOAP calls.

3. Integrable and/or Developer friendly
* Many IdM functionalities are exposed as API (SOAP and REST calls).
* Clustering for high available deployment.
* Integrated to [WSO2 Enterprise Service Bus](http://wso2.com/products/enterprise-service-bus/ "WSO2 ESB") for AuthZ and AuthN.

4. Managed
* Integrated with [WSO2 Business Activity Monitoring](http://wso2.com/products/business-activity-monitor/ "WSO2 BAM") for operational auditing and KPI.
* JMX MBeans for metrics and management.
* Integrated with [WSO2 Governance Registry ](http://wso2.com/products/governance-registry/ "WSO2 GREG")for configuration management (multiple deployments, versioning of configurations).
* Logging support.

Well, WSO2 IS is constantly evolving, although still is in the version 4.5.0, many of its attractive features are 1-2 years old. The big advantage is that WSO2's engineers are working hard and WSO2 has an active community supporting it. In this blog we will be publishing a series of articles related to WSO2IS for anyone interested in IdM and security can begin rapidly.
Then I leave the official source WSO2 IS resources:
* WSO2 IS - Product Documentation  
http://docs.wso2.org/display/IS450/WSO2+Identity+Server+Documentation
* WSO2 IS - Support in StackOverflow  
http://stackoverflow.com/questions/tagged/wso2is
* WSO2 IS - Source code  
https://svn.wso2.org/repos/wso2/carbon
I hope you have been useful.