package com.examples.jms.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.examples.jms.models.Purchase;
import com.examples.jms.models.PurchaseFactory;

public class ExampleTopicProducerObject {

	public static void main(String[] args) throws NamingException, JMSException {

		// ---------------------------------------------------------------------------------------
		// WARNING!!
		// The approach of passing objects in messages is not advised since malicious objects
		// might be passed which represents a security problem
		// ---------------------------------------------------------------------------------------

		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection("user", "user_pwd");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination topic = (Destination) context.lookup("store");
		MessageProducer producer = session.createProducer(topic); 
		
		// We send an object directly. In this case it's a Purchase.
		Purchase purchase = new PurchaseFactory().generatePurchaseWithValues();

		Message message = session.createObjectMessage(purchase);
		producer.send(message);
		
		session.close();
		connection.close();
		context.close();
	}

}
