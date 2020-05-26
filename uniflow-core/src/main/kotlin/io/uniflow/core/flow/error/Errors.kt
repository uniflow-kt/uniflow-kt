package io.uniflow.core.flow.error

import io.uniflow.core.flow.data.UIError
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.flow.data.toThrowableKt

class NetworkException(message: String? = null, error: Throwable? = null) : UIError(message, error?.toThrowableKt())
class DatabaseException(message: String? = null, error: Throwable? = null) : UIError(message, error?.toThrowableKt())

fun UIState.Failed.isNetworkError() = this.error is NetworkException
fun UIState.Failed.isDatabaseError() = this.error is DatabaseException

data class DatabaseRequestFailure(val error: Exception) : UIEvent()
data class NetworkRequestFailure(val error: Exception) : UIEvent()