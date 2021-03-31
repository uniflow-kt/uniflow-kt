package io.uniflow.android.test.sync

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.uniflow.android.test.TestViewObserver
import io.uniflow.android.test.createTestObserver
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.DebugMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.test.rule.DefaultTestDispatchersRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SyncFlowTest {
    companion object {
        init {
            UniFlowLogger.init(DebugMessageLogger())
        }
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    val td = DefaultTestDispatchersRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var dataFlow: SyncFlow
    lateinit var tester: TestViewObserver

    @Before
    fun before() {
        dataFlow = SyncFlow()
        tester = dataFlow.createTestObserver()
    }

    @Test
    fun `init state`(){
        assert(dataFlow.getState() == UIState.Empty)
    }

    @Test
    fun `actions are done in orders`() = runBlocking {
        dataFlow.action2()
        dataFlow.action1()
        dataFlow.action3()

        delay(50)

        tester.verifySequence(
            CountState(2),
            CountState(1),
            CountState(3)
        )
    }

    @Test
    fun `actions List`() = runBlocking {
        dataFlow.actionList()

        delay(20)

        tester.verifySequence(
            CountState(1),
            CountState(2),
            CountState(3)
        )
    }

    @Test
    fun `actions error order`() = runBlocking {
        dataFlow.actionBoom()
        dataFlow.action1()

        delay(20)

        tester.verifySequence(
            UIState.Failed(error = IllegalStateException("boom")),
            CountState(1)
        )
    }
}