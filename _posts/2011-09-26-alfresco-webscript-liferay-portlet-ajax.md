---
layout:     post
title:      "Calling Alfresco's Webscripts from a Liferay Portlet using Ajax"
date:       2011-09-26 11:42:59
categories: ['ECM', 'Micro Frontend', 'Microservices', 'Portal']
tags:       ['Alfresco', 'Java Script', 'JSR-286', 'Liferay', 'Portlet']
status:     publish 
permalink:  "/2011/09/26/alfresco-webscript-liferay-portlet-ajax/"
---
I think that we are reaching the maturity level in Liferay and Alfresco, because we can create applications on top of them of fastly and easy way.
Alfresco ECM has functionalities exposed as a RESTful API, as know as Alfresco Webscripts, built on the basis of Spring Surf.  
Liferay Portal has Liferay IDE based on Eclipse where we can create from scratch different types of Portlets. Also Liferay allows to include external libraries as jQuery, ExtJS, Vaadin, etc. that allows to develop highly customized portlets.
Right now, when several people ask me how to integrate Alfresco into Liferay, after I ask them what does mean when you said _integrate_?. Well I say that implies several thing as:
Integration mean:
1. User and roles, SSO ?
2. Include Alfresco Explorer or Share as a Portlet?
3. Include Alfresco Explorer inside iFrame Portlet?
4. Call any Alfresco's functionality from a Portlet?
5. ...
Well, everything is possible to do, but to create applications from scratch following point 5 was very difficult, but now I think is the quickest way to do it, also the best from an architectural point of view.
This post explain how to do a portlet calling to Alfresco's Webscripts (REST URIs) via ajax using jQuery. I also give some recommendations.

[caption id="" align="alignnone" width="374" caption="Ajax Portlet calls Alfresco Webscripts"][![Ajax Portlet calls Alfresco Webscripts]({{ site.baseurl }}/assets/ajaxalflfry_1arch.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_1arch.png)[/caption]

## Requeriments
1. Liferay IDE version 1.3.1 as IDE
2. Liferay Portal version 6.0.6 installed into IDE
3. Liferay SDK version 6.0.6 installed into IDE
4. Alfresco ECM version 3.4d CE installed
5. Identify and verify Alfresco Webscripts:  
* Login and get Ticket: http://${ALFRESCO}/alfresco/service/api/login?u=${USR}&pw=${PWD}
* Folder browser: http://${ALFRESCO}/alfresco/service/sample/folder${INITIAL_FOLDER}
6. jQuery version 1.6.3 added to new portlet

## Process
1. From Liferay IDE create a new Liferay Project that implement GenericPortlet as follow:

[caption id="" align="alignnone" width="322" caption="Liferay IDE - creating new Liferay Project (1/6)"][![Liferay IDE - creating new Liferay Project \(1/6\)]({{ site.baseurl }}/assets/ajaxalflfry_2newproj.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_2newproj.png)[/caption]

[caption id="" align="alignnone" width="360" caption="Liferay IDE - creating new Liferay Project (2/6)"][![Liferay IDE - creating new Liferay Project \(2/6\)]({{ site.baseurl }}/assets/ajaxalflfry_3newproj.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_3newproj.png)[/caption]

[caption id="" align="alignnone" width="274" caption="Liferay IDE - creating new Liferay Project (3/6)"][![Liferay IDE - creating new Liferay Project \(3/6\) ]({{ site.baseurl }}/assets/ajaxalflfry_4newproj.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_4newproj.png)[/caption]

[caption id="" align="alignnone" width="298" caption="Liferay IDE - creating new Liferay Project (4/6)"][![Liferay IDE - creating new Liferay Project \(4/6\) ]({{ site.baseurl }}/assets/ajaxalflfry_5newproj.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_5newproj.png)[/caption]

[caption id="" align="alignnone" width="368" caption="Liferay IDE - creating new Liferay Project (5/6)"][![Liferay IDE - creating new Liferay Project \(5/6\) ]({{ site.baseurl }}/assets/ajaxalflfry_6newproj.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_6newproj.png)[/caption]

[caption id="" align="alignnone" width="344" caption="Liferay IDE - creating new Liferay Project (6/6)"][![Liferay IDE - creating new Liferay Project \(6/6\) ]({{ site.baseurl }}/assets/ajaxalflfry_7newproj.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_7newproj.png)[/caption]
1. The structure of Project in Liferay IDE will be as follow:

[caption id="" align="alignnone" width="378" caption="Liferay IDE - folder structure of new project"][![Liferay IDE - folder structure of new project]({{ site.baseurl }}/assets/ajaxalflfry_8structureproj.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_8structureproj.png)[/caption]
1. Add code in view.jsp to call serverResource method and to do ajax call to Alfresco. Also, in view.jsp you will add JavaScript code (jQuery) for parsing HTML/XML ajax responses.

[sourcecode language="html" gutter="true" wraplines="false"]  
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects />
This is the <b>Ajax Alfresco Folder Browser</b> portlet in View mode.  
<hr/>
<%  
// "http://192.168.56.101:8080";  
String strUrlAlfIP = renderRequest.getAttribute("alfServer").toString();  
// "/alfresco/service/api/login?u=admin&pw=admin";  
String strUrlAlfLogin = renderRequest.getAttribute("alfTicketSvc").toString() + "?" \+ renderRequest.getAttribute("alfTicketSvcParams");  
// "/alfresco/service/sample/folder/Company%20Home";  
String strUrlAlfDir = renderRequest.getAttribute("alfWebscriptBrowserURL").toString();  
%>  
<portlet:resourceURL var="resourceUrlAlfIP" id="<%=strUrlAlfIP%>" escapeXml="false" />  
<portlet:resourceURL var="resource1AlfLogin" id="<%=strUrlAlfIP.concat(strUrlAlfLogin.trim()).trim()%>" escapeXml="false" />  
<portlet:resourceURL var="resource2AlfFolderBrowser" id="<%=strUrlAlfIP.concat(strUrlAlfDir.trim()).trim()%>" escapeXml="false" />
<script type="text/javascript">  
jQuery(document).ready(function() {
var urlAlfIP = "<%=strUrlAlfIP%>";  
var currentAlfTicket = jQuery("#<portlet:namespace/>alfrescoticket").text();  
$("#<portlet:namespace/>buttonAlfLoginAndTicket").click(function(){  
if (currentAlfTicket == "") {  
jQuery("#<portlet:namespace/>loading").html("<img src='<%=request.getContextPath()%>/images/2-1.gif' border='0'> loading ...");  
jQuery("#<portlet:namespace/>errormsg").html("");  
jQuery.get( '<%=renderResponse.encodeURL(resource1AlfLogin)%>',  
function (data1, textStatus1, jqXHR1) {  
jQuery("#<portlet:namespace/>alfrescoticket").html(data1.getElementsByTagName("ticket")[0].childNodes[0].nodeValue);  
jQuery.get( '<%=renderResponse.encodeURL(resource2AlfFolderBrowser)%>',  
'alf_ticket=' + data1.getElementsByTagName("ticket")[0].childNodes[0].nodeValue,  
function (data2, textStatus2, jqXHR2) {  
jQuery("#<portlet:namespace/>loading").html("");  
jQuery("#<portlet:namespace/>alfrescowscontent").html("<b><font color='blue'>Folder:</font></b> " \+ $(data2).filter("title").text() + "<br><table>");  
var i=1;  
$(data2).find("a").each(  
function() {  
$("#<portlet:namespace/>alfrescowscontent").append( "<tr><td>" \+ i++ + "&nbsp;&gt;&nbsp;</td><td><a href='" \+ $(this).attr("href") + "' id='IdLinkAlfPath'>" \+ $(this).text() + "</a></td></tr>");  
}  
);  
jQuery("#<portlet:namespace/>alfrescowscontent").append("</table><hr/>");  
jQuery("#<portlet:namespace/>errormsg").append("<font color='green'>* textStatus2: " \+ textStatus2 + "</font><br/>");  
jQuery("#<portlet:namespace/>errormsg").append("<font color='green'>* jqXHR2: " \+ jqXHR2.status + "</font><br/>");  
jQuery("#<portlet:namespace/>loading").html("");  
},  
'html'  
).error(function() { //alert("2nd ajax error");  
}); // 2nd end-jquery-get  
jQuery("#<portlet:namespace/>errormsg").append("<font color='green'>* textStatus1: " \+ textStatus1 + "</font><br/>");  
jQuery("#<portlet:namespace/>errormsg").append("<font color='green'>* jqXHR1: " \+ jqXHR1.status + "</font><br/>");  
} // end-function-data  
).error(function() { //alert("1st ajax error");  
}); // 1st end-jquery-get  
} // end-if  
}); // end-click-button
$('a#IdLinkAlfPath').live('click', function(event) {  
jQuery("#<portlet:namespace/>loading").html("<img src='<%=request.getContextPath()%>/images/2-1.gif' border='0'> loading ...");  
var urlAlfGeneric = "" \+ "<%=renderResponse.encodeURL(resourceUrlAlfIP)%>";  
urlAlfGeneric = urlAlfGeneric.replace(encodeURIComponent(urlAlfIP), encodeURIComponent(urlAlfIP + $(this).attr("href")));
jQuery.get( urlAlfGeneric,  
'alf_ticket=' + jQuery("#<portlet:namespace/>alfrescoticket").text(),  
function (data3, textStatus3, jqXHR3) {  
jQuery("#<portlet:namespace/>loading").html("");  
jQuery("#<portlet:namespace/>alfrescowscontent").html("<b><font color='blue'>Folder:</font></b> " \+ $(data3).filter("title").text() + "<br><table>");  
var i=1;  
$(data3).find("a").each( function() {  
$("#<portlet:namespace/>alfrescowscontent").append( "<tr><td>" \+ i++ + "&nbsp;&gt;&nbsp;</td><td><a href='" \+ $(this).attr("href") + "' id='IdLinkAlfPath'>" \+ $(this).text() + "</a></td></tr>");  
});  
jQuery("<portlet:namespace/>alfrescowscontent").append("</table>");
jQuery("#<portlet:namespace/>errormsg").append("<font color='green'>* textStatus3: " \+ textStatus3 + "</font><br/>");  
jQuery("#<portlet:namespace/>errormsg").append("<font color='green'>* jqXHR3: " \+ jqXHR3.status + "</font><br/>"); 
jQuery("#<portlet:namespace/>loading").html("");  
},  
'html'  
).error(function() { //alert("3rd ajax error");  
}); // 3rd end jQuery.get  
return false; // works, does not propagate  
});  
$('a#IdLinkAlfPath').trigger('click');
// jquery error management  
jQuery("#<portlet:namespace/>errormsg").ajaxError(  
function (event, jqXHR, ajaxSettings, thrownError) {  
jQuery("#<portlet:namespace/>errormsg").html("");  
jQuery("#<portlet:namespace/>errormsg").append("<font color='red'>* Status Code jqXHR: " \+ jqXHR.status + "</font><br/>");  
jQuery("#<portlet:namespace/>errormsg").append("<font color='red'>* Status Text jqXHR: " \+ jqXHR.statusText + "</font><br/>");  
jQuery("#<portlet:namespace/>errormsg").append("<font color='red'>* URL: " \+ ajaxSettings.url + "</font><br/>");  
jQuery("#<portlet:namespace/>errormsg").append("<font color='red'>* thrownError: " \+ jqXHR.statusText + "</font><br/>");
// intix: does not work HTTP_STATUS_CODE in 6.0.6 CE  
// http://issues.liferay.com/browse/LPS-13039  
// for this reason, bellow messages will not be show  
if(jqXHR.status == 0) {  
// a status of 0 indicates a failure to connect to alfresco  
jQuery("#<portlet:namespace/>errormsg").append("<font color='red'>* Message: Unable to reach the Alfresco server, check your network connection</font>");  
}else if(jqXHR.status == 403) {  
// a 403 indicates that the login via the alfresco ticket service has failed.  
// display the "access denied" div  
jQuery("#<portlet:namespace/>errormsg").append("<font color='red'>* Message: An authentication error has occurred loading content from Alfresco, check login params</font>");  
}else if(jqXHR.status == 500) {  
// we shouldn't see many 500 errors from Alfrsco services if they  
// have been properly configured.  
jQuery("#<portlet:namespace/>errormsg").append("<font color='red'>* Message: A server error has occurred loading content from Alfresco</font>");  
}else {  
// report timeouts to the user  
jQuery("#<portlet:namespace/>errormsg").append("<font color='red'>* Message: Request to Alfresco server has timed out</font>");  
}  
jQuery("#<portlet:namespace/>loading").html("");  
}  
); //end-ajax-error 
// toggles the slickbox on clicking the noted link   
$('#alf-error-slick-toggle').click(function() {  
$('#<portlet:namespace/>errormsg').toggle(400);  
return false;  
});
// toggles the slickbox on clicking the noted link   
$('#alf-content-slick-toggle').click(function() {  
$('#<portlet:namespace/>alfrescowscontent').toggle(400);  
return false;  
});  
});  
</script>
<input type="button" id="<portlet:namespace/>buttonAlfLoginAndTicket" value="Login Alfresco and get Ticket">  
<hr>
<!-- div to contain ticket retrieved from Alfresco Login web script -->  
<div id="<portlet:namespace/>alfrescoticket"></div>
<!-- Div to hold loading image -->  
<div id="<portlet:namespace/>loading"><img src="<%=request.getContextPath()%>/images/2-1.gif" border="0"> ...click on above button to start or change params in portlet menu preferences!</div>
<br/>  
<!-- Div to hold error messages -->  
<a href="#" id="alf-error-slick-toggle">[+] Toggle error console</a>  
<div id="<portlet:namespace/>errormsg" class="div_bg_white" > :) </div>
<!-- Div to hold logs -->  
<div id="<portlet:namespace/>logs"></div>
<br/>  
<!-- Div to hold alfresco content -->  
<a href="#" id="alf-content-slick-toggle">[+] Toggle Alfresco content</a>  
<div class="div_bg_white" id="<portlet:namespace/>alfrescowscontent">:)</div>  

[/sourcecode]
1. AjaxAlfrescoFolderBrowser.java extends GenericPortlet, in the serverResource method manages ajax calls and returns ResourceResponse to be parsed in view.jsp

[sourcecode language="java" gutter="true" wraplines="false"]  
package info.intix.lfry.samples;
import com.liferay.portal.kernel.log.Log;  
import com.liferay.portal.kernel.log.LogFactoryUtil;
import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.net.HttpURLConnection;  
import java.net.URL;  
import java.util.Iterator;  
import java.util.List;  
import java.util.Map;  
import java.util.Map.Entry;
import javax.portlet.ActionRequest;  
import javax.portlet.ActionResponse;  
import javax.portlet.GenericPortlet;  
import javax.portlet.PortletException;  
import javax.portlet.PortletMode;  
import javax.portlet.PortletPreferences;  
import javax.portlet.PortletRequestDispatcher;  
import javax.portlet.RenderRequest;  
import javax.portlet.RenderResponse;  
import javax.portlet.ResourceRequest;  
import javax.portlet.ResourceResponse;
/**  
* Portlet implementation class AjaxAlfrescoFolderBrowser  
*/  
public class AjaxAlfrescoFolderBrowser extends GenericPortlet {
public void init() {  
editJSP = getInitParameter("edit-jsp");  
helpJSP = getInitParameter("help-jsp");  
viewJSP = getInitParameter("view-jsp");  
}
/**  
* intix: Changes are persisted when the store method is called.  
* The store method can only be invoked within the scope of a processAction call.  
* Changes that are not persisted are discarded when the processAction or render method ends.  
*/  
public void processAction(  
ActionRequest actionRequest, ActionResponse actionResponse)  
throws IOException, PortletException {
//super.processAction(actionRequest, actionResponse);  
PortletPreferences prefs = actionRequest.getPreferences();  
prefs.setValue("ticketUrl", actionRequest.getParameter("ticketUrl"));  
prefs.setValue("alfServer", actionRequest.getParameter("alfServer"));  
prefs.setValue("alfTicketSvc", actionRequest.getParameter("alfTicketSvc"));  
prefs.setValue("alfTicketSvcParams", actionRequest.getParameter("alfTicketSvcParams"));  
prefs.setValue("alfWebscriptBrowserURL", actionRequest.getParameter("alfWebscriptBrowserURL"));  
prefs.setValue("alfWebscriptBrowserURLParams", actionRequest.getParameter("alfWebscriptBrowserURLParams"));  
prefs.setValue("jQuery", actionRequest.getParameter("jQuery"));  
prefs.store();  
actionResponse.setPortletMode(PortletMode.EDIT);  
}
/**  
* intix:  
*/  
public void doEdit(  
RenderRequest renderRequest, RenderResponse renderResponse)  
throws IOException, PortletException {  
if (renderRequest.getPreferences() == null) {  
//super.doEdit(renderRequest, renderResponse);  
} else {  
// get editable preferences  
PortletPreferences prefs = renderRequest.getPreferences();
// intix: these values will override options in portlet.xml  
renderRequest.setAttribute("alfServer", (prefs.getValue("alfServer", "")));  
renderRequest.setAttribute("alfTicketSvc", (prefs.getValue("alfTicketSvc", "")));  
renderRequest.setAttribute("alfTicketSvcParams", (prefs.getValue("alfTicketSvcParams", "")));  
renderRequest.setAttribute("alfWebscriptBrowserURL", (prefs.getValue("alfWebscriptBrowserURL", "")));  
renderRequest.setAttribute("alfWebscriptBrowserURLParams", (prefs.getValue("alfWebscriptBrowserURLParams", "")));  
renderRequest.setAttribute("jQuery", (prefs.getValue("jQuery", "")));  
include(editJSP, renderRequest, renderResponse);  
}  
}
public void doHelp(  
RenderRequest renderRequest, RenderResponse renderResponse)  
throws IOException, PortletException {
include(helpJSP, renderRequest, renderResponse);  
}
/**  
* intix:  
*/  
public void doView(  
RenderRequest renderRequest, RenderResponse renderResponse)  
throws IOException, PortletException {
try {  
// get portlet prefs  
PortletPreferences prefs = renderRequest.getPreferences(); 
String alfServer = prefs.getValue("alfServer", "");  
String alfTicketSvc = prefs.getValue("alfTicketSvc", "");  
String alfTicketSvcParams = prefs.getValue("alfTicketSvcParams", "");  
String alfWebscriptBrowserURL= prefs.getValue("alfWebscriptBrowserURL", "");  
String alfWebscriptBrowserURLParams = prefs.getValue("alfWebscriptBrowserURLParams", "");  
String jQuery = prefs.getValue("jQuery", "");  
String ticketUrl = alfServer + alfTicketSvc + "?" \+ alfTicketSvcParams;
renderRequest.setAttribute("ticketUrl", ticketUrl);  
renderRequest.setAttribute("alfServer", alfServer);  
renderRequest.setAttribute("alfTicketSvc", alfTicketSvc);  
renderRequest.setAttribute("alfTicketSvcParams", alfTicketSvcParams);  
renderRequest.setAttribute("alfWebscriptBrowserURL", alfWebscriptBrowserURL);  
renderRequest.setAttribute("alfWebscriptBrowserURLParams", alfWebscriptBrowserURLParams);  
renderRequest.setAttribute("jQuery", jQuery);  
}catch(Exception ex) {  
_log.error(ex);  
}  
include(viewJSP, renderRequest, renderResponse);  
}
protected void include(  
String path, RenderRequest renderRequest,  
RenderResponse renderResponse)  
throws IOException, PortletException {
PortletRequestDispatcher portletRequestDispatcher =  
getPortletContext().getRequestDispatcher(path);
if (portletRequestDispatcher == null) {  
_log.error(path + " is not a valid include");  
}  
else {  
portletRequestDispatcher.include(renderRequest, renderResponse);  
}  
}
/**  
* intix: serveResource does HTTP and Ajax call behind of Liferay  
*/  
public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {  
response.setContentType("text/xml");  
String strAlfTicket= request.getParameter("alf_ticket");  
String strQueryString = "";  
if (strAlfTicket != null) {  
// intix: if alf_ticket exists, then user was authenticate with alfresco  
Map<String, String[]> mapParameters = request.getParameterMap();  
for (Entry<String, String[]> entryParameter : mapParameters.entrySet()) {  
System.out.println(">> Key = " \+ entryParameter.getKey() + ", Value = " \+ entryParameter.getValue()[0]);  
strQueryString = strQueryString + entryParameter.getKey() + "=" \+ entryParameter.getValue()[0] + "&";  
}  
} else {  
// intix: ticket is null  
String strUser = request.getParameter("u");  
String strPw = request.getParameter("pw");  
strQueryString = "u=" \+ strUser + "&pw=" \+ strPw;  
}  
String requestUrl = request.getResourceID();  
BufferedInputStream web2ProxyBuffer = null;  
BufferedOutputStream proxy2ClientBuffer = null;  
HttpURLConnection con;  
URL url = null;  
try {  
int oneByte = 0;  
String methodName;  
if (strAlfTicket != null) {  
url = new URL(requestUrl + "?" \+ strQueryString);  
} else {  
url = new URL(requestUrl);  
}  
con = (HttpURLConnection) url.openConnection();  
methodName = request.getMethod();  
System.out.println(">> methodName: " \+ methodName);
con.setRequestMethod(methodName);  
con.setDoOutput(true);  
con.setDoInput(true);  
con.setFollowRedirects(false);  
con.setUseCaches(true);  
con.connect();
// does not work in 6.0.6 CE  
// http://issues.liferay.com/browse/LPS-13039  
int httpRespCode = con.getResponseCode();  
response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(httpRespCode));  
System.out.println(">> HTTP_STATUS_CODE: " \+ httpRespCode);
if(methodName.equals("POST")) {  
BufferedInputStream clientToProxyBuf = new BufferedInputStream(request.getPortletInputStream());  
BufferedOutputStream proxyToWebBuf = new BufferedOutputStream(con.getOutputStream());  
while ((oneByte = clientToProxyBuf.read()) != -1) {  
proxyToWebBuf.write(oneByte);  
}  
proxyToWebBuf.flush();  
proxyToWebBuf.close();  
clientToProxyBuf.close();  
}
for( Iterator i = con.getHeaderFields().entrySet().iterator() ; i.hasNext() ;) {  
Map.Entry mapEntry = (Map.Entry)i.next();  
if(mapEntry.getKey()!=null) {  
//response.setHeader(mapEntry.getKey().toString(), ((List)mapEntry.getValue()).get(0).toString());  
System.out.println(">> HEADER > " \+ mapEntry.getKey().toString() + "\t" \+ ((List)mapEntry.getValue()).get(0).toString());  
}  
}
InputStream in = con.getInputStream();  
web2ProxyBuffer = new BufferedInputStream(in);  
proxy2ClientBuffer = new BufferedOutputStream(response.getPortletOutputStream());
byte [] byteArray = new byte[1024]; // intix: any array size is valid  
int intByteRead = web2ProxyBuffer.read(byteArray);  
while (intByteRead > 0) {  
// intix: print response-html/xml, must be the first line after while loop  
System.out.println(new String(byteArray, 0, intByteRead));  
proxy2ClientBuffer.write(byteArray, 0, intByteRead);  
intByteRead = web2ProxyBuffer.read(byteArray);  
}  
proxy2ClientBuffer.flush();  
proxy2ClientBuffer.close();  
web2ProxyBuffer.close();  
con.disconnect();  
} catch(Exception e) {  
e.getMessage();  
} finally {  
//  
}  
} 
protected String editJSP;  
protected String helpJSP;  
protected String viewJSP;
private static Log _log = LogFactoryUtil.getLog(AjaxAlfrescoFolderBrowser.class);
}  

[/sourcecode]
1. When you have successfully deployed the portlet, open a browser, login, then add the new portlet to any page. Then you see the following:

[caption id="" align="alignnone" width="303" caption="Ajax Portlet calling Alfresco Webscripts"][![Ajax Portlet calling Alfresco Webscripts]({{ site.baseurl }}/assets/ajaxalflfry_9ajaxportlet.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_9ajaxportlet.png)[/caption]

[caption id="" align="alignnone" width="411" caption="Ajax Portlet calling Alfresco Webscript - view mode"][![Ajax Portlet calling Alfresco Webscript - view mode]({{ site.baseurl }}/assets/ajaxalflfry_10ajaxportlet.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_10ajaxportlet.png)[/caption]

[caption id="" align="alignnone" width="401" caption="Ajax Portlet calling Alfresco Webscripts - edit mode"][![Ajax Portlet calling Alfresco Webscripts - edit mode]({{ site.baseurl }}/assets/ajaxalflfry_11ajaxportlet.png)](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/ajaxalflfry_11ajaxportlet.png)[/caption]

## Conclussions
1. In the JSR-286 specifications (Portlet 2.0) now is possible to use serveResource() method andto request data. I use it as a servlet-proxy to do ajax calls to Alfresco.
2. Exists a issue in Liferay 6.0.6 when setting ResourceResponse.HTTP_STATUS_CODE in the Portlet response (<http://issues.liferay.com/browse/LPS-13039>), this implies I have to manage HTTP_STATUS_CODE by parsing the Ajax HTML/XML responses.
3. I have Liferay and Alfresco in different VMs (different IP and Ports) and I never had cross-domain issues thanks to Point #1 (serveResource nad portlet:resourceURL), but if you run into it is recommended that you use Apache HTTP server as a reverse-proxy.
You can download entire project (source code) and compiled from here:
1. Source code (Liferay IDE project): [AjaxAlfrescoFolderBrowser-portlet.zip](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/AjaxAlfrescoFolderBrowser-portlet.zip)
2. Compiled: [AjaxAlfrescoFolderBrowser-portlet.war](http://dl.dropbox.com/u/2961879/blog20110920_ajaxalfrescoliferayportlet/AjaxAlfrescoFolderBrowser-portlet.war)
End.
