package io.uniflow.android.livedata

import androidx.lifecycle.SavedStateHandle
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onMain


class LivePersistentDataPublisher(
    defaultState: UIState,
    private val savedStateHandle: SavedStateHandle,
    private val tag: String
) : LiveDataPublisher(defaultState,tag) {

    init {
        restoreState() ?: defaultState(defaultState)
    }

    private fun restoreState(): UIState? {
        return savedStateHandle.get<UIState>(tag)?.let { state ->
            _states.value = state
            state
        }
    }

    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) {
        onMain(immediate = true) {
            UniFlowLogger.debug("$tag --> $state")
            _states.value = state
        }
        saveState(state)
    }

    private fun saveState(state: UIState) {
        savedStateHandle.set(tag, state)
    }
}

fun livePersistentDataPublisher(defaultState: UIState, tag: String, savedStateHandle: SavedStateHandle) = LivePersistentDataPublisher(defaultState, savedStateHandle, tag)