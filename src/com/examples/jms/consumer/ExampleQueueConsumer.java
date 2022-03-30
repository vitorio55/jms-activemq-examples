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
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ExampleQueueConsumer {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		// These credentials are configured in the ActiveMQ's configuration file conf/activemq.xml
		Connection connection = factory.createConnection("user", "user_pwd");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination queue = (Destination) context.lookup("financial");
		MessageConsumer consumer = session.createConsumer(queue); 

		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {			
				TextMessage textMessage = (TextMessage) message;
				try {
					System.out.println(textMessage.getText());
				} catch (JMSException e) {
					e.printStackTrace();
				}
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
