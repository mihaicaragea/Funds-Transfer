package com.financial.funds.transfer.model

import java.math.BigDecimal


data class TransferRequest(val from: Long, val to: Long, val amount: BigDecimal)