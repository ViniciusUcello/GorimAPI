package com.gorim.enums;

/**
 * Código que identifica o status do jogo
 */
public enum GameStatus {
	
	INICIO_ETAPA(0),
	TODOS_JOGADORES_NA_ETAPA(INICIO_ETAPA.status + 1),
	TODOS_JOGADORES_ACABARAM_ETAPA(TODOS_JOGADORES_NA_ETAPA.status + 1),
	MESTRE_TERMINOU_ETAPA(TODOS_JOGADORES_ACABARAM_ETAPA.status + 1),
	FIM_JOGO(MESTRE_TERMINOU_ETAPA.status + 1);
	
	public final int status;
	
	GameStatus(int status){
		this.status = status;
	}
}
