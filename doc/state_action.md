
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## DataFlow: emit flow of states & events

We call a `Dataflow` a component that will emits states and events. This component is used in a Unidirectional data flow architecture approach 
to ensure:
- Single source of truth: data only come from `Dataflow`
- States/Events are immutable, to avoid any side effects

## States as immutable data

To describe your data flow states, extends the `UIState` class directly or use it as a sealed class as follow:

```kotlin
class WeatherStates : UIState(){
	object LoadingWeather : WeatherStates()
	data class WeatherState(val day : String, val temperature : String) : WeatherStates()
}
```

## Updating our state with an Action

Your ViewModel class, aka your DataFlow, will provide `actions` that will trigger states and events.

An action is a simple function, like that:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {
    
    // getWeather action
    fun getWeather() = action {
        ...
    }
}
```

From our `action { }` codeblock, we can set a new state with `setState` function:

```kotlin
// update the current state
fun getWeather() = action {
    // return directly your state object
    setState { WeatherState(...) }
}
```

## Observing DataFlow states

To listen incoming states from your Activity/Fragment, we need to use `onStates` function:

```kotlin
class MyActivity : AppCompatActivity(){
    
    fun onCreate(...){

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

## Getting the current state

From our `ViewModel` we can have access to the current state with the first action parameter: `currentState`

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    fun getWeather() = action { currentState ->

    }
}
```

## ActionOn: executing an action only from a given state

The `actionOn<T>` function help set a new state if you are in the given state <T>. Else your DataFlow will send `BadOrWrongState` event:

```kotlin
// Execute loadNewLocation action only if current state is in WeatherListState
fun loadNewLocation(location: String) = actionOn<WeatherState>{ currentState : WeatherListState ->
    // currentState is WeatherListState
    // ...
}
```

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)
