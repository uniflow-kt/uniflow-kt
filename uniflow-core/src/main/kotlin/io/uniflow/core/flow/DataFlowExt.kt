package io.uniflow.core.flow

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
inline fun <reified T : UIState> DataFlow.fromState(noinline onStateUpdate: StateUpdateFunction, noinline errorFunction: ErrorFunction): StateAction {
    return if (getCurrentState() is T) {
        setState(onStateUpdate, errorFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.fromState(noinline onStateUpdate: StateUpdateFunction): StateAction {
    return if (getCurrentState() is T) {
        setState(onStateUpdate)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: StateFlowFunction, noinline errorFunction: ErrorFunction): StateFlowAction {
    return if (getCurrentState() is T) {
        stateFlow(stateFlow, errorFunction)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: StateFlowFunction): StateFlowAction {
    return if (getCurrentState() is T) {
        stateFlow(stateFlow)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}