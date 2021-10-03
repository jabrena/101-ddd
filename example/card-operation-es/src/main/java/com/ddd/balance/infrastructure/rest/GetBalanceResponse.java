package com.ddd.balance.infrastructure.rest;

import java.math.BigDecimal;

public record GetBalanceResponse(
    String balanceId,
    String customerId,
    BigDecimal balance, BigDecimal balanceLimit) implements BalanceOperationResponse {

}
