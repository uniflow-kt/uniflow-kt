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
package io.uniflow.android

import io.uniflow.android.logger.AndroidMessageLogger
import io.uniflow.core.dispatcher.ApplicationDispatcherConfiguration
import io.uniflow.core.dispatcher.DataFlowDispatchers
import io.uniflow.core.dispatcher.DispatcherConfiguration
import io.uniflow.core.logger.EventLogger
import io.uniflow.core.logger.MessageLogger

/**
 * Setup UniFlow
 *
 * @author Arnaud Giuliani
 */
fun setupUniFlow(logger: MessageLogger = AndroidMessageLogger(), dispatcherConfiguration: DispatcherConfiguration = ApplicationDispatcherConfiguration()) {
    EventLogger.loggers += logger
    DataFlowDispatchers.dispatcherConfig = dispatcherConfiguration
}