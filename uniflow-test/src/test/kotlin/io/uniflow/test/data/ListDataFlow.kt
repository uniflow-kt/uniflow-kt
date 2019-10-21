package io.uniflow.test.data

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.StateAction
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onIO
import io.uniflow.core.threading.onMain
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor

abstract class ListDataFlow : DataFlow {

    private val supervisorJob = SupervisorJob()
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    val states = arrayListOf<UIState>()
    val events = arrayListOf<UIEvent>()

    override suspend fun sendEvent(event: UIEvent): UIState? {
        onMain {
            UniFlowLogger.logEvent(event)
            events.add(event)
        }
        return null
    }

    override suspend fun applyState(state: UIState) {
        onMain {
            UniFlowLogger.logState(state)
            states.add(state)
        }
    }

    override val state: UIState?
        get() = states.lastOrNull()

    open fun cancel() {
        coroutineScope.cancel()
        actorFlow.close()
    }

    override val actorFlow = coroutineScope.actor<StateAction>(UniFlowDispatcher.dispatcher.default(), capacity = 10) {
        for (action in channel) {
            if (coroutineScope.isActive) {
                UniFlowLogger.log("ListDataFlow run action $action")
                onIO {
                    proceedAction(action)
                }
            } else {
                UniFlowLogger.log("ListDataFlow action cancelled")
            }
        }
    }
}