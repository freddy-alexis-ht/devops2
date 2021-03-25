package com.devops.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

@Configuration
@EnableJpaRepositories(basePackages="com.devops.backend.persistence.repositories")
@EntityScan(basePackages="com.devops.backend.persistence.domain.backend")
@EnableTransactionManagement
@PropertySource("file:D:\\UDEMY\\01 PROYECTO FULL STACK\\config\\application-common.properties")
public class ApplicationConfig {

	// desde application-common.properties -> miperfil
	@Value("${aws.s3.profile}")
	private String awsProfileName;
	
	@Bean
	public AmazonS3Client s3Client() {
		AWSCredentials credentials = new ProfileCredentialsProvider(awsProfileName).getCredentials();
		// Se usan las credenciales para construir un objeto-AmazonS3Client
		AmazonS3Client s3Client = new AmazonS3Client(credentials);
		
		Region region = Region.getRegion(Regions.SA_EAST_1);
		s3Client.setRegion(region);
		return s3Client;
	}
}
