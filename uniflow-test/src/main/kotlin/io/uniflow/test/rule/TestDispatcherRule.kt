package io.uniflow.test.rule

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
 * @param testCoroutineDispatcher The [TestCoroutineDispatcher] used to replace the coroutine
 * dispatchers used by UniFlow.
 * Defaults to `TestCoroutineDispatcher()`.
 */
@ExperimentalCoroutinesApi
class TestDispatcherRule(
    val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {
    override fun starting(description: Description?) {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    override fun finished(description: Description?) {
        testCoroutineDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}
