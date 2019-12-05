package io.uniflow.result

import io.uniflow.core.flow.UIState
import io.uniflow.result.SafeResult.Companion.safeResult
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

    @Test
    fun `map result`() = runBlocking {
        val sndValue = " #2"
        val result = SafeResult.Failure(error)
                .map { sndValue }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `orElse result`() = runBlocking {
        val sndValue = " #2"
        val result =
                safeResult<String> { throw error }
                        .orElse { sndValue.success() }
        assertTrue(result.get() == sndValue)
    }

    @Test
    fun `flatmap result`() = runBlocking {
        val sndValue = 42
        val result = SafeResult.Failure(error)
                .flatMap { value -> sndValue.success() }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `onValue`() = runBlocking {
        var writtenValue = ""
        SafeResult.Failure(error)
                .onSuccess { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `onError`() = runBlocking {
        var writtenValue = ""
        SafeResult.Failure(error)
                .onFailure { writtenValue = "$it" }

        assertTrue(writtenValue == "$error")
    }

    @Test
    fun `to State null`() = runBlocking {
        val result = SafeResult.Failure(error)
                .toStateOrNull { UIState.Success }

        assertTrue(result == null)
    }
}