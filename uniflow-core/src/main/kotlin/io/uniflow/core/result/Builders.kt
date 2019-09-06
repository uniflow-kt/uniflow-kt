package io.uniflow.core.result

fun <T> safeResult(value: T): SafeResult<T> = SafeResult.Success(value)

fun errorResult(message: String): SafeResult<Nothing> = SafeResult.Error(IllegalStateException(message))

fun errorResult(exception: Exception): SafeResult<Nothing> = SafeResult.Error(exception)

fun <T> T.asSafeResult(): SafeResult<T> = safeResult(this)

fun emptyResult() = SafeResult.Empty

@Deprecated("use asErrorResult instead", level = DeprecationLevel.ERROR)
fun <T : Exception> T.asSafeResult(): SafeResult<T> = error("Don't use .asSafeResult() on Exception, use asErrorResult() instead")

fun <T : Exception> T.asErrorResult(): SafeResult<T> = errorResult(this)

suspend fun <T> safeCall(expr: suspend () -> T): SafeResult<T> {
    return try {
        expr().asSafeResult()
    } catch (error: Exception) {
        SafeResult.Error(error)
    }
}

suspend fun <T> safeCall(expr: suspend () -> T, onError: (Exception) -> Exception): SafeResult<T> {
    return try {
        expr().asSafeResult()
    } catch (exception: Exception) {
        SafeResult.Error(onError(exception))
    }
}

suspend fun <T> safeResultCall(expr: suspend () -> SafeResult<T>): SafeResult<T> {
    return try {
        expr()
    } catch (exception: Exception) {
        SafeResult.Error(exception)
    }
}

suspend fun <T> safeResultCall(expr: suspend () -> SafeResult<T>, onError: (Exception) -> Exception): SafeResult<T> {
    return try {
        expr()
    } catch (exception: Exception) {
        SafeResult.Error(onError(exception))
    }
}

suspend fun <T> networkCall(expr: suspend () -> T): SafeResult<T> {
    return safeCall(expr) { error ->
        NetworkException(error = error)
    }
}

suspend fun <T> databaseCall(expr: suspend () -> T): SafeResult<T> {
    return safeCall(expr) { error ->
        DatabaseException(error = error)
    }
}

