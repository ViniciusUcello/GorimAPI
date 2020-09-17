package com.gorim.model;

public class MundoModel {
	private int rodada;
	private int etapa;
	private double poluicaoMundo;
	
	public MundoModel(
			int rodada,
			int etapa,
			double poluicaoMundo
	) {
		this.rodada = rodada;
		this.etapa = etapa;
		this.poluicaoMundo = poluicaoMundo;
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
