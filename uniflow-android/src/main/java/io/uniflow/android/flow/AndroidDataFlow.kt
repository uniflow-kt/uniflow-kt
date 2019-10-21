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
package io.uniflow.android.flow

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.*
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onIO
import io.uniflow.core.threading.onMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.isActive

abstract class AndroidDataFlow(defaultCapacity: Int = 10) : ViewModel(), DataFlow {

    private val viewModelJob = SupervisorJob()
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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
            _states.value = state
        }
    }

    override val state: UIState?
        get() = _states.value

    override val actorFlow = coroutineScope.actor<StateAction>(UniFlowDispatcher.dispatcher.default(), capacity = defaultCapacity) {
        for (action in channel) {
            if (coroutineScope.isActive) {
                UniFlowLogger.log("AndroidActorFlow action $action")
                onIO {
                    proceedAction(action)
                }
            } else {
                UniFlowLogger.log("AndroidActorFlow action $action cancelled")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        actorFlow.close()
    }
}