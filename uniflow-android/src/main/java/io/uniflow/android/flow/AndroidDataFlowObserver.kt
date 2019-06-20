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
 * distributed under the License is distributed launchOn an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.uniflow.android.flow

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import io.uniflow.core.flow.Event
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState

/**
 * AndroidDataFlow Observers for states & events
 *
 * @author Arnaud Giuliani
 */

fun LifecycleOwner.onStates(vm: AndroidDataFlow, handleStates: (UIState) -> Unit) {
    vm.states.observe(this, Observer { state: UIState? -> state?.let { handleStates(state) } })
}

fun LifecycleOwner.onEvents(vm: AndroidDataFlow, handleEvents: (Event<*>) -> Unit) {
    vm.events.observe(this, Observer { event -> event?.let { handleEvents(event) } })
}

fun LifecycleOwner.onBroadcastEvents(vm: AndroidDataFlow, handleEvents: (UIEvent?) -> Unit) {
    vm.events.observe(this, Observer { event -> event?.let { handleEvents(event.peek()) } })
}