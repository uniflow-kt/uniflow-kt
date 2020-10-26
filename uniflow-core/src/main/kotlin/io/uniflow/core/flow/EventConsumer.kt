package io.uniflow.core.flow

import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.logger.UniFlowLogger

/**
 * Help receive & consume event.
 */
class EventConsumer {
    private val receivedIds: MutableList<String> = arrayListOf()

    fun onEvent(event: Event<UIEvent>): UIEvent? {
        return if (canConsumeEvent(event)) {
            consumeEvent(event)
        } else {
            UniFlowLogger.debug("$this has already received $event")
            null
        }
    }

    private fun canConsumeEvent(event: Event<UIEvent>): Boolean = receivedIds.contains(event.id)
    private fun consumeEvent(event: Event<UIEvent>): UIEvent {
        receivedIds.add(event.id)
        UniFlowLogger.debug("$this consumed $event")
        return event.content
    }
}