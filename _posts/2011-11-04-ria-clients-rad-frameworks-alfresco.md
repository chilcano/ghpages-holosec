---
layout:     post
title:      'Review: RIA Clients and RAD Frameworks for Alfresco ECM'
date:       2011-11-04 17:03:54
categories: ['Development', 'ECM', 'Portal']
tags:       ['Alfresco', 'Java', 'Java Script', 'Liferay', 'PHP', 'Portlet']
status:     publish 
permalink:  "/2011/11/04/ria-clients-rad-frameworks-alfresco/"
---
Some days ago I saw a post on the LinkedIn's Alfresco forums asking ['Which front end framework is Generally used with Alfresco?'](http://www.linkedin.com/groups/Which-front-end-framework-is-663827.S.69732274?view=&srchtype=discussedNews&gid=663827&item=69732274&type=member&trk=eml-anet_dig-b_pd-ttl-cn&ut=2MCRzVZGv0CAU1), for this reason I started to research and test them and discovered with joy that the trend is to use lightweight frameworks capable of creating rich interfaces quickly. Well here I put the review: Because I started to carefully review and discovered with joy that the trend is to use lightweight frameworks capable of creating rich interfaces quickly. Well here I put the review:

**What is a client for Alfresco ECM?**

A client for Alfresco is any webapp or desktop app that consumes the Alfresco's services, this client app can be created with Alfresco's standard framework or with third party frameworks.
Several endusers want always customize traditional and existing Alfresco's web clients, because these do not fit its requeriments.  
Then, they quickly seek to modify existing Alfresco's webclient and if this is not possible, then seek a tool (framework) to build a desktop or client from scratch.

[![How to client applications can be connected to Alfresco ECM]({{ site.baseurl }}/assets/ria_clients_alfresco_rev1.png)](http://dl.dropbox.com/u/2961879/blog20111104_riaclientsalfresco/ria_clients_alfresco_rev1.png) _How to client applications can be connected to Alfresco ECM_


<!-- more -->

What are the main reasons why you need to modify Alfresco's webclients or create new ones?  
Well, there are several reasons:
* The technology stack in the company is not Java.
* Is very difficult to make changes. Although there is much online information available on how to make customizations to Alfresco Explorer and Alfresco Share.
* Make changes in existing webapp are not supported.
* UI (look&feel) is not aligned to corporate graphical style.
* Alfresco's content must be delivered for any devices: iPad, Android tablet, PC, etc.
* ... or simply, everybody has different requirements.
Well, this post will review existing initiatives, RIA, RDA (Rich Desktop App) and RAD frameworks for developing clients for Alfresco ECM.

## List of Clients

**1. Alfresco Explorer**

Alfresco Explorer is the clasic webapp bundled with Alfresco ECM, It is the principal web application to do document management, this offers some functionalities of system administratios.  
Big changes or personalizations on this webapp must be done by XML file (Spring) configuring.
* Web: <http://www.alfresco.com>  
* License: Enterprise Edition is open source with commercial support, Community Edition is LGPL 2 with some exception.  
* Technology:  
Alfresco Explorer based in JavaServerFace.

**2. Alfresco Share**

* Provides a rich web-based collaboration environment for managing documents, wiki content, blogs and more.  
* Is based on Alfresco Surf API (now is a Spring project).
* Web: <http://www.alfresco.com>  
* License: Idem to Alfresco Explorer  
* Technology:  
Alfresco Share based in SpringSurf/Webscripts.

**3. Opsoro**

"opsoro is an alternative web client for the Alfresco Enterprise Content Management Plattform. It's goal is to provide an easier user interface with a richer user experience by leveraging the latest web technologies."
* Version: ExtJS-version is 0.1beta  
* Web: <http://www.opsoro.org>  
* License: GPL  
* Architecture: <http://www.opsoro.org/architecture>  
* Technology:  
\- Alfresco server side: Opsoro does uses standard Alfresco Web Scripts.  
\- Web server side: Ext JS 2.0  
* Features:  
Basic document management features as:  
\- Folder, category and tag cloud browser  
\- My Alfresco portal with draggable portlets  
\- Document tagging  
\- Embedded preview (crop content & image preview)  
\- Inline view (supports text, pdf and images)

**4. FlexSpaces (in browser, portlet and Air)**

"Flex RIA client for Alfresco ECM"
* Version: build 04052011 from 2011/05/10  
* Web: <http://code.google.com/p/flexspaces>  
* License: GNU Lesser GPL  
* Architecture: see my diagram above  
* Technology: Flex, own RESTful WebScripts, Air.  
* Features:  
\- Supports document management, search, workflow and wcm.

**5. CMISSpaces (in browser, portlet and Air)**

· "Flex RIA client for CMIS content management APIs."  
· Based on FlexSpaces
* Version: Build 18 from 2011/05/23  
* Web: <http://code.google.com/p/cmisspaces>  
* License: GNU Lesser GPL  
* Architecture: see my diagram above  
* Technology: Flex, BlazeDS, CMIS, Air.  
* Features:  
\- Doc management, except:  
· cut,copy,paste, advanced search not implemented, properties is read-only  
\- No workflow actions.

**6. ifresco**

"OpenSource Client for Alfresco  
With the ifresco client we offer a powerful OpenSource Web-Client for Alfresco which has all features which are important and neccessary for the daily work with the DMS/ECM system and which can be operated easily and intuitiv.  
ifresco is Web-browser based - but offers consumer-like experience of MS-Windows applications with sortable lists, context menus, drag and drop.  
A plug-in interface allows to add new functions and features to the client without changing the base product."
* Version: 0.2 beta from 2011/05  
* Web: <http://code.google.com/p/ifresco-client>  
* License: GNU GPL v3  
* Architecture: <http://www.ifresco.at/de/products/client/overview.html>  
* Technology:
* PHP5+  
* Symfony 1.4 for MVC Framework  
* Doctrine for database ORM  
* Alfresco Integration based on Standard Restful Services & Webservice API  
* No additional webscripts must be installed on the server!  
* Server Side Converter for OFFICE2SWF Conversion (jodconvert & swftools)  
* MySQL to save - administrative data, user sessions and favorites  
* jQuery for various JavaScript functions and drag & drop  
* ExtJS for the user interface with plug-ins
* Features:  
* All the DMS/ECM functionalities.  
* A plug-in interface allows to add new functions and features to the client without changing the base product.  
* No workflow actions.  
* Provides a mature and complete PHP Lib based on Alfresco PHP API (http://code.google.com/p/ifresco-php-library).

**7. Liferay Document Library by CMIS**

* Web: <http://www.liferay.com>  
* License: GNU General Public License version 3.0 (GPLv3) for Community Edition  
* Architecture: see my diagram above  
* Technology:  
Up to version 6.0.6 was possible to use Liferay Document Library to connect to any CMIS repository through a Liferay Hook, but was little usable in real scenarios, [now in version 6.1b3](http://www.liferay.com/web/sergio.gonzalez/blog/-/blogs/9876984) we can connect to any CMIS repository and allow synchronization between Alfresco repository and Liferay.  
I think this is a huge step forward demonstrating the strength of CMIS interface to any type of application in front-end side.
* Features:  
\- Connect to any CMIS repository  
\- Still remaining to be developed all the features of CMIS 1.0  
\- It is very usable in real scenarios.

**8. DoCASU**

"The goal of DoCASU is to provide to the Alfresco Community a Custom Alfresco UI with a strong focus on User eXperience. This will be less confusing for average end users and will permit a broader acceptance of the solution by a larger group of users."
* Version: 1.6.3 from 2010/01/08  
* Web: <http://docasu.sourceforge.net>  
* License: GNU GPL  
* Architecture: <http://docasu.sourceforge.net/productInfo.html>  
* Technology: ExtJS, own WebScripts (REST) and WS.  
* Features:  
DoCASU provides the functionality that average end users need to easily work with Alfresco:  
\- Folder Actions (View, create, delete, rename Folder, Create HTML or Text File, Upload File, Paste all)  
\- File Actions (View, update, delete, checkout, checkin, copy File, Download File, Add to favorites, Mail Link to File, Copy File Path/URL to clipboard)  
\- Simple and Advanced Search (Node type, Creation date, Modification date, Look in current folder, all folders)  
\- No workflow actions

**9. ExtAlf**

"This is not an Opsoro or FlexSpaces alternative, rather than a developer toolkit or a simple SDK what supports embedding Alfresco backend functionality into Web2.0/AJAX portals and frontends."
* Web: [http://louise.hu/poet/?p=753](http://louise.hu/poet/?p=753 )
* License: ExtAlf is part of a bigger project what is closed-source yet.  
* Architecture: see diagram above  
* Technology: ExtJS and own WebScripts  
* Features:  
\- Doc management.  
\- More info here: [http://louise.hu/poet/?p=927](http://louise.hu/poet/?p=927)

**10. CMIS Explorer**

[http://blogs.citytechinc.com/sjohnson/?p=60](http://blogs.citytechinc.com/sjohnson/?p=60)
[http://code.google.com/p/cmis-explorer](http://code.google.com/p/cmis-explorer)

**11. Alfresco Office Plugin**

"Implemented on a mini-browser windows, all actions available through REST API (HTML response for UI, others JSON for responses to update UI and data retrieval".  
Core services accessible through plugin:  
\- Document management  
\- Workflow  
\- Search

**12. SCAr**

<http://wiki.rivetlogic.org/display/SCAr/Home>

**13. AWPr**

"AWPr (Alfresco Web Script Portlet rivet) is a JSR-286 portlet that can be used to expose remote Alfresco Web scripts, including those that need user authentication."
* Web: <http://wiki.rivetlogic.org/display/AWPr/About+AWPr>  
* License: GNU Affero General Public License.  
* Version:

**14. Alfresco Web Script Portlet**

"A webscript portlet for Liferay Portal, supporting multiple modes of authentication, a simple AJAX proxy and jQuery support."
* Web: <http://forge.alfresco.com/projects/liferaywsp>  
* License: MIT/X Consortium License  
* Version: 1.0 of May/31/2010

**15. WeWeBu OpenWorkdesk**

"WeWebU OpenWorkdesk is an application suite (not just a CMIS browser!) for Enterprise Content Management (ECM) with an intuitive Web 2.0 front-end."
* Web: http://openworkdesk.org (<http://sourceforge.net/projects/owd>)  
* License: GNU General Public License version 3.0 (GPLv3) for Community Edition  
* Version: 3.2.0.0 sprint6  
* Architecture:
Integration using CMIS standard and custom framework (WeWebU OpenECM-Framework) more info here:  
<http://www.wewebu.de/fileadmin/user_upload/software/OECM_Architektur_EN.gif>
* Technology:  
-Java, CMIS and ExtJS 3.2.0 for UI in frontend
* Features:  
The Community Edition version contains standard functionalities related to document manangement and supports all CMIS-enabled ECM systems:  
\- checkin, checkout, create, delete, search, ...  
Extra functionalities as workflow interactions, etc. are not availables in Community Edition.  
For more detailed information about editions, here is a short comparison matrix:  
<http://www.openworkdesk.org/system/files/u4/20100623_OS-Editionen-Matrix_All.jpg>
For an updated roadmap, you can see here:  
<http://www.openworkdesk.org/roadmap>

## Meta-frameworks

1. Jibe Framework (AlfExt)  
http://code.google.com/p/jibeframework

2. Spring Surf  
http://www.springsurf.org

3. Apache Chemistry (CMIS)  
http://chemistry.apache.org

4. Spring Surf CMIS Application Browser  
> Spring Surf provides a scriptable approach to building web applications. OpenCMIS provides a Java client library for accessing any CMIS compliant content repository. By combining the two, we create a development platform for building CMIS enabled web applications.
> [http://blogs.alfresco.com/wp/cmis/2010/03/17/spring-surf-and-opencmis-integration](http://blogs.alfresco.com/wp/cmis/2010/03/17/spring-surf-and-opencmis-integration/)  
> [http://blogs.alfresco.com/wp/cmis/2010/06/14/spring-surf-and-opencmis-integration-part-2](http://blogs.alfresco.com/wp/cmis/2010/06/14/spring-surf-and-opencmis-integration-part-2)

5. Struts2 CMIS Explorer  
> Struts2CmisExplorer is a web-based CMIS explorer. It uses Struts2 for the user interface. If you have documents stored in your ECM repository and want to expose them (or some of them) to your clients/employees through an Intranet (or Internet/portal) web application...  
> [http://code.google.com/p/struts2cmisexplorer](http://code.google.com/p/struts2cmisexplorer)

6. Drupal + CMIS  
http://drupal.org/project/cmis

7. Joomla + CMIS  
http://blog.joomlatools.eu/2008/12/joomla-meet-alfresco.html

8. Drupal Open Atrium + CMIS  
http://ecmarchitect.com/archives/2010/03/22/1141

9. ifresco PHP Lib  
> ifresco PHP Library extends the Alfresco PHP Library, many RESTFul Services (which are used by Share) are implemented. Zoho Upload is implemented in here too.
* Web: <http://code.google.com/p/ifresco-php-library>  
* License: GNU GPL v3  
* Technology:  
\- PHP5+  
\- Alfresco Integration based on Standard Restful Services & Webservice API  
\- No additional webscripts must be installed on the server!  
\- Parts of the Administration Service  
\- Category Management  
\- Lucene Search for Limited Result Query's

## Conclusions
* This review concludes that the trend is to use existing products (ifresco, Drupal, OpenAtrium, Liferay, ....) enabling rapid integration with Alfresco services.
* The second option is to create RIA applications from scratch, and quickly, taking advantage of existing metaframeworks, we can see in doCASU, ExtAlf, AWPr, FlexSpace, etc.., The fact that they have started as a proof of concept and then have become standalone products confirms this trend.
* You can download a resume table with all features of each product reviewed from here.
