package com.ddd.balance.domain.service;

import com.ddd.balance.domain.model.Customer;

import org.springframework.data.repository.CrudRepository;

interface CustomerRepository extends CrudRepository<Customer, Long> {}