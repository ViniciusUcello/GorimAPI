/**
 * Acesso ao repositório do usuário
*/
package com.gorim.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gorim.model.db.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	/**
	 * Acha os usuários a partir do id do jogo
	 * 
	 * @param idJogo
	 * @return Lista de User
	 */
	public List<User> findByIdJogo(int idJogo);

	/**
	 * Acha o usuário a partir do id do jogo e do id do cliente
	 * 
	 * @param idJogo
	 * @param idPessoa
	 * @return User
	 */
	public User findByIdJogoAndIdPessoa(int idJogo, int idPessoa);

	/**
	 * Acha o usuário a partir do username
	 * 
	 * @param username
	 * @return User
	 */
	public User findFirstByUsername(String username);
	
	/**
	 * Verifica se o usuário existe a partir do username
	 * @param username
	 * @return True ou False
	 */
	public boolean existsByUsername(String username);

}
