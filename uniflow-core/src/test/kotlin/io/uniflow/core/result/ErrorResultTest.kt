package io.uniflow.core.result

import io.uniflow.core.flow.UIState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorResultTest {

    val error = IllegalStateException("My Error")

    @Test
    fun `create result`() {
        val result = errorResult(error)
        assertTrue(result.getOrNull() == null)

        assertTrue(!result.isSuccess())
        assertTrue(result.isError())
    }

    @Test
    fun `map result`() = runBlocking {
        val sndValue = " #2"
        val result = errorResult(error)
                .map { sndValue }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `flatmap result`() = runBlocking {
        val sndValue = 42
        val result = errorResult(error)
                .flatMap { value -> sndValue.asSafeResult() }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `onValue`() = runBlocking {
        var writtenValue = ""
        errorResult(error)
                .onValue { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `onError`() = runBlocking {
        var writtenValue = ""
        errorResult(error)
                .onError { writtenValue = "$it" }

        assertTrue(writtenValue == "$error")
    }

    @Test
    fun `map State`() = runBlocking {
        val result = errorResult(error)
                .mapState({ UIState.Success }, { UIState.Failed(error = it) })

        assertTrue(result.get() == UIState.Failed(error = error))
    }

    @Test
    fun `map State null`() = runBlocking {
        val result = errorResult(error)
                .mapState({ UIState.Success }, { null })

        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `to State null`() = runBlocking {
        val result = errorResult(error)
                .toStateOrNull{ UIState.Success }

        assertTrue(result == null)
    }

    @Test
    fun `to State`() = runBlocking {
        val result = errorResult(error)
                .toState({ UIState.Success }, { UIState.Failed(error = it) })

        assertTrue(result == UIState.Failed(error = error))
    }
}