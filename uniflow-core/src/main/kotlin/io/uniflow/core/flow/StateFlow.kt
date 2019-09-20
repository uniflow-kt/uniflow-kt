package io.uniflow.core.flow

class StateFlowPublisher(val flow: DataFlow, val errorFunction: ErrorFunction? = null) {
    suspend fun setState(state: UIState) {
        val action: Action = if (errorFunction != null) {
            Action({ state }, errorFunction)
        } else Action({ state })
        flow.proceedAction(action)
    }

    suspend fun setState(state: () -> UIState) {
        val action: Action = if (errorFunction != null) {
            Action({ state() }, errorFunction)
        } else Action({ state() })
        flow.proceedAction(action)
    }

    @Deprecated("", level = DeprecationLevel.ERROR)
    fun setState(onStateUpdate: StateUpdateFunction, onError: ErrorFunction) {
        error("")
    }

    @Deprecated("", level = DeprecationLevel.ERROR)
    fun setState(updateFunction: StateUpdateFunction) {
        error("")
    }

    @Deprecated("", level = DeprecationLevel.ERROR)
    fun withState(onStateAction: StateActionFunction, errorFunction: ErrorFunction) {
        error("")
    }

    @Deprecated("", level = DeprecationLevel.ERROR)
    fun withState(onStateAction: StateActionFunction) {
        error("")
    }

    @Deprecated("", level = DeprecationLevel.ERROR)
    suspend fun applyState(state: UIState){
        error("")
    }
}

typealias StateFlowFunction = suspend StateFlowPublisher.(UIState?) -> Unit