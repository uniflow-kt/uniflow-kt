@file:Suppress("UNCHECKED_CAST")

package io.uniflow.core.flow

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
inline fun <reified T : UIState> DataFlow.fromState(noinline onStateUpdate: TypedUpdateFunction<T>, noinline errorFunction: ErrorFunction<T>) {
    if (getCurrentState() is T) {
        setState(onStateUpdate as StateUpdateFunction, errorFunction as StateErrorFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.fromState(noinline onStateUpdate: TypedUpdateFunction<T>) {
    if (getCurrentState() is T) {
        setState(onStateUpdate as StateUpdateFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: TypedFlowFunction<T>, noinline errorFunction: ErrorFunction<T>) {
    if (getCurrentState() is T) {
        stateFlow(stateFlow as StateFlowFunction, errorFunction as StateErrorFunction)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: TypedFlowFunction<T>) {
    if (getCurrentState() is T) {
        stateFlow(stateFlow as StateFlowFunction)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}