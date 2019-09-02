package io.uniflow.core.result

import io.uniflow.core.flow.UIState

sealed class SafeResult<out T : Any> {

    abstract suspend fun <R : Any> map(result: suspend (T) -> R): SafeResult<R>
    abstract suspend fun mapError(result: suspend (Exception) -> Exception): SafeResult<T>
    abstract suspend fun <R : Any> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R>

    abstract fun get(): T
    abstract fun getOrNull(): T?
    operator fun not() = get()

    abstract suspend fun onValue(block: suspend (T) -> Unit): SafeResult<T>
    abstract suspend fun onError(block: suspend (Exception) -> Unit): SafeResult<T>

    suspend fun <R : UIState> mapState(onSuccess: suspend (T) -> R, onError: suspend (Exception) -> R?): SafeResult<R> {
        return when (this) {
            is Success -> map(onSuccess)
            is Error -> {
                val value = onError(exception)
                if (value != null) safeResult(value)
                else this
            }
        }
    }

    suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R): R {
        return when (this) {
            is Success -> map(onSuccess).get()
            is Error -> throw exception
        }
    }

    suspend fun <R : UIState> toStateOrNull(onSuccess: suspend (T) -> R): R? {
        return when (this) {
            is Success -> map(onSuccess).get()
            is Error -> null
        }
    }

    suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R, onError: suspend (Exception) -> R?): R {
        return mapState(onSuccess, onError).get()
    }


    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[$value]"
            is Error -> "Error[$exception]"
        }
    }

    data class Success<out T : Any>(internal val value: T) : SafeResult<T>() {

        override suspend fun <R : Any> map(result: suspend (T) -> R): SafeResult<R> {
            return try {
                safeResult(result(value))
            } catch (e: Exception) {
                errorResult(e)
            }
        }

        override suspend fun mapError(result: suspend (Exception) -> Exception): SafeResult<T> = this

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
    }

    open class Error(val exception: Exception) : SafeResult<Nothing>() {

        override suspend fun <R : Any> map(result: suspend (Nothing) -> R): SafeResult<R> = this

        override suspend fun mapError(result: suspend (Exception) -> Exception): SafeResult<Nothing> {
            return errorResult(result(exception))
        }

        override suspend fun <R : Any> flatMap(result: suspend (Nothing) -> SafeResult<R>): SafeResult<R> = this

        override fun get(): Nothing = error(exception)

        override fun getOrNull(): Nothing? = null

        override suspend fun onError(block: suspend (Exception) -> Unit): SafeResult<Nothing> {
            block(exception)
            return this
        }

        override suspend fun onValue(block: suspend (Nothing) -> Unit): SafeResult<Nothing> = this

    }
}

