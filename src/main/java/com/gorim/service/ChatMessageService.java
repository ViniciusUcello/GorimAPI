package com.gorim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gorim.exception.ResourceNotFoundException;
import com.gorim.model.db.ChatMessage;
import com.gorim.model.forms.MessageStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        this.chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId, String recipientId) {
        return this.chatMessageRepository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        Optional<String> chatId = this.chatRoomService.getChatId(senderId, recipientId, false);

        List<ChatMessage> messages = chatId.map(id -> this.chatMessageRepository.findByChatId(id))
        									.orElse(new ArrayList<>());

        if(messages.size() > 0) {
            this.updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }

        return messages;
    }

    public ChatMessage findById(long id) {
    	
    	Optional<ChatMessage> aux = this.chatMessageRepository.findById(id);
    	if(aux.isPresent()) {
    		aux.get().setStatus(MessageStatus.DELIVERED);
    		return this.chatMessageRepository.save(aux.get());
    	}
    	throw new ResourceNotFoundException("Não foi possível achar a mensagem (" + id + ")");
    }

    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
    	List<ChatMessage> messages = this.chatMessageRepository.findBySenderIdAndRecipientId(senderId, recipientId);
    	
    	for(ChatMessage message : messages) {
    		message.setStatus(status);
    		this.chatMessageRepository.save(message);
    	}
    }
}