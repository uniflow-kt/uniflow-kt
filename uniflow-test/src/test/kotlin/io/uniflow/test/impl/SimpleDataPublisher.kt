package io.uniflow.test.impl

import io.uniflow.core.flow.UIDataPublisher
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onMain

class SimpleDataPublisher : UIDataPublisher {

    val states = arrayListOf<UIState>()
    val data = arrayListOf<UIData>()
    val events = arrayListOf<UIEvent>()

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