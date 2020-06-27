package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmpresarioSellFormParcel {
	private final int idProd;
	private final int idAgr;
	private final int numParcela;
	private final int precoProd;
	
	public EmpresarioSellFormParcel(
			@JsonProperty("idProd") int idProd,
			@JsonProperty("idAgr") int idAgr,
			@JsonProperty("numParcela") int numParcela,
			@JsonProperty("precoProd") int precoProd
			) {
		this.idProd = idProd;
		this.idAgr = idAgr;
		this.numParcela = numParcela;
		this.precoProd = precoProd;
	}

	public int getIdProd() {
		return idProd;
	}

	public int getIdAgr() {
		return idAgr;
	}

	public int getNumParcela() {
		return numParcela;
	}

	public int getPrecoProd() {
		return precoProd;
	}
	
	
}
