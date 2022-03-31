package com.examples.jms.producer;

import java.io.StringWriter;
import java.util.Scanner;

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

public class ExampleTopicProducerXMLWithProperty {

	public static void main(String[] args) throws NamingException, JMSException {

		// This example is meant to be used with the ExampleTopicConsumerSelector
		// First run the consumer cited above chosing a value for the ebook property, then run the code here
		// Once you run this code, the program prompts you to chose a value for the ebook property
		// of the message to be sent
		// 
		// Possibilities:
		//
		// - Chose a value (true/false) in this program, then run ExampleTopicConsumerSelector and there
		//   you chose the SAME value, and you can see the message is consumed since the filter matches
		//
		// - Chose a value (true/false) in this program, then run ExampleTopicConsumerSelector and there
		//   you chose a DIFFERENT value, and you can see no message is received since the filter didn't
		//   match the property in the message produced

		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection("user", "user_pwd");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination topic = (Destination) context.lookup("topic_with_property_messages");
		MessageProducer producer = session.createProducer(topic); 

		Purchase purchase = new PurchaseFactory().generatePurchaseWithValues();

		StringWriter writer = new StringWriter();
		JAXB.marshal(purchase, writer);
		String xml = writer.toString();
		
		System.out.println("Sending the following XML message:");
		System.out.println(xml);

		Message message = session.createTextMessage(xml);

		// We add a property so the consumer can filter
		System.out.println("Set ebook property of messages to true? (y/n)");
		boolean answer = readYesNoFromTerminal();

		message.setBooleanProperty("ebook", answer);
		
		producer.send(message);

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
