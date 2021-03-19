package io.uniflow.core.flow.data

import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.logger.UniFlowLogger

/**
 * Help consume events & extract content
 */
data class EventConsumer(val id: String) {

    init {
        UniFlowLogger.debug("$this has been created")
    }

    fun onEvent(event: Event<UIEvent>): UIEvent? {
        return event.getContentForConsumer(this)
    }
}
