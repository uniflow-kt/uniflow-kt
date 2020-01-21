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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.*
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onMain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

abstract class AndroidDataFlow(
    defaultCapacity: Int = Channel.BUFFERED,
    override val defaultDispatcher: CoroutineDispatcher = UniFlowDispatcher.dispatcher.io()
) : ViewModel(),
    DataFlow {

    override val coroutineScope: CoroutineScope = viewModelScope

    private val _states = MutableLiveData<UIState>()
    val states: LiveData<UIState>
        get() = _states

    private val _events = MutableLiveData<Event<UIEvent>>()
    val events: LiveData<Event<UIEvent>>
        get() = _events

    override suspend fun sendEvent(event: UIEvent): UIState? {
        onMain(immediate = true) {
            UniFlowLogger.logEvent(event)
            _events.value = Event(event)
        }
        return null
    }

    override suspend fun applyState(state: UIState) {
        onMain(immediate = true) {
            UniFlowLogger.logState(state)
            _internalState = state
            _states.value = state
        }
    }

    override suspend fun notifyUpdate(newState: UIState, notificationEvent: UIEvent): UIState? {
        onMain(immediate = true) {
            UniFlowLogger.logState(newState)
            _internalState = newState
            UniFlowLogger.logEvent(notificationEvent)
            _events.value = Event(notificationEvent)
        }
        return null
    }

    private var _internalState: UIState? = null

    override val currentState: UIState?
        get() = _internalState

    override val actorFlow = coroutineScope.actor<StateAction>(UniFlowDispatcher.dispatcher.default(), capacity = defaultCapacity) {
        for (action in channel) {
            if (coroutineScope.isActive) {
                withContext(defaultDispatcher) {
                    proceedAction(action)
                }
            } else {
                UniFlowLogger.log("actor $action cancelled")
            }
        }
    }
}
