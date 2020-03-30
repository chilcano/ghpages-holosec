package com.chakray.wso2.samples;

import java.io.InputStream;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class HelloRabbitMQ
{

    public HelloRabbitMQ()
    {
    }

    public static void main(String[] args)
    {
        HelloRabbitMQ hello = new HelloRabbitMQ();
        hello.runTest();
    }

    private void runTest()
    {
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream("hello_rabbitmq_jndi.properties"))
        {
            //System.setProperty("qpid.amqp.version", "0-91");
            //System.setProperty("qpid.dest_syntax", "BURL");
            System.setProperty("IMMEDIATE_PREFETCH", "true");
            System.setProperty("qpid.declare_exchanges", "false");
            System.setProperty("qpid.declare_queues", "false");

            Properties properties = new Properties();
            properties.load(resourceAsStream);

            Context context = new InitialContext(properties);

            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("myRabbitMQConnectionFactory1");
            //Connection connection = connectionFactory.createConnection("admin","JRq7b1Ct");
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destinationPublisher = (Destination) context.lookup("myJndiDestQueuePublisher1");
            Destination destinationConsumer = (Destination) context.lookup("myJndiDestQueueConsumer1");

            MessageProducer messageProducer = session.createProducer(destinationPublisher);
            MessageConsumer messageConsumer = session.createConsumer(destinationConsumer);

            TextMessage message = session.createTextMessage("Hello RabbitMQ 3.3.5 !!");
            messageProducer.setDeliveryMode(Session.AUTO_ACKNOWLEDGE);
            messageProducer.send(message);
            messageProducer.close();

            Thread.sleep(10000);  // 10 secs

            message = (TextMessage)messageConsumer.receive();
            System.out.println(message.getText());

            session.close();
            connection.close();
            context.close();
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }
}
