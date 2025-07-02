package com.financial.funds.transfer.controller

import com.financial.funds.transfer.model.ExchangeResponse
import com.financial.funds.transfer.model.TransferRequest
import com.financial.funds.transfer.service.TransferService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal

@WebFluxTest(TransferController::class)
@Import(TransferControllerTest.MockedServiceConfig::class)
class TransferControllerTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var transferService: TransferService

    @Test
    fun `should return successful exchange response`() {
        runBlocking {
            val request = TransferRequest(
                from = 1L,
                to = 2L,
                amount = BigDecimal("10.00"),
                fromCurrency = "EUR"
            )

            val response = ExchangeResponse(
                rate = BigDecimal("1.10"),
                from = 1L,
                to = 2L,
                fromCurrency = "EUR",
                toCurrency = "USD",
                fromAmount = BigDecimal("990.00"),
                toAmount = BigDecimal("511.00")
            )

            Mockito.`when`(
                transferService.transfer(
                    request.from,
                    request.to,
                    request.amount,
                    request.fromCurrency
                )
            ).thenReturn(response)

            webClient.post()
                .uri("/api/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.rate").isEqualTo(1.10)
                .jsonPath("$.from").isEqualTo(1)
                .jsonPath("$.to").isEqualTo(2)
                .jsonPath("$.fromAmount").isEqualTo(990.00)
        }
    }

    @TestConfiguration
    class MockedServiceConfig {
        @Bean
        fun transferService(): TransferService = Mockito.mock(TransferService::class.java)
    }
}