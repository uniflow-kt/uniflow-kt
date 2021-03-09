package io.uniflow.core.coroutines

import io.uniflow.core.flow.ActionErrorFunction
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.launchOnIO
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@OptIn(InternalCoroutinesApi::class)

fun <T> DataFlow.emitActions(
    flow: () -> Flow<T>,
    newAction: suspend UIState.(T) -> Unit
): Job {
    return coroutineScope.launchOnIO {
        try {
            flow().collect { v ->
                action { s -> newAction(s, v) }
            }
        } catch (e: Exception) {
            action { throw e }
        }
    }
}

fun <T> DataFlow.emitActions(
    flow: () -> Flow<T>,
    newAction: suspend UIState.(T) -> Unit,
    onError: (ActionErrorFunction)
): Job {
    return coroutineScope.launchOnIO {
        try {
            flow().collect { v ->
                action { s -> newAction(s, v) }
            }
        } catch (e: Exception) {
            action { onError(e, it) }
        }
    }
}