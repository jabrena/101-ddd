package com.ddd.balance.application;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.domain.service.BalanceRepository;

import com.ddd.statements.application.BalanceEventListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest()
public class BalanceServiceTest {

    //A way to disable Event processing from Statements
    @MockBean
    private BalanceEventListener balanceEventListener;

    @MockBean
    private BalanceRepository balanceRepository;

    @Autowired
    private BalanceService balanceService;

    @Test
    public void given_service_when_withdraw_then_Ok() {

        //Given
        BigDecimal current = new BigDecimal("100.0");
        BigDecimal amount = new BigDecimal("10.0");
        Balance mockedBalance = new Balance(1L, current, 1L, null, null);
        Mockito.when(balanceRepository.findById(any())).thenReturn(Optional.of(mockedBalance));
        Mockito.when(balanceRepository.save(any())).thenReturn(new Balance(1L, current.subtract(amount), 1L, Timestamp.from(Instant.now()), null));

        //When
        Optional<Balance> newBalance = balanceService.witdhdraw(1L, amount);

        //Then
        then(newBalance.isPresent()).isTrue();
        then(newBalance.get().balance()).isEqualTo(current.subtract(amount));
    }

    @Test
    public void given_service_when_withdraw_and_not_found_customer_then_Ok() {

        //Given
        Long idCustomer = 99L;
        BigDecimal amount = new BigDecimal("10.0");
        Mockito.when(balanceRepository.findById(any())).thenReturn(Optional.empty());

        //When
        Optional<Balance> newBalance = balanceService.witdhdraw(1L, amount);

        //Then
        then(newBalance.isPresent()).isFalse();
    }

    @Test
    public void given_service_when_withdraw_if_not_enough_then_Ko() {

        //Given
        BigDecimal current = new BigDecimal("0.0");
        BigDecimal amount = new BigDecimal("10.0");
        Balance mockedBalance = new Balance(1L, current, 1L, null, null);
        Mockito.when(balanceRepository.findById(any())).thenReturn(Optional.of(mockedBalance));

        //When
        Optional<Balance> newBalance = balanceService.witdhdraw(1L, amount);

        //Then
        then(newBalance.isPresent()).isFalse();
    }


    @Test
    public void given_service_when_withdrawLimit_firstTime_then_Ok() {

        //Given
        BigDecimal current = new BigDecimal("100.0");
        BigDecimal withDrawlimit = new BigDecimal("10.0");
        Balance mockedBalance = new Balance(1L, current, 1L, null, null);
        Mockito.when(balanceRepository.findById(any())).thenReturn(Optional.of(mockedBalance));
        Mockito.when(balanceRepository.save(any())).thenReturn(new Balance(1L, current, 1L, Timestamp.from(Instant.now()), withDrawlimit));

        //When
        Optional<Balance> newBalance = balanceService.witdhdrawLimit(1L, withDrawlimit);

        //Then
        then(newBalance.isPresent()).isTrue();
        then(newBalance.get().withdrawLimit()).isEqualTo(withDrawlimit);
    }

    @Test
    public void given_service_when_withdrawLimit_and_not_found_customer_then_Ok() {

        //Given
        Long idCustomer = 99L;
        BigDecimal amount = new BigDecimal("10.0");
        Mockito.when(balanceRepository.findById(any())).thenReturn(Optional.empty());

        //When
        Optional<Balance> newBalance = balanceService.witdhdrawLimit(1L, amount);

        //Then
        then(newBalance.isPresent()).isFalse();
    }

    @Test
    public void given_service_when_repay_then_Ok() {

        //Given
        BigDecimal currentBalance = new BigDecimal("100.0");
        BigDecimal amount = new BigDecimal("10.0");
        Balance mockedBalance = new Balance(1L, currentBalance, 1L, null, null);
        Mockito.when(balanceRepository.findById(any())).thenReturn(Optional.of(mockedBalance));
        Mockito.when(balanceRepository.save(any())).thenReturn(new Balance(1L, currentBalance.add(amount), 1L, Timestamp.from(Instant.now()), null));

        //When
        Optional<Balance> newBalance = balanceService.repay(1L, amount);

        //Then
        then(newBalance.isPresent()).isTrue();
        then(newBalance.get().balance()).isEqualTo(currentBalance.add(amount));
    }

    @Test
    public void given_service_when_repay_and_not_found_customer_then_Ok() {

        //Given
        Long idCustomer = 99L;
        BigDecimal amount = new BigDecimal("10.0");
        Mockito.when(balanceRepository.findById(any())).thenReturn(Optional.empty());

        //When
        Optional<Balance> newBalance = balanceService.repay(1L, amount);

        //Then
        then(newBalance.isPresent()).isFalse();
    }
}
