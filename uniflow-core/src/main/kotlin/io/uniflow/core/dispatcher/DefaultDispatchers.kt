package io.uniflow.core.dispatcher

class DefaultDispatchers : Dispatchers {
    override fun main() = kotlinx.coroutines.Dispatchers.Main

    override fun default() = kotlinx.coroutines.Dispatchers.Default

    override fun io() = kotlinx.coroutines.Dispatchers.IO
}