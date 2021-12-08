package io.uniflow.android.test.sync

import io.uniflow.android.AndroidDataFlow
import io.uniflow.core.flow.actionOn
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.Logger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.test.rule.UniflowTestDispatchersRule
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OtherTests {
    companion object {
        private val testCoroutineDispatcher = TestCoroutineDispatcher()
    }

    @get:Rule
    val uniflowTestDispatchersRule = UniflowTestDispatchersRule(testCoroutineDispatcher)

    @Test
    fun `bad or wrong state is logged once`() {
        val logger = object : Logger {
            val messages = mutableListOf<String>()

            override fun debug(message: String) {
                println("[dbg] - $message")
            }

            override fun log(message: String) {
                println("[log] - $message")
            }

            override fun logError(errorMessage: String, error: Exception?) {
                messages += errorMessage
                println("[err] - $errorMessage")
            }

            override fun logEvent(event: UIEvent) {
            }

            override fun logState(state: UIState) {
            }
        }

        UniFlowLogger.init(logger)

        val androidDataFlow = object : AndroidDataFlow(defaultState = UIState.Empty) {
            fun foo() = actionOn<UIState.Loading> {}
        }

        androidDataFlow.foo() // Should log an uncaught error about a BadOrWrongStateException

        assertEquals(1, logger.messages.size)
        
        UniFlowLogger.default()
    }
}