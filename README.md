
![Uniflow logo](./doc/uniflow_header.png)

## Setup 🚀

### Version [![Maven Central](https://img.shields.io/maven-central/v/org.uniflow-kt/uniflow-core)](https://search.maven.org/search?q=org.uniflow-kt)


### Gradle setup

Repository is now __Maven Central__:

```gradle
repositories {
    mavenCentral()
}
```

Check the [latest version](https://search.maven.org/search?q=org.uniflow-kt)

```gradle
// Core
implementation 'org.uniflow-kt:uniflow-core:$uniflow_version'
testImplementation 'org.uniflow-kt:uniflow-test:$uniflow_version'

// Android
implementation 'org.uniflow-kt:uniflow-android:$uniflow_version'
testImplementation 'org.uniflow-kt:uniflow-android-test:$uniflow_version'

// Extras
implementation 'org.uniflow-kt:uniflow-saferesult:$uniflow_version'
implementation 'org.uniflow-kt:uniflow-arrow:$uniflow_version'
```

⚠️ Due to Maven Central migration, group id has been updated from `io.uniflow` to `org.uniflow-kt` ⚠️

## Getting started 🚀

- [Quick start with Uniflow](doc/start.md)

## Documentation 📖

- [What is Uniflow?](doc/what.md)
- [Writing an Action to update your state](doc/state_action.md)
- [Pushing Events for side effects](doc/events.md)
- [Running coroutines from your Action](doc/coroutines.md)
- [State Guard - Running an action on a given state](doc/state_guard.md)
- [Notifying State Changes](doc/notify_update.md)
- [Easy Error Handling](doc/errors.md)
- [Logging Actions](doc/logging.md)
- [Testing your DataFlow](doc/testing.md)
- [States and Jetpack Compose](doc/compose.md)
- [From Coroutines Flow<T> to Actions](doc/flow.md)
- [Defining Multiple Streams](doc/multiple_streams.md)
- [Persistent Stream to recover state](doc/persistent.md)
- [More Safely with Functional Approach](doc/functional.md)
- [Migrating from Uniflow 0.x to 1.0.x](doc/migrating.md)

### Sample Apps 🎉

- [Weather App](https://github.com/uniflow-kt/weatherapp-uniflow)
- [Jetpack Compose Samples with Uniflow](https://github.com/uniflow-kt/compose-samples)

## Resources ☕️

- [Riding the state flow (AndroidMakers 2020)](https://www.youtube.com/watch?v=m6dyIv1rDdo)
- [Making Android unidirectional data flow with Kotlin coroutines 🦄](https://medium.com/@giuliani.arnaud/making-android-unidirectional-data-flow-with-kotlin-coroutines-d69966717b6e)
- [An efficient way to use Uniflow](https://blog.kotlin-academy.com/an-efficient-way-to-use-uniflow-2b41a9785a05?gi=bce973f6a529)

## Contact us 💬

Come talk on Kotlin Slack @ [#uniflow channel](https://kotlinlang.slack.com/?redir=%2Fmessages%2Funiflow) 
