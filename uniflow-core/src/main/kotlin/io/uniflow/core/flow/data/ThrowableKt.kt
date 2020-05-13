package io.uniflow.core.flow.data

fun Throwable.isNotEquals(error: Throwable): Boolean = !isEquals(error)

fun Throwable.isEquals(error: Throwable): Boolean {
    return message == error.message &&
            cause == error.cause
}

fun Throwable.toThrowableKt(): ThrowableKt {
    return ThrowableKt(message, cause?.toThrowableKt())
}

fun throwableKt(t: Throwable) = t.toThrowableKt()

open class ThrowableKt(val message: String? = null, val cause: ThrowableKt? = null) {

    constructor(message: String? = null) : this(message, null as? ThrowableKt)
    constructor(message: String? = null, cause: Throwable? = null) : this(message, cause?.toThrowableKt())
    constructor(message: String? = null, cause: Exception? = null) : this(message, cause?.toThrowableKt())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ThrowableKt

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