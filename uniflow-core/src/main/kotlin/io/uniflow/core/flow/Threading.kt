package io.uniflow.core.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext


/**
 * Switch current execution context to Main thread
 */
suspend fun <T> onMain(block: suspend CoroutineScope.() -> T) = withContext(UniFlowDispatcher.dispatcher.main(), block = block)

/**
 * Switch current execution context to Default Thread
 */
suspend fun <T> onDefault(block: suspend CoroutineScope.() -> T) = withContext(UniFlowDispatcher.dispatcher.default(), block = block)

/**
 * Switch current execution context to IO Thread
 */
suspend fun <T> onIO(block: suspend CoroutineScope.() -> T) = withContext(UniFlowDispatcher.dispatcher.io(), block = block)