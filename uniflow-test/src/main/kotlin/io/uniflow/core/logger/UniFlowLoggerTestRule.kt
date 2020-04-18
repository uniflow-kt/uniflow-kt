package io.uniflow.core.logger

import org.junit.ClassRule
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * If used as a [@Rule][Rule], then this rule ensures that [UniFlowLogger] is reset to the default
 * [Logger] after a test function.
 * If used as a [@ClassRule][ClassRule], then this is ensured after a test class.
 */
class UniFlowLoggerTestRule : TestWatcher() {
    override fun finished(description: Description?) = UniFlowLogger.default()
}
