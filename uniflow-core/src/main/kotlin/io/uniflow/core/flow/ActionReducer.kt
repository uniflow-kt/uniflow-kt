package io.uniflow.core.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlin.reflect.KClass

@OptIn(ObsoleteCoroutinesApi::class)
class ActionReducer(
        private val uiDataStore: UIDataStore,
        private val coroutineScope: CoroutineScope,
        private val defaultDispatcher: CoroutineDispatcher,
        defaultCapacity: Int = Channel.BUFFERED,
        val tag: String
) {

    private val actor = coroutineScope.actor<ActionFlow>(UniFlowDispatcher.dispatcher.default(),
            capacity = defaultCapacity) {
        for (action in channel) {
            if (coroutineScope.isActive) {
                withContext(defaultDispatcher) {
                    reduceAction(action)
                }
            } else {
                UniFlowLogger.debug("$tag - $action cancelled")
            }
        }
    }

    suspend fun enqueueAction(action: ActionFlow) {
        actor.send(action)
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun reduceAction(action: ActionFlow) {
        UniFlowLogger.debug("$tag - execute: $action")
        val currentState: UIState = uiDataStore.currentState
        try {
            if (!action.klass.isInstance(currentState)) {
                enqueueAction(ActionFlow(currentState::class as KClass<UIState>, onSuccess = { sendEvent { UIEvent.BadOrWrongState(currentState) } }, onError = action.onError))
            } else {
                val onSuccess = action.onSuccess
                flow<UIDataUpdate> {
                    action.flow = this
                    onSuccess(action, currentState)
                }.onCompletion {
                    UniFlowLogger.debug("$tag - completed: $action")
                }.collect { dataUpdate ->
                    uiDataStore.pushNewData(dataUpdate)
                }
            }
        } catch (e: Exception) {
            val onError = action.onError
            flow<UIDataUpdate> {
                action.flow = this
                onError(action, e, currentState)
            }.collect { dataUpdate ->
                uiDataStore.pushNewData(dataUpdate)
            }
        }
    }

    fun close() {
        actor.close()
    }
}