
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

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

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)


