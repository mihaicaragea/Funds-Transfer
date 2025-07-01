package com.financial.funds.transfer.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "exchange.api")
class ExchangeApiProperties {
    var baseUrl: String = ""
    var accessKey: String = ""
}