package io.uniflow.core.threading

import io.uniflow.core.dispatcher.UniFlowDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Execute job in Main thread
 */
fun CoroutineScope.launchOnMain(block: suspend CoroutineScope.() -> Unit) = launch(UniFlowDispatcher.dispatcher.main(), block = block)

/**
 * Execute job in  Default Thread
 */
fun CoroutineScope.launchOnDefault(block: suspend CoroutineScope.() -> Unit) = launch(UniFlowDispatcher.dispatcher.default(), block = block)

/**
 * Execute job in  IO Thread
 */
fun CoroutineScope.launchOnIO(block: suspend CoroutineScope.() -> Unit) = launch(UniFlowDispatcher.dispatcher.io(), block = block)

/**
 * Switch current execution context to Main thread
 */
suspend fun <T> onMain(immediate: Boolean = false, block: suspend CoroutineScope.() -> T) = withContext(getMainOrImmediateDispatcher(immediate), block = block)

private fun getMainOrImmediateDispatcher(immediate: Boolean) =
        if (immediate && UniFlowDispatcher.dispatcher.main() is MainCoroutineDispatcher) (UniFlowDispatcher.dispatcher.main() as MainCoroutineDispatcher).immediate else UniFlowDispatcher.dispatcher.main()

/**
 * Switch current execution context to Default Thread
 */
suspend fun <T> onDefault(block: suspend CoroutineScope.() -> T) = withContext(UniFlowDispatcher.dispatcher.default(), block = block)

/**
 * Switch current execution context to IO Thread
 */
suspend fun <T> onIO(block: suspend CoroutineScope.() -> T) = withContext(UniFlowDispatcher.dispatcher.io(), block = block)