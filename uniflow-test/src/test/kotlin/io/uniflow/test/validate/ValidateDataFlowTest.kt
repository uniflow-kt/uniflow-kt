package io.uniflow.test.validate

import io.uniflow.core.flow.UIState
import io.uniflow.test.data.ListDataFlow
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

class GoodDF : ListDataFlow() {

    fun goodAction() = setState {
        UIState.Success
    }
}

class BadDF : ListDataFlow() {

    fun goodAction() = setState {
        UIState.Success
    }

    fun badAction() {
        setState {
            UIState.Success
        }
    }
}