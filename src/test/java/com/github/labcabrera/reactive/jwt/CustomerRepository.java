package com.github.labcabrera.reactive.jwt;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class CustomerRepository {

	public Mono<Customer> findById(String id) {
		Customer customer = new Customer();
		customer.setId(id);
		customer.setName("Customer Name " + id);
		return Mono.just(customer);
	}

}
