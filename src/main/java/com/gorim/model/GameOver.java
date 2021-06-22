package com.gorim.model;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GameOver {
	private double poluicaoMundo;
	private int rodada;
	private int etapa;
	private int idJogo;
	private List<ResumoJogador> resumoJogadores;
	
	public GameOver(double poluicaoMundo, int rodada, int etapa, int idJogo, List<ResumoJogador> resumoJogadores) {
		this.poluicaoMundo = poluicaoMundo;
		this.rodada = rodada;
		this.etapa = etapa;
		this.idJogo = idJogo;
		this.resumoJogadores = resumoJogadores;
	}

	public double getPoluicaoMundo() {
		return this.poluicaoMundo;
	}

	public void setPoluicaoMundo(double poluicaoMundo) {
		this.poluicaoMundo = poluicaoMundo;
	}

	public int getRodada() {
		return this.rodada;
	}

	public void setRodada(int rodada) {
		this.rodada = rodada;
	}

	public int getEtapa() {
		return this.etapa;
	}

	public void setEtapa(int etapa) {
		this.etapa = etapa;
	}
	
	public int getIdJogo() {
		return this.idJogo;
	}
	
	public void setIdJogo(int idJogo) {
		this.idJogo = idJogo;
	}

	public List<ResumoJogador> getResumoJogadores() {
		return this.resumoJogadores;
	}

	public void setResumoJogadores(List<ResumoJogador> resumoJogadores) {
		this.resumoJogadores = resumoJogadores;
	}
	
	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject json = new JSONObject();
		
		json.put("poluicaoMundo", this.poluicaoMundo);
		json.put("rodada", this.rodada);
		json.put("etapa", this.etapa);
		
		JSONArray resumoJogadores = new JSONArray();
		for(ResumoJogador res : this.resumoJogadores)
			resumoJogadores.add(res.toJSONObject());
		
		json.put("resumoJogadores", resumoJogadores);
		
		return json.toString();
	}
	
}
