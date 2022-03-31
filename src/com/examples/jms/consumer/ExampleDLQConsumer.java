package com.examples.jms.consumer;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ExampleDLQConsumer {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		// These credentials are configured in the ActiveMQ's configuration file conf/activemq.xml
		Connection connection = factory.createConnection("user", "user_pwd");
		connection.setClientID("dlq_consumer");
		connection.start();

		boolean transacted = false;
		Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

		// DLQ stands for Dead Letter Queue
		// It's a special queue that holds messages that were unable to be delivered
		Destination topic = (Destination) context.lookup("DLQ");
		MessageConsumer consumer = session.createConsumer(topic);

		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				System.out.println(message);
			}
		});

		// Hold program running
		new Scanner(System.in).nextLine();
		System.out.println("Exiting.");

		session.close();
		connection.close();
		context.close();
	}

}
