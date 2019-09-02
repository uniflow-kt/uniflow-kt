package io.uniflow.core.result

class NetworkException(message: String? = null, error: Throwable? = null) : Exception(message, error)
class DatabaseException(message: String? = null, error: Throwable? = null) : Exception(message, error)
