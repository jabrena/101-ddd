package com.ddd.balance.infrastructure.rest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Valid
public record BalanceRequest(

        @NotNull
        @Positive
        Long idCustomer
) { }
