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
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.Logger
import io.uniflow.core.logger.UniFlowLogger

/**
 * Android Logger
 *
 * @author Arnaud Giuliani
 */
class AndroidMessageLogger(val tag: String = UniFlowLogger.TAG, val showDebug: Boolean = false) : Logger {

    val dbg_th: String = if (showDebug) "[${Thread.currentThread().name}] " else ""

    override fun debug(message: String) {
        if (showDebug){
            Log.d(tag,"$dbg_th $message")
        }
    }

    override fun log(message: String) {
        Log.i(tag,"$dbg_th $message")
    }

    override fun logState(state: UIState) {
        Log.i(tag,"$dbg_th[STATE] - $state")
    }

    override fun logEvent(event: UIEvent) {
        Log.i(tag,"$dbg_th<EVENT> - $event")
    }

    override fun logError(errorMessage: String, error: Exception?) {
        Log.e(tag,"$dbg_th!ERROR! - $errorMessage :: $error")
    }
}