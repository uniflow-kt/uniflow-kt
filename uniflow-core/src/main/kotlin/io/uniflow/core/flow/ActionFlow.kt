package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.flow.FlowCollector
import kotlin.reflect.KClass


typealias ActionFunction<T> = suspend ActionFlow.(T) -> (Unit)
typealias ActionErrorFunction = suspend ActionFlow.(Exception, UIState) -> (Unit)

enum class UIDataUpdateType {
    PUBLISH, NOTIFY
}

data class UIDataUpdate(val data: UIData, val type: UIDataUpdateType = UIDataUpdateType.PUBLISH)

class ActionFlow(
        val klass: KClass<UIState>,
        val onSuccess: ActionFunction<UIState>,
        val onError: ActionErrorFunction
) {
    internal lateinit var flow: FlowCollector<UIDataUpdate>

    suspend fun setState(state: UIState) {
        flow.emit(UIDataUpdate(state))
    }

    suspend fun setState(state: () -> UIState) {
        flow.emit(UIDataUpdate(state()))
    }

    suspend fun setStateAsync(state: suspend () -> UIState) {
        flow.emit(UIDataUpdate(state()))
    }

    suspend fun notifyStateUpdate(state: UIState, event: UIEvent) {
        flow.emit(UIDataUpdate(state, UIDataUpdateType.NOTIFY))
        sendEvent(event)
    }

    suspend fun sendEvent(event: UIEvent) {
        flow.emit(UIDataUpdate(event))
    }

    suspend fun sendEvent(event: () -> UIEvent) {
        flow.emit(UIDataUpdate(event()))
    }
}