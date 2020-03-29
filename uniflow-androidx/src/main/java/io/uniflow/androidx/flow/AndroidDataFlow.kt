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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.*
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onMain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel

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
) : ViewModel(),
    DataFlow,
    UIDataPublisher {

    final override val coroutineScope = viewModelScope

    private val uiDataManager by lazy { UIDataManager(this, defaultState) }
    final override val scheduler =
        ActionFlowScheduler(uiDataManager, coroutineScope, defaultDispatcher, defaultCapacity)

    private val _states = MutableLiveData<UIState>()
    val states: LiveData<UIState> = _states

    private val _events = MutableLiveData<Event<UIEvent>>()
    val events: LiveData<Event<UIEvent>> = _events

    init {
        action { setState { defaultState } }
    }

    final override fun getCurrentState() = uiDataManager.currentState

    final override suspend fun publishState(state: UIState) {
        onMain(immediate = true) {
            _states.value = state
        }
    }

    final override suspend fun sendEvent(event: UIEvent) {
        onMain(immediate = true) {
            _events.value = Event(event)
        }
    }

    @CallSuper
    override fun onCleared() {
        scheduler.close()
        super.onCleared()
    }
}
