package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SeloVerde {
	private int idAgr;
	private int[] parcelas;
	private boolean atribuir;
	
	public SeloVerde(
			@JsonProperty("idAgr") int idAgr,
			@JsonProperty("parcelas") int[] parcelas,
			@JsonProperty("atribuir") boolean atribuir
	) {
		this.idAgr = idAgr;
		this.parcelas = parcelas;
		this.atribuir = atribuir;
	}
	
	public int getIdAgr() {
		return idAgr;
	}
	
	public void setIdAgr(int idAgr) {
		this.idAgr = idAgr;
	}
	
	public int[] getParcelas() {
		return parcelas;
	}
	
	public void setParcelas(int[] parcelas) {
		this.parcelas = parcelas;
	}
	
	public boolean isAtribuir() {
		return atribuir;
	}
	
	public void setAtribuir(boolean atribuir) {
		this.atribuir = atribuir;
	}
	
	
}
