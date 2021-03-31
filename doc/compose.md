
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Jetpack Compose 🚀

When working with Jetpacvk compose, you can simply observe your `AndroidDataFlow` states with the `states` property. 
From it you can observe Uniflow `UIState` and observe them as Compose `State`, with `observeAsState()`:

`states.observeAsState().value?.let { state -> }`

In the following example (JetSurvey App), we observe incoming `UIState` to make Compose `State`:

```kotlin
class SurveyFragment : Fragment() {

	private val viewModel: SurveyViewModel

    override fun onCreateView(...): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                JetsurveyTheme {
                    viewModel.states.observeAsState().value?.let { surveyState ->
                    	// New Compose Screens
                        when (surveyState) {
                            is SurveyState.Questions -> SurveyQuestionsScreen( ... )
                            is SurveyState.Result -> SurveyResultScreen(
                                result = surveyState,
                                onDonePressed = {
                                    activity?.onBackPressedDispatcher?.onBackPressed()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
```

Concerning Events, you can use Uniflow in a regular way with your ViewModel and Activity/Fragment to listen them.

Check the [JetSurvey Compose Sample app](https://github.com/uniflow-kt/compose-samples/tree/uniflow/Jetsurvey) for more details.

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)


