package io.uniflow.core.flow

import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.CoroutineScope

/**
 * Unidirectional Data Flow
 *
 *
 */
interface DataFlow {

    /**
     * Current CoroutineScope
     */
    val coroutineScope: CoroutineScope

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
     * Make an Action to update the current state
     *
     * @param updateFunction - function to produce a new state, from the current state
     */
    fun setState(updateFunction: ActionFunction<UIState?, UIState?>, errorFunction: ErrorFunction) {
        onAction(Action(updateFunction, errorFunction))
    }

    fun setState(updateFunction: ActionFunction<UIState?, UIState?>) {
        onAction(Action(updateFunction))
    }

    /**
     * Make an Action that can update or not the current state
     * More for side effects
     *
     * @param actionFunction - function run against the current state
     */
    fun withState(actionFunction: ActionFunction<UIState?, Unit>, errorFunction: ErrorFunction) {
        onAction(Action(actionFunction, errorFunction))
    }

    fun withState(actionFunction: ActionFunction<UIState?, Unit>) {
        onAction(Action(actionFunction))
    }

    /**
     * An action that can trigger several state changes
     *
     * stateFlowFunction allow to use the StateFlowPublisher.setState(...) function to set any new state
     *
     * @param stateFlowFunction - flow state
     * @param errorFunction - flowError function
     */
    fun stateFlow(stateFlowFunction: StateFlowFunction<UIState?>, errorFunction: ErrorFunction) {
        coroutineScope.launchOnIO {
            try {
                val publisher = StateFlowPublisher(this@DataFlow, errorFunction)
                stateFlowFunction(publisher, getCurrentState())
            } catch (e: Exception) {
                errorFunction(e)?.let { applyState(it) }
            }
        }
    }

    /**
     * An action that can trigger several state changes
     *
     * stateFlowFunction allow to use the StateFlowPublisher.setState(...) function to set any new state
     *
     * @param stateFlowFunction - flow state
     */
    fun stateFlow(stateFlowFunction: StateFlowFunction<UIState?>) {
        coroutineScope.launchOnIO {
            try {
                val publisher = StateFlowPublisher(this@DataFlow)
                stateFlowFunction(publisher, getCurrentState())
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    /**
     * If any flowError occurs and is not caught, this function catch it
     * @param error
     */
    suspend fun onError(error: Exception) {
        UniFlowLogger.logError("Got Flow Error", error)
        throw error
    }

    /**
     * Execute the action & catch any flowError
     * @param action
     */
    fun onAction(action: Action<UIState?, *>) {
        coroutineScope.launchOnIO {
            proceedAction(action)
        }
    }

    /**
     * Execute action on coroutine
     */
    suspend fun proceedAction(action: Action<UIState?, *>) {
        try {
            val result = action.actionFunction.invoke(getCurrentState())
            if (result is UIState) {
                applyState(result)
            }
        } catch (e: Exception) {
            onError(action, e)
        }
    }

    /**
     * Handle flowError catch for given withState
     * @param action
     * @param error
     */
    fun onError(action: Action<*, *>, error: Exception) {
        coroutineScope.launchOnIO {
            if (action.errorFunction != null) {
                val failState = action.errorFunction.let {
                    it.invoke(error)
                }
                failState?.let { applyState(failState) }
            } else onError(error)
        }
    }
}