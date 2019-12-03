package io.uniflow.core.flow

data class StateAction(internal val stateFunction: StateFunction<*>? = null, internal val errorFunction: ErrorFunction? = null) {

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    fun setState(onStateUpdate: StateUpdateFunction, onError: ErrorFunction) {}

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    fun setState(updateFunction: StateUpdateFunction) {}

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    fun applyState(state: UIState) {}

    @Deprecated("Can't redeclare stateFlow inside an stateFlow", level = DeprecationLevel.ERROR)
    fun stateFlow(onStateFlow: StateFlowFunction, onActionError: ErrorFunction) {}

    @Deprecated("Can't redeclare stateFlow inside an stateFlow", level = DeprecationLevel.ERROR)
    fun stateFlow(onStateFlow: StateFlowFunction) {}

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState> fromState(noinline onStateUpdate: StateUpdateFunction, noinline errorFunction: ErrorFunction) {}

    @Deprecated("Can't redeclare an action inside an stateFlow", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> fromState(noinline onStateUpdate: StateUpdateFunction) {}

    @Deprecated("Can't redeclare stateFlow inside an stateFlow", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> stateFlowFrom(noinline stateFlow: StateFlowFunction, noinline errorFunction: ErrorFunction) {}

    @Deprecated("Can't redeclare stateFlow inside an stateFlow", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> stateFlowFrom(noinline stateFlow: StateFlowFunction) {}
}

typealias StateFunction<T> = suspend StateAction.(UIState?) -> T
typealias TypedUpdateFunction<T> = suspend StateAction.(T) -> UIState?
typealias StateUpdateFunction = StateFunction<UIState?>
typealias StateActionFunction = StateFunction<Unit>
typealias ErrorFunction = suspend StateAction.(Exception) -> UIState?