package io.uniflow.arrow

import arrow.core.*
import io.uniflow.core.flow.data.UIState
import io.uniflow.result.*
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
    fun `onValue`() = runBlocking {
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
        val result = Failure(error)
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