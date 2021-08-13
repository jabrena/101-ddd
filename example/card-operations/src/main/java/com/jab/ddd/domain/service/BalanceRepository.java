package com.jab.ddd.domain.service;

import org.springframework.data.repository.CrudRepository;

import com.jab.ddd.domain.model.Balance;

public interface BalanceRepository extends CrudRepository<Balance, Long> {}