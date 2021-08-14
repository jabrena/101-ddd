package com.ddd.balance.domain.model;

import java.math.BigDecimal;
import java.util.Optional;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.domain.model.Customer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class BalanceTest {

    @Test
    public void given_balance_when_withdraw_happy_path_then_Ok() {

        //Given
        Customer customer = new Customer(1L, "Juan Antonio", "Breña Moral", "50401080H");
        BigDecimal currentBalance = new BigDecimal("100.0");
        Balance balance = new Balance(1L, currentBalance, customer.id());

        //When
        BigDecimal amount = new BigDecimal("10.00");
        Optional<Balance> newBalance = balance.withdraw(amount);

        //Then
        then(newBalance.isPresent()).isTrue();
        newBalance.stream().forEach(value -> {
            then(value.balance())
                    .usingComparator(BigDecimal::compareTo)
                    .isEqualByComparingTo(currentBalance.subtract(amount));
        });
    }

    @Test
    public void given_balance_when_withdraw_with_not_enough_balance_then_Ko() {

        //Given
        Customer customer = new Customer(1L, "Juan Antonio", "Breña Moral", "50401080H");
        BigDecimal currentBalance = new BigDecimal("100.0");
        Balance balance = new Balance(1L, currentBalance, customer.id());

        //When
        BigDecimal amount = new BigDecimal("101.00");
        Optional<Balance> newBalance = balance.withdraw(amount);

        //Then
        then(newBalance.isPresent()).isFalse();
    }

    @Test
    public void demo() {

        System.out.println("10 > 0 = " + new BigDecimal("10.00").compareTo(BigDecimal.ZERO));
        System.out.println(new BigDecimal("10.00").compareTo(BigDecimal.ZERO) == 1);
        System.out.println("10.00 = 10.0 = " + new BigDecimal("10.00").compareTo(new BigDecimal("10.0")));
        System.out.println(new BigDecimal("10.00").compareTo(new BigDecimal("10.0")) == 0);
        System.out.println("10.00 < 20.0 = " + new BigDecimal("10.00").compareTo(new BigDecimal("20.0")));
        System.out.println(new BigDecimal("10.00").compareTo(new BigDecimal("20.0")) == -1);

    }
}
