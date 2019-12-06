package io.uniflow.test.data

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.StateAction
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onMain
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor

abstract class ListDataFlow : DataFlow {

    private val supervisorJob = SupervisorJob()
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)
    override val defaultDispatcher: CoroutineDispatcher = UniFlowDispatcher.dispatcher.io()

    val states = arrayListOf<UIState>()
    val events = arrayListOf<UIEvent>()

    override suspend fun sendEvent(event: UIEvent): UIState? {
        onMain(immediate = true) {
            UniFlowLogger.logEvent(event)
            events.add(event)
        }
        return null
    }

    override suspend fun applyState(state: UIState) {
        onMain(immediate = true) {
            UniFlowLogger.logState(state)
            states.add(state)
        }
    }

    override suspend fun notifyUpdate(newState: UIState, notificationEvent: UIEvent): UIState? {
        onMain(immediate = true) {
            UniFlowLogger.logState(newState)
            states.add(newState)
            UniFlowLogger.logEvent(notificationEvent)
            events.add(notificationEvent)
        }
        return null
    }

    override val currentState: UIState?
        get() = states.lastOrNull()

    open fun cancel() {
        coroutineScope.cancel()
        actorFlow.close()
    }

    override val actorFlow = coroutineScope.actor<StateAction>(UniFlowDispatcher.dispatcher.default(), capacity = 100) {
        for (action in channel) {
            if (coroutineScope.isActive) {
                withContext(defaultDispatcher) {
                    proceedAction(action)
                }
            } else {
                UniFlowLogger.log("ListDataFlow action cancelled")
            }
        }
    }
}