
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin, using Kotlin coroutines

<br>

## Writing an Action

Your ViewModel class, aka your DataFlow, will provide `actions` that will trigger states and events.

An action is a simple function, that directly use one state operator:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {
    
    // getWeather action
    fun getWeather() = action {
        ...
    }
}
```

The state mutation operator are the following:

- `action { current -> ... newState}` - from current state, udpate the current state
- `actionOn<T> { current as T -> newState }` - from current state <T>, udpate the current state

## States as immutable data

To describe your data flow states, extends the `UIState` class directly or use it as a sealed class as follow:

```kotlin
class WeatherStates : UIState(){
	object LoadingWeather : WeatherStates()
	data class WeatherState(val day : String, val temperature : String) : WeatherStates()
}
```

## Getting the current state

From your `ViewModel` you can have access to the current state with the `state` by incoming action function argument:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    fun getWeather() = action { currentState ->

    }
}
```


## Updating the current state

`action` is an action builder to simply set a new state, with `setState` function:

```kotlin
// update the current state
fun getWeather() = action {
    // return directly your state object
    setState { WeatherState(...) }
}
```

Listen to states from your Activity/Fragment with `onStates`:

```kotlin
// Observe incoming states
onStates(weatherFlow) { state ->
	when (state) {
		// react on WeatherState update
		is WeatherState -> showWeather(state)
	}
}
```

The `actionOn<T>` operator help set a new state if you are in the given state <T>. Else your DataFlow will send `BadOrWrongState` event:

```kotlin
// Execute loadNewLocation action only if current state is in WeatherListState
fun loadNewLocation(location: String) = actionOn<WeatherState>{ currentState : WeatherListState ->
    // currentState is WeatherListState
    // ...
}
```

## Applying side effects with Events

When you don't want to update the current state, you can use an event:

```kotlin
fun getWeather() = action {
    sendEvent(...)
    // won't update the current state
}
```

The same way you define States, we define events from `UIEvent` class, as immutable Kotlin data:

```kotlin
// Events definition
sealed class WeatherEvent : UIEvent() {
    data class Success(val location: String) : WeatherEvent()
    data class Failed(val location: String, val error: Throwable? = null) : WeatherEvent()
}
```

From your VIewModel, simply send an event with `sendEvent()` function:

```kotlin
	fun getWeather() = action {
	    // send event
	    sendEvent(WeatherEvent.Success(location))
	}
}

```


To observe events from your Activity/Fragment view class, use the  `onEvent` function with your ViewModel instance:

```kotlin
onEvents(viewModel) { event ->
    when (val data = event.take()) {
        is WeatherListUIEvent.Success -> showSuccess(data.location)
        is WeatherListUIEvent.Failed -> showFailed(data.location, data.error)
    }
}
```

_Warning_: On an event, you can either `take()` or `peek()` its data:

- `take` - consume the event data, can't be taken by other event consumer
- `peek` - peek the event's data, even if the data has been consumed


## Testing 🚑

### Asserting States & Events

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

### Extra JUnit Rules

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

## Smart Coroutines ✨🦄

### Coroutines, the easy way

Every action launched by a DataFlow is runned in a coroutines context, by default on IO Thread. Then you know that by default, we launch things in background for you 👍

If you need to switch context of the current thread you use from your action:

- `onIO { }` - equivalent of withContext(IO dispatcher)
- `onMain { }` - equivalent of withContext(IO Main)
- `onDefault { }` - equivalent of withContext(IO default)

And if you need to launch a job on different thread, use:

- `launchOnIO { }` - equivalent of withContext(IO dispatcher)
- `launchOnMain { }` - equivalent of withContext(IO Main)
- `launchOnDefault { }` - equivalent of withContext(IO default)

_note_: we simplify here the wirting of such threading operator, as we also make an asbtaction around the used dispatcher to help further testing. See testing section below.

### Error handling

Each action is surrounded by a `try/catch` block for you under the hood. It avoids you to use `try/catch` block every where around your code. Then you can catch errors in 2 ways: 

- provide a second function passed to the state mutation operator, that receive an error:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    fun getWeather() = action(
        onAction = {
            // call to get data
            val weather = repo.getWeatherForToday().await()
            // return a new state
            WeatherState(weather.day, weather.temperature)
        }, 
        onError = { error, currentState -> 
            // handle error here 
        })
    
}
```

For each action builder, you can provide a error handling function like below.

_Note_: Can be interesting when catching exception, you can set the current state of you DataFlow:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    fun getWeather() = setState(
        onAction = {
            //...

        }, 
        onError = { 
            error, state -> UIState.Failed("Got failure :(",error,state) 
        })

}
```


- override the `onError` function to receive any uncaught exception:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    // Unhandled errors here
    override suspend fun onError(error: Throwable, currentState :UIState, actionFlow : ActionFlow){
        // get error here

        // setting a new state on given error
        actionFlow.setState { UIState.Failed("got error",error) }
    }
}
```


