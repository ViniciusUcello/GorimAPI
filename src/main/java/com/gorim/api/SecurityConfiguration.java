package com.gorim.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gorim.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	AuthEntryPointJwt authEntryPoint;
	
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.customUserDetailsService);//.passwordEncoder(this.passwordEncoder());
	}
	
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.exceptionHandling().authenticationEntryPoint(this.authEntryPoint).and()
			.authorizeRequests()
			.antMatchers("/**").permitAll()
			.antMatchers("/authenticate").permitAll().anyRequest().authenticated()
			//.and().formLogin().disable()
			.and().exceptionHandling()
	        .authenticationEntryPoint(this.authEntryPoint).and()
			// make sure we use stateless session; session won't be used to
			// store user's state.
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(this.authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
}
