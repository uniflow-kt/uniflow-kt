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
        onAction(Action(fromBlock as ActionFunction<UIState?, UIState?>, errorFunction))
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlowFunction: StateFlowFunction<T>) {
    if (getCurrentState() is T) {
        stateFlow(stateFlowFunction as StateFlowFunction<UIState?>)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlowFunction: StateFlowFunction<T>, noinline errorFunction: ErrorFunction) {
    if (getCurrentState() is T) {
        stateFlow(stateFlowFunction as StateFlowFunction<UIState?>, errorFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}