package io.uniflow.arrow

import arrow.core.Try

suspend fun <T> safeValue(expr: suspend () -> T): Try<T> {
    return try {
        Try.Success(expr())
    } catch (exception: Throwable) {
        Try.Failure(exception)
    }
}

suspend fun <T> safeValue(expr: suspend () -> T, onError: (Throwable) -> Throwable): Try<T> {
    return try {
        Try.Success(expr())
    } catch (exception: Throwable) {
        Try.Failure(onError(exception))
    }
}

suspend fun <T> safeCall(expr: suspend () -> Try<T>): Try<T> {
    return try {
        expr()
    } catch (exception: Throwable) {
        Try.Failure(exception)
    }
}

suspend fun <T> safeCall(expr: suspend () -> Try<T>, onError: (Throwable) -> Try<T>): Try<T> {
    return try {
        expr()
    } catch (exception: Throwable) {
        onError(exception)
    }
}

suspend fun <T> networkCall(expr: suspend () -> T): Try<T> {
    return safeValue(expr) { error ->
        NetworkException(error = error)
    }
}

suspend fun <T> databaseCall(expr: suspend () -> T): Try<T> {
    return safeValue(expr) { error ->
        DatabaseException(error = error)
    }
}