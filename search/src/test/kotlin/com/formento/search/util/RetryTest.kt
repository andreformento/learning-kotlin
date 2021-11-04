package com.formento.search.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration

internal class RetryTest {

    @Test
    fun `should run when everything its ok`() {
        assertThat(Retry(sleepTime = Duration.ZERO, tryAtLeast = 1).tryToRun { true }).isTrue
    }

    @Test
    fun `should run on the second time when is not ok on the first`() {
        var attemptCount = 0

        val function: () -> Boolean = { ++attemptCount == 2 }

        assertThat(Retry(sleepTime = Duration.ZERO, tryAtLeast = 2).tryToRun(function)).isTrue
    }

    @Test
    fun `should run on the second time when to fail on the first`() {
        var attemptCount = 0

        val function: () -> Boolean = {
            if (++attemptCount == 2) {
                true
            } else {
                throw Throwable("fail on first time")
            }
        }

        assertThat(Retry(sleepTime = Duration.ZERO, tryAtLeast = 2).tryToRun(function)).isTrue
    }

    @Test
    fun `should not run on the second time when to fail on the first`() {
        val function: () -> Boolean = {
            throw Throwable("fail on first time")
        }

        assertThat(Retry(sleepTime = Duration.ZERO, tryAtLeast = 2).tryToRun(function)).isFalse
    }

    @Test
    fun `should fail on the second time and try only one time`() {
        var attemptCount = 0

        val function: () -> Boolean = { ++attemptCount == 2 }

        assertThat(Retry(sleepTime = Duration.ZERO, tryAtLeast = 1).tryToRun(function)).isFalse
    }

}
