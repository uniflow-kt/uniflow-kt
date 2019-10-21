@file:Suppress("UNCHECKED_CAST")

package io.uniflow.core.flow

/**
 * Return current State if is type T
 * @return state or null
 */
inline fun <reified T> DataFlow.stateAs(): T? = state?.let { if (it is T) it else null }

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
inline fun <reified T : UIState> DataFlow.fromState(noinline onStateUpdate: TypedUpdateFunction<T>, noinline errorFunction: ErrorFunction) {
    if (state is T) {
        setState(onStateUpdate as StateUpdateFunction, errorFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(state)) }
    }
}

inline fun <reified T : UIState?> DataFlow.fromState(noinline onStateUpdate: TypedUpdateFunction<T>) {
    if (state is T) {
        setState(onStateUpdate as StateUpdateFunction)
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(state)) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: TypedFlowFunction<T>, noinline errorFunction: ErrorFunction) {
    if (state is T) {
        stateFlow(stateFlow as StateFlowFunction, errorFunction)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(state)) }
    }
}

inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: TypedFlowFunction<T>) {
    if (state is T) {
        stateFlow(stateFlow as StateFlowFunction)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(state)) }
    }
}