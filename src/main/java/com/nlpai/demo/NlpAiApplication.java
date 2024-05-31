package com.nlpai.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class NlpAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NlpAiApplication.class, args);
	}

}
