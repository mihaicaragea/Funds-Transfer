package com.financial.funds.transfer.model
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Version
import java.math.BigDecimal


@Entity
data class Account(
    @Id val id: Long,
    val ownerId: Long,
    var currency: String,
    var balance: BigDecimal,
    @Version
    var version: Long? = null
)