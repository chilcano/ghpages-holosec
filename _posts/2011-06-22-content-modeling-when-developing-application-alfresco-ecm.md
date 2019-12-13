---
layout:     post
title:      'Content Modeling when developing application in Alfresco ECM'
date:       2011-06-21 23:24:23
categories: ['ECM']
tags:       ['Alfresco']
status:     publish 
permalink:  "/2011/06/22/content-modeling-when-developing-application-alfresco-ecm/"
---
When developing applications, you start with a stage of analysis-design that allows modeling the structure of the database that you application will use.  
So why not do something similar when developing applications on Alfresco ECM?.
What makes Alfresco ECM a powerful tool is the ability to do content modeling. If you do not that, would not make much sense to use Alfresco ECM and instead should use any file-system such as WebDAV, FTP, NFS, etc.
The content model in Alfresco ECM allows us to identify types of documents, properties and their relationships existing in our organization/business.
This post will explain how to modeling the content that our type of organization/business requires and also explains how to deploy it on an installation of Alfresco ECM.

## I. XML definition files 

Create the following files:

**1\. intix-model-context.xml**

This is Spring file configuration for the new content model.

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version='1.0' encoding='UTF-8'?>  
<!DOCTYPE beans PUBLIC '-//SPRING//DTD BEAN//EN' 'http://www.springframework.org/dtd/spring-beans.dtd'>  
<beans>  
<!-- Registration of new models -->  
<bean id="intix.dictionaryBootstrap"  
parent="dictionaryModelBootstrap"  
depends-on="dictionaryBootstrap">  
<property name="models">  
<list>  
<value>alfresco/extension/intixModel.xml</value>  
</list>  
</property>  
</bean>  
</beans>  

[/sourcecode]

**2\. intixModel.xml**

This xml file contains our custom model.

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<!-- Definition of new Model -->  
<model name="ix:intixmodel" xmlns="http://www.alfresco.org/model/dictionary/1.0">  
<!-- Optional meta-data about the model -->  
<description>INTIX.info Content Model</description>  
<author>Roger Carhuatocto</author>  
<version>1.0</version>
<!-- Imports are required to allow references to definitions in other models -->  
<imports>  
<import uri="http://www.alfresco.org/model/dictionary/1.0" prefix="d" />  
<import uri="http://www.alfresco.org/model/content/1.0" prefix="cm" />  
<import uri="http://www.alfresco.org/model/system/1.0" prefix="sys" />  
</imports>
<!-- Introduction of new namespaces defined by this model -->  
<namespaces>  
<namespace uri="http://www.intix.info/model/content/1.0" prefix="ix" />  
</namespaces>
<constraints>  
<constraint name="ix:projectList" type="LIST">  
<parameter name="allowedValues">  
<list>  
<value>Foo Project</value>  
<value>Bar Project</value>  
<value>Acme Project</value>  
</list>  
</parameter>  
</constraint>  
</constraints>
<types>  
<!-- Enterprise-wide generic document type -->  
<type name="ix:doc">  
<title>INTIX.info Document</title>  
<parent>cm:content</parent>  
<associations>  
<association name="ix:relatedDocuments">  
<title>INTIX.info Related Documents</title>  
<source>  
<mandatory>false</mandatory>  
<many>true</many>  
</source>  
<target>  
<class>ix:doc</class>  
<mandatory>false</mandatory>  
<many>true</many>  
</target>  
</association>  
</associations>  
<mandatory-aspects>  
<aspect>cm:generalclassifiable</aspect>  
</mandatory-aspects>  
</type>
<type name="ix:legalDoc">  
<title>INTIX.info Legal Document</title>  
<parent>ix:doc</parent>  
</type>
<type name="ix:fld">  
<title>INTIX.info Folder Document</title>  
<parent>cm:folder</parent>  
<associations>  
<association name="ix:relatedFolder2Docs">  
<title>INTIX.info Documents Related to Folder</title>  
<source>  
<mandatory>false</mandatory>  
<many>true</many>  
</source>  
<target>  
<class>ix:doc</class>  
<mandatory>false</mandatory>  
<many>true</many>  
</target>  
</association>  
</associations>  
</type>
<type name="ix:marketingDoc">  
<title>INTIX.info Marketing Document</title>  
<parent>ix:doc</parent>  
<properties>  
<property name="ix:project">  
<type>d:text</type>  
<multiple>true</multiple>  
<constraints>  
<constraint ref="ix:projectList" />  
</constraints>  
</property>  
</properties>  
</type>  
</types>  
</model>  

[/sourcecode]

**3\. web-client-config-custom.xml**

This configuration file enables Alfresco Explore/Web Client to load the new content model in the Alfresco UI.

[sourcecode language="xml" gutter="true" wraplines="false"]  
<alfresco-config>  
<!-- show related documents association on doc property sheet -->  
<config evaluator="node-type" condition="ix:doc">  
<property-sheet>  
<show-association name="ix:relatedDocuments" />  
</property-sheet>  
</config>  
<!-- show related documents association on FOLDER property sheet -->  
<config evaluator="node-type" condition="ix:fld">  
<property-sheet>  
<show-association name="ix:relatedFolder2Docs" />  
</property-sheet>  
</config>  
<!-- show PROJECT on marketingDoc property sheet -->  
<config evaluator="node-type" condition="ix:marketingDoc">  
<property-sheet>  
<show-property name="ix:project" display-label-id="project" />  
</property-sheet>  
</config>  
<!-- show related documents association on DOC property sheet -->  
<config evaluator="node-type" condition="ix:doc">  
<property-sheet>  
<show-association name="ix:relatedDocuments" />  
</property-sheet>  
</config>  
<!-- add NEW CONTENT types to add content list wizard -->  
<config evaluator="string-compare" condition="Content Wizards">  
<content-types>  
<type name="ix:doc" />  
<type name="ix:legalDoc" />  
<type name="ix:marketingDoc" />  
</content-types>  
</config>  
<!-- add new FOLDER types to add content list wizard -->  
<config evaluator="string-compare" condition="Space Wizards">  
<folder-types>  
<type name="ix:fld" />  
</folder-types>  
</config>  
<config evaluator="string-compare" condition="Action Wizards">  
<!-- The list of types shown in the is-subtype condition -->  
<subtypes>  
<type name="ix:doc" />  
<type name="ix:legalDoc" />  
<type name="ix:marketingDoc" />  
</subtypes>  
<!-- The list of content and/or folder types shown in the specialise-type action -->  
<specialise-types>  
<type name="ix:doc" />  
<type name="ix:legalDoc" />  
<type name="ix:marketingDoc" />  
<type name="ix:fld" />  
</specialise-types>  
</config>  
<config evaluator="string-compare" condition="Advanced Search">  
<advanced-search>  
<content-types>  
<type name="ix:doc" />  
<type name="ix:legalDoc" />  
<type name="ix:marketingDoc" />  
<type name="ix:fld" />  
</content-types>  
</advanced-search>  
</config>  
</alfresco-config>  

[/sourcecode]

## II. Deploy the content model 

1\. Stop Alfresco.
2\. Copy **intix-model-context.xml** to **${ALFRESCO_HOME}\tomcat\shared\classes\alfresco\extension**
3\. Copy **intixModel.xml** to **${ALFRESCO_HOME}\tomcat\shared\classes\alfresco\extension**
4\. At this point, you will still not actually can not "see" the new custom model on the Alfresco UI (Alfresco Explorer/Web Client).  
For that, you will need to set up the property dialogs for each of the content types.
Then, copy **web-client-config-custom.xml** to **${ALFRESCO_HOME}\tomcat\shared\classes\alfresco\extension**
5\. Start Alfresco.
6\. Verify successfully deployment of new content model by browsing on Alfresco Explorer/Web Client.

[caption id="" align="alignnone" width="432" caption="Step 1 - Upload a new type of content"]![Step 1 - Upload a new type of content]({{ site.baseurl }}/assets/01_upload_content.png)[/caption]

[caption id="" align="alignnone" width="350" caption="Step 2 - Modify properties (metadata) of new uploaded content"]![Step 2 - Modify properties \(metadata\) of new uploaded content]({{ site.baseurl }}/assets/02_upload_content.png)[/caption]

[caption id="" align="alignnone" width="463" caption="Step 3 - Modify properties of new type content"]![Step 3 - Modify properties of new type content]({{ site.baseurl }}/assets/03_upload_content.png)[/caption]
7\. End.
