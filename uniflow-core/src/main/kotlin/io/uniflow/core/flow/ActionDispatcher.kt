package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.launchOnIO
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

class ActionDispatcher(
        private val coroutineScope: CoroutineScope,
        private val reducer: ActionReducer,
        private val runError: suspend (Exception, UIState) -> Unit,
        val tag: String
) {
    fun dispatchAction(onAction: ActionFunction): Action = dispatchAction(onAction) { error, state -> runError(error, state) }

    fun dispatchAction(onAction: ActionFunction, onError: ActionErrorFunction): Action = Action(onAction, onError).also {
        dispatchAction(it)
    }

    fun dispatchAction(action: Action) {
        coroutineScope.launchOnIO {
            UniFlowLogger.debug("$tag - enqueue: $action")
            reducer.enqueueAction(action)
        }
    }

    fun actionOn(kClass: KClass<out UIState>, onAction: ActionFunction): Action = actionOn(kClass,onAction){ error, state -> runError(error, state) }
    fun actionOn(kClass: KClass<out UIState>, onAction: ActionFunction, onError: ActionErrorFunction): Action = Action(onAction, onError, kClass).also { dispatchAction(it) }

    fun close() {
        reducer.close()
    }
}