package com.gorim.model.db;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.gorim.model.forms.MessageStatus;

@Entity
public class ChatMessage {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String chatId;
	private String senderId;
	private String recipientId;
	private String senderName;
	private String recipientName;
	private String content;
	private Date timestamp;
	private MessageStatus status;
	
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
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
	public String getSenderName() {
		return this.senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getRecipientName() {
		return this.recipientName;
	}
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getTimestamp() {
		return this.timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public MessageStatus getStatus() {
		return this.status;
	}
	public void setStatus(MessageStatus status) {
		this.status = status;
	}
}