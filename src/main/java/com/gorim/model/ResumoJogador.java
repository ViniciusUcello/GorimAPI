package com.gorim.model;

import org.json.simple.JSONObject;

public class ResumoJogador {
	private String nome;
	private double saldo;
	private String cidade;
	private String papel;
	
	
	
	public ResumoJogador(String nome, double saldo, String cidade, String papel) {
		this.nome = nome;
		this.saldo = saldo;
		this.cidade = cidade;
		this.papel = papel;
	}
	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public double getSaldo() {
		return this.saldo;
	}
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	public String getCidade() {
		return this.cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getPapel() {
		return this.papel;
	}
	public void setPapel(String papel) {
		this.papel = papel;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		
		json.put("nome", this.nome);
		json.put("saldo", this.saldo);
		json.put("cidade", this.cidade);
		json.put("papel", this.papel);
		
		return json;
	}
	
}
