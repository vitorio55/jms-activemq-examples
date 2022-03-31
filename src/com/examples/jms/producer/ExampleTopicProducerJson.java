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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExampleTopicProducerJson {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection("user", "user_pwd");
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination topic = (Destination) context.lookup("topic_json_messages");
		MessageProducer producer = session.createProducer(topic); 
		
		Purchase purchase = new PurchaseFactory().generatePurchaseWithValues();

		String purchaseJson = convertPurchaseToJSONString(purchase);
		Message message = session.createTextMessage(purchaseJson);
		producer.send(message);

		session.close();
		connection.close();
		context.close();
	}
		     
	 private static String convertPurchaseToJSONString(Purchase purchase) {
         // Using Jackson API              
         ObjectMapper mapper = new ObjectMapper();
         String json = null;
         try {
             json = mapper.writeValueAsString(purchase);
             System.out.println("ResultingJSONstring = " + json);
         } catch (JsonProcessingException e) {
             e.printStackTrace();
         }
         return json;
	 }

}
