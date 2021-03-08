package io.uniflow.android.test.sync

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.uniflow.android.test.TestViewObserver
import io.uniflow.android.test.createTestObserver
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.SimpleMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.test.rule.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SyncFlowTest {
    companion object {
        init {
            UniFlowLogger.init(SimpleMessageLogger(UniFlowLogger.FUN_TAG, showDebug = true))
        }
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    val td = TestDispatcherRule()

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
    fun `actions are done in orders`() = runBlocking {
        dataFlow.action2()
        dataFlow.action1()
        dataFlow.action3()

        while(tester.statesCount < 4){
            delay(25)
        }

        tester.verifySequence(
            UIState.Empty,
            CountState(2),
            CountState(1),
            CountState(3)
        )
    }
}