
# UniFlow 🦄 - A Simple Unidirectional Data Flow framework for Android, using Kotlin coroutines

## Current Version

Uniflow current version is `0.3.8`

## Setup

Choose one of the following dependency:

```gradle
// Android
implementation 'io.uniflow:uniflow-android:$version'
testImplementation 'io.uniflow:uniflow-android-test:$version'

// AndroidX
implementation 'io.uniflow:uniflow-androidx:$version'
testImplementation 'io.uniflow:uniflow-androidx-test:$version'
```

## What is UniFlow?

A Simple Unidirectional Data Flow framework for Android, using Kotlin Coroutines


Describe your data flow states with `UIState`:

```kotlin
data class WeatherState(val weather : Weather) : UIState()
```

Publish states from your ViewModel:

```kotlin
class WeatherViewModelFlow : AndroidDataFlow() {

    init {
        // init state as Loading
        setState { UIState.Loading }
    }

    fun getMyWeather(val day : String) = setState {
        // Background call
        val weather = getWeatherForDay(day).await()
        // return state to UI
        WeatherState(weather)
    }
}
```

Observe your state flow from your Activity or Fragment:

```kotlin
class WeatherActivity : AppCompatActivity {

    // ViewModel instance here
    val myWeatherFlow : WeatherViewModelFlow ...

     override fun onCreate(savedInstanceState: Bundle?) {
     
        // Observe incoming states
        onStates(myWeatherFlow) { state ->
            when (state) {
                is UIState.Loading -> showLoading()
                is WeatherState -> showWeather(state)
                is UIState.Failed -> showError(state.error)
            }
        }
    }
}

```

## SetState, WithState, FromState

SetState help you register a new state:

```kotlin
// update the current state
fun getDetail() = setState{
        // return directly your state object
        WeatherState(...)
    }
```


FromState help you register a state if you are in the given state, else send BadOrWrongState event:

```kotlin
// Execute this state update only if current state is in WeatherListState
fun loadNewLocation(location: String) = fromState<WeatherListState>{ currentState ->
        // currentState is WeatherListState
        // ...
    }
```

For more side effects actions, you can use `UIEvent` and avoid update the current state with `withState`:

```kotlin
fun getDetail() = withState {
        // won't update the current state
    }
```


## Trigger also Events

For fire and forget side effects/events, define some events with `UIEvent`:

```kotlin
// Events definition
sealed class WeatherEvent : UIEvent() {
    data class Success(val location: String) : WeatherEvent()
    data class Failed(val location: String, val error: Throwable? = null) : WeatherEvent()
}
```

From your Data Flow VIewModel, trigger events with `sendEvent()`:

```kotlin
class WeatherListViewModel(
        private val getCurrentWeather: GetCurrentWeather,
        private val getWeatherForLocation: GetWeatherForGivenLocation
) : AndroidDataFlow() {


    fun loadNewLocation(location: String) = fromState<WeatherListState> (
            {
                // send event
                sendEvent(WeatherEvent.Success(location))

            },
            { error ->
                // on error send event
                sendEvent(WeatherEvent.Failed(location,error))
            })
}

```

_note_: sendEvent() return a null state (UIState?). Can help write

Observe events from your ViewModel with `onEvent`:

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


## Clean Error Handling


Each action allow you to provide an error handling function. You can also catch any error more globally:


```kotlin
class WeatherViewModelFlow : AndroidDataFlow() {

    init {
        // init state as Loading
        setState { UIState.Loading }
    }

    fun getMyWeather(val day : String) = setState(
        { lastState ->
            // Background call
            val weather = getWeatherForDay(day).await()
            // return state to UI
            WeatherState(weather)
        },
        { error -> // get error here })

    // Unhandled errors here
    override suspend fun onError(error: Throwable){
        // ...
    }

}
```

## Scheduling & Testing

First don't forget to use thr following rule:

```kotlin
@get:Rule
var rule = TestDispatchersRule()
```

`TestDispatchersRule` allow you to dispatch with `Unconfined` thread, as we flatten all scheduling to help sequential running of all states & events.

You can also use the `TestThreadRule`, to emulate a main thread: replace main dispatcher by a single thread context dispatcher

## Easy testing with Mockk

Create your ViewModel instance and mock your states/events observer (``view` here):

```kotlin
@Before
fun before() {
    detailViewModel = DetailViewModel(id, getWeatherDetail)
    view = detailViewModel.mockObservers()
}
```

Now you can test incoming states with `hasState`:

```kotlin
@Test
fun testGetLastWeather() {
    detailViewModel.getDetail()

    verifySequence {
        view.hasState(UIState.Loading)
        view.hasState(WeatherDetail(...))
    }
}
```

also check incoming events with `hasEvents`:

```kotlin
@Test
fun testGetLastWeather() {
    detailViewModel.getDetail()

    verifySequence {
        view.hasEventWeatherListUIEvent.Success)
    }
}
