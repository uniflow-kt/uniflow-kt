package io.uniflow.android.test.sync

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.uniflow.android.test.TestViewObserver
import io.uniflow.android.test.createTestObserver
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.DebugMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.test.rule.UniflowTestDispatchersRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MultiFlowTest {
    companion object {
        init {
            UniFlowLogger.init(DebugMessageLogger())
        }
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    val td = UniflowTestDispatchersRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var dataFlow: MultiFlow
    lateinit var messageObserver: TestViewObserver
    lateinit var countObserver: TestViewObserver

    @Before
    fun before() {
        dataFlow = MultiFlow()
        messageObserver = dataFlow.messageDataFlow.createTestObserver()
        countObserver = dataFlow.counterDataFlow.createTestObserver()
    }

    @Test
    fun `actions are done in orders`() = runBlocking {
        val message = "test"
        dataFlow.countString(message)

        messageObserver.verifySequence(
            UIState.Empty,
            MessageState(message)
        )
        countObserver.verifySequence(
            UIState.Empty,
            CountState(message.length)
        )
    }
}