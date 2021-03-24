
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Logic depending on current state

When dealing with different states, we can have an action that needs to run only from a given state.

We can use the `onState<T> { t -> ... }` to safely execute a code for given state, but then we have to manually handle any "wrong" states. 

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    fun getWeather() = action { 
        onState<WeatherState> { currentState ->
            // running on MyState state
        }
        // Anything here?
    }
}
```

## State Guard - Running an action on a given state

The `actionOn<T>` function help set a new state if you are in the given state <T>:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {
    
    fun actionOnCurrentWeather(location: String) = actionOn<WeatherState> { currentState: WeatherState ->
        // We have directly the WeatherState here
        // Will throw BadOrWrongStateException if not the right state
    }
}
```

If your `DataFlow` is not in the right state type, the action will run on error with a `BadOrWrongStateException` error.
You can deal with this error on the `onError` action function, or your `DataFlow.onError` function:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    override suspend fun onError(ex: Exception, currentState: UIState) {
        // Send event to UI if we have BadOrWrongStateException
        if (ex is BadOrWrongStateException){
            sendEvent(UIEvent.BadOrWrongState(currenState = currentState))
        }
    }
}
```

See also the [Error Handling section](./errors.md).

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)
