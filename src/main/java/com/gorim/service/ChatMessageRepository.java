package com.gorim.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gorim.model.db.ChatMessage;
import com.gorim.model.forms.MessageStatus;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    long countBySenderIdAndRecipientIdAndStatus(
            String senderId, String recipientId, MessageStatus status);
    
    Optional<ChatMessage> findById(long id);
    List<ChatMessage> findByChatId(String chatId);
    List<ChatMessage> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
