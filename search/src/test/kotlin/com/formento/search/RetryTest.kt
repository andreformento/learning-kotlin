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
//        var secondTime = false
//
//        val function: () -> Boolean = {
//            val result = secondTime
//            secondTime = true
//            result
//        }

        var attemptCount = 0

        val function: () -> Boolean = { ++attemptCount == 1 }

        assertThat(Retry(sleepTime = Duration.ZERO, tryAtLeast = 1).tryToRun(function)).isTrue
    }

}
