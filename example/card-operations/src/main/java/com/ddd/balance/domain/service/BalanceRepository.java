package com.ddd.balance.domain.service;

import com.ddd.balance.domain.model.Balance;

import org.springframework.data.repository.CrudRepository;

public interface BalanceRepository extends CrudRepository<Balance, Long> {}