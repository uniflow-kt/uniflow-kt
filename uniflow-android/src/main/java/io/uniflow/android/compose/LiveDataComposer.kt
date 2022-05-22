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
package io.uniflow.android.compose

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import io.uniflow.android.AndroidDataFlow
import io.uniflow.android.consumerId
import io.uniflow.android.livedata.LiveDataPublisher
import io.uniflow.core.flow.data.EventConsumer
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger

/**
 * DataFlow Observers for states & events handled with Jetpack Compose
 *
 * @author Stephan Schuster
 */

/**
 * Listen incoming states (UIState) on given AndroidDataFlow
 */
@SuppressLint("ComposableNaming")
@Composable
fun Fragment.onStates(vm: AndroidDataFlow, handleStates: @Composable (UIState) -> Unit) {
    (vm.defaultDataPublisher as? LiveDataPublisher)?.onStates(this.viewLifecycleOwner, handleStates)
}

/**
 * Listen incoming states (UIState) on given AndroidDataFlow
 */
@SuppressLint("ComposableNaming")
@Composable
fun LifecycleOwner.onStates(vm: AndroidDataFlow, handleStates: @Composable (UIState) -> Unit) {
    (vm.defaultDataPublisher as? LiveDataPublisher)?.onStates(this, handleStates)
}

/**
 * Listen incoming events (Event<UIEvent>) on given AndroidDataFlow
 */
@SuppressLint("ComposableNaming")
@Composable
fun Fragment.onEvents(vm: AndroidDataFlow, handleEvents: @Composable (UIEvent) -> Unit) {
    (vm.defaultDataPublisher as? LiveDataPublisher)?.onEvents(this.viewLifecycleOwner, this.consumerId, handleEvents)
}

/**
 * Listen incoming events (Event<UIEvent>) on given AndroidDataFlow
 */
@SuppressLint("ComposableNaming")
@Composable
fun LifecycleOwner.onEvents(vm: AndroidDataFlow, handleEvents: @Composable (UIEvent) -> Unit) {
    (vm.defaultDataPublisher as? LiveDataPublisher)?.onEvents(this, this.consumerId, handleEvents)
}


/**
 * Listen incoming states (UIState) on given AndroidDataFlow
 */
@SuppressLint("ComposableNaming")
@Composable
fun LiveDataPublisher.onStates(owner: LifecycleOwner, handleStates: @Composable (UIState) -> Unit) {
    states.observeAsState().value?.let { state ->
        UniFlowLogger.debug("onStates - $owner <- $state")
        handleStates(state)
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun LiveDataPublisher.onEvents(owner: LifecycleOwner, ownerId: String, handleEvents: @Composable (UIEvent) -> Unit) {
    val consumer = EventConsumer(ownerId)
    events.observeAsState().value?.let { event ->
        consumer.onEvent(event)?.let {
            UniFlowLogger.debug("onEvents - $owner <- $event")
            handleEvents(it)
        } ?: UniFlowLogger.debug("onEvents - already received - $owner <- $event")
    }
}
