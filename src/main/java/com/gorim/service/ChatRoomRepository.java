/**
 * Interface para pegar a sala de mensagens do banco de dados
 * ao ser requisitado pelo sistema
*/
package com.gorim.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gorim.model.db.ChatRoom;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    /**
     * Retorna a sala do chat utilizando como base os ids do usuário que envia
     * e o que recebe a mensagem (senderId e recipientId)
     * @param senderId
     * @param recipientId
     * @return Opcional de ChatRoom
     */
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}