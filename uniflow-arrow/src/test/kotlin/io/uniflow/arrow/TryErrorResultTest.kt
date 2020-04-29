package io.uniflow.arrow

import arrow.core.Failure
import arrow.core.Try
import arrow.core.failure
import arrow.core.orElse
import arrow.core.success
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.result.get
import io.uniflow.result.getOrNull
import io.uniflow.result.onFailure
import io.uniflow.result.onSuccess
import io.uniflow.result.onValue
import io.uniflow.result.toEvent
import io.uniflow.result.toEventOrNull
import io.uniflow.result.toState
import io.uniflow.result.toStateOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorResultTest {

    val error = IllegalStateException("My Error")

    @Test
    fun `create result`() {
        val result = error.failure()
        assertTrue(result.getOrNull() == null)

        assertTrue(!result.isSuccess())
        assertTrue(result.isFailure())
    }

    @Test(expected = IllegalStateException::class)
    fun `create mapped result`() {
        val result = error.failure()
        result.get { "" }
    }

    @Test
    fun `map result`() = runBlocking {
        val sndValue = " #2"
        val result = Failure(error)
                .map { sndValue }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `orElse result`() = runBlocking {
        val sndValue = " #2"
        val result =
                Try<String> { throw error }
                        .orElse { sndValue.success() }
        assertTrue(result.get() == sndValue)
    }

    @Test
    fun `flatmap result`() = runBlocking {
        val sndValue = 42
        val result = Failure(error)
                .flatMap { value -> sndValue.success() }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `onSuccess`() = runBlocking {
        var writtenValue = ""
        Failure(error)
                .onSuccess { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `onError`() = runBlocking {
        var writtenValue = ""
        Failure(error)
                .onFailure { writtenValue = "$it" }

        assertTrue(writtenValue == "$error")
    }

    @Test
    fun `onValue`() {
        var writtenValue = ""
        Failure(error)
            .onValue { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `to State null`() = runBlocking {
        val result = error.failure()
            .toStateOrNull { UIState.Success }

        assertTrue(result == null)
    }

    @Test(expected = IllegalStateException::class)
    fun `to State`() = runBlocking {
        val result = error.failure()
            .toState { UIState.Failed() }
    }

    @Test
    fun `to Event null`() = runBlocking {
        val result = error.failure()
            .toEventOrNull { UIEvent.Error() }

        assertTrue(result == null)
    }

    @Test(expected = IllegalStateException::class)
    fun `to Event`() = runBlocking {
        val result = error.failure()
            .toEvent { UIEvent.Error() }
    }
}
