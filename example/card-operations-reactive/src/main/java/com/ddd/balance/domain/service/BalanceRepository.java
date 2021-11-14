package com.ddd.balance.domain.service;

import com.ddd.balance.domain.model.Balance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BalanceRepository extends ReactiveCrudRepository<Balance, Long> {}
