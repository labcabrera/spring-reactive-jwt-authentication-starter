package com.github.labcabrera.reactive.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class CreateJwtTokenTest {

	@Test
	public void test() {
		String username = "demo-user";
		String issuer = "sample-application";
		String secret = "changeit";
		int expiration = 60000;

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expirationDate = now.plusMinutes(expiration);
		ZoneId zoneId = ZoneId.systemDefault();
		List<String> roles = Arrays.asList("role-customer-reader", "role-foo", "role-bar");
		String token = Jwts.builder()
			.setIssuedAt(Date.from(now.atZone(zoneId).toInstant()))
			.setExpiration(Date.from(expirationDate.atZone(zoneId).toInstant())).setIssuer(issuer)
			.setSubject(username).claim("roles", roles)
			.signWith(SignatureAlgorithm.HS512, secret)
			.compact();
		System.out.println("Token:");
		System.out.println("Bearer " + token);
	}

}
