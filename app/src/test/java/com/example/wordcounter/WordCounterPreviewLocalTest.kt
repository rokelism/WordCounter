package com.example.wordcounter

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.text.AnnotatedString
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.wordcounter.ui.WordCounterScreenErrorPreview
import com.example.wordcounter.ui.WordCounterScreenLandscapePreview
import com.example.wordcounter.ui.WordCounterScreenPortraitPreview
import com.example.wordcounter.ui.WordCounterTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

/**
 * Local JVM unit tests verifying the three Compose preview functions:
 * [WordCounterScreenPortraitPreview], [WordCounterScreenLandscapePreview],
 * and [WordCounterScreenErrorPreview].
 *
 * Renders the real preview composables directly on the JVM with simulated
 * layout configurations, validates their literal fixture snapshots, and invokes
 * each preview's real no-op callbacks (text change + both buttons) to prove state
 * stability and achieve complete coverage of ComposableSingletons lambda methods.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class WordCounterPreviewLocalTest {

    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private fun renderPreviewWithConfiguration(orientation: Int, content: @Composable () -> Unit) {
        val configuration = Configuration(compose.activity.resources.configuration).apply {
            this.orientation = orientation
        }
        // Do not wait for Compose roots before setContent on an initially empty host
        compose.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                content()
            }
        }
        compose.waitForIdle()
    }

    private fun string(id: Int) = compose.activity.getString(id)
    private fun historyTag(index: Int) = "${WordCounterTestTags.HISTORY_CARD}:$index"
    private fun list() = compose.onNodeWithTag(WordCounterTestTags.LAZY_COLUMN)

    private fun show(matcher: SemanticsMatcher, useUnmergedTree: Boolean = true) {
        val target = compose.onNode(matcher, useUnmergedTree = useUnmergedTree)
        if (target.isDisplayed()) return
        scrollToStart()
        repeat(100) {
            if (target.isDisplayed()) return
            if (!scrollOneViewport()) {
                target.assertIsDisplayed()
                return
            }
        }
        throw AssertionError("Visible-node search exceeded 100 viewports: ${matcher.description}")
    }

    private fun scrollToStart() {
        repeat(100) {
            if (!scrollOneViewport(down = false)) return
        }
        throw AssertionError("Could not reach the start of the list within 100 swipes")
    }

    private fun scrollOneViewport(down: Boolean = true): Boolean {
        val node = list().fetchSemanticsNode()
        val range = node.config.getOrNull(SemanticsProperties.VerticalScrollAxisRange) ?: return false
        val previousOffset = range.value()
        list().performTouchInput {
            if (down) swipeUp() else swipeDown()
        }
        compose.waitForIdle()
        val currentOffset = list().fetchSemanticsNode()
            .config[SemanticsProperties.VerticalScrollAxisRange].value()
        return kotlin.math.abs(currentOffset - previousOffset) >= 0.01f
    }

    private fun assertAbsentFromList(matcher: SemanticsMatcher) {
        scrollToStart()
        repeat(100) {
            compose.onNode(matcher, useUnmergedTree = true).assertDoesNotExist()
            if (!scrollOneViewport()) {
                compose.onNode(matcher, useUnmergedTree = true).assertDoesNotExist()
                return
            }
        }
        throw AssertionError("List search exceeded 100 viewports; absence was not established for: ${matcher.description}")
    }

    private fun assertInput(text: String) {
        show(hasSetTextAction())
        compose.onNode(hasSetTextAction()).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(text))
        )
    }

    private fun invokeInputTextChange(replacementText: String) {
        show(hasSetTextAction())
        compose.onNode(hasSetTextAction()).performTextReplacement(replacementText)
        compose.activityRule.scenario.onActivity { activity ->
            activity.currentFocus?.clearFocus()
            WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                .hide(WindowInsetsCompat.Type.ime())
        }
        compose.waitForIdle()
    }

    private fun clickButton(resourceId: Int) {
        val matcher = hasText(string(resourceId)) and hasClickAction()
        show(matcher, useUnmergedTree = false)
        compose.onNode(matcher).performClick()
        compose.waitForIdle()
    }

    private fun calculate() = clickButton(R.string.button_calculate)
    private fun generateSample() = clickButton(R.string.button_generate_sample)

    private fun assertError() = show(hasText(string(R.string.input_error_empty)))

    private fun assertNoError() {
        show(hasSetTextAction())
        compose.onNode(hasText(string(R.string.input_error_empty))).assertDoesNotExist()
    }

    private fun labels() = listOf(
        R.string.label_sentences, R.string.label_words,
        R.string.label_characters, R.string.label_numbers
    ).map(::string)

    private fun assertResults(sentences: Int, words: Int, characters: Int, numbers: Int) {
        show(hasText(string(R.string.results_title)))
        labels().zip(listOf(sentences, words, characters, numbers)).forEach { (label, value) ->
            val metricTag = "${WordCounterTestTags.METRIC_ITEM_PREFIX}$label"
            show(hasTestTag(metricTag))
            show(hasText(label) and hasAnyAncestor(hasTestTag(metricTag)))
            show(
                hasText(value.toString()) and
                    hasAnySibling(hasText(label)) and
                    hasAnyAncestor(hasTestTag(metricTag))
            )
        }
    }

    private fun assertHistoryEntry(
        index: Int, text: String, sentences: Int, words: Int, characters: Int, numbers: Int
    ) {
        val inCard = hasAnyAncestor(hasTestTag(historyTag(index)))
        show(hasTestTag(historyTag(index)))
        show(hasText(text) and inCard)
        labels().zip(listOf(sentences, words, characters, numbers)).forEach { (label, value) ->
            show(hasText("$label: $value") and inCard)
        }
    }

    private fun assertHistorySize(size: Int) {
        if (size == 0) {
            show(hasText(string(R.string.history_empty)))
        } else {
            repeat(size) { show(hasTestTag(historyTag(it))) }
            assertAbsentFromList(hasText(string(R.string.history_empty)))
        }
        assertAbsentFromList(hasTestTag(historyTag(size)))
    }

    @Test
    fun portraitPreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState() {
        renderPreviewWithConfiguration(Configuration.ORIENTATION_PORTRAIT) {
            WordCounterScreenPortraitPreview()
        }

        // Expected values are literal fixture snapshots from the preview definition, not calculator truth
        val fixtureInput = "Labas rytas! Šiandien yra 2026 metai."
        assertInput(fixtureInput)
        assertNoError()
        assertResults(sentences = 2, words = 5, characters = 37, numbers = 1)

        val historyText = "COVID-19 pandemijos metu temperatūra nukrito iki -5,5 laipsnių!"
        assertHistoryEntry(index = 0, text = historyText, sentences = 1, words = 7, characters = 63, numbers = 1)
        assertHistorySize(1)

        // Invoke real no-op callback for text replacement
        invokeInputTextChange("Pakeistas tekstas")
        assertInput(fixtureInput)

        // Invoke real no-op callbacks for calculate and generate sample buttons
        calculate()
        generateSample()

        // Verify fixture snapshot state remains completely unchanged
        assertInput(fixtureInput)
        assertNoError()
        assertResults(sentences = 2, words = 5, characters = 37, numbers = 1)
        assertHistoryEntry(index = 0, text = historyText, sentences = 1, words = 7, characters = 63, numbers = 1)
        assertHistorySize(1)
    }

    @Test
    fun landscapePreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState() {
        renderPreviewWithConfiguration(Configuration.ORIENTATION_LANDSCAPE) {
            WordCounterScreenLandscapePreview()
        }

        val fixtureInput = "Labas rytas! Šiandien yra 2026 metai."
        assertInput(fixtureInput)
        assertNoError()
        assertResults(sentences = 2, words = 5, characters = 37, numbers = 1)

        val historyText = "Prekė A4 kainuoja 3.14 euro..."
        assertHistoryEntry(index = 0, text = historyText, sentences = 1, words = 4, characters = 31, numbers = 1)
        assertHistorySize(1)

        invokeInputTextChange("Pakeistas tekstas")
        assertInput(fixtureInput)

        calculate()
        generateSample()

        assertInput(fixtureInput)
        assertNoError()
        assertResults(sentences = 2, words = 5, characters = 37, numbers = 1)
        assertHistoryEntry(index = 0, text = historyText, sentences = 1, words = 4, characters = 31, numbers = 1)
        assertHistorySize(1)
    }

    @Test
    fun errorPreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState() {
        renderPreviewWithConfiguration(Configuration.ORIENTATION_PORTRAIT) {
            WordCounterScreenErrorPreview()
        }

        assertInput("")
        assertError()
        assertAbsentFromList(hasTestTag(WordCounterTestTags.RESULTS_CARD))
        assertHistorySize(0)

        invokeInputTextChange("Pakeistas tekstas")
        assertInput("")

        calculate()
        generateSample()

        assertInput("")
        assertError()
        assertAbsentFromList(hasTestTag(WordCounterTestTags.RESULTS_CARD))
        assertHistorySize(0)
    }
}
