package io.uniflow.core.flow.error

import io.uniflow.core.flow.data.UIState
import kotlin.reflect.KClass

class BadOrWrongStateException(state: UIState, requiredState: KClass<out UIState>) : Exception("WrongOrBadStateException: was in state:$state but required state:$requiredState")