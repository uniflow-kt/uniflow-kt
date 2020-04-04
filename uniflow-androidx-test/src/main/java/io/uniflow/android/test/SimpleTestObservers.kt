package io.uniflow.android.test

import androidx.lifecycle.Observer
import io.uniflow.androidx.flow.AndroidDataFlow
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

class TestObserver<T> : Observer<T> {
    val elements = arrayListOf<T>()

    override fun onChanged(t: T?) {
        t?.let { elements.add(it) }
    }
}

data class TestViewObserver(private val states: TestObserver<UIState>, private val events: TestObserver<Event<UIEvent>>) {
    fun hasState(state: UIState) = hasState(state, states.elements.lastIndex)
    fun hasState(state: UIState, index: Int) = assert(states.elements[index] == state) { "should have state '$state' at $index but was '${states.elements[index]}'" }
    fun hasStates(vararg states: UIState) = assert(this.states.elements == states) { "should have states [$states] but was [${this.states.elements}]" }
    fun hasNoState() = assert(this.states.elements.isEmpty()) { "should have no state but was [${this.states.elements}]" }
    val lastState: UIState?
        get() = states.elements[states.elements.lastIndex]
    val statesCount
        get() = states.elements.count()

    fun hasEvent(event: UIEvent) = hasEvent(event, events.elements.lastIndex)
    fun hasEvent(event: UIEvent, index: Int) = assert(events.elements[index] == event) { "should have event '$event' at $index but was [${this.events.elements[index]}]" }
    fun hasEvent(vararg events: UIEvent) = assert(this.events.elements == events) { "should have has events [$events] but was [${this.events.elements}]" }
    fun hasNoEvent() = assert(this.events.elements.isEmpty()) { "should have no event but was [${this.events.elements}]" }
    val eventsCount
        get() = events.elements.count()
    val lastEvent
        get() = events.elements[events.elements.lastIndex].take()
}

fun AndroidDataFlow.createTestObserver(): TestViewObserver {
    val viewStates: TestObserver<UIState> = TestObserver()
    val viewEvents: TestObserver<Event<UIEvent>> = TestObserver()
    states.observeForever(viewStates)
    events.observeForever(viewEvents)
    return TestViewObserver(viewStates, viewEvents)
}