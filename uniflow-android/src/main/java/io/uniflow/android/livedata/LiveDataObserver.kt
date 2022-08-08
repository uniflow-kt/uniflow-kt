/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.uniflow.android.livedata

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import io.uniflow.android.AndroidDataFlow
import io.uniflow.android.consumerId
import io.uniflow.core.flow.data.EventConsumer
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger

/**
 * DataFlow Observers for states & events
 *
 * @author Arnaud Giuliani
 */

/**
 * Listen incoming states (UIState) on given AndroidDataFlow
 */
fun Fragment.onStates(vm: AndroidDataFlow, handleStates: (UIState) -> Unit) {
    (vm.defaultDataPublisher as? LiveDataPublisher)?.onStates(this.viewLifecycleOwner, handleStates)
}

/**
 * Listen incoming states (UIState) on given AndroidDataFlow
 */
fun LifecycleOwner.onStates(vm: AndroidDataFlow, handleStates: (UIState) -> Unit) {
    (vm.defaultDataPublisher as? LiveDataPublisher)?.onStates(this, handleStates)
}

/**
 * Listen incoming events (Event<UIEvent>) on given AndroidDataFlow
 */
fun Fragment.onEvents(vm: AndroidDataFlow, handleEvents: (UIEvent) -> Unit) {
    (vm.defaultDataPublisher as? LiveDataPublisher)?.onEvents(this.viewLifecycleOwner, this.consumerId, handleEvents)
}

/**
 * Listen incoming events (Event<UIEvent>) on given AndroidDataFlow
 */
fun LifecycleOwner.onEvents(vm: AndroidDataFlow, handleEvents: (UIEvent) -> Unit) {
    (vm.defaultDataPublisher as? LiveDataPublisher)?.onEvents(this, this.consumerId, handleEvents)
}


/**
 * Listen incoming states (UIState) on given AndroidDataFlow
 */
fun LiveDataPublisher.onStates(owner: LifecycleOwner, handleStates: (UIState) -> Unit) {
    states.observe(owner, { state: UIState? ->
        state?.let {
            UniFlowLogger.debug("onStates - $owner <- $state")
            handleStates(state)
        }
    })
}

fun LiveDataPublisher.onEvents(owner: LifecycleOwner, ownerId: String, handleEvents: (UIEvent) -> Unit) {
    val consumer = EventConsumer(ownerId)
    events.observe(owner, { event ->
        event?.let {
            consumer.onEvent(event)?.let {
                UniFlowLogger.debug("onEvents - $owner <- $event")
                handleEvents(it)
            } ?: UniFlowLogger.debug("onEvents - already received - $owner <- $event")
        }
    })
}

/**
 * Only reacts to state changes of a specific type, useful for exhaustive when statements
 */
inline fun <reified TState : UIState> Fragment.onStatesOfType(
    vm: AndroidDataFlow,
    crossinline handleStates: (TState) -> Unit
) {
    onStates(vm) {
        if (it is TState) handleStates(it)
    }
}

/**
 * Only handles events are a specific type, useful for exhaustive when statements
 */
inline fun <reified TEvent : UIEvent> Fragment.onEventsOfType(
    vm: AndroidDataFlow,
    crossinline handleEvents: (TEvent) -> Unit
) {
    onEvents(vm) {
        if (it is TEvent) handleEvents(it)
    }
}

/**
 * Only reacts to state changes of a specific type, useful for exhaustive when statements
 */
inline fun <reified TState : UIState> LifecycleOwner.onStatesOfType(
    vm: AndroidDataFlow,
    crossinline handleStates: (TState) -> Unit
) {
    onStates(vm) {
        if (it is TState) handleStates(it)
    }
}

/**
 * Only handles events are a specific type, useful for exhaustive when statements
 */
inline fun <reified TEvent : UIEvent> LifecycleOwner.onEventsOfType(
    vm: AndroidDataFlow,
    crossinline handleEvents: (TEvent) -> Unit
) {
    onEvents(vm) {
        if (it is TEvent) handleEvents(it)
    }
}
