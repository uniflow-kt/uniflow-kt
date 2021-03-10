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

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.uniflow.android.AndroidDataFlow
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
fun LifecycleOwner.onStates(vm: AndroidDataFlow, handleStates: (UIState) -> Unit) {
    vm.defaultDataPublisher.onStates(this, handleStates)
}

/**
 * Listen incoming states (UIState) on given AndroidDataFlow
 */
fun LiveDataPublisher.onStates(owner: LifecycleOwner, handleStates: (UIState) -> Unit) {
    var lastState: UIState? = null
    states.observe(owner, Observer { state: UIState? ->
        state?.let {
            UniFlowLogger.debug("onStates - $owner - last state: $lastState")
            if (lastState != state) {
                UniFlowLogger.debug("onStates - $owner <- $state")
                handleStates(state)
                lastState = state
            } else {
                UniFlowLogger.debug("onStates - already received -  $owner <- $state")
            }
        }
    })
}

fun LiveDataPublisher.onEvents(owner: LifecycleOwner, handleEvents: (UIEvent) -> Unit) {
    val consumer = EventConsumer(consumerId)
    events.observe(owner, Observer { event ->
        event?.let {
            consumer.onEvent(event)?.let {
                UniFlowLogger.debug("onEvents - $owner <- $event")
                handleEvents(it)
            } ?: UniFlowLogger.debug("onEvents - already received - $owner <- $event")
        }
    })
}

/**
 * Listen incoming events (Event<UIEvent>) on given AndroidDataFlow
 */
fun LifecycleOwner.onEvents(vm: AndroidDataFlow, handleEvents: (UIEvent) -> Unit) {
    vm.defaultDataPublisher.onEvents(this, handleEvents)
}

internal val Any.consumerId: String
    get() = this::class.simpleName ?: error("can't get consumerId for $this")