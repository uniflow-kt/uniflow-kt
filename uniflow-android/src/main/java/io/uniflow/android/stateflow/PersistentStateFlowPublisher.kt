//package io.uniflow.android.stateflow
//
//import androidx.lifecycle.SavedStateHandle
//import io.uniflow.core.flow.data.UIState
//import io.uniflow.core.logger.UniFlowLogger
//import io.uniflow.core.threading.onMain
//
//
//class PersistentStateFlowPublisher(
//    private val savedStateHandle: SavedStateHandle,
//    defaultState: UIState,
//    _tag: String? = null
//) : StateFlowPublisher(defaultState,_tag) {
//
//    init {
//        restoreState()
//    }
//
//    private fun restoreState(): UIState? {
//        return savedStateHandle.get<UIState>(tag)?.let { state ->
//            UniFlowLogger.debug("$tag --> restore --> $state")
//            _states.value = state
//            state
//        }
//    }
//
//    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) {
//        onMain(immediate = true) {
//            UniFlowLogger.debug("$tag --> $state")
//            _states.value = state
//        }
//        saveState(state)
//    }
//
//    private fun saveState(state: UIState) {
//        savedStateHandle.set(tag, state)
//    }
//}
//
//fun persistentStateFlowPublisher(savedStateHandle: SavedStateHandle,defaultState: UIState = UIState.Empty, tag: String? = null) = PersistentStateFlowPublisher(savedStateHandle,defaultState, tag)