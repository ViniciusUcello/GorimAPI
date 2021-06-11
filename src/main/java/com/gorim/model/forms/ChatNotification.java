package com.gorim.model.forms;

public class ChatNotification {
	private long id;
	private String senderId;
	private String senderName;
	
	public ChatNotification(long id, String senderId, String senderName) {
		this.id = id;
		this.senderId = senderId;
		this.senderName = senderName;
	}
	
	public ChatNotification() {	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getSenderId() {
		return this.senderId;
	}
	
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	
	public String getSenderName() {
		return this.senderName;
	}
	
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	@Override
	public String toString() {
		return "ChatNotification [id=" + id + ", senderId=" + senderId + ", senderName=" + senderName + "]";
	}
}
