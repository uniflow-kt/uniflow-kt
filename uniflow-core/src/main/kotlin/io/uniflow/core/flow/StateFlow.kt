package io.uniflow.core.flow

import kotlinx.coroutines.flow.FlowCollector

typealias StateFunctionFlow = suspend FlowCollector<UIState>.() -> Unit

suspend fun FlowCollector<UIState>.setState(updateFunction: StateFunction) {
    updateFunction.invoke(null)?.let { emit(it) }
}

suspend fun FlowCollector<UIState>.setState(state: UIState) {
    emit(state)
}