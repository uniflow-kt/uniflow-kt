package io.uniflow.arrow

import io.uniflow.core.flow.UIState
import io.uniflow.core.result.onFailure
import io.uniflow.core.result.safeValue
import io.uniflow.core.result.toState
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
                        { UIState.Failed(error = it as Exception) })

        assertTrue(value is UIState.Failed)

    }
}