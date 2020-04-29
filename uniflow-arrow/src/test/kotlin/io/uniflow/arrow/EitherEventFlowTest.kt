package io.uniflow.arrow

import arrow.core.Either
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.result.onFailure
import io.uniflow.result.onSuccess
import io.uniflow.result.toEvent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class EitherEventFlowTest {

    @Test
    fun `error event test flow`() = runBlocking {

        val value = Either.catch<UIEvent> { throw RuntimeException("boom") }
                .onFailure { System.err.println("error -> $it") }
                .toEvent(
                        onSuccess = { UIEvent.Success },
                        onError = { UIEvent.Error(error = it) })

        assertTrue(value is UIEvent.Error)
    }

    @Test
    fun `success event test flow`() = runBlocking {

        val value = Either.catch { "Success" }
                .onSuccess { System.err.println("success -> $it") }
                .toEvent(
                        onSuccess = { UIEvent.Success },
                        onError = { UIEvent.Error(error = it) })

        assertTrue(value is UIEvent.Success)
    }
}
