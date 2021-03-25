package com.devops.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.devops.backend.service.EmailService;
import com.devops.backend.service.SmtpEmailService;

@Configuration
@Profile("prod")
@PropertySource("file:D:\\UDEMY\\01 PROYECTO FULL STACK\\config\\application-prod.properties")
public class ProductionConfig {

	@Bean
	public EmailService emailService() {
		return new SmtpEmailService();
	}
}
