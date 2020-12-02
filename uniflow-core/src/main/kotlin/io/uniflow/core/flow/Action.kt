package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIState
import kotlin.reflect.KClass

// pass default publisher's state
typealias ActionFunction = suspend (UIState) -> (Unit)
typealias ActionFunction_T<T> = suspend (T) -> (Unit)
typealias ActionErrorFunction = suspend (Exception, UIState) -> (Unit)
typealias ActionErrorFunction_T<T> = suspend (Exception, T) -> (Unit)

class Action(
        val onSuccess: ActionFunction,
        val onError: ActionErrorFunction,
        val targetState : KClass<out UIState>? = null
)