package com.gorim.model;

public class PessoaModel {
	private int id;
	private String nome;
	private String nomeCurto;
	
	public PessoaModel(int id, String nome, String nomeCurto) {
		this.id = id;
		this.nome = nome;
		this.nomeCurto = nomeCurto;
	}

	public String getNomeCurto() {
		return this.nomeCurto;
	}

	public void setNomeCurto(String nomeCurto) {
		this.nomeCurto = nomeCurto;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
