package io.uniflow.core.result

sealed class Result<out T : Any> {

    data class Success<out T : Any>(val value: T) : Result<T>()
    open class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[value=$value]"
            is Error -> "Error[message='$message', error='$exception']"
        }
    }
}

fun <T : Any> success(t: T): Result<T> = Result.Success(t)
fun <T : Any> error(exception: Exception): Result<T> = Result.Error(exception.message.orEmpty(), exception)
fun <T : Any> error(message: String, exception: Exception? = null): Result<T> = Result.Error(message, exception)
