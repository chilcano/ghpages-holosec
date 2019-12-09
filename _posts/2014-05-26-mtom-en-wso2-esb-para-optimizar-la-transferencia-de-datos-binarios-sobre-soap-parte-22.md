---
layout:     post
title:      'MTOM en WSO2 ESB para optimizar la transferencia de datos binarios sobre SOAP (Parte 2/2)'
date:       2014-05-26 20:02:00
categories: ['SOA']
tags:       ['Apache Axiom', 'Axis2', 'MTOM', 'SOAP', 'soapUI', 'SwA', 'WSO2 ESB']
status:     publish 
permalink:  "/2014/05/26/mtom-en-wso2-esb-para-optimizar-la-transferencia-de-datos-binarios-sobre-soap-parte-22/"
---
[En la anterior entrada](http://holisticsecurity.wordpress.com/2014/05/14/mtom-en-wso2-esb-para-optimizar-la-transferencia-de-datos-binarios-sobre-soap-parte1/ "MTOM en WSO2 ESB para optimizar la transferencia de datos binarios sobre SOAP \(Parte 1/2\)") explorábamos los beneficios del uso de MTOM en WSO2 ESB, ahora explicaremos cómo integrar esta funcionalidad en nuestras aplicaciones web, es decir, cómo invocar el mismo servicio desde una página web tradicional.

[  

![wso2-esb]({{ site.baseurl }}/assets/wso2-esb.jpg?w=300)  
](http://holisticsecurity.files.wordpress.com/2014/05/wso2-esb.jpg)

## I. Escenario.
* * *
Una vez implementado MTOM sobre SOAP empleando WSO2, subir ficheros es sumamente fácil desde SoapUI, pero lo sería también desde un formulario HTML?. Pues, en principio no lo es debido a que ambos implementan maneras diferentes de hacerlo, a pesar de que ambos envían información sobre el mismo protocolo HTTP.
Un formulario HTML envía ficheros sobre HTTP codificándolo en "multipart/form-data", mientras que en el servidor se decodifica. Hay que decirlo, "multipart/form-data" es una especificación nada reciente, es la forma de envio de ficheros más usada equiparable al FTP.
MTOM sobre SOAP, es una especificación relativamente joven, cubre la necesidad de envio de fichero sobre el protocolo SOAP, pero el valor añadido es que el envio es optimizado, esto requiere implementar un cliente SOAP que use MTOM. SoapUI es una buena herramienta de prueba para estos fines, pero dentro de tu proyecto tarde o temprano tendrás que implementar un cliente SOAP con MTOM.
Entonces, sacando ventaja de los mecanismos de mediación que WSO2 ESB ofrece, podríamos crear un Proxy_01 que exponga un servicio de transferencia de fichero usando MTOM y SOAP, luego podríamos crear otro Proxy_02 que nos facilite enviar ficheros desde un formulario HTML. En el fondo el Proxy_02 haría la traducción de "multipart/form-data" a "MTOM".
Si tenemos un cliente MTOM/SOAP que nos permita subir ficheros, lo enviaremos a Proxy_01, pero si tenemos un formulario HTML para subir ficheros, éste apuntará a Proxy_02, que hará la traducción y que luego lo enviará a Proxy_01.

[caption id="attachment_1220" align="aligncenter" width="800"][![HTTP Multipart/form-data a MTOM SOAP en WSO2 ESB]({{ site.baseurl }}/assets/wso2esb-mtom-part2-http2soap-01.png?w=800)](http://holisticsecurity.files.wordpress.com/2014/05/wso2esb-mtom-part2-http2soap-01.png) HTTP Multipart/form-data a MTOM SOAP en WSO2 ESB[/caption]

## II. Implementando el escenario.
* * *
Para implementar este escenario necesitamos lo siguiente:
1. WSO2 ESB 4.8.1 (<http://wso2.com/products/enterprise-service-bus>)
2. Proxy_01, similar al "Sample 51" ([MTOM and SwA Optimizations and Request/Response Correlation](https://docs.wso2.org/pages/viewpage.action?pageId=33136025)) desplegado en WSO2 ESB.
3. Proxy_02 que reciba la petición de envio de fichero codificada en "multipart/form-data" realizada desde un formulario HTML.
4. El formulario HTML para enviar el fichero.
5. Modificar la implementación del servicio MTOM SOAP en el lado del Axis2 Server.

### II.1. Proxy_01 para el envio de ficheros usando MTOM y SOAP.
* * *
En la anterior entrada implementamos un proxy para el envio de ficheros usando MTOM y SOAP. Entonces, vamos a reutilizar el mismo proxy y añadiremos unos pequeños cambios.Este proxy es un proxy actualizado a partir del proxy publicado en la entrada anterior "[MTOM en WSO2 ESB para optimizar la transferencia de datos binarios sobre SOAP (Parte 1/2)](http://holisticsecurity.wordpress.com/2014/05/14/mtom-en-wso2-esb-para-optimizar-la-transferencia-de-datos-binarios-sobre-soap-parte1/ "MTOM en WSO2 ESB para optimizar la transferencia de datos binarios sobre SOAP \(Parte 1/2\)")". Sabemos que en el lado servidor tenemos que desplegar la implementación de este servicio para que todo funcione. Más adelante explicaré que es necesario tocar algunas líneas de código.

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<proxy xmlns="http://ws.apache.org/ns/synapse"  
name="MTOMSwASampleService_proxy"  
transports="https,http"  
statistics="disable"  
trace="disable"  
startOnLoad="true">  
<target>  
<inSequence>  
<log level="custom">  
<property name="======== Proxy-MTOMSwASampleService" value="====== inSeq - ini"/>  
</log>  
<filter source="get-property('Action')" regex="urn:oneWayUploadUsingHTTP">  
<then>  
<log level="custom">  
<property name="...calling " value="oneWayUploadUsingHTTP"/>  
</log>  
<property name="OUT_ONLY" value="true"/>  
<property name="FORCE_SC_ACCEPTED" value="true" scope="axis2"/>  
<property name="example" value="onewayhttp"/>  
<send>  
<endpoint>  
<address uri="http://localhost:9000/services/MTOMSwASampleService"/>  
</endpoint>  
</send>  
</then>  
<else/>  
</filter>  
<filter source="get-property('Action')" regex="urn:uploadFileUsingMTOM">  
<then>  
<log level="custom">  
<property name="...calling " value="uploadFileUsingMTOM"/>  
</log>  
<header name="Action" scope="default" action="remove"/>  
<property name="SOAPAction" scope="default" action="remove"/>  
<property name="example" value="mtom"/>  
<property name="Action" value="urn:uploadFileUsingMTOM" scope="default"/>  
<header name="Action" scope="default" value="urn:uploadFileUsingMTOM"/>  
<send>  
<endpoint>  
<address uri="http://localhost:9000/services/MTOMSwASampleService"  
optimize="mtom"/>  
</endpoint>  
</send>  
</then>  
<else/>  
</filter>  
<filter source="get-property('Action')" regex="urn:uploadFileUsingSwA">  
<then>  
<log level="custom">  
<property name="...calling " value="uploadFileUsingSwA"/>  
</log>  
<property name="example" value="swa"/>  
<send>  
<endpoint>  
<address uri="http://localhost:9000/services/MTOMSwASampleService"  
optimize="swa"/>  
</endpoint>  
</send>  
</then>  
<else/>  
</filter>  
<filter source="get-property('Action')" regex="urn:oneWayUploadUsingMTOM">  
<then>  
<log level="custom">  
<property name="...calling " value="oneWayUploadUsingMTOM"/>  
</log>  
<property name="OUT_ONLY" value="true"/>  
<property name="FORCE_SC_ACCEPTED" value="true" scope="axis2"/>  
<property name="example" value="onewaymtom"/>  
<send>  
<endpoint>  
<address uri="http://localhost:9000/services/MTOMSwASampleService"  
optimize="mtom"/>  
</endpoint>  
</send>  
</then>  
<else/>  
</filter>  
<log level="custom">  
<property name="======== Proxy MTOMSwASampleService" value="====== inSeq - fin"/>  
</log>  
</inSequence>  
<outSequence>  
<log level="custom">  
<property name="======== Proxy MTOMSwASampleService" value="====== outSeq - ini"/>  
</log>  
<filter source="get-property('example')" regex="onewayhttp">  
<property name="enableMTOM" value="true" scope="axis2"/>  
</filter>  
<filter source="get-property('example')" regex="onewaymtom">  
<property name="enableMTOM" value="true" scope="axis2"/>  
</filter>  
<filter source="get-property('example')" regex="mtom">  
<property name="enableMTOM" value="true" scope="axis2"/>  
</filter>  
<filter source="get-property('example')" regex="swa">  
<property name="enableSwA" value="true" scope="axis2"/>  
</filter>  
<send/>  
<log level="custom">  
<property name="======== Proxy MTOMSwASampleService" value="====== outSeq - fin"/>  
</log>  
</outSequence>  
</target>  
<publishWSDL uri="http://localhost:9000/services/MTOMSwASampleService?wsdl"/>  
<parameter name="enableMTOM">true</parameter>  
<description>MTOMSwASampleService (Sample 51) updated</description>  
</proxy>  

[/sourcecode]
La URL `http://localhost:9000` representa al Axis2 server, donde desplegamos el servicio. Es Axis2 server que auto-genera el `WSDL` asociado al backend service.  
Es importante iniciar primero el Axis2 server, luego iniciar WSO2 ESB ya que desde el ESB hacemos referencia al `WSDL` alojado en Axis2 server. Esto es lo que en WSO2 ESB se llamada "WSDL inline".

### II.2. Proxy_02 que reciba el fichero en "multipart/form-data" y lo envie a Proxy_01.
* * *
Aquí básicamente, el Proxy_02 transformará la cabecera y el cuerpo del mensaje para adaptarlo al formato que el Proxy_01 lo necesita.  
Evidentemente, deberás conocer cómo "multipart/form-data" y MTOM codifican para implementar este proxy.

[sourcecode language="xml" gutter="true" wraplines="false"]  
<?xml version="1.0" encoding="UTF-8"?>  
<proxy xmlns="http://ws.apache.org/ns/synapse"  
name="MTOMSwASampleService_http"  
transports="https,http"  
statistics="disable"  
trace="disable"  
startOnLoad="true">  
<target>  
<inSequence>  
<log level="custom">  
<property name="****** MTOMSwASampleService - HTTP" value="***** inSeq - ini"/>  
</log>  
<property name="DISABLE_CHUNKING" value="true"/>  
<property name="Action" value="urn:oneWayUploadUsingHTTP" scope="default"/>  
<header name="Action" scope="default" value="urn:oneWayUploadUsingHTTP"/>  
<send>  
<endpoint>  
<address uri="http://localhost:8281/services/MTOMSwASampleService_proxy"  
format="soap11"/>  
</endpoint>  
</send>  
<log level="custom">  
<property name="****** MTOMSwASampleService - HTTP" value="***** inSeq - fin"/>  
</log>  
</inSequence>  
<outSequence>  
<log level="custom">  
<property name="******* MTOMSwASampleService - HTTP" value="***** outSeq - ini"/>  
</log>  
<property name="OUT_ONLY" value="true"/>  
<property name="FORCE_SC_ACCEPTED" value="true"/>  
<send/>  
<log level="custom">  
<property name="******* MTOMSwASampleService - HTTP" value="***** outSeq - fin"/>  
</log>  
</outSequence>  
</target>  
<description>Proxy Upload File using Multipart Form Data</description>  
</proxy>  

[/sourcecode]

### II.3. Formulario HTML para el envio de ficheros.
* * *
Implementar un formulario de este tipo es muy fácil, sólo hay que usar `enctype` con el siguiente valor `"multipart/form-data"`. Tal como se muestra a continuación:

[sourcecode language="html" gutter="true" wraplines="false"]  
<html>  
<head>  
<title>Sending Binary Data by SOAP with MTOM using WSO2 ESB</title>  
</head>  
<body>  
<h2>Sending Binary Data by SOAP with MTOM using WSO2 ESB</h2>
<form action="http://localhost:8283/services/MTOMSwASampleService_http" method="POST" enctype="multipart/form-data">  
Binary file: <input type="file" name="myimagefile" size="50" multiple>  
<br/>  
<input type="submit" value="Submit">  
</form>
</body>  
</html>  

[/sourcecode]
Las 3 partes a prestar atención aquí son:
1. El `Action` del form apunta al `Proxy_02`, asegurarse que el puerto sea el correcto y corresponda al del WSO2 ESB.
2. El `enctype` debe indicar la codificación con el que se enviará el fichero.
3. El `input` del form es de tipo `file` y debe tener un nombre, como en este caso `myimagefile`, con el que podamos luego leerlo para hacer la transformación.
Finalmente, recordar que no será necesario un Servidor Web para invocar este formulario HTML, basta con abrir el formulario desde cualquier navegador y elegir el fichero que queremos enviar desde nuestro disco duro.

### II.4. Modificar el servicio de backend que implementa el envio y recepción de ficheros.
* * *
Como indiqué líneas arriba, será necesario actualizar la implementación del servicio de backend del "Sample 51" para nuestro escenario, ya que en principio sólo está preparado para trabajar con MTOM y SwA sobre SOAP.
Lo que haremos es básicamente añadir una nueva operación SOAP que recoja el mensaje y sepa decodificarlo para guardarlo en el lado servidor.

[sourcecode language="java" gutter="true" wraplines="false"]  
package samples.services;
import org.apache.axiom.om.OMText;  
import org.apache.axiom.om.OMElement;  
import org.apache.axiom.om.OMFactory;  
import org.apache.axiom.om.OMNamespace;  
import org.apache.axiom.util.base64.Base64Utils;  
import org.apache.axiom.attachments.Attachments;  
import org.apache.axis2.context.MessageContext;  
import org.apache.axis2.wsdl.WSDLConstants;
import javax.activation.DataHandler;  
import javax.activation.FileDataSource;  
import javax.xml.namespace.QName;  
import java.io.*;
public class MTOMSwASampleService {
private static final int BUFFER = 2048;
public OMElement uploadFileUsingMTOM(OMElement request) throws Exception {
/*  
<soapenv:Envelope  
xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"  
xmlns:ser="http://services.samples">  
<soapenv:Header/>  
<soapenv:Body>  
<ser:uploadFileUsingMTOM>  
<ser:request>  
<ser:image>cid:tux-grenade.png</ser:image>  
</ser:request>  
</ser:uploadFileUsingMTOM>  
</soapenv:Body>  
</soapenv:Envelope>  
*/
OMText binaryNode = (OMText) request.  
getFirstChildWithName(new QName("http://services.samples", "request")).  
getFirstChildWithName(new QName("http://services.samples", "image")).  
getFirstOMChild();  
DataHandler dataHandler = (DataHandler) binaryNode.getDataHandler();  
InputStream is = dataHandler.getInputStream();
File tempFile = File.createTempFile("mtom-", ".gif");  
FileOutputStream fos = new FileOutputStream(tempFile);  
BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
byte data[] = new byte[BUFFER];  
int count;  
while ((count = is.read(data, 0, BUFFER)) != -1) {  
dest.write(data, 0, count);  
}
dest.flush();  
dest.close();  
System.out.println("Wrote MTOM content to temp file : " \+ tempFile.getAbsolutePath());
OMFactory factory = request.getOMFactory();  
OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");  
OMElement payload = factory.createOMElement("uploadFileUsingMTOMResponse", ns);  
OMElement response = factory.createOMElement("response", ns);  
OMElement image = factory.createOMElement("image", ns);
FileDataSource fileDataSource = new FileDataSource(tempFile);  
dataHandler = new DataHandler(fileDataSource);  
OMText textData = factory.createOMText(dataHandler, true);  
image.addChild(textData);  
response.addChild(image);  
payload.addChild(response);
MessageContext outMsgCtx = MessageContext.getCurrentMessageContext().getOperationContext().getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);  
outMsgCtx.setProperty(  
org.apache.axis2.Constants.Configuration.ENABLE_MTOM,  
org.apache.axis2.Constants.VALUE_TRUE);
return payload;  
}
public OMElement uploadFileUsingSwA(OMElement request) throws Exception {
String imageContentId = request.  
getFirstChildWithName(new QName("http://services.samples", "request")).  
getFirstChildWithName(new QName("http://services.samples", "imageId")).  
getText();
MessageContext msgCtx = MessageContext.getCurrentMessageContext();  
Attachments attachment = msgCtx.getAttachmentMap();  
DataHandler dataHandler = attachment.getDataHandler(imageContentId);  
File tempFile = File.createTempFile("swa-", ".gif");  
FileOutputStream fos = new FileOutputStream(tempFile);  
dataHandler.writeTo(fos);  
fos.flush();  
fos.close();  
System.out.println("Wrote SwA attachment to temp file : " \+ tempFile.getAbsolutePath());
MessageContext outMsgCtx = msgCtx.getOperationContext().getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);  
outMsgCtx.setProperty(  
org.apache.axis2.Constants.Configuration.ENABLE_SWA,  
org.apache.axis2.Constants.VALUE_TRUE);
OMFactory factory = request.getOMFactory();  
OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");  
OMElement payload = factory.createOMElement("uploadFileUsingSwAResponse", ns);  
OMElement response = factory.createOMElement("response", ns);  
OMElement imageId = factory.createOMElement("imageId", ns);
FileDataSource fileDataSource = new FileDataSource(tempFile);  
dataHandler = new DataHandler(fileDataSource);  
imageContentId = outMsgCtx.addAttachment(dataHandler);  
imageId.setText(imageContentId);  
response.addChild(imageId);  
payload.addChild(response);
return payload;  
}
public void oneWayUploadUsingMTOM(OMElement element) throws Exception {
/*  
<soapenv:Envelope  
xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"  
xmlns:ser="http://services.samples">  
<soapenv:Header/>  
<soapenv:Body>  
<ser:oneWayUploadUsingMTOM>cid:chilcano.jpg</ser:oneWayUploadUsingMTOM>  
</soapenv:Body>  
</soapenv:Envelope>  
*/
OMText binaryNode = (OMText) element.getFirstOMChild();  
System.out.println("contentID : " \+ binaryNode.getContentID() );  
System.out.println("isBinary :" \+ binaryNode.isBinary() );  
System.out.println("isOptimized :" \+ binaryNode.isOptimized() );  
DataHandler dataHandler = (DataHandler) binaryNode.getDataHandler();  
InputStream is = dataHandler.getInputStream();
File tempFile = File.createTempFile("mtom-", ".gif");  
FileOutputStream fos = new FileOutputStream(tempFile);  
BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
byte data[] = new byte[BUFFER];  
int count;  
while ((count = is.read(data, 0, BUFFER)) != -1) {  
dest.write(data, 0, count);  
}
dest.flush();  
dest.close();  
System.out.println("Wrote to file : " \+ tempFile.getAbsolutePath());  
}
public void oneWayUploadUsingHTTP(OMElement element) throws Exception {
/*  
<?xml version="1.0" encoding="UTF-8"?>  
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">  
<soapenv:Body>  
<mediate>  
<myimagefile>...content in base64...</myimagefile>  
</mediate>  
</soapenv:Body>  
</soapenv:Envelope>  
*/
String imageContentB64 = element.getFirstElement().getText();  
File tempFile = File.createTempFile("1wayhttp-", ".gif");  
byte[] byteImageContent = Base64Utils.decode(imageContentB64);  
try (OutputStream ostream = new FileOutputStream(tempFile)) {  
ostream.write(byteImageContent);  
}  
System.out.println("Wrote 1WayHTTP attachment to temp file : " \+ tempFile.getAbsolutePath());  
}
}

[/sourcecode]
Veréis que he implementado una nueva operación al servicio (método java) llamada `oneWayUploadUsingHTTP` que recibe como un parámetro de tipo `OMElement`.
Si deseas añadir más código para efectuar acciones más complejas te recomiendo que crees un proyecto nuevo desde WSO2 Studio haciendo uso de Maven, luego importes esta clase.
Una vez actualizado, debemos compilarlo y desplegarlo en Axis2 Server. El `WSDL` será actualizado automáticamente.  
Sólo seguir los siguientes pasos:
1. Editar el fichero `%ESB_HOME%/samples/axis2Server/src/MTOMSwASampleService/src/samples/servicesMTOMSwASampleService.java`, dejarlo como se indica líneas arriba.
2. Grabar `servicesMTOMSwASampleService.java` para preservar los cambios.
3. Desde la línea de comandos, ejecutar ANT para compilar el servicio y desplegarlo en el Axis2 server.
4. Si todo ha ido bien, iniciemos Axis2 server.
5. Para verificar que hemos implementado una nueva operación, podemos consultar el actualizado `WSDL` en la siguiente dirección: <http://localhost:9000/services/MTOMSwASampleService?wsdl>

[sourcecode language="text" gutter="true" wraplines="false"]  
Chilcano@Pisc0 $ cd ~/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService  
Chilcano@Pisc0 $ ant  
Buildfile: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/build.xml
clean:  

[delete] Deleting directory /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/temp
init:  

[mkdir] Created dir: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/temp  

[mkdir] Created dir: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/temp/classes
compile-all:  

[javac] /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/build.xml:47: warning: 'includeantruntime' was not set, defaulting to build.sysclasspath=last; set to false for repeatable builds  

[javac] Compiling 1 source file to /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/temp/classes
build-service:  

[mkdir] Created dir: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/temp/MTOMSwASampleService  

[mkdir] Created dir: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/temp/MTOMSwASampleService/META-INF  

[copy] Copying 1 file to /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/temp/MTOMSwASampleService/META-INF  

[copy] Copying 1 file to /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/src/MTOMSwASampleService/temp/MTOMSwASampleService  

[jar] Building jar: /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/MTOMSwASampleService.aar
BUILD SUCCESSFUL  
Total time: 2 seconds  

[/sourcecode]

## III. Ejecutando todo esto.
* * *
Finalmente, después de desplegar satisfactoriamente el servicio de backend, iniciar Axis2 server, iniciar WSO2 ESB, crear Proxy_01 y Proxy_02, estamos a punto de poder ejecutar el form HTML para hacer el upload de un fichero.

[![wso2esb-mtom-part2-http2soap-02form]({{ site.baseurl }}/assets/wso2esb-mtom-part2-http2soap-02form.png?w=800)](http://holisticsecurity.files.wordpress.com/2014/05/wso2esb-mtom-part2-http2soap-02form.png)
Después del envío de un fichero a través del form HTML, tu verás que en el lado de Axis2 server se recuperará el fichero enviado para guardarlo en el disco del servidor.

[sourcecode language="text" gutter="true" wraplines="false"]  

[...]  
14/05/26 22:43:07 INFO nhttp.HttpCoreNIOSender: HTTPS Sender starting  
14/05/26 22:43:07 INFO nhttp.HttpCoreNIOSender: HTTP Sender starting  
14/05/26 22:43:07 INFO jms.JMSSender: JMS Sender started  
14/05/26 22:43:07 INFO jms.JMSSender: JMS Transport Sender initialized...  
14/05/26 22:43:07 INFO deployment.DeploymentEngine: Deploying Web service: FastStockQuoteService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/FastStockQuoteService.aar  
14/05/26 22:43:07 INFO deployment.DeploymentEngine: Deploying Web service: LBService1.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/LBService1.aar  
14/05/26 22:43:07 INFO deployment.DeploymentEngine: Deploying Web service: LBService2.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/LBService2.aar  
14/05/26 22:43:07 INFO deployment.DeploymentEngine: Deploying Web service: MTOMSwASampleService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/MTOMSwASampleService.aar  
14/05/26 22:43:07 INFO deployment.DeploymentEngine: Deploying Web service: ReliableStockQuoteService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/ReliableStockQuoteService.aar  
14/05/26 22:43:07 INFO deployment.DeploymentEngine: Deploying Web service: SecureStockQuoteService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/SecureStockQuoteService.aar  
14/05/26 22:43:07 INFO deployment.DeploymentEngine: Deploying Web service: SimpleStockQuoteService.aar - file:/Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/repository/services/SimpleStockQuoteService.aar  
14/05/26 22:43:08 INFO nhttp.HttpCoreNIOListener: HTTPS Listener started on 0:0:0:0:0:0:0:0:9002  
14/05/26 22:43:08 INFO nhttp.HttpCoreNIOListener: HTTP Listener started on 0:0:0:0:0:0:0:0:9000  
14/05/26 22:43:08 INFO util.SampleAxis2ServerManager: [SimpleAxisServer] Started
Wrote 1WayHTTP attachment to temp file : /Users/Chilcano/0dev-env/2srv/wso2esb-4.8.1/samples/axis2Server/../../tmp/sampleServer/1wayhttp-1767851362396795349.gif  

[/sourcecode]
En el lado de WSO2 ESB verás las siguientes trazas:
sourcecode language="text" gutter="true" wraplines="false"]  

[...]  

[2014-05-25 11:48:59,110] INFO - PassThroughHttpSSLListener Pass-through HTTPS Listener started on 0:0:0:0:0:0:0:0:8246  

[2014-05-25 11:48:59,110] INFO - PassThroughHttpListener Starting Pass-through HTTP Listener...  

[2014-05-25 11:48:59,112] INFO - PassThroughHttpListener Pass-through HTTP Listener started on 0:0:0:0:0:0:0:0:8283  

[2014-05-25 11:48:59,114] INFO - NioSelectorPool Using a shared selector for servlet write/read  

[2014-05-25 11:48:59,328] INFO - NioSelectorPool Using a shared selector for servlet write/read  

[2014-05-25 11:48:59,343] INFO - RegistryEventingServiceComponent Successfully Initialized Eventing on Registry  

[2014-05-25 11:48:59,379] INFO - JMXServerManager JMX Service URL : service:jmx:rmi://localhost:11114/jndi/rmi://localhost:10002/jmxrmi  

[2014-05-25 11:48:59,379] INFO - StartupFinalizerServiceComponent Server : WSO2 Enterprise Service Bus-4.8.1  

[2014-05-25 11:48:59,380] INFO - StartupFinalizerServiceComponent WSO2 Carbon started in 22 sec  

[2014-05-25 11:48:59,599] INFO - CarbonUIServiceComponent Mgt Console URL : https://192.168.56.1:9446/carbon/  

[2014-05-25 11:49:06,594] INFO - CarbonAuthenticationUtil 'admin@carbon.super [-1234]' logged in at [2014-05-25 11:49:06,594+0200]  

[2014-05-25 11:51:28,952] INFO - ProxyService Building Axis service for Proxy service : MTOMSwASampleService_http  

[2014-05-25 11:51:28,952] INFO - ProxyService Adding service MTOMSwASampleService_http to the Axis2 configuration  

[2014-05-25 11:51:28,954] INFO - DeploymentInterceptor Deploying Axis2 service: MTOMSwASampleService_http {super-tenant}  

[2014-05-25 11:51:28,978] INFO - ProxyService Successfully created the Axis2 service for Proxy service : MTOMSwASampleService_http  

[2014-05-26 22:43:51,296] INFO - CarbonAuthenticationUtil 'admin@carbon.super [-1234]' logged in at [2014-05-26 22:43:51,296+0200]

[2014-05-26 22:47:15,938] INFO - LogMediator ****** MTOMSwASampleService - HTTP = ***** inSeq - ini  

[2014-05-26 22:47:15,945] INFO - LogMediator ======== Proxy-MTOMSwASampleService = ====== inSeq - ini  

[2014-05-26 22:47:15,945] INFO - LogMediator ...calling = oneWayUploadUsingHTTP  

[2014-05-26 22:47:15,946] INFO - LogMediator ======== Proxy MTOMSwASampleService = ====== inSeq - fin  

[2014-05-26 22:47:15,954] INFO - LogMediator ****** MTOMSwASampleService - HTTP = ***** inSeq - fin  

[/sourcecode]

## IV. Conclusiones.
* * *
* Hemos demostrado que WSO2 ESB nos permite transformar tanto la estructura del mensaje como el protocolo de transporte (de HTTP a SOAP) de manera fácil creando únicamente 2 Proxies.
* Hemos implementado un patrón de integración llamado [Content-Based Router (CBR)](https://docs.wso2.org/display/IntegrationPatterns/Content-Based+Router) todo ello gracias a la capacidades de `Mediation` de WSO2 ESB.
* El uso de MTOM directamente desde un form HTML no es posible, es necesario usar otro proxy que transforme la petición `multipart/form-data` a MTOM.
Espero que os haya servido!.
