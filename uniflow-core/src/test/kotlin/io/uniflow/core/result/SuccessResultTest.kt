package io.uniflow.core.result

import arrow.core.success
import io.uniflow.core.flow.UIState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SuccessResultTest {

    val value = "MY VALUE"

    @Test
    fun `create result`() {
        val result = value.success()
        assertTrue(result.get() == value)

        assertTrue(result.isSuccess())
        assertTrue(!result.isFailure())
    }

    @Test
    fun `map result`() = runBlocking {
        val sndValue = " #2"
        val result = value.success()
                .map { value -> value + sndValue }
        assertTrue(result.get() == value + sndValue)
    }

    @Test
    fun `flatmap result`() = runBlocking {
        val sndValue = 42
        val result = value.success()
                .flatMap { value -> sndValue.success() }
        assertTrue(result.get() == sndValue)
    }

    @Test
    fun `onValue`() = runBlocking {
        var writtenValue = ""
        value.success()
                .onSuccess { writtenValue = "$it" }

        assertTrue(writtenValue == value)
    }

    @Test
    fun `onError`() = runBlocking {
        var writtenValue = ""
        value.success()
                .onFailure { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `map State`() = runBlocking {
        val result = value.success()
                .map { UIState.Success }

        assertTrue(result.get() == UIState.Success)
    }

    @Test
    fun `to State or null`() = runBlocking {
        val result = value.success()
                .toStateOrNull { UIState.Success }

        assertTrue(result == UIState.Success)
    }
}