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
package io.uniflow.core.flow.data

import java.lang.Exception

/**
 * Data Flow UI State
 *
 * @author Arnaud Giuliani
 */
open class UIState : UIData {
    object Empty : UIState() {
        override fun toString(): String = "Empty"
    }

    object Loading : UIState() {
        override fun toString(): String = "Loading"
    }

    object Success : UIState() {
        override fun toString(): String = "Success"
    }

    data class Failed(val message: String? = null, val error: UIError? = null, val state: UIState? = null) : UIState() {
        constructor(message: String? = null) : this(message, null as UIError)
        constructor(message: String? = null, state: UIState? = null) : this(message, null as UIError, state)
        constructor(message: String? = null, error: Throwable? = null, state: UIState? = null) : this(message, error?.toUIError(), state)
        constructor(message: String? = null, error: Exception? = null, state: UIState? = null) : this(message, error?.toUIError(), state)
    }
}


