package uk.gov.hmrc.wso2.rabbitmq.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.axis2.transport.rabbitmq.RabbitMQConstants;
import org.apache.axis2.transport.rabbitmq.RabbitMQMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.task.Task;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

import uk.gov.hmrc.wso2.synapse.message.senders.blocking.BlockingMsgSender;

public class RabbitMQCustomTaskReceiver implements Task, ManagedLifecycle {

	private Log log = LogFactory.getLog(RabbitMQCustomTaskReceiver.class);

	private String queueName;
	private String exchangeName;
	private String queueRoutingKey;
	private String consumerTag;
	private String amqpStringConnectionUri;	// "amqp://userName:password@hostName:portNumber/virtualHost"
	private int nextDeliveryTimeout; 		// seconds
	private String backendEndpoint; 		// http://hostName:portNumber/serviceName
	
	private String httpStatusCodesAck = "200, 202";			// just send ACK to initial Queue.
	private String httpStatusCodesRetry = "502";			// trigger sending MSG to RetryQueue and send ACK to initial Queue.
	private String sequenceRetry = "sequence-SNP-retry";						// name of sequence to send MSG to RetryQueue.
	private String httpStatusCodesDeadLetter = "400,500";	// just send NACK to initial queue. Broker moves MSG to DeadLetterQueue from initial Queue.
		
	private final String QUEUE_AUTO_ACK = "false";  // (true, false) create consumer with AutoAck=false by default
    private final boolean BASICACK_AUTO_ACKNOWLEDGEMENT  = false; // keep message in queue when sending basicAck
	
    /**
     * Holds the SynapseEnvironment to which the MSG will be injected
     */
    private SynapseEnvironment synapseEnvironment;
    
    /**
     * Holds the BlockingMsgSender that will send the MSG to Backend EP (REST or SOAP) in IN-OUT or OUT-ONLY mode.
     */
    private BlockingMsgSender blockingBackendSender = null;
    
    public static void main(String[] args) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
    	BasicConfigurator.configure();
    	RabbitMQCustomTaskReceiver rmqstr = new RabbitMQCustomTaskReceiver();
    	
    	rmqstr.setQueueName("q.test1");
    	rmqstr.setExchangeName("q.ex.test1");
    	rmqstr.setQueueRoutingKey("q.rk.test1");
    	rmqstr.setConsumerTag("tag-task-q-test1");
    	rmqstr.setAmqpStringConnectionUri("amqp://admin:JRq7b1Ct@10.105.135.53:5672/DES_DEV");
    	rmqstr.setNextDeliveryTimeout(20);
    	rmqstr.setBackendEndpoint("http://localhost:8290/services/echo");

    	rmqstr.execute();
   }
    
	public RabbitMQCustomTaskReceiver() {
		// empty constructor
	}

	
	public void destroy() {
    	log.debug("destroy()");
	}

	public void init(SynapseEnvironment se) {
		log.debug("init(SynapseEnvironment)");
		this.synapseEnvironment = se;
    	this.blockingBackendSender = new BlockingMsgSender();
	}
	
	// ------------- getters of the scheduled task * begin ----------------- //
	
	public String getQueueName() {
		return queueName;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public String getQueueRoutingKey() {
		return queueRoutingKey;
	}

	public String getConsumerTag() {
		return consumerTag;
	}
	
	public String getAmqpStringConnectionUri() {
		return amqpStringConnectionUri;
	}
	
	public int getNextDeliveryTimeout() {
		return nextDeliveryTimeout;
	}

	public String getBackendEndpoint() {
		return backendEndpoint;
	}
	
	// ------------- getters of the scheduled task * begin ----------------- //
	
	// ------------- setters of the scheduled task * begin ----------------- //
	
	public void setQueueName(String queueName) {
		log.debug("setQueueName() = " + queueName);
		this.queueName = queueName;
	}

	public void setExchangeName(String exchangeName) {
		log.debug("setExchangeName() = " + exchangeName);
		this.exchangeName = exchangeName;
	}

	public void setQueueRoutingKey(String queueRoutingKey) {
		log.debug("setQueueRoutingKey() = " + queueRoutingKey);
		this.queueRoutingKey = queueRoutingKey;
	}

	public void setConsumerTag(String consumerTag) {
		log.debug("setConsumerTag() = " + consumerTag);
		this.consumerTag = consumerTag;
	}
	
	public void setAmqpStringConnectionUri(String amqpStringConnectionUri) {
		log.debug("setAmqpStringConnectionUri() = " + amqpStringConnectionUri);
		this.amqpStringConnectionUri = amqpStringConnectionUri;
	}
	
	public void setNextDeliveryTimeout(int nextDeliveryTimeout) {
		if (nextDeliveryTimeout < 1) {
			this.nextDeliveryTimeout = 20;
		} else {
			this.nextDeliveryTimeout = nextDeliveryTimeout;
		}
		log.debug("setNextDeliveryTimeout() = " + this.nextDeliveryTimeout);
		
	}
	
	public void setBackendEndpoint(String backendEndpoint) {
		log.debug("setBackendEndpoint() = " + backendEndpoint);
		this.backendEndpoint = backendEndpoint;
	}
	
	// ------------- setters of the scheduled task * end ----------------- //
	
	public String getObfuscatedAmqpStringConnectionUri() {
		// i.e.: amqp://admin:xxx@10.105.135.53:5672/DES_DEV
		String strAmqpUri = getAmqpStringConnectionUri();

		try {
			URI amqpUri = new URI(strAmqpUri);			
			int beginAtIndex = amqpUri.toString().indexOf("@");
			String strAmqpUri01 = strAmqpUri.substring(0, beginAtIndex - 1);
			String strAmqpUri02 = strAmqpUri.substring(beginAtIndex);
			int beginPwdIndex = strAmqpUri01.indexOf(":", 6);
			String strAmqpUri00 = strAmqpUri01.substring(0, beginPwdIndex + 1);
			return strAmqpUri00 + "xxx" + strAmqpUri02;
			
		} catch (URISyntaxException e) {
			return "AMQP URI (" + strAmqpUri + ") is malformatted";
		}

	}
	
	
	/*
	 * Implements logic of the Custom Scheduled Task.
	 * This Task retrieves the messages from RabbitMQ queue as a WSO2 ESB scheduled task. 
	 * 
	 * @see org.apache.synapse.task.Task#execute()
	 */
	public void execute() {
		
    	log.debug("execute() - start");

    	try {
			getMessageFromQueue();
		} catch (ShutdownSignalException se) {
        	log.error("ShutdownSignalException.", se);
		} catch (Exception e) {
			log.error("Exception", e);
		}
		
	}
	
	
	private void getMessageFromQueue() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		
    	log.debug("getMessageFromQueue() - start");
    	   
        ConnectionFactory connFactory = new ConnectionFactory();     
        connFactory.setUri(getAmqpStringConnectionUri());
        
		Connection connection = null;
		Channel channel = null;
		QueueingConsumer queueingConsumer = null;
		
		boolean isReceivingMessages = true;
		
        try {
    		connection = connFactory.newConnection();
    		channel = connection.createChannel();        	
    		queueingConsumer = createQueueConsumer(connection, channel);
    		
			while (isReceivingMessages) {
			
	        	boolean successful = false;
	            RabbitMQMessage rabbitMQMessage = null;			
	            String strMsgReceivedBody = null;
	            
	            try {
	                channel.txSelect(); // start transaction
	            } catch (IOException e) {
	                log.error("getMessageFromQueue() - IOException - Error while starting transaction.", e);
	                continue;
	            }
	            
	            try {
	            	rabbitMQMessage = getConsumerDelivery(queueingConsumer);
	            } catch (InterruptedException e) {
	                log.error("getMessageFromQueue() - InterruptedException - Error while consuming message.", e);
	                continue;
	            }
	
	            if (rabbitMQMessage != null) {
	
	            	
	                try {
	    				strMsgReceivedBody = new String(rabbitMQMessage.getBody());
	    				log.debug("getMessageFromQueue() - Message Received:\n" + strMsgReceivedBody + "\n");
	    				
	                	successful = callBackend(rabbitMQMessage);
	    				
	                } finally {
	                    if (successful) {
	                        try {
	                            channel.basicAck(rabbitMQMessage.getDeliveryTag(), BASICACK_AUTO_ACKNOWLEDGEMENT);	                            
	                            channel.txCommit();
	                            log.debug("getMessageFromQueue() - Sending basic ACK.");
	                        } catch (IOException e) {
	                            log.error("getMessageFromQueue() - IOException - Error while commiting transaction with basic ACK.", e);
	                        }
	                    } else {
	                        try {
	                        	channel.basicNack(rabbitMQMessage.getDeliveryTag(), false, false);
	                        	channel.txCommit();
	                            //channel.txRollback();
	                        	log.debug("getMessageFromQueue() - Sending basic NACK.");
	                        } catch (IOException e) {
	                        	log.error("getMessageFromQueue() - IOException - Error while commiting transaction with basic NACK.", e);
	                        }
	                    }
	                    
	                } // -- finally
	                
	                
	            } // -- if		
	            else {
	            	// ready to go out of while bucle because there are not messages in queue and NEXT_DELIVERY_TIMEOUT is over
                    isReceivingMessages = false;
	            	log.debug("getMessageFromQueue() - Releasing Connection and Channel because there are not messages in queue and NEXT_DELIVERY_TIMEOUT is over."); 
	            }
			} // -- end while


        } catch (IOException ioe) {
        	log.debug("getMessageFromQueue() - IOException - when creating new Connection, Channel and QueueingConsumer to '" + getObfuscatedAmqpStringConnectionUri() + "'", ioe);
        } finally {
        	log.debug("getMessageFromQueue() - Closing Connection and Channel (from finally).");
        	
        	// ready to go out of while bucle because there are some exceptions
        	isReceivingMessages = false;
        	
        	if ( (channel != null) ) {
        		try {
        			channel.close();    // close channel with OK
        			log.debug("getMessageFromQueue() - Channel closed.");
        		} catch (IOException e) {
                    log.error("getMessageFromQueue() - IOException - Error while closing channel.", e);
                } finally {
                    channel = null;
                }
        	} else {
        		log.debug("getMessageFromQueue() - Channel is already closed.");
        	}
			
        	if ( (connection != null && connection.isOpen()) ) {
        		try {
        			connection.close();
        			log.debug("getMessageFromQueue() - Connection closed.");
        		} catch (IOException e) {
                    log.error("getMessageFromQueue() - IOException - Error while closing connection.", e);
                } finally {
                    connection = null;
                }
        	} else {
        		log.debug("getMessageFromQueue() - Connection is already closed.");
        	}
		} // -- finally

	}

	
    private QueueingConsumer createQueueConsumer(Connection connection, Channel channel) throws IOException {
    	
    	log.debug("createQueueConsumer() - start");
        QueueingConsumer consumer = null;
        boolean autoAck = false;
        String serviceName = "tmp-queue-" + System.currentTimeMillis();
      
        try {
             
            String queueName = getQueueName();
            String exchangeName = getExchangeName();
            String routeKey = getQueueRoutingKey();
            String consumerTagString = getConsumerTag();
            String autoAckStringValue = QUEUE_AUTO_ACK;
            
            if (autoAckStringValue != null) {
                autoAck = Boolean.parseBoolean(autoAckStringValue);
            }
            
            //If no queue name is specified then service name will be used as queue name
            if (queueName == null || queueName.equals("")) {
                queueName = serviceName;
                log.warn("createQueueConsumer() - No queue name is specified for " + serviceName + ". " + "Service name will be used as queue name.");
            }
            
            if (routeKey == null  || routeKey.equals("")) {
                log.info("createQueueConsumer() - rabbitmq.queue.routing.key property not found. Using queue name as the routing key.");
                routeKey = queueName;
              }
            
            consumer = new QueueingConsumer(channel);
            
            if (consumerTagString == null || consumerTagString.equals("")) {
                channel.basicConsume(queueName, autoAck, consumer);
                log.debug("createQueueConsumer() - basicConsume was created. Without TAG, QUEUE_AUTO_ACK (" + autoAck + ")");
            } else {
                channel.basicConsume(queueName, autoAck, consumerTagString, consumer);
                log.debug("createQueueConsumer() - basicConsume was created. With TAG (" + consumerTagString + "), QUEUE_AUTO_ACK (" + autoAck + ")");
            }
            
        } catch (IOException e) {
        	log.error("createQueueConsumer() - IOException - Error while creating consumer.", e);
        }       

        return consumer;
    }

    private RabbitMQMessage getConsumerDelivery(QueueingConsumer queueingConsumer) throws InterruptedException {
    	
        RabbitMQMessage message = new RabbitMQMessage();
        Delivery delivery = null;
        
        try {
        	log.debug("getConsumerDelivery() - A new QueueConsumerDelivery was created for " + getNextDeliveryTimeout() + " seconds more.");
        	delivery = queueingConsumer.nextDelivery(getNextDeliveryTimeout()*1000);
        	
        	//delivery = queueingConsumer.nextDelivery();
        	
        } catch (ShutdownSignalException e) {
        	log.error("getConsumerDelivery() - ShutdownSignalException", e);
            return null;
        }
        
        if (delivery != null) {
            AMQP.BasicProperties properties = delivery.getProperties();
            Map<String, Object> headers = properties.getHeaders();
            message.setBody(delivery.getBody());
            message.setDeliveryTag(delivery.getEnvelope().getDeliveryTag());
            message.setReplyTo(properties.getReplyTo());
            message.setMessageId(properties.getMessageId());
            message.setContentType(properties.getContentType());
            message.setContentEncoding(properties.getContentEncoding());
            message.setCorrelationId(properties.getCorrelationId());
            if (headers != null) {
                message.setHeaders(headers);
                message.setSoapAction(headers.get(RabbitMQConstants.SOAP_ACTION).toString());
            }
            return message;
        } else {
        	return null;
        }

    }
    
    
    public boolean callBackend(RabbitMQMessage message) {
    	log.debug("callBackend() - Endpoint = " + getBackendEndpoint());
    	
    	

    	
    	
    	
        boolean successful = false;
       // boolean sendAckBE = false;
       // try {
    		Map<String, Object > m = message.getHeaders();
    		
    		if (m.get("SNP_EP") != null) {
    			//sendAckBE = sendSNP(message);
    		} else {
    			//sendAckBE = true;
    			// TODO just for testing
    			successful = true;
    			
    		}
        	//successful = processThroughAxisEngine(message);
        //} catch (AxisFault axisFault) {
         //   log.error(" ~~~~> Error while processing message", axisFault);
        //}
        //return sendAck && successful;
    		
    		
    		
    	return successful;
    }	
	
}
