package com.ddd.balance.application;

import java.math.BigDecimal;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.domain.service.BalanceRepository;

import com.ddd.balance.infrastructure.rest.WithdrawRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class BalanceServiceImpl implements BalanceService {

    Logger logger = LoggerFactory.getLogger(BalanceServiceImpl.class);

    @Autowired
    private BalanceRepository balanceRepository;

    @Transactional
    @Override
    public Mono<Balance> witdhdraw(Mono<WithdrawRequest> withdrawRequest) {

        System.out.println("demo");
        return balanceRepository.findById(withdrawRequest.map(x -> x.idCustomer()))
                .map(currentBalance -> currentBalance.withdraw(withdrawRequest.map(x -> x.amount())
                .map(x -> {


                    if(x.isPresent()) {
                        return balanceRepository.save(currentBalance.get()).log("Done");
                    }
                    return Mono.empty();
                })
                .log("Done");
    }

    @Transactional
    @Override
    public Mono<Balance> witdhdrawLimit(Long idCustomer, BigDecimal limit) {

        return balanceRepository.findById(idCustomer)
                .map(currentBalance -> currentBalance.configureWithdrawLimit(limit))
                .flatMap(currentBalance -> {

                    if(currentBalance.isPresent()) {
                        return balanceRepository.save(currentBalance.get());
                    }
                    return Mono.empty();
                });
    }

    @Transactional
    @Override
    public Mono<Balance> repay(Long idCustomer, BigDecimal amount) {

        return balanceRepository.findById(idCustomer)
                .map(currentBalance -> currentBalance.repay(amount))
                .flatMap(currentBalance -> {

                    if(currentBalance.isPresent()) {
                        return balanceRepository.save(currentBalance.get());
                    }
                    return Mono.empty();
                });
    }
}
