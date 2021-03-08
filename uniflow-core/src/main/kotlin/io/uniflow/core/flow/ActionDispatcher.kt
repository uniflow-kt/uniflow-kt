package io.uniflow.core.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.launchOnIO
import io.uniflow.core.threading.launchOnMain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.xml.ws.Dispatch
import kotlin.reflect.KClass

/**
 * Handle dispatch logic to ActionReducer, help wrap Action
 * dispatchAction - Help dispatch given action to ActionReducer, on given coroutineScope context.
 * actionOn - Handle default error behavior by routing back to ::onError function
 *
 * dispatch will be done in background
 *
 * @author Arnaud Giuliani
 */
class ActionDispatcher(
    private val coroutineScope: CoroutineScope,
    private val reducer: ActionReducer,
    private val coroutineDispatcher : CoroutineDispatcher = UniFlowDispatcher.dispatcher.main(),
    private val runError: suspend (Exception, UIState) -> Unit,
    val tag: String
) {
    fun dispatchAction(onAction: ActionFunction): Action =
        dispatchAction(onAction) { error, state -> runError(error, state) }

    fun dispatchAction(onAction: ActionFunction, onError: ActionErrorFunction): Action =
        Action(onAction, onError).also {
            dispatchAction(it)
        }

    fun dispatchAction(action: Action) {
        coroutineScope.launch(coroutineDispatcher) {
            UniFlowLogger.debug("$tag - enqueue: $action")
            reducer.enqueueAction(action)
        }
    }

    fun actionOn(kClass: KClass<out UIState>, onAction: ActionFunction): Action =
        actionOn(kClass, onAction) { error, state -> runError(error, state) }

    fun actionOn(kClass: KClass<out UIState>, onAction: ActionFunction, onError: ActionErrorFunction): Action =
        Action(onAction, onError, kClass).also { dispatchAction(it) }

    fun close() {
        reducer.close()
    }
}