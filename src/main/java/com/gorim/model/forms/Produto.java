package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Produto {
	private int id;
	private int preco;
	
	public Produto(
		@JsonProperty("id") int id,
		@JsonProperty("preco") int preco
	) {
		this.id = id;
		this.preco = preco;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setPreco(int preco) {
		this.preco = preco;
	}
	
	public int getPreco() {
		return this.preco;
	}

	@Override
	public String toString() {
		return "id=" + id + ", preco=" + preco;
	}
	
}
