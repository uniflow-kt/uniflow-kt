package io.uniflow.core.flow

class StateFlowAction(private val flow: DataFlow, internal val onStateFlow: StateFlowFunction, internal val errorFunction: ErrorFunction? = null) {
    suspend fun setState(state: UIState) {
        val action: StateAction = if (errorFunction != null) {
            StateAction({ state }, errorFunction)
        } else StateAction({ state })
        flow.proceedAction(action)
    }

    suspend fun setState(state: () -> UIState) {
        val action: StateAction = if (errorFunction != null) {
            StateAction({ state() }, errorFunction)
        } else StateAction({ state() })
        flow.proceedAction(action)
    }

    @Deprecated("Can't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun setState(onStateUpdate: StateUpdateFunction, onError: ErrorFunction) {
    }

    @Deprecated("CCan't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun setState(updateFunction: StateUpdateFunction) {
    }

    @Deprecated("CCan't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun withState(onStateAction: StateActionFunction, errorFunction: ErrorFunction) {
    }

    @Deprecated("Can't redeclare an action inside a stateFlow", level = DeprecationLevel.ERROR)
    fun withState(onStateAction: StateActionFunction) {
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

typealias StateFlowFunction = suspend StateFlowAction.(UIState?) -> Unit