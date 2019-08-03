package io.uniflow.core.flow

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : UIState?> DataFlow.fromState(noinline fromBlock: ActionFunction<T, UIState?>) {
    if (getCurrentState() is T) {
        onAction(Action(fromBlock as ActionFunction<UIState?, UIState?>))
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : UIState> DataFlow.fromState(noinline fromBlock: ActionFunction<T, UIState?>, noinline errorFunction: ErrorFunction) {
    if (getCurrentState() is T) {
        onAction(Action(fromBlock as ActionFunction<UIState?, UIState?>))
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlowFunction: StateFlowFunction) {
    if (getCurrentState() is T) {
        stateFlow(stateFlowFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlowFunction: StateFlowFunction, noinline errorFunction: ErrorFunction) {
    if (getCurrentState() is T) {
        stateFlow(stateFlowFunction, errorFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}