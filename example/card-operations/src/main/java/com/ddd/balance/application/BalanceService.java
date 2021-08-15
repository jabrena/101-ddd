package com.ddd.balance.application;

import java.math.BigDecimal;
import java.util.Optional;

import com.ddd.balance.domain.model.Balance;

public interface BalanceService {

    Optional<Balance> witdhdraw(Long idCustomer, BigDecimal amount);
    Optional<Balance> witdhdrawLimit(Long idCustomer, BigDecimal limit);

}
