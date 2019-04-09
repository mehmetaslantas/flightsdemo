package com.mehmetaslantas.flightdemo;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlightdemoApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Before
	public void setUp() {
		webTestClient = webTestClient
				.mutate()
				.responseTimeout(Duration.ofMillis(36000))
				.build();
	}

	@Test
	public void getFlights() {
		webTestClient.get()
				.uri("/api/flights")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.consumeWith(response ->
						Assertions.assertThat(response.getResponseBody()).isNotNull());
	}
}
