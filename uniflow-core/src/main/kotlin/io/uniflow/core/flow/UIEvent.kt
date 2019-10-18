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

/**
 * Data Flow UI Event
 *
 * @author Arnaud Giuliani
 */
open class UIEvent {
    object Pending : UIEvent()
    object Success : UIEvent()
    data class Fail(val message: String? = null, val error: Exception? = null, val state: UIState? = null) : UIEvent()
    data class BadOrWrongState(val currentState: UIState? = null) : UIEvent()

    data class DatabaseRequestFailure(val error: Exception) : UIEvent()
    data class NetworkRequestFailure(val error: Exception) : UIEvent()
}