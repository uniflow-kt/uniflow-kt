package io.uniflow.core.result

import io.uniflow.core.flow.UIState

sealed class SafeResult<out T : Any> {

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[$value]"
            is Error -> "Error[$exception]"
            is Empty -> "Empty"
        }
    }

    abstract suspend fun <R : Any> map(result: suspend (T) -> R?): SafeResult<R>
    abstract suspend fun <R : Any> mapError(result: suspend (Exception) -> R?): SafeResult<R>
    abstract suspend fun <R : Any> mapEmpty(result: suspend () -> R?): SafeResult<R>
    abstract suspend fun <R : Any> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R>

    abstract fun get(): T
    abstract fun getOrNull(): T?
    operator fun not() = get()

    abstract suspend fun onValue(block: suspend (T) -> Unit): SafeResult<T>
    abstract suspend fun onEmpty(block: suspend () -> Unit): SafeResult<T>
    abstract suspend fun onError(block: suspend (Exception) -> Unit): SafeResult<T>

    abstract fun isSuccess(): Boolean
    abstract fun isError(): Boolean
    abstract fun isEmpty(): Boolean

    abstract suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R): R
    abstract suspend fun <R : UIState> toStateOrNull(onSuccess: suspend (T) -> R?): R?

    suspend fun <R : Any> orElse(result: suspend () -> R): SafeResult<R> {
        return if (getOrNull() == null) {
            safeResult(result())
        } else this as SafeResult<R>
    }

    data class Success<out T : Any>(internal val value: T) : SafeResult<T>() {

        override suspend fun <R : Any> map(result: suspend (T) -> R?): SafeResult<R> {
            return try {
                result(value)?.let { safeResult(it) } ?: emptyResult()
            } catch (e: Exception) {
                errorResult(e)
            }
        }

        override suspend fun <R : Any> mapError(result: suspend (Exception) -> R?): SafeResult<R> = this as SafeResult<R>

        override suspend fun <R : Any> mapEmpty(result: suspend () -> R?): SafeResult<R> = this as SafeResult<R>

        override suspend fun <R : Any> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R> {
            return try {
                result(value)
            } catch (e: Exception) {
                errorResult(e)
            }
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
        override fun isEmpty(): Boolean = false
        override fun isError(): Boolean = false

        override suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R): R = onSuccess(value)
        override suspend fun <R : UIState> toStateOrNull(onSuccess: suspend (T) -> R?): R? = onSuccess(value)
    }

    object Empty : SafeResult<Nothing>() {

        override suspend fun <R : Any> map(result: suspend (Nothing) -> R?): SafeResult<R> = this

        override suspend fun <R : Any> flatMap(result: suspend (Nothing) -> SafeResult<R>): SafeResult<R> = this

        override suspend fun <R : Any> mapError(result: suspend (Exception) -> R?): SafeResult<R> = this

        override suspend fun <R : Any> mapEmpty(result: suspend () -> R?): SafeResult<R> {
            return try {
                result()?.let { safeResult(it) } ?: emptyResult()
            } catch (e: Exception) {
                errorResult(e)
            }
        }

        override fun get(): Nothing = error("Empty result")

        override fun getOrNull(): Nothing? = null

        override suspend fun onError(block: suspend (Exception) -> Unit): SafeResult<Nothing> = this

        override suspend fun onValue(block: suspend (Nothing) -> Unit): SafeResult<Nothing> = this

        override suspend fun onEmpty(block: suspend () -> Unit): SafeResult<Nothing> {
            block()
            return this
        }

        override suspend fun <R : UIState> toState(onSuccess: suspend (Nothing) -> R): R = error("Empty result")
        override suspend fun <R : UIState> toStateOrNull(onSuccess: suspend (Nothing) -> R?): R? = null

        override fun isSuccess(): Boolean = false
        override fun isEmpty(): Boolean = true
        override fun isError(): Boolean = false
    }

    open class Error(val exception: Exception) : SafeResult<Nothing>() {

        override suspend fun <R : Any> map(result: suspend (Nothing) -> R?): SafeResult<R> = this

        override suspend fun <R : Any> mapError(result: suspend (Exception) -> R?): SafeResult<R> {
            val newValue = result(exception)
            return if (newValue is Exception) errorResult(exception) else newValue?.let { safeResult(it) }
                    ?: emptyResult()
        }

        override suspend fun <R : Any> mapEmpty(result: suspend () -> R?): SafeResult<R> = this

        override suspend fun <R : Any> flatMap(result: suspend (Nothing) -> SafeResult<R>): SafeResult<R> = this

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
        override fun isEmpty(): Boolean = false
        override fun isError(): Boolean = true
    }
}



