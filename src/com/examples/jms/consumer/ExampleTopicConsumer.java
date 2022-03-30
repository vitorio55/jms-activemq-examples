package com.examples.jms.consumer;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ExampleTopicConsumer {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection("user", "user_pwd");
		connection.setClientID("commercial");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Topic topic = (Topic) context.lookup("store");
		
		// IMPORTANT!!
		//
		// createDurableSubscriber() identifies the consumer in ActiveMQ.
		// This way it is possible for this subscriber to receive messages of the topic it subscribes to
		// even if the consumer is offline.
		// 
		// Once the consumer comes online, ActiveMQ delivers all the messages of the subscribed topic that
		// the consumer should have received.
		//
		// However, the order of execution is important for this to work. It should be:
		// 1 - The consumer must run to register itself as a durable subscriber (all messages sent before this
		//     will not be received)
		// 2 - The consumer can be stopped, since it is now registered as a durable consumer
		// 3 - The producer now can produce message(s)
		// 4 - The consumer can now be stared. It will start receiving messages that were produced for the topic
		//     while the consumer was offline
		MessageConsumer consumer = session.createDurableSubscriber(topic, "my_subscription");

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
