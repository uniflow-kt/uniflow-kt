package io.uniflow.test.impl

import io.uniflow.core.flow.DataPublisher
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onMain

class SimpleDataPublisher(defaultState: UIState) : DataPublisher {

    val states = arrayListOf<UIState>()
    val data = arrayListOf<UIData>()
    val events = arrayListOf<UIEvent>()

    init {
        UniFlowLogger.log("init state: $defaultState")
        states.add(defaultState)
        data.add(defaultState)
    }

    override suspend fun getState(): UIState = states.last()
    override suspend fun getStateOrNull(): UIState? = states.lastOrNull()

    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) {
        onMain(immediate = true) {
            UniFlowLogger.log("state <($$pushStateUpdate)- $state")
            data.add(state)
            states.add(state)
        }
    }

    override suspend fun publishEvent(event: UIEvent) {
        onMain(immediate = true) {
            UniFlowLogger.log("event <- $event")
            data.add(event)
            events.add(event)
        }
    }
}