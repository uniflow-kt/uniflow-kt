package io.uniflow.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

interface UniFlowDispatcherConfiguration {
    fun main() : CoroutineDispatcher
    fun default() : CoroutineDispatcher
    fun io() : CoroutineDispatcher
}