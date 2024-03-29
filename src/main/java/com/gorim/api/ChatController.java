package com.gorim.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gorim.model.GameEvent;
import com.gorim.model.db.ChatMessage;
import com.gorim.model.forms.ChatNotification;
import com.gorim.service.ChatMessageService;
import com.gorim.service.ChatRoomService;

@Controller
public class ChatController {
	  @Autowired private SimpMessagingTemplate messagingTemplate;
	  
	  @Autowired private ChatMessageService chatMessageService;
	  
	  @Autowired private ChatRoomService chatRoomService;
	  
	  @MessageMapping("/chat")
	  public void processMessage(@Payload ChatMessage chatMessage) {
		  Optional<String> chatId = chatRoomService // Antes, "String" era "var" => JAVA 10
				  .getChatId(
						  chatMessage.getSenderId(),
						  chatMessage.getRecipientId(),
						  true
					);
		  
		  chatMessage.setChatId(chatId.get());
	  
		  ChatMessage saved = chatMessageService.save(chatMessage);
		  
		  ChatNotification cn = new ChatNotification(
				  saved.getId(),
				  saved.getSenderId(),
				  saved.getSenderName()
		  );
		  
		  messagingTemplate.convertAndSendToUser(
				  chatMessage.getRecipientId(),
				  "/queue/messages",
				  (new GameEvent("chat", cn.toJSON())).toJSON()
		  );
	  }

	  @GetMapping("/messages/{senderId}/{recipientId}/count")
	  public ResponseEntity<Long> countNewMessages(
	          @PathVariable("senderId") String senderId,
	          @PathVariable("recipientId") String recipientId
	  ){
		  return ResponseEntity
	              .ok(chatMessageService.countNewMessages(senderId, recipientId));
	  }

	  @GetMapping("/messages/{senderId}/{recipientId}")
	  public ResponseEntity<?> findChatMessages(
			  @PathVariable String senderId,
	          @PathVariable String recipientId
	  ){
		  return ResponseEntity
				  .ok(chatMessageService.findChatMessages(senderId, recipientId));
	  }

	  @GetMapping("/messages/{id}")
	  public ResponseEntity<?> findMessage(@PathVariable("id") long id) {
		  return ResponseEntity
				  .ok(chatMessageService.findById(id));
	  }
	 
}
