---
layout:     post
title:      'Building a Social eCommerce solution'
date:       2013-06-25 11:22:03
categories: ['Portal', 'SOA']
tags:       ['ECOMMERCE', 'KONAKART', 'Liferay', 'WSO2']
status:     publish 
permalink:  "/2013/06/25/building-social-ecommerce-solution/"
---
I'd like to explain our recent experience gained in a project of eCommerce.
Now more than ever the need to sell and generate business is an important point, the strategy of building the eCommerce solution and their viability is closely linked to these business needs.
For these reasons, I will explain how to approach this project from a technological point of view and its direct relation to requirements.
The requirements of this project were:
* Time to market.  
* Should be possible to have a store quickly to sell, remembering that in the short term can be adding more and better features.
* Robust platform.  
* That technically is aligned to market technology trends and be able to support large transaction volumes.
* Customizable and Extendible.  
* This requirement made ​​our list of technology products to be reduced. It is true that there are very mature products on the internet, however many of them are closed and final products which is very difficult to customize and add new features, and many of them are not designed for it.
* Web 2.0.  
* Again, there are very good e-commerce products but do not have web 2.0 features and vice versa. In this case, a combination of these was the alternative.
Given these customer requirements, we add our own requirements:
* Rapid Application Development.  
* The product or products that we use to build eCommerce solution are prepared to be modified or to extend its functionality from programming tools. This is always necessary in any project, we must ensure that we have the appropriate development tools to shorten construction cycles.
* Decoupled. 
* One of the paradigms in design and application architecture is "loosely coupled design", ie the components of the application have not "knowledge" of the other components, to achieve this is usually used "interfaces" of integration. In other words, no matter how technologically is implemented each of the components, and the integration between the components is made ​​via interfaces.

**Proposed architecture**

We believe that at the front-end the best product we can use is Liferay Portal, while in the back-end, ie the e-commerce engine, is KonaKart.
Liferay allows us to quickly build Web Portals with social capabilities. In addition there is plenty of technical information on programming, development support tools, frameworks, bibliography and an active community.
While KonaKart implements all the functionalities related to the store, from the sale and to the management (of products, orders, stock, etc..).
Our proposal involves take full advantage of existing functionalities of KonaKart and do not change the KonaKart core.
The communication/integration between front-end and back-end is done via SOAP API of KonaKart, both for the store (Store Front) and the store management (Admin).
We use Liferay's ability to mashup and its social features such as:
* Social Login
* Document management (product sheet)
* Mashup
* Activity Monitoring

[caption id="" align="alignnone" width="494"][![Social eCommerce Architecture with opensource]({{ site.baseurl }}/assets/social-ecomm-01-architecture.png)](https://dl.dropboxusercontent.com/u/2961879/blog20130625_social_ecommerce/social-ecomm-01-architecture.png) Social eCommerce Architecture with opensource[/caption]
During the initial stage of project, definition and feasibility assessment, we had to justify our proposal, here are some of them:

**Why KonaKart ?**

Currently there are many eCommerce projects, some 100% Free / Open Source, others using PHP technology, etc..
KonaKart was chosen for these reasons:
* Java, although the view of KonaKart Admin webapp is implemented in GWT.
* API. For KonaKart will use the SOAP-based API, since it 100% access the functionality of the store as the administration.
* Easily extensible. You can import KonaKart to Eclipse and from there extend its functionality.
Although KonaKart StoreFront can be deployed in Liferay, our project did not require it. Our strategy was to avoid refactor the StoreFront and instead make ad-hoc development based on multiple Portlets, all consuming KonaKart API.

**What eCommerce products were considered?**

Among the products considered were all those with an API (SOAP or REST)​​, and preferably it is implemented in Java. Also be prepared to be extended.
Among them:
1. Apache OFBiz - <https://ofbiz.apache.org>
2. JadaSite - <http://www.jadasite.com>
3. Broadleaf Commerce - <http://www.broadleafcommerce.org>
4. Shopizer - <http://www.shopizer.com>
5. Magento - <http://www.magentocommerce.com>
6. KonaKart - <http://www.konakart.com>
Of all these options, Broadleaf and KonaKart were the best alternatives considering the requirements listed above (Time-To-Market, Extensibility and API).
If you have other requirements and have experience in technologies such as PHP, it may be a good alternative Magento.

**What other Portal products were considered?**

In this part we had no doubt, Liferay Portal as development and integration platform is definitely the best alternative. Liferay also incorporates a CMS, Web 2.0 features and robustness.

**KonaKart is Web 2.0?**

KonaKart need not be Web2.0 or incorporate social features, since Liferay and portlets that we will develop have Web2.0 and social features.
KonaKart has some Web 2.0 features, however, through Liferay social features that we require will be covered. ****

[caption id="" align="alignnone" width="462"][![Opensource products for building eCommerce platform]({{ site.baseurl }}/assets/social-ecomm-02-konakart-liferay-wso2.png)](https://dl.dropboxusercontent.com/u/2961879/blog20130625_social_ecommerce/social-ecomm-02-konakart-liferay-wso2.png) Opensource products for building eCommerce platform[/caption]

**From Social eCommerce to Social Business**

Although it was not in the project requirements, it is possible to integrate existing applications of the organization to recently created eCommerce solution. We can integrate from CRM like SugarCRM, vTiger or any ERP like SAP, etc.. All this is possible because posed architecture is based on layers, ie, the presentation layer and the service layer are completely decoupled, this allows us to register in WSO2 ESB the entire API (SOAP) of KonaKart, similarly through WSO2 ESB can fully integrate heterogeneous applications to eCommerce solution, obviously, each of them can be integrated through its service layer exposure.
If through Liferay could incorporate social features eCommerce solution, thanks to WSO2 ESB can also incorporate social features to any organization's internal application.

**From Social eCommerce to Mobile eCommerce**

In the short term we can make the jump to Mobile eCommerce, ie access to the store and buy from a mobile device. This is possible through the use of Liferay Portal.
The choice of Liferay Portal was not only for his ability to create Agile portlets and put into production, as well as web platform quickly adaptable to terminals of reduced size as the Smartphone or Tablets.
Thanks to Liferay Portal can create a customizable web interface to mobile terminals in a very fast using the strategy of "responsive web design".
In conclusion, I believe that at this time any organization can build a Social eCommerce platform that meets your needs using only opensource products, in response to the lack of solutions in the market, for example, an eCommerce platform flexible, adaptable, scalable, time to market, social, mobile, integrated with CRM / ERP existing in the organization, etc..
Regards.
