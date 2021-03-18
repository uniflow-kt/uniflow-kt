
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

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

- `action { current -> ... newState}` - from current state, update the current state
- `actionOn<T> { current as T -> newState }` - from current state <T>, update the current state

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

## [Back To Readme](../Readme.md)
