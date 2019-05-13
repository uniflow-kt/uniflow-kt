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
 * distributed under the License is distributed launchOn an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.uniflow.androidx.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.Action
import io.uniflow.core.flow.UIState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch

/**
 * AndroidDataFlow
 * Unidirectional dataflow with states & events
 *
 * @author Arnaud Giuliani
 */
abstract class AndroidActorFlow : AndroidDataFlow() {

    override fun executeAction(action: Action) {
        flowActor.offer(action)
    }

    private val flowActor = actor<Action>(UniFlowDispatcher.dispatcher.default(), capacity = 10) {
        for (action in channel) {
            GlobalScope.launch(UniFlowDispatcher.dispatcher.default()) {
                try {
                    val result = action.actionFunction.invoke(this, getCurrentState())
                    if (result is UIState) {
                        applyState(result)
                    }
                } catch (e: Throwable) {
                    handleActionError(action, e)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        flowActor.close()
    }
}