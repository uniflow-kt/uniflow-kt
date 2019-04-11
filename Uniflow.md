

## What is a Unidirectional Data Flow?

Redux, MVI... same intention

A State is a set of attributes that represent the _state_ of the UI of a given time.

An event occurs doesn't not affect directly the UI state and is more a side effect of your flow.

An Action/Intent, is a function of your DataFlow offered to your View.


## Describe your flow with states & events

Here is how to describe states and events:

### States

You need to define the states handled by your UI flow, by inheriting the `UIState` class:

```kotlin
data class MyLoadedState(...) : UIState()
```

### Events

You need to define the events handled by your UI flow, by inheriting the `UIEvent` class:

```kotlin
data class MyEvent() : UIEvent()
```

## Making your Data Flow with a ViewModel

Based on ViewModel Android architecture component, setup your data flow with `DataFlow` class as follow:

```kotlin
class MyViewModel(...) : DataFlow()
```

### Actions & States

An action is a function that triggers a state change. This action is done in background with a coroutine call:

```kotlin
class MyViewModel(...) : DataFlow()
```


### Setup intial state

### Error handling

### Sending events

### Generic States & Events

## Observe your Data Flow

## Test your Data Flow