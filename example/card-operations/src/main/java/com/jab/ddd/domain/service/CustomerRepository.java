package com.jab.ddd.domain.service;

import org.springframework.data.repository.CrudRepository;

import com.jab.ddd.domain.model.Customer;

interface CustomerRepository extends CrudRepository<Customer, Long> {}