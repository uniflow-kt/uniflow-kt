package io.uniflow.core.result

suspend fun <T : Any> result(code: suspend () -> Result<T>, onError: suspend (Throwable) -> Result<T>): Result<T> {
    return try {
        code()
    } catch (e: Throwable) {
        onError(e)
    }
}

