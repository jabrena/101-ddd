package com.ddd.balance.infrastructure.rest;

import com.ddd.balance.application.BalanceService;
import com.ddd.balance.domain.model.Balance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
public class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BalanceService balanceService;

    @Test
    public void given_balanceController_when_withdraw_happy_path_Ok() throws Exception {

        //Given
        BigDecimal request = new BigDecimal("10.00");
        BigDecimal response = new BigDecimal("90.00");
        when(balanceService.witdhdraw(any(), eq(request)))
                .thenReturn(Optional.of(new Balance(1l, response, 1l,
                                Timestamp.from(Instant.now()), null)));

        //When
        //Then
        WithdrawRequest widthDrawRequest = new WithdrawRequest(1L, request);
        WithdrawResponse expectedResponse = new WithdrawResponse(true);
        this.mockMvc.perform(post("/api/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(widthDrawRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expectedResponse)));
    }

    @Test
    public void given_balanceController_when_withdrawLimit_happy_path_Ok() throws Exception {

        //Given
        BigDecimal balance = new BigDecimal("100.00");
        BigDecimal limit = new BigDecimal("10.00");
        when(balanceService.witdhdrawLimit(any(), eq(limit)))
                .thenReturn(Optional.of(new Balance(1l, balance, 1l,
                        Timestamp.from(Instant.now()), limit)));

        //When
        //Then
        WithdrawLimitRequest widthLimitDrawRequest = new WithdrawLimitRequest(1L, limit);
        WithdrawLimitResponse expectedResponse = new WithdrawLimitResponse(true);
        this.mockMvc.perform(post("/api/withdrawlimit")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(widthLimitDrawRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expectedResponse)));
    }

    @Test
    public void given_balanceController_when_repay_happy_path_Ok() throws Exception {

        //Given
        BigDecimal currentBalance = new BigDecimal("100.00");
        BigDecimal amount = new BigDecimal("10.00");
        when(balanceService.repay(any(), eq(amount)))
                .thenReturn(Optional.of(new Balance(1l, currentBalance.add(amount), 1l,
                        Timestamp.from(Instant.now()), null)));

        //When
        //Then
        RepayRequest repayRequest = new RepayRequest(1L, amount);
        RepayResponse expectedResponse = new RepayResponse(true);
        this.mockMvc.perform(post("/api/repay")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(repayRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expectedResponse)));
    }

    public static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
