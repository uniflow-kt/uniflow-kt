package io.uniflow.android.test

import androidx.lifecycle.Observer
import io.mockk.mockk
import io.uniflow.androidx.flow.AndroidDataFlow
import io.uniflow.core.flow.Event
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState

data class DataFlowObserver(val states: Observer<UIState>, val events: Observer<Event<UIEvent>>)

fun AndroidDataFlow.mockObservers(): DataFlowObserver {
    val viewStates: Observer<UIState> = mockk(relaxed = true)
    val viewEvents: Observer<Event<UIEvent>> = mockk(relaxed = true)
    states.observeForever(viewStates)
    events.observeForever(viewEvents)
    return DataFlowObserver(viewStates, viewEvents)
}