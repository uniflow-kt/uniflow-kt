
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Easy Error Handling 🚑

To avoid the use `try/catch` block everywhere, we can provide an `onError` fallback function to our Action: 

- By providing a second function passed to the action block, that receive an error:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    fun getWeather() = action(
        onAction = { state -> 
            // our action code ...
        }, 
        onError = { error, state -> 
            // handle error here 
        })
    
}
```

_Note_: You can set a new state in case of failure:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    fun getWeather() = setState(
        onAction = {
            //...

        }, 
        onError = { 
            error, state -> setState(UIState.Failed("Got failure :(",error,state))
        })

}
```

- override the `onError` Dataflow function to receive uncaught exception:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    // Unhandled errors goes here
    override suspend fun onError(ex: Exception, currentState: UIState) {
        // Error handling ...

        // Can also set a new state or send an event
        setState { UIState.Failed(message = "Uncaught Error", error = ex)}
    }
}
```

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)


