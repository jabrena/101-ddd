package com.ddd.balance.domain.service;

import com.ddd.balance.domain.model.entity.Balance;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface BalanceService {

    BalanceOperationResult createBalance(UUID customerId, BigDecimal amount);
    Optional<Balance> getBalance(UUID balanceId);
    BalanceOperationResult defineLimit(UUID balanceId, BigDecimal balanceLimit);
    BalanceOperationResult withdraw(UUID balanceId, BigDecimal amount);
    BalanceOperationResult repay(UUID balanceId, BigDecimal amount);
}
