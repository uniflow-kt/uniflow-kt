//package io.uniflow.android
//
//import com.myweatherapp.domain.model.weather.DailyForecastId
//import com.myweatherapp.domain.usecase.weather.GetWeatherDetail
//import com.myweatherapp.main.view.detail.mapToDetailState
//import com.myweatherapp.unit.MockedData
//import io.mockk.coEvery
//import io.mockk.mockk
//import io.uniflow.core.setupUniFlow
//import io.uniflow.core.dispatcher.TestDispatcherConfiguration
//import io.uniflow.core.logger.SimpleMessageLogger
//import kotlinx.coroutines.runBlocking
//import org.junit.Assert
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertTrue
//import org.junit.Test
//
//
//class CoViewModelTest {
//
//    val getWeatherDetail: GetWeatherDetail = mockk()
//
//    val id = DailyForecastId("ID")
//
//    init {
//        setupUniFlow(SimpleMessageLogger(), TestDispatcherConfiguration())
//    }
//
//    @Test
//    fun testLaunch() = runBlocking {
//        val weather = MockedData.dailyForecasts.first()
//
//        val vm = MyTestViewModel(id, getWeatherDetail)
//
//        coEvery { getWeatherDetail(id) } returns weather
//
//        vm.success()!!.join()
//
//        Assert.assertEquals(weather.mapToDetailState(), vm.uiDetail)
//
//        vm.clear()
//    }
//
//    @Test
//    fun testLaunchIO() = runBlocking {
//        val weather = MockedData.dailyForecasts.first()
//
//        val vm = MyTestViewModel(id, getWeatherDetail)
//
//        coEvery { getWeatherDetail(id) } returns weather
//
//        vm.successIO()!!.join()
//
//        Assert.assertEquals(weather.mapToDetailState(), vm.uiDetail)
//
//        vm.clear()
//    }
//
//    @Test
//    fun testLaunchIOError() = runBlocking {
//        val error = Throwable("Boom")
//
//        val vm = MyTestViewModel(id, getWeatherDetail)
//
//        coEvery { getWeatherDetail(id) } throws error
//        vm.error()
//
//        assertEquals(error, vm.uiError)
//        assertTrue(vm.uiDetail == null)
//
//        vm.clear()
//    }
//
//    @Test
//    fun testLaunchIOCancel() = runBlocking {
//        val weather = MockedData.dailyForecasts.first()
//
//        val vm = MyTestViewModel(id, getWeatherDetail)
//
//        coEvery { getWeatherDetail(id) } returns weather
//
//        vm.longTask()
//        vm.clear()
//
//        Assert.assertNull(vm.uiDetail)
//        Assert.assertNull(vm.uiError)
//        Assert.assertTrue(vm.coroutineContext.parentJob.isCancelled)
//    }
//}