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


    @Override
    public Optional<Balance> witdhdraw(Long idCustomer, BigDecimal quantity) {

        Optional<Balance> currentBalance = balanceRepository.findById(idCustomer);

        //TODO Refactor the Optional syntax
        if (currentBalance.isPresent()) {

            Optional<Balance> newBalance = currentBalance.get().withdraw(quantity);

            if(newBalance.isPresent()) {
                return Optional.ofNullable(balanceRepository.save(newBalance.get()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Balance> witdhdrawLimit(Long idCustomer, BigDecimal limit) {

        Optional<Balance> currentBalance = balanceRepository.findById(idCustomer);

        //TODO Refactor the Optional syntax
        if (currentBalance.isPresent()) {

            Optional<Balance> newLimit = currentBalance.get().configureWithdrawLimit(limit);

            if(newLimit.isPresent()) {
                return Optional.ofNullable(balanceRepository.save(newLimit.get()));
            }

        }
        return Optional.empty();
    }

    @Override
    public Optional<Balance> repay(Long idCustomer, BigDecimal amount) {

        Optional<Balance> currentBalance = balanceRepository.findById(idCustomer);

        //TODO Refactor the Optional syntax
        if (currentBalance.isPresent()) {

            Optional<Balance> newBalance = currentBalance.get().repay(amount);

            if(newBalance.isPresent()) {
                return Optional.ofNullable(balanceRepository.save(newBalance.get()));
            }

        }
        return Optional.empty();
    }
}
