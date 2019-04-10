package io.uniflow.android.test

import android.arch.lifecycle.Observer
import io.mockk.mockk
import io.uniflow.android.flow.DataFlow
import io.uniflow.core.flow.Event
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState

data class DataFlowObserver(val states: Observer<UIState>, val events: Observer<Event<UIEvent>>)

fun DataFlow.mockObservers(): DataFlowObserver {
    val viewStates: Observer<UIState> = mockk(relaxed = true)
    val viewEvents: Observer<Event<UIEvent>> = mockk(relaxed = true)
    states.observeForever(viewStates)
    events.observeForever(viewEvents)
    return DataFlowObserver(viewStates, viewEvents)
}