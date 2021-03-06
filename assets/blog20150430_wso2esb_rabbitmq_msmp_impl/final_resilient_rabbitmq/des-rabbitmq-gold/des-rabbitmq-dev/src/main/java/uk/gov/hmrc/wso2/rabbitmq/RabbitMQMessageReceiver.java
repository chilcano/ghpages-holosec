/*
 * Copyright (c) 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.wso2.rabbitmq;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.axiom.om.util.Base64;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
//import org.apache.axis2.transport.rabbitmq.ConnectionFactory;
import org.apache.axis2.transport.rabbitmq.RabbitMQConstants;
import org.apache.axis2.transport.rabbitmq.RabbitMQMessage;
//import org.apache.axis2.transport.rabbitmq.RabbitMQOutTransportInfo;
import org.apache.axis2.transport.rabbitmq.utils.RabbitMQUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

/**
 * This is the RabbitMQ AMQP message receiver which is invoked when a message is received. This processes
 * the message through the axis2 engine
 */
public class RabbitMQMessageReceiver {
    private static final Log log = LogFactory.getLog(RabbitMQMessageReceiver.class);
    private final RabbitMQEndpoint endpoint;
    private final RabbitMQListener listener;
    private final ConnectionFactory connectionFactory;

    /**
     * Create a new RabbitMQMessage receiver
     *
     * @param listener            the AMQP transport Listener
     * @param connectionFactory   the AMQP connection factory we are associated with
     * @param endpoint            the RabbitMQEndpoint definition to be used
     */
    public RabbitMQMessageReceiver(RabbitMQListener listener,
                                   ConnectionFactory connectionFactory, RabbitMQEndpoint endpoint) {
        this.endpoint = endpoint;
        this.connectionFactory = connectionFactory;
        this.listener = listener;
    }

    /**
     * Process a new message received
     *
     * @param message the RabbitMQ AMQP message received
     */
    public boolean onMessage(RabbitMQMessage message) {
        boolean successful = false;
        boolean sendAck = false;
        try {
    		Map<String, Object > m = message.getHeaders();
    		if (m.get("SNP_EP")!=null) {
    			sendAck = sendSNP(message);
    		} 
    		else if (m.get("NTC_DSS_EP")!=null) {
    			sendAck = sendNTC(message);
    		} else {
    			sendAck = true;
    		}
        	successful = processThroughAxisEngine(message);
        } catch (AxisFault axisFault) {
            log.error("Error while processing message", axisFault);
        }
        return sendAck && successful;
    }
    
    private String basicAuthToken() {
	    String username = System.getProperty("uk.gov.hmrc.wso2.uct.snp.username");
	    String password = System.getProperty("uk.gov.hmrc.wso2.uct.snp.password");
	    String access = username + ":" + password;
	    return "Basic " + Base64.encode(access.getBytes());
	}
    
    public boolean sendSNP (RabbitMQMessage message) {
    	log.debug("Starting sendSNP.");

    	boolean sendAck = false;
    	String errorReason = "";
    	int statusCode = 502;

    	Map<String, Object > headersMap = message.getHeaders();
    	String body = new String(message.getBody());
    		
		try {
    		// send message to snp
			HttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost("http://"+headersMap.get("SNP_EP")+"/ntc/tax-credits/stop");
	       
	        StringEntity input = new StringEntity(body);
	        post.setEntity(input);
	        post.addHeader("CorrelationId", headersMap.get("CorrelationId")+"");
	        post.addHeader("Content-Type", "application/json");
	        post.addHeader("Authorization", basicAuthToken());
	        HttpResponse response = client.execute(post);
	        statusCode = response.getStatusLine().getStatusCode();
	        
	        // get error reason for SNP response body
	        if (400<=statusCode && statusCode<=499) {
		        HttpEntity entity = response.getEntity();
		        if (entity!=null) {
		        	BufferedReader br = new BufferedReader(new InputStreamReader((entity.getContent())));
		        	String output = "";
		        	while ((output = br.readLine()) != null) {
		        		errorReason += output;
		    		}
		        }
	        }

    	} catch (Exception e) {
    		log.error("Error sending the message to SNP: " + e);
    		errorReason = "SNP not reachable";
    	}
        	        
        // add CorrelationId to SNP response body
    	String jsonCorrelationId = ", \"CorrelationId\" : \""+headersMap.get("CorrelationId")+"\"}";
    	body = body.replace("}", jsonCorrelationId);
    	message.setBody(body.getBytes());
        
        // add HTTP status code to SNP response body
    	String jsonStatusCode = ", \"StatusCode\" : \""+statusCode+"\"}";
    	body = body.replace("}", jsonStatusCode);
    	message.setBody(body.getBytes());
    	
        // add errorReason to body
    	String jsonErrorReason = ", \"ErrorReason\" : \""+formatMessage(errorReason)+"\"}";
    	body = body.replace("}", jsonErrorReason);
    	message.setBody(body.getBytes());
    	
    	// SNP response: success or server error
//        if ((200<=statusCode && statusCode<=299) || (500<=statusCode && statusCode<=599)){
//        	sendAck = true;
//        } 
        if (202==statusCode || 502==statusCode){
        	sendAck = true;
        } 
                
    	return sendAck;
	}
    
    public boolean sendNTC (RabbitMQMessage message) {
    	
    	log.debug("Starting sendNTC.");

    	boolean sendAck = false;
    	String errorCode = "";
    	String errorReason = "";
    	int statusCode = 502;

    	Map<String, Object > headersMap = message.getHeaders();
    	String body = new String(message.getBody());
    		
    	
		// send message to NTC DSS
    	//if (hostAvailabilityCheck()) {
    		
    		try {
    			// Create SOAP Connection
    	        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
    	        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

    	        // Send SOAP Message to SOAP Server
    	        String url = "http://"+headersMap.get("NTC_DSS_EP")+"/services/ProcessStopNotice_DSS/";
    	        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(message, headersMap), url);
    	        
    	        Iterator<SOAPElement> it;
    	        
    	        // check errors in SOAP response
    	        SOAPBody soapBody = soapResponse.getSOAPBody();
    	        if (soapBody!= null) {
    	            SOAPFault soapFault = soapBody.getFault();
    		        if (soapFault!=null) {
    		        	boolean faultcode = false;
    		        	boolean faultstring = false; 
    		        	it = soapFault.getChildElements();
    			        while (it.hasNext() && (!faultcode || !faultstring)) {
    			        	SOAPElement element = it.next();
    			        	if (element.getNodeName().equals("faultcode")) {
    			        		errorCode = element.getValue();
    			        		if (errorCode.contains("DATABASE_ERROR")) statusCode = 416;
    			        		else {
    			        			statusCode = 502;
    			        			errorReason = "NTC not reachable";
    			        		}
    			        		faultcode = true;
    			        	} else if (element.getNodeName().equals("faultstring")) {
    			        		errorReason = element.getValue();
    			        		faultstring = true;
    			        	}
    			        }
    			    // check body in SOAP response
    		        } else {
    			        it = soapResponse.getSOAPBody().getChildElements();
    			        while (it.hasNext()) {
    			        	SOAPElement element = it.next();		        	
    			        	if (element.getNodeName().contains("REQUEST_STATUS")) {
    				        	if (element.getValue().contains("SUCCESSFUL")) {
    				        		statusCode = 202;
    				        		break;
    				        	}
    			        	}
    			        }
    		        }	        	
    	        	
    	        } else {
        			statusCode = 502;
        			errorReason = "DSS service not reachable";
    	        }

    	        // print SOAP Response
//    	        System.out.println("Response SOAP Message:");
//    	        soapResponse.writeTo(System.out);

    	        soapConnection.close();

        	} catch (Exception e) {
        		log.error("Error sending the message to NTC_DSS: "+e);
        		errorReason = "DSS not reachable";
        	}    		
//    	}
	        
        // add CorrelationId to SNP response body
    	String jsonCorrelationId = ", \"CorrelationId\" : \""+headersMap.get("CorrelationId")+"\"}";
    	body = body.replace("}", jsonCorrelationId);
    	message.setBody(body.getBytes());
           	
        // add HTTP status code to SNP response body
    	String jsonStatusCode = ", \"StatusCode\" : \""+statusCode+"\"}";
    	body = body.replace("}", jsonStatusCode);
    	message.setBody(body.getBytes());
    	
        // add errorCode to body
    	String jsonErrorCode = ", \"ErrorCode\" : \""+formatMessage(errorCode)+"\"}";
    	body = body.replace("}", jsonErrorCode);
    	message.setBody(body.getBytes());
    	
        // add errorReason to body
    	String jsonErrorReason = ", \"ErrorReason\" : \""+formatMessage(errorReason)+"\"}";
    	body = body.replace("}", jsonErrorReason);
    	message.setBody(body.getBytes());
    	
    	// SNP response: success or server error
//        if ((200<=statusCode && statusCode<=299) || (500<=statusCode && statusCode<=599)){
//        	sendAck = true;
//        } 
        if (202==statusCode || 502==statusCode){
        	sendAck = true;
        } 
                
    	return sendAck;
    	
	}
    
    private String formatMessage(String message) {
    	return message.replaceAll("\"", "");
    }
    
    private boolean checkJsonField(JSONObject obj, String field) {
    	return obj.has(field) && !obj.getString(field).isEmpty();
    }
    
//    public static boolean hostAvailabilityCheck() 
//    { 
//        boolean available = true; 
//        Socket s;
//        try {         
//        	s = new Socket("localhost", 9767);
//            if (s.isConnected()) { 
//            	s.close();    
//            }               
//        } catch (UnknownHostException e) { // unknown host 
//            available = false;
//            s = null;
//        } catch (IOException e) { // io exception, service probably not running 
//            available = false;
//            s = null;
//        } catch (NullPointerException e) {
//            available = false;
//            s=null;
//        }
//
//        return available;   
//    } 
    
    private SOAPMessage createSOAPRequest(RabbitMQMessage message, Map<String, Object > headersMap) throws Exception {
        
    	MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "http://ws.wso2.org/dataservice";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("dat", serverURI);        
        
        /*
		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:dat="http://ws.wso2.org/dataservice">
		   <soapenv:Header/>
		   <soapenv:Body>
		      <dat:createStopNotice>
		         <dat:p_id>1234567890</dat:p_id>
		         <dat:p_nino>AB212345C</dat:p_nino>
		         <dat:p_date_of_birth>1977-11-24</dat:p_date_of_birth>
		         <dat:p_uc_start_date>2014-10-21</dat:p_uc_start_date>
		         <dat:p_uc_sent_date>2014-11-23</dat:p_uc_sent_date>
		         <dat:p_framework_id>1234567</dat:p_framework_id>
		         <dat:p_surname>ADDDDD</dat:p_surname>
		         <dat:p_first_forename>ABBB</dat:p_first_forename>
		         <dat:p_second_forename>ACC</dat:p_second_forename>
		         <dat:p_nissa_indicator>N</dat:p_nissa_indicator>
		      </dat:createStopNotice>
		   </soapenv:Body>
		</soapenv:Envelope>
         */
        
        /*
		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:dat="http://ws.wso2.org/dataservice">
		   <soapenv:Header/>
		   <soapenv:Body>
		      <dat:createMismatchedStopNotice>
		         <dat:p_id>?</dat:p_id>
		         <dat:p_nino>?</dat:p_nino>
		         <dat:p_date_of_birth>?</dat:p_date_of_birth>
		         <dat:p_uc_start_date>?</dat:p_uc_start_date>
		         <dat:p_uc_sent_date>?</dat:p_uc_sent_date>
		         <dat:p_framework_id>?</dat:p_framework_id>
		         <dat:p_surname>?</dat:p_surname>
		         <dat:p_first_forename>?</dat:p_first_forename>
		         <dat:p_second_forename>?</dat:p_second_forename>
		         <dat:p_nissa_indicator>?</dat:p_nissa_indicator>
		         <dat:p_hmrc_nino>?</dat:p_hmrc_nino>
		         <dat:p_hmrc_dob>?</dat:p_hmrc_dob>
		         <dat:p_mismatch_reason>?</dat:p_mismatch_reason>
		      </dat:createMismatchedStopNotice>
		   </soapenv:Body>
		</soapenv:Envelope>
         */
        
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        JSONObject obj = new JSONObject(new String(message.getBody()));
        
        // NTC Stop notice request
        if (!obj.has("mismatchReason") && !obj.has("hmrcDob") && !obj.has("hmrcNino")) {
        	
        	String operation = "createStopNotice";
        	if (!checkJsonField(obj, "dob")){
        		operation = "createStopNoticeDob";
        	} else if (!checkJsonField(obj, "ucDate")){
        		operation = "createStopNoticeUcDate";
        	} else if (!checkJsonField(obj, "receivedDate")){
        		operation = "createStopNoticeReceivedDate";        		
        	}
        	
        	SOAPElement soapBodyElem = soapBody.addChildElement(operation, "dat");
            SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("p_id", "dat");
            soapBodyElem1.addTextNode(headersMap.get("CorrelationId")+"");
            SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("p_nino", "dat");
            soapBodyElem2.addTextNode(obj.getString("nino"));
            SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("p_date_of_birth", "dat");
            soapBodyElem3.addTextNode(obj.getString("dob"));
            SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("p_uc_start_date", "dat");
            soapBodyElem4.addTextNode(obj.getString("ucDate"));
            SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("p_uc_sent_date", "dat");
            soapBodyElem5.addTextNode(obj.getString("receivedDate"));
            SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("p_framework_id", "dat");
            soapBodyElem6.addTextNode(obj.getString("frameworkId"));	
            SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("p_surname", "dat");
            soapBodyElem7.addTextNode(obj.getString("surname"));
            SOAPElement soapBodyElem8 = soapBodyElem.addChildElement("p_first_forename", "dat");
            soapBodyElem8.addTextNode(obj.getString("firstForename"));
            SOAPElement soapBodyElem9 = soapBodyElem.addChildElement("p_second_forename", "dat");
            soapBodyElem9.addTextNode(obj.getString("secondForename")); 	
            SOAPElement soapBodyElem10 = soapBodyElem.addChildElement("p_nissa_indicator", "dat");
            soapBodyElem10.addTextNode(obj.getString("nissaIndicator"));  
            
        // NTC Mismatch request	
        } else {
        	
        	String operation = "createMismatchedStopNotice";
        	if (!checkJsonField(obj, "hmrcDob")) {
        		operation = "createMismatchedStopNoticeHmrcDob";
        	} else if (!checkJsonField(obj, "dob")){
        		operation = "createMismatchedStopNoticeDob";
        	} else if (!checkJsonField(obj, "ucDate")){
        		operation = "createMismatchedStopNoticeUcDate";
        	} else if (!checkJsonField(obj, "receivedDate")){
        		operation = "createMismatchedStopNoticeReceivedDate";        		
        	}
        	
        	SOAPElement soapBodyElem = soapBody.addChildElement(operation, "dat");
            SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("p_id", "dat");
            soapBodyElem1.addTextNode(headersMap.get("CorrelationId")+"");
            SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("p_nino", "dat");
            soapBodyElem2.addTextNode(obj.getString("nino"));
            SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("p_date_of_birth", "dat");
            soapBodyElem3.addTextNode(obj.getString("dob"));
            SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("p_uc_start_date", "dat");
            soapBodyElem4.addTextNode(obj.getString("ucDate"));
            SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("p_uc_sent_date", "dat");
            soapBodyElem5.addTextNode(obj.getString("receivedDate"));
            SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("p_framework_id", "dat");
            soapBodyElem6.addTextNode(obj.getString("frameworkId"));	
            SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("p_surname", "dat");
            soapBodyElem7.addTextNode(obj.getString("surname"));
            SOAPElement soapBodyElem8 = soapBodyElem.addChildElement("p_first_forename", "dat");
            soapBodyElem8.addTextNode(obj.getString("firstForename"));
            SOAPElement soapBodyElem9 = soapBodyElem.addChildElement("p_second_forename", "dat");
            soapBodyElem9.addTextNode(obj.getString("secondForename")); 	
            SOAPElement soapBodyElem10 = soapBodyElem.addChildElement("p_nissa_indicator", "dat");
            soapBodyElem10.addTextNode(obj.getString("nissaIndicator"));  
            SOAPElement soapBodyElem11 = soapBodyElem.addChildElement("p_hmrc_nino", "dat");
            soapBodyElem11.addTextNode(obj.getString("hmrcNino")); 
            SOAPElement soapBodyElem12 = soapBodyElem.addChildElement("p_hmrc_dob", "dat");
            soapBodyElem12.addTextNode(obj.getString("hmrcDob"));
            SOAPElement soapBodyElem13 = soapBodyElem.addChildElement("p_mismatch_reason", "dat");
            soapBodyElem13.addTextNode(obj.getString("mismatchReason"));              	
        	
        }

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", headersMap.get("SOAP_ACTION")+"");

        soapMessage.saveChanges();

        /* Print the request message */
//        System.out.println("Request SOAP Message:");
//        soapMessage.writeTo(System.out);
//        System.out.println();

        return soapMessage;
    }

    /**
     * Process the new message through Axis2
     *
     * @param message the RabbitMQMessage
     * @return true if the caller should commit
     * @throws AxisFault     on Axis2 errors
     */
    private boolean processThroughAxisEngine(RabbitMQMessage message) throws AxisFault {

        MessageContext msgContext = endpoint.createMessageContext();
        
        msgContext.setMessageID(message.getMessageId());
        String amqpCorrelationID = message.getCorrelationId();
        if (amqpCorrelationID != null && amqpCorrelationID.length() > 0) {
            msgContext.setProperty(RabbitMQConstants.CORRELATION_ID, amqpCorrelationID);
        } else {
            msgContext.setProperty(RabbitMQConstants.CORRELATION_ID, message.getMessageId());
        }

        String contentType = message.getContentType();
        if (contentType == null) {
            throw new AxisFault("Unable to determine content type for message " + msgContext.getMessageID());
        }
        msgContext.setProperty(RabbitMQConstants.CONTENT_TYPE, contentType);
        if (message.getContentEncoding() != null) {
            msgContext.setProperty(RabbitMQConstants.CONTENT_ENCODING, message.getContentEncoding());
        }
        String soapAction = message.getSoapAction();

        if (soapAction == null) {
            soapAction = RabbitMQUtils.getSOAPActionHeader(message);
        }
        String replyTo = message.getReplyTo();
        if (replyTo != null) {
            msgContext.setProperty(Constants.OUT_TRANSPORT_INFO, new RabbitMQOutTransportInfo(connectionFactory, replyTo, contentType));
        }
        
        RabbitMQUtils.setSOAPEnvelope(message, msgContext, contentType);

        try {
            listener.handleIncomingMessage(
                    msgContext,
                    RabbitMQUtils.getTransportHeaders(message),
                    soapAction,
                    contentType );
        } catch (AxisFault axisFault) {
            log.error("Error when tryting to read incoming message ...", axisFault);
            return false;
        }
        return true;
    }
}