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
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.ParameterIncludeImpl;
import org.apache.axis2.transport.rabbitmq.AxisRabbitMQException;
import org.apache.axis2.transport.rabbitmq.RabbitMQConstants;
//import org.apache.axis2.transport.rabbitmq.utils.RabbitMQUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.Connection;

/**
 * 
 * <b>This class has been extended (cloned) from org.apache.axis2.transport.rabbitmq.ConnectionFactory<b>
 * 
 * Encapsulate a RabbitMQ AMQP Connection factory definition within an Axis2.xml
 *
 * Connection Factory definitions, allows service level parameters to be defined,
 * and re-used by each service that binds to it
 *
 */
public class ConnectionFactory {

    private static final Log log = LogFactory.getLog(ConnectionFactory.class);

    private com.rabbitmq.client.ConnectionFactory connectionFactory = null;
    private String name;
    private Hashtable<String, String> parameters = new Hashtable<String, String>();
    ExecutorService es = Executors.newFixedThreadPool(20);
    private Connection connection = null;



    public ConnectionFactory(String name, com.rabbitmq.client.ConnectionFactory connectionFactory) {
    	log.debug(" ---> ConnectionFactory(name, com.rabbitmq.client.ConnectionFactory connectionFactory) - starts");
        this.name = name;
        this.connectionFactory = connectionFactory;
    }

    /**
     * Digest a AMQP CF definition from an axis2.xml 'Parameter' and construct
     *
     * @param parameter the axis2.xml 'Parameter' that defined the AMQP CF
     */
    public ConnectionFactory(Parameter parameter) {
    	log.debug(" ---> ConnectionFactory(parameter) - starts");
        this.name = parameter.getName();
        ParameterIncludeImpl pi = new ParameterIncludeImpl();

        try {
            pi.deserializeParameters((OMElement) parameter.getValue());
        } catch (AxisFault axisFault) {
            handleException("Error reading parameters for AMQP connection factory" + name, axisFault);
        }

        for (Object o : pi.getParameters()) {
            Parameter p = (Parameter) o;
            parameters.put(p.getName(), (String) p.getValue());
        }
        initConnectionFactory();
        //log.info("AMQP ConnectionFactory : " + name + " initialized");
    }

    public Connection createConnection() {
    	log.debug(" ---> createConnection() - start");
        Connection connection = null;
        try {
            connection = RabbitMQUtils.createConnection(connectionFactory);
        } catch (IOException e) {
            handleException("Error while creating AMQP Connection...", e);
        }
        return connection;
    }

    public Connection getConnectionPool() throws IOException {
    	log.debug(" ---> getConnectionPool() - start");
        //if (connection == null) {	// This does not work, also is necessary to check if this connection is open
        if (connection == null || !connection.isOpen()) {
            connection = connectionFactory.newConnection(es);
            
            long seconds = 60;
            try {
            	String consumerTime = System.getProperty("uk.gov.hmrc.wso2.uct.RABBIT_NETWORK_RECOVERY_INTERVAL");
            	if (consumerTime!=null && !consumerTime.isEmpty()) seconds = new Long(consumerTime);
            	else log.error("---> getConnectionPool() error reading uk.gov.hmrc.wso2.uct.RABBIT_CONSUMMER_TIMEOUT. Setting default NetworkRecoveryInterval to 60 seconds");
            } catch (NumberFormatException e) {
            	log.error("---> getConnectionPool() error reading uk.gov.hmrc.wso2.uct.RABBIT_CONSUMMER_TIMEOUT. Setting default NetworkRecoveryInterval to 60 seconds");
            }

            long networkRecoveryInterval = seconds*1000;
            
            log.debug(" ---> getConnectionPool() - creating connection with AutomaticRecoveryEnabled ("+seconds+" secs).");
            log.debug(" ---> getConnectionPool() - Topology (exchanges, routing key, channels) AutomaticRecovery is not Enabled.");
            connectionFactory.setAutomaticRecoveryEnabled(true);
            connectionFactory.setNetworkRecoveryInterval(networkRecoveryInterval);
            connectionFactory.setTopologyRecoveryEnabled(false);
            
            log.debug(" ---> getConnectionPool() - was forced to create fresh connection successfully.");
        }
        return connection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void handleException(String msg, Exception e) {
        log.error(msg, e);
        throw new AxisRabbitMQException(msg, e);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    private void initConnectionFactory() {
    	log.debug(" ---> initConnectionFactory() - start");
        connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        String hostName = parameters.get(RabbitMQConstants.SERVER_HOST_NAME);
        String portValue = parameters.get(RabbitMQConstants.SERVER_PORT);
        
        if (hostName != null && !hostName.equals("")) {
            connectionFactory.setHost(hostName);
        } else {
            throw new AxisRabbitMQException("Host name is not correctly defined");
        }
        
        int port = Integer.parseInt(portValue);
        
        if (port > 0) {
            connectionFactory.setPort(port);
        }
        
        String userName = parameters.get(RabbitMQConstants.SERVER_USER_NAME);

        if (userName != null && !userName.equals("")) {
            connectionFactory.setUsername(userName);
        }

        String password = parameters.get(RabbitMQConstants.SERVER_PASSWORD);

        if (password != null && !password.equals("")) {
            connectionFactory.setPassword(password);
        }
        String virtualHost = parameters.get(RabbitMQConstants.SERVER_VIRTUAL_HOST);

        if (virtualHost != null && !virtualHost.equals("")) {
            connectionFactory.setVirtualHost(virtualHost);
        }
    }
}
