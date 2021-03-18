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
 * This rule [sets][setMain] the [`Main`][Dispatchers.Main] coroutine dispatcher before tests.
 * Any coroutines running on [testCoroutineDispatcher] are
 * [cleaned up][kotlinx.coroutines.test.TestCoroutineDispatcher.cleanupTestCoroutines] after tests,
 * after which the main dispatcher is [reset][resetMain].
 *
 * set UniFlowDispatcher.dispatcher = testDispatchers to run
 *
 * @param testCoroutineDispatcher The [TestCoroutineDispatcher] used to replace the coroutine
 * dispatchers used by UniFlow.
 * Defaults to `TestCoroutineDispatcher()`.
 */
@ExperimentalCoroutinesApi
class UniflowTestDispatchersRule(
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
