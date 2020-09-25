package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Venda {
	private int idAgr;
	private int idEmp;
	private boolean sucesso;
	private int idProduto;
	private int quantidade;
	private String preco;
	
	public Venda(
			@JsonProperty("idAgr") int idAgr,
			@JsonProperty("idEmp") int idEmp,
			@JsonProperty("sucesso") boolean sucesso,
			@JsonProperty("idProduto") int idProduto,
			@JsonProperty("quantidade") int quantidade,
			@JsonProperty("preco") String preco
	) {
		this.idAgr = idAgr;
		this.idEmp = idEmp;
		this.sucesso = sucesso;
		this.idProduto = idProduto;
		this.quantidade = quantidade;
		this.preco = preco;
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

	public int getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(int idProduto) {
		this.idProduto = idProduto;
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
