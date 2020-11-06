package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Imposto {
	private int tipo;
	private String taxa;
	public Imposto(
			@JsonProperty("tipo") int tipo,
			@JsonProperty("taxa") String taxa
	) {
		this.tipo = tipo;
		this.taxa = taxa;
	}
	
	public int getTipo() {
		return this.tipo;
	}
	
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	public String getTaxa() {
		return this.taxa;
	}
	
	public void setTaxa(String taxa) {
		this.taxa = taxa;
	}
}
