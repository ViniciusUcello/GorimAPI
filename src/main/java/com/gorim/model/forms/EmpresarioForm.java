package com.gorim.model.forms;

//import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmpresarioForm {
	private final int id;
	private final List<Transfer> transferencias;
	
	public EmpresarioForm(
			@JsonProperty("id") int id,
			@JsonProperty("transferencias") List<Transfer> transferencias) {
		this.id = id;
		this.transferencias = transferencias;
	}

	public int getId() {
		return id;
	}

	public List<Transfer> getTransferencias() {
		return transferencias;
	}
	
	public boolean temTransferencias() {
		if(this.transferencias.isEmpty()) return false;
		return true;
	}
	
}
