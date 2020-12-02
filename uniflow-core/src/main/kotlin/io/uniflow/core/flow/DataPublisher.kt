package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

interface StatePublisher {
    suspend fun publishState(state: UIState, pushStateUpdate: Boolean = true)
    suspend fun getState(): UIState
    suspend fun getStateOrNull(): UIState?
    suspend fun setState(state: UIState) = publishState(state)
    suspend fun setState(state: () -> UIState) = publishState(state())
}

interface EventPublisher {
    suspend fun publishEvent(event: UIEvent)
    suspend fun sendEvent(event: UIEvent) = publishEvent(event)
    suspend fun sendEvent(event: () -> UIEvent) = publishEvent(event())
}

interface DataPublisher : StatePublisher, EventPublisher {
    suspend fun notifyStateUpdate(state: UIState, event: UIEvent) {
        publishState(state, pushStateUpdate = false)
        publishEvent(event)
    }
}
