package io.uniflow.core.flow.error

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

class NetworkException(message: String? = null, error: Throwable? = null) : Exception(message, error)
class DatabaseException(message: String? = null, error: Throwable? = null) : Exception(message, error)

fun UIState.Failed.isNetworkError() = this.error is NetworkException
fun UIState.Failed.isDatabaseError() = this.error is DatabaseException

data class DatabaseRequestFailure(val error: Exception) : UIEvent()
data class NetworkRequestFailure(val error: Exception) : UIEvent()