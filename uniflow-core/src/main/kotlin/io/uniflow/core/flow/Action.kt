package io.uniflow.core.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

data class Action<T>(val actionFunction: ActionFunction<T>) {
    internal var errorFunction: ErrorFunction? = null
    internal var executionContext: CoroutineContext = UniFlowDispatcher.dispatcher.default()

    infix fun onError(function: ErrorFunction): Action<T> {
        UniFlowLogger.log("$this add onError")
        errorFunction = function
        return this
    }

    infix fun launchOn(context: CoroutineContext): Action<T> {
        UniFlowLogger.log("$this add launchOn")
        executionContext = context
        return this
    }
}
typealias ActionFunction<U> = suspend CoroutineScope.(UIState?) -> U?
typealias ErrorFunction = suspend CoroutineScope.(Throwable) -> Unit