package io.uniflow.androidx.flow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.uniflow.core.flow.UIDataPublisher
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onMain

class LiveDataPublisher : UIDataPublisher {

    private val _states = MutableLiveData<UIState>()
    val states: LiveData<UIState> = _states
    private val _events = MutableLiveData<Event<UIEvent>>()
    val events: LiveData<Event<UIEvent>> = _events

    override suspend fun publishState(state: UIState) {
        onMain(immediate = true) {
            _states.value = state
        }
    }

    override suspend fun publishEvent(event: UIEvent) {
        onMain(immediate = true) {
            _events.value = Event(event)
        }
    }
}