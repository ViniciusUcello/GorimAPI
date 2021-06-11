package com.gorim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gorim.model.db.ChatRoom;

import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired private ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExist) {

    	Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
    	
    	if(chatRoom.isPresent()) return Optional.of(chatRoom.get().getChatId());
    	
    	if(!createIfNotExist) {
            return Optional.empty();
        }
        
    	String chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipient = new ChatRoom(chatId, senderId, recipientId);

        ChatRoom recipientSender = new ChatRoom(chatId, recipientId, senderId);
        
        if(senderRecipient != null && recipientSender != null) {
        	chatRoomRepository.save(senderRecipient);
            chatRoomRepository.save(recipientSender);
        }
        else System.out.println("É null");

        return Optional.of(chatId);
    }
}
