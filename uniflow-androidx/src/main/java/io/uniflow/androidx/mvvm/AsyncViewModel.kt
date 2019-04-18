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
package io.uniflow.androidx.mvvm

import androidx.lifecycle.ViewModel
import io.uniflow.core.dispatcher.DataFlowDispatchers.dispatcherConfig
import io.uniflow.core.logger.EventLogger
import io.uniflow.core.mvvm.AsyncComponent
import io.uniflow.core.mvvm.ErrorFunction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

/**
 * Spcialized ViewModel for coroutines primitive delegates to AsyncComponent
 *
 * @author Arnaud Giuliani
 */
abstract class AsyncViewModel : ViewModel() {

    val coroutineContext = AsyncComponent()

    /**
     * Run coroutine in current context
     */
    internal fun launch(block: suspend CoroutineScope.() -> Unit, errorHandling: ErrorFunction? = null) = coroutineContext.launch(function = block, errorHandling = errorHandling)

    /**
     * Run coroutine in current context
     */
    internal fun launch(block: suspend CoroutineScope.() -> Unit) = coroutineContext.launch(function = block, errorHandling = ::onError)

    /**
     * Run coroutine in IO context
     */
    internal fun launchAsync(block: suspend CoroutineScope.() -> Unit, errorHandling: ErrorFunction): Job? = coroutineContext.launch(coroutineContext.parentJob + dispatcherConfig.default(), function = block, errorHandling = errorHandling)

    /**
     * Run coroutine in IO context
     */
    internal fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job? = coroutineContext.launch(coroutineContext.parentJob + dispatcherConfig.default(), function = block, errorHandling = ::onError)

    suspend fun onIO(block: suspend CoroutineScope.() -> Unit) =
            withContext(coroutineContext.parentJob + dispatcherConfig.io(), block)

    suspend fun onUI(block: suspend CoroutineScope.() -> Unit) =
            withContext(coroutineContext.parentJob + dispatcherConfig.main(), block)

    suspend fun onBackground(block: suspend CoroutineScope.() -> Unit) =
            withContext(coroutineContext.parentJob + dispatcherConfig.default(), block)

    open fun onError(error: Throwable) {
        EventLogger.log("Error from $this - $error")
        throw error
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }
}
