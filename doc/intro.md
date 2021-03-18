
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Quick intro 🚸

UniFlow help you write your app with a simple unidirectional data flow approach (think states and events) to ensure consistency through the time, and this with Kotlin Coroutines.

UniFlow provides:
* Smart way to write a Data flow in pure Kotlin
* Android extensions to let you just focus on States & Events
* Ready for Kotlin coroutines
* Easy to test
* Open to functional programming with [Arrow](https://arrow-kt.io/)

<details><summary>What is Unidirectional Data Flow? 🤔</summary>
<p>

### What is Unidirectional Data Flow?

Unidirectional Data Flow is a concept that means that data has one, and only one, way to be transferred to other parts of the application.

This means that:

- state is passed to the view
- actions are triggered by the view
- actions can update the state
- the state change is passed to the view

The view is a result of the application state. State can only change when actions happen. When actions happen, the state is updated.

Thanks to one-way bindings, data cannot flow in the opposite way (as would happen with two-way bindings, for example), and this has some key advantages:

it’s less error prone, as you have more control over your data
it’s easier to debug, as you know what is coming from where

</p>
</details>

## Getting Started 🚀

### Writing your first UI state

Describe your data flow states with `UIState`:

```kotlin
data class WeatherState(val day : String, val temperature : String) : UIState()
```

Publish state updates from your ViewModel:

```kotlin
class WeatherDataFlow(val repo : WeatherRepository) : AndroidDataFlow(defaultState = Empty) {

    fun getWeather() = action {
        // call to get data
        val weather = repo.getWeatherForToday().await()
        // return a new state
        setState { WeatherState(weather.day, weather.temperature) }
    }
}
```

Observe your state flow from your Activity or Fragment:

```kotlin
class WeatherActivity : AppCompatActivity {

	val weatherFlow : WeatherDataFlow ... // ViewModel created with Koin for example :)
	
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

### Easy to test!

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
    dataFlow.verifySequence(
    	UIState.Empty,
        WeatherState(weatherData.day, weatherData.temperature)
    )
}
```

--
## [Back To Documentation Topics](../README.md#getting-started--documentation-)


