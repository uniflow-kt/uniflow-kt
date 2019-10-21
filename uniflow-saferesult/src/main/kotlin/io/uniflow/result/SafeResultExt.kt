package io.uniflow.result

import io.uniflow.core.flow.UIState

suspend fun <T, R : UIState> SafeResult<T>.toState(onSuccess: suspend (T) -> R): R =
        when (this) {
            is SafeResult.Success -> onSuccess(value)
            is SafeResult.Failure -> throw exception
        }

suspend fun <T, R : UIState> SafeResult<T>.toStateOrNull(onSuccess: suspend (T) -> R?): R? =
        when (this) {
            is SafeResult.Success -> onSuccess(value)
            is SafeResult.Failure -> null
        }

suspend fun <T, R : UIState> SafeResult<T>.toState(onSuccess: suspend (T) -> R, onError: suspend (Exception) -> R): R =
        when (this) {
            is SafeResult.Success -> onSuccess(value)
            is SafeResult.Failure -> onError(exception)
        }


