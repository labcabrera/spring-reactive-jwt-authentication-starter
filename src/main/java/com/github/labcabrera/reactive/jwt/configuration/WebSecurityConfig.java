package com.github.labcabrera.reactive.jwt.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class WebSecurityConfig {

	@Autowired
	private Environment env;

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		log.info("Configuring JWT authentication filter");

		String unsecuredPaths = env.getProperty("security.jwt.unsecured-paths", "/actuator/health");
		log.debug("Unsecured paths: {}", unsecuredPaths);

		return http
			.httpBasic().disable()
			.formLogin().disable()
			.csrf().disable()

			.authorizeExchange()
			.pathMatchers(unsecuredPaths)
			.permitAll()

			.and()
			.addFilterAt(jwtFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
			.build();
	}

	private AuthenticationWebFilter jwtFilter() {
		ReactiveAuthenticationManager authManager = new JwtReactiveAuthenticationManager();
		AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager);
		ServerAuthenticationConverter authenticationConverter = new JwtServerAuthenticationConverter(env);
		filter.setServerAuthenticationConverter(authenticationConverter);
		return filter;
	}
}