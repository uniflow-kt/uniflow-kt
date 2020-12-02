package io.uniflow.core.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.flow.error.BadOrWrongStateException
import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor

class ActionReducer(
        private val dataFlow: DataFlow,
        private val coroutineScope: CoroutineScope,
        private val defaultDispatcher: CoroutineDispatcher,
        defaultCapacity: Int = Channel.BUFFERED,
        val tag: String
) {

    @OptIn(ObsoleteCoroutinesApi::class)
    private val actor = coroutineScope.actor<Action>(UniFlowDispatcher.dispatcher.default(),
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

    suspend fun enqueueAction(action: Action) {
        actor.send(action)
    }

    private suspend fun reduceAction(action: Action) {
        UniFlowLogger.debug("$tag - reduce: $action")
        val currentState: UIState = dataFlow.defaultPublisher().getState()
        try {
            action.targetState?.let { targetState ->
                if (targetState != currentState::class) {
                    action.onError(BadOrWrongStateException(currentState, targetState), currentState)
                    return
                }
            }
            action.onSuccess(currentState)
            UniFlowLogger.debug("$tag - completed: $action")
        } catch (e: Exception) {
            UniFlowLogger.debug("$tag - error: $action")
            action.onError(e, currentState)
        }
    }

    fun close() {
        actor.close()
    }
}