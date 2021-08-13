package com.jab.ddd.domain.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import com.jab.ddd.domain.model.Balance;
import com.jab.ddd.domain.model.Customer;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
public class BalanceRepositoryTest {

    @Autowired
    private CustomerRepository customers;

    @Autowired
    private BalanceRepository balances;

    @Test
    public void given_repository_when_save_then_Ok() {

        //Given
        Customer customer = new Customer(null, "Juan Antonio", "Breña Moral", "50401080H");
        var customerSaved = customers.save(customer);
        BigDecimal currentBalance = new BigDecimal("100.0");

        //When
        Balance balance = new Balance(null, currentBalance, customerSaved.id());
        var balanceSaved = balances.save(balance);
        
        //Then
        then(balanceSaved.balanceId()).isNotNull();
    }

}
