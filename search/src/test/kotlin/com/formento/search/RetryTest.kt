package com.formento.search

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration

internal class RetryTest {
    @Test
    fun shouldRunWhenEverythingItsOk() {
        assertThat(Retry(sleepTime = Duration.ZERO, tryAtLeast = 1).tryToRun { true }).isTrue
    }

    @Test
    fun shouldRunOnTheSecondTime() {
        var attemptCount = 0

//        val function = () -> {
//            attemptCount == 1
//        }
        // TODO
        assertThat(Retry(sleepTime = Duration.ZERO, tryAtLeast = 1).tryToRun { true }).isTrue
    }
}
