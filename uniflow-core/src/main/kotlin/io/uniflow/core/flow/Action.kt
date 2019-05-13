package io.uniflow.core.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

data class Action<T>(val actionFunction: ActionFunction<T>) {
    internal var errorFunction: ErrorFunction? = null
    internal var executionContext: CoroutineContext = Dispatchers.Default

    infix fun onError(function: ErrorFunction): Action<T> {
        errorFunction = function
        return this
    }

    infix fun launchOn(context: CoroutineContext): Action<T> {
        executionContext = context
        return this
    }
}
typealias ActionFunction<U> = suspend CoroutineScope.(UIState?) -> U?
typealias ErrorFunction = suspend CoroutineScope.(Throwable) -> Unit