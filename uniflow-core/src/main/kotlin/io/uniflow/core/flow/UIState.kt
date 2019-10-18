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
package io.uniflow.core.flow

import io.uniflow.core.result.DatabaseException
import io.uniflow.core.result.NetworkException

/**
 * Data Flow UI State
 *
 * @author Arnaud Giuliani
 */
open class UIState {
    object Empty : UIState()
    object Loading : UIState()
    object Success : UIState()
    data class Failed(val message: String? = null, val error: Throwable? = null, val state: UIState? = null) : UIState()
}

fun UIState.Failed.isNetworkError() = this.error is NetworkException
fun UIState.Failed.isDatabaseError() = this.error is DatabaseException
