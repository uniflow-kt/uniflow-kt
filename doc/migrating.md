## Migrating from 0.x to 1.0 🚧

#### Update from `io.uniflow` to `org.uniflow-kt` (Maven Central)

//TBD

#### `io.uniflow:uniflow-androidx` merged into `org.uniflow-kt:uniflow-android`

//TBD

#### Imports & Package of AndroidX API

//TBD

#### Events - No more peek/take

Event consumption is now transparent. You don't have to care about `take`/`peek` function on it to unwrap your event content. We directly receive it:

```kotlin
class MyActivity : AppCompatActivity(){

    fun onCreate(...) {
        
        // Let's observe incoming events
        onEvents(viewModel) { event ->
            when (event) {
                is WeatherListUIEvent.Success -> showSuccess(event.location)
                is WeatherListUIEvent.Failed -> showFailed(event.location, event.error)
            }
        }
    }
}
```

#### ActionOn throws BadOrWrongStateException

The `actionOn<>` function will emit `BadOrWrongStateException` if the current state is not the required state. It won't be notified directly as a `BadOrWrongState` event.
We need to do it by hand if needed:

```kotlin
class WeatherDataFlow(...) : AndroidDataFlow() {

    fun actionOnCurrentWeather(location: String) = actionOn<WeatherState> { currentState: WeatherState ->
        // We have directly the WeatherState here
        // Will throw BadOrWrongStateException if not the right state
    }
    
    override suspend fun onError(ex: Exception, currentState: UIState) {
        // Send event to UI if we have BadOrWrongStateException
        if (ex is BadOrWrongStateException){
            sendEvent(UIEvent.BadOrWrongState(currenState = currentState))
        }
    }
}
```

#### Test Dispatchers

//TBD

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)
