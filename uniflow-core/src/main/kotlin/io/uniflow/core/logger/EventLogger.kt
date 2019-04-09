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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.uniflow.core.logger

/**
 * Event Logger
 *
 * @author Arnaud Giuliani
 */
object EventLogger : Logger {

    private const val TAG = "[UniFlow]"

    val loggers: ArrayList<MessageLogger> = arrayListOf()

    override fun log(message: String) {
        loggers.forEach { it.log(TAG, message) }
    }

    override fun error(message: String, error: Throwable?) {
        loggers.forEach { it.error(TAG, message, error) }
    }
}