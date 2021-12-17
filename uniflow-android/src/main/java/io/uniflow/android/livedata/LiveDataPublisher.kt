package io.uniflow.android.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.uniflow.android.AndroidDataFlow
import io.uniflow.core.flow.DataPublisher
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onMain

open class LiveDataPublisher(
    val defaultState: UIState,
    _tag: String? = null
) : DataPublisher {

    val tag = _tag ?: this.toString()

    internal val _states = MutableLiveData<UIState>(defaultState)
    val states: LiveData<UIState> = _states
    internal val _events = MutableLiveData<Event<UIEvent>>()
    val events: LiveData<Event<UIEvent>> = _events

    private var _currentState: UIState = defaultState
    override fun getState(): UIState = _currentState

    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) {
        onMain(immediate = true) {
            UniFlowLogger.logState(state)
            UniFlowLogger.debug("$tag new state $state")
            _currentState = state
            if (pushStateUpdate) {
                UniFlowLogger.debug("$tag --> $state")
                _states.value = state
            }
        }
    }

    override suspend fun publishEvent(event: UIEvent) {
        onMain(immediate = true) {
            UniFlowLogger.debug("$tag --> $event")
            UniFlowLogger.logEvent(event)
            _events.value = Event(content = event)
        }
    }
}

fun liveDataPublisher(defaultState: UIState = UIState.Empty, tag: String? = null) = LiveDataPublisher(defaultState, tag)

val AndroidDataFlow.states: LiveData<UIState> get() = (defaultDataPublisher as LiveDataPublisher).states