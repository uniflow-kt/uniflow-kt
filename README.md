
# Uniflow 🦄 -- Simple Unidirectional Data Flow for Android & Kotlin

## Setup 🚀

### Current version

```gradle
// Current Stable
uniflow_version = "1.0.0"
```

### Gradle setup

Repository is now __Maven Central__:

```gradle
repositories {
    mavenCentral()
}
```

⚠️ Maven group id has been updated from `io.uniflow` to `org.uniflow-kt` ⚠️

```gradle
// Minimal Core
implementation 'org.uniflow-kt:uniflow-core:$uniflow_version'
testImplementation 'org.uniflow-kt:uniflow-test:$uniflow_version'

// Android
implementation 'org.uniflow-kt:uniflow-android:$uniflow_version'
testImplementation 'org.uniflow-kt:uniflow-android-test:$uniflow_version'
```

## Getting started & Documentation 📖
- [Getting Started](./Intro.md)
- [Documentation](./Documentation.md)
- Sample app: https://github.com/arnaudgiuliani/weatherapp-uniflow

Come talk on Kotlin Slack @ [#uniflow channel](https://kotlinlang.slack.com/?redir=%2Fmessages%2Funiflow)

## Migrating from 0.x to 1.0 🚧

#### Update from `io.uniflow` to `org.uniflow-kt` (Maven Central)

//TBD

#### `io.uniflow:uniflow-androidx` merged into `org.uniflow-kt:uniflow-android`

//TBD

#### Imports & Package of Android API

//TBD


## Online Resources 🎉

- [An efficient way to use Uniflow](https://blog.kotlin-academy.com/an-efficient-way-to-use-uniflow-2b41a9785a05?gi=bce973f6a529)
- [Making Android unidirectional data flow with Kotlin coroutines 🦄](https://medium.com/@giuliani.arnaud/making-android-unidirectional-data-flow-with-kotlin-coroutines-d69966717b6e)


