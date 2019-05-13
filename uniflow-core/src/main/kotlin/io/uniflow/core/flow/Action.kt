package io.uniflow.core.flow

import kotlinx.coroutines.CoroutineScope

data class Action<T>(val actionFunction: ActionFunction<T>, val errorFunction: ErrorFunction? = null)

typealias ActionFunction<U> = suspend CoroutineScope.(UIState?) -> U?
typealias ErrorFunction = suspend CoroutineScope.(Throwable) -> Unit