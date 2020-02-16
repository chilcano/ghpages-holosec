package uk.gov.hmrc.wso2.rabbitmq.message.store.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2SynapseEnvironment;
import org.apache.synapse.message.MessageConsumer;
import org.apache.synapse.message.store.impl.jms.JmsConsumer;
import org.apache.synapse.message.MessageProducer;
import org.apache.synapse.message.store.impl.jms.JmsProducer;
import org.apache.synapse.message.store.AbstractMessageStore;
import org.apache.synapse.message.store.Constants;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

public class RabbitMQStore extends AbstractMessageStore {
	
    /** JMS Broker username */
    public static final String USERNAME = "store.jms.username";
    /** JMS Broker password */
    public static final String PASSWORD = "store.jms.password";
    /** Whether to cache the connection or not */
    public static final String CACHE = "store.jms.cache.connection";
    
    /** JMS destination (ie. Queue) name that this message store must store the messages to. */
    // Publ: BURL:direct://amq.direct//?routingkey='des_rk_queue1'
    public static final String DESTINATION_PUBLISHER = "store.jms.destinationPublisher"; 
    // Cons: BURL:direct://amq.direct/des_rk_queue1/des_queue1
    public static final String DESTINATION_CONSUMER = "store.jms.destinationConsumer";   
    
    /** JMS Specification version */
    public static final String JMS_VERSION = "store.jms.JMSSpecVersion";
    /** */
    public static final String CONSUMER_TIMEOUT = "store.jms.ConsumerReceiveTimeOut";
    /** */
    public static final String CONN_FACTORY = "store.jms.connection.factory";
    /** */
    public static final String NAMING_FACTORY_INITIAL = "java.naming.factory.initial";
    /** */
    public static final String CONNECTION_STRING = "connectionfactory.QueueConnectionFactory";
    /** */
    public static final String PROVIDER_URL = "java.naming.provider.url";
    /** JNDI Queue Prefix */
    public static final String QUEUE_PREFIX = "queue.";

    /** JMS connection properties */
    private final Properties properties = new Properties();
    /** JMS username */
    private String userName;
    /** JMS password */
    private String password;
    
    /** JMS queue name */
    // Publ: BURL:direct://amq.direct//?routingkey='des_rk_queue1'
    private String destinationPublisher;
    // Cons: BURL:direct://amq.direct/des_rk_queue1/des_queue1
    private String destinationConsumer;  
    
    /** type of the JMS destination. we support queue */
    private String destinationType = "queue";
    /** */
    private static final Log logger = LogFactory.getLog(RabbitMQStore.class.getName());
    /** */
    private int cacheLevel = 0;
    /** */
    public static final String JMS_SPEC_11 = "1.1";
    /** Is JMS Version 1.1? */
    private boolean isVersion11 = true;
    /** Look up context */
    private Context context;
    /** JMS cachedConnection factory */
    private javax.jms.ConnectionFactory connectionFactory;
    
    /** JMS destination */
    // Publ: BURL:direct://amq.direct//?routingkey='des_rk_queue1'
    private Destination queuePublisher;
    // Cons: BURL:direct://amq.direct/des_rk_queue1/des_queue1
    private Destination queueConsumer;
    
    /** */
    private final Object queueLock = new Object();
    /** JMS Connection used to send messages to the queue */
    private Connection producerConnection;
    /** lock protecting the producer connection */
    private final Object producerLock = new Object();
    /** records the last retried time between the broker and ESB */
    private long retryTime = -1;

    
    public MessageProducer getProducer() {
    	
    	logger.debug("getProducer() - start");
    	
        //RabbitMQFakeProducer producer = new RabbitMQFakeProducer(this);
    	JmsProducer producer = new JmsProducer(this);
        producer.setId(nextProducerId());
        Throwable throwable = null;
        Session session = null;
        javax.jms.MessageProducer messageProducer;
        
        boolean error = false;
        try {
            synchronized (producerLock) {
                if (producerConnection == null) {
                    boolean ok = newWriteConnection();
                    if (!ok) {
                        return producer;
                    }
                }
            }
            try {
                session = newSession(producerConnection(), Session.AUTO_ACKNOWLEDGE);
            } catch (JMSException e) {
                synchronized (producerLock) {
                    boolean ok = newWriteConnection();
                    if (!ok) {
                        return producer;
                    }
                }
                session = newSession(producerConnection(), Session.AUTO_ACKNOWLEDGE);
                logger.info("getProducer() - " + nameString() + " established a connection to the broker.");
            }
            messageProducer = newProducer(session);
            producer.setConnection(producerConnection()).setSession(session).setProducer(messageProducer);
        } catch (Throwable t) {
            error = true;
            throwable = t;
        }
        if (error) {
            String errorMsg = "getProducer() - " + "Could not create a Message Producer for " + nameString() + ". Error:" + throwable.getLocalizedMessage();
            logger.error(errorMsg, throwable);
            synchronized (producerLock) {
                cleanup(producerConnection, session, true);
                producerConnection = null;
            }
            return producer;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getProducer() - " + nameString() + " created message producer " + producer.getId());
        }
        return producer;
    }

    
    public MessageConsumer getConsumer() {
    	
    	logger.debug("getConsumer() - start");
    	
        //RabbitMQFakeConsumer consumer =  new RabbitMQFakeConsumer(this);
        JmsConsumer consumer =  new JmsConsumer(this);
        consumer.setId(nextConsumerId());
        Connection connection = null;
        try {
            // Had to add a condition to avoid piling up log files with the error message and throttle retries.
            // need to improve this to allow the client to configure it.
            if ((System.currentTimeMillis() - retryTime) >= 3000) {
                connection = newConnection();
                retryTime = -1;
            }
        } catch (JMSException e) {
            retryTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.error("getConsumer() - " + "Could not create a Message Consumer for " + nameString() + ". Could not create connection.");
            }
            return consumer;
        }
        if (connection == null) {
            return consumer;
        }
        Session session;
        try {
            session = newSession(connection, Session.CLIENT_ACKNOWLEDGE);
        } catch (JMSException e) {
            if (logger.isDebugEnabled()) {
                logger.error("getConsumer() - " + "Could not create a Message Consumer for " + nameString() + ". Could not create session.");
            }
            return consumer;
        }
        if (session == null) {
            return consumer;
        }
        javax.jms.MessageConsumer c;
        try {
            c = newConsumer(session);
        } catch (JMSException e) {
            if (logger.isDebugEnabled()) {
                logger.error("getConsumer() - " + "Could not create a Message Consumer for " + nameString() + ". Could not create consumer.");
            }
            return consumer;
        }
        consumer.setConnection(connection).setSession(session).setConsumer(c);
        if (logger.isDebugEnabled()) {
            logger.debug("getConsumer() - " + nameString() + " created message consumer " + consumer.getId());
        }
        return consumer;
    }

    
    public int getType() {
        return Constants.JMS_MS;
    }

    /** JMS Message store does not support following operations. */
    public MessageContext remove() throws NoSuchElementException {
        return null;
    }

    public void clear() {
    }

    public MessageContext remove(String messageID) {
        return null;
    }

    public MessageContext get(int index) {
        return null;
    }

    public List<MessageContext> getAll() {
        return null;
    }

    public MessageContext get(String messageId) {
        return null;
    } /** End of unsupported operations. */

    
    public void init(SynapseEnvironment se) {
    	
    	logger.debug("init(SynapseEnvironment)");
        if (se == null) {
            logger.error("Cannot initialize store.");
            return;
        }
        boolean initOk = initme();
        super.init(se);
        if (initOk) {
            logger.info(nameString() + ". Initialized.");
        } else {
            logger.info(nameString() + ". Initialization failed.");
        }       
        boolean initSystemPropsOk = initSystemProps();
        if (initSystemPropsOk) {
            logger.info(nameString() + ". System Properties - Initialized.");
        } else {
            logger.info(nameString() + ". System Properties - Initialization failed.");
        }
    }

    private boolean initSystemProps(){
    	logger.debug("initSystemProps() - start");
    	
    	
    	
    	
    	try {
    		System.setProperty("qpid.amqp.version", "0-91");
    		logger.debug("initSystemProps() - qpid.amqp.version \t: " + System.getProperty("qpid.amqp.version"));
    		
    		//System.setProperty("qpid.dest_syntax", "BURL");
    		//logger.debug("initSystemProps() - qpid.dest_syntax \t: " + System.getProperty("qpid.dest_syntax"));
    		
    		System.setProperty("IMMEDIATE_PREFETCH", "true");
    		logger.debug("initSystemProps() - IMMEDIATE_PREFETCH \t: " + System.getProperty("IMMEDIATE_PREFETCH"));

    		System.setProperty("qpid.declare_exchanges", "false");
    		logger.debug("initSystemProps() - qpid.declare_exchanges \t: " + System.getProperty("qpid.declare_exchanges"));
    		
    		System.setProperty("qpid.declare_queues", "false");
    		logger.debug("initSystemProps() - qpid.declare_queues \t: " + System.getProperty("qpid.declare_queues"));
       		
    		return true;
    	} catch (Exception e) {
    		logger.error("initSystemProps() - Error: Cannot set System Properties.", e);
    		return false;
    	}
    	
    }
    
    private boolean initme() { // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
    	
    	logger.debug("initme() - start");
    	
        Set<Map.Entry<String, Object>> mapSet = parameters.entrySet();
        for (Map.Entry<String, Object> e : mapSet) {
            if (e.getValue() instanceof String) {
                properties.put(e.getKey(), e.getValue());
                
                System.out.println("initme() ~ " + e.getKey() + " : " + e.getValue());
            }
        }
        userName = (String) parameters.get(USERNAME);
        password = (String) parameters.get(PASSWORD);
        
        String conCaching = (String) parameters.get(CACHE);
        if ("true".equals(conCaching)) {
            logger.debug("initme() - " + nameString() + " enabling connection Caching");
            cacheLevel = 1;
        }
        
        String strDestinationPublisher = (String) parameters.get(DESTINATION_PUBLISHER);
        if (strDestinationPublisher != null) {
            this.destinationPublisher = strDestinationPublisher;
        } else {
            String name = getName();
            String defaultDestPublisher;
            if (name != null && !name.isEmpty()) {
            	defaultDestPublisher = name + "_Queue";
            } else {
            	defaultDestPublisher =  "MyRabbitMQMsgStore4Publisher_" + System.currentTimeMillis() + "_Queue";
            }
            logger.warn("initme() - " + nameString() + ". Destination not provided. " + "Setting default destination to [" + defaultDestPublisher + "].");
            this.destinationPublisher = defaultDestPublisher;
        }

        // TODO documentar
        String strDestinationConsumer = (String) parameters.get(DESTINATION_CONSUMER);
        if (strDestinationConsumer != null) {
            this.destinationConsumer = strDestinationConsumer;
        } else {
            String strNameStore = getName();
            String defaultDestConsumer;
            if (strNameStore != null && !strNameStore.isEmpty()) {
            	defaultDestConsumer = strNameStore + "_Queue";
            } else {
            	defaultDestConsumer =  "MyRabbitMQMsgStore4Consumer_" + System.currentTimeMillis() + "_Queue";
            }
            logger.warn("initme() - " + nameString() + ". Destination for consumer not provided. " + "Setting default destination to [" + defaultDestConsumer + "].");
            this.destinationConsumer = defaultDestConsumer;
        }
        
        destinationType = "queue";
        
        String version = (String) parameters.get(JMS_VERSION);
        if (version != null) {
            if (!JMS_SPEC_11.equals(version)) {
                isVersion11 = false;
            }
        }
        
        String consumerReceiveTimeOut = (String) parameters.get(CONSUMER_TIMEOUT);
        int consumerReceiveTimeOutI = 6000;
        if (consumerReceiveTimeOut != null) {
            try {
                consumerReceiveTimeOutI = Integer.parseInt(consumerReceiveTimeOut);
            } catch (NumberFormatException e) {
                //logger.error(nameString() + ". Error parsing consumer receive time out value. " + "Set to 60s.");
            }
        } //else {
            //logger.warn(nameString() + ". Consumer Receiving time out not passed in. " + "Set to 60s.");
        //}
        
        String strConnectionFactory = null;
        try {
            context = new InitialContext(properties);
            strConnectionFactory = (String) parameters.get(CONN_FACTORY);
            if (strConnectionFactory == null) {
                strConnectionFactory = "QueueConnectionFactory";
            }
            connectionFactory = lookup(context, javax.jms.ConnectionFactory.class, strConnectionFactory);
            if (connectionFactory == null) {
                throw new SynapseException("initme() - " + nameString() + " could not initialize JMS Connection Factory. " + "Connection factory not found : " + strConnectionFactory);
            }
            
            createDestPublisherIfAbsent(null);
            if (queuePublisher == null) {
                logger.warn("initme() - " + nameString() + ". JMS Destination Publisher [" + destinationPublisher + "] does not exist.");
            }
            
            // TODO documentar
            createDestConsumerIfAbsent(null);
            if (queueConsumer == null) {
                logger.warn("initme() - " + nameString() + ". JMS Destination Consumer [" + destinationConsumer + "] does not exist.");
            }
            
        } catch (NamingException e) {
            logger.error("initme() - " + nameString() 
            		+ ". Could not initialize JMS Message Store. Error:"
                    + e.getLocalizedMessage() 
                    + ". Initial Context Factory:[" + parameters.get(NAMING_FACTORY_INITIAL) 
                    + "]; Provider URL:[" + parameters.get(PROVIDER_URL) 
                    + "]; Connection Factory:[" + strConnectionFactory + "].");
        } catch (Throwable t) {
            logger.error("initme() - " + nameString() 
            		+ ". Could not initialize JMS Message Store. Error:"
                    + t.getMessage() + ". Initial Context Factory:[" + parameters.get(NAMING_FACTORY_INITIAL) + "]; Provider URL:[" + parameters.get(PROVIDER_URL) + "]; Connection Factory:[" + strConnectionFactory + "].");
        }
        if (!newWriteConnection()) {
            logger.warn("initme() - " + nameString() + ". Starting with a faulty connection to the broker.");
            return false;
        }
        return true;
    }

    
    public void destroy() {
    	//logger.debug("destroy()");
        // do whatever...
        logger.debug("Destroying " + nameString() + "...");
        closeWriteConnection();
        super.destroy();
    }

    
    /**
     * Creates a new JMS Connection.
     *
     * @return A connection to the JMS Queue used as the store of this message store.
     * @throws JMSException
     */
    public Connection newConnection() throws JMSException {
    	
    	logger.debug("newConnection() - start");
        Connection connection;
        if (connectionFactory == null) {
            logger.error("newConnection() - " + nameString() + ". Could not create a new connection to the broker." +
                    " \nInitial Context Factory:[" + parameters.get(NAMING_FACTORY_INITIAL) + "]; Provider URL: [" + parameters.get(PROVIDER_URL) + "]; Connection Factory:[null].");
            return null;
        }
        
        logger.debug("newConnection() - userName : " + userName + ", password : " + password);
        
        if (isVersion11) {
        	logger.debug("newConnection() - isVersion11 : yes");
            if (userName != null && password != null) {
                connection = connectionFactory.createConnection(userName, password);
            } else {
                connection = connectionFactory.createConnection();
            }
        } else {
        	logger.debug("newConnection() - isVersion11 : not");
            QueueConnectionFactory queueConnectionFactory;
            queueConnectionFactory = (QueueConnectionFactory) this.connectionFactory;
            if (userName != null && password != null) {
                connection = queueConnectionFactory.createQueueConnection(userName, password);
            } else {
                connection = queueConnectionFactory.createQueueConnection();
            }
        }
        logger.debug("newConnection() - " + nameString() + ". JMS connection was created.");
        connection.start();
        logger.debug("newConnection() - " + nameString() + ". JMS Connection started.");

        return connection;
    }

    /**
     * Creates a new JMS Session.
     *
     * @param connection The JMS Connection that must be used when creating the session.
     * @param mode Acknowledgement mode that must be used for this session.
     * @return A JMS Session.
     * @throws JMSException
     */
    public Session newSession(Connection connection, int mode) throws JMSException {
    	
    	logger.debug("newSession() - start");
        if (connection == null) {
            logger.error("newSession() - " + nameString() + " cannot create JMS Session. Invalid connection.");
            return null;
        }
        Session session;
        if (isVersion11) {
            session = connection.createSession(false, mode);
        } else {
            session = ((QueueConnection) connection).createQueueSession(false, mode);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("newSession() - " + nameString() + ". Created JMS Session.");
        }
        return session;
        
    }

    /**
     * Creates a new JMS Message Producer.
     *
     * @param session  A JMS Session.
     * @return A JMS Message Producer.
     * @throws JMSException
     */
    public javax.jms.MessageProducer newProducer(Session session) throws JMSException {
    	
    	logger.debug("newProducer(session) - start");
        if (session == null) {
            logger.error("newProducer(Session) - " + nameString() + " cannot create JMS Producer. Invalid session.");
            return null;
        } else {
        	logger.debug("newProducer(session) - 'session' is null.");
        }
        if (!createDestPublisherIfAbsent(session)) {
            logger.error("newProducer(session) - " + nameString() + " cannot create JMS Producer. Destination queue is invalid.");
            return null;
        } else {
        	logger.debug("newProducer(session) - Is not necessary to create a Destination (in Broker) because It exists.");
        }
        javax.jms.MessageProducer messageProducer;
        logger.debug("newProducer(session) - Destination for JMS MessageProducer : " + queuePublisher.toString());
        if (isVersion11) {
        	messageProducer = session.createProducer(queuePublisher);
        } else {
        	messageProducer = ((QueueSession) session).createSender((javax.jms.Queue) queuePublisher);
        }
        logger.debug("newProducer(session) - " + nameString() + " created JMS Message Producer to destination [" + queuePublisher.toString() + "].");
        return messageProducer;
        
    }

    /**
     * Returns a new JMS Message Consumer.
     * @param session A JMS Session
     * @return A JMS Message Consumer
     * @throws JMSException
     */
    public javax.jms.MessageConsumer newConsumer(Session session) throws JMSException {
    	
    	logger.debug("newConsumer(session) - start");
        if (session == null) {
            logger.error("newConsumer(session) - " + nameString() + " cannot create JMS Consumer. Invalid session.");
            return null;
        } else {
        	logger.debug("newConsumer(session) - 'session' is null.");
        }
        if (!createDestConsumerIfAbsent(session)) {
            logger.error("newConsumer(session) - " + nameString() + " cannot create JMS Consumer. Destination queue is invalid.");
            return null;
        } else {
        	logger.debug("newConsumer(session) - Is not necessary to create a Destination (in Broker) because It exists.");
        }
        javax.jms.MessageConsumer messageConsumer;
        logger.debug("newConsumer(session) - Destination for JMS MessageConsumer : " + queueConsumer.toString());     
        if(isVersion11) {
        	messageConsumer = session.createConsumer(queueConsumer);
        } else {
        	messageConsumer = ((QueueSession) session).createReceiver((Queue) queueConsumer);
        }
        logger.debug("newConsumer(session) - " + nameString() + " created JMS MessageConsumer to destination [" + queueConsumer.toString() + "].");
        return messageConsumer;
        
    }

    /**
     * Creates a new JMS Message producer connection.
     *
     * @return true if new producer connection was successfully created, <br/>
     * false otherwise.
     */
    public boolean newWriteConnection() {
    	
    	logger.debug("newWriteConnection() - start");
        synchronized (producerLock) {
            if (producerConnection != null) {
                if (!closeConnection(producerConnection)) {
                    return false;
                }
            }
            try {
                producerConnection = newConnection();
            } catch (JMSException e) {
                logger.error("newWriteConnection() - " + nameString() 
                		+ " cannot create connection to the broker. \nError: " + e.getLocalizedMessage()
                        + ". \nInitial Context Factory: [" + parameters.get(NAMING_FACTORY_INITIAL) + "]; Provider URL:[" + parameters.get(PROVIDER_URL) + "]; Connection Factory:[" + connectionFactory + "].");
                producerConnection = null;
            }
        }
        return producerConnection != null;
    }

    /**
     * Closes the existing JMS message producer connection.
     *
     * @return true if the producer connection was closed without any error, <br/>
     * false otherwise.
     */
    public boolean closeWriteConnection() {
    	logger.debug("closeWriteConnection()");
        synchronized (producerLock) {
            if (producerConnection != null) {
                if (!closeConnection(producerConnection)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the existing JMS message producer connection.
     *
     * @return The current JMS Connection used to create message producers.
     */
    public Connection producerConnection() {
    	logger.debug("producerConnection()");
        return producerConnection;
    }

    /**
     * Closes the given JMS Connection.
     *
     * @param connection The JMS Connection to be closed.
     * @return true if the connection was successfully closed. false otherwise.
     */
    public boolean closeConnection(Connection connection) {
    	logger.debug("closeConnection()");
        try {
            connection.close();
            if (logger.isDebugEnabled()) {
                logger.debug(nameString() + " closed connection to JMS broker.");
            }
        } catch (JMSException e) {
            return false;
        }
        return true;
    }

    /**
     * Cleans up the JMS Connection and Session associated with a JMS client.
     *
     * @param connection  JMS Connection
     * @param session JMS Session associated with the given connection
     * @param error is this method called upon an error
     * @return {@code true} if the cleanup is successful. {@code false} otherwise.
     */
    public boolean cleanup(Connection connection, Session session, boolean error) {
    	logger.debug("cleanup(connection, session, error)");
        if (connection == null && error) {
            return true;
        }
        try {
            if (session != null) {
                session.close();
            }
        } catch (JMSException e) {
            return false;
        }
        try {
            if (connection != null && error) {
                connection.close();
            }
        } catch (JMSException e) {
            return false;
        }
        return true;
    }

    public org.apache.axis2.context.MessageContext newAxis2Mc() {
        return ((Axis2SynapseEnvironment) synapseEnvironment).getAxis2ConfigurationContext().createMessageContext();
    }

    public org.apache.synapse.MessageContext newSynapseMc(org.apache.axis2.context.MessageContext msgCtx) {
        SynapseConfiguration configuration = synapseEnvironment.getSynapseConfiguration();
        return new Axis2MessageContext(msgCtx, configuration, synapseEnvironment);
    }

    public void setParameters(Map<String, Object> parameters) {
    	logger.debug("setParameters(map) - start");
        if (parameters == null || parameters.isEmpty()) {
            throw new SynapseException("setParameters(map) - " + "Cannot initialize JMS Store [" + getName() + "]. Required parameters are not available.");
        }
        super.setParameters(parameters);
    }


    /**
     * TODO getDestinationPublisher() 
     * @param session
     * @return
     */
    private Destination getDestinationPublisher(Session session) {
    	
    	logger.debug("getDestinationPublisher() - start");
        Destination destPubl = queuePublisher;
        
        if (destPubl != null) {
        	logger.info("getDestinationPublisher() - Destination 'publisher' is not null. Returning back the existing queue [" + destPubl.toString() + "].");
            return destPubl;
        } else {
        	logger.info("getDestinationPublisher() - Destination 'publisher' is null.");
        }
        InitialContext newContext = null;
        logger.info("getDestinationPublisher() - JNDI destination 'publisher' name : " + destinationPublisher);
        try {
        	destPubl = lookup(context, javax.jms.Destination.class, destinationPublisher);
        } catch (NamingException e) {
            logger.debug("getDestinationPublisher() - " + nameString() + ". Could not lookup destinationPublisher [" + destinationPublisher + "]. Message: " + e.getLocalizedMessage());
            newContext = newContext();
        }
        try {
            if (newContext != null) {
            	destPubl = lookup(newContext, javax.jms.Destination.class, destinationPublisher);
             } else {
            	logger.info("getDestinationPublisher() - 'newContext' is null.");
            }
        } catch (Throwable t) {
            logger.info("getDestinationPublisher() - " + nameString() + ". Destination for Publisher [" + destinationPublisher + "] is not defined in JNDI context. Message:" + t.getLocalizedMessage());
        }
        if (destPubl == null && session != null) {
            try {
            	destPubl = session.createQueue(destinationPublisher);
                logger.debug("getDestinationPublisher() - " + nameString() + " created destination for publisher [" + destinationPublisher + "] from session object.");
            } catch (JMSException e) {
                logger.error("getDestinationPublisher() - " + nameString() + " could not create destination for publisher [" + destinationPublisher + "]. Error:" + e.getLocalizedMessage(), e);
                destPubl = null;
            }
        }
        if (destPubl == null && session == null) {
            logger.debug("getDestinationPublisher() - " + nameString() + ". Both destination and session are null." + " Could not create destination.");
        }
        synchronized (queueLock) {
        	queuePublisher = destPubl;
        }
        return destPubl;
        
    }

    /**
     * TODO getDestinationConsumer()
     * @param session
     * @return
     */
    private Destination getDestinationConsumer(Session session) {
    	
    	logger.debug("getDestinationConsumer() - start");
        Destination destConsumer = queueConsumer;
        
        if (destConsumer != null) {
        	logger.info("getDestinationConsumer() - Destination 'consumer' is not null. Return back queue [" + destConsumer.toString() + "].");
            return destConsumer;
        } else {
        	logger.info("getDestinationConsumer() - Destination 'consumer' is null.");
        }
        InitialContext newContext = null;
        logger.info("getDestinationConsumer() - JNDI destination 'consumer' name : " + destinationConsumer);
        try {
        	destConsumer = lookup(context, javax.jms.Destination.class, destinationConsumer);
        } catch (NamingException e) {
            logger.debug("getDestinationConsumer() - " + nameString() + ". Could not lookup destinationConsumer[" + destinationConsumer + "]. Message: " + e.getLocalizedMessage());
            newContext = newContext();
        }
        try {
            if (newContext != null) {
            	destConsumer = lookup(newContext, javax.jms.Destination.class, destinationConsumer);
             } else {
            	logger.info("getDestinationConsumer() - 'newContext' is null.");
            }
        } catch (Throwable t) {
            logger.info("getDestinationConsumer() - " + nameString() + ". Destination for Consumer [" + destinationConsumer + "] is not defined in JNDI context. Message:" + t.getLocalizedMessage());
        }
        if ( destConsumer == null && session != null) {
            try {
            	destConsumer = session.createQueue(destinationConsumer);
                logger.debug("getDestinationConsumer() - " + nameString() + " created destination for consumer [" + destinationConsumer + "] from session object.");
            } catch (JMSException e) {
                logger.error("getDestinationConsumer() - " + nameString() + " could not create destination for consumer [" + destinationConsumer + "]. Error:" + e.getLocalizedMessage(), e);
                destConsumer = null;
            }
        }
        if (destConsumer == null && session == null) {
            logger.debug("getDestinationConsumer() - " + nameString() + ". Both destination and session are null." + " Could not create destinationConsumer.");
        }
        synchronized (queueLock) {
            queueConsumer = destConsumer;
        }
        return destConsumer;
        
    }
    
    
    private InitialContext newContext() {
    	logger.debug("newContext() - start");
        Properties properties = new Properties();
        InitialContext newContext;
        Map env;
        try {
            env = context.getEnvironment();
            Object o = env.get(NAMING_FACTORY_INITIAL);
            if (o != null) {
                properties.put(NAMING_FACTORY_INITIAL, o);
            }
            o = env.get(CONNECTION_STRING);
            if (o != null) {
                properties.put(CONNECTION_STRING, o);
            }
            o = env.get(PROVIDER_URL);
            if (o != null) {
                properties.put(PROVIDER_URL, o);
            }
            properties.put(QUEUE_PREFIX + destinationPublisher, destinationPublisher);
            
            // TODO documentar
            properties.put(QUEUE_PREFIX + destinationConsumer, destinationConsumer);
            
            newContext = new InitialContext(properties);
        } catch (NamingException e) {
            logger.info("newContext() - " + nameString() + " could not create a new Context. Message: " + e.getLocalizedMessage());
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("newContext() - " + nameString() + " Created a new Context.");
        }
        return newContext;
    }

    private <T> T lookup(Context context, Class<T> clazz, String name) throws NamingException {
    	logger.debug("lookup() - start");
    	logger.debug("lookup() - clazz : " + clazz.getCanonicalName());
    	logger.debug("lookup() - name  : " + name);
    	
        if (context == null) {
            logger.error(nameString() + ". Cannot perform JNDI lookup. Invalid context.");
            return null;
        }
        if (name == null || "".equals(name)) {
            logger.error(nameString() + ". Cannot perform JNDI lookup. Invalid name.");
            return null;
        }
        Object object = context.lookup(name);
        try {
            return clazz.cast(object);
        } catch (ClassCastException e) {
            logger.error(nameString() + ". Cannot perform JNDI lookup for the name [" + name + "].", e);
            return null;
        }
    }

    private boolean destinationNonNull() {
    	logger.debug("destinationNonNull()");
        synchronized (queueLock) {
            //return queue != null;
        	return (queuePublisher != null) && (queueConsumer != null);
        }
    }

    private boolean createDestPublisherIfAbsent(Session session) {
    	logger.debug("createDestPublisherIfAbsent(session) - start");
        synchronized (queueLock) {
            return getDestinationPublisher(session) != null;
        }
    }

    private boolean createDestConsumerIfAbsent(Session session) {
    	logger.debug("createDestConsumerIfAbsent(session) - start");
        synchronized (queueLock) {
            return getDestinationConsumer(session) != null;
        }
    }
    
    
    private String nameString() {
    	//logger.debug("nameString() ~~> Store : " + getName());
        return "Store [" + getName() + "]";
    }
}
