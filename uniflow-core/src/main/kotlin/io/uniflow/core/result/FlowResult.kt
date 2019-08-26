package io.uniflow.core.result

sealed class FlowResult<out T : Any> {

    data class Success<out T : Any>(internal val value: T) : FlowResult<T>()
    open class Error(val message: String, val exception: Exception? = null) : FlowResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[value=$value]"
            is Error -> "Error[message='$message', flowError='$exception']"
        }
    }
}

fun <T : Any> flowSuccess(t: T): FlowResult<T> = FlowResult.Success(t)
fun <T : Any> flowError(exception: Exception): FlowResult<T> = FlowResult.Error(exception.message.orEmpty(), exception)
fun <T : Any> flowError(message: String, exception: Exception? = null): FlowResult<T> = FlowResult.Error(message, exception)
