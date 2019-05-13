package io.uniflow.core.flow

import kotlinx.coroutines.CoroutineScope

data class Action(val actionFunction: ActionFunction, val errorFunction: ErrorFunction? = null)

typealias ActionFunction = suspend CoroutineScope.(UIState?) -> Any?
typealias ErrorFunction = suspend CoroutineScope.(Throwable) -> Unit