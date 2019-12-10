package io.uniflow.core.flow

import kotlinx.coroutines.flow.FlowCollector

typealias StateFunctionFlow = suspend FlowCollector<UIState>.() -> Unit