package io.uniflow.core.result

import io.uniflow.core.flow.UIState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class StateFlowTest {

    @Test
    fun `test flow`() = runBlocking {

        val value = safeCall { throw RuntimeException("boom") }
                .orElse { errorResult(IllegalStateException("ISE Boom")) }
                .onError { System.err.println("error -> $it") }
                .toState(
                        { UIState.Success },
                        { UIState.Failed(error = it) })

        assertTrue(value is UIState.Failed)

    }
}