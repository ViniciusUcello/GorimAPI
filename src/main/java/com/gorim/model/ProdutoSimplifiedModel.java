package com.gorim.model;

public class ProdutoSimplifiedModel {
	private String tipo;
	private double custo;
	private String setor;
	
	public ProdutoSimplifiedModel(String tipo, double custo, String setor) {
		this.tipo = tipo;
		this.custo = custo;
		this.setor = setor;
	}

	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	public void setCusto(double custo) {
		this.custo = custo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public double getCusto() {
		return custo;
	}

	public void setCusto(int custo) {
		this.custo = custo;
	}
	
}
