package io.uniflow.arrow

import arrow.core.Either
import io.uniflow.core.flow.data.UIState
import io.uniflow.result.onFailure
import io.uniflow.result.toState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class EitherStateFlowTest {

    @Test
    fun `test flow`() = runBlocking {

        val value = Either.catch { throw RuntimeException("boom") }
                .onFailure { System.err.println("error -> $it") }
                .toState(
                        { UIState.Success },
                        { UIState.Failed(error = it as Exception) })

        assertTrue(value is UIState.Failed)

    }
}
