package com.financial.funds.transfer

import com.financial.funds.transfer.config.ExchangeApiProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ExchangeApiProperties::class)
class FundsTransferApplication

fun main(args: Array<String>) {
	runApplication<FundsTransferApplication>(*args)
}
