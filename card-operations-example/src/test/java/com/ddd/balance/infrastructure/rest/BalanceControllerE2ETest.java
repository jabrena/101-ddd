package com.ddd.balance.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {"spring.datasource.data=classpath:/test_data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BalanceControllerE2ETest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void given_balanceController_when_withdraw_happy_path_Ok() {

		//Given
		String address = "http://localhost:" + port + "/api/withdraw";
		BigDecimal amount = new BigDecimal("10.00");
		WithdrawRequest widthDrawRequest = new WithdrawRequest(1L, amount);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<WithdrawRequest> request = new HttpEntity<>(widthDrawRequest, headers);

		//When
		ResponseEntity<String> result = this.restTemplate.postForEntity(address, request, String.class);

		//Then
		WithdrawResponse expectedResponse = new WithdrawResponse(true);
		then(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(result.getBody()).contains(asJsonString(expectedResponse));
	}

	@Test
	public void given_balanceController_when_repay_happy_path_Ok() {

		//Given
		String address = "http://localhost:" + port + "/api/repay";
		BigDecimal amount = new BigDecimal("10.00");
		WithdrawRequest widthDrawRequest = new WithdrawRequest(1L, amount);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<WithdrawRequest> request = new HttpEntity<>(widthDrawRequest, headers);

		//When
		ResponseEntity<String> result = this.restTemplate.postForEntity(address, request, String.class);

		//Then
		RepayResponse expectedResponse = new RepayResponse(true);
		then(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(result.getBody()).contains(asJsonString(expectedResponse));
	}

	public static String asJsonString(Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
