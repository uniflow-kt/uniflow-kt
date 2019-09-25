
# UniFlow 🦄 - Unidirectional Data Flow for Android, using Kotlin coroutines

## Setup

#### Current version is `0.8.1`

Choose one of the following dependency:

```gradle
jcenter()

// Minimal Core
implementation 'io.uniflow:uniflow-core:$version'
testImplementation 'io.uniflow:uniflow-test:$version'

// Android
implementation 'io.uniflow:uniflow-android:$version'
testImplementation 'io.uniflow:uniflow-android-test:$version'

// AndroidX
implementation 'io.uniflow:uniflow-androidx:$version'
testImplementation 'io.uniflow:uniflow-androidx-test:$version'
```

this version is based on Kotlin `1.3.50` & Coroutines `1.3.0`

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
class WeatherDataFlow(val repo : WeatherRepository) : AndroidDataFlow() {

    fun getWeather() = setState {
        // call to get data
        val weather = repo.getWeatherForToday().await()
        // return a new state
        WeatherState(weather.day, weather.temperature)
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
	// create mocked observer
	view = detailViewModel.mockObservers()
}
```

Now you can test incoming states with `hasState`. We are providing Mockk to help mock underlying coroutines call: 

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
	verifySequence {
		view.hasState(WeatherState(weatherData.day, weatherData.temperature))
	}
}
```

## Actions, States & Events 🔄

Your ViewModel class, aka your DataFlow, will provide `actions` that will trigger states and events.

An action is a simple function, that directly use one state operator:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {
    
    // getWeather action
    fun getWeather() = setState {
        ...
    }
}
```

The state mutation operator are the following:

- `setState { current -> ... newState}` - from current state, udpate the current state
- `fromState<T> { current as T -> newState }` - from current state <T>, udpate the current state
- `stateFlow { setState() ... }` - allow several state updates from same flow/job
- `withState { current -> ... }` - from current state, do a side effect


### Writing states as immutable data

To describe your data flow states, inherit from `UIState` class directly or use a sealed class as follow:

```kotlin
class WeatherStates : UIState(){
	object LoadingWeather : WeatherStates()
	data class WeatherState(val day : String, val temperature : String) : WeatherStates()
}

```

### Updating the current state

`SetState` is an action builder to simply set a new state:

```kotlin
// update the current state
fun getWeather() = setState{
    // return directly your state object
    WeatherState(...)
}
```

Don't forget to listen to states from your Activity/Fragment with `onStates`:

```kotlin
// Observe incoming states
onStates(weatherFlow) { state ->
	when (state) {
		// react on WeatherState update
		is WeatherState -> showWeather(state)
	}
}
```

The `FromState<T>` operator, help set a new state if you are in the given state <T>. Else your DataFlow will send `BadOrWrongState` event:

```kotlin
// Execute loadNewLocation action only if current state is in WeatherListState
fun loadNewLocation(location: String) = fromState<WeatherState>{ currentState ->
    // currentState is WeatherListState
    // ...
}
```

### StateFlow: multiple state updates from one action

When you need to publish several states from one action function, then you can't use one of the function above. Use the `stateFlow` action builder to let you publish states when needed, with `setState()` function:

```kotlin
// push state updates
fun getWeather() = stateFlow {
	// Set loading state
	setState(UIState.Loading)
	// return directly your state object
	setState(WeatherState(...))
}
```

### Side effects & Events

For side effects actions, you can use `UIEvent` and avoid update the current state with `withState`:

```kotlin
fun getWeather() = withState {
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

From your VIewModel, simply trigger an event with `sendEvent()` function:

```kotlin
	fun getWeather() = withState {
	    // send event
	    sendEvent(WeatherEvent.Success(location))
	}
}

```

_note_: sendEvent() can be used in any state mutation operator


To observe events from your Activity/Fragment view class, use the  `onEvent` fucntion with your ViewModel instance:

```kotlin
onEvents(viewModel) { event ->
    when (val data = event.take()) {
        is WeatherListUIEvent.Success -> showSuccess(data.location)
        is WeatherListUIEvent.Failed -> showFailed(data.location, data.error)
    }
}
```

On an event, you can either `take()` or `peek()` its data:

- `take` - consume the event data, can't be taken by other event consumer
- `peek` - peek the event's data, even if the data has been consumed


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

### Error handling out of the box

Each action is surrounded by a `try/catch` block for you under the hood. It avoids you to use `try/catch` block every where around your code. Then you can catch errors in 2 ways: 

- provide a second function passed to the state mutation operator, that receive an error:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    fun getWeather() = setState({
        // call to get data
        val weather = repo.getWeatherForToday().await()
        // return a new state
        WeatherState(weather.day, weather.temperature)
    }, { error -> // get error here })
    
}
```

- override the `onError` function to receive any uncaught exception:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    // Unhandled errors here
    override suspend fun onError(error: Throwable){
        // get error here
    }
}
```

## Functional Coroutines, to safely make states & events 🌈

One way to handle properly exceptions, is to do it with a functional approach.

### SafeResult, your safe result wrapper!

`SafeResult` is the Success/Error functional wrapper type of uniflow. It will help you write your state flow in a functional way.

You can wrap any value like that:

```kotlin
val myData : Any ...

// wrap it as SafeResult
safeResult(myData) or myData.asSafeResult()
```

Concerning errors, you can wrap a SaferResult error like follow:

```kotlin
val myError : Exception ...

// wrap it as SafeResult
errorResult(myError)
```

### Wrapping unsafe code

To help you deal with expression that can raise exceptions, we provide lazy SafeResult builder, that will catch any error for you. Use the `safeCall` function to wrap an expression as SafeResult:

```kotlin
// Will transform result as SafeResult.Success and any error to SafeResult.Error

fun myDangerousCall() : MyData

// will produce SafeResult<MyData>
safeCall { myDangerousCall() }

```

Here we have the following SafeResult builders:

- `safeCall { } ` - wrap SafeResult (data or exception)
- `networkCall { } ` - wrap SafeResult, catch exception and wap it in a `NetworkException` object
- `databaseCall { } `- wrap SafeResult, catch exception and wap it in a `DatabaseException` object

You can also make your own SafeResult builder, depending on your APIs 👍

### Functional operators

You can then build expression to combine unsafe IO calls (here we use `networkCall` to wrap Retrofit expression):

```kotlin
networkCall { weatherDatasource.geocode(targetLocation).await() }
	    .map { it.mapToLocation() ?: error("Can't map to location: $it") }
	    .flatMap { (lat, lng) -> networkCall { weatherDatasource.weather(lat, lng).await() } }
	    .map { it.mapToWeatherEntities(targetLocation) }
	    .onValue { weatherCache.addAll(it) }
```

SafeResult offers some classic operators:
- `map` - map value of a SafeResult
- `flatMap` - transform a SafeResult value into another
- `onValue` - do something on existing value
- `onError` - do something on existing error
- `get` - get the existing value or throw current error's exception
- `getOrNull` - get the existing value or null

`get` & `getOrNull` are terminal operators, they give you the final result of your functional flow.

### Making states & events

In your DataFlow ViewModel class, you will operators concerning UIState:

```kotlin
fun loadNewLocation(location: String) = fromState<WeatherListState> {
        getWeatherForLocation(location)
                .toState( { it.mapToWeatherListState() }, { error -> UIState.Failed(error = error) })
    }
```

- `mapState` - map current value to a UIState value
- `toState` - get & map current value to a UIState (can specify success & error state)
- `toStateOrNull` - get & map current value to UIState if only it's a success

`toState` & `toStateOrNull` are terminal operators

To send any event, use the `onValue` or `onError` operator, to send a it:

```kotlin
fun loadNewLocation(location: String) = fromState<WeatherListState> {
        getWeatherForLocation(location)
                .onError { error -> sendEvent(WeatherListUIEvent.ProceedLocationFailed(location, error)) }
                .toStateOrNull { it.mapToWeatherListState() }
    }
```

## More tools for test

### Scheduling & Testing

First don't forget to use thr following rule:

```kotlin
@get:Rule
var rule = TestDispatchersRule()
```

`TestDispatchersRule` allow you to dispatch with `Unconfined` thread, as we flatten all scheduling to help sequential running of all states & events.

You can also use the `TestThreadRule`, to emulate a main thread: replace main dispatcher by a single thread context dispatcher


