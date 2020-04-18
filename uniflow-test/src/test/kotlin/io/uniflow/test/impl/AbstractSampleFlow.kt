package io.uniflow.test.impl

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.*
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onMain
import kotlinx.coroutines.*

abstract class AbstractSampleFlow(defaultState: UIState) : DataFlow {

    private val supervisorJob = SupervisorJob()
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)
    private val defaultDispatcher: CoroutineDispatcher = UniFlowDispatcher.dispatcher.io()

    private val dataPublisher: UIDataPublisher = object : UIDataPublisher {
        override suspend fun publishState(state: UIState) {
            onMain(immediate = true) {
                states.add(state)
            }
        }

        override suspend fun publishEvent(event: UIEvent) {
            onMain(immediate = true) {
                events.add(event)
            }
        }
    }
    private val dataStore = UIDataStore(dataPublisher, defaultState)
    override val reducer: ActionReducer = ActionReducer(dataStore, coroutineScope, defaultDispatcher)

    val states = arrayListOf<UIState>()
    val events = arrayListOf<UIEvent>()

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
