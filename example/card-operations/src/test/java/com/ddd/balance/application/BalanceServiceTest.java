package com.ddd.balance.application;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import com.ddd.balance.domain.model.Balance;
import com.ddd.balance.domain.service.BalanceRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class BalanceServiceTest {

    @MockBean
    private BalanceRepository balanceRepository;

    @Autowired
    private BalanceService balanceService;

    @Test
    public void given_service_when_withdraw_then_Ok() {

        //Given
        BigDecimal current = new BigDecimal("100.0");
        BigDecimal amount = new BigDecimal("10.0");
        Balance mockedBalance = new Balance(1L, current, 1L, null);
        Mockito.when(balanceRepository.findById(any())).thenReturn(Optional.of(mockedBalance));
        Mockito.when(balanceRepository.save(any())).thenReturn(new Balance(1L, current.subtract(amount), 1L, Timestamp.from(Instant.now())));

        //When
        Optional<Balance> newBalance = balanceService.witdhdraw(1L, amount);

        //Then
        then(newBalance.isPresent()).isTrue();
        then(newBalance.get().balance()).isEqualTo(current.subtract(amount));
    }

    @Test
    public void given_service_when_withdraw_if_not_enough_then_Ko() {

        //Given
        BigDecimal current = new BigDecimal("0.0");
        BigDecimal amount = new BigDecimal("10.0");
        Balance mockedBalance = new Balance(1L, current, 1L, null);
        Mockito.when(balanceRepository.findById(any())).thenReturn(Optional.of(mockedBalance));

        //When
        Optional<Balance> newBalance = balanceService.witdhdraw(1L, amount);

        //Then
        then(newBalance.isPresent()).isFalse();
    }

}
