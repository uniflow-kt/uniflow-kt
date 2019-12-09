package io.uniflow.core.flow

data class StateAction(internal val stateFunction: StateFunction? = null, internal val errorFunction: ErrorFunction? = null) {

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    fun setState(onStateUpdate: StateFunction, onError: ErrorFunction) {
    }

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    fun setState(updateFunction: StateFunction) {
    }

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    fun applyState(state: UIState) {
    }

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState> fromState(noinline onStateUpdate: StateFunction, noinline errorFunction: ErrorFunction) {
    }

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> fromState(noinline onStateUpdate: StateFunction) {
    }
}

typealias StateFunction = suspend (UIState?) -> UIState?
typealias TypedStateFunction<T> = suspend (T) -> UIState?
typealias ErrorFunction = suspend (Exception) -> UIState?