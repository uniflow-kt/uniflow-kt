package io.uniflow.android.test

import android.arch.lifecycle.Observer
import io.mockk.mockk
import io.uniflow.android.flow.AndroidDataFlow
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

data class MockedViewObserver(val states: Observer<UIState>, val events: Observer<Event<UIEvent>>) {
    infix fun hasState(state: UIState) = states.onChanged(state)
    infix fun hasEvent(event: UIEvent) = events.onChanged(Event(content = event))
}

fun AndroidDataFlow.mockObservers(): MockedViewObserver {
    val viewStates: Observer<UIState> = mockk(relaxed = true)
    val viewEvents: Observer<Event<UIEvent>> = mockk(relaxed = true)
    dataPublisher.states.observeForever(viewStates)
    dataPublisher.events.observeForever(viewEvents)
    return MockedViewObserver(viewStates, viewEvents)
}