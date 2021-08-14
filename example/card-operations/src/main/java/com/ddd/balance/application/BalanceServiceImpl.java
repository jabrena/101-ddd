package com.ddd.balance.application;

import java.math.BigDecimal;
import java.util.Optional;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.domain.service.BalanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceServiceImpl implements BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    //TODO Refactor the Optional syntax
    @Override
    public Optional<Balance> witdhdraw(Long idCustomer, BigDecimal quantity) {

        Optional<Balance> currentBalance = balanceRepository.findById(idCustomer);

        if (currentBalance.isPresent()) {

            Optional<Balance> newBalance = currentBalance.get().witdhdraw(quantity);

            if(newBalance.isPresent()) {
                return Optional.ofNullable(balanceRepository.save(newBalance.get()));
            }
        }
        return Optional.empty();
    }
}
