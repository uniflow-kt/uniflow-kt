
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin, using Kotlin coroutines and open to functional programming

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
	setState { UIState.Loading }
	// return directly your state object
	setState { WeatherState(...) }
}
```

A `StateFlow` can handle error for the multiple state update. It will be called if one of the `setState` failed, or if the block code failed:

```kotlin
// push state updates
fun getWeather() = stateFlow({
	// Set loading state
	setState { UIState.Loading }
	// return directly your state object
	setState { WeatherState(...) }
},
// If any error
{ error -> UIState.Failed(error = error) }
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
    }, { error, state -> // get error here })
    
}
```

For each action builder, you can provide a error handling function like below


- override the `onError` function to receive any uncaught exception:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    // Unhandled errors here
    override suspend fun onError(error: Throwable), currentState : UIState?){
        // get error here
    }
}
```

For a `stateFlow`, you can provide a protection like above. It will be called if one of the `setState` failed, or if the block code failed:

```kotlin
// push state updates
fun getWeather() = stateFlow({
	// Set loading state
	setState { UIState.Loading }
	// return directly your state object
	setState { WeatherState(...) }
},
// If any error
{ error, failedState -> UIState.Failed("Got error :(",error,failedState) }
```

## Functional Coroutines, to safely make states & events 🌈

One way to handle properly dangerous calls & exceptions, is to do it with a functional approach.

### Safely wrapping results with Try type

[`Try`](https://arrow-kt.io/docs/arrow/core/try/) is the Success/Failure functional wrapper type of Arrow. It will help you write your state flow in a functional way.

You can wrap any `Try.Success` value like that:

```kotlin
val myData : Any ...

// wrap it as Success
Try.just(myData) or myData.success()
```

Concerning errors, you can wrap a `Try.Failure` error like follow:

```kotlin
val myError : Exception ...

// wrap it as Failure
Try.raiseError(myError) or myError.failure()
```

### Wrapping unsafe expression

To help you deal with expression that can raise exceptions, we provide a result wrapper that will catch any error for you. Use the `safeValue` uniflow function to wrap an expression as `Try`:

```kotlin
// Will transform result as SafeResult.Success and any error to SafeResult.Error

fun myDangerousCall() : MyData

// will produce Try<MyData>
val safeResult : Try<MyData> = safeValue { myDangerousCall() }
```

Here we have the following default builders:

- `safeCall { } ` - wrap Try result (data or exception)
- `networkCall { } ` - wrap Try result, catch exception and wap it in a `NetworkException` object
- `databaseCall { } `- wrap Try result, catch exception and wap it in a `DatabaseException` object

You can also make your own safe result builder, depending on your APIs 👍

_NB_: this could be done also with `Try { }` expression wrapper directly

### Functional operators

You can then build expression to combine unsafe IO calls. Here we use `networkCall` to wrap Retrofit expression:

```kotlin
networkCall { weatherDatasource.geocode(targetLocation).await() }
	    .map { it.mapToLocation() ?: error("Can't map to location: $it") }
	    .flatMap { (lat, lng) -> networkCall { weatherDatasource.weather(lat, lng).await() } }
	    .map { it.mapToWeatherEntities(targetLocation) }
	    .onSuccess { weatherCache.addAll(it) }
```

Amoung the classical `Try` functional operators, we add some more:  
- `get` - get the existing value or throw current error's exception
- `getOrNull` - get the existing value or null
- `onSuccess` - do something on existing value
- `onFailure` - do something on existing error

`get` & `getOrNull` are terminal operators, they give you the final result of your functional flow.

### Making safe states & events

In your DataFlow ViewModel class, you will make sequence of operation to result in UIState(s):

```kotlin
fun loadNewLocation(location: String) = fromState<WeatherListState> {
        getWeatherForLocation(location)
                .toState( { it.mapToWeatherListState() }, { error -> UIState.Failed(error = error) })
    }
```

- `mapState` - map current value to a UIState value
- `toState` - get & map current value to a UIState (can specify Success & Failure state mapping)
- `toStateOrNull` - get & map current Success value to UIState else return null

`toState` & `toStateOrNull` are terminal operators

To send any event, use the `onSuccess` or `onFailure` operator, to send a it:

```kotlin
fun loadNewLocation(location: String) = fromState<WeatherListState> {
        getWeatherForLocation(location)
                .onFailure { error -> sendEvent(WeatherListUIEvent.ProceedLocationFailed(location, error)) }
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


