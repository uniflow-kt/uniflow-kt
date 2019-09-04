package io.uniflow.core.result

import io.uniflow.core.flow.UIState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SuccessResultTest {

    val value = "MY VALUE"

    @Test
    fun `create result`() {
        val result = value.asSafeResult()
        assertTrue(result.get() == value)

        assertTrue(result.isSuccess())
        assertTrue(!result.isError())
    }

    @Test
    fun `map result`() = runBlocking {
        val sndValue = " #2"
        val result = value.asSafeResult()
                .map { value -> value + sndValue }
        assertTrue(result.get() == value + sndValue)
    }

    @Test
    fun `flatmap result`() = runBlocking {
        val sndValue = 42
        val result = value.asSafeResult()
                .flatMap { value -> sndValue.asSafeResult() }
        assertTrue(result.get() == sndValue)
    }

    @Test
    fun `onValue`() = runBlocking {
        var writtenValue = ""
        value.asSafeResult()
                .onValue { writtenValue = "$it" }

        assertTrue(writtenValue == value)
    }

    @Test
    fun `onError`() = runBlocking {
        var writtenValue = ""
        value.asSafeResult()
                .onError { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `map State`() = runBlocking {
        val result = value.asSafeResult()
                .map { UIState.Success }

        assertTrue(result.get() == UIState.Success)
    }
}