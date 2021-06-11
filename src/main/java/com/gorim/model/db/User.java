package com.gorim.model.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private int idJogo;
	private int idPessoa;
	private String username;
	private String password;
	
	public User() {	}
	
	public User(int idJogo, int idPessoa, String username, String password) {
		this.idJogo = idJogo;
		this.idPessoa = idPessoa;
		this.username = username;
		this.password = password;
	}
	public int getIdJogo() {
		return this.idJogo;
	}
	public void setIdJogo(int idJogo) {
		this.idJogo = idJogo;
	}
	public int getIdPessoa() {
		return this.idPessoa;
	}
	public void setIdPessoa(int idPessoa) {
		this.idPessoa = idPessoa;
	}
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [idJogo=" + this.idJogo + ", idPessoa=" + this.idPessoa + ", username=" + this.username + ", password=" + this.password + "]";
	}
}
