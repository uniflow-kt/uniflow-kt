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

import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Unidirectional Data Flow
 *
 * @author Arnaud Giuliani
 */
interface DataFlow {

    /**
     * Current used coroutine scope
     */
    val coroutineScope: CoroutineScope

    /**
     * Default Coroutine dispatcher
     */
    val defaultDispatcher: CoroutineDispatcher

    /**
     * Actor to buffer incoming actions
     */
    val actorFlow: SendChannel<StateAction>

    /**
     * Return current State if any
     * @return state
     */
    val currentState: UIState?

    /**
     * Send an event
     * @param event
     * @param nothing
     */
    suspend fun sendEvent(event: UIEvent): UIState?

    /**
     *
     */
    suspend fun notifyUpdate(newState: UIState, notificationEvent: UIEvent): UIState?

    /**
     * Apply new state to current state
     * @param state
     */
    suspend fun applyState(state: UIState)

    /**
     * If any flowError occurs and is not caught, this function catch it
     * @param error
     */
    suspend fun onError(error: Exception) {
        UniFlowLogger.logError("Uncaught error - '${error.message}' on state '$currentState'", error)
        throw error
    }

    /**
     * Make an StateAction to update the current state
     *
     * @param onStateUpdate - function to produce a new state, from the current state
     */
    fun setState(onStateUpdate: StateFunction, onActionError: ErrorFunction): StateAction {
        val action = StateAction(onStateUpdate, onActionError)
        onAction(action)
        return action
    }

    fun setState(onStateUpdate: StateFunction): StateAction {
        val action = StateAction(onStateUpdate)
        onAction(action)
        return action
    }

    /**
     * Create a state action that is listening for ActionFlow to emit/complete
     */
    fun stateFlow(stateFlowAction: StateFunctionFlow, onActionError: ErrorFunction): StateAction {
        return runActionFlow(stateFlowAction, onActionError)
    }

    fun stateFlow(stateFlowAction: StateFunctionFlow): StateAction {
        return runActionFlow(stateFlowAction)
    }

    fun runActionFlow(stateFlowAction: StateFunctionFlow, onActionError: ErrorFunction? = null): StateAction {
        return onActionError?.let {
            setState({ runFlow(stateFlowAction) }, onActionError)
        } ?: setState { runFlow(stateFlowAction) }
    }

    suspend fun runFlow(stateFlowAction: StateFunctionFlow): UIState? {
        flow(stateFlowAction).collect { newState ->
            applyState(newState)
        }
        return null
    }

    /**
     * Execute the action & catch any flowError
     * @param action
     */
    fun onAction(action: StateAction) {
        coroutineScope.apply {
            if (isActive) {
                actorFlow.offer(action)
            } else {
                UniFlowLogger.log("action $action cancelled")
            }
        }
    }

    /**
     * Execute action on coroutine
     */
    suspend fun proceedAction(action: StateAction) {
        try {
            val result = action.stateFunction?.invoke(currentState)
            if (result is UIState) {
                applyState(result)
            }
        } catch (e: Exception) {
            onActionError(action, e)
        }
    }

    /**
     * Handle flowError catch
     * @param action
     * @param error
     */
    fun onActionError(action: StateAction, error: Exception) {
        coroutineScope.apply {
            if (isActive) {
                UniFlowLogger.log("action error - '${error.message}'")
                launch(defaultDispatcher) {
                    if (action.errorFunction != null) {
                        val failState = action.errorFunction.let {
                            it.invoke(error)
                        }
                        failState?.let { setState { failState } }
                    } else setState {
                        onError(error)
                        null
                    }
                }
            } else {
                UniFlowLogger.log("error action cancelled - $action")
            }
        }
    }
}