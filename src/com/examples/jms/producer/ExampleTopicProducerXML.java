package com.examples.jms.producer;

import java.io.StringWriter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXB;

import com.examples.jms.models.Purchase;
import com.examples.jms.models.PurchaseFactory;

public class ExampleTopicProducerXML {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection("user", "user_pwd");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination topic = (Destination) context.lookup("topic_messages");
		MessageProducer producer = session.createProducer(topic); 
		
		Purchase purchase = new PurchaseFactory().generatePurchaseWithValues();

		StringWriter writer = new StringWriter();
		JAXB.marshal(purchase, writer);
		String xml = writer.toString();
		System.out.println(xml);

		Message message = session.createTextMessage(xml);
		producer.send(message);

		session.close();
		connection.close();
		context.close();
	}

}
