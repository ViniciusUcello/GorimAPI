package com.gorim.model.forms;

//import java.util.ArrayList;
import java.util.List;

//import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AgricultorForm {
	private final int id;
	/*
	 * private final Parcela p1; private final Parcela p2; private final Parcela p3;
	 * private final Parcela p4; private final Parcela p5; private final Parcela p6;
	 */

	private final List<Transfer> transferencias;
	private final List<PedidoFiscal> pedidosFiscal;
	
	public AgricultorForm(
			@JsonProperty("id") int id,
			/*
			 * @JsonProperty("p1") Parcela p1,
			 * 
			 * @JsonProperty("p2") Parcela p2,
			 * 
			 * @JsonProperty("p3") Parcela p3,
			 * 
			 * @JsonProperty("p4") Parcela p4,
			 * 
			 * @JsonProperty("p5") Parcela p5,
			 * 
			 * @JsonProperty("p6") Parcela p6,
			 */
			@JsonProperty("transferencias") List<Transfer> transferencias,
			@JsonProperty("pedidos") List<PedidoFiscal> pedidos
	) {
		this.id = id;
		/*
		 * this.p1 = p1; this.p2 = p2; this.p3 = p3; this.p4 = p4; this.p5 = p5; this.p6
		 * = p6;
		 */
		this.transferencias = transferencias;
		this.pedidosFiscal = pedidos;
	}

	public int getId() {
		return id;
	}

	/*
	 * public Parcela getP1() { return p1; }
	 * 
	 * public Parcela getP2() { return p2; }
	 * 
	 * public Parcela getP3() { return p3; }
	 * 
	 * public Parcela getP4() { return p4; }
	 * 
	 * public Parcela getP5() { return p5; }
	 * 
	 * public Parcela getP6() { return p6; }
	 */

	public List<Transfer> getTranferencias() {
		return transferencias;
	}

	public List<PedidoFiscal> getPedidosFiscal() {
		return pedidosFiscal;
	}
	
	public boolean temTransferencias() {
		if(this.transferencias.isEmpty()) return false;
		return true;
	}
	
	public boolean temPedidos() {
		if(this.pedidosFiscal.isEmpty()) return false;
		return true;
	}
	
}
