package io.uniflow.core.result

import arrow.core.flatMap
import arrow.core.right
import io.uniflow.core.flow.UIState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SuccessResultTest {

    val value = "MY VALUE"

    @Test
    fun `create result`() {
        val result = value.right()
        assertTrue(result.get() == value)

        assertTrue(result.isRight())
        assertTrue(!result.isLeft())
    }

    @Test
    fun `map result`() = runBlocking {
        val sndValue = " #2"
        val result = value.right()
                .map { value -> value + sndValue }
        assertTrue(result.get() == value + sndValue)
    }

    @Test
    fun `flatmap result`() = runBlocking {
        val sndValue = 42
        val result = value.right()
                .flatMap { value -> sndValue.right() }
        assertTrue(result.get() == sndValue)
    }

    @Test
    fun `onValue`() = runBlocking {
        var writtenValue = ""
        value.right().onSuccess { writtenValue = "$it" }

        assertTrue(writtenValue == value)
    }

    @Test
    fun `onError`() = runBlocking {
        var writtenValue = ""
        value.right().onFailure { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `map State`() = runBlocking {
        val result = value.right()
                .map { UIState.Success }

        assertTrue(result.get() == UIState.Success)
    }

    @Test
    fun `to State or null`() = runBlocking {
        val result = value.right()
                .toStateOrNull { UIState.Success }

        assertTrue(result == UIState.Success)
    }
}