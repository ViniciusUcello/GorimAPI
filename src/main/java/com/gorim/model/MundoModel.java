package com.gorim.model;

public class MundoModel {
	
	private int rodada;
	private int etapa;
	private double poluicaoMundo;
	private int idJogo;
	private double produtividadeMundo;
	private int quantidadeJogadores;
	
	public MundoModel(
			int rodada,
			int etapa,
			double poluicaoMundo,
			int idJogo,
			double produtividadeMundo,
			int quantidadeJogadores
	) {
		this.rodada = rodada;
		this.etapa = etapa;
		this.poluicaoMundo = poluicaoMundo*100;
		this.idJogo = idJogo;
		this.produtividadeMundo = produtividadeMundo*100;
		this.quantidadeJogadores = quantidadeJogadores;
	}

	public int getQuantidadejogadores() {
		return quantidadeJogadores;
	}

	public void setQuantidadejogadores(int quantidadejogadores) {
		this.quantidadeJogadores = quantidadejogadores;
	}

	public int getIdJogo() {
		return idJogo;
	}

	public void setIdJogo(int idJogo) {
		this.idJogo = idJogo;
	}

	public double getProdutividadeMundo() {
		return produtividadeMundo;
	}

	public void setProdutividadeMundo(double produtividadeMundo) {
		this.produtividadeMundo = produtividadeMundo;
	}

	public int getRodada() {
		return rodada;
	}

	public void setRodada(int rodada) {
		this.rodada = rodada;
	}

	public int getEtapa() {
		return etapa;
	}

	public void setEtapa(int etapa) {
		this.etapa = etapa;
	}

	public double getPoluicaoMundo() {
		return poluicaoMundo;
	}

	public void setPoluicaoMundo(double poluicaoMundo) {
		this.poluicaoMundo = poluicaoMundo;
	}
	
	
}
