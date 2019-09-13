package com.github.labcabrera.reactive.jwt.configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

	private final byte[] secret;

	public JwtServerAuthenticationConverter(Environment env) {
		if (!env.containsProperty("security.jwt.secret")) {
			throw new IllegalArgumentException("Missing required property 'security.jwt.secret'");
		}
		secret = env.getProperty("security.jwt.secret").getBytes();
	}

	@Override
	public Mono<Authentication> convert(ServerWebExchange swe) {
		ServerHttpRequest request = swe.getRequest();
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String jwt = authHeader.substring(7);
			//TODO parse/err handling
			return create(jwt);
		}
		else {
			return Mono.empty();
		}
	}

	private Mono<Authentication> create(String jwt) {
		Jws<Claims> claims;
		try {
			claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt);
		}
		catch (Exception ex) {
			log.debug("JWT parse error", ex);
			return Mono.empty();
		}
		String user = claims.getBody().getSubject();
		if (user == null) {
			log.debug("Missing subject in JWT token");
			return Mono.empty();
		}
		List<?> roles = claims.getBody().get("roles", List.class);
		List<GrantedAuthority> grantedAuthorities = roles.stream().map(e -> new SimpleGrantedAuthority(String.valueOf(e)))
			.collect(Collectors.toList());

		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user, user, grantedAuthorities);
		log.debug("Granted authorities: {}", result.getAuthorities());
		Map<String, Object> details = new LinkedHashMap<>();
		details.put("issuer", claims.getBody().getIssuer());
		details.put("expiration", claims.getBody().getExpiration());
		details.put("issuedAt", claims.getBody().getIssuedAt());
		result.setDetails(details);

		return Mono.just(result);
	}

}
