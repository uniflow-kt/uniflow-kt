package io.uniflow.core.flow

class StateFlowAction(private val flow: DataFlow, internal val onStateFlow: StateFlowFunction, internal val errorFunction: StateErrorFunction? = null) {

    suspend fun setState(state: suspend () -> UIState) {
        if (errorFunction != null) {
            flow.setState({ state() }, errorFunction)
        } else flow.setState { state() }
    }

    fun setState(state: UIState) {
        if (errorFunction != null) {
            flow.setState({ state }, errorFunction)
        } else flow.setState { state }
    }

    @Deprecated("Can't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun setState(onStateUpdate: StateUpdateFunction, onError: StateErrorFunction) {
    }

    @Deprecated("CCan't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun withState(onStateAction: StateActionFunction, errorFunction: StateErrorFunction) {
    }

    @Deprecated("Can't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun withState(onStateAction: StateActionFunction) {
    }

    @Deprecated("Can't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun applyState(state: UIState) {
    }

    @Deprecated("Can't use redeclare stateFlow", level = DeprecationLevel.ERROR)
    fun stateFlow(onStateFlow: StateFlowFunction, onActionError: StateErrorFunction) {
    }

    @Deprecated("Can't use redeclare stateFlow", level = DeprecationLevel.ERROR)
    fun stateFlow(onStateFlow: StateFlowFunction) {
    }

    @Deprecated("Can't redeclare an action inside an action", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState> fromState(noinline onStateUpdate: StateUpdateFunction, noinline errorFunction: ErrorFunction<T>) {
    }

    @Deprecated("Can't redeclare an action inside an action", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> fromState(noinline onStateUpdate: StateUpdateFunction) {
    }

    @Deprecated("Can't use stateFlow inside an action", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> stateFlowFrom(noinline stateFlow: StateFlowFunction, noinline errorFunction: ErrorFunction<T>) {
    }

    @Deprecated("Can't use stateFlow inside an action", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> stateFlowFrom(noinline stateFlow: StateFlowFunction) {
    }
}

typealias StateFlowFunction = suspend StateFlowAction.(UIState?) -> Unit
typealias TypedFlowFunction<T> = suspend StateFlowAction.(T) -> Unit