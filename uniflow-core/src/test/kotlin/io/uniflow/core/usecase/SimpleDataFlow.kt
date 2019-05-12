package io.uniflow.core.usecase

import io.uniflow.core.StackDataFlow
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import kotlinx.coroutines.Dispatchers
import java.util.*

data class Todo(val title: String, val done: Boolean)

class SimpleDataFlow : StackDataFlow() {

    fun firstAction() =
            setState(Dispatchers.IO) {
                SimpleState()
            } onError { error ->
                sendEvent(UIEvent.Fail("Fail!", error))
            }

    fun secondAction(id: String) = setState {
        SimpleState(id)
    }

    override suspend fun onError(error: Throwable) {
        setState { UIState.Failed("Failed State", error) }
    }
}

data class SimpleState(val uuid: String = UUID.randomUUID().toString()) : UIState()
