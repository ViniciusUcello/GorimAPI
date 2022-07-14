/**
 * Interface para pegar as mensagens do banco de dados
 * ao serem requisitados pelos usuários
*/
package com.gorim.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gorim.model.db.ChatMessage;
import com.gorim.model.forms.MessageStatus;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    /**
     * Executa o contador de mensagens no banco utilizando os parâmetros
     * @param senderId
     * @param recipientId
     * @param status
     * @return long
     */
    long countBySenderIdAndRecipientIdAndStatus(
            String senderId, String recipientId, MessageStatus status);
    
    /**
     * Acha as mensagens utilizando o id
     * @param id
     * @return ChatMessage, se tiver
     */
    Optional<ChatMessage> findById(long id);

    /**
     * Acha as mensagens utilizando o ChatId (id da sala em que a mensagem foi enviada)
     * @param chatId
     * @return Lista de ChatMessage
     */
    List<ChatMessage> findByChatId(String chatId);

    /**
     * Acha as mensagens utilizando o SenderId e o RecipientId (o id de quem enviou e recebeu)
     * @param senderId
     * @param recipientId
     * @return Lista de ChatMessage
     */
    List<ChatMessage> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
