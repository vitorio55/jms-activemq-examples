package com.examples.jms.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

//JAX-B
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Purchase implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer productCode;
	private Calendar date;
	private Calendar paymentDate;
	private BigDecimal totalPrice;
	
	@XmlElementWrapper(name="items")
	@XmlElement(name="item")
	private Set<Item> items = new LinkedHashSet<>();
	
	Purchase() {
	}

	public Purchase(Integer productCode, Calendar date, Calendar paymentDate, BigDecimal totalPrice) {
		this.productCode = productCode;
		this.date = date;
		this.paymentDate = paymentDate;
		this.totalPrice = totalPrice;
	}

	public void addItem(Item item) {
		this.items.add(item);
	}

	public Integer getProductCode() {
		return productCode;
	}

	public Calendar getDate() {
		return date;
	}

	public Calendar getPaymentDate() {
		return paymentDate;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public Set<Item> getItems() {
		return Collections.unmodifiableSet(this.items);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Purchase other = (Purchase) obj;
		if (productCode == null) {
			if (other.productCode != null)
				return false;
		} else if (!productCode.equals(other.productCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Purchase [productCode=" + productCode + ", date=" + date + ", paymentDate=" + paymentDate
				+ ", totalPrice=" + totalPrice + ", items=" + items + "]";
	}
	
}
