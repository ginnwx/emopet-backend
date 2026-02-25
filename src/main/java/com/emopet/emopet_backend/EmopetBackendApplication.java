package com.emopet.emopet_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmopetBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmopetBackendApplication.class, args);
	}

}
