package io.uniflow.test.rule

import io.uniflow.core.dispatcher.ApplicationDispatchers
import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.test.dispatcher.TestDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Setup Test Configuration Dispatcher
 */
@ExperimentalCoroutinesApi
class TestDispatchersRule(
    val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {
    private val testDispatchers: TestDispatchers = TestDispatchers(testCoroutineDispatcher)
    override fun starting(description: Description?) {
        Dispatchers.setMain(testCoroutineDispatcher)
        UniFlowDispatcher.dispatcher = testDispatchers
    }

    override fun finished(description: Description?) {
        testCoroutineDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
        UniFlowDispatcher.dispatcher = ApplicationDispatchers()
    }
}
