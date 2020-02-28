package io.uniflow.core.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

@UseExperimental(ObsoleteCoroutinesApi::class)
class ActionFlowScheduler(private val uiDataManager: UIDataManager, private val coroutineScope: CoroutineScope,
    private val defaultDispatcher: CoroutineDispatcher, defaultCapacity: Int = Channel.BUFFERED) {

    private val actor = coroutineScope.actor<ActionFlow>(UniFlowDispatcher.dispatcher.default(),
        capacity = defaultCapacity) {
        for (action in channel) {
            if (coroutineScope.isActive) {
                withContext(defaultDispatcher) {
                    runAction(action)
                }
            } else {
                UniFlowLogger.log("actor $action cancelled")
            }
        }
    }

    suspend fun addAction(action: ActionFlow) {
        actor.send(action)
    }

    private suspend fun runAction(action: ActionFlow) {
        val currentState: UIState = uiDataManager.currentState
        try {
            val onSuccess = action.onSuccess
            flow<UIDataUpdate> {
                action.flow = this
                onSuccess(action, currentState)
            }.collect { dataUpdate ->
                uiDataManager.pushNewData(dataUpdate)
            }
        } catch (e: Exception) {
            val onError = action.onError
            flow<UIDataUpdate> {
                action.flow = this
                onError(action, e, currentState)
            }.collect { dataUpdate ->
                uiDataManager.pushNewData(dataUpdate)
            }
        }
    }

    fun close() {
        actor.close()
    }
}