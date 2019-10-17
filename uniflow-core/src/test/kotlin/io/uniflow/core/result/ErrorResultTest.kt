package io.uniflow.core.result

import arrow.core.*
import io.uniflow.core.flow.UIState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorResultTest {

    val error = IllegalStateException("My Error")

    @Test
    fun `create result`() {
        val result = error.left()
        assertTrue(result.orNull() == null)

        assertTrue(!result.isRight())
        assertTrue(result.isLeft())
    }

    @Test
    fun `map result`() = runBlocking {
        val sndValue = " #2"
        val result = Left(error)
                .map { sndValue }
        assertTrue(result.orNull() == null)
    }

    @Test
    fun `orElse result`() = runBlocking {
        val sndValue = " #2"
        val result = safeCall<String> { throw error }.getOrElse { sndValue }

        assertTrue(result == sndValue)
    }

    @Test
    fun `flatmap result`() = runBlocking {
        val sndValue = 42
        val result = error.left()
                .flatMap { value -> sndValue.right() }
        assertTrue(result.orNull() == null)
    }

    @Test
    fun `onValue`() = runBlocking {
        var writtenValue = ""

        error.left().onSuccess { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `onError`() = runBlocking {
        var writtenValue = ""
        error.left().onFailure { writtenValue = "$it" }

        assertTrue(writtenValue == "$error")
    }

//    @Test
//    fun `map State`() = runBlocking {
//        val result = errorResult(error)
//                .mapState({ UIState.Success }, { UIState.Failed(error = it) })
//
//        assertTrue(result.get() == UIState.Failed(error = error))
//    }
//
//    @Test
//    fun `map State null`() = runBlocking {
//        val result = errorResult(error)
//                .mapState({ UIState.Success }, { null })
//
//        assertTrue(result.getOrNull() == null)
//    }

    @Test
    fun `to State null`() = runBlocking {
        val result = error.left()
                .toStateOrNull { UIState.Success }

        assertTrue(result == null)
    }

//    @Test
//    fun `to State`() = runBlocking {
//        val result = errorResult(error)
//                .toState({ UIState.Success }, { UIState.Failed(error = it) })
//
//        assertTrue(result == UIState.Failed(error = error))
//    }
}