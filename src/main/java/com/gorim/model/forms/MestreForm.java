package com.gorim.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MestreForm {
	private final int playerQuantity;

	public MestreForm(
			@JsonProperty("playerQuantity") int playerQuantity
	) {
		this.playerQuantity = playerQuantity;
	}

	public int getPlayerQuantity() {
		return playerQuantity;
	}
	
	
}
