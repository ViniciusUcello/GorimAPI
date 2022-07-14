/**
 * Serviço que gerencia as mensagens e faz a comunicação
 * com a ChatMessageRepository
 */
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
    /**
     * Comunicaçãoa com o banco de dados
     */
    @Autowired private ChatMessageRepository chatMessageRepository;
    /**
     * Comunicação com o serviço
     */
    @Autowired private ChatRoomService chatRoomService;

    /**
     * Salva a mensagem no banco de dados ao receber
     * @param chatMessage
     * @return a propria mensagem
     */
    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        this.chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    /**
     * Conta quantas mensagens novas entre o senderId e o recipientId existem
     * no banco, ou seja, quantas ainda não foram lidas pelo recipientId
     * @param senderId
     * @param recipientId
     * @return a quantidade de mensagens
     */
    public long countNewMessages(String senderId, String recipientId) {
        return this.chatMessageRepository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    /**
     * Acha as mensagens no banco que fazem parte da conversa entre
     * os usuários com ids senderId e recipientId 
     * @param senderId
     * @param recipientId
     * @return Lista de ChatMessage
     */
    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        // Acha o ChatId a partir dos ids dos usuários
        // ou cria a sala caso não exista ainda
        Optional<String> chatId = this.chatRoomService.getChatId(senderId, recipientId, false);

        // Busca as mensagens a partir do id da sala ou retorna uma lista vazia
        List<ChatMessage> messages = chatId.map(id -> this.chatMessageRepository.findByChatId(id))
        									.orElse(new ArrayList<>());

        // Se tiver mensagens na lista, seta todas como enviadas
        if(messages.size() > 0) {
            this.updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }

        return messages;
    }

    /**
     * Acha a mensagem de id id
     * @param id
     * @return ChatMessage
     */
    public ChatMessage findById(long id) {
    	
        // Tenta achar a mensagem
    	Optional<ChatMessage> aux = this.chatMessageRepository.findById(id);

        // Se achou
    	if(aux.isPresent()) {
    		// Marca como enviada e retorna após salvar no banco o novo status
            aux.get().setStatus(MessageStatus.DELIVERED);
    		return this.chatMessageRepository.save(aux.get());
    	}
        // Se não achou, larga exceção
    	throw new ResourceNotFoundException("Não foi possível achar a mensagem (" + id + ")");
    }

    /**
     * Atualiza status de todas as mensagens entre os usuários de ids senderId
     * e recipientId com o status recebido por parâmetro
     * @param senderId
     * @param recipientId
     * @param status
     */
    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        // Acha as mensagens a partir dos ids
    	List<ChatMessage> messages = this.chatMessageRepository.findBySenderIdAndRecipientId(senderId, recipientId);
    	
        // Para cada mensagem, salva com novo status
    	for(ChatMessage message : messages) {
    		message.setStatus(status);
    		this.chatMessageRepository.save(message);
    	}
    }
}