package io.uniflow.core.result

import arrow.core.Either

suspend fun <T> safeValue(expr: suspend () -> T): Either<Throwable, T> {
    return try {
        Either.right(expr())
    } catch (exception: Throwable) {
        Either.left(exception)
    }
}

suspend fun <T> safeValue(expr: suspend () -> T, onError: (Throwable) -> Throwable): Either<Throwable, T> {
    return try {
        Either.right(expr())
    } catch (exception: Throwable) {
        Either.left(onError(exception))
    }
}

suspend fun <T> safeCall(expr: suspend () -> Either<Throwable, T>): Either<Throwable, T> {
    return try {
        expr()
    } catch (exception: Throwable) {
        Either.left(exception)
    }
}

suspend fun <T> safeCall(expr: suspend () -> Either<Throwable, T>, onError: (Throwable) -> Either<Throwable, T>): Either<Throwable, T> {
    return try {
        expr()
    } catch (exception: Throwable) {
        onError(exception)
    }
}

suspend fun <T> networkCall(expr: suspend () -> T): Either<Throwable, T> {
    return safeValue(expr) { error ->
        NetworkException(error = error)
    }
}

suspend fun <T> databaseCall(expr: suspend () -> T): Either<Throwable, T> {
    return safeValue(expr) { error ->
        DatabaseException(error = error)
    }
}