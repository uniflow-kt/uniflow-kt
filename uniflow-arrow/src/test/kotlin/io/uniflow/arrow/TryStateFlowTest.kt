package io.uniflow.arrow

import arrow.core.Try
import io.uniflow.core.flow.data.UIState
import io.uniflow.result.onFailure
import io.uniflow.result.toState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class StateFlowTest {

    @Test
    fun `test flow`() = runBlocking {

        val value = Try { throw RuntimeException("boom") }
                .onFailure { System.err.println("error -> $it") }
                .toState(
                        { UIState.Success },
                        { UIState.Failed(error = it as Exception) })

        assertTrue(value is UIState.Failed)

    }
}