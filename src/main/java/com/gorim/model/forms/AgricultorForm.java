package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AgricultorForm {
	
	private Parcela[] parcelas;
	private boolean pedirSeloVerde;
	
	public AgricultorForm(
			@JsonProperty("parcelas") Parcela[] parcelas,
			@JsonProperty("seloVerde") boolean pedirSeloVerde
	) {
		this.parcelas = parcelas;
		this.pedirSeloVerde = pedirSeloVerde;
	}
	
	public boolean getPedirSeloVerde() {
		return this.pedirSeloVerde;
	}
	
	public void setPedirSeloVerde(boolean pedirSeloVerde) {
		this.pedirSeloVerde = pedirSeloVerde;
	}
	
	public Parcela[] getParcelas() {
		return this.parcelas;
	}
	
	public void setParcelas(Parcela[] parcelas) {
		this.parcelas = parcelas;
	}
	
}
