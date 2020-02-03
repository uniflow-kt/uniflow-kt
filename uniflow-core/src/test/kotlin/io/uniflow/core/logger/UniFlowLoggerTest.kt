package io.uniflow.core.logger

import io.mockk.*
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.rule.MockkTestClassRule
import io.uniflow.core.rule.MockkTestRule
import org.junit.*

class UniFlowLoggerTest {
    companion object {
        private val testLoggerMock = mockk<Logger>(name = "testLoggerMock", relaxUnitFun = true)

        private const val expectedMessage = "test message"
        private val expectedUIState = UIState()
        private val expectedUIEvent = UIEvent()
        private val expectedException = Exception("expected exception")

        @JvmStatic
        @get:ClassRule
        val mockkTestClassRule = MockkTestClassRule()

        @JvmStatic
        @BeforeClass
        fun `before class`() = mockkConstructor(SimpleMessageLogger::class)
    }

    @get:Rule
    val mockkTestRule = MockkTestRule()

    @get:Rule
    val uniFlowLoggerTestRule = UniFlowLoggerTestRule()

    @Before
    fun before() {
        // Suppress SimpleMessageLogger output
        every { anyConstructed<SimpleMessageLogger>().log(any()) } just runs
        every { anyConstructed<SimpleMessageLogger>().logState(any()) } just runs
        every { anyConstructed<SimpleMessageLogger>().logEvent(any()) } just runs
        every { anyConstructed<SimpleMessageLogger>().logError(any(), any()) } just runs
    }

    @Test
    fun `Given the default UniFlowLogger, When log() is called, Then Logger calls are delegated to SimpleMessageLogger`() {
        with(UniFlowLogger) {
            log(expectedMessage)
            logState(expectedUIState)
            logEvent(expectedUIEvent)
            logError(expectedMessage, expectedException)
        }

        verifySequence {
            with(anyConstructed<SimpleMessageLogger>()) {
                log(expectedMessage)
                logState(expectedUIState)
                logEvent(expectedUIEvent)
                logError(expectedMessage, expectedException)
            }
        }
    }

    @Test
    fun `Given UniFlowLogger with a custom Logger, When default() is called, Then Logger calls are delegated to SimpleMessageLogger`() {
        UniFlowLogger.init(testLoggerMock)

        with(UniFlowLogger) {
            default()

            log(expectedMessage)
            logState(expectedUIState)
            logEvent(expectedUIEvent)
            logError(expectedMessage, expectedException)
        }

        verifySequence {
            with(anyConstructed<SimpleMessageLogger>()) {
                log(expectedMessage)
                logState(expectedUIState)
                logEvent(expectedUIEvent)
                logError(expectedMessage, expectedException)
            }
        }
    }

    @Test
    fun `Given the default UniFlowLogger, When init() is called with a custom Logger, Then Logger calls are delegated to it`() {
        with(UniFlowLogger) {
            init(testLoggerMock)

            log(expectedMessage)
            logState(expectedUIState)
            logEvent(expectedUIEvent)
            logError(expectedMessage, expectedException)
        }

        with(testLoggerMock) {
            verifySequence {
                log(expectedMessage)
                logState(expectedUIState)
                logEvent(expectedUIEvent)
                logError(expectedMessage, expectedException)
            }
        }
    }
}
