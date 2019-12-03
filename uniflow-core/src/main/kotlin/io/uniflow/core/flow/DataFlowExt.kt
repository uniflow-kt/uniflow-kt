/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed launchOn an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UNCHECKED_CAST")

package io.uniflow.core.flow

/**
 * Return current State if is type T
 * @return state or null
 */
inline fun <reified T> DataFlow.getStateAsOrNull(): T? = currentState?.let { if (it is T) it else null }

/**
 * Return current State if is type T else throw error
 * @return state
 */
inline fun <reified T> DataFlow.getStateAs(): T = getStateAsOrNull() ?: error("current state is not ${T::class}")

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
inline fun <reified T : UIState> DataFlow.fromState(noinline onStateUpdate: TypedUpdateFunction<T>, noinline errorFunction: ErrorFunction) {
    if (currentState is T) {
        setState(onStateUpdate as StateUpdateFunction, errorFunction)
    } else {
        setState { sendEvent(UIEvent.BadOrWrongState(currentState)) }
    }
}

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
inline fun <reified T : UIState?> DataFlow.fromState(noinline onStateUpdate: TypedUpdateFunction<T>) {
    if (currentState is T) {
        setState(onStateUpdate as StateUpdateFunction)
    } else {
        setState { sendEvent(UIEvent.BadOrWrongState(currentState)) }
    }
}

/**
 * Execute stateflow from the given T state else send UIEvent.BadOrWrongState with current state
 */
@Deprecated("Use setState,WithState or FromState instead")
inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: TypedFlowFunction<T>, noinline errorFunction: ErrorFunction) {
    if (currentState is T) {
        stateFlow(stateFlow as StateFlowFunction, errorFunction)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(currentState)) }
    }
}

/**
 * Execute stateflow from the given T state else send UIEvent.BadOrWrongState with current state
 */
@Deprecated("Use setState,WithState or FromState instead")
inline fun <reified T : UIState?> DataFlow.stateFlowFrom(noinline stateFlow: TypedFlowFunction<T>) {
    if (currentState is T) {
        stateFlow(stateFlow as StateFlowFunction)
    } else {
        stateFlow { sendEvent(UIEvent.BadOrWrongState(currentState)) }
    }
}