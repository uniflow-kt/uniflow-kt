package io.uniflow.android.test.sync

import io.uniflow.android.AndroidDataFlow
import io.uniflow.android.livedata.liveDataPublisher
import io.uniflow.core.flow.data.UIState

class MultiFlow : AndroidDataFlow() {

    val messageDataFlow = liveDataPublisher(UIState.Empty, "publisher_message")
    val counterDataFlow = liveDataPublisher(UIState.Empty, "publisher_count")

    init {
        action {
            messageDataFlow.setState { UIState.Empty }
            counterDataFlow.setState { UIState.Empty }
        }
    }

    fun countString(string: String) = action {
        messageDataFlow.setState { MessageState(string) }
        counterDataFlow.setState { CountState(string.length) }
    }
}

data class MessageState(val message: String) : UIState()