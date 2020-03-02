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

    private val classHashCode = arrayListOf<Int>()

    /**
     * Returns the content and put the event has handled
     */
    fun take(clazz: Class<Any>): T? {
        return if (classHashCode.contains(clazz.hashCode())) {
            null
        } else {
            classHashCode.add(clazz.hashCode())
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peek(clazz: Class<Any>): T {
        if (!classHashCode.contains(clazz.hashCode())) {
            classHashCode.add(clazz.hashCode())
        }

        return content
    }

    /**
     * Check if event was already consumed
     */
    fun isConsumed(clazz: Class<Any>): Boolean {
        return classHashCode.contains(clazz.hashCode())
    }
}