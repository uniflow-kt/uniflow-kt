package io.uniflow.core.flow

@Deprecated("Use setState,WithState or FromState instead")
class StateFlowAction(private val flow: DataFlow, internal val onStateFlow: StateFlowFunction, internal val errorFunction: ErrorFunction? = null) {

    @Deprecated("Use setState,WithState or FromState instead")
    suspend fun setState(state: suspend () -> UIState) {
        if (errorFunction != null) {
            flow.setState({ state() }, errorFunction)
        } else flow.setState { state() }
    }

    @Deprecated("Use setState,WithState or FromState instead")
    fun setState(state: UIState) {
        if (errorFunction != null) {
            flow.setState({ state }, errorFunction)
        } else flow.setState { state }
    }

    @Deprecated("Can't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun setState(onStateUpdate: StateUpdateFunction, onError: ErrorFunction) {
    }

    @Deprecated("Can't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun applyState(state: UIState) {
    }

    @Deprecated("Can't use redeclare stateFlow", level = DeprecationLevel.ERROR)
    fun stateFlow(onStateFlow: StateFlowFunction, onActionError: ErrorFunction) {
    }

    @Deprecated("Can't use redeclare stateFlow", level = DeprecationLevel.ERROR)
    fun stateFlow(onStateFlow: StateFlowFunction) {
    }

    @Deprecated("Can't redeclare an action inside an action", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState> fromState(noinline onStateUpdate: StateUpdateFunction, noinline errorFunction: ErrorFunction) {
    }

    @Deprecated("Can't redeclare an action inside an action", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> fromState(noinline onStateUpdate: StateUpdateFunction) {
    }

    @Deprecated("Can't use stateFlow inside an action", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> stateFlowFrom(noinline stateFlow: StateFlowFunction, noinline errorFunction: ErrorFunction) {
    }

    @Deprecated("Can't use stateFlow inside an action", level = DeprecationLevel.ERROR)
    inline fun <reified T : UIState?> stateFlowFrom(noinline stateFlow: StateFlowFunction) {
    }
}

@Deprecated("Use setState,WithState or FromState instead")
typealias StateFlowFunction = suspend StateFlowAction.(UIState?) -> Unit
@Deprecated("Use setState,WithState or FromState instead")
typealias TypedFlowFunction<T> = suspend StateFlowAction.(T) -> Unit