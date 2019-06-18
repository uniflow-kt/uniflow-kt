package io.uniflow.core.result

suspend fun <T : Any> resultForValue(code: suspend () -> T, onError: suspend (Throwable) -> Result<T>): Result<T> {
    return try {
        success(code())
    } catch (e: Throwable) {
        onError(e)
    }
}

suspend fun <T : Any> result(code: suspend () -> Result<T>, onError: suspend (Throwable) -> Result<T>): Result<T> {
    return try {
        code()
    } catch (e: Throwable) {
        onError(e)
    }
}

suspend fun <T : Any> networkResult(errorMessage: String = "Network error", code: suspend () -> Result<T>): Result<T> {
    return try {
        code()
    } catch (e: Throwable) {
        Result.Error(errorMessage, NetworkException(exception = e))
    }
}

suspend fun <T : Any> networkResultForValue(errorMessage: String = "Network error", code: suspend () -> T): Result<T> {
    return try {
        success(code())
    } catch (e: Throwable) {
        Result.Error(errorMessage, NetworkException(exception = e))
    }
}

suspend fun <T : Any> databaseResult(errorMessage: String = "Database error", code: suspend () -> Result<T>): Result<T> {
    return try {
        code()
    } catch (e: Throwable) {
        Result.Error(errorMessage, DatabaseException(exception = e))
    }
}


suspend fun <T : Any> databaseResultForValue(errorMessage: String = "Database error", code: suspend () -> T): Result<T> {
    return try {
        success(code())
    } catch (e: Throwable) {
        Result.Error(errorMessage, DatabaseException(exception = e))
    }
}

