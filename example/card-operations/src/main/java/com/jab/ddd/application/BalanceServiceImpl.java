package com.jab.ddd.application;

import java.math.BigDecimal;

import com.jab.ddd.domain.model.Balance;
import com.jab.ddd.domain.service.BalanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceServiceImpl implements BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;
 
    @Override
    public Balance witddraw(Long idCustomer, BigDecimal quantity) {
        Balance currentBalance = balanceRepository.findById(idCustomer)
            .orElseThrow(() -> new RuntimeException("Not found balance"));
        
        return balanceRepository.save(currentBalance.witddraw(quantity));
    }
}
