package com.ddd.balance.infrastructure.rest;

import java.math.BigDecimal;

public record BalanceResponse(
        Long idBalance,
        BigDecimal amount
) { }
