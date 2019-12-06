---
layout: post
title: Testing Alfresco Webservices with soapUI
date: 2010-07-26 16:11:09.000000000 +02:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- ECM
tags:
- Alfresco ECM
- soapUI
meta:
  _edit_last: '578869'
  _wpas_done_twitter: '1'
  _wp_old_slug: ''
  _wpas_skip_5053092: '1'
  _wpcom_is_markdown: '1'
  _wpas_skip_5053089: '1'
  _oembed_e1f286feac6b02ed8dd1c8e962bff059: "{{unknown}}"
  _oembed_bf89e3b7e3e82d294670205526ae84b3: "{{unknown}}"
  geo_public: '0'
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/07/26/testing-alfresco-webservices-with-soapui/"
---
Alfresco ECM has a WS layer that implement all content management services as upload, search, transform, checkin/checkout, etc of any content stored in the repository.

  
This WS layer enables to Business Application easier integration of content management functionalities. It is only necessary to invoke the Alfresco's WS for the desired functionality. Alfresco has two types of WS set, one set implements WS of specific way and the other WS layer follows the CMIS standard, in the end, both layers offer the same services in an equivalent manner.  
  
![SoapUI Testing Alfresco Webservices]({{ site.baseurl }}/assets/smartbear-soapui-logo-300x140.png)

  
<!-- more -->

  
 **Design of Test Master Plan: Define the scope**

  
For testing purposes, I recommend that you first define the scope of testing:

  
  

  * What do you want to test?:
  

  * How much you want to test?: type of test, i.e. regression, load test, etc.
  

  
Then, you create a Test Suite with several test cases as many as you want to execute functionalities or WS.

  
 **Implement Test Suite with soapUI**

  
  

  1. Identify URL/endpoints for each functionality to be tested
  

  2. Create test-cases for all functionality to be tested
  

  3. Automates each test case with soapUI by programming with groovy (soapUI scripting)
  

  4. Add indicators to measure the performance
  

  
 **Run Test Suite**

  
Define the initial conditions to execute the test, for example, time of execution, ignore errors, etc.

  
[![soapui_alf33ce_1_testrunner]({{ site.baseurl }}/assets/soapui_alf33ce_1_testrunner.png)](http://holisticsecurity.wordpress.com/2010/07/26/testing-alfresco-webservices-with-soapui/soapui_alf33ce_1_testrunner/)  
  
 _soapUI Launch TestRunner settings_

  
Alter a quick introduction, now will test Alfresco ECM, firstly, we will do queries to Alfresco’s Dictionary, after, and we will automate the creation of content in Alfresco.

  
 **Test Suite sample: "Testing Alfresco’s Dictionary Service"**

  
[![]({{ site.baseurl }}/assets/soapui_alf33ce_2_testsuite_dictionaryservice.png)](http://holisticsecurity.files.wordpress.com/2010/07/soapui_alf33ce_2_testsuite_dictionaryservice.png)  
  
soapUI Test Suite - Alfresco's DictionaryService

  
Steps in the test suite:

  
  

  * startSession:
  

  
We must be authenticated before accessing resources Alfresco. We must invoke the URL (<http://localhost:8080/alfresco/api/AuthenticationService> and startSession method/service) passing username/password, Alfresco should respond by sending a TICKET_ID.

  
[![]({{ site.baseurl }}/assets/soapui_alf33ce_3_startsession_req.png)](http://holisticsecurity.files.wordpress.com/2010/07/soapui_alf33ce_3_startsession_req.png)  
  
StartSession SOAP request in Alfresco

[![]({{ site.baseurl }}/assets/soapui_alf33ce_4_startsession_res.png)](http://holisticsecurity.files.wordpress.com/2010/07/soapui_alf33ce_4_startsession_res.png)  
StartSession SOAP response in Alfresco

  * Queries: getAssociations, getClasses, getProperties, …

For each SOAP query, we must identify SOAP endpoint (for example, <http://localhost:8080/alfresco/api/DirectoryService> and getAssociations) and method/service, then web have to pass a valid TICKET_ID.

[![]({{ site.baseurl }}/assets/soapui_alf33ce_5_getassociations.png)](http://holisticsecurity.files.wordpress.com/2010/07/soapui_alf33ce_5_getassociations.png)  
GetAssociations SOAP request in Alfresco

This process we must repeat for each step (SOAP endpoint and method/service) that we identify in our tests.

  * endSession:

For close session we have to invoke to <http://localhost:8080/alfresco/api/AuthenticationService> and endSession method using the initial TICKET_ID created in step 1.

I have created two Test Suites with soapUI that you can import to workspace.

The first make several queries to Alfresco’s Dictionary (in this case you need to modify the standard content model of Alfresco or simply edit each test step of this soapUI project). The second test suite creates new content in the guest_home space and then transform to PDF.

  * Download [soapUI project for Alfresco v. 3.4d](https://dl.dropboxusercontent.com/u/2961879/blog20111203_alf34_ws_rest_testing/alf34dce_ws_rest_soapui_prj.zip)

I hope that is useful.