package com.examples.jms.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.examples.jms.models.Purchase;

public class ExampleTopicConsumerObject {

	public static void main(String[] args) throws NamingException, JMSException {

		// ---------------------------------------------------------------------------------------
		// WARNING!!
		// The approach of passing objects in messages is not advised since malicious objects
		// might be passed which represents a security problem
		// ---------------------------------------------------------------------------------------

		InitialContext context = new InitialContext();
		ActiveMQConnectionFactory factory = (ActiveMQConnectionFactory) context.lookup("ConnectionFactory");

		// Add trusted object packages
		List<String> newPackages = new ArrayList<>(factory.getTrustedPackages());
		newPackages.add("com.examples.jms.models");
		newPackages.add("java.util");
		newPackages.add("sun.util.calendar");
		newPackages.add("java.math");
		factory.setTrustedPackages(newPackages);

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
				ObjectMessage objectMessage = (ObjectMessage) message;
				Purchase purchase = null;
				try {
					purchase = (Purchase) objectMessage.getObject();
				} catch (JMSException e) {
					e.printStackTrace();
				}
				System.out.println("Purchase received. It's product code is:");
				System.out.println(purchase.getProductCode());
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
