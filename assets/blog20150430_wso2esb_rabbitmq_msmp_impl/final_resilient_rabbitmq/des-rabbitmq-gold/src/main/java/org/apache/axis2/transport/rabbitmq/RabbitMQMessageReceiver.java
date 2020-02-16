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

package org.apache.axis2.transport.rabbitmq;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.rabbitmq.utils.RabbitMQUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

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
        boolean sendSNP = false;
        try {
    		Map<String, Object > m = message.getHeaders();
    		if (m.get("SNP_EP")!=null) {
    			sendSNP = sendSNP(message);
    		} else {
    			sendSNP = true;
    		}
        	successful = processThroughAxisEngine(message);
        } catch (AxisFault axisFault) {
            log.error("Error while processing message", axisFault);
        }
        return sendSNP && successful;
    }
    
    public boolean sendSNP (RabbitMQMessage message) {

    	boolean successSNP = false;
    	String errorReason = "";
    	
    	try {
    		
    		// send message to snp
	    	Map<String, Object > headersMap = message.getHeaders();
			HttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost("http://"+headersMap.get("SNP_EP")+"/ntc/tax-credits/stop");
	        String body = new String(message.getBody());
	        StringEntity input = new StringEntity(body);
	        
	        post.setEntity(input);
	        post.addHeader("CorrelationId", headersMap.get("CorrelationId")+"");
	        post.addHeader("Content-Type", "application/json");
	        HttpResponse response = client.execute(post);
	        int statusCode = response.getStatusLine().getStatusCode();
	        	        
	        // add CorrelationId to SNP response body
	    	String jsonCorrelationId = ", \"CorrelationId\" : \""+headersMap.get("CorrelationId")+"\"}";
	    	body = body.replace("}", jsonCorrelationId);
	    	message.setBody(body.getBytes());
	        
	        // add HTTP status code to SNP response body
	    	String jsonStatusCode = ", \"StatusCode\" : \""+statusCode+"\"}";
	    	body = body.replace("}", jsonStatusCode);
	    	message.setBody(body.getBytes());
	    	
	    	// SNP response: success
	        if ((statusCode >= 200) && (299 >= statusCode)){
	        	successSNP = true;
		    // SNP response: message error
	        } else if ((statusCode >= 400) && (499 >= statusCode)) {	        	
	        	// get error reason for SNP response body
		        HttpEntity entity = response.getEntity();
		        if (entity!=null) {
		        	BufferedReader br = new BufferedReader(new InputStreamReader((entity.getContent())));
		        	String output = "";
		        	while ((output = br.readLine()) != null) {
		        		errorReason += output;
		    		}
		        }
	        // SNP response: server error
	        } else if ((statusCode >= 500) && (599 >= statusCode)) {
	        	successSNP = true;
	        }
	        
	        // add errorReason to body
	    	String jsonErrorReason = ", \"ErrorReason\" : \""+errorReason+"\"}";
	    	body = body.replace("}", jsonErrorReason);
	    	message.setBody(body.getBytes());

    	} catch (Exception e) {
    		System.out.println(" [sendSNP] exception: "+e);
    	}
        
    	return successSNP;
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
            throw new AxisFault("Unable to determine content type for message " +
                                msgContext.getMessageID());
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
            msgContext.setProperty(Constants.OUT_TRANSPORT_INFO,
                                   new RabbitMQOutTransportInfo(connectionFactory, replyTo, contentType));

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