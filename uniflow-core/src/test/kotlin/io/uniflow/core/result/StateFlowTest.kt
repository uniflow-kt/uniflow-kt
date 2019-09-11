package io.uniflow.core.result

import io.uniflow.core.flow.UIState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class StateFlowTest {

    @Test
    fun `test flow`() = runBlocking {

        val value = safeValue { throw RuntimeException("boom") }
                .onFailure { System.err.println("error -> $it") }
                .toState(
                        { UIState.Success },
                        { UIState.Failed(error = it) })

        assertTrue(value is UIState.Failed)

    }
}