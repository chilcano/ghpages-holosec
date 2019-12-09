---
layout:     post
title:      'Which Portal Solution should I use?'
date:       2011-06-14 07:25:32
categories: ['Portal']
tags:       ['Alfresco', 'eXo', 'GateIn', 'Hippo', 'InfoGlue', 'Jahia', 'JetSpeed', 'Liferay', 'OpenPortal', 'Pluto', 'Sakai', 'uPortal']
status:     publish 
permalink:  "/2011/06/14/which-portal-solution-should-i-use/"
---
Tell me what you want to do and I will recommend a portal solution that meets your needs. There are many alternatives Free / Open Source, Commercial Open Source and Proprietary, in the LAMP area there are many more and in the world .Net, more.  
Therefore, is important to identify the needs and thus select the portal product with which you will build your solution.  
But before we should define what is Portal?, is another buzz word?, Portal or Web Portal?.

[caption id="" align="alignnone" width="498" caption="Enterprise Portal"]![Enterprise Portal]({{ site.baseurl }}/assets/portal-architecture.png)[/caption]

## What is Portal?
From Wikipedia (http://en.wikipedia.org/wiki/Web_portal):
> "A web portal or links page is a web site that functions as a point of access to information on the World Wide Web. A portal presents information from diverse sources in a unified way.  
> Apart from the standard search engine feature, web portals offer other services such as e-mail, news, stock prices, information, databases and entertainment. Portals provide a way for enterprises to provide a consistent look and feel with access control and procedures for multiple applications and databases, which otherwise would have been different entities altogether.  
> Examples of public web portals are AOL, Excite, iGoogle, MSN, Netvibes, and Yahoo!."

## ¿What is Software Portal?
From SearchCIO.com (http://searchcio.techtarget.com/definition/portal-software):
> "Portal software is a type of development tool used to create a portal (starting point) on a company's  
> intranet so that employees can find a centralized starting place for access to consolidated  
> enterprise-related functions, such as e-mail, customer relationship management (CRM) tools,  
> company information, workgroup systems, and other applications. The package may be customized to  
> varying degrees of enterprise or individual specificity. Portal software is similar to intranet  
> software, but the end product typically features more complexity, automation, organization, and  
> interactivity. Although the end product is sometimes referred to as an intranet portal, it is usually  
> called an enterprise information"

## What types of portals are there?
It is increasingly difficult to classify a Portal under a category, and that these categories are changing, also the portals have common details among themselves.  
I have taken the definition of types of Portals from www.contentmanager.eu.com I think is wide and exact.  
And these are:

### 1\. Information Portals vs. Content Management Portals
 **Information Portals:**  
Also called Vertical Portals or Enterprise Information Portals, they consolidate the information from different types and from different sources.  
The users of these sites tend to consume information, they do not create information.
It is often used for:
  * make "mash-up" in general.
  * RSS, weather, financial information
  * Access to corporate email, calendar, events, courses, etc..
  * View company's corporate information, download forms, etc..
  * View reports, etc.
**Content Management Portals:**  
Allows you to manage (create, modify, delete, publish, share, etc.) digital assets (content, documents, etc.) from the Portal.
It is often used for:
  * Check-in, check-out, do versioning, lock, etc. on documents.
  * Perform a workflow on the documents.

### 2\. Application Centric Portals vs. Content Centric Portals
**Application Centric Portals:**  
Basically it is a portal that allows you to interact with applications or business systems of the organization, avoids access to each of them.  
This portal provides access to all business applications from a single point.
It is often used for:
  * Perform procedures or workflows, not just the record or the initial part of them.
**Content Centric Portals:**  
Very similar to Content Management Portal, in this case, the portal takes the information from enterprise applications such as WCMS, DMS, DAM, RMS and standard desktop applications.

### 3\. Vertical Enterprise Portals vs. Horizontal Enterprise Portals
**Vertical Enterprise Portals:**  
There are specialized portals that address a certain theme and geared to a very specific audience.
Examples:
  * www.mp3.com
  * www.pets.com
**Horizontal Enterprise Portals:**  
They are portals that treat or display information from different types and are aimed at all audiences.
Examples:
  * www.yahoo.com
  * www.aol.com

## What type of portal you want to build?
Apparently, from the types of portal, a personal website is a portal and a Facebook page too.  
So it is most convenient identify the portal we want to implement and see if it fits the definition.

### Web Dashboard:
  * Show real-time financial information of the Stock Exchange.
  * Generate reports.
  * Historical view of certain indicators.

### e-Learning Web Portal:
  * Define courses, associate an agenda, content, exams, etc.
  * See students activity in virtual classrooms.
  * Allow collaboration: create content, conduct examinations.
  * Allow communication, via chat, instant messaging, email, VoIP calls, etc..
  * Allow assess.

### Web of e-Commerce:
  * Define product catalog.
  * Be able to pay.
  * Be able to order a gift, etc..
  * Make a delivery.
  * Notifications.

### Web of administrative Processes:
  * Define the list of processes.
  * Have an "inbox" where I will get notifications with the status of my processes.
  * Have a folder with all the processes that I can start.
  * Have a folder to save my documents that I could use in any process.

### Web blog:
  * Define categories, tags related to topics.
  * Rich editor that allows write articles on my blog.
  * Have tools for evaluating the quality of the articles, their acceptance, their importance or simply to indicate whether or not you like someone.
  * Have tools to make diffusion of the articles published on the blog.

### Intranet:
  * To access the document repository of the organization.
  * Able to access to email, instant messaging, chat, the blog, etc.
  * To access certain corporate applications such as SAP, CRM, ERP, etc.

### Corporate Web:
  * To create an information structure: about us, services / products, news, contact, etc.
  * It is the "cover letter" of my organization on the Internet and should be able to find through search engines or Internet directories.

### Social Network:
  * Be able to have a Wall as Facebook, to publish all my activity.
  * Post photos, videos, documents, etc.
  * Add members to my network.
  * To have communication and collaboration tools.
... yes, the list is endless, it is important to define the scope of your website and then see if the technology exists to implement it.  
That is what the next step.

## What Software Portal can use?
There are many products / software / technology to create the Portal we want.  
There are 2 product lines well marked, the difference is whether they are aligned or not standards Portals
The Portal standards are:
  * JSR-168 (Java Portlet Specification v1.0)
  * JSR-286 (Java Portlet Specification v2.0)
  * WSRP (Web Services for Remote Portlets).
Among those who are aligned to these standards are (in alphabetical order):
**1\. Alfresco ECM (http://www.alfresco.com)**  

![]({{ site.baseurl }}/assets/logo_01_alfresco.png)Is an Enterprise Content Management, Document Management, has features of collaborative work, Record Management, Knowledge Management, Web Content Management and has social elements.
**2\. Apache Pluto (http://portals.apache.org/pluto)**  

![]({{ site.baseurl }}/assets/logo_02_pluto.png)From its website:  
"Pluto is the Reference Implementation of the Java Portlet Specification. The current version (2.0) of this specification is known as JSR-286. The previous specification version, JSR-168, covers version 1.0 of the specification. Pluto implements and is fully compliant with both the 1.0 and 2.0 specifications."
Yes it is true, is a reference implementation of JSR-168 and JSR-286. The libraries of Apache Pluto is embedded in specialized implementations such as Apache JetSpeed​​, Sakai (for e-learning, etc.).
**3\. Apache Jetspeed-2 (http://portals.apache.org/jetspeed-2)**  

![]({{ site.baseurl }}/assets/logo_03_jetspeed.gif)Is based on Apache Pluto.  
From its website: "Jetspeed is an Open Portal Platform and Enterprise Information Portal, written entirely in open source under the Apache license in Java and XML and based on open standards".
**4\. eXo Platform (http://www.exoplatform.com)**  

![]({{ site.baseurl }}/assets/logo_04_exo.png)From its website: "eXo Platform 3.5 will enable social intranets and websites to be accessible in new ways, by adding cloud-readiness and mobile apps to the only User Experience Platform (UXP) for Java. Enterprise Java organizations will be able to more easily develop, extend and deploy modern enterprise portals, gadgets and mashups within a private cloud or in Platform-as-a-Service (PaaS) environments."
**5\. Hippo Portal 2 (http://www.onehippo.com/en/products/portal)**  

![]({{ site.baseurl }}/assets/logo_05_hippo.png)From its website: "With Hippo Portal you can give secure and personalized access to content and business applications."
Is based on Apache Jetspeed-2, has an easy integration with Hippo CMS, providing it with content management functionality.
**6\. IBM WebSphere Portal (http://www-01.ibm.com/software/websphere/portal)**  

![]({{ site.baseurl }}/assets/logo_06_ibm.png)From its website: "IBM® WebSphere® Portal Server is the foundation offering of the WebSphere Portal product family, with enterprise portal capabilities that enable you to quickly consolidate applications and content into role-based applications, complete with search, personalization, and security capabilities."
**7\. InfoGlue (http://www.infoglue.org)**  

![]({{ site.baseurl }}/assets/logo_07_infoglue.png)Is a platform that integrates CMS and Portal. Is based on Apache Pluto.  
From its website: "InfoGlue have step by step matured into one of the most advanced, scalable and robust Open Source content management / portal platforms available. Our increased focus on usability together with flexibibility in every area makes it suitable for a wide range of applications and organisations."
**8\. Jahia (http://www.jahia.com)**  

![]({{ site.baseurl }}/assets/logo_08_jahia.png)Basado en Apache Pluto, es un software muy similar a Alfresco ECM.  
Y de su web sacamos: "Jahia 6.5 is the comprehensive platform to meet these expectations: It is much more than a simple WCM, it helps your organization build cutting edge user experiences and virtually any type of web applications." y  
"Jahia includes an embedded portal server, which is based on the Apache Pluto reference implementation of the JCR Portlet API specification. The goal of this implementation is to offer support for integrators who need to embed portlets on content pages."
**9\. JBoss GateIn (http://www.jboss.org/gatein)**  

![]({{ site.baseurl }}/assets/logo_09_gatein.png)From its website: "GateIn is a set of projects revolving aroung the main project called 'GateIn Portal'.  
GateIn portal is a merge of two mature projects that have been around for a while, JBoss Portal and eXo Portal.  
It takes the best of both into a single new project.  
The aim is to provide both an intuitive portal to use as-is and a portal framework to build upon depending on your needs."
**10\. Liferay Portal (http://www.liferay.com)**  

![]({{ site.baseurl }}/assets/logo_10_liferay.png)From its website: "Liferay Portal is an enterprise web platform for building business solutions that deliver immediate results and long-term value."
**11\. OpenPortal (http://en.wikipedia.org/wiki/OpenPortal)**  

![]({{ site.baseurl }}/assets/logo_11_openportal.png)From Wikipedia: "OpenPortal is an open-source web portal project. The code was donated by Sun Microsystems and was initially based on the Sun Java System Portal Server 7.1 update 1 release."  
Is a discontinued project.
**12\. Oracle Portal (http://www.oracle.com/technetwork/middleware/portal/overview)**  

![]({{ site.baseurl }}/assets/logo_12_oracle.gif)From its web: "Oracle Portal 11g Release 1 offers a complete and integrated framework for building, deploying, and managing enterprise portals running on Oracle WebLogic Server. Oracle Portal's unified and secure point of access to vital enterprise information and services improves business visibility and collaboration, reduces integration costs, and ensures investment protection."
**13\. Sakai (http://sakaiproject.org)**  

![]({{ site.baseurl }}/assets/logo_13_sakai.png)There are 2 products, Sakai CLE (Collaboration and Learning Environment) and Sakai OAE (Open Academic Environment) launched as a pre-release with version 0.1.  
Sakai CLE is based on Apache Pluto.
From its web, Sakai CLE: "A full-featured system supporting technology-enabled teaching, learning, research and collaboration for education." and  
Sakai OAE: "The Sakai Open Academic Environment is a new system that embraces a new vision for academic collaboration."
**14\. uPortal (http://www.jasig.org/uportal)**  

![]({{ site.baseurl }}/assets/logo_14_uportal.jpg)Based on Apache Pluto, from its web:"uPortal is the leading open source enterprise portal framework built by and for the higher education community."

## What about software or products based on PHP, Python, Ruby, ... ?
Yes, yes there are, and they are very good, in the following article we will discuss them and its level of maturity.

## So, Is Portal Software ready for use?
Depending on the type of portal you want to implement, usually with the products listed above could implement almost everything with a little effort in customizing and developing some custom components.  
Portlets (functional components that are deployed in Portales JSR-168) give the proper behavior of a portal type website.  
For example, if I have my own Facebook-style social network, you first have to install the Portal solution (the container), then add the tipical Portlets of a social network such as:  
Wall Portlet, RSS Reader Portlet, Chat Portlet, Portlet YouTube, etc.

## What features should have a Portal software?
There are different features, they are:
**1\. Content Management**  
\- Document Library  
\- Themes and Layout  
\- Web & webspace publishing  
\- Language support  
\- Knowledge management: taxonomy, categories, WCM, Wiki, etc.  
\- Indexing/Searching: Lucene/Solr
**2\. Collaboration**  
\- Wiki  
\- Blogs  
\- Message boards  
\- Instant messaging  
\- Calendar  
\- Knowledgebase: versioning, creation of docs, print to PDF, file attachment, integration with OpenSearch, rating system, etc.  
\- Webmail client
**3\. Social**  
\- Presence & Social: chat, friend list, activity tracker, etc.  
\- Alerts & announcements  
\- Mashup & integration: Facebook, iGoogle, OpenSocial, etc.
**4\. Security**  
\- Authentication methods: LDAP, JAAS, NTLM, Facebook, etc.  
\- Authorization based on roles (Role-Based Authorization Control - RBAC)  
\- Signgle Sign On with CAS, CA SiteMinder, OpenSSO, OpenID, etc.  
\- Identity Management: LDAP sync, Oracle Access Manager, Facebook, etc.
**5\. Integration Platform**  
\- UI Integration Platform: AlloyUI, jQuery, ...  
\- Enterprise Integration Platform: Workflow Engine, Reporting Engine, SOAP, JSon, RMI over HTTP, ...  
\- Site Integration Platform: XSLT/XML, iFrame, WSRP, ...
**6\. Extendibility and easy Development**  
\- SDK and/or API  
\- IDE for developing  
\- Support for portlets frameworks: JSF, Wicket, Spring MVC, ICEfaces, Vaadin, ...
**7\. Ready for Mobility**  
\- Native mobile application  
\- Mobile as thin client.

## What is the level of maturity of each portal software?
Again, much depends on the needs and resources that are available, some solutions have a good base portal technology but lack the special features for WCM, for example.  
While other solutions lack of features related to social networks, so it must be created from scratch.
So now assess every one of these solutions and add aspects related to the strength of Community and the type of licensing (Free / Open Source, Commercial Open Source, Proprietary).  
This is not an exhaustive technical evaluation of each portal software, is simply a general review where I try to identify who is best suited for every need.
The aspects to consider are:
1\. Content Management  
2\. Collaboration  
3\. Social  
4\. Security  
5\. Integration Platform  
6\. Extendibility and easy Development  
7\. Ready to Mobility  
8\. Community (user forums, documentation, wiki, etc.)  
9\. License type (Free Open Source, Commercial Open Source, Proprietary)
Rating:
    Not have:   0
    Low:        1
    Media:      2
    Enough:     3
    Good:       4
    Excellent:  5
Finally, the result is this:

[caption id="" align="alignnone" width="490" caption="Enterprise Portals - Valoración"]![Enterprise Portals - Valoración ]({{ site.baseurl }}/assets/portal-madurez.png)[/caption]
Making a quick review of the product software Portals winner is Liferay Portal.  
With **Liferay** you can build any kind of portal, including social networks, e-learning portals and e-commerce portals, etc., however, its weakness is that it still has no native application for Android or iPhone, only you can use the mobile browser and wait to use the liferay theme for iphone.  
**eXo Platform** is in a second position and it really is a very powerful product, it is native applications for iPad, iPhone and Android.  
its weakness, compared with Liferay, is the weakness of their community.
If my purpose is to create an e-learning portal with social and collaborative features, as the first option is to use **Sakai**. There are initiatives to port the e-learning Portlets in Sakai and take them to another container such as Liferay, but this is not necessary.  
Sakai is based on Apache Pluto and this at the same time implements WSRP (Remote Portlets invocation via webservice) so you do not need to migrate, simply invoke remotely.
If you want to create a corporate Intranet, all of the above are good, but if we need document management functionalities, then **Alfresco** ECM is our best option.  
Nor should we forget Jahia, Hippo and eXo, they are all good choices.
**uPortal** is notable for the strength of its community. Behind are universities and North American academic networks.  
As a product, in terms of technology is very mature, especially has a seamless integration with CAS (solution of authentication, authorization and SSO), also a product resulting from the efforts of the community.  
Particularly delight me much because it is a very lightweight alternative Portal, compared with Liferay for example. So is Hippo Portal (Optimized for low memory footprints and modest hardware requirements: Light-weight Installer Including 38MB Tomcat 6, Spring Portal Light-weight Framework) and Apache JetSpeed-2.
Finally, if we want to create a more powerful portal solution and we do not create it from scratch, as a good alternative is to use as a basis Apache Pluto, Apache or JBoss JetSpeed​​-2 GateIn. These three are good implementations of Portal standards and many manufacturers are embedding these products into their advanced products.  
As is the case of Pentaho (Business Intelligence solution), they use JBoss Portal to create a Dashboard.

## Conclusions:
  1. If you want to create a corporate portal, a social network, etc. you should know that there is software to do that quickly and easily.
  2. If you want to create your own portal solution, you can embed an existing one as Apache Pluto.
  3. If your goal is to have a specialized portal (vertical portal) immediately, it is best to use an existing one for that purpose, for example, Alfresco ECM, Sakai for e-learning, etc.  
But if you have all the necessary resources, it is best to choose one that provides us with tools to quickly develop or customize functionality not had before, examples: Liferay, eXo, WebSphere Portal, Oracle Portal, etc.
  4. If your organization is aligned to the spirit of Free / Open Source and do not need advanced features, then the best alternatives are Apache JetSpeed​​-2, Jahia, Hippo, etc..
**References:**
  * What is a Portal?  
http://www.contentmanager.eu.com/portal.htm
  * Definition: portal software  
http://searchcio.techtarget.com/definition/portal-software
  * New Subway (Content Technology) Vendor Map for 2011  
http://www.realstorygroup.com/Blog/2083-New-Subway-Vendor-Map-for-2011
  * Web portal  
http://en.wikipedia.org/wiki/Web_portal
  * Open Source Portal Servers Written in Java  
http://www.manageability.org/blog/stuff/open_source_portal_servers_in_java/view
**** [Versión en castellano aquí.](http://holisticsecurity.wordpress.com/2011/06/14/que-solucion-portal-deberia-usar/) ****
