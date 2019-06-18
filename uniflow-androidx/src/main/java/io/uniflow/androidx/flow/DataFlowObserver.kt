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
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState

/**
 * DataFlow Observers for states & events
 *
 * @author Arnaud Giuliani
 */

fun LifecycleOwner.onStates(vm: AndroidDataFlow, handleStates: (UIState) -> Unit) {
    vm.states.observe(this, Observer { state: UIState? -> state?.let { handleStates(state) } })
}

fun LifecycleOwner.onEvents(vm: AndroidDataFlow, handleEvents: (UIEvent?) -> Unit) {
    vm.events.observe(this, Observer { event -> event?.let { handleEvents(event.get()) } })
}

fun LifecycleOwner.onBroadcastEvents(vm: AndroidDataFlow, handleEvents: (UIEvent?) -> Unit) {
    vm.events.observe(this, Observer { event -> event?.let { handleEvents(event.peekContent()) } })
}