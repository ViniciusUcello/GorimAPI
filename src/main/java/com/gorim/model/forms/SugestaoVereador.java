package com.gorim.model.forms;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SugestaoVereador {
	private Imposto imposto;
	private String acaoAmbiental;
	private int tipoSugestao;
	private boolean aceito;
	private int idSugestao;

	public SugestaoVereador(@JsonProperty("imposto") Imposto imposto,
			@JsonProperty("acaoAmbiental") String acaoAmbiental,
			@JsonProperty("tipoSugestao") int tipoSugestao,
			@JsonProperty("aceito") boolean aceito,
			@JsonProperty("idSugestao") int idSugestao)
	{
		this.imposto = imposto;
		this.acaoAmbiental = acaoAmbiental;
		this.tipoSugestao = tipoSugestao;
		this.aceito = aceito;
		this.idSugestao = idSugestao;
	}

	public Imposto getImposto() {
		return imposto;
	}

	public void setImposto(Imposto imposto) {
		this.imposto = imposto;
	}

	public String getAcaoAmbiental() {
		return acaoAmbiental;
	}

	public void setIdAcaoAmbiental(String acaoAmbiental) {
		this.acaoAmbiental = acaoAmbiental;
	}

	public int getTipoSugestao() {
		return tipoSugestao;
	}

	public void setTipoSugestao(int tipoSugestao) {
		this.tipoSugestao = tipoSugestao;
	}

	public boolean isAceito() {
		return aceito;
	}

	public void setAceito(boolean aceito) {
		this.aceito = aceito;
	}

	public int getIdSugestao() {
		return idSugestao;
	}

	public void setIdSugestao(int idSugestao) {
		this.idSugestao = idSugestao;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject sugestaoJSON = new JSONObject();
		
		sugestaoJSON.put("imposto", this.imposto);
		sugestaoJSON.put("acaoAmbiental", this.acaoAmbiental);
		sugestaoJSON.put("tipoSugestao", this.tipoSugestao);
		sugestaoJSON.put("aceito", this.aceito);
		sugestaoJSON.put("idSugestao", this.idSugestao);
		
		return sugestaoJSON;
	}
	
	@Override
	public String toString() {
		return "[idSugestao=" + this.idSugestao + "; tipoSugestao=" + this.tipoSugestao + "; imposto=" + this.imposto.toString() + "; acaoAmbiental=" + this.acaoAmbiental + "; aceito=" + this.aceito + "]";
	}
	
}
