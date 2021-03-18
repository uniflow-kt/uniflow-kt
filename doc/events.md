
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

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

## [Back To Readme](../Readme.md)


