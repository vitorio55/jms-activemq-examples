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

import com.examples.jms.models.Purchase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExampleTopicConsumerJson {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection("user", "user_pwd");
		connection.setClientID("json_consumer");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Topic topic = (Topic) context.lookup("topic_json_messages");
		
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
				ObjectMapper mapper = new ObjectMapper();
				Purchase purchase = null;
				try {
					String json = textMessage.getText();
					purchase = mapper.readValue(json, Purchase.class);
				} catch (JMSException | JsonProcessingException e) {
					e.printStackTrace();
				}
				System.out.println("Purchase converted from JSON. It's product code is:");
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
