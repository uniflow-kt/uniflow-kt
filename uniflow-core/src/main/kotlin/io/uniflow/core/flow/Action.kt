package io.uniflow.core.flow

data class Action<R : UIState?, T : Any?>(val actionFunction: ActionFunction<R, T>, val errorFunction: ErrorFunction? = null)

typealias ActionFunction<R, T> = suspend (R) -> T

typealias StateFlowFunction<T> = suspend StateFlowPublisher.(T) -> Unit

typealias ErrorFunction = suspend (Exception) -> UIState?