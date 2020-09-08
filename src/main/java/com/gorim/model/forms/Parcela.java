package com.gorim.model.forms;

public class Parcela {
	private final int semente;
	private final int sementePreco;
	private final int fertilizante;
	private final int fertilizantePreco;
	private final int agrotoxicoMaquina;
	private final int agrotoxicoMaquinaPreco;
	private final boolean pulverizador;
	private final int pulverizadorPreco;
	
	public Parcela(
			int semente,
			int sementePreco,
			int fertilizante,
			int fertilizantePreco,
			int agrotoxicoMaquina,
			int agrotoxicoMaquinaPreco,
			boolean pulverizador,
			int pulverizadorPreco
	) {
		this.semente = semente;
		this.sementePreco = sementePreco;
		this.fertilizante = fertilizante;
		this.fertilizantePreco = fertilizantePreco;
		this.agrotoxicoMaquina = agrotoxicoMaquina;
		this.agrotoxicoMaquinaPreco = agrotoxicoMaquinaPreco;
		this.pulverizador = pulverizador;
		this.pulverizadorPreco = pulverizadorPreco;
	}

	public int getSemente() {
		return semente;
	}

	public int getSementePreco() {
		return sementePreco;
	}

	public int getFertilizante() {
		return fertilizante;
	}

	public int getFetilizantePreco() {
		return fertilizantePreco;
	}

	public int getAgrotoxicoMaquina() {
		return agrotoxicoMaquina;
	}

	public int getAgrotoxicoMaquinaPreco() {
		return agrotoxicoMaquinaPreco;
	}

	public boolean isPulverizador() {
		return pulverizador;
	}
	
	public int getPulverizadorPreco() {
		return pulverizadorPreco;
	}
	
}
