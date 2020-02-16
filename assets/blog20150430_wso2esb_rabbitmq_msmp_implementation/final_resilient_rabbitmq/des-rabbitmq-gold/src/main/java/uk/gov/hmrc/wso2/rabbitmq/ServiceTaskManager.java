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

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.axis2.transport.base.threads.WorkerPool;
import org.apache.axis2.transport.rabbitmq.AxisRabbitMQException;
//import org.apache.axis2.transport.rabbitmq.ConnectionFactory;
import org.apache.axis2.transport.rabbitmq.RabbitMQConstants;
import org.apache.axis2.transport.rabbitmq.RabbitMQMessage;
import org.apache.axis2.transport.rabbitmq.utils.RabbitMQUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;


/**
 * Each service will have one ServiceTaskManager instance that will create, manage and also destroy
 * idle tasks created for it, for message receipt. It uses the MessageListenerTask to poll for the
 * RabbitMQ AMQP Listening destination and consume messages. The consumed messages is build and sent to
 * axis2 engine for processing
 */

public class ServiceTaskManager {
    private static final Log log = LogFactory.getLog(ServiceTaskManager.class);

    private static final int STATE_STOPPED = 0;
    private static final int STATE_STARTED = 1;
    private static final int STATE_PAUSED = 2;
    private static final int STATE_SHUTTING_DOWN = 3;
    private static final int STATE_FAILURE = 4;
    private volatile int activeTaskCount = 0;
    
    private WorkerPool workerPool = null;
    private String serviceName;
    private Hashtable<String, String> rabbitMQProperties = new Hashtable<String, String>();
    private final ConnectionFactory connectionFactory;
    private final List<MessageListenerTask> pollingTasks = Collections.synchronizedList(new ArrayList<MessageListenerTask>());
    private RabbitMQMessageReceiver rabbitMQMessageReceiver;
    private int serviceTaskManagerState = STATE_STOPPED;

    public ServiceTaskManager(ConnectionFactory connectionFactory) {
    	log.debug(" ~~> ServiceTaskManager ~ constructor()");
        this.connectionFactory = connectionFactory;
    }

    /**
     * Start  the Task Manager by adding a new MessageListenerTask to he worker pool.
     */
    public synchronized void start() {
        log.debug(" ~~> start() - new MessageListenerTask() is started"); 
        workerPool.execute(new MessageListenerTask());
        serviceTaskManagerState = STATE_STARTED;
    }

    public synchronized void stop() {
    	log.debug(" ~~> stop()");
        if (serviceTaskManagerState != STATE_FAILURE) {
            serviceTaskManagerState = STATE_SHUTTING_DOWN;
        }

        synchronized (pollingTasks) {
        	log.debug(" ~~> stop() - pollingTasks");
            for (MessageListenerTask lstTask : pollingTasks) {
                lstTask.requestShutdown();
            }
        }

        if (serviceTaskManagerState != STATE_FAILURE) {
            serviceTaskManagerState = STATE_STOPPED;
        }
    }

    public synchronized void pause() {
    	log.debug(" ~~> pause() - TODO");
        //TODO implement me ..
    }

    public synchronized void resume() {
    	log.debug(" ~~> resume() - TODO");
        //TODO implement me ..
    }

    public void setWorkerPool(WorkerPool workerPool) {
    	log.debug(" ~~> setWorkerPool()");
        this.workerPool = workerPool;
    }

    public void setRabbitMQMessageReceiver(RabbitMQMessageReceiver rabbitMQMessageReceiver) {
    	log.debug(" ~~> setRabbitMQMessageReceiver()");
    	this.rabbitMQMessageReceiver = rabbitMQMessageReceiver;
    }

    public Hashtable<String, String> getRabbitMQProperties() {
        return rabbitMQProperties;
    }

    public void addRabbitMQProperties(Map<String, String> rabbitMQProperties) {
    	log.debug(" ~~> addRabbitMQProperties()");
        this.rabbitMQProperties.putAll(rabbitMQProperties);
    }

    public void removeAMQPProperties(String key) {
        this.rabbitMQProperties.remove(key);
    }

    public String getServiceName() {
    	log.debug(" ~~> getServiceName() : serviceName = " + serviceName);
        return serviceName;
    }

    public void setServiceName(String serviceName) {
    	log.debug(" ~~> setServiceName(String) : serviceName = " + serviceName);
        this.serviceName = serviceName;
    }

    private void handleException(String msg, Exception e) {
    	log.debug(" ~~> handleException()");
        log.error(msg, e);
        throw new AxisRabbitMQException(msg, e);
    }
    
    /**
     * The actual threads/tasks that perform message polling
     */
    private class MessageListenerTask implements Runnable {

        private Connection connection = null;
        private Channel channel = null;
        private boolean autoAck = false;

        private volatile int workerState = STATE_STOPPED;
        private volatile boolean idle = false;
        private volatile boolean connected = false;
        
        /**
         * As soon as we create a new polling task, add it to the ServiceTaskManager for control later
         */
        MessageListenerTask() {
            synchronized (pollingTasks) {
                pollingTasks.add(this);
            }
        }

        public void pause() {
            //TODO implement me
        }

        public void resume() {
            //TODO implement me
        }

        /**
         * Execute the polling worker task
         */
        public void run() {
        	log.debug(" ~~> MessageListenerTask.run()");
            workerState = STATE_STARTED;
            activeTaskCount++;
           
            try {
            	
                connection = getConnection();
                if (channel == null) {
                	log.debug(" ~~> MessageListenerTask.run() - creating channel because the current channel is null.");
                    channel = connection.createChannel();
                }
                
                QueueingConsumer queueingConsumer = createQueueConsumer(channel);
                
                log.debug(" ~~> MessageListenerTask.run() - out of createQueueConsumer() and before of while()");
                
                while (isActive()) {
                    
                	boolean successful = false;
                    RabbitMQMessage message = null;
                    
                	// == Fixed 18/Nov/2014 ==
                	// "The RabbitMQ ConnectionRecovery implemented" - This try-catch manages AlreadyClosedException
                	try {
                		
	                    try {
	                        channel.txSelect();
	                    } catch (IOException e) {
	                        log.error("Error while starting transaction", e);
	                        continue;
	                    }

		                try {
		                    message = getConsumerDelivery(queueingConsumer);
		                } catch (InterruptedException e) {
		                    log.error("Error while consuming message", e);
		                    log.debug(" ~~> MessageListenerTask.run() - InterruptedException while consuming message.");
		                    continue;
		                }

                	} catch (AlreadyClosedException ace) {
                		log.debug(" ~~> MLT.run() - AlreadyClosedException when starting MLT, wait while the connection is recovered.");
                    } catch (AxisRabbitMQException se) {
                    	log.debug(" ~~> MLT.run() - AxisRabbitMQException - wait while the connection is recovered.");
                    } catch (ShutdownSignalException se) {
                    	log.debug(" ~~> MLT.run() - ShutdownSignalException - wait while the connection is recovered.");
                    }
                
                    if (message != null) {
                        idle = false;
                        try {
                            successful = handleMessage(message);
                        } finally {
                            if (successful) {
                                try {
                                    channel.basicAck(message.getDeliveryTag(), false);
                                    channel.txCommit();
                                    log.debug(" ~~> MessageListenerTask.run() - sending basicAck.");
                                } catch (IOException e) {
                                    log.error("Error while commiting transaction with basicAck.", e);
                                }
                            } else {
                                try {
                                	channel.basicNack(message.getDeliveryTag(), false, false);
                                	channel.txCommit();
                                    //channel.txRollback();
                                	log.debug(" ~~> MessageListenerTask.run() - sending basicNack.");
                                } catch (IOException e) {
                                    log.error("Error while commiting transaction with basicNack.", e);
                                }
                            }
                        }
                    } else {
                    	idle = true;
                    	
                    	// == Fixed 18/Nov/2014 ==
                    	// "The RabbitMQ ConnectionRecovery implemented"
                    	// == Fixed 05/Nov/2014 ==
                    	// "Connection closed abruptly for the LoadBalancer"
                        log.debug(" ~~> MLT.run() - there are not messages, closing connection.");                        
                        closeConnection();
                        workerState = STATE_PAUSED;
                        activeTaskCount--;
                        synchronized (pollingTasks) {
                            pollingTasks.remove(this);
                        }
                   	
                        long seconds = 20;
                        try {
                        	String timeSleeping = System.getProperty("uk.gov.hmrc.wso2.uct.RABBIT_TIME_SLEPPING");
                        	if (timeSleeping!=null && !timeSleeping.isEmpty()) seconds = new Long(timeSleeping);
                        	else log.error("~~> MLT.run() - MLT  error reading uk.gov.hmrc.wso2.uct.RABBIT_TIME_SLEPPING. Setting default timeSleeping to 20 seconds");
                        } catch (NumberFormatException e) {
                        	log.error("~~> MLT.run() - MLT error reading uk.gov.hmrc.wso2.uct.RABBIT_TIME_SLEPPING. Setting default timeSleeping to 20 seconds");
                        }
                        
                    	long timeSleepingInMillisecs = seconds*1000;         
                    	
                    	log.debug(" ~~> MLT.run() - MLT sleeping (" + seconds + " secs).");
                    	Thread.sleep(timeSleepingInMillisecs);
                    	
                        try {
                        	log.debug(" ~~> MLT.run() - creating a new connection.");
                            workerState = STATE_STARTED;
                            activeTaskCount++;
                            connection = getConnection();
                            
                            log.debug(" ~~> MLT.run() - creating a new channel.");
                        	channel.addShutdownListener(new ShutdownListener() {
                                public void shutdownCompleted(ShutdownSignalException e) {
                                	log.trace(" ~~> MLT.run().shutdownCompleted - Reason = " + e.getReason());
                                	log.trace(" ~~> MLT.run().shutdownCompleted - Reference = " + e.getReference());
                                	log.trace(" ~~> MLT.run().shutdownCompleted - Message = " + e.getMessage());
                                    for (StackTraceElement el : e.getStackTrace()) {
                                    	log.trace(" ~~> MLT.run().getStackTrace - " + el.getFileName());
                                    	log.trace(" ~~> MLT.run().getStackTrace - " + el.getMethodName());
                                    }
                                }
                            });
                        	
                        	channel = connection.createChannel();
                            log.debug(" ~~> MLT.run() - new channel was created.");
                            queueingConsumer = createQueueConsumer(channel);
                            log.debug(" ~~> MLT.run() - QueueingConsumer was created.");
                            
                        } catch (AlreadyClosedException ace) {
                        	log.debug(" ~~> MLT.run() - AlreadyClosedException - wait while the connection is recovered.");
                        } catch (SocketException se) {
                        	log.debug(" ~~> MLT.run() - SocketException - wait while the connection is recovered.");
	                    } catch (AxisRabbitMQException se) {
	                    	log.debug(" ~~> MLT.run() - AxisRabbitMQException - wait while the connection is recovered.");
	                    } catch (ShutdownSignalException se) {
	                    	log.debug(" ~~> MLT.run() - ShutdownSignalException - wait while the connection is recovered.");
	                    }   
                        // =======================
                       
                    } // ----- end if
                    
                } // -------- end while()
                
                log.debug(" ~~> MLT.run() - out of while().");
                
            } catch (IOException e) {
                handleException("Error while reciving message from queue", e);
            } catch (Exception e) {
	            handleException("Error while creating connection and/or channel", e);    
            } finally {
            	log.debug(" ~~> MessageListenerTask.run() - finally - before of closeConnection().");
                closeConnection();
                workerState = STATE_STOPPED;
                activeTaskCount--;
                log.debug(" ~~> MessageListenerTask.run() - finally - syncronizing pollingTasks.");
                synchronized (pollingTasks) {
                    pollingTasks.remove(this);
                }
            }
        }

		/**
         * Create a queue consumer using the properties form transport listener configuration
         *
         * @return the queue consumer
         * @throws IOException on error
         */
        private QueueingConsumer createQueueConsumer(Channel channel) throws IOException {
        	log.debug(" ~~> MessageListenerTask.createQueueConsumer()");
            QueueingConsumer consumer = null;
            try {
                String queueName = rabbitMQProperties.get(RabbitMQConstants.QUEUE_NAME);
                String exchangeName = rabbitMQProperties.get(RabbitMQConstants.EXCHANGE_NAME);
                String routeKey = (String)rabbitMQProperties.get("rabbitmq.queue.routing.key");
  
                String autoAckStringValue = rabbitMQProperties.get(RabbitMQConstants.QUEUE_AUTO_ACK);
                if (autoAckStringValue != null) {
                    autoAck = Boolean.parseBoolean(autoAckStringValue);
                }
                
                //If no queue name is specified then service name will be used as queue name
                if (queueName == null || queueName.equals("")) {
                    queueName = serviceName;
                    log.warn("No queue name is specified for " + serviceName + ". " + "Service name will be used as queue name");
                }
                
                if (routeKey == null) {
                    log.info("rabbitmq.queue.routing.key property not found.Using queue name as the routing key..");
                    routeKey = queueName;
                  }

//                channel.queueDeclare(queueName,
//                        RabbitMQUtils.isDurableQueue(rabbitMQProperties),
//                        RabbitMQUtils.isExclusiveQueue(rabbitMQProperties),
//                        RabbitMQUtils.isAutoDeleteQueue(rabbitMQProperties), null);
//                consumer = new QueueingConsumer(channel);
                
                Boolean queueAvailable = Boolean.valueOf(false);
                try {
                  channel.queueDeclarePassive(queueName);
                  queueAvailable = Boolean.valueOf(true);
                } catch (IOException e) {
                  log.info("Queue :" + queueName + " not found. Declaring queue.");
                }

                if (!queueAvailable.booleanValue()) {
                  if (!channel.isOpen()) {
                    channel = connection.createChannel();
                  }
                  try {
                    channel.queueDeclare(queueName, RabbitMQUtils.isDurableQueue(rabbitMQProperties), RabbitMQUtils.isExclusiveQueue(rabbitMQProperties), RabbitMQUtils.isAutoDeleteQueue(rabbitMQProperties), null);
                  } catch (IOException e) {
                    handleException("Error while creating the queue: " + queueName, e);
                  }
                }

                consumer = new QueueingConsumer(channel);
                
                /*
                 * TODO Review this code.
                 * The RabbitMQ's user is not able (user has not permissions) to create exchanges and to do binding to Queues
                 * 
                if (exchangeName != null && !exchangeName.equals("")) {
                    String exchangerType = rabbitMQProperties.get(RabbitMQConstants.EXCHANGE_TYPE);
                    if (exchangerType != null) {
                        String durable = rabbitMQProperties.get(RabbitMQConstants.EXCHANGE_DURABLE);
                        if (durable != null) {
                            channel.exchangeDeclare(exchangeName, exchangerType, Boolean.parseBoolean(durable));
                        } else {
                            channel.exchangeDeclare(exchangeName, exchangerType, true);
                        }
                    } else {
                        channel.exchangeDeclare(exchangeName, "direct", true);
                    }
                    channel.queueBind(queueName, exchangeName, routeKey);
                }
                */
                
                String consumerTagString = rabbitMQProperties.get(RabbitMQConstants.CONSUMER_TAG);
                if (consumerTagString != null) {
                    channel.basicConsume(queueName, autoAck, consumerTagString, consumer);
                    log.debug(" ~~> MessageListenerTask.createQueueConsumer() - basicConsume with TAG was created");
                } else {
                    channel.basicConsume(queueName, autoAck, consumer);
                    log.debug(" ~~> MessageListenerTask.createQueueConsumer() - basicConsume was created");
                }
                
            } catch (IOException e) {
                handleException("Error while creating consumer", e);
	        }       

            return consumer;
        }     
        
        /**
         * Returns the delivery from the consumer
         *
         * @param consumer the consumer to get the delivery
         * @return RabbitMQMessage consumed by the consumer
         * @throws InterruptedException on error
         */
        private RabbitMQMessage getConsumerDelivery(QueueingConsumer consumer) throws InterruptedException {
        	log.debug(" ~~> MessageListenerTask.getConsumerDelivery()");
            RabbitMQMessage message = new RabbitMQMessage();
            QueueingConsumer.Delivery delivery = null;
            
            long seconds = 30;
            try {
            	String consumerTime = System.getProperty("uk.gov.hmrc.wso2.uct.RABBIT_CONSUMMER_TIMEOUT");
            	if (consumerTime!=null && !consumerTime.isEmpty()) seconds = new Long(consumerTime);
            	else log.error("~~> MessageListenerTask.getConsumerDelivery() error reading uk.gov.hmrc.wso2.uct.RABBIT_CONSUMMER_TIMEOUT. Setting default consumerTimeout to 30 seconds");
            } catch (NumberFormatException e) {
            	log.error("~~> MessageListenerTask.getConsumerDelivery() error reading uk.gov.hmrc.wso2.uct.RABBIT_CONSUMMER_TIMEOUT. Setting default consumerTimeout to 30 seconds");
            }

            long consumerTimeInMillisecs = seconds*1000;
            
            try {
            	log.debug(" ~~> MessageListenerTask.getConsumerDelivery() - listening for messages (" + consumerTimeInMillisecs/1000 + " secs).");
            	delivery = consumer.nextDelivery(consumerTimeInMillisecs);
            	//delivery = consumer.nextDelivery();
            } catch (ShutdownSignalException e) {
            	log.debug(" ~~> MessageListenerTask.getConsumerDelivery() - ShutdownSignalException");
                //ignore
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
            	String queueConsumerTag = consumer.getConsumerTag();
            	log.debug(" ~~> MessageListenerTask.getConsumerDelivery() - queueConsumerTag = " + queueConsumerTag);
            	return null;
            }
            //return message;
        }

        /**
         * Invoke message receiver on received messages
         *
         * @param message the AMQP message received
         */
        private boolean handleMessage(RabbitMQMessage message) {
            boolean successful;
            successful = rabbitMQMessageReceiver.onMessage(message);
            log.debug(" ~~> MessageListenerTask.handleMessage() - successful = " + successful);
            return successful;
        }

        protected void requestShutdown() {
        	log.debug(" ~~> MessageListenerTask.requestShutdown() - STATE_SHUTTING_DOWN = " + STATE_SHUTTING_DOWN);
            workerState = STATE_SHUTTING_DOWN;
            closeConnection();
        }

        private boolean isActive() {
        	log.debug(" ~~> MessageListenerTask.isActive() - workerState - STATE_STARTED = " + STATE_STARTED);
            return workerState == STATE_STARTED;
        }
        
        protected boolean isTaskIdle() {
        	log.debug(" ~~> MessageListenerTask.isTaskIdle()");
            return idle;
        }

        public boolean isConnected() {
        	log.debug(" ~~> MessageListenerTask.isConnected()");
            return connected;
        }

        public void setConnected(boolean connected) {
        	log.debug(" ~~> MessageListenerTask.setConnected() - connected = " + connected);
            this.connected = connected;
        }
        
        private Connection getConnection() throws Exception {
        	log.debug(" ~~> MessageListenerTask.getConnection()");
            if (connection == null) {
                connection = createConnection();
                setConnected(true);
            }
            return connection;
        }

        private void closeConnection() {
        	log.debug(" ~~> MessageListenerTask.closeConnection()");
            if (connection != null && connection.isOpen()) {
                try {
                    connection.close();
                } catch (IOException e) {
                    log.error("Error while closing connection ", e);
                } finally {
                    connection = null;
                }
            }
        }

        private Connection createConnection() throws Exception {
        	log.debug(" ~~> MessageListenerTask.createConnection()");
            Connection connection = null;
            connection = connectionFactory.createConnection();

            return connection;
        }
    }

}
