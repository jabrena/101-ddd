package com.ddd.balance.domain.service;

import com.ddd.balance.domain.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {}
