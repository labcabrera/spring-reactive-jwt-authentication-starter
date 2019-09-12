package com.github.labcabrera.reactive.jwt.configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

	@Value("${security.jwt.secret}")
	private String secret;

	@Value("${security.jwt.tags.claims}")
	private String claimsTagName;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String token = authentication.getCredentials().toString();
		log.debug("Authenticating token {}", token);

		Jws<Claims> claims;
		try {
			claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
		}
		catch (MalformedJwtException ex) {
			throw new BadCredentialsException(String.format("Malformed Jwt token: '%s'", token));
		}
		catch (SignatureException ex) {
			throw new BadCredentialsException(ex.getMessage());
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