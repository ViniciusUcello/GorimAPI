package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MestreForm {
	private final int quantidadeJogadores;

	public MestreForm(
			@JsonProperty("quantidadeJogadores") int quantidadeJogadores
	) {
		this.quantidadeJogadores = quantidadeJogadores;
	}

	public int getQuantidadeJogadores() {
		return quantidadeJogadores;
	}
	
	@Override
	public String toString() {
		return "[quantidadeJogadores=" + this.quantidadeJogadores + "]";
	}
	
}
