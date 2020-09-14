package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.launchOnIO
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

class ActionDispatcher(
        private val coroutineScope: CoroutineScope,
        private val reducer: ActionReducer,
        private val dataStore: UIDataStore,
        private val dataFlow: DataFlow,
        val tag: String
) {
    fun getCurrentState(): UIState = dataStore.currentState

    @Suppress("UNCHECKED_CAST")
    fun <T : UIState> getCurrentStateOrNull(): T? = getCurrentState() as? T

    fun action(onAction: ActionFunction<UIState>): ActionFlow = action(onAction) { error, state -> dataFlow.onError(error, state, this) }

    fun action(onAction: ActionFunction<UIState>, onError: ActionErrorFunction): ActionFlow = ActionFlow(onAction, onError).also {
        coroutineScope.launchOnIO {
            UniFlowLogger.debug("$tag - enqueue: $it")
            reducer.enqueueAction(it)
        }
    }

    fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow = actionOn(stateClass, onAction) { error, state -> dataFlow.onError(error, state, this) }

    @Suppress("UNCHECKED_CAST")
    fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow {
        val currentState = getCurrentState()
        return if (stateClass.isInstance(currentState)) {
            val action = ActionFlow(onAction as ActionFunction<UIState>, onError)
            coroutineScope.launchOnIO {
                UniFlowLogger.debug("$tag - enqueue: $action")
                reducer.enqueueAction(action)
            }
            action
        } else {
            action { sendEvent { UIEvent.BadOrWrongState(currentState) } }
        }
    }
}