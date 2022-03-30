package com.examples.jms.models;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PurchaseFactory {

	public Purchase generatePurchaseWithValues() {
		Purchase purchase = new Purchase(
			2459,
			composeDateFromString("22/12/2016"),
			composeDateFromString("23/12/2016"),
			new BigDecimal("145.98")
		);

		Item motoG = composeItem(23,"Moto G");
		Item motoX = composeItem(51,"Moto X");
		
		purchase.addItem(motoX);
		purchase.addItem(motoG);
		
		return purchase;

	}

	private Item composeItem(int id, String name) {
		return new Item(id, name);
	}

	private Calendar composeDateFromString(String dateString) {
		try {
			Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
}
