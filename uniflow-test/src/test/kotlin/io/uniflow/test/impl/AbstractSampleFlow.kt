package io.uniflow.test.impl

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.ActionFlowScheduler
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.UIDataManager
import io.uniflow.core.flow.UIDataPublisher
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onMain
import kotlinx.coroutines.*

abstract class AbstractSampleFlow(defaultState: UIState) : DataFlow, UIDataPublisher {

    private val supervisorJob = SupervisorJob()
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)
    private val defaultDispatcher: CoroutineDispatcher = UniFlowDispatcher.dispatcher.io()
    private val uiDataManager = UIDataManager(this, defaultState)
    override val scheduler: ActionFlowScheduler = ActionFlowScheduler(uiDataManager, coroutineScope, defaultDispatcher)

    val states = arrayListOf<UIState>()
    val data = arrayListOf<UIData>()
    val events = arrayListOf<UIEvent>()

    val lastState: UIState?
        get() = states.lastOrNull()

    val lastEvent: UIEvent?
        get() = events.lastOrNull()

    fun assertReceived(vararg states: UIState) {
        assert(this.states == states.toList()) { "Wrong values\nshould have ${states.toList()}\nbut was ${this.states}" }
    }

    fun assertReceived(vararg events: UIEvent) {
        assert(this.events == events.toList()) { "Wrong values\nshould have ${events.toList()}\nbut was ${this.events}" }
    }

    fun assertReceived(vararg any: UIData) {
        assert(data == any.toList()) { "Wrong values\nshould have ${any.toList()}\nbut was $data" }
    }

    override fun getCurrentState(): UIState {
        return uiDataManager.currentState
    }

    init {
        action { setState { defaultState } }
    }

    override suspend fun publishState(state: UIState) {
        onMain(immediate = true) {
            data.add(state)
            states.add(state)
        }
    }

    override suspend fun sendEvent(event: UIEvent) {
        onMain(immediate = true) {
            data.add(event)
            events.add(event)
        }
    }

    fun close() {
        coroutineScope.cancel()
        scheduler.close()
    }
}