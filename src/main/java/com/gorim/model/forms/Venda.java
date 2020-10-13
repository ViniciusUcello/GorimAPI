package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Venda {
	private String nomeAgr;
	private int idAgr;
	private String nomeEmp;
	private int idEmp;
	private boolean sucesso;
	private int idProduto;
	private String nomeProduto;
	private int quantidade;
	private String preco;
	private int idOrcamento;
	
	public Venda(
			@JsonProperty("nomeAgr") String nomeAgr,
			@JsonProperty("idAgr") int idAgr,
			@JsonProperty("nomeEmp") String nomeEmp,
			@JsonProperty("idEmp") int idEmp,
			@JsonProperty("sucesso") boolean sucesso,
			@JsonProperty("idProduto") int idProduto,
			@JsonProperty("nomeProduto") String nomeProduto,
			@JsonProperty("quantidade") int quantidade,
			@JsonProperty("preco") String preco,
			@JsonProperty("idOrcamento") int idOrcamento
	) {
		this.nomeAgr = nomeAgr;
		this.idAgr = idAgr;
		this.nomeEmp = nomeEmp;
		this.idEmp = idEmp;
		this.sucesso = sucesso;
		this.idProduto = idProduto;
		this.nomeProduto = nomeProduto;
		this.quantidade = quantidade;
		this.preco = preco;
		this.idOrcamento =  idOrcamento;
	}

	public int getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(int idProduto) {
		this.idProduto = idProduto;
	}

	public int getIdOrcamento() {
		return idOrcamento;
	}

	public void setIdOrcamento(int idOrcamento) {
		this.idOrcamento = idOrcamento;
	}

	public String getNomeAgr() {
		return nomeAgr;
	}

	public void setNomeAgr(String nomeAgr) {
		this.nomeAgr = nomeAgr;
	}

	public String getNomeEmp() {
		return nomeEmp;
	}

	public void setNomeEmp(String nomeEmp) {
		this.nomeEmp = nomeEmp;
	}

	public String getNomeProduto() {
		return nomeProduto;
	}

	public void setNomeProduto(String nomeProduto) {
		this.nomeProduto = nomeProduto;
	}

	public int getIdAgr() {
		return idAgr;
	}

	public void setIdAgr(int idAgr) {
		this.idAgr = idAgr;
	}

	public int getIdEmp() {
		return idEmp;
	}

	public void setIdEmp(int idEmp) {
		this.idEmp = idEmp;
	}

	public boolean isSucesso() {
		return sucesso;
	}

	public void setSucesso(boolean sucesso) {
		this.sucesso = sucesso;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public String getPreco() {
		return preco;
	}

	public void setPreco(String preco) {
		this.preco = preco;
	}
	
}
