package io.uniflow.android.test

import androidx.lifecycle.Observer
import io.mockk.mockk
import io.uniflow.android.AndroidDataFlow
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

data class MockViewObserver(val states: Observer<UIState>, val events: Observer<Event<UIEvent>>) {
    infix fun hasState(state: UIState) = states.onChanged(state)
    infix fun hasEvent(event: UIEvent) = events.onChanged(Event(content = event))
}

fun AndroidDataFlow.mockObservers(): MockViewObserver {
    val viewStates: Observer<UIState> = mockk(relaxed = true)
    val viewEvents: Observer<Event<UIEvent>> = mockk(relaxed = true)
    defaultDataPublisher.states.observeForever(viewStates)
    defaultDataPublisher.events.observeForever(viewEvents)
    return MockViewObserver(viewStates, viewEvents)
}