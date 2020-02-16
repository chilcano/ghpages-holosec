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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.MessageFormatter;
import org.apache.axis2.transport.base.BaseUtils;
import org.apache.axis2.transport.rabbitmq.utils.RabbitMQUtils;
import org.apache.axis2.util.MessageProcessorSelector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * Class that performs the actual sending of a RabbitMQ AMQP message,
 */

public class RabbitMQMessageSender {
    private static final Log log = LogFactory.getLog(RabbitMQMessageSender.class);

    private Connection connection = null;
    private String targetEPR = null;
    private Hashtable<String, String> properties;

    /**
     * Create a RabbitMQSender using a ConnectionFactory and target EPR
     *
     * @param factory   the ConnectionFactory
     * @param targetEPR the targetAddress
     */
    public RabbitMQMessageSender(ConnectionFactory factory, String targetEPR) {
        try {
            this.connection = factory.getConnectionPool();
        } catch (IOException e) {
            handleException("Error while creating connection pool", e);
        }
        this.targetEPR = targetEPR;
        if (!targetEPR.startsWith(RabbitMQConstants.RABBITMQ_PREFIX)) {
            handleException("Invalid prefix for a AMQP EPR : " + targetEPR);
        } else {
            this.properties = BaseUtils.getEPRProperties(targetEPR);
        }
    }

    /**
     * Perform actual send of RabbitMQ AMQP message to the destination
     *
     * @param message    the RabbitMQ AMQP message
     * @param msgContext the Axis2 MessageContext
     */
    public void send(RabbitMQMessage message, MessageContext msgContext) throws
                                                                         AxisRabbitMQException {

        String exchangeName = null;
        AMQP.BasicProperties basicProperties = null;
        byte[] messageBody = null;
        if (connection != null) {
        	Channel channel = null;
        	
        	String queueName = properties.get(RabbitMQConstants.QUEUE_NAME);
            String routeKey = (String)properties.get("rabbitmq.queue.routing.key");

            try {
            	TreeMap transportHeaders = (TreeMap)msgContext.getProperty("TRANSPORT_HEADERS");
            	
            	if (message.getHeaders()==null) message.setHeaders(new HashMap<String, Object>());
            	
            	 if (transportHeaders.get("SNP_EP")!=null) {
                 	if (message.getHeaders()==null) message.setHeaders(new HashMap<String, Object>());
     	            message.getHeaders().put("SNP_EP", transportHeaders.get("SNP_EP"));
     	            System.out.println("SNP_EP..... "+transportHeaders.get("SNP_EP"));
                 }
            	 
            	 if (transportHeaders.get("CorrelationId")!=null) {
                  	if (message.getHeaders()==null) message.setHeaders(new HashMap<String, Object>());
      	            message.getHeaders().put("CorrelationId", transportHeaders.get("CorrelationId"));
      	            System.out.println("CorrelationId..... "+transportHeaders.get("CorrelationId"));
            	 }
            	
            } catch (ClassCastException e) {
            	
            	HashMap transportHeaders = (HashMap)msgContext.getProperty("TRANSPORT_HEADERS");
            	
           	 	if (transportHeaders.get("SNP_EP")!=null) {
                	if (message.getHeaders()==null) message.setHeaders(new HashMap<String, Object>());
    	            message.getHeaders().put("SNP_EP", transportHeaders.get("SNP_EP"));
    	            System.out.println("SNP_EP..... "+transportHeaders.get("SNP_EP"));
                }
           	 	
	           	 if (transportHeaders.get("CorrelationId")!=null) {
	               	if (message.getHeaders()==null) message.setHeaders(new HashMap<String, Object>());
	   	            message.getHeaders().put("CorrelationId", transportHeaders.get("CorrelationId"));
	   	            System.out.println("CorrelationId..... "+transportHeaders.get("CorrelationId"));
	         	 }
           	 }
	                    
            exchangeName = (String)this.properties.get("rabbitmq.exchange.name");
            String exchangeType = (String)this.properties.get("rabbitmq.exchange.type");

            String durable = (String)this.properties.get("rabbitmq.exchange.durable");
        	
                try {
                    if (routeKey == null) {
                        log.info("rabbitmq.queue.routing.key property not found.Using queue name as the routing key..");
                        routeKey = queueName;
                      }

                      channel = connection.createChannel();
                      
                      
                      if ((exchangeName != null) && (!exchangeName.equals(""))) {
                          Boolean exchangeAvailable = Boolean.valueOf(false);
                          try
                          {
                            channel.exchangeDeclarePassive(exchangeName);
                            exchangeAvailable = Boolean.valueOf(true);
                          } catch (IOException e) {
                            log.info("Exchange :" + exchangeName + " not found.Declaring exchange.");
                          }

                          if (!exchangeAvailable.booleanValue())
                          {
                            if (!channel.isOpen())
                              channel = connection.createChannel();
                            try
                            {
                              if ((exchangeType != null) && (!exchangeType.equals("")))
                              {
                                if ((durable != null) && (!durable.equals(""))) {
                                  channel.exchangeDeclare(exchangeName, exchangeType, Boolean.parseBoolean(durable));
                                }
                                else
                                {
                                  channel.exchangeDeclare(exchangeName, exchangeType, true);
                                }
                              }
                              else
                                channel.exchangeDeclare(exchangeName, "direct", true);
                            }
                            catch (IOException e)
                            {
                              handleException("Error occured while declaring exchange.");
                            }
                          }

                          if ((queueName != null) && (!queueName.equals("")))
                          {
                            Boolean queueAvailable = Boolean.valueOf(false);
                            try
                            {
                              channel.queueDeclarePassive(queueName);
                              queueAvailable = Boolean.valueOf(true);
                            } catch (IOException e) {
                              log.info("Queue :" + queueName + " not found.Declaring queue.");
                            }

                            if (!queueAvailable.booleanValue())
                            {
                              if (!channel.isOpen())
                                channel = connection.createChannel();
                              try
                              {
                            	Map<String, Object> args = new HashMap<String, Object>();
                            	args.put("x-dead-letter-exchange", "demo.exchange.death");  
                                channel.queueDeclare(queueName, RabbitMQUtils.isDurableQueue(properties), RabbitMQUtils.isExclusiveQueue(properties), RabbitMQUtils.isAutoDeleteQueue(properties), args);

                                channel.queueBind(queueName, exchangeName, routeKey);
                              }
                              catch (IOException e)
                              {
                                handleException("Error while creating/binding queue: " + e);
                              }

                            }
                            else
                            {
                              try
                              {
                                channel.queueBind(queueName, exchangeName, routeKey);
                              }
                              catch (IOException e) {
                                handleException("Error occured while creating the bind between the queue: " + queueName + " & exchange: " + exchangeName + e);
                              }
                            }
                          }

                        }
                        else
                        {
                          throw new AxisRabbitMQException("rabbitmq.exchange.name property not found.");
                        }
                	

                    AMQP.BasicProperties.Builder builder= buildBasicProperties(message);

                    // set delivery mode
                    String deliveryModeString = properties.get(RabbitMQConstants.QUEUE_DELIVERY_MODE);
                    if (deliveryModeString != null) {
                        int deliveryMode = Integer.parseInt(deliveryModeString);
                        builder.deliveryMode(deliveryMode);
                    }

                    basicProperties = builder.build();
                    OMOutputFormat format = BaseUtils.getOMOutputFormat(msgContext);
                    MessageFormatter messageFormatter = null;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try {
                        messageFormatter = MessageProcessorSelector.getMessageFormatter(msgContext);
                    } catch (AxisFault axisFault) {
                        throw new AxisRabbitMQException("Unable to get the message formatter to use",
                                                axisFault);
                    }

                    try
                    {
                      if ((exchangeType != null) && (exchangeType.equals("x-consistent-hash")))
                      {
                        routeKey = "1234567890";
                      }
                    } catch (UnsupportedCharsetException ex) {
                      handleException("Unsupported encoding " + format.getCharSetEncoding(), ex);
                    }

                    try
                    {
                      messageFormatter.writeTo(msgContext, format, out, false);
                      messageBody = out.toByteArray();
                    } catch (IOException e) {
                      handleException("IO Error while creating BytesMessage", e);
                    } finally {
                      if (out != null) {
                        out.close();
                        channel.abort();
                      }
                    }
                  }
                  catch (IOException e) {
                    handleException("Error while publishing message to the queue ", e);
                  }

                  try
                  {
                    if (this.connection != null)
                      try {
                        channel = this.connection.createChannel();
                        channel.basicPublish(exchangeName, routeKey, basicProperties, messageBody);
                      }
                      catch (IOException e) {
                        log.error("Error while publishing the message");
                      } finally {
                        if (channel != null)
                          channel.close();
                      }
                  }
                  catch (IOException e)
                  {
                    handleException("Error while publishing message to the queue ", e);
                  }
        }
    }

    /**
     * Close the connection
     */
    public void close() {
        if (connection != null && connection.isOpen()) {
            try {
                connection.close();
            } catch (IOException e) {
                handleException("Error while closing the connection ..", e);
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Build and populate the AMQP.BasicProperties using the RabbitMQMessage
     *
     * @param message the RabbitMQMessage to be used to get the properties
     * @return AMQP.BasicProperties object
     */
    private AMQP.BasicProperties.Builder buildBasicProperties(RabbitMQMessage message) {
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties().builder();
        builder.messageId(message.getMessageId());
        builder.contentType(message.getContentType());
        builder.replyTo(message.getReplyTo());
        builder.correlationId(message.getCorrelationId());
        builder.contentEncoding(message.getContentEncoding());
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(RabbitMQConstants.SOAP_ACTION, message.getSoapAction());
        Map<String, Object > m = message.getHeaders();
        if (m!= null && m.get("SNP_EP")!=null) headers.put("SNP_EP", m.get("SNP_EP"));
        if (m!= null && m.get("CorrelationId")!=null) headers.put("CorrelationId", m.get("CorrelationId"));
        builder.headers(headers);
        
        return builder;
    }

    private void handleException(String s) {
        log.error(s);
        throw new AxisRabbitMQException(s);
    }

    private void handleException(String message, Exception e) {
        log.error(message, e);
        throw new AxisRabbitMQException(message, e);
    }

}