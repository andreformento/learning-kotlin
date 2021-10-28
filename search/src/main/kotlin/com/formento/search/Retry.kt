package com.formento.search

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration

class Retry(
    private val sleepTime: Duration,
    private val tryAtLeast: Int,
) {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Retry::class.java)
    }

    fun tryToRun(tryFunction: () -> Boolean): Boolean = tryToRun(tryAtLeast, tryFunction)

    private fun tryToRun(attemptCount: Int, tryFunction: () -> Boolean): Boolean =
        if (attemptCount <= 0) {
            false
        } else {
            try {
                if (tryFunction()) {
                    true
                } else {
                    tryToRun(attemptCount - 1, tryFunction)
                }
            } catch (e: Exception) {
                LOGGER.error("Retry $attemptCount to run check", e)
                Thread.sleep(sleepTime.toMillis())
                tryToRun(attemptCount - 1, tryFunction)
            }
        }

}
