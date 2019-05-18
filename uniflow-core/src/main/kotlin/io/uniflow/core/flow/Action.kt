package io.uniflow.core.flow

import kotlinx.coroutines.CoroutineScope

data class Action<T>(val actionFunction: ActionFunction<T>, val errorFunction: ErrorFunction? = null)

typealias ActionFunction<T> = suspend CoroutineScope.(UIState?) -> T
typealias ErrorFunction = suspend CoroutineScope.(Throwable) -> Unit