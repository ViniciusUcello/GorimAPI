package com.gorim.enums;

public enum EventCodes {
	GAME_STATUS(0), 			  								// "message": {"status": GameStatus, "rodada": rodada, "etapa": etapa}
	PESSOA_COMECOU_JOGADA(GAME_STATUS.code + 1),				// "message": {"idPessoa": idPessoa, "nomePessoa": nomePessoa, "etapa": etapa}
	PESSOA_FINALIZOU_JOGADA(PESSOA_COMECOU_JOGADA.code + 1), 	// "message": {"idPessoa": idPessoa, "nomePessoa": nomePessoa, "etapa": etapa}
	ORCAMENTO(PESSOA_FINALIZOU_JOGADA.code + 1), 	  			// "message" : {} as Orcamento
	ORCAMENTO_RESPOSTA(ORCAMENTO.code + 1),      	  			// "message" : {} as Orcamento
	SUGESTAO(ORCAMENTO_RESPOSTA.code + 1), 			  			// "message" : {} as Sugestao
	SUGESTAO_RESPOSTA(SUGESTAO.code + 1); 			  			// "message" : {} as Sugestao
	
	
	public final int code;
	
	EventCodes(int code){
		this.code = code;
	}
}
