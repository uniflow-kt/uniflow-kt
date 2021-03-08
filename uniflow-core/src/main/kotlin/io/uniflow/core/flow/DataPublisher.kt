package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger

/**
 * Component for State publishing
 *
 * publishState - implementation used by setState
 * getState - get current state
 */
interface StatePublisher {
    suspend fun publishState(state: UIState, pushStateUpdate: Boolean = true)
    suspend fun getState(): UIState
    suspend fun setState(state: UIState) = publishState(state)
    suspend fun setState(state: () -> UIState) = publishState(state())
}

/**
 * Component for Event publishing
 *
 * publishEvent - implementation used by sendEvent-
 */
interface EventPublisher {
    suspend fun publishEvent(event: UIEvent)
    suspend fun sendEvent(event: UIEvent) = publishEvent(event)
    suspend fun sendEvent(event: () -> UIEvent) = publishEvent(event())
}

/**
 * Component that handle State & Event
 *
 * notifyStateUpdate - help notify a data update via event, instead of publishing a state
 * useful when need to notify part data update (like lists ...)
 */
interface DataPublisher : StatePublisher, EventPublisher {
    suspend fun notifyStateUpdate(state: UIState, event: UIEvent) {
        publishState(state, pushStateUpdate = false)
        publishEvent(event)
    }
}

/**
 *
 */
suspend inline fun <reified T : UIState> StatePublisher.getStateOrNull(): T? {
    val state = getState()
    return if (state is T) {
        state
    } else null
}

suspend inline fun <reified T : UIState> StatePublisher.onState(code: (T) -> Unit) {
    getStateOrNull<T>()?.let { code(it) } ?: UniFlowLogger.log("onState ${T::class} returned null")
}