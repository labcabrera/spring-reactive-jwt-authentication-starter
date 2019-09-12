package com.github.labcabrera.reactive.jwt;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {

	private final CustomerRepository customerRepository;

	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@GetMapping("/{id}")
	private Mono<Customer> findById(@PathVariable String id, ServerWebExchange exchange) {
		log.debug("Finding customer {}", id);
		return customerRepository.findById(id);
	}
	
	@GetMapping("/{id}/private")
	@PreAuthorize("hasRole('root')")
	private Mono<Customer> findDetail(@PathVariable String id, ServerWebExchange exchange) {
		log.debug("Finding customer private data {}", id);
		return customerRepository.findById(id);
	}
}