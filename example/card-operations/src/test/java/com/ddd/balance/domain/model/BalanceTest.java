package com.ddd.balance.domain.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class BalanceTest {

    @Test
    public void given_balance_when_withdraw_happy_path_then_Ok() {

        //Given
        Customer customer = new Customer(1L, "Juan Antonio", "Breña Moral", "50401080H");
        BigDecimal currentBalance = new BigDecimal("100.0");
        Balance balance = new Balance(1L, currentBalance, customer.id(), null, null);

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
        Balance balance = new Balance(1L, currentBalance, customer.id(), null, null);

        //When
        BigDecimal amount = new BigDecimal("101.00");
        Optional<Balance> newBalance = balance.withdraw(amount);

        //Then
        then(newBalance.isPresent()).isFalse();
    }

    //TODO It is necessary to mock time internally in Balance Object
    @Disabled
    @Test
    public void given_balance_when_withdraw_multiple_times_in_last_hour_then_Ko() {

        //Given

        //First Withdraw
        Customer customer = new Customer(1L, "Juan Antonio", "Breña Moral", "50401080H");
        BigDecimal currentBalance = new BigDecimal("100.0");

        Timestamp creationTs = Timestamp.from(Instant.now().minus(30, ChronoUnit.MINUTES));
        Balance balance = new Balance(1L, currentBalance, customer.id(), creationTs, null);

        BigDecimal amount = new BigDecimal("10.00");
        Optional<Balance> newBalance = balance.withdraw(amount);
        Timestamp ts1 = newBalance.get().lastUpdate();

        //When

        //Second Withdraw
        BigDecimal amount2 = new BigDecimal("10.00");
        Optional<Balance> newBalance2 = newBalance.get().withdraw(amount2);
        Timestamp ts2 = newBalance2.get().lastUpdate();

        //Then
        then(newBalance2.isPresent()).isFalse();
        then(ts1).isEqualTo(ts2);
    }

    @Test
    public void given_balance_when_withdraw_without_limit_then_Ko() {

        //Given
        Customer customer = new Customer(1L, "Juan Antonio", "Breña Moral", "50401080H");
        BigDecimal currentBalance = new BigDecimal("100.0");
        Balance balance = new Balance(1L, currentBalance, customer.id(), null, null);

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
    public void given_balance_when_withdraw_with_limit_then_Ko() {

        //Given
        Customer customer = new Customer(1L, "Juan Antonio", "Breña Moral", "50401080H");
        BigDecimal currentBalance = new BigDecimal("100.0");
        BigDecimal limit = new BigDecimal("10.00");
        Balance balance = new Balance(1L, currentBalance, customer.id(), null, limit);

        //When
        BigDecimal amount = new BigDecimal("11.00");
        Optional<Balance> newBalance = balance.withdraw(amount);

        //Then
        then(newBalance.isPresent()).isFalse();
    }

    @Test
    public void given_balance_when_set_limit_first_time_then_Ok() {

        //Given
        Customer customer = new Customer(1L, "Juan Antonio", "Breña Moral", "50401080H");
        BigDecimal currentBalance = new BigDecimal("100.0");
        Balance balance = new Balance(1L, currentBalance, customer.id(), null, null);

        //When
        BigDecimal limit = new BigDecimal("50.00");
        Optional<Balance> newBalance = balance.setLimit(limit);

        //Then
        then(newBalance.isPresent()).isFalse();
    }

}
