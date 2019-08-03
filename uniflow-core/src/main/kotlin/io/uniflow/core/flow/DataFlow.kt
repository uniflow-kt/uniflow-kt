package io.uniflow.core.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.dispatcher.UniFlowDispatcher.dispatcher
import io.uniflow.core.logger.UniFlowLogger
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
     * If any error occurs and is not caught, this function catch it
     * @param error
     */
    suspend fun onError(error: Throwable) {
        UniFlowLogger.logError("Got error", error)
        throw error
    }

    /**
     * Execute the action & catch any error
     * @param action
     */
    fun onAction(action: Action<UIState?, *>) {
        launch(dispatcher.io()) {
            proceedAction(action)
        }
    }

    /**
     * Execute action on coroutine
     */
    suspend fun proceedAction(action: Action<UIState?, *>) {
        try {
            val result = action.actionFunction.invoke(this, getCurrentState())
            if (result is UIState) {
                applyState(result)
            }
        } catch (e: Throwable) {
            onError(action, e)
        }
    }

    /**
     * Handle error catch for given withState
     * @param action
     * @param error
     */
    fun onError(action: Action<*, *>, error: Throwable) {
        launch(dispatcher.io()) {
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
@Suppress("UNCHECKED_CAST")
inline fun <reified T : UIState?> DataFlow.fromState(noinline fromBlock: ActionFunction<T, UIState?>) {
    if (getCurrentState() is T) {
        onAction(Action(fromBlock as ActionFunction<UIState?, UIState?>))
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}

/**
 * Execute update action from the given T state else send UIEvent.BadOrWrongState with current state
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : UIState> DataFlow.fromState(noinline fromBlock: ActionFunction<T, UIState?>, noinline errorFunction: ErrorFunction) {
    if (getCurrentState() is T) {
        onAction(Action(fromBlock as ActionFunction<UIState?, UIState?>))
    } else {
        withState { sendEvent(UIEvent.BadOrWrongState(getCurrentState())) }
    }
}