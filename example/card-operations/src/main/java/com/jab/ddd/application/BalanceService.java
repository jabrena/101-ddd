package com.jab.ddd.application;

import java.math.BigDecimal;

import com.jab.ddd.domain.model.Balance;

public interface BalanceService {
    
    Balance witddraw(Long idCustomer, BigDecimal quantity);

}
