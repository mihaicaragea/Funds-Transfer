package com.financial.funds.transfer.service

import com.financial.funds.transfer.config.ExchangeApiProperties
import com.financial.funds.transfer.model.ExchangeRatesResponse
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal

@Service
class ExchangeService(
    private val props: ExchangeApiProperties
) {

    private val client = WebClient.create(props.baseUrl)

    suspend fun getRate(fromCurrency: String, toCurrency: String): BigDecimal {
        val response = client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("access_key", props.accessKey)
                    .queryParam("base", fromCurrency)
                    .queryParam("to", toCurrency)
                    .build()
            }
            .retrieve()
            .bodyToMono(ExchangeRatesResponse::class.java)
            .awaitSingle()

        val fromRate = response.rates[toCurrency]
            ?: throw IllegalStateException("Missing rate for $toCurrency")

        return BigDecimal.valueOf(fromRate)
    }
}