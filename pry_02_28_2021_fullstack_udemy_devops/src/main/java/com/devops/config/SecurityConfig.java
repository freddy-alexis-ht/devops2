package com.devops.config;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devops.backend.service.UserSecurityService;
import com.devops.web.controllers.ForgotMyPasswordController;
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserSecurityService userSecurityService;
	
	@Autowired
	private Environment env;

	/** The encryption */
	private static final String SALT = "fdalkjalk;3jlwf00sfaof";

	// arg-1: es el 'strength', por defecto es '10'
	// Del Spring javadoc docum para esta clase, se sabe que mientras más 'strength', más
	// ..trabajo tendrá que hacerse, de forma exponencial, to hash the password
	// Así que es un asunto de encontrar el balance entre velocidad y strength
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12, new SecureRandom(SALT.getBytes()));
	}
	
    /** Public URLs. */
    private static final String[] PUBLIC_MATCHERS = {
            "/webjars/**", // bootstrap & jquery libraries
            "/css/**",
            "/js/**",
            "/images/**", // todo el contenido estático
            "/",		  // home page
            "/about/**",
            "/contact/**",
            "/error/**/*",
            "/console/**",
            ForgotMyPasswordController.FORGOT_PASSWORD_URL_MAPPING,
            ForgotMyPasswordController.CHANGE_PASSWORD_PATH
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {

    	// env.getActiveProfiles() .. obtiene todos los active-profiles desde el Environment
    	// Si estamos trabajando con dev-profiles, desactivará 'csrf' y 'frame-options' del header ..
    	// .. esto es necesario para que el H2-console trabaja correctamente
	     List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
	     if (activeProfiles.contains("dev")) {
	            http.csrf().disable();
	            http.headers().frameOptions().disable();
	     }
    	
    	http
                .authorizeRequests()
                .antMatchers(PUBLIC_MATCHERS).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/payload")
                .failureUrl("/login?error").permitAll()
                .and()
                .logout().permitAll();
    }

    // Con usar la dependencia de Spring-Security este bean está disponible
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
        		.userDetailsService(userSecurityService)
        		.passwordEncoder(passwordEncoder());
    }
    
//    @Bean
//    public PasswordEncoder getPasswordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }   

	
}
