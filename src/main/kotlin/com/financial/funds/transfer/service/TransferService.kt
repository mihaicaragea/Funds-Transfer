package com.financial.funds.transfer.service

import com.financial.funds.transfer.model.ExchangeResponse
import com.financial.funds.transfer.repository.AccountRepository
import com.financial.funds.transfer.util.retrySuspend
import jakarta.transaction.Transactional
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class TransferService(val repo: AccountRepository,val clientService: ExchangeService) {

    suspend fun transfer(fromId: Long, toId: Long, amount: BigDecimal, fromCurrency: String): ExchangeResponse {
        return retrySuspend(
            maxRetries = 3,
            delayMs = 50,
            retryOn = listOf(OptimisticLockingFailureException::class.java)
        ) {
            doTransfer(fromId, toId, amount, fromCurrency)
        }
    }

    @Transactional
    suspend fun doTransfer(fromId: Long, toId: Long, amount: BigDecimal, fromCurrency: String): ExchangeResponse {
        val from = repo.findById(fromId).orElseThrow { IllegalArgumentException("From not found") }
        val to = repo.findById(toId).orElseThrow { IllegalArgumentException("To not found") }

        val rate = retrySuspend(maxRetries = 0, delayMs = 200) {
            clientService.getRate(fromCurrency, to.currency)
        }
        val converted = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP)

        from.balance = from.balance.subtract(amount)
        to.balance = to.balance.add(converted)

        val fromResponse = repo.save(from)
        val toResponse = repo.save(to)

        return ExchangeResponse(
            rate,
            from.id,
            to.id,
            fromCurrency,
            to.currency,
            fromResponse.balance,
            toResponse.balance
        )
    }
}