package com.gorim.model;

public class MundoModel {
	
	private int rodada;
	private int etapa;
	private double poluicaoMundo;
	private String idJogo;
	private double produtividadeMundo;
	
	public MundoModel(
			int rodada,
			int etapa,
			double poluicaoMundo,
			String idJogo,
			double produtividadeMundo
	) {
		this.rodada = rodada;
		this.etapa = etapa;
		this.poluicaoMundo = poluicaoMundo;
		this.idJogo = idJogo;
		this.produtividadeMundo = produtividadeMundo*100;
	}

	public String getIdJogo() {
		return idJogo;
	}

	public void setIdJogo(String idJogo) {
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
