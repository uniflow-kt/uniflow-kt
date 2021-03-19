package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIState
import kotlin.reflect.KClass

typealias ActionFunction = suspend (UIState) -> (Unit)
typealias ActionFunction_T<T> = suspend (T) -> (Unit)
typealias ActionErrorFunction = suspend (Exception, UIState) -> (Unit)
typealias ActionErrorFunction_T<T> = suspend (Exception, T) -> (Unit)

/**
 * Wrap a function that will set a new state or event, regarding current state
 * We can target a specific State KClass to ensure we want to execute on it
 *
 * @author Arnaud Giuliani
 */
class Action(
        val onSuccess: ActionFunction,
        val onError: ActionErrorFunction,
        val targetState: KClass<out UIState>? = null
)