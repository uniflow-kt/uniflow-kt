package io.uniflow.core.flow

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
inline fun <reified T : UIState> DataFlow.fromState(noinline onStateUpdate: TypedUpdateFunction<T>, noinline errorFunction: ErrorFunction): StateAction {
    return if (getCurrentState() is T) {
        setState(onStateUpdate as StateUpdateFunction, errorFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.fromState(noinline onStateUpdate: TypedUpdateFunction<T>): StateAction {
    return if (getCurrentState() is T) {
        setState(onStateUpdate as StateUpdateFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: TypedFlowFunction<T>, noinline errorFunction: ErrorFunction): StateFlowAction {
    return if (getCurrentState() is T) {
        stateFlow(stateFlow as StateFlowFunction, errorFunction)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: TypedFlowFunction<T>): StateFlowAction {
    return if (getCurrentState() is T) {
        stateFlow(stateFlow as StateFlowFunction)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}