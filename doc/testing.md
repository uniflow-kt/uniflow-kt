
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Testing 🚑

### Observing States/Events

Create your ViewModel instance and mock your states observer:

```kotlin
val mockedRepo : WeatherRepository = mockk(relaxed = true)
lateinit var dataFlow : WeatherDataFlow

@Before
fun before() {
    // create WeatherDataFlow instance with mocked WeatherRepository
    dataFlow = WeatherDataFlow(mockedRepo)
    // create test observer 
    view = detailViewModel.createTestObserver()
}
```

Now you can test incoming states & events with `assertReceived`:

```kotlin
@Test
fun `has some weather`() {
    // prepare test data
    val weatherData = WeatherData(...)
    // setup mocked call
    coEvery { mockedRepo.getWeatherForToday() } return weatherData

    // Call getWeather()
    dataFlow.getWeather()
        
    // verify state
    dataFlow.assertReceived (
        WeatherState(weatherData.day, weatherData.temperature)
    )
}
```

### Mocking States/Events

Create your ViewModel instance and mock your states observer:

```kotlin
val mockedRepo : WeatherRepository = mockk(relaxed = true)
lateinit var dataFlow : WeatherDataFlow

@Before
fun before() {
    // create WeatherDataFlow instance with mocked WeatherRepository
    dataFlow = WeatherDataFlow(mockedRepo)
    // create test observer 
    view = detailViewModel.createTestObserver()
}
```

Now you can test incoming states & events with `assertReceived`:

```kotlin
@Test
fun `has some weather`() {
    // prepare test data
    val weatherData = WeatherData(...)
    // setup mocked call
    coEvery { mockedRepo.getWeatherForToday() } return weatherData

    // Call getWeather()
    dataFlow.getWeather()
        
    // verify state
    dataFlow.assertReceived (
        WeatherState(weatherData.day, weatherData.temperature)
    )
}
```

### JUnit Rules

- Change Logging during tests:

```kotlin
companion object {
    init {
        UniFlowLogger.init(SimpleMessageLogger(UniFlowLogger.FUN_TAG, debugThread = true))
    }

    @JvmStatic
    @get:ClassRule
    val uniFlowLoggerTestRule = UniFlowLoggerTestRule()
}
```

- Use TestDispatcher

```kotlin
@get:Rule
val testDispatchersRule = TestDispatchersRule()

private val testCoroutineDispatcher = testDispatchersRule.testCoroutineDispatcher
```

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)
