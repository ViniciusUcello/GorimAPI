package com.gorim;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GorimApplication {
	
	@PostConstruct
    void started() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }
	
	public static void main(String[] args) {
		SpringApplication.run(GorimApplication.class, args);
	}

}
