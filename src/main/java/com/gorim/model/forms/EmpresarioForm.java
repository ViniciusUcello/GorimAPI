package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmpresarioForm {
	private final int idEmp;
	
	public EmpresarioForm(
			@JsonProperty("idEmp") int idEmp
			) {
		this.idEmp = idEmp;
	}

	public int getIdEmp() {
		return idEmp;
	}
	
	@Override
	public String toString() {
		return "EmpresarioForm [idEmp=" + this.idEmp + "]";
	}
	
}
