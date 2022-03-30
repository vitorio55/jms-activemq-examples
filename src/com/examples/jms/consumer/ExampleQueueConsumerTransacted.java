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

public class ExampleQueueConsumerTransacted {

	private static int messageNumber = 0;
	
	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection("user", "user_pwd");
		connection.start();

		boolean transacted = true;
		Session session = connection.createSession(transacted, Session.SESSION_TRANSACTED);

		Destination queue = (Destination) context.lookup("financial");
		MessageConsumer consumer = session.createConsumer(queue); 
		
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {			
				TextMessage textMessage = (TextMessage) message;
				try {
					System.out.println(textMessage.getText());
					ExampleQueueConsumerTransacted.messageNumber++;

					if (ExampleQueueConsumerTransacted.messageNumber % 5 == 0) {
						System.out.println("Acknowledging every fifth message read...");
						session.commit();
					}

					// If we want it is possible to do a rollback
					// session.rollback();
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
