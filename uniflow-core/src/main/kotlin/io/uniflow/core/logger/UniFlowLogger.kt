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
package io.uniflow.core.logger

import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState

/**
 * Event Logger
 *
 * @author Arnaud Giuliani
 */
object UniFlowLogger : Logger {

    private var logger: Logger = SimpleMessageLogger()

    fun init(logger: Logger) {
        this.logger = logger
    }

    override fun log(message: String) = logger.log(message)

    override fun logState(state: UIState) = logger.logState(state)

    override fun logEvent(event: UIEvent) = logger.logEvent(event)

    override fun logError(errorMessage: String, error: Exception?) = logger.logError(errorMessage, error)

    const val TAG = "[UniFlow]"
    const val FUN_TAG = "🦄"
}