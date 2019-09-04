package io.uniflow.core.result

class NetworkException(message: String? = null, error: Exception? = null) : Exception(message, error)
class DatabaseException(message: String? = null, error: Exception? = null) : Exception(message, error)
