package io.uniflow.android.livedata

import androidx.lifecycle.SavedStateHandle
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onMain

class PersistentLiveDataPublisher(
    defaultState: UIState,
    private val savedStateHandle: SavedStateHandle,
    _tag: String? = null
) : LiveDataPublisher(defaultState,_tag) {

    init {
        restoreState()
    }

    private fun restoreState(): UIState? {
        return savedStateHandle.get<UIState>(tag)?.let { state ->
            UniFlowLogger.debug("$tag --> restore --> $state")
            _states.value = state
            state
        }
    }

    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) {
        super.publishState(state, pushStateUpdate)
        saveState(state)
    }

    private fun saveState(state: UIState) {
        savedStateHandle.set(tag, state)
    }
}

fun persistentLiveDataPublisher(savedStateHandle: SavedStateHandle, defaultState: UIState = UIState.Empty, tag: String? = null) = PersistentLiveDataPublisher(defaultState, savedStateHandle, tag)