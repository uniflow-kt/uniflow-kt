package io.uniflow.core.flow

import kotlinx.coroutines.CoroutineScope

data class Action<R : UIState?, T : Any?>(val actionFunction: ActionFunction<R, T>, val errorFunction: ErrorFunction? = null)

typealias ActionFunction<R, T> = suspend CoroutineScope.(R) -> T

typealias StateFlowFunction<T> = suspend StateFlowPublisher.(T) -> Unit

typealias ErrorFunction = suspend CoroutineScope.(Throwable) -> UIState?