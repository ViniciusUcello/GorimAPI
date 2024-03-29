package com.gorim.model.forms;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrefeitoForm {
	private Imposto[] impostos;
	private int[] idAcoesAmbientais;
	
	public PrefeitoForm(
			@JsonProperty("impostos") Imposto[] impostos,
			@JsonProperty("idAcoesAmbientais") int[] idAcoesAmbientais
	) {
		this.impostos = impostos;
		this.idAcoesAmbientais = idAcoesAmbientais;
	}

	public Imposto[] getImpostos() {
		return this.impostos;
	}

	public void setImpostos(Imposto[] impostos) {
		this.impostos = impostos;
	}

	public int[] getIdAcoesAmbientais() {
		return this.idAcoesAmbientais;
	}

	public void setIdAcoesAmbientais(int[] idAcoesAmbientais) {
		this.idAcoesAmbientais = idAcoesAmbientais;
	}
	
	@Override
	public String toString() {
		return "[impostos=" + Arrays.toString(this.impostos) + "; idAcoesAmbientais=" + Arrays.toString(this.idAcoesAmbientais) + "]";
	}
	
	
}
