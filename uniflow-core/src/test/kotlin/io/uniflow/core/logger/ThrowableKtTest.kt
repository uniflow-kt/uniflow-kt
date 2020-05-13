package io.uniflow.core.logger

import io.uniflow.core.flow.data.toThrowableKt
import org.junit.Assert.assertEquals
import org.junit.Test

class ThrowableKtTest {

    @Test
    fun `test equals`() {
        val e1 = Exception("boom")
        val e2 = Exception("boom")

        assertEquals(e1.toThrowableKt(), e2.toThrowableKt())
    }
}