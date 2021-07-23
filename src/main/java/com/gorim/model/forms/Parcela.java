package com.gorim.model.forms;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Parcela {
	
	private Produto[] produtos;
	
	public Parcela(
			@JsonProperty("produtos") Produto[] produtos
	) {
		this.produtos = produtos;
	}

	public Produto[] getProdutos(){
		return this.produtos;
	}
	
	public void setProdutos(Produto[] produtos) {
		this.produtos = produtos;
	}

	@Override
	public String toString() {
		return "produtos=[" + Arrays.toString(produtos) + "]";
	}
	
}
