
package com.financial.funds.transfer.service

import com.financial.funds.transfer.model.Account
import com.financial.funds.transfer.repository.AccountRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Optional.empty
import java.util.Optional.of
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TransferServiceTest {

    private lateinit var repo: AccountRepository
    private lateinit var exchangeService: ExchangeService
    private lateinit var service: TransferService
    private val accounts = ConcurrentHashMap<Long, AtomicReference<Account>>()

    private val fromAccount = Account(1L, 123, "EUR", BigDecimal("1000.00"), 0)
    private val toAccount = Account(2L, 456, "USD", BigDecimal("500.00"), 0)

    @BeforeEach
    fun setup() {
        repo = mockk(relaxed = true)
        exchangeService = mockk()
        service = TransferService(repo, exchangeService)

        val from = Account(1L, 100, "EUR", BigDecimal("1000.00"), 0)
        val to = Account(2L, 200, "USD", BigDecimal("500.00"), 0)

        accounts[1L] = AtomicReference(from)
        accounts[2L] = AtomicReference(to)

        every { repo.findById(1L) } answers { of(accounts[1L]!!.get().copy()) }
        every { repo.findById(2L) } answers { of(accounts[2L]!!.get().copy()) }

        every { repo.save(any()) } answers {
            val acc = firstArg<Account>()
            accounts[acc.id]?.set(acc)
            acc
        }

        coEvery { exchangeService.getRate("EUR", "USD") } returns BigDecimal("1.10")
    }

    @Test
    fun `should transfer money correctly`() = runBlocking {
        service.doTransfer(1L, 2L, BigDecimal("10.00"), "EUR")

        assertEquals(BigDecimal("990.00"), accounts[1L]!!.get().balance)
        assertEquals(BigDecimal("511.00"), accounts[2L]!!.get().balance)
    }

    @Test
    fun `should throw when from account not found`() {
        runBlocking {
            every { repo.findById(1L) } returns empty()

            assertFailsWith<IllegalArgumentException> {
                service.transfer(1L, 2L, BigDecimal("100.00"), "EUR")
            }
        }
    }

    @Test
    fun `should throw when to account not found`() {
        runBlocking {
            every { repo.findById(1L) } returns of(fromAccount)
            every { repo.findById(2L) } returns empty()

            assertFailsWith<IllegalArgumentException> {
                service.transfer(1L, 2L, BigDecimal("100.00"), "EUR")
            }
        }
    }

    @Test
    fun `should round converted amount correctly`() = runBlocking {
        every { repo.findById(1L) } returns of(fromAccount.copy())
        every { repo.findById(2L) } returns of(toAccount.copy())
        coEvery { exchangeService.getRate("EUR", "USD") } returns BigDecimal("1.105")
        every { repo.save(any()) } answers { firstArg() }

        val result = service.transfer(1L, 2L, BigDecimal("100.00"), "EUR")

        assertEquals(BigDecimal("1.105"), result.rate)
        assertEquals(BigDecimal("900.00"), result.fromAmount)
        assertEquals(BigDecimal("610.50"), result.toAmount)
    }
}