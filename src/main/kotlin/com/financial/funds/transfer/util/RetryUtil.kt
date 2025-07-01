package com.financial.funds.transfer.util

import kotlinx.coroutines.delay

suspend fun <T> retrySuspend(
    maxRetries: Int = 3,
    delayMs: Long = 100,
    retryOn: List<Class<out Throwable>> = listOf(Exception::class.java),
    block: suspend () -> T
): T {
    repeat(maxRetries - 1) {
        try {
            return block()
        } catch (e: Throwable) {
            if (retryOn.any { it.isInstance(e) }) {
                delay(delayMs)
            } else {
                throw e
            }
        }
    }
    return block()
}