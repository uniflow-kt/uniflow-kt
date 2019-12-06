package io.uniflow.core.flow

import kotlinx.coroutines.flow.FlowCollector

data class StateAction(internal val stateFunction: StateFunction<*>? = null, internal val errorFunction: ErrorFunction? = null) {

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    fun setState(onStateUpdate: StateUpdateFunction, onError: ErrorFunction) {
    }

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    fun setState(updateFunction: StateUpdateFunction) {
    }

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    fun applyState(state: UIState) {
    }

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState> fromState(noinline onStateUpdate: StateUpdateFunction, noinline errorFunction: ErrorFunction) {
    }

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> fromState(noinline onStateUpdate: StateUpdateFunction) {
    }
}

typealias StateFunction<T> = suspend (UIState?) -> T
typealias TypedUpdateFunction<T> = suspend (T) -> UIState?
typealias StateUpdateFunction = StateFunction<UIState?>
typealias ErrorFunction = suspend (Exception) -> UIState?
typealias ActionFlow = suspend FlowCollector<UIState>.() -> Unit