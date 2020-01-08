package io.uniflow.result

import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState

sealed class SafeResult<out T> {

    abstract suspend fun <R> map(result: suspend (T) -> R): SafeResult<R>
    abstract suspend fun <R> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R>
    abstract fun get(): T
    abstract fun getOrNull(): T?

    abstract suspend fun onFailure(block: suspend (Exception) -> Unit): SafeResult<T>
    abstract suspend fun onSuccess(block: suspend (T) -> Unit): SafeResult<T>
    abstract suspend fun onValue(block: (T) -> Unit): SafeResult<T>

    abstract fun isSuccess(): Boolean
    abstract fun isFailure(): Boolean

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[$value]"
            is Failure -> "Failure[$exception]"
        }
    }

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
                    failure(e)
                }
            }

    suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R): R =
            when (this) {
                is Success -> onSuccess(value)
                is Failure -> throw exception
            }

    suspend fun <R : UIState> toStateOrNull(onSuccess: suspend (T) -> R?): R? =
            when (this) {
                is Success -> onSuccess(value)
                is Failure -> null
            }

    suspend fun <R : UIState> toState(onSuccess: suspend (T) -> R, onError: suspend (Exception) -> R): R =
            when (this) {
                is Success -> onSuccess(value)
                is Failure -> onError(exception)
            }

    suspend fun <R : UIEvent> toEvent(onSuccess: suspend (T) -> R): R =
        when (this) {
            is Success -> onSuccess(value)
            is Failure -> throw exception
        }

    suspend fun <R : UIEvent> toEvent(onSuccess: suspend (T) -> R, onError: suspend (Exception) -> R): R =
        when (this) {
            is Success -> onSuccess(value)
            is Failure -> onError(exception)
        }

    suspend fun <R : UIEvent> toEventOrNull(onSuccess: suspend (T) -> R): R? =
        when (this) {
            is Success -> onSuccess(value)
            is Failure -> null
        }

    data class Success<out T>(internal val value: T) : SafeResult<T>() {

        override suspend fun <R> map(result: suspend (T) -> R): SafeResult<R> =
                try {
                    success(result(value))
                } catch (e: Exception) {
                    failure(e)
                }


        override suspend fun <R> flatMap(result: suspend (T) -> SafeResult<R>): SafeResult<R> =
                try {
                    result(value)
                } catch (e: Exception) {
                    failure(e)
                }


        override fun get(): T = value

        override fun getOrNull(): T? = value

        override suspend fun onFailure(block: suspend (Exception) -> Unit): SafeResult<T> = this

        override suspend fun onSuccess(block: suspend (T) -> Unit): SafeResult<T> {
            block(value)
            return this
        }

        override suspend fun onValue(block: (T) -> Unit): SafeResult<T> {
            block(value)
            return this
        }

        override fun isSuccess(): Boolean = true
        override fun isFailure(): Boolean = false
    }

    open class Failure(val exception: Exception) : SafeResult<Nothing>() {

        override suspend fun <R> map(result: suspend (Nothing) -> R): SafeResult<R> = this

        override suspend fun <R> flatMap(result: suspend (Nothing) -> SafeResult<R>): SafeResult<R> = this

        override fun get(): Nothing = throw exception

        override fun getOrNull(): Nothing? = null

        override suspend fun onFailure(block: suspend (Exception) -> Unit): SafeResult<Nothing> {
            block(exception)
            return this
        }

        override suspend fun onSuccess(block: suspend (Nothing) -> Unit): SafeResult<Nothing> = this

        override suspend fun onValue(block: (Nothing) -> Unit): SafeResult<Nothing> = this

        override fun isSuccess(): Boolean = false
        override fun isFailure(): Boolean = true
    }

    companion object {
        fun <A> success(a: A): SafeResult<A> = Success(a)

        suspend fun <A> safeResult(code: suspend () -> A): SafeResult<A> = try {
            success(code())
        } catch (e: Exception) {
            failure(e)
        }

        suspend fun <A> safeValue(code: () -> A): SafeResult<A> = try {
            success(code())
        } catch (e: Exception) {
            failure(e)
        }

        suspend fun <A> safeResult(code: suspend () -> A, onError: suspend (Exception) -> Exception): SafeResult<A> = try {
            success(code())
        } catch (e: Exception) {
            failure(onError(e))
        }

        suspend fun <A> safeValue(code: () -> A, onError: (Exception) -> Exception): SafeResult<A> = try {
            success(code())
        } catch (e: Exception) {
            failure(onError(e))
        }

        suspend fun <A> onSafeResult(code: suspend () -> SafeResult<A>, onError: suspend (Exception) -> SafeResult<Nothing>): SafeResult<A> = try {
            code()
        } catch (e: Exception) {
            onError(e)
        }

        fun failure(a: Exception): SafeResult<Nothing> = Failure(a)
    }
}

fun <A : Any> A.success(): SafeResult<A> = SafeResult.success(this)
fun <A : Exception> A.failure(): SafeResult<Nothing> = SafeResult.failure(this)

