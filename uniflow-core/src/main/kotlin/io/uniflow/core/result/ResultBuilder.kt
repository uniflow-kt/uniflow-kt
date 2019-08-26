package io.uniflow.core.result

suspend fun <T : Any> result(code: suspend () -> FlowResult<T>, onError: suspend (Throwable) -> FlowResult<T>): FlowResult<T> {
    return try {
        code()
    } catch (e: Throwable) {
        onError(e)
    }
}

suspend fun <T : Any> networkResult(errorMessage: String = "Network flowError", code: suspend () -> FlowResult<T>): FlowResult<T> {
    return try {
        code()
    } catch (e: Throwable) {
        FlowResult.Error(errorMessage, NetworkException("$errorMessage - $e",e))
    }
}

suspend fun <T : Any> databaseResult(errorMessage: String = "Database flowError", code: suspend () -> FlowResult<T>): FlowResult<T> {
    return try {
        code()
    } catch (e: Throwable) {
        FlowResult.Error(errorMessage, DatabaseException("$errorMessage - $e",e))
    }
}

