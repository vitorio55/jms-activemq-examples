package com.examples.jms.producer;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ExampleQueueProducer {

	public static void main(String[] args) throws NamingException, JMSException {
		// Another way of define MQ configuration using properties
		Properties properties = new Properties();
		properties.setProperty("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		properties.setProperty("java.naming.provider.url", "tcp://localhost:61616");
		properties.setProperty("queue.financial", "some_queue_category.financial");
		InitialContext context = new InitialContext(properties);

		// If we simply initialize the InitialContext the jndi file is used instead 
		// InitialContext context = new InitialContext();

		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		Connection connection = factory.createConnection("user", "user_pwd");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination queue = (Destination) context.lookup("queue_messages");
		MessageProducer producer = session.createProducer(queue); 

		for (int i = 0; i < 100; i++) {
			Message message = session.createTextMessage("<item><id>" + i + "</id><item>");
			producer.send(message);
		}

		session.close();
		connection.close();
		context.close();
	}

}
