package com.ddd.balance.application;

import java.math.BigDecimal;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.infrastructure.rest.WithdrawRequest;
import reactor.core.publisher.Mono;

public interface BalanceService {

    Mono<Balance> witdhdraw(Mono<WithdrawRequest> withdrawRequest);
    Mono<Balance> witdhdrawLimit(Long idCustomer, BigDecimal limit);
    Mono<Balance> repay(Long idCustomer, BigDecimal amount);
}
