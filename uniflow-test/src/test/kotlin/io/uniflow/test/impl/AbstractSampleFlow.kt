package io.uniflow.test.impl

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.ActionFlowScheduler
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.UIDataManager
import io.uniflow.core.flow.UIDataPublisher
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onMain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class AbstractSampleFlow(defaultState: UIState) : DataFlow, UIDataPublisher {

    private val supervisorJob = SupervisorJob()
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)
    private val defaultDispatcher: CoroutineDispatcher = UniFlowDispatcher.dispatcher.io()
    private val uiDataManager = UIDataManager(this, defaultState)
    override val scheduler: ActionFlowScheduler = ActionFlowScheduler(uiDataManager, coroutineScope, defaultDispatcher)

    val states = arrayListOf<UIState>()
    val events = arrayListOf<UIEvent>()

    override fun getCurrentState(): UIState {
        return uiDataManager.currentState
    }

    init {
        action { setState { defaultState } }
    }

    override suspend fun publishState(state: UIState) {
        onMain(immediate = true) {
            states.add(state)
        }
    }

    override suspend fun sendEvent(event: UIEvent) {
        onMain(immediate = true) {
            events.add(event)
        }
    }

    fun close() {
        coroutineScope.cancel()
        scheduler.close()
    }
}