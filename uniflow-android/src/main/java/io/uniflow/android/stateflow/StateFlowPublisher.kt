//package io.uniflow.android.stateflow
//
//import io.uniflow.core.flow.DataPublisher
//import io.uniflow.core.flow.data.Event
//import io.uniflow.core.flow.data.UIEvent
//import io.uniflow.core.flow.data.UIState
//import io.uniflow.core.logger.UniFlowLogger
//import io.uniflow.core.threading.onMain
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharedFlow
//import kotlinx.coroutines.flow.StateFlow
//
//open class StateFlowPublisher(
//    defaultState: UIState,
//    _tag: String? = null
//) : DataPublisher {
//
//    val tag = _tag ?: this.toString()
//
//    internal val _states = MutableStateFlow(defaultState)
//    val states: StateFlow<UIState> = _states
//    internal val _events = MutableSharedFlow<Event<UIEvent>?>(replay = 0)
//    val events: SharedFlow<Event<UIEvent>?> = _events
//
//    override suspend fun getState(): UIState = _states.value
//
//    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) {
//        onMain(immediate = true) {
//            UniFlowLogger.debug("$tag --> $state")
//            _states.tryEmit(state)
//        }
//    }
//
//    override suspend fun publishEvent(event: UIEvent) {
//        onMain(immediate = true) {
//            UniFlowLogger.debug("$tag --> $event")
//            _events.tryEmit(Event(content = event))
//        }
//    }
//}
//
//fun stateFlowPublisher(defaultState: UIState = UIState.Empty, tag: String? = null) = StateFlowPublisher(defaultState, tag)