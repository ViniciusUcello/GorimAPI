package com.gorim.api;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/queue");
		registry.setApplicationDestinationPrefixes("/app");
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/greeting")
				.setAllowedOrigins("http://localhost:4200")
				.setHandshakeHandler(new HandshakeHandler())
				.withSockJS();
	}
	
	class HandshakeHandler extends DefaultHandshakeHandler {
		
		public boolean beforeHandshake(
		        ServerHttpRequest request,
		        ServerHttpResponse response, 
		        WebSocketHandler wsHandler,
		        Map<String, Object> attributes) throws Exception
		{
		 
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                HttpSession session = servletRequest
                		.getServletRequest()
                		.getSession();
                attributes.put("sessionId", session.getId());
            }
                return true;
		}
		
		@Override
		protected Principal determineUser(ServerHttpRequest request,
				WebSocketHandler wsHandler, Map<String, Object> attributes) {
			
			//String idPessoa = request.getPrincipal().getName();
			System.out.println("request: " + request.toString());
			System.out.println("wsHandler: " + wsHandler.toString());
			System.out.println("attributes: " + attributes.toString());
			
			return new StompPrincipal(null);
		}
	}
	
	class StompPrincipal implements Principal {
	    String name;

	    StompPrincipal(String name) {
	        this.name = name;
	    }

	    @Override
	    public String getName() {
	        return name;
	    }
	}
}
