package io.uniflow.core.logger

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

/**
 * Logger to ... log nothing
 */
class EmptyLogger : Logger {
    override fun debug(message: String) {}

    override fun log(message: String) {}

    override fun logState(state: UIState) {}

    override fun logEvent(event: UIEvent) {}

    override fun logError(errorMessage: String, error: Exception?) {}
}