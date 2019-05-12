package io.uniflow.core

import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.logger.EventLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext

abstract class StackDataFlow : DataFlow {

    private val viewModelJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + viewModelJob

    val states = ConcurrentLinkedQueue<UIState>()
    val events = ConcurrentLinkedQueue<UIEvent>()

    override suspend fun sendEvent(event: UIEvent): UIState? {
        onMain {
            EventLogger.log("UI Event - $event")
            events.add(event)
        }
        return null
    }

    override fun getCurrentState(): UIState? {
        return states.lastOrNull()
    }

    override suspend fun applyState(state: UIState) {
        onMain {
            EventLogger.log("UI State - $state")
            states.add(state)
        }
    }
}