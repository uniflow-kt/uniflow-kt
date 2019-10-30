package io.uniflow.result

import io.uniflow.core.flow.UIState
import io.uniflow.result.SafeResult.Companion.safeResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class StateFlowTest {

    @Test
    fun `test flow`() = runBlocking {

        val value = safeResult { throw RuntimeException("boom") }
                .onFailure { System.err.println("error -> $it") }
                .toState(
                        { UIState.Success },
                        { UIState.Failed(error = it) })

        assertTrue(value is UIState.Failed)

    }
}