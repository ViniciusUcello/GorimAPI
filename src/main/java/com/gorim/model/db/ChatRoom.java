package com.gorim.model.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class ChatRoom {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
	
    private String chatId;
    private String senderId;
    private String recipientId;
    
	public ChatRoom(String chatId, String senderId, String recipientId) {
		this.chatId = chatId;
		this.senderId = senderId;
		this.recipientId = recipientId;
	}
	public ChatRoom() { }
	
	public long getId() {
		return this.id;
	}
	
	public String getChatId() {
		return this.chatId;
	}
	
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	
	public String getSenderId() {
		return this.senderId;
	}
	
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	
	public String getRecipientId() {
		return this.recipientId;
	}
	
	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}
	
	@Override
	public String toString() {
		return "ChatRoom [id=" + id + ", chatId=" + chatId + ", senderId=" + senderId + ", recipientId=" + recipientId
				+ "]";
	}
	
}