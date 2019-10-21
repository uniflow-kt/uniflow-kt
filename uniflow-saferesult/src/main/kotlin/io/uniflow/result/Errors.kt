package io.uniflow.result

import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState

class NetworkException(message: String? = null, error: Throwable? = null) : Exception(message, error)
class DatabaseException(message: String? = null, error: Throwable? = null) : Exception(message, error)

fun UIState.Failed.isNetworkError() = this.error is NetworkException
fun UIState.Failed.isDatabaseError() = this.error is DatabaseException

data class DatabaseRequestFailure(val error: Exception) : UIEvent()
data class NetworkRequestFailure(val error: Exception) : UIEvent()