package io.uniflow.test.impl

import io.uniflow.core.flow.data.UIState
import io.uniflow.test.validate.validate
import org.junit.Assert.fail
import org.junit.Test

class ValidateDataFlowTest {

    @Test
    fun `test bad Dataflow`() {
        try {
            validate<BadDF>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `test good Dataflow`() {
        validate<GoodDF>()
    }

}

class GoodDF : AbstractSampleFlow(UIState.Empty) {

    fun goodAction() = action {
        setState { UIState.Success }
    }
}

class BadDF : AbstractSampleFlow(UIState.Empty) {

    fun goodAction() = action {
        setState { UIState.Success }
    }

    fun badAction() {
        action {
            setState { UIState.Success }
        }
    }
}
