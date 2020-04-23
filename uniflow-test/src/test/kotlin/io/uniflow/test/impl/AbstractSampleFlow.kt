package io.uniflow.test.impl

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.*
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onMain
import kotlinx.coroutines.*

abstract class AbstractSampleFlow(defaultState: UIState) : DataFlow {

    private val supervisorJob = SupervisorJob()
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)
    private val defaultDispatcher: CoroutineDispatcher = UniFlowDispatcher.dispatcher.io()

    val states = arrayListOf<UIState>()
    val data = arrayListOf<UIData>()
    val events = arrayListOf<UIEvent>()
    private val dataPublisher: UIDataPublisher = object : UIDataPublisher {
        override suspend fun publishState(state: UIState) {
            onMain(immediate = true) {
                data.add(state)
                states.add(state)
            }
        }

        override suspend fun publishEvent(event: UIEvent) {
            onMain(immediate = true) {
                data.add(event)
                events.add(event)
            }
        }
    }
    private val dataStore = UIDataStore(dataPublisher, defaultState)
    override val reducer: ActionReducer = ActionReducer(dataStore, coroutineScope, defaultDispatcher)


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
        return dataStore.currentState
    }

    init {
        action { setState { defaultState } }
    }

    fun close() {
        coroutineScope.cancel()
        reducer.close()
    }

    final override suspend fun onError(error: Exception, currentState: UIState, flow: ActionFlow) {
        flow.setState { UIState.Failed("Got error $error", error) }
    }
}
