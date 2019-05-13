package io.uniflow.core.flow

import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

abstract class StackFlow : DataFlow {

    private val viewModelJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + viewModelJob

    val states = arrayListOf<UIState>()
    val events = arrayListOf<UIEvent>()

    override suspend fun sendEvent(event: UIEvent): UIState? {
        onMain {
            UniFlowLogger.logEvent(event)
            events.add(event)
        }
        return null
    }

    override fun getCurrentState(): UIState? {
        return states.lastOrNull()
    }

    override suspend fun applyState(state: UIState) {
        onMain {
            UniFlowLogger.logState(state)
            states.add(state)
        }
    }

    open fun cancel() {
        viewModelJob.cancel()
    }
}