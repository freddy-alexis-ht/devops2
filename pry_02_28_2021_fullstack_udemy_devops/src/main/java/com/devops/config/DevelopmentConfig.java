package com.devops.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.devops.backend.service.EmailService;
import com.devops.backend.service.MockEmailService;

@Configuration
@Profile("dev")
@PropertySource("file:D:\\UDEMY\\01 PROYECTO FULL STACK\\application-dev.properties")
public class DevelopmentConfig {

	@Bean
	public EmailService emailService() {
		return new MockEmailService();
	}
}
