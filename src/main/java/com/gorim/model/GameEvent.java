package com.gorim.model;

import org.json.simple.JSONObject;

public class GameEvent {
	private String type;
	private JSONObject content;
	
	public GameEvent(String type, JSONObject content){
		this.type = type;
		this.content = content;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject aux = new JSONObject();
		
		aux.put("t", this.type);
		aux.put("c", this.content);
		
		return aux;
	}
}
