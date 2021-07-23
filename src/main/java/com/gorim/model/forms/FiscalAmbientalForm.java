package com.gorim.model.forms;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FiscalAmbientalForm {
	private Multa[] multas;
	private SeloVerde[] selosVerde;
	
	public FiscalAmbientalForm(
			@JsonProperty("multas") Multa[] multas,
			@JsonProperty("selosVerde") SeloVerde[] selosVerde
	) {
		super();
		this.multas = multas;
		this.selosVerde = selosVerde;
	}

	public Multa[] getMultas() {
		return multas;
	}

	public void setMultas(Multa[] multas) {
		this.multas = multas;
	}

	public SeloVerde[] getSelosVerde() {
		return selosVerde;
	}

	public void setSelosVerde(SeloVerde[] selosVerde) {
		this.selosVerde = selosVerde;
	}
	
	@Override
	public String toString() {
		return "FiscalAmbientalForm [multas=" + Arrays.toString(this.multas) + "; selosVerde=" + Arrays.toString(this.selosVerde) + "]";
	}
}
