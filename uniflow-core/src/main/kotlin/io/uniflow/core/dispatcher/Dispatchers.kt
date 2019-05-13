package io.uniflow.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

interface Dispatchers {
    fun main() : CoroutineDispatcher
    fun default() : CoroutineDispatcher
    fun io() : CoroutineDispatcher
}