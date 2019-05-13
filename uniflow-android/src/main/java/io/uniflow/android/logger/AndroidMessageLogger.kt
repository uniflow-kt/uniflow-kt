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
package io.uniflow.android.logger

import android.util.Log
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.logger.Logger
import io.uniflow.core.logger.UniFlowLogger.TAG

/**
 * Android Logger
 *
 * @author Arnaud Giuliani
 */
class AndroidMessageLogger : Logger {
    override fun log(message: String) {
        Log.i(TAG, message)
    }

    override fun logState(state: UIState) {
        Log.i(TAG, "[STATE] - $state")
    }

    override fun logEvent(event: UIEvent) {
        Log.i(TAG, "[EVENT] - $event")
    }

    override fun logError(errorMessage: String, error: Throwable?) {
        Log.i(TAG, "[ERROR] - $errorMessage :: $error")
    }

}