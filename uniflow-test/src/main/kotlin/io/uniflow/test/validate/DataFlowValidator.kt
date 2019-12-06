package io.uniflow.test.validate

import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.StateAction
import io.uniflow.core.logger.UniFlowLogger
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.functions

fun <T : DataFlow> KClass<T>.validate() {
    val publicFunctions = functions.filter { it.visibility == KVisibility.PUBLIC }
    val hasUnvalidActions = publicFunctions.map { function ->
        function.validate()
    }.any { result ->
        !result
    }
    if (hasUnvalidActions) {
        UniFlowLogger.logError("DataFlow '$this' is not valid")
        throw UnvalidActionFunctionException("DataFlow '$this' is not valid")
    } else {
        UniFlowLogger.log("DataFlow '$this' is valid")
    }
}

inline fun <reified T : DataFlow> validate() {
    T::class.validate()
}

fun DataFlow.validate() {
    this::class.validate()
}

@UseExperimental(ExperimentalStdlibApi::class)
fun KFunction<*>.validate(): Boolean {
    return if (name in exclusion) true
    else {
        val clazz = returnType.classifier as KClass<*>
        val isActionClass = clazz == StateAction::class
        if (!isActionClass) {
            UniFlowLogger.logError("Function '$name' is not a valid State Action Function. It should return 'ActionState'. Please use setState or FromState operator.")
        }
        isActionClass
    }
}

val exclusion = listOf("applyState", "cancel", "equals", "hashCode", "notifyUpdate", "onAction", "onActionError", "onError", "proceedAction", "sendEvent", "setState", "toString")
