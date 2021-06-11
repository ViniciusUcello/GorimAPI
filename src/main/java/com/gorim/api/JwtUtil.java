package com.gorim.api;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.gorim.model.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class JwtUtil {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
	
	private String secret;
	private int jwtExpirationInMs;
	
	@Value("${jwt.secret}")
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	@Value("${jwt.jwtExpirationInMs}")
	public void setJwtExpirationInMs(int jwtExpirationInMs) {
		this.jwtExpirationInMs = jwtExpirationInMs;
	}
	
	public String generateJwtToken(Authentication authentication) {

		CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
		
		Map<String, Object> claims = new HashMap<>();
		claims.put("isUser", true);
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + this.jwtExpirationInMs))
				.signWith(SignatureAlgorithm.HS512, this.secret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public List<SimpleGrantedAuthority> getRolesFromToken(String authToken) {
		List<SimpleGrantedAuthority> roles = null;
		Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody();
		Boolean isUser = claims.get("isUser", Boolean.class);
		if (isUser != null && isUser == true) {
			roles = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
		}
		return roles;
	}
	
}
