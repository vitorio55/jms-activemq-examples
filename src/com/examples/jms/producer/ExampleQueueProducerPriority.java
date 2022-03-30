package com.examples.jms.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ExampleQueueProducerPriority {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection("user", "user_pwd");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination queue = (Destination) context.lookup("financial");
		MessageProducer producer = session.createProducer(queue); 

		final int LOWEST_PRIORITY = 0;
		final int HIGHEST_PRIORITY = 9;
		final int INFINITE_TIME_TO_LIVE = 0;

		// This messages are being sent from lowest to highest priority
		// If priority is enabled in ActiveMQ, once the consumer reads the messages we can verify
		// that they arrived reordered based on priority for the consumer
		
		for (int priority = LOWEST_PRIORITY; priority <= HIGHEST_PRIORITY; priority++) {
			System.out.println("Sending message with priority " + priority);
			Message message = session.createTextMessage("Message with Priority " + priority);
			producer.send(message, DeliveryMode.PERSISTENT, priority, INFINITE_TIME_TO_LIVE);
		}

		session.close();
		connection.close();
		context.close();
	}

}
