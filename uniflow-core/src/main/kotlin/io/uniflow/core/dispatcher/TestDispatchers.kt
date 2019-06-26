package io.uniflow.core.dispatcher

class TestDispatchers : UniFlowDispatcherConfiguration {
    override fun main() = kotlinx.coroutines.Dispatchers.Unconfined
    override fun default() = kotlinx.coroutines.Dispatchers.Unconfined
    override fun io() = kotlinx.coroutines.Dispatchers.Unconfined
}