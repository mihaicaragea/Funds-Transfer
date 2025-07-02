package com.financial.funds.transfer

import com.financial.funds.transfer.model.Account
import com.financial.funds.transfer.repository.AccountRepository
import com.financial.funds.transfer.service.ExchangeService
import com.financial.funds.transfer.service.TransferService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.set
import kotlin.test.assertEquals

class TransferServiceConcurrencyTest {

    private lateinit var repo: AccountRepository
    private lateinit var exchangeService: ExchangeService
    private lateinit var service: TransferService

    private val accounts = ConcurrentHashMap<Long, AtomicReference<Account>>()

    @BeforeEach
    fun setup() {
        repo = mockk()
        exchangeService = mockk()
        service = TransferService(repo, exchangeService)

        val sharedFrom = Account(1L, 100, "EUR", BigDecimal("1000.00"), 0)
        val sharedTo = Account(2L, 200, "USD", BigDecimal("500.00"), 0)

        accounts[1L] = AtomicReference(sharedFrom)
        accounts[2L] = AtomicReference(sharedTo)

        every { repo.findById(1L) } answers {
            Optional.of(accounts[1L]!!.get().copy())
        }
        every { repo.findById(2L) } answers {
            Optional.of(accounts[2L]!!.get().copy())
        }

        every { repo.save(any()) } answers {
            val acc = it.invocation.args[0] as Account
            accounts[acc.id]?.set(acc)
            acc
        }

        coEvery { exchangeService.getRate(any(), any()) } returns BigDecimal("1.10")
    }

    @Test
    fun `concurrent transfers should not corrupt balances`() = runBlocking {
        val jobs = List(10) {
            launch {
                service.transfer(1L, 2L, BigDecimal("10.00"), "EUR")
            }
        }

        jobs.forEach { it.join() }

        val finalFromBalance = accounts[1L]?.get()?.balance
        val finalToBalance = accounts[2L]?.get()?.balance

        println("Final From Balance: $finalFromBalance")
        println("Final To Balance: $finalToBalance")

        assertEquals(BigDecimal("900.00"), finalFromBalance)
        assertEquals(BigDecimal("610.00"), finalToBalance)
    }
}
