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
import io.uniflow.android.mvvm.AsyncViewModel
import io.uniflow.core.dispatcher.DataFlowDispatchers.dispatcherConfig
import io.uniflow.core.flow.Event
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.logger.EventLogger
import io.uniflow.core.mvvm.ErrorFunction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

/**
 * DataFlow
 *
 * Unidirectional dataflow with states & events
 *
 * @author Arnaud Giuliani
 */
open class DataFlow : AsyncViewModel() {

    private val _states = MutableLiveData<UIState>()
    val states: LiveData<UIState>
        get() = _states

    private val _events = MutableLiveData<Event<UIEvent>>()
    val events: LiveData<Event<UIEvent>>
        get() = _events

    suspend fun sendEvent(event: UIEvent): UIState? {
        withContext(dispatcherConfig.ui()) {
            EventLogger.log("UI Event - $event")
            _events.value = Event(event)
        }
        return null
    }

    fun getCurrentState(): UIState? = _states.value

    suspend fun applyState(state: UIState) {
        withContext(dispatcherConfig.ui()) {
            EventLogger.log("UI State - $state")
            _states.value = state
        }
    }

    fun <U : UIState> setState(updateFunction: UpdateFunction<U>) {
        setState(updateFunction, null)
    }

    fun <U : UIState> setState(updateFunction: UpdateFunction<U>, errorFunction: ErrorFunction?) {
        launchAsync(
                {
                    val newState = updateFunction(getCurrentState())
                    when {
                        newState != null -> applyState(newState)
                        else -> EventLogger.log("UI State - no update")
                    }
                },
                { error ->
                    EventLogger.log("Error while setState - $error")
                    when {
                        errorFunction != null -> errorFunction(error)
                        else -> onError(error)
                    }
                })
    }

    fun withState(actionFunction: ActionFunction, errorFunction: ErrorFunction?) {
        launchAsync(
                {
                    actionFunction(getCurrentState())
                },
                { error ->
                    EventLogger.log("Error while setState - $error")
                    when {
                        errorFunction != null -> errorFunction(error)
                        else -> onError(error)
                    }
                })
    }

    fun withState(actionFunction: ActionFunction) = withState(actionFunction, null)

}

typealias ActionFunction = suspend CoroutineScope.(UIState?) -> Unit
typealias UpdateFunction<U> = suspend CoroutineScope.(UIState?) -> U?