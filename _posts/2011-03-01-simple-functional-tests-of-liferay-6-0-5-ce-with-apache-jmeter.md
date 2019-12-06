---
layout: post
title: Simple functional tests of Liferay 6.0.5 CE with Apache JMeter
date: 2011-03-01 01:49:05.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Portal
tags:
- JMeter
- Liferay
meta:
  _edit_last: '578869'
  _wpas_done_twitter: '1'
  _oembed_86cb0b2af82863e0aab12d0c2405ea28: "{{unknown}}"
  _oembed_13406119b77fbc009810130eb38ecd93: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2011/03/01/simple-functional-tests-of-liferay-6-0-5-ce-with-apache-jmeter/"
---
If do you want to know the health of an easy and quickly way, the current status of your Liferay Portal installation or if you want to test quickly any basic functionality from UI (quickly functional test), then you could use Apache JMeter to do it.  
  
  
  
To do that, you should do the following:  
  
  
  
1\. Download and install Apache JMeter from here <http://jakarta.apache.org/site/downloads/downloads_jmeter.cgi>  
  
  
  
2\. Identify the target to test it.  
  
In this case we will test if Liferay Sign in portlet works.  
  
  
  
3\. Create a Test Plan.  
  
  
  
4\. Add a "Thread Group".  
  
  
  
5\. In the before "Thread Group" to add a "HTTP Cookie Manager". This allows to keep the liferay session open between diferents HTTP requests.  
  
  
  
[caption id="" align="alignnone"  
  
caption="Create a HTTP Cookie Manager in JMeter for Liferay"]![Create a HTTP Cookie Manager in JMeter for Liferay]({{ site.baseurl }}/assets/20110228_1_liferayjmeter_httpcookiemanager.png)  
  
[/caption]  
  
  
  
6\. Also, to add an "User Defined Variables" as shows in next figure:  
  
  
  
[caption id="" align="alignnone"  
  
caption="Create a User Defined Variables in JMeter for Liferay"]  
  
![Create a User Defined Variables in JMeter for Liferay]({{ site.baseurl }}/assets/20110228_2_liferayjmeter_userdefinedvariables.png)  
  
[/caption]  
  
  
  
7\. In the before "Thread Group" to add a "Loop Controller" named "Sign in to Liferay".  
  
  
  
[caption id="" align="alignnone"  
  
caption="Add a Loop Controller"]  
  
![Add a Loop Controller]({{ site.baseurl }}/assets/20110228_3_liferayjmeter_loopcontroller.png)  
  
[/caption]  
  
  
8\. Add a first "HTTP Resquest" named "[Sign in - 1] Go to Welcome page".  
You have to fill out the request with the appropriate parameters, for exmaple:  
\- Server Name or IP  
\- Port Number  
\- Method  
\- Path (URL of our target to be tested)  
  
9\. Then, to add the first "Response Assertion" named "Check resp. 1 ". This will check if the result is correct. In this case only verify the existence of a message/text in the HTTP response.  
  
10\. Later, to add the second "HTTP Request" named "[Sign in - 2] Do login". This allows to do log into liferay, in this case we will need to compose a HTML form with the following parameters:  
  
[caption id="" align="alignnone"  
caption="Create a HTTP Request to do Login from Liferay Sing in Portlet"]  
![Create a HTTP Request to do Login from Liferay Sing in Portlet]({{ site.baseurl }}/assets/20110228_4_liferayjmeter_httprequestsignin.png)  
[/caption]  
  
These parameters have been extracted from the Liferay welcome web page that contains the "Sign in" portlet, as shown in the figure below:  
  
[caption id="" align="alignnone"  
caption="Existing parameters in Liferay Sign in Portlet form"]  
![Existing parameters in Liferay Sign in Portlet form]({{ site.baseurl }}/assets/20110228_5_liferayjmeter_signinformparams.png)  
[/caption]  
  
11\. Create a "Response Assertion" of similarly way to step 9 if we want to verify if login process has been successful.  
In my case, I will verify the existence of a message/text "Sign Out" in the HTTP response.  
  
[caption id="" align="alignnone"  
caption="Create a Response Assertion for Liferay Login process"]  
![Create a Response Assertion for Liferay Login process]({{ site.baseurl }}/assets/20110228_6_liferayjmeter_respassertion.png)  
[/caption]  
  
12\. Add a third "HTTP Request" named "[Sign in - 3] Do logout" and other "Response Assertion".  
This step is optional, it does not help us verify if the login process has been OK.  
  
13\. At level of the "Thread Group" create a "Vire Results Tree" and "Assertion Results". Both will allow us to monitor the test results.  
  
14\. Run test plan and observe the results in the "View Result Tree".  
If everything goes “green” is that everything has been correctly. If any test is “red”, is that the test is wrong or something is wrong. Then, you should see or check you network connection, Liferay is running or if you Database is running, etc.  
  
[caption id="" align="alignnone"  
caption="View Result Tree for Liferay Sign in portlet"]  
![View Result Tree for Liferay Sign in portlet]({{ site.baseurl }}/assets/20110228_7_liferayjmeter_viewresultstree.png)  
[/caption]  
  
You can copy this test plan in Apache JMeter:  
  
[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<jmeterTestPlan version="1.2" properties="2.1">  
<hashTree>  
<TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Test Plan - Liferay Sign in portlet" enabled="true">  
<stringProp name="TestPlan.comments">Monitoring Liferay Portal 6.0.5 CE</stringProp>  
<boolProp name="TestPlan.functional_mode">false</boolProp>  
<boolProp name="TestPlan.serialize_threadgroups">false</boolProp>  
<elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">  
<collectionProp name="Arguments.arguments"/>  
</elementProp>  
<stringProp name="TestPlan.user_define_classpath"></stringProp>  
</TestPlan>  
<hashTree>  
<ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Thread Group" enabled="true">  
<elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller" enabled="true">  
<boolProp name="LoopController.continue_forever">false</boolProp>  
<stringProp name="LoopController.loops">1</stringProp>  
</elementProp>  
<stringProp name="ThreadGroup.num_threads">1</stringProp>  
<stringProp name="ThreadGroup.ramp_time">1</stringProp>  
<longProp name="ThreadGroup.start_time">1281132211000</longProp>  
<longProp name="ThreadGroup.end_time">1281132211000</longProp>  
<boolProp name="ThreadGroup.scheduler">false</boolProp>  
<stringProp name="ThreadGroup.on_sample_error">continue</stringProp>  
<stringProp name="ThreadGroup.duration"></stringProp>  
<stringProp name="ThreadGroup.delay"></stringProp>  
</ThreadGroup>  
<hashTree>  
<CookieManager guiclass="CookiePanel" testclass="CookieManager" testname="HTTP Cookie Manager" enabled="true">  
<collectionProp name="CookieManager.cookies"/>  
<stringProp name="TestPlan.comments">Enable to keep session in Liferay</stringProp>  
<boolProp name="CookieManager.clearEachIteration">false</boolProp>  
<stringProp name="CookieManager.policy">rfc2109</stringProp>  
</CookieManager>  
<hashTree/>  
<Arguments guiclass="ArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">  
<collectionProp name="Arguments.arguments">  
<elementProp name="LFRY_HOSTNAME" elementType="Argument">  
<stringProp name="Argument.name">LFRY_HOSTNAME</stringProp>  
<stringProp name="Argument.value">lfry01</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
<elementProp name="LFRY_PORT" elementType="Argument">  
<stringProp name="Argument.name">LFRY_PORT</stringProp>  
<stringProp name="Argument.value">8080</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
<elementProp name="LFRY_NON_ROOT_CTX" elementType="Argument">  
<stringProp name="Argument.name">LFRY_NON_ROOT_CTX</stringProp>  
<stringProp name="Argument.value">intixportal</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
<elementProp name="LFRY_TEST_USER" elementType="Argument">  
<stringProp name="Argument.name">LFRY_TEST_USER</stringProp>  
<stringProp name="Argument.value">test</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
<elementProp name="LFRY_TEST_PWD" elementType="Argument">  
<stringProp name="Argument.name">LFRY_TEST_PWD</stringProp>  
<stringProp name="Argument.value">test</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
<elementProp name="LFRY_ASSERTION_MSG_1" elementType="Argument">  
<stringProp name="Argument.name">LFRY_ASSERTION_MSG_1</stringProp>  
<stringProp name="Argument.value">Password</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
<elementProp name="LFRY_ASSERTION_MSG_2" elementType="Argument">  
<stringProp name="Argument.name">LFRY_ASSERTION_MSG_2</stringProp>  
<stringProp name="Argument.value">Sign Out</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
<elementProp name="LFRY_ASSERTION_MSG_3a" elementType="Argument">  
<stringProp name="Argument.name">LFRY_ASSERTION_MSG_3a</stringProp>  
<stringProp name="Argument.value">HTTP/1.1 200 OK</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
<elementProp name="LFRY_ASSERTION_MSG_3b" elementType="Argument">  
<stringProp name="Argument.name">LFRY_ASSERTION_MSG_3b</stringProp>  
<stringProp name="Argument.value">Liferay-Portal: Liferay Portal</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
<elementProp name="LFRY_ASSERTION_MSG_3c" elementType="Argument">  
<stringProp name="Argument.name">LFRY_ASSERTION_MSG_3c</stringProp>  
<stringProp name="Argument.value">Set-Cookie: JSESSIONID=</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
</elementProp>  
</collectionProp>  
<stringProp name="TestPlan.comments">Define my liferay parameters</stringProp>  
</Arguments>  
<hashTree/>  
<LoopController guiclass="LoopControlPanel" testclass="LoopController" testname="Sign in to Liferay" enabled="true">  
<boolProp name="LoopController.continue_forever">false</boolProp>  
<stringProp name="LoopController.loops">1</stringProp>  
<stringProp name="TestPlan.comments">Log into Liferay Portal</stringProp>  
</LoopController>  
<hashTree>  
<HTTPSampler guiclass="HttpTestSampleGui" testclass="HTTPSampler" testname="[Sign in - 1] Go to Welcome page" enabled="true">  
<elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">  
<collectionProp name="Arguments.arguments"/>  
</elementProp>  
<stringProp name="HTTPSampler.domain">${LFRY_HOSTNAME}</stringProp>  
<stringProp name="HTTPSampler.port">${LFRY_PORT}</stringProp>  
<stringProp name="HTTPSampler.connect_timeout"></stringProp>  
<stringProp name="HTTPSampler.response_timeout"></stringProp>  
<stringProp name="HTTPSampler.protocol">http</stringProp>  
<stringProp name="HTTPSampler.contentEncoding"></stringProp>  
<stringProp name="HTTPSampler.path">/${LFRY_NON_ROOT_CTX}</stringProp>  
<stringProp name="HTTPSampler.method">GET</stringProp>  
<boolProp name="HTTPSampler.follow_redirects">false</boolProp>  
<boolProp name="HTTPSampler.auto_redirects">true</boolProp>  
<boolProp name="HTTPSampler.use_keepalive">true</boolProp>  
<boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>  
<boolProp name="HTTPSampler.monitor">false</boolProp>  
<stringProp name="HTTPSampler.embedded_url_re"></stringProp>  
<stringProp name="TestPlan.comments">[Sign in - 1] Go to Welcome page</stringProp>  
</HTTPSampler>  
<hashTree>  
<ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Check resp. 1 " enabled="true">  
<collectionProp name="Asserion.test_strings">  
<stringProp name="-1010617650">${LFRY_ASSERTION_MSG_1}</stringProp>  
</collectionProp>  
<stringProp name="Assertion.test_field">Assertion.response_data</stringProp>  
<boolProp name="Assertion.assume_success">false</boolProp>  
<intProp name="Assertion.test_type">16</intProp>  
<stringProp name="TestPlan.comments">Check response 1</stringProp>  
</ResponseAssertion>  
<hashTree/>  
</hashTree>  
<HTTPSampler guiclass="HttpTestSampleGui" testclass="HTTPSampler" testname="[Sign in - 2] Do login" enabled="true">  
<elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">  
<collectionProp name="Arguments.arguments">  
<elementProp name="p_p_id" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value">58</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">p_p_id</stringProp>  
</elementProp>  
<elementProp name="p_p_lifecycle" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value">1</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">p_p_lifecycle</stringProp>  
</elementProp>  
<elementProp name="p_p_state" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value">normal</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">p_p_state</stringProp>  
</elementProp>  
<elementProp name="p_p_mode" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value">view</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">p_p_mode</stringProp>  
</elementProp>  
<elementProp name="saveLastPath" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value">0</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">saveLastPath</stringProp>  
</elementProp>  
<elementProp name="_58_struts_action" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value">/login/login</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">_58_struts_action</stringProp>  
</elementProp>  
<elementProp name="_58_redirect" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value"></stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">_58_redirect</stringProp>  
</elementProp>  
<elementProp name="_58_login" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value">${LFRY_TEST_USER}</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">_58_login</stringProp>  
</elementProp>  
<elementProp name="_58_password" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value">${LFRY_TEST_PWD}</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">_58_password</stringProp>  
</elementProp>  
<elementProp name="_58_rememberMe" elementType="HTTPArgument">  
<boolProp name="HTTPArgument.always_encode">true</boolProp>  
<stringProp name="Argument.value">false</stringProp>  
<stringProp name="Argument.metadata">=</stringProp>  
<boolProp name="HTTPArgument.use_equals">true</boolProp>  
<stringProp name="Argument.name">_58_rememberMe</stringProp>  
</elementProp>  
</collectionProp>  
</elementProp>  
<stringProp name="HTTPSampler.domain">${LFRY_HOSTNAME}</stringProp>  
<stringProp name="HTTPSampler.port">${LFRY_PORT}</stringProp>  
<stringProp name="HTTPSampler.connect_timeout"></stringProp>  
<stringProp name="HTTPSampler.response_timeout"></stringProp>  
<stringProp name="HTTPSampler.protocol">http</stringProp>  
<stringProp name="HTTPSampler.contentEncoding"></stringProp>  
<stringProp name="HTTPSampler.path">/${LFRY_NON_ROOT_CTX}/web/guest/home</stringProp>  
<stringProp name="HTTPSampler.method">POST</stringProp>  
<boolProp name="HTTPSampler.follow_redirects">false</boolProp>  
<boolProp name="HTTPSampler.auto_redirects">true</boolProp>  
<boolProp name="HTTPSampler.use_keepalive">true</boolProp>  
<boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>  
<boolProp name="HTTPSampler.monitor">false</boolProp>  
<stringProp name="HTTPSampler.embedded_url_re"></stringProp>  
<stringProp name="TestPlan.comments">[Sign in - 2] Do login</stringProp>  
</HTTPSampler>  
<hashTree>  
<ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Check resp. 2" enabled="true">  
<collectionProp name="Asserion.test_strings">  
<stringProp name="-1010617619">${LFRY_ASSERTION_MSG_2}</stringProp>  
</collectionProp>  
<stringProp name="Assertion.test_field">Assertion.response_data</stringProp>  
<boolProp name="Assertion.assume_success">false</boolProp>  
<intProp name="Assertion.test_type">16</intProp>  
<stringProp name="TestPlan.comments">Check response 2</stringProp>  
</ResponseAssertion>  
<hashTree/>  
</hashTree>  
<HTTPSampler guiclass="HttpTestSampleGui" testclass="HTTPSampler" testname="[Sign in - 3] Do logout" enabled="true">  
<elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">  
<collectionProp name="Arguments.arguments"/>  
</elementProp>  
<stringProp name="HTTPSampler.domain">${LFRY_HOSTNAME}</stringProp>  
<stringProp name="HTTPSampler.port">${LFRY_PORT}</stringProp>  
<stringProp name="HTTPSampler.connect_timeout"></stringProp>  
<stringProp name="HTTPSampler.response_timeout"></stringProp>  
<stringProp name="HTTPSampler.protocol">http</stringProp>  
<stringProp name="HTTPSampler.contentEncoding"></stringProp>  
<stringProp name="HTTPSampler.path">/${LFRY_NON_ROOT_CTX}/c/portal/logout</stringProp>  
<stringProp name="HTTPSampler.method">GET</stringProp>  
<boolProp name="HTTPSampler.follow_redirects">false</boolProp>  
<boolProp name="HTTPSampler.auto_redirects">true</boolProp>  
<boolProp name="HTTPSampler.use_keepalive">true</boolProp>  
<boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>  
<boolProp name="HTTPSampler.monitor">false</boolProp>  
<stringProp name="HTTPSampler.embedded_url_re"></stringProp>  
<stringProp name="TestPlan.comments">[Sign in - 3] Do logout</stringProp>  
</HTTPSampler>  
<hashTree>  
<ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Check resp. 3 " enabled="true">  
<collectionProp name="Asserion.test_strings">  
<stringProp name="-1264374899">${LFRY_ASSERTION_MSG_3a}</stringProp>  
<stringProp name="-1264374868">${LFRY_ASSERTION_MSG_3b}</stringProp>  
</collectionProp>  
<stringProp name="Assertion.test_field">Assertion.response_headers</stringProp>  
<boolProp name="Assertion.assume_success">false</boolProp>  
<intProp name="Assertion.test_type">2</intProp>  
<stringProp name="TestPlan.comments">Check response 3 </stringProp>  
</ResponseAssertion>  
<hashTree/>  
</hashTree>  
</hashTree>  
<ResultCollector guiclass="ViewResultsFullVisualizer" testclass="ResultCollector" testname="View Results Tree" enabled="true">  
<boolProp name="ResultCollector.error_logging">false</boolProp>  
<objProp>  
<name>saveConfig</name>  
<value class="SampleSaveConfiguration">  
<time>true</time>  
<latency>true</latency>  
<timestamp>true</timestamp>  
<success>true</success>  
<label>true</label>  
<code>true</code>  
<message>true</message>  
<threadName>true</threadName>  
<dataType>true</dataType>  
<encoding>false</encoding>  
<assertions>true</assertions>  
<subresults>true</subresults>  
<responseData>false</responseData>  
<samplerData>false</samplerData>  
<xml>true</xml>  
<fieldNames>false</fieldNames>  
<responseHeaders>false</responseHeaders>  
<requestHeaders>false</requestHeaders>  
<responseDataOnError>false</responseDataOnError>  
<saveAssertionResultsFailureMessage>false</saveAssertionResultsFailureMessage>  
<assertionsResultsToSave>0</assertionsResultsToSave>  
<bytes>true</bytes>  
</value>  
</objProp>  
<stringProp name="filename"></stringProp>  
</ResultCollector>  
<hashTree/>  
<ResultCollector guiclass="AssertionVisualizer" testclass="ResultCollector" testname="Assertion Results" enabled="true">  
<boolProp name="ResultCollector.error_logging">false</boolProp>  
<objProp>  
<name>saveConfig</name>  
<value class="SampleSaveConfiguration">  
<time>true</time>  
<latency>true</latency>  
<timestamp>true</timestamp>  
<success>true</success>  
<label>true</label>  
<code>true</code>  
<message>true</message>  
<threadName>true</threadName>  
<dataType>true</dataType>  
<encoding>false</encoding>  
<assertions>true</assertions>  
<subresults>true</subresults>  
<responseData>false</responseData>  
<samplerData>false</samplerData>  
<xml>true</xml>  
<fieldNames>false</fieldNames>  
<responseHeaders>false</responseHeaders>  
<requestHeaders>false</requestHeaders>  
<responseDataOnError>false</responseDataOnError>  
<saveAssertionResultsFailureMessage>false</saveAssertionResultsFailureMessage>  
<assertionsResultsToSave>0</assertionsResultsToSave>  
<bytes>true</bytes>  
</value>  
</objProp>  
<stringProp name="filename"></stringProp>  
</ResultCollector>  
<hashTree/>  
</hashTree>  
</hashTree>  
</hashTree>  
</jmeterTestPlan>  
[/sourcecode]

Have a Liferay happy testing!.  

