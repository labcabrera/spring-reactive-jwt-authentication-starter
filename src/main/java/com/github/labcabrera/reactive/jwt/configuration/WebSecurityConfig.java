package com.github.labcabrera.reactive.jwt.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@EnableWebFluxSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		return http
			.authorizeExchange()
			.pathMatchers("/**")
			.authenticated()
			.and()
			.addFilterAt(jwtFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
			.build();
	}

	private AuthenticationWebFilter jwtFilter() {
		ReactiveAuthenticationManager authManager = new JwtReactiveAuthenticationManager();
		AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager);
		ServerAuthenticationConverter authenticationConverter = new JwtServerAuthenticationConverter();
		filter.setServerAuthenticationConverter(authenticationConverter);
		return filter;
	}
}