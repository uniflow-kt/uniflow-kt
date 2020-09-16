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
package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import kotlin.reflect.KClass

/**
 * Unidirectional Data Flow
 *
 * @author Arnaud Giuliani
 */
interface DataFlow {
    fun getCurrentState(): UIState
    fun <T : UIState> getCurrentStateOrNull(stateClass: KClass<T>): T?
    fun action(onAction: ActionFunction<UIState>): ActionFlow
    fun action(onAction: ActionFunction<UIState>, onError: ActionErrorFunction): ActionFlow
    fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow
    fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow
    suspend fun onError(error: Exception, currentState: UIState, flow: ActionFlow) {
        error.printStackTrace()
        UniFlowLogger.logError("Uncaught error: $error", error)
        throw error
    }
}

inline fun <reified T : UIState> DataFlow.getCurrentStateOrNull(): T? = getCurrentStateOrNull(T::class)
inline fun <reified T : UIState> DataFlow.actionOn(noinline onAction: ActionFunction<T>): ActionFlow = actionOn(T::class, onAction)
inline fun <reified T : UIState> DataFlow.actionOn(noinline onAction: ActionFunction<T>, noinline onError: ActionErrorFunction): ActionFlow = actionOn(T::class, onAction, onError)