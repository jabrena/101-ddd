package com.jab.ddd.application;

import java.math.BigDecimal;
import java.util.Optional;

import com.jab.ddd.domain.model.Balance;

public interface BalanceService {
    
    Optional<Balance> witdhdraw(Long idCustomer, BigDecimal quantity);

}
