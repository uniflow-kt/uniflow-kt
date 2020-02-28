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

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger

/**
 * Unidirectional Data Flow
 *
 * @author Arnaud Giuliani
 */
interface DataFlow {

    val scheduler: ActionFlowScheduler

    fun getCurrentState(): UIState

    fun action(onAction: ActionFunction<UIState>): ActionFlow {
        val action = ActionFlow(onAction) { error, state -> this@DataFlow.onError(error, state, this) }
        scheduler.addAction(action)
        return action
    }

    fun action(onAction: ActionFunction<UIState>, onError: ActionErrorFunction): ActionFlow {
        val action = ActionFlow(onAction, onError)
        scheduler.addAction(action)
        return action
    }

    suspend fun onError(error: Exception, currentState: UIState, flow: ActionFlow) {
        val errorMessage = "Uncaught error: $error"
        UniFlowLogger.logError(errorMessage, error)
        throw error
    }
}

inline fun <reified T : UIState> DataFlow.getCurrentStateOrNull(): T? {
    val currentState = getCurrentState()
    return if (currentState is T) currentState else null
}

inline fun <reified T : UIState> DataFlow.actionOn(noinline onAction: ActionFunction<T>): ActionFlow {
    return actionOn(onAction) { error, state -> onError(error, state) }
}

inline fun <reified T : UIState> DataFlow.actionOn(noinline onAction: ActionFunction<T>,
    noinline onError: ActionErrorFunction): ActionFlow {
    val currentState = getCurrentState()
    return if (currentState is T) {
        val action = ActionFlow(onAction as ActionFunction<UIState>, onError)
        scheduler.addAction(action)
        action
    } else {
        action { sendEvent { UIEvent.BadOrWrongState(currentState) } }
    }
}