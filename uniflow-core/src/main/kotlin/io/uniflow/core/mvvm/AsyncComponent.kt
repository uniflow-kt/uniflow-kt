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
package io.uniflow.core.mvvm

import io.uniflow.core.dispatcher.DataFlowDispatchers.dispatcherConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine base component to encapsulate Coroutine Root job & CoroutineContext
 *
 * Give also first async primitives
 *
 * @author Arnaud Giuliani
 */
class AsyncComponent : CoroutineScope {

    val parentJob = Job()
    override val coroutineContext: CoroutineContext = parentJob + dispatcherConfig.ui()

    fun launch(context: CoroutineContext = coroutineContext, function: suspend CoroutineScope.() -> Unit, errorHandling: ErrorFunction? = null): Job? {
        return launch(context = context) {
            try {
                function()
            } catch (e: Throwable) {
                when {
                    errorHandling != null -> errorHandling(e)
                    else -> throw e
                }
            }
        }
    }

    fun cancel(): Unit = coroutineContext.cancel()
}

typealias ErrorFunction = (Throwable) -> Unit