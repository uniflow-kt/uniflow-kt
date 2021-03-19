package io.uniflow.core.flow.error

import io.uniflow.core.flow.data.UIError
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.flow.data.toUIError

class NetworkException(message: String? = null, error: Throwable? = null) : UIError(message, error?.toUIError())
class DatabaseException(message: String? = null, error: Throwable? = null) : UIError(message, error?.toUIError())

fun UIState.Failed.isNetworkError() = this.error is NetworkException
fun UIState.Failed.isDatabaseError() = this.error is DatabaseException

data class DatabaseRequestFailure(val error: Exception) : UIEvent()
data class NetworkRequestFailure(val error: Exception) : UIEvent()