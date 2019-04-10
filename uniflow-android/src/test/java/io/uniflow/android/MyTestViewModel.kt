//package io.uniflow.android
//
//import io.uniflow.core.dispatcher.DataFlowDispatchers
//import io.uniflow.core.mvvm.AsyncComponent
//import io.uniflow.core.mvvm.ErrorFunction
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//
//class MyTestViewModel(val id: String, val task: (String) -> String) {
//    val coroutineContext = AsyncComponent()
//    internal fun launch(block: suspend CoroutineScope.() -> Unit, errorHandling: ErrorFunction? = null) = coroutineContext.launch(function = block, errorHandling = errorHandling)
//    internal fun launchOnIO(block: suspend CoroutineScope.() -> Unit, errorHandling: ErrorFunction? = null): Job? = coroutineContext.launch(coroutineContext.parentJob + DataFlowDispatchers.dispatcherConfig.io(), function = block, errorHandling = errorHandling)
//
//    var uiDetail: String? = null
//    var uiError: Throwable? = null
//
//    fun success(): Job? {
//        return launch({
//            runTask()
//        })
//    }
//
//    fun successIO(): Job? {
//        return launchOnIO({
//            runTask()
//        })
//    }
//
//    fun longTask() {
//        launchOnIO({
//            delay(500)
//            runTask()
//        })
//    }
//
//    fun error(): Job? {
//        return launchOnIO({ runTask() }, { e -> uiError = e })
//    }
//
//    private suspend fun runTask() {
//        val weatherDetail = task(id)
//        println("weatherDetail: $weatherDetail")
//        uiDetail = weatherDetail
//        println("uiDetail: $uiDetail")
//    }
//
//    fun clear() {
//        println("cancel!")
//        coroutineContext.cancel()
//    }
//}