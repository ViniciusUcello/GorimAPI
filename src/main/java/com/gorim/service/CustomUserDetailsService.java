package com.gorim.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gorim.model.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<SimpleGrantedAuthority> roles = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
		if(userRepository.existsByUsername(username)){
			CustomUserDetails user = new CustomUserDetails(this.userRepository.findFirstByUsername(username), roles);
			return user;
		}
		throw new UsernameNotFoundException("User not found with the name " + username);
	}

}