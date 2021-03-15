package io.uniflow.android


val Any.consumerId: String
    get() = this::class.simpleName ?: error("can't get consumerId for $this")