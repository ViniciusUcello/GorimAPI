package com.gorim.model;

public class MundoModel {
	
	private int rodada;
	private int etapa;
	private double poluicaoMundo;
	private int idJogo;
	private double produtividadeMundo;
	private int quantidadeJogadores;
	private String[] eleitos;
	
	public MundoModel(
			int rodada,
			int etapa,
			double poluicaoMundo,
			int idJogo,
			double produtividadeMundo,
			int quantidadeJogadores,
			String[] eleitos
	) {
		this.rodada = rodada;
		this.etapa = etapa;
		this.poluicaoMundo = poluicaoMundo*100;
		this.idJogo = idJogo;
		this.produtividadeMundo = produtividadeMundo*100;
		this.quantidadeJogadores = quantidadeJogadores;
		this.eleitos = eleitos;
	}

	public int getQuantidadeJogadores() {
		return quantidadeJogadores;
	}

	public void setQuantidadejogadores(int quantidadeJogadores) {
		this.quantidadeJogadores = quantidadeJogadores;
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

	public String[] getEleitos() {
		return this.eleitos;
	}

	public void setEleitos(String[] eleitos) {
		this.eleitos = eleitos;
	}
	
	
}
