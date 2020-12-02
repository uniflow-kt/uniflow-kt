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

import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger

/**
 * Unidirectional Data Flow
 *
 * @author Arnaud Giuliani
 */
interface DataFlow {
    val tag: String
    val actionDispatcher: ActionDispatcher
    fun action(onAction: ActionFunction): Action = actionDispatcher.dispatchAction(onAction)
    fun action(onAction: ActionFunction, onError: ActionErrorFunction): Action = actionDispatcher.dispatchAction(onAction, onError)
    suspend fun onError(error: Exception, currentState: UIState) {
        UniFlowLogger.logError("Uncaught error: $error - ${error.stackTrace}", error)
        throw error
    }
}

//inline fun <reified T : UIState> DataFlow.getCurrentStateOrNull(): T? = getCurrentStateOrNull(T::class)
inline fun <reified T : UIState> DataFlow.actionOn(noinline onAction: ActionFunction_T<T>): Action = actionDispatcher.actionOn(T::class, onAction as ActionFunction)
inline fun <reified T : UIState> DataFlow.actionOn(noinline onAction: ActionFunction_T<T>, noinline onError: ActionErrorFunction_T<T>): Action = actionDispatcher.actionOn(T::class, onAction as ActionFunction, onError as ActionErrorFunction)