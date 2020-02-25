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
import io.uniflow.core.flow.ActionFlowScheduler
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.UIDataManager
import io.uniflow.core.flow.UIDataPublisher
import io.uniflow.core.flow.data.Event
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel

/**
 * Android implementation of [DataFlow].
 * This is also a [ViewModel].
 * Its [coroutineScope] uses [Dispatchers.Main] and is automatically cancelled if the `ViewModel`
 * is cleared.
 *
 * @param defaultCapacity
 * The default capacity of this `DataFlow`.
 * Any state actions dispatched using [setState] will be added to the buffer unless it's full.
 * Defaults to [Channel.BUFFERED].
 *
 * @param defaultDispatcher The default [CoroutineDispatcher] on which state actions are dispatched.
 * Defaults to [Dispatchers.IO].
 */
abstract class AndroidDataFlow<S : UIState, E : UIEvent>(
        defaultState: UIState,
        defaultCapacity: Int = Channel.BUFFERED,
        defaultDispatcher: CoroutineDispatcher = UniFlowDispatcher.dispatcher.io()
) : DataFlow<S, E>, UIDataPublisher, ViewModel() {

    private val supervisorJob = SupervisorJob()
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)
    private val uiDataManager = UIDataManager(this, defaultState)
    override val scheduler: ActionFlowScheduler = ActionFlowScheduler(uiDataManager, coroutineScope, defaultDispatcher, defaultCapacity)

    private val _states = MutableLiveData<UIState>()
    val states: LiveData<UIState>
        get() = _states

    private val _events = MutableLiveData<Event<UIEvent>>()
    val events: LiveData<Event<UIEvent>>
        get() = _events

    init {
        action { setState { defaultState } }
    }

    override fun onCleared() {
        super.onCleared()
        supervisorJob.cancel()
        scheduler.close()
    }
}
