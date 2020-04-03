package io.uniflow.result

import arrow.core.Success
import arrow.core.Try
import io.uniflow.core.flow.data.UIState

fun <A> Try<A>.getOrNull(): A? =
        when (this) {
            is Try.Failure -> null
            is Try.Success -> value
        }

fun <A> Try<A>.get(): A =
        when (this) {
            is Try.Failure -> throw exception
            is Try.Success -> value
        }

fun <A, R> Try<A>.get(result: (A) -> R): R =
        when (this) {
            is Try.Failure -> throw exception
            is Try.Success -> result(value)
        }

suspend fun <A> Try<A>.onSuccess(f: suspend (A) -> Unit): Try<A> =
        when (this) {
            is Try.Success -> {
                f(value)
                this
            }
            else -> this
        }

suspend fun <A> Try<A>.onFailure(f: suspend (Throwable) -> Unit): Try<A> =
        when (this) {
            is Try.Failure -> {
                f(exception)
                this
            }
            else -> this
        }

suspend fun <T, R : UIState> Try<T>.toState(onSuccess: suspend (T) -> R): R = onSuccess(get())
suspend fun <T, R : UIState> Try<T>.toStateOrNull(onSuccess: suspend (T) -> R?): R? = getOrNull()?.let { onSuccess(it) }
suspend fun <T, R : UIState> Try<T>.toState(onSuccess: suspend (T) -> R, onError: suspend (Throwable) -> R): R =
        when (this) {
            is Success -> toState(onSuccess)
            is Try.Failure -> onError(exception)
        }

