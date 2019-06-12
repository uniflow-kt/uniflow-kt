
# UniFlow - A Simple Unidirectional Data Flow framework for Android, using Kotlin coroutines

## Current Version

Uniflow current version is `0.2.7`

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

A Simple Unidirectional Data Flow framework for Android, using Kotlin coroutines

Describe your data flow states & ViewModel:

```kotlin
data class WeatherState(val weather : Weather) : UIState()

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

    // Unhandled errors here
    override suspend fun onError(error: Throwable){
        setState { UIState.Failed(error = error) }
    }
}
```

Just observe your state flow from your Activity or Fragment:

```kotlin
class WeatherActivity : AppCompatActivity {

    // created WeatherViewModelFlow ViewModel instance here
    val myWeatherFlow : WeatherViewModelFlow ...

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe incoming states flow
        onStates(myWeatherFlow) { state ->
            when (state) {
                is UIState.Loading -> showLoading()
                is WeatherState -> showWeather(state)
                is UIState.Failed -> showError(state.error)
            }
        }
        myWeatherFlow.getMyWeather("monday")
    }
}

```

## SetState, WithState, FromState

SetState help you register a new state:

```kotlin
// update the current state
fun getDetail() = setState{
        getWeatherDetail(id).mapToDetailState()
    }
```


FromState help you register a state if you are in the given state, else send BadOrWrongState event:

```kotlin
// Execute this state update only if current state is in WeatherListState
fun loadNewLocation(location: String) = fromState<WeatherListState>{
        sendEvent(WeatherListUIEvent.ProceedLocation(location))
        getWeatherForLocation(location).mapToWeatherListState()
    }
```

WithState help you execute a side effect against the current state:

```kotlin
// execute an action without updating the current state
fun getLastWeather() = withState{
        sendEvent(UIEvent.Pending)
        loadCurrentWeather()
        sendEvent(UIEvent.Success)
    }
```


## Easy testing

Create your ViewModel and State/Event observers on it:

```kotlin
@Before
fun before() {
    detailViewModel = DetailViewModel(id, getWeatherDetail)
    view = detailViewModel.mockObservers()
}
```

Now you can test incoming states and events:

```kotlin
@Test
fun testGetLastWeather() {
    detailViewModel.getDetail()

    verifySequence {
        view.states.onChanged(UIState.Loading)
        view.states.onChanged(WeatherDetail(...))
    }
}
```

