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

import io.uniflow.core.logger.UniFlowLogger
import java.util.*

/**
 * Event wrapper
 *
 * Help send content to EventConsumer
 *
 * @author Arnaud Giuliani
 */
data class Event<out T : UIEvent>(val id: String = UUID.randomUUID().toString(), val content: T) {

    private val consumerIds = arrayListOf<String>()

    private fun hasNotBeenSentTo(consumer: EventConsumer): Boolean = !consumerIds.contains(consumer.id)

    private fun registerConsumer(consumer: EventConsumer) {
        consumerIds.add(consumer.id)
    }

    fun getContentForConsumer(consumer: EventConsumer): UIEvent? {
        return if (hasNotBeenSentTo(consumer)) {
            registerConsumer(consumer)
            UniFlowLogger.debug("$consumer received $content")
            content
        } else {
            UniFlowLogger.debug("$consumer skipped - already received $content")
            null
        }
    }
}
