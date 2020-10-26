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
package io.uniflow.androidx.flow

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.uniflow.core.flow.EventConsumer
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
    vm.dataPublisher.states.observe(this, Observer { state: UIState? ->
        state?.let {
            UniFlowLogger.debug("onStates - $this <- $state")
            handleStates(state)
        }
    })
}

/**
 * Listen incoming events (Event<UIEvent>) on given AndroidDataFlow
 */
fun LifecycleOwner.onEvents(vm: AndroidDataFlow, handleEvents: (UIEvent) -> Unit) {
    val consumer = EventConsumer()
    vm.dataPublisher.events.observe(this, Observer { event ->
        event?.let {
            UniFlowLogger.debug("onEvents - $this <- $event")
            consumer.onEvent(event)
        }
    })
}