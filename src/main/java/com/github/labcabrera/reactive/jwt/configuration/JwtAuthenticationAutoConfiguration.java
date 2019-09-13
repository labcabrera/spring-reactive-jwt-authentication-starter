package com.github.labcabrera.reactive.jwt.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(WebSecurityConfig.class)
public class JwtAuthenticationAutoConfiguration {

}
