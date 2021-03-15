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
//package io.uniflow.android
//
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import io.uniflow.android.stateflow.stateFlowPublisher
//import io.uniflow.core.flow.DataFlow
//import io.uniflow.core.flow.data.UIState
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.channels.Channel
//
///**
// * Android implementation of [DataFlow].
// * This is also a [ViewModel].
// * Its [coroutineScope] equals [viewModelScope].
// *
// * @param defaultCapacity
// * The default capacity of this `DataFlow`.
// * Any state actions dispatched using [setState] will be added to the buffer unless it's full.
// * Defaults to [Channel.BUFFERED].
// *
// * @param defaultDispatcher The default [CoroutineDispatcher] on which state actions are dispatched.
// * Defaults to [Dispatchers.IO].
// */
//abstract class AndroidAsyncDataFlow(
//    defaultState: UIState = UIState.Empty,
//    savedStateHandle: SavedStateHandle? = null,
//    config: AndroidDataFlowConfig = AndroidDataFlowConfig(
//        defaultDataPublisher = stateFlowPublisher()
//    )
//) : AndroidDataFlow(defaultState, savedStateHandle, config)