package io.uniflow.android.test.sync

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.uniflow.android.test.TestViewObserver
import io.uniflow.android.test.createTestObserver
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.flow.getStateOrNull
import io.uniflow.core.logger.DebugMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.test.rule.DefaultTestDispatchersRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
        val randomDelayAction2 = dataFlow.getRandomDelay()
        val randomDelayAction1 = dataFlow.getRandomDelay()
        val randomDelayAction3 = dataFlow.getRandomDelay()
        dataFlow.action2(randomDelayAction2)
        dataFlow.action1(randomDelayAction1)
        dataFlow.action3(randomDelayAction3)

        delay(randomDelayAction2 + randomDelayAction1 + randomDelayAction3 + dataFlow.getRandomDelay())

        assertEquals(3,tester.statesCount)
    }

    @Test(expected= AssertionError::class)
    fun `incorrect size of state in verifySequence`() = runBlocking {
        dataFlow.actionList()

        delay(50)

        tester.verifySequence(
            CountState(2),
            CountState(1),
            CountState(3),
            CountState(3),
            CountState(3)
        )
    }

    @Test
    fun `actions List`() = runBlocking {
        dataFlow.actionList()

        delay(20)

        tester.verifySequence(
            UIState.Empty,
            CountState(1),
            CountState(2),
            CountState(3)
        )
    }

    @Test
    fun `actions error order`() = runBlocking {
        dataFlow.actionBoom()
        dataFlow.action1()

        delay(70)

        tester.verifySequence(
            UIState.Empty,
            UIState.Failed(error = IllegalStateException("boom")),
            CountState(1)
        )
    }

    @Test
    fun `notify state change`() = runBlocking {
        dataFlow.action1()
        dataFlow.clearState()

        delay(70)

        tester.verifySequence(
            UIState.Empty,
            CountState(1),
            ClearStateEvent
        )
        assertEquals(CountState(0),dataFlow.getStateOrNull<CountState>())
    }
}