package io.uniflow.core.logger

import io.uniflow.core.flow.data.UIState
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

    @Test
    fun `test equals Fail state`() {
        val e1 = Exception("boom")
        val e2 = Exception("boom")

        assertEquals(UIState.Failed("Got error $e1", e1), UIState.Failed("Got error $e2", e2))
    }
}