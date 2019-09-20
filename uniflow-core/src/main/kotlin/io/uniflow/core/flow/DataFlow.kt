package io.uniflow.core.flow

import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.CoroutineScope

/**
 * Unidirectional Data Flow
 *
 *
 */
interface DataFlow : CoroutineScope {

    /**
     * Send an event
     * @param event
     */
    suspend fun sendEvent(event: UIEvent): UIState?

    /**
     * Return current State if any
     * @return state
     */
    fun getCurrentState(): UIState?

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
        UniFlowLogger.logError("Got Flow Error", error)
        throw error
    }

    /**
     * Make an Action to update the current state
     *
     * @param onStateUpdate - function to produce a new state, from the current state
     */
    fun setState(onStateUpdate: StateUpdateFunction, onActionError: ErrorFunction) {
        onAction(Action(onStateUpdate, onActionError))
    }
    fun setState(updateFunction: StateUpdateFunction) {
        onAction(Action(updateFunction))
    }

    /**
     * Make an Action that can update or not the current state
     * More for side effects
     *
     * @param onStateAction - function run against the current state
     */
    fun withState(onStateAction: StateActionFunction, onActionError: ErrorFunction) {
        onAction(Action(onStateAction, onActionError))
    }
    fun withState(onStateAction: StateActionFunction) {
        onAction(Action(onStateAction))
    }

    /**
     * An action that can trigger several state changes
     *
     * stateFlowFunction allow to use the StateFlowPublisher.setState(...) function to set any new state
     *
     * @param stateFlowFunction - flow state
     * @param errorFunction - flowError function
     */
    fun stateFlow(onStateFlow: StateFlowFunction, onActionError: ErrorFunction) {
        launchOnIO {
            proceedStateFlow(onStateFlow, onActionError)
        }
    }
    fun stateFlow(onStateFlow: StateFlowFunction) {
        launchOnIO {
            proceedStateFlow(onStateFlow)
        }
    }

    suspend fun proceedStateFlow(onStateFlow: StateFlowFunction, onActionError: ErrorFunction? = null) {
        try {
            val publisher = StateFlowPublisher(this, onActionError)
            onStateFlow(publisher, getCurrentState())
        } catch (e: Exception) {
            if (onActionError != null) {
                onActionError(Action(errorFunction = onActionError), e)
            } else {
                onError(e)
            }
        }
    }

    /**
     * Execute the action & catch any flowError
     * @param action
     */
    fun onAction(action: Action) {
        launchOnIO {
            proceedAction(action)
        }
    }

    /**
     * Execute action on coroutine
     */
    suspend fun proceedAction(action: Action) {
        try {
            val result = action.stateFunction?.invoke(action, getCurrentState())
            if (result is UIState) {
                applyState(result)
            } else {
                //TODO do something when no update?
            }
        } catch (e: Exception) {
            onActionError(action, e)
        }
    }

    /**
     * Handle flowError catch for given withState
     * @param action
     * @param error
     */
    fun onActionError(action: Action, error: Exception) {
        launchOnIO {
            if (action.errorFunction != null) {
                val failState = action.errorFunction.let {
                    it.invoke(action, error)
                }
                failState?.let { applyState(failState) }
            } else onError(error)
        }
    }
}