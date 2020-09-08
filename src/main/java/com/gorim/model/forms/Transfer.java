package com.gorim.model.forms;

public class Transfer {
	private final int destinatario;
	private final double valor;
	
	public Transfer(int destinatario, double valor) {
		this.destinatario = destinatario;
		this.valor = valor;
	}

	public int getDestinatario() {
		return destinatario;
	}

	public double getValor() {
		return valor;
	}
}
