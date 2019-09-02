package io.uniflow.core.result

import io.uniflow.core.flow.UIState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ComplexFlowTest {

    @Test
    fun `test flow`() = runBlocking {

        val value = safeCall { throw RuntimeException("boom") }
                .mapError { IllegalStateException("ISE Boom") }
                .onError { System.err.println("error -> $it") }
                .mapState({ UIState.Success }, { UIState.Failed(error = it) })

        assertTrue(value.get() is UIState.Failed)

    }
}