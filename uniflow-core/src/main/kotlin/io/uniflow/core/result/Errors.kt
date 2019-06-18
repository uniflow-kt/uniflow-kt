package io.uniflow.core.result

import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState

class NetworkException(message: String? = null, exception: Throwable? = null) : Exception(message, exception)
class DatabaseException(message: String? = null, exception: Throwable? = null) : Exception(message, exception)

data class DatabaseRequestFailure(val error: Throwable) : UIEvent()
suspend fun DataFlow.sendDatabaseFailure(error: Throwable): UIState? {
    sendEvent(DatabaseRequestFailure(error))
    return null
}

data class NetworkRequestFailure(val error: Throwable) : UIEvent()
suspend fun DataFlow.sendNetworkFailure(error: Throwable): UIState? {
    sendEvent(NetworkRequestFailure(error))
    return null
}