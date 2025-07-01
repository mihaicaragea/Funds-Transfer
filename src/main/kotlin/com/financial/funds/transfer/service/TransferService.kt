package com.financial.funds.transfer.service

import com.financial.funds.transfer.repository.AccountRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransferService {

    @Autowired
    private lateinit var repo: AccountRepository

     fun transfer(fromId: Long, toId: Long, amount: BigDecimal) {
            performTransferWithRetry(fromId, toId, amount)
        }

        private fun performTransferWithRetry(fromId: Long, toId: Long, amount: BigDecimal, maxRetries: Int = 0) {
            var attempts = 0
            while (true) {
                try {
                    doTransfer(fromId, toId, amount)
                    return
                } catch (e: OptimisticLockingFailureException) {
                    if (++attempts >= maxRetries) throw e
                    Thread.sleep(50)
                }
            }
        }

        @Transactional
        fun doTransfer(fromId: Long, toId: Long, amount: BigDecimal) {
            val from = repo.findById(fromId).orElseThrow { IllegalArgumentException("From not found") }
            val to = repo.findById(toId).orElseThrow { IllegalArgumentException("To not found") }

            if (from.balance < amount) throw IllegalArgumentException("Insufficient funds")

            val rate = BigDecimal.TEN
            val converted = amount.multiply(rate).setScale(2)

            from.balance = from.balance.subtract(amount)
            to.balance = to.balance.add(converted)

            repo.save(from)
            repo.save(to)
        }
}