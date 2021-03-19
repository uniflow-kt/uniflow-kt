//package io.uniflow.test
//
//import io.uniflow.core.flow.data.UIState
//import io.uniflow.core.logger.UniFlowLogger
//import io.uniflow.test.impl.AbstractSampleFlow
//import io.uniflow.test.validate.validate
//import org.junit.Assert.fail
//import org.junit.Before
//import org.junit.Test
//
//class ValidateDataFlowTest {
//
//    @Before
//    fun before() {
//        UniFlowLogger.default()
//    }
//
//    @Test
//    fun `test bad Dataflow`() {
//        try {
//            validate<BadDF>()
//            fail()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    @Test
//    fun `test good Dataflow`() {
//        validate<GoodDF>()
//    }
//
//}
//
//class GoodDF : AbstractSampleFlow(UIState.Empty) {
//
//    fun goodAction() = action {
//        setState { UIState.Success }
//    }
//}
//
//class BadDF : AbstractSampleFlow(UIState.Empty) {
//
//    fun goodAction() = action {
//        setState { UIState.Success }
//    }
//
//    fun badAction() {
//        action {
//            setState { UIState.Success }
//        }
//    }
//}
