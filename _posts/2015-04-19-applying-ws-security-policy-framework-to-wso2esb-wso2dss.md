---
layout:     post
title:      'Applying WS-Security Policy Framework to Webservices (WSO2 ESB) and Dataservices (WSO2 DSS)'
date:       2015-04-19 16:30:28
categories: ['EIP', 'Security', 'SOA']
tags:       ['Apache Neethi', 'soapUI', 'WS-Policy']
status:     publish 
permalink:  "/2015/04/19/applying-ws-security-policy-framework-to-wso2esb-wso2dss/"
---
The [OASIS WS-SecurityPolicy](http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.3/os/ws-securitypolicy-1.3-spec-os.html) language is built on top of [W3C WS-Policy TR's framework](http://www.w3.org/TR/ws-policy) and defines a set of policy assertions that can be used in a granular way to apply or to secure at message-level, in part of message, at operation-level or even at transport-level or service-level.  
Those individual policy assertions can be combined using policy operators defined in the WS-Policy framework to create security policies that can be used to secure our exposed services in WSO2 ESB and/or WSO2 DSS.  
In this post entry I will explain how to apply custom `WS-SecurityPolicy` to exposed services of WSO2 ESB 4.8.1 and WSO2 DSS 3.2.1 and will explore some common use cases. Since WSO2 ESB 4.8.1 and WSO2 DSS 3.2.1 have the same security module, any example will work on both.

![WS-Security Use cases]({{ site.baseurl }}/assets/wso2-ws-security-policy-1scenarios.png)

<!-- more -->


## I. Use cases

Again, I recommend the blog post of Sagara Gunathunga (Technical Leader of WSO2 Inc). There, Sagara explains the different use cases where we can apply WS-SecurityPolicy on services using WSO2. http://ssagara.blogspot.co.uk/2013/07/wso2-esb-set-ws-security-ut-user-names.html I think that this post is great to get starting with WS-Security and WS-Policy in WSO2 ESB/DSS and hope this is also useful for you.
Below, I will explore some details about these scenarios or use cases.

### I.1. Using by default the existing WSO2 20 WS-SecurityPolicy

This is the easiest way to apply security to services in WSO2 ESB and/or WSO2 DSS. The security module embeded into WSO2 ESB and DSS implements 20 security scenarios, they are in the local registry of WSO2 ESB/DSS, under `/_system/config/repository/components/org.wso2.carbon.security.mgt/policy/`:  

![WSO2 WS Security Policies]({{ site.baseurl }}/assets/wso2-ws-security-policy-2list-policies.png)
If you do want to go further with the 20 existing WS-SecurityPolicy deployed in WSO2 ESB/DSS, you can browse all XML policy file, they here are: https://github.com/Chilcano/wso2-dss-3.2.1-security-ws-policy-files
To enable the security on the services, just click on the webservice or dataservice and choose the required Policy of the Policy List.  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-3apply-sec.png)
Now, select UsernameToken Authentication.  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-4apply-sec.png)
And next screen select the user credentials to be required.  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-5apply-sec.png)
After that, you are ready to deploy your service in a secure way. To know what happened behind of this, we should review the Policy applied. Below a copy of Usertoken Authentication Policy (scenario1 UTOverTransport):  
https://gist.github.com/9805c4369612be163ccc
**Interpreting the WS-SecurityPolicy UsenameTokenOverTransport**
* HTTPS is required instead of HTTP. HTTP is not already available. 
* Send credentials (user and password) in the message over HTTPS 
* The password should be encrypted 
**Testing the Echo Service with WS-SecurityPolicy UsenameTokenOverTransport enabled**
Using SoapUI will create a soapui project from the new WSDL of Echo Service. After this, send a request without the required valid credentials.  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-6soapui-ut-https.png)
Now, if you send a request with valid credentials, then you will receive a successfully message.  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-7soapui-ut-https.png)
**Observations**
* Any of 20 WS-SecurityPolicy uses or consider to WSO2 instance as container of user credentials. In other words, for example, if you select UsernameToken Authentication Policy, the authentication process will validate the Username Token againts to the WSO2 User Store.

### I.2. Apply a custom WS-SecurityPolicy

What happens if your requirements are different and any of the 20 existing policies do not meets the requirements?. Well, if you have knowledge about WS-SecurityPolicy you could create one following your requirements. Remember, with WS-SecurityPolicy I can apply security at message-level, operaton-level and at transport-level, as example in this blog I will explain how to add HTTP support to secured SOAP Echo Service deployed by default in WSO2.
**Sample of custom Policy: Adding HTTP support using custom WS-SecurityPolicy**
Let me explain further on this sample. Whe you enable any of 20 WS-SecurityPolicy to improve the security on the services, it is common sense leave only enabled HTTPS (SOAP over SSL/TLS) automatically, because HTTP is insecure if this is used over public networks as Internet. Then, this sample scenario is only for testing purposes. Lets go to create the WS-SecurityPolicy file, in this case, I will use `scenario1_UTOverTransport.xml` Policy, will remove the part related to "TransportBinding" because I want to keep HTTP and HTTPS enabled for Echo Service.
The remaining part of scenario1_UTOverTransport.xml Policy is related to the required credentials to call this service. The new Policy (UTOverTransport policy with HTTP support added) will be as shown below.  
https://gist.github.com/42aab5c3b67e9c667dba
**Upload the UsenameTokenOverTransport Policy with HTTP support added to WSO2's Registry repository**
Basically, upload the new Policy file to WSO2's Registry repository, in my case I have uploaded it as resource under `/_system/config/repository/components/org.wso2.carbon.security.mgt/policy`, but you could place it in any location.  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-8policy-ut-http.png)
**Testing the Echo Service UsenameTokenOverTransport with HTTP support added**
Before testing, we have to apply this new Policy the Echo Service. Follow the steps as in the previous Policy escenario.  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-9wso2-set-new-policy.png)
At the end, on the Echo Service in WSO2 web console you will see a new Endpoint over HTTP.  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-10wso2-set-new-policy.png)
Now, using SoapUI will add a new Endpoint of Echo Service, in this case using HTTP. After this, send a request without the required valid credentials and using the new added Endpoint (over HTTP).  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-11soapui-new-policy-error.png)
Above is shown an error because the new Policy requires valid user credentials in SOAP request message header. Now, if you send over HTTP a request with valid credentials, then you will receive a successfully message as shown below.  

![]({{ site.baseurl }}/assets/wso2-ws-security-policy-12soapui-new-policy-ok.png)

### I.3. Applying advanced custom WS-SecurityPolicy

Since it is possible to incorporate your own policies at WSO2, you could implement more complex or customized scenarios of security. Some more advanced scenarios are:
1. The credentials repository is managed by Identity Management System in the backend, not by WSO2 ESB or WSO2 DSS.
2. The external users who consume my services are authenticated with credentials that should be validated in the backend.
3. Specified parts of a message, such as header, body, and so on, should be signed or encrypted.
4. Specified elements within a message should be signed or encrypted.
5. Specials encryption algorithms or timestamps in a message are requireds.
In the next posts I will explain each advanced scenarios. Stay tuned ;)

## II. Conclusions

It is very important to learn the concepts of WS-Security and WS-Policy. Without this, if you implement a security policy and apply it on your web services, you probably would have security risks.  
There are a lot of information on WS-Security and WS-Policy on Internet, below there is a list of resources. Also a great resource is (Apache Neethi)(http://ws.apache.org/neethi), it is a framework that implements WS-Security Policy, this is used in WSO2, this framework is accompanied by a series of examples that make your learning easier. Anyway you may incur to errors in the creation of the policies, then I recommend this Java application called [WS-Policy Editor](https://github.com/IAAS/WS-Policy-Editor) that allows easy editing.

## III. References
* Understanding WS-SecurityPolicy. By Nandana Mihindukulasooriya (http://wso2.com/library/3132)
* Java Web services: Granular use of WS-Security: Applying WS-Security at the operation or message level (https://www.ibm.com/developerworks/webservices/library/j-jws7)
* Understanding web services specifications, Part 5: WS-Policy (http://www.ibm.com/developerworks/webservices/tutorials/ws-understand-web-services5)
* Java web services: Modeling and verifying WS-SecurityPolicy (http://www.ibm.com/developerworks/library/j-jws21)
* WS-SecurityPolicy 1.3, OASIS Standard, 2 February 2009 (http://docs.oasis-open.org/ws-sx/ws-securitypolicy/v1.3/os/ws-securitypolicy-1.3-spec-os.html)
* Web Services Policy 1.5 - Framework. W3C Recommendation 04 September 2007 (http://www.w3.org/TR/ws-policy)
* A Java-Swing-based editor for WS-Policies (https://github.com/IAAS/WS-Policy-Editor)
* Apache Neethi, the referent framework to use WS Policy (http://ws.apache.org/neethi)
