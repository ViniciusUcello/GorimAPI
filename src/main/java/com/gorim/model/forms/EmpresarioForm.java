package com.gorim.model.forms;

//import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmpresarioForm {
	private final int id;
	private final List<Transfer> transfers;
	
	public EmpresarioForm(
			@JsonProperty("id") int id,
			@JsonProperty("transfers") List<Transfer> transfers) {
		this.id = id;
		this.transfers = transfers;
	}

	public int getId() {
		return id;
	}

	public List<Transfer> getTransfers() {
		return transfers;
	}
	
	public boolean hasTransfers() {
		if(this.transfers.isEmpty()) return false;
		return true;
	}
	
}
