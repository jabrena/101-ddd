package com.ddd.balance.infrastructure.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {"spring.datasource.data=classpath:/test_data.sql"})
public class ReactiveBalanceControllerE2ETest {

	@LocalServerPort
	private int port;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void testCreateGithubRepository() {

		//Given
		String address = "http://localhost:" + port + "/api/withdraw";
		BigDecimal amount = new BigDecimal("10.00");
		WithdrawRequest widthDrawRequest = new WithdrawRequest(1L, amount);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<WithdrawRequest> request = new HttpEntity<>(widthDrawRequest, headers);

		webTestClient.post().uri(address)
				//.contentType(MediaType.APPLICATION_JSON)
				//.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(request), WithdrawRequest.class)
				.exchange()
				.expectStatus().isOk();
	}
}
