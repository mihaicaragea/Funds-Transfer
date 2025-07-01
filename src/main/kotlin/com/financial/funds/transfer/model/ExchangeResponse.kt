package com.financial.funds.transfer.model

import java.math.BigDecimal

data class ExchangeResponse(
    val rate: BigDecimal,
    val from: Long,
    val to: Long,
    val fromCurrency: String,
    val toCurrency: String,
    val fromAmount: BigDecimal,
    val toAmount: BigDecimal
)