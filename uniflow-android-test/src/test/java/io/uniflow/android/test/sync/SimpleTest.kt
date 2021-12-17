package io.uniflow.android.test.sync

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.uniflow.android.AndroidDataFlow
import io.uniflow.android.test.createTestObserver
import io.uniflow.core.flow.data.UIState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SimpleTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun androidDataFlowEmitsDefaultState() {
        val androidDataFlow = object : AndroidDataFlow(defaultState = UIState.Success) {}
        val testObserver = androidDataFlow.createTestObserver()
        assertEquals(1, testObserver.statesCount)
    }
}