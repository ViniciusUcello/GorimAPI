package com.gorim.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gorim.model.db.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{//CrudRepository<User, Integer> {

	public List<User> findByIdJogo(int idJogo);
	public User findByIdJogoAndIdPessoa(int idJogo, int idPessoa);
	public User findFirstByUsername(String username);
	
	public boolean existsByUsername(String username);

}
