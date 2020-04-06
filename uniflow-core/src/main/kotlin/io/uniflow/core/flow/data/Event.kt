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

/**
 * Data Flow Event wrapper
 *
 * @author Arnaud Giuliani
 */
data class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and put the event has handled
     */
    fun take(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Return and execute code on given value
     * put the event has handled
     */
    fun take(code: (T) -> Unit) {
        take()?.let { code(it) }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peek(): T = content
}