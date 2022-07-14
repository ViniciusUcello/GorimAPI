package com.gorim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gorim.model.db.ChatRoom;

import java.util.Optional;

@Service
public class ChatRoomService {

    // Acesso ao banco de dados para salas do chat
    @Autowired private ChatRoomRepository chatRoomRepository;

    /**
     * Retorna o nome do chat e o cria se não existe (depende do parâmetro)
     * 
     * @param senderId
     * @param recipientId
     * @param createIfNotExist
     * @return Nome
     */
    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExist) {

    	Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
    	
    	if(chatRoom.isPresent()) return Optional.of(chatRoom.get().getChatId());
    	
    	if(!createIfNotExist) {
            return Optional.empty();
        }
        
        // Cria o chat para ambos os lados, para o caso da função ser chamada
        // por um ou outro usuário (por exemplo um chat criado para o EmpSem e
        // AgrAT1, se a função for chamada pelo EmpSem, o senderId seria do EmpSem
        // e o recipientId seria do AgrAT1; porém, se fosse chamada pelo AgrAT1, o
        // senderId seria o do AgrAT1 e o recipientId seria o do EmpSem)
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
