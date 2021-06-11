package com.gorim.model;

public class AuthenticationResponse {
	private String token;
	
	public AuthenticationResponse(String token) {
		this.token = token;
	}
	
	public AuthenticationResponse() { }
	
	public String getToken() {
		return this.token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
}