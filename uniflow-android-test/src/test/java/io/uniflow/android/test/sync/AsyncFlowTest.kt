//package io.uniflow.android.test.sync
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import io.uniflow.android.stateflow.StateFlowPublisher
//import io.uniflow.android.stateflow.onStates
//import io.uniflow.android.test.TestViewObserver
//import io.uniflow.android.test.createTestObserver
//import io.uniflow.core.flow.data.UIState
//import io.uniflow.core.logger.DebugMessageLogger
//import io.uniflow.core.logger.SimpleMessageLogger
//import io.uniflow.core.logger.UniFlowLogger
//import io.uniflow.test.rule.TestDispatcherRule
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.runBlocking
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//class AsyncFlowTest {
//    companion object {
//        init {
//            UniFlowLogger.init(DebugMessageLogger())
//        }
//    }
//
//    @ExperimentalCoroutinesApi
//    @get:Rule
//    val td = TestDispatcherRule()
//
//    @get:Rule
//    val rule = InstantTaskExecutorRule()
//
//    lateinit var dataFlow: SyncFlow
//    lateinit var tester: TestViewObserver
//
//    @Before
//    fun before() {
//        dataFlow = SyncFlow()
//    }
//
//    @Test
//    fun `actions are done in orders`() = runBlocking {
//
//        (dataFlow.defaultDataPublisher as StateFlowPublisher).onStates(this){ state ->
//
//        }
//
//        dataFlow.action2()
//        dataFlow.action1()
//        dataFlow.action3()
//
//        while (tester.statesCount < 4) {
//            delay(25)
//        }
//
//        tester.verifySequence(
//            UIState.Empty,
//            CountState(2),
//            CountState(1),
//            CountState(3)
//        )
//    }
//}