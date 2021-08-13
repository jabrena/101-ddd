package com.jab.ddd.domain.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class BalanceTest {
    
    @Test
    public void given_balance_when_withdrew_then_Ok() {

        //Given
        Customer customer = new Customer(1L, "Juan Antonio", "Breña Moral", "50401080H");
        BigDecimal currentBalance = new BigDecimal("100.0");
        
        //When
        Balance balance = new Balance(1L, currentBalance, customer.id());

        //Then
        then(balance.balanceId()).isNotNull();
    }

}
