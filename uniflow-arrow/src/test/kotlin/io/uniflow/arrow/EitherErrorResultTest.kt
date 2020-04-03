package io.uniflow.arrow

import arrow.core.flatMap
import arrow.core.getOrHandle
import arrow.core.left
import arrow.core.right
import io.uniflow.core.flow.data.UIState
import io.uniflow.result.get
import io.uniflow.result.getOrNull
import io.uniflow.result.onFailure
import io.uniflow.result.onSuccess
import io.uniflow.result.toState
import io.uniflow.result.toStateOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class EitherErrorResultTest {

    val error = IllegalStateException("My Error")

    @Test
    fun `create result`() = runBlocking {
        val result = error.left()
        assertTrue(result.getOrNull() == null)

        assertTrue(!result.isRight())
        assertTrue(result.isLeft())
    }

    @Test(expected = IllegalStateException::class)
    fun `create mapped result`() {
        val result = error.left()
        result.get { "" }
    }

    @Test
    fun `map result`() = runBlocking {
        val sndValue = " #2"
        val result = error.left()
            .map { sndValue }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `orElse result`() = runBlocking {
        val sndValue = " #2"
        val result =
            error.left()
                .getOrHandle { sndValue.right() }
        assertTrue(result.get() == sndValue)
    }

    @Test
    fun `flatmap result`() = runBlocking {
        val sndValue = 42
        val result = error.left()
            .flatMap { sndValue.right() }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `onValue`() = runBlocking {
        var writtenValue = ""
        error.left()
            .onSuccess { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `onError`() = runBlocking {
        var writtenValue = ""
        error.left()
            .onFailure { writtenValue = "$it" }

        assertTrue(writtenValue == "$error")
    }

    @Test
    fun `to State null`() = runBlocking {
        val result = error.left()
            .toStateOrNull { UIState.Success }

        assertTrue(result == null)
    }

    @Test(expected = IllegalStateException::class)
    fun `to State`() = runBlocking {
        val result = error.left()
            .toState { UIState.Failed() }
    }
}
