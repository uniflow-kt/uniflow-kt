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
import io.uniflow.core.flow.*
import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class AndroidDataFlow : ViewModel(), DataFlow {

    override val coroutineContext: CoroutineContext = viewModelScope.coroutineContext

    private val _states = MutableLiveData<UIState>()
    val states: LiveData<UIState>
        get() = _states

    private val _events = MutableLiveData<Event<UIEvent>>()
    val events: LiveData<Event<UIEvent>>
        get() = _events

    override suspend fun sendEvent(event: UIEvent): UIState? {
        onMain {
            UniFlowLogger.logEvent(event)
            _events.value = Event(event)
        }
        return null
    }

    override suspend fun applyState(state: UIState) {
        onMain {
            UniFlowLogger.logState(state)
            _states.value = state
        }
    }

    override fun getCurrentState(): UIState? = _states.value

    override fun onCleared() {
        super.onCleared()
        try {
            viewModelScope.cancel()
        } catch (e: Exception) {
            UniFlowLogger.logError("AndroidDataFlow cancel error", e)
        }
    }
}