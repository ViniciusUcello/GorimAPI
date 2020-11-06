package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Multa {
	private int idPessoa;
	private int tipo;
	
	public Multa(
			@JsonProperty("idPessoa") int idPessoa,
			@JsonProperty("tipo") int tipo
	) {
		this.idPessoa = idPessoa;
		this.tipo = tipo;
	}

	public int getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(int idPessoa) {
		this.idPessoa = idPessoa;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
}
