//package io.uniflow.test.rule
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.asCoroutineDispatcher
//import kotlinx.coroutines.test.resetMain
//import kotlinx.coroutines.test.setMain
//import org.junit.rules.TestWatcher
//import org.junit.runner.Description
//import java.util.concurrent.Executors
//
//class TestThreadRule : TestWatcher() {
//
//    private val mainThreadSurrogate = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
//
//    @ExperimentalCoroutinesApi
//    override fun starting(description: Description?) {
//        super.starting(description)
//        Dispatchers.setMain(mainThreadSurrogate)
//    }
//
//    @ExperimentalCoroutinesApi
//    override fun finished(description: Description?) {
//        super.finished(description)
//        Dispatchers.resetMain()
//    }
//}