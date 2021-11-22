package com.ddd.balance.application;

import java.math.BigDecimal;
import java.util.Optional;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.domain.model.MoneyRepaidEvent;
import com.ddd.balance.domain.model.MoneyWithDrewEvent;
import com.ddd.balance.domain.service.BalanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceServiceImpl implements BalanceService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private BalanceRepository balanceRepository;

    @Transactional
    @Override
    public Optional<Balance> witdhdraw(Long idCustomer, BigDecimal quantity) {

        var newBalance = balanceRepository.findById(idCustomer)
                .flatMap(currentBalance -> currentBalance.withdraw(quantity))
                .map(balanceRepository::save);

        //TODO Review how to integrate event publising at Domain level
        newBalance.ifPresent(balance -> {
            applicationEventPublisher.publishEvent(new MoneyWithDrewEvent(balance));
        });

        return newBalance;
    }

    @Transactional
    @Override
    public Optional<Balance> witdhdrawLimit(Long idCustomer, BigDecimal limit) {

        return balanceRepository.findById(idCustomer)
                .flatMap(currentBalance -> currentBalance.configureWithdrawLimit(limit))
                .map(balanceRepository::save);
    }

    @Transactional
    @Override
    public Optional<Balance> repay(Long idCustomer, BigDecimal amount) {

        var newBalance = balanceRepository.findById(idCustomer)
                .flatMap(currentBalance -> currentBalance.repay(amount))
                .map(balanceRepository::save);

        newBalance.ifPresent(balance -> {
            applicationEventPublisher.publishEvent(new MoneyRepaidEvent(balance));
        });

        return newBalance;
    }
}
