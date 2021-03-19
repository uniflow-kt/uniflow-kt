
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Getting Started 🚀

### Turn on Logging

At your Android Application start, just turn on Logging:

```kotlin
UniFlowLogger.init(AndroidMessageLogger())
```

### Writing your first UI state

Let's describe our data state with `UIState` type:

```kotlin
data class WeatherState(val day : String, val temperature : String) : UIState()
```

Let's push a new state from our ViewModel:

```kotlin
class WeatherDataFlow(val repo : WeatherRepository) : AndroidDataFlow(defaultState = Empty) {
    
    // Uniflow Action
    fun getWeather() = action {
        // call to get data
        val weather = repo.getWeatherForToday().await()
        // return a new state
        setState { WeatherState(weather.day, weather.temperature) }
    }
}
```

On our UI, let's observe our incoming states:

```kotlin
class WeatherActivity : AppCompatActivity { 
    
    val weatherViewModel : WeatherDataFlow // ViewModel created with Koin for example :)
	
    override fun onCreate(savedInstanceState: Bundle?) {	
        
        // Observe incoming states
        onStates(weatherFlow) { state ->
            when (state) {
                // react on WeatherState update
                is WeatherState -> showWeather(state)
            }
        }
    }
}

```

### Easy to test

Let's create our ViewModel instance and mock your states observer:

```kotlin
// Mocked repository
val mockedRepo : WeatherRepository = mockk(relaxed = true)
// Our DataFlow ViewModel
lateinit var dataFlow : WeatherDataFlow

@Before
fun before() {
    // create WeatherDataFlow instance with mocked WeatherRepository
    dataFlow = WeatherDataFlow(mockedRepo)
    // create test observer 
    view = detailViewModel.createTestObserver()
}
```

Don't forget to setup your test dispatcher:

```kotlin
// For Android ViewModel Test
@get:Rule
val rule = InstantTaskExecutorRule()

// Uniflow Test Dispatcher
@get:Rule
val testDispatcherRule = UniflowTestDispatchersRule()
```

Now we can test incoming states & events with `assertReceived`:

```kotlin
@Test
fun `has some weather`() {
    // prepare test data
    val weatherData = WeatherData()
    // setup mocked call
    coEvery { mockedRepo.getWeatherForToday() } willReturn { weatherData }
    
    // Call getWeather()
    dataFlow.getWeather()
        
    // verify state
    dataFlow.verifySequence(
        UIState.Empty,
        WeatherState(weatherData.day, weatherData.temperature)
    )
}
```
----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)


