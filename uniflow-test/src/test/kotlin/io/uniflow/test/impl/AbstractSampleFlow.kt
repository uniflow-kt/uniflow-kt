package io.uniflow.test.impl

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.ActionFlowScheduler
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.UIDataManager
import io.uniflow.core.flow.UIDataPublisher
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
    val events = arrayListOf<UIEvent>()

    fun hasStates(vararg states: UIState) {
        assert(this.states == states.toList()) { "should have states ${states.toList()} but was ${this.states}" }
    }

    fun hasState(index: Int, state: UIState) {
        assert(this.states[index] == state) { "should have state $state but was ${this.states[index]}" }
    }

    fun hasNoState() = hasStates()
    val lastState: UIState?
        get() = states.lastOrNull()

    fun hasEvents(vararg events: UIEvent) {
        assert(this.events == events.toList()) { "should have events ${events.toList()} but was ${this.events}" }
    }

    val lastEvent: UIEvent?
        get() = events.lastOrNull()

    fun hasNoEvent() = hasEvents()


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