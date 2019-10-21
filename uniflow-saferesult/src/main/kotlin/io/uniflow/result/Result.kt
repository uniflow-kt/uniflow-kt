package io.uniflow.result

import io.uniflow.core.flow.UIState

interface Result<out T> {
    fun get(): T
    fun getOrNull(): T?
    suspend fun <R> map(result: suspend (T) -> R): SafeResult<R>
    suspend fun <R> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R>
    suspend fun onSuccess(block: suspend (T) -> Unit): SafeResult<T>
    suspend fun onFailure(block: suspend (Exception) -> Unit): SafeResult<T>
    fun isSuccess(): Boolean
    fun isFailure(): Boolean

    suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R): R
    suspend fun <R : UIState> toStateOrNull(onSuccess: suspend (T) -> R?): R?
    suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R, onError: suspend (Exception) -> R): R
}