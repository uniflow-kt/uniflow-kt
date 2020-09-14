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

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.*
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlin.reflect.KClass

/**
 * Android implementation of [DataFlow].
 * This is also a [ViewModel].
 * Its [coroutineScope] equals [viewModelScope].
 *
 * @param defaultCapacity
 * The default capacity of this `DataFlow`.
 * Any state actions dispatched using [setState] will be added to the buffer unless it's full.
 * Defaults to [Channel.BUFFERED].
 *
 * @param defaultDispatcher The default [CoroutineDispatcher] on which state actions are dispatched.
 * Defaults to [Dispatchers.IO].
 */
abstract class AndroidDataFlow(
        defaultState: UIState = UIState.Empty,
        defaultCapacity: Int = Channel.BUFFERED,
        defaultDispatcher: CoroutineDispatcher = UniFlowDispatcher.dispatcher.io()
) : ViewModel(), DataFlow {
    private val tag = this.toString()
    val dataPublisher: LiveDataPublisher = LiveDataPublisher(defaultState)
    private val coroutineScope: CoroutineScope = viewModelScope
    private val dataStore: UIDataStore = UIDataStore(dataPublisher, defaultState,tag)
    private val reducer: ActionReducer = ActionReducer(dataStore, coroutineScope, defaultDispatcher, defaultCapacity,tag)
    private val actionDispatcher: ActionDispatcher
        get() = ActionDispatcher(viewModelScope, reducer, dataStore, this,tag)

    final override fun getCurrentState() = actionDispatcher.getCurrentState()
    final override fun <T : UIState> getCurrentStateOrNull(stateClass: KClass<T>): T? = actionDispatcher.getCurrentStateOrNull()
    final override fun action(onAction: ActionFunction<UIState>): ActionFlow = actionDispatcher.action(onAction)
    final override fun action(onAction: ActionFunction<UIState>, onError: ActionErrorFunction): ActionFlow = actionDispatcher.action(onAction, onError)
    final override fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow = actionDispatcher.actionOn(stateClass, onAction)
    final override fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow = actionDispatcher.actionOn(stateClass, onAction, onError)

    @CallSuper
    override fun onCleared() {
        reducer.close()
        super.onCleared()
    }
}
