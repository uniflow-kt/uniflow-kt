package io.uniflow.core.result

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class EmptyResultTest {

    @Test
    fun `create result`() {
        val value = emptyResult()
        assertTrue(value.getOrNull() == null)

        assertTrue(value.isEmpty())
        assertTrue(!value.isSuccess())
        assertTrue(!value.isError())
    }

    @Test
    fun `map result`() = runBlocking {
        val result = emptyResult()
                .map { "42" }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `orElse result`() = runBlocking {
        val result = safeCall { "" }
                .flatMap<String> { emptyResult() }
                .orElse { "42".asSafeResult() }
        assertTrue(result.get() == "42")
    }

    @Test
    fun `flatmap result`() = runBlocking {
        val sndValue = 42
        val result = emptyResult()
                .flatMap { value -> sndValue.asSafeResult() }
        assertTrue(result.getOrNull() == null)
    }

    @Test
    fun `onValue`() = runBlocking {
        var writtenValue = ""
        emptyResult()
                .onValue { writtenValue = "42" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `onError`() = runBlocking {
        var writtenValue = ""
        emptyResult()
                .onError { writtenValue = "$it" }

        assertTrue(writtenValue == "")
    }

    @Test
    fun `onEmpty`() = runBlocking {
        var writtenValue = ""
        emptyResult()
                .onEmpty { writtenValue = "42" }

        assertTrue(writtenValue == "42")
    }
//
////    @Test
////    fun `map State`() = runBlocking {
////        val result = errorResult(error)
////                .mapState({ UIState.Success }, { UIState.Failed(error = it) })
////
////        assertTrue(result.get() == UIState.Failed(error = error))
////    }
////
////    @Test
////    fun `map State null`() = runBlocking {
////        val result = errorResult(error)
////                .mapState({ UIState.Success }, { null })
////
////        assertTrue(result.getOrNull() == null)
////    }
//
//    @Test
//    fun `to State null`() = runBlocking {
//        val result = errorResult(error)
//                .toStateOrNull { UIState.Success }
//
//        assertTrue(result == null)
//    }
//
////    @Test
////    fun `to State`() = runBlocking {
////        val result = errorResult(error)
////                .toState({ UIState.Success }, { UIState.Failed(error = it) })
////
////        assertTrue(result == UIState.Failed(error = error))
////    }
}