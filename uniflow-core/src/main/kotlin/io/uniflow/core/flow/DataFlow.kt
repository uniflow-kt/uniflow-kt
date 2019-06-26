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
    fun setState(updateFunction: ActionFunction<UIState?, UIState?>, errorFunction: ErrorFunction) {
        executeAction(Action(updateFunction, errorFunction))
    }

    fun setState(updateFunction: ActionFunction<UIState?, UIState?>) {
        executeAction(Action(updateFunction))
    }

    /**
     * Make an Action that can update or not the current state
     * More for side effects
     *
     * @param actionFunction - function run against the current state
     */
    fun withState(actionFunction: ActionFunction<UIState?, Unit>, errorFunction: ErrorFunction) {
        executeAction(Action(actionFunction, errorFunction))
    }

    fun withState(actionFunction: ActionFunction<UIState?, Unit>) {
        executeAction(Action(actionFunction))
    }

    /**
     * If any error occurs and is not caught, this function catch it
     * @param error
     */
    suspend fun onError(error: Throwable) {
        throw error
    }

    /**
     * Execute the action & catch any error
     * @param action
     */
    fun executeAction(action: Action<UIState?, *>) {
        launch(UniFlowDispatcher.dispatcher.io()) {
            try {
                val result = action.actionFunction.invoke(this, getCurrentState())
                if (result is UIState) {
                    applyState(result)
                }
            } catch (e: Throwable) {
                handleActionError(action, e)
            }
        }
    }

    /**
     * Execute the action from the given state T & catch any error
     * @param action
     */
    fun <T : UIState> safeExecuteAction(action: Action<T, *>) {
        launch(UniFlowDispatcher.dispatcher.io()) {
            try {
                val result = action.actionFunction.invoke(this, getCurrentState() as T?
                        ?: error("Current state is null"))
                if (result is UIState) {
                    applyState(result)
                }
            } catch (e: Throwable) {
                handleActionError(action, e)
            }
        }
    }

    /**
     * Handle error catch for given withState
     * @param action
     * @param error
     */
    suspend fun handleActionError(action: Action<*, *>, error: Throwable) {
        UniFlowLogger.logError("$TAG [ERROR] on $this", error)
        onMain {
            if (action.errorFunction != null) {
                val failState = action.errorFunction?.let {
                    it.invoke(this@DataFlow, error)
                }
                failState?.let { applyState(failState) }
            } else onError(error)
        }
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


/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
inline fun <reified T : UIState> DataFlow.fromState(noinline fromBlock: ActionFunction<T, UIState?>) {
    if (getCurrentState() is T) {
        safeExecuteAction(Action(fromBlock))
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
inline fun <reified T : UIState> DataFlow.fromState(noinline fromBlock: ActionFunction<T, UIState?>, noinline errorFunction: ErrorFunction) {
    if (getCurrentState() is T) {
        safeExecuteAction(Action(fromBlock, errorFunction))
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}