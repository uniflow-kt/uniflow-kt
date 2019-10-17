package io.uniflow.core.result

import arrow.core.Either
import arrow.core.orNull
import io.uniflow.core.flow.UIState

fun <A> Either<Throwable, A>.get(): A =
        when (this) {
            is Either.Left -> throw a
            is Either.Right -> b
        }

suspend fun <A> Either<Throwable, A>.onSuccess(f: suspend (A) -> Unit): Either<Throwable, A> =
        when (this) {
            is Either.Right -> {
                f(b)
                this
            }
            else -> this
        }

suspend fun <A> Either<Throwable, A>.onFailure(f: suspend (Throwable) -> Unit): Either<Throwable, A> =
        when (this) {
            is Either.Left -> {
                f(a)
                this
            }
            else -> this
        }

suspend fun <T, R : UIState> Either<Throwable, T>.toState(onSuccess: suspend (T) -> R): R = onSuccess(get())
suspend fun <T, R : UIState> Either<Throwable, T>.toStateOrNull(onSuccess: suspend (T) -> R?): R? = orNull()?.let { onSuccess(it) }
suspend fun <T, R : UIState> Either<Throwable, T>.toState(onSuccess: suspend (T) -> R, onError: suspend (Throwable) -> R): R =
        when (this) {
            is Either.Right -> toState(onSuccess)
            is Either.Left -> onError(a)
        }