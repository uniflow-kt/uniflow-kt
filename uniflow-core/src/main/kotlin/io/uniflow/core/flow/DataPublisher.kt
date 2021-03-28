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
    fun getState(): UIState
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
 * Get state for type T or return null
 */
inline fun <reified T : UIState> StatePublisher.getStateOrNull(): T? {
    val state = getState()
    return if (state is T) {
        state
    } else null
}

/**
 * Let execute block on given state T if not null
 */
inline fun <reified T : UIState> StatePublisher.onState(code: (T) -> Unit){
    getStateOrNull<T>()?.let { code(it) }
}

/**
 * Let execute block on given state T if not null, return a Result R
 */
inline fun <reified T : UIState, R: Any> StatePublisher.letOnState(code: (T) -> R) : R? {
    return getStateOrNull<T>()?.let { code(it) }
}