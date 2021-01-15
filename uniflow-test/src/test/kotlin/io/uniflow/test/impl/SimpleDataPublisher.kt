package io.uniflow.test.impl

import io.uniflow.core.flow.DataPublisher
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onMain

class SimpleDataPublisher(val defaultState: UIState, val tag : String) : DataPublisher {

    val states = arrayListOf<UIState>()
    val data = arrayListOf<UIData>()
    val events = arrayListOf<UIEvent>()

    override suspend fun getState(): UIState = states.lastOrNull() ?: defaultState

    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) {
        onMain(immediate = true) {
            UniFlowLogger.log("$tag - new state: $state")
            states.add(state)
            if (pushStateUpdate) {
                UniFlowLogger.log("$tag - state <-- $state")
                data.add(state)
            }
        }
    }

    override suspend fun publishEvent(event: UIEvent) {
        onMain(immediate = true) {
            UniFlowLogger.log("$tag - event <-- $event")
            data.add(event)
            events.add(event)
        }
    }
}