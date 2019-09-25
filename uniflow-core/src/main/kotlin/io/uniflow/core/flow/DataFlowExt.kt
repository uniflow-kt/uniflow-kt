package io.uniflow.core.flow

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
inline fun <reified T : UIState> DataFlow.fromState(noinline onStateUpdate: StateUpdateFunction, noinline errorFunction: ErrorFunction) {
    if (getCurrentState() is T) {
        onAction(StateAction(onStateUpdate, errorFunction))
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.fromState(noinline onStateUpdate: StateUpdateFunction) {
    if (getCurrentState() is T) {
        onAction(StateAction(onStateUpdate))
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: StateFlowFunction, noinline errorFunction: ErrorFunction) {
    if (getCurrentState() is T) {
        stateFlow(stateFlow, errorFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: StateFlowFunction) {
    if (getCurrentState() is T) {
        stateFlow(stateFlow)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}