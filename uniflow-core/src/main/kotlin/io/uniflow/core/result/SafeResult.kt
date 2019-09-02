package io.uniflow.core.result

import io.uniflow.core.flow.UIState

sealed class SafeResult<out T : Any> {

    abstract suspend fun <R : Any> map(result: suspend (T) -> R): SafeResult<R>
    abstract suspend fun <R : Any> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R>
    abstract fun get(): T
    abstract fun getOrNull(): T?
    abstract suspend fun onValue(block: suspend (T) -> Unit): SafeResult<T>
    abstract suspend fun onError(block: suspend (Throwable) -> Unit): SafeResult<T>
    abstract suspend fun mapError(result: suspend (Throwable) -> Throwable): SafeResult<T>
    abstract suspend fun mapState(result: suspend (T) -> UIState): SafeResult<UIState>

    suspend fun toState(result: suspend (T) -> UIState): UIState = mapState(result).get()

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

        override suspend fun <R : Any> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R> {
            return try {
                result(value)
            } catch (e: Exception) {
                errorResult(e)
            }
        }

        override fun get(): T = value

        override fun getOrNull(): T? = value

        override suspend fun onError(block: suspend (Throwable) -> Unit): SafeResult<T> = this

        override suspend fun onValue(block: suspend (T) -> Unit): SafeResult<T> {
            block(value)
            return this
        }

        override suspend fun mapError(result: suspend (Throwable) -> Throwable): SafeResult<T> = this

        override suspend fun mapState(result: suspend (T) -> UIState): SafeResult<UIState> = map(result)
    }

    open class Error(val exception: Throwable) : SafeResult<Nothing>() {
        override suspend fun <R : Any> map(result: suspend (Nothing) -> R): SafeResult<R> = this

        override suspend fun <R : Any> flatMap(result: suspend (Nothing) -> SafeResult<R>): SafeResult<R> = this

        override fun get(): Nothing = error(exception)

        override fun getOrNull(): Nothing? = null

        override suspend fun onError(block: suspend (Throwable) -> Unit): SafeResult<Nothing> {
            block(exception)
            return this
        }

        override suspend fun onValue(block: suspend (Nothing) -> Unit): SafeResult<Nothing> = this

        override suspend fun mapError(result: suspend (Throwable) -> Throwable): SafeResult<Nothing> = Error(result(exception))

        override suspend fun mapState(result: suspend (Nothing) -> UIState): SafeResult<UIState> = safeResult(UIState.Failed(error = exception))

    }
}

