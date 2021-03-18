package io.uniflow.android.test

import androidx.lifecycle.Observer
import io.mockk.mockk
import io.uniflow.android.AndroidDataFlow
import io.uniflow.android.livedata.LiveDataPublisher
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

data class MockViewObserver(val states: Observer<UIState>, val events: Observer<Event<UIEvent>>) {
    infix fun hasState(state: UIState) = states.onChanged(state)
    infix fun hasEvent(event: UIEvent) = events.onChanged(Event(content = event))
}

fun AndroidDataFlow.mockObservers(
    viewStates: Observer<UIState> = mockk(name = "${this::class.java.simpleName}-states",relaxed = true),
    viewEvents: Observer<Event<UIEvent>> = mockk(name = "${this::class.java.simpleName}-events",relaxed = true)
): MockViewObserver {
    val liveDataPublisher = defaultDataPublisher as LiveDataPublisher
    liveDataPublisher.states.observeForever(viewStates)
    liveDataPublisher.events.observeForever(viewEvents)
    return MockViewObserver(viewStates, viewEvents)
}

fun AndroidDataFlow.mockObservers(): MockViewObserver {
    val currentName = this::class.java.simpleName
    val viewStates: Observer<UIState> = mockk(name = "$currentName-states",relaxed = true)
    val viewEvents: Observer<Event<UIEvent>> = mockk(name = "$currentName-events",relaxed = true)
    val liveDataPublisher = defaultDataPublisher as LiveDataPublisher
    liveDataPublisher.states.observeForever(viewStates)
    liveDataPublisher.events.observeForever(viewEvents)
    return MockViewObserver(viewStates, viewEvents)
}