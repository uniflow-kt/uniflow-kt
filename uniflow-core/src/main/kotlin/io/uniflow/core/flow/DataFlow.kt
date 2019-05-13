package io.uniflow.core.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.logger.UniFlowLogger.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
     * Make an Action to update the current state
     *
     * @param updateFunction - function to produce a new state, from the current state
     */
    fun <U : UIState> setState(updateFunction: ActionFunction<U>, errorFunction: ErrorFunction) {
        executeAction(Action(updateFunction, errorFunction))
    }

    fun <U : UIState> setState(updateFunction: ActionFunction<U>) {
        executeAction(Action(updateFunction))
    }

    /**
     * Make an Action that can update or not the current state
     * More for side effects
     *
     * @param actionFunction - function run against the current state
     */
    fun withState(actionFunction: ActionFunction<Any?>, errorFunction: ErrorFunction) {
        executeAction(Action(actionFunction, errorFunction))
    }

    fun withState(actionFunction: ActionFunction<Any?>) {
        executeAction(Action(actionFunction))
    }

    /**
     * If any error occurs and is not caught, this function catch it
     * @param error
     */
    suspend fun onError(error: Throwable) {
        UniFlowLogger.logError("$TAG [ERROR] on $this", error)
        throw error
    }

    /**
     * Execute the withState & catch any error
     * @param action
     */
    fun <T> executeAction(action: Action<T>) = launch(UniFlowDispatcher.dispatcher.default()) {
        try {
            val result = action.actionFunction.invoke(this, getCurrentState())
            if (result is UIState) {
                applyState(result)
            }
        } catch (e: Throwable) {
            handleActionError(action, e)
        }
    }

    /**
     * Handle error catch for given withState
     * @param action
     * @param error
     */
    suspend fun <T> DataFlow.handleActionError(action: Action<T>, error: Throwable) {
        onMain {
            action.errorFunction?.let {
                it.invoke(this@DataFlow, error)
            } ?: onError(error)
        }
    }

    /**
     * Switch current execution context to Main thread
     */
    suspend fun <T> onMain(block: suspend CoroutineScope.() -> T) = withContext(UniFlowDispatcher.dispatcher.main(), block = block)

    /**
     * Switch current execution context to Default Thread
     */
    suspend fun <T> onDefault(block: suspend CoroutineScope.() -> T) = withContext(UniFlowDispatcher.dispatcher.default(), block = block)

    /**
     * Switch current execution context to IO Thread
     */
    suspend fun <T> onIO(block: suspend CoroutineScope.() -> T) = withContext(UniFlowDispatcher.dispatcher.io(), block = block)
}