package com.devops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages="com.devops.backend.persistence.repositories")
public class Pry02282021FullstackUdemyDevopsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Pry02282021FullstackUdemyDevopsApplication.class, args);
	}

}
