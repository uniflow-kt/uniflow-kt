package io.uniflow.test.rule

import io.uniflow.core.dispatcher.ApplicationDispatchers
import io.uniflow.core.dispatcher.TestDispatchers
import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.dispatcher.UniFlowDispatcherConfiguration
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Setup Test Configuration Dispatcher
 */
class TestDispatchersRule(
    private val testDispatchers: UniFlowDispatcherConfiguration = TestDispatchers()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        UniFlowDispatcher.dispatcher = testDispatchers
    }

    override fun finished(description: Description?) {
        super.finished(description)
        UniFlowDispatcher.dispatcher = ApplicationDispatchers()
    }
}