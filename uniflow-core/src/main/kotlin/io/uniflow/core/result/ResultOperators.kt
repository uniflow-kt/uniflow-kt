package io.uniflow.core.result

import io.uniflow.core.flow.UIState

suspend fun <R : Any, T : Any> Result<R>.map(result: suspend (R) -> T): Result<T> {
    return when (this) {
        is Result.Success -> success(result(this.value))
        is Result.Error -> error(message, exception)
    }
}

suspend fun <R : Any, T : Any> Result<R>.flatMap(result: suspend (R) -> Result<T>): Result<T> {
    return when (this) {
        is Result.Success -> result(this.value)
        is Result.Error -> error(message, exception)
    }
}

fun <R : Any> Result<R>.value(): R {
    return when (this) {
        is Result.Success -> this.value
        is Result.Error -> exception?.let { throw exception } ?: kotlin.error(message)
    }
}

suspend fun <R : Any> Result<R>.onValue(block: suspend (R) -> Unit): Result<R> {
    return when (this) {
        is Result.Success -> {
            block(this.value)
            this
        }
        is Result.Error -> exception?.let { throw exception } ?: kotlin.error(message)
    }
}

suspend fun <R : Any> Result<R>.mapUIState(onResult: suspend (R) -> UIState?): UIState? {
    return when (this) {
        is Result.Success -> onResult(this.value)
        is Result.Error -> exception?.let { throw exception } ?: kotlin.error(message)
    }
}

@Suppress("UNCHECKED_CAST")
suspend fun <R : Any> Result<R>.mapUIState(onResult: suspend (R) -> UIState?, onError: suspend (Result<Nothing>) -> UIState?): UIState? {
    return when (this) {
        is Result.Success -> onResult(this.value)
        else -> onError(this as Result<Nothing>)
    }
}
