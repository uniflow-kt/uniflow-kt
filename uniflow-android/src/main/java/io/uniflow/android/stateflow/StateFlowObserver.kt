///*
// * Copyright 2019 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package io.uniflow.android.stateflow
//
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.lifecycleScope
//import io.uniflow.android.AndroidDataFlow
//import io.uniflow.android.consumerId
//import io.uniflow.core.flow.data.EventConsumer
//import io.uniflow.core.flow.data.UIEvent
//import io.uniflow.core.flow.data.UIState
//import io.uniflow.core.logger.UniFlowLogger
//import io.uniflow.core.threading.launchOnMain
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.launch
//
///**
// * DataFlow Observers for states & events
// *
// * @author Arnaud Giuliani
// */
//
///**
// * Listen incoming states (UIState) on given AndroidDataFlow
// */
//fun LifecycleOwner.onAsyncStates(vm: AndroidDataFlow, handleStates: suspend (UIState) -> Unit) {
//    (vm.defaultDataPublisher as? StateFlowPublisher)?.onStates(this, handleStates)
//}
//
///**
// * Listen incoming events (Event<UIEvent>) on given AndroidDataFlow
// */
//fun LifecycleOwner.onAsyncEvents(vm: AndroidDataFlow, handleEvents: suspend (UIEvent) -> Unit) {
//    (vm.defaultDataPublisher as? StateFlowPublisher)?.onEvents(this, handleEvents)
//}
//
///**
// * Listen incoming states (UIState) on given AndroidDataFlow
// */
//fun StateFlowPublisher.onStates(owner: LifecycleOwner, handleStates: suspend (UIState) -> Unit) {
//    owner.lifecycleScope.launchOnMain {
//        onStates(owner, handleStates)
//    }
//}
//
//suspend fun StateFlowPublisher.onStates(
//    owner: Any,
//    handleStates: suspend (UIState) -> Unit
//) {
//    var lastState: UIState? = null
//    states.collect { state ->
//        UniFlowLogger.debug("onStates - $owner - last state: $lastState")
//        if (lastState != state) {
//            UniFlowLogger.debug("onStates - $owner <- $state")
//            handleStates(state)
//            lastState = state
//        } else {
//            UniFlowLogger.debug("onStates - already received -  $owner <- $state")
//        }
//    }
//}
//
//fun StateFlowPublisher.onEvents(owner: LifecycleOwner, handleEvents: suspend (UIEvent) -> Unit) {
//    owner.lifecycleScope.launchOnMain {
//        onEvents(owner, handleEvents)
//    }
//}
//
//suspend fun StateFlowPublisher.onEvents(
//    owner: Any,
//    handleEvents: suspend (UIEvent) -> Unit
//) {
//    val consumer = EventConsumer(owner.consumerId)
//    events.collect { event ->
//        event?.let {
//            consumer.onEvent(event)?.let {
//                UniFlowLogger.debug("onEvents - $owner <- $event")
//                handleEvents(it)
//            } ?: UniFlowLogger.debug("onEvents - already received - $owner <- $event")
//        }
//    }
//}