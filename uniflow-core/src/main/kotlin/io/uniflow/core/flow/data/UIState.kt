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
    data class Failed(val message: String? = null, val error: Throwable? = null, val state: UIState? = null) : UIState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Failed) return false

            if (message != other.message) return false
            if (error?.javaClass != other.error?.javaClass) return false
            if (error?.message != other.error?.message) return false
            if (state != other.state) return false

            return true
        }

        override fun hashCode(): Int {
            var result = message?.hashCode() ?: 0
            result = 31 * result + (error?.hashCode() ?: 0)
            result = 31 * result + (state?.hashCode() ?: 0)
            return result
        }
    }
}
