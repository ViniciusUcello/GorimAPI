package com.gorim.model.forms;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
	private int type;
	private int fromId;
	private int toId;
	private String message;
	private Date dateSent;
	private Date dateSeen;
	
	public Message(
		@JsonProperty("type") int type,
		@JsonProperty("fromId") int fromId,
		@JsonProperty("toId") int toId,
		@JsonProperty("message") String message,
		@JsonProperty("dateSent") Date dateSent,
		@JsonProperty("dateSeen") Date dateSeen
	){
		this.type = type;
		this.fromId = fromId;
		this.toId = toId;
		this.message = message;
		this.dateSent = dateSent;
		this.dateSeen = dateSeen;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}

	public Date getDateSeen() {
		return dateSeen;
	}

	public void setDateSeen(Date dateSeen) {
		this.dateSeen = dateSeen;
	}
	
	
}
