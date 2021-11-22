package com.ddd.balance.infrastructure.rest;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Valid
public record RepayRequest(

        @NotNull
        @Positive
        Long idCustomer,

        @NotNull
        @Positive
        //@Pattern(regexp = "^[0-9]*.[0-9]{0,2}$")
        @Digits(integer = 99, fraction = 2)
        BigDecimal amount
) { }
