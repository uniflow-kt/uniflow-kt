package io.uniflow.core.result

import io.uniflow.core.flow.UIState

sealed class SafeResult<out T> {

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[$value]"
            is Error -> "Error[$exception]"
        }
    }

    abstract suspend fun <R> map(result: suspend (T) -> R): SafeResult<R>
    abstract suspend fun <R> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R>

    operator fun not() = get()
    abstract fun get(): T
    abstract fun getOrNull(): T?

    fun getOrElse(defaultValue: @UnsafeVariance T): T =
            when (this) {
                is Success -> value
                else -> defaultValue
            }

    fun getOrElse(defaultValue: () -> @UnsafeVariance T): T =
            when (this) {
                is Success -> value
                else -> defaultValue()
            }

    fun orElse(defaultValue: () -> SafeResult<@UnsafeVariance T>): SafeResult<T> =
            when (this) {
                is Success -> this
                else -> try {
                    defaultValue()
                } catch (e: Exception) {
                    errorResult(e)
                }
            }

    abstract suspend fun onValue(block: suspend (T) -> Unit): SafeResult<T>
    abstract suspend fun onEmpty(block: suspend () -> Unit): SafeResult<T>
    abstract suspend fun onError(block: suspend (Exception) -> Unit): SafeResult<T>

    abstract fun isSuccess(): Boolean
    abstract fun isError(): Boolean

    abstract suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R): R

    suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R, onError: suspend (Exception) -> R): R =
            when (this) {
                is Success -> toState(onSuccess)
                is Error -> onError(exception)
            }

    abstract suspend fun <R : UIState> toStateOrNull(onSuccess: suspend (T) -> R?): R?


    data class Success<out T>(internal val value: T) : SafeResult<T>() {

        override suspend fun <R> map(result: suspend (T) -> R): SafeResult<R> =
                try {
                    safeResult(result(value))
                } catch (e: Exception) {
                    errorResult(e)
                }


        override suspend fun <R> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R> =
                try {
                    result(value)
                } catch (e: Exception) {
                    errorResult(e)
                }


        override fun get(): T = value

        override fun getOrNull(): T? = value

        override suspend fun onError(block: suspend (Exception) -> Unit): SafeResult<T> = this

        override suspend fun onValue(block: suspend (T) -> Unit): SafeResult<T> {
            block(value)
            return this
        }

        override suspend fun onEmpty(block: suspend () -> Unit): SafeResult<T> = this

        override fun isSuccess(): Boolean = true
        override fun isError(): Boolean = false

        override suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R): R = onSuccess(value)
        override suspend fun <R : UIState> toStateOrNull(onSuccess: suspend (T) -> R?): R? = onSuccess(value)
    }

    open class Error(val exception: Exception) : SafeResult<Nothing>() {

        override suspend fun <R> map(result: suspend (Nothing) -> R): SafeResult<R> = this

        override suspend fun <R> flatMap(result: suspend (Nothing) -> SafeResult<R>): SafeResult<R> = this

        override fun get(): Nothing = throw exception

        override fun getOrNull(): Nothing? = null

        override suspend fun onError(block: suspend (Exception) -> Unit): SafeResult<Nothing> {
            block(exception)
            return this
        }

        override suspend fun onValue(block: suspend (Nothing) -> Unit): SafeResult<Nothing> = this
        override suspend fun onEmpty(block: suspend () -> Unit): SafeResult<Nothing> = this

        override suspend fun <R : UIState> toState(onSuccess: suspend (Nothing) -> R): R = throw exception
        override suspend fun <R : UIState> toStateOrNull(onSuccess: suspend (Nothing) -> R?): R? = null

        override fun isSuccess(): Boolean = false
        override fun isError(): Boolean = true
    }
}



