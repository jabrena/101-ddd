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
    public void given_balanceController_when_happy_path_Ok() throws Exception {

        //Given
        BigDecimal request = new BigDecimal("10.00");
        BigDecimal response = new BigDecimal("90.00");
        when(balanceService.witdhdraw(any(), eq(request)))
                .thenReturn(Optional.of(new Balance(1l, response, 1l)));

        //When
        //Then
        WidthDrawRequest widthDrawRequest = new WidthDrawRequest(1L, request);
        WidthDrawResponse expectedResponse = new WidthDrawResponse(true);
        this.mockMvc.perform(post("/api/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(widthDrawRequest)))
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
