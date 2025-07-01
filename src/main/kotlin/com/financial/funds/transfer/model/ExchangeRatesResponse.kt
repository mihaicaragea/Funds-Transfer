package com.financial.funds.transfer.model

data class ExchangeRatesResponse(
    val rates: Map<String, Double>,
    val base: String? = null,
    val date: String? = null
)