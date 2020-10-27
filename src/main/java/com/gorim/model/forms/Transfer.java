package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transfer {
	private int remetente;
	private double quantia;
	private int destinatario;
	
	public Transfer(
		@JsonProperty("destinatario") int destinatario,
		@JsonProperty("quantia") double quantia,
		@JsonProperty("remetente") int remetente
	) {
		this.remetente = remetente;
		this.quantia = quantia;
		this.destinatario = destinatario;
	}

	public int getDestinatario() {
		return this.destinatario;
	}

	public double getQuantia() {
		return this.quantia;
	}

	public int getRemetente() {
		return this.remetente;
	}
	
	public void setRemetente(int remetente) {
		this.remetente = remetente;
	}

	public void setQuantia(double quantia) {
		this.quantia = quantia;
	}

	public void setDestinatario(int destinatario) {
		this.destinatario = destinatario;
	}
}
