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

public class ExampleTopicConsumerSelector {

	public static void main(String[] args) throws NamingException, JMSException {

		// This example is meant to be used with the ExampleTopicProducerXMLWithProperty
		// Run the producer code and chose an ebook property value when prompted, then run the code here
		// 
		// Possibilities:
		//
		// - Chose a value (true/false) in this program that is the SAME value chosen for the
		//   ExampleTopicProducerXMLWithProperty, and you can see the message is consumed and its content is displayed
		//
		// - Chose a value (true/false) in this program that is DIFFERENT from the chosen for the
		//   ExampleTopicProducerXMLWithProperty, and you can see that no message is received since the
		//   filter didn't match the property in the message produced

		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection("user", "user_pwd");
		connection.setClientID("topic_consumer_selector");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		Topic topic = (Topic) context.lookup("topic_with_property_messages");

		System.out.println("Filter for ebook messages? (y/n)");
		boolean answer = readYesNoFromTerminal();

		// Add a selector for message filtering
		String selectorQuery = "ebook is null OR ebook=" + answer;

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
		MessageConsumer consumer = session.createDurableSubscriber(topic, "my_selector_subscription", selectorQuery, false);

		System.out.println("Awaiting messages...\n");
		
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
	
	private static boolean readYesNoFromTerminal() {
		Scanner scanner = new Scanner(System.in);
		String val = scanner.next();

		if (val.equalsIgnoreCase("y")||val.equalsIgnoreCase("yes")) {
		    return true;
		} else if (val.equalsIgnoreCase("n")||val.equalsIgnoreCase("no")) {
		    return false;
		} else { 
			System.out.println("Invalid character");
			return false;
		}
	}
}
