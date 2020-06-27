package com.gorim.model.forms;

public class Transfer {
	private final int to;
	private final double quantity;
	
	public Transfer(int to, double quantity) {
		this.to = to;
		this.quantity = quantity;
	}

	public int getTo() {
		return to;
	}

	public double getQuantity() {
		return quantity;
	}
}
