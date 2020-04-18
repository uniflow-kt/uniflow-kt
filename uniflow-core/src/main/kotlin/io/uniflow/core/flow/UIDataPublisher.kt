package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

interface UIDataPublisher {
    suspend fun publishState(state: UIState)
    suspend fun publishEvent(event: UIEvent)
}