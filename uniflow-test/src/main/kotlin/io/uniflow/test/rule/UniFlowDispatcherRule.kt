package io.uniflow.test.rule

import io.uniflow.core.dispatcher.DefaultDispatchers
import io.uniflow.core.dispatcher.TestDispatchers
import io.uniflow.core.dispatcher.UniFlowDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class UniFlowDispatcherRule : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        UniFlowDispatcher.dispatcher = TestDispatchers()
    }

    override fun finished(description: Description?) {
        super.finished(description)
        UniFlowDispatcher.dispatcher = DefaultDispatchers()
    }
}