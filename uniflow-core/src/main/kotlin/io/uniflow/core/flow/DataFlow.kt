package io.uniflow.core.flow

import io.uniflow.core.logger.EventLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface DataFlow : CoroutineScope {

    suspend fun sendEvent(event: UIEvent): UIState?

    fun getCurrentState(): UIState?

    suspend fun applyState(state: UIState)

    fun <U : UIState> setState(executionContext: CoroutineContext = Dispatchers.Default, updateFunction: ActionFunction<U>): Action<U> {
        val action = Action(updateFunction) launchOn executionContext
        executeAction(action)
        return action
    }

    fun withState(executionContext: CoroutineContext = Dispatchers.Default, actionFunction: ActionFunction<UIState?>): Action<UIState?> {
        val action = Action<UIState?>(actionFunction) launchOn executionContext
        executeAction(action)
        return action
    }

    suspend fun onError(error: Throwable) {
        EventLogger.log("Error from $this - $error")
        throw error
    }

    fun <T> executeAction(action: Action<T>) = launch(action.executionContext) {
        try {
            val result = action.actionFunction.invoke(this, getCurrentState())
            if (result is UIState) {
                applyState(result)
            }
        } catch (e: Throwable) {
            handleActionError(action, e)
        }
    }

    suspend fun <T> DataFlow.handleActionError(action: Action<T>, e: Throwable) {
        onMain {
            if (action.errorFunction != null) {
                action.errorFunction?.let { it.invoke(this@DataFlow, e) }
            } else {
                onError(e)
            }
        }
    }

    suspend fun <T> onMain(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Main, block = block)
    suspend fun <T> onDefault(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Default, block = block)
    suspend fun <T> onIO(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO, block = block)
}

data class Action<T>(val actionFunction: ActionFunction<T>) {
    internal var errorFunction: ErrorFunction? = null
    internal var executionContext: CoroutineContext = Dispatchers.Default

    infix fun onError(function: ErrorFunction): Action<T> {
        errorFunction = function
        return this
    }

    infix fun launchOn(context: CoroutineContext): Action<T> {
        executionContext = context
        return this
    }
}
typealias ActionFunction<U> = suspend CoroutineScope.(UIState?) -> U?
typealias ErrorFunction = suspend CoroutineScope.(Throwable) -> Unit