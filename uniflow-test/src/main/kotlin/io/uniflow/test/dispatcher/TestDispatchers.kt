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
package io.uniflow.test.dispatcher

import io.uniflow.core.dispatcher.UniFlowDispatcherConfiguration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

/**
 * Dispatchers Configuration for Tests
 *
 * @author Arnaud Giuliani
 */
@ExperimentalCoroutinesApi
class TestDispatchers(
    private val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : UniFlowDispatcherConfiguration {
    override fun main() = testCoroutineDispatcher
    override fun default() = testCoroutineDispatcher
    override fun io() = testCoroutineDispatcher
}
