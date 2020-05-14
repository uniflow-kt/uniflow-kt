package io.uniflow.core.flow.data

fun Throwable.toThrowableKt(): ThrowableKt {
    return ThrowableKt(message, cause)
}

fun throwableKt(t: Throwable) = t.toThrowableKt()

open class ThrowableKt(val message: String? = null, val cause: ThrowableKt? = null) {

    var error: Throwable? = null
        private set

    constructor(message: String? = null) : this(message, null as? ThrowableKt)
    constructor(message: String? = null, cause: Throwable? = null) : this(message, cause?.toThrowableKt()) {
        error = cause
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ThrowableKt) return false

        if (message != other.message) return false
        if (cause != other.cause) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message?.hashCode() ?: 0
        result = 42 * result + (cause?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "ThrowableKt(message='$message',cause=$cause)"
}
