package com.example.wordcounter

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
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.text.AnnotatedString
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.wordcounter.ui.WordCounterTestTags
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

/**
 * Local JVM unit tests executing the real [MainActivity] and its Compose UI tree
 * on the JVM via Robolectric without requiring an Android emulator or device.
 *
 * Verifies interactive state flow, input validation, sample cycling, lifecycle
 * recreation, duplicate calculation ordering, and both portrait and landscape
 * layout branches using stable semantic test tags and resource-aware matchers.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class WordCounterActivityLocalTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

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
            val distance = height * 0.3f
            swipe(
                start = center,
                end = center.copy(y = center.y + if (down) -distance else distance)
            )
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

    private fun setInput(text: String) {
        show(hasSetTextAction())
        compose.onNode(hasSetTextAction()).performTextReplacement(text)
        compose.activityRule.scenario.onActivity { activity ->
            activity.currentFocus?.clearFocus()
            WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                .hide(WindowInsetsCompat.Type.ime())
        }
        compose.waitForIdle()
        assertInput(text)
    }

    private fun assertInput(text: String) {
        show(hasSetTextAction())
        compose.onNode(hasSetTextAction()).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(text))
        )
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

    /**
     * Asserts the 4 metric values in the current results card.
     * Order: sentences, words, characters (including spaces/punctuation), numbers.
     */
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
    fun validCalculation_displaysAllFourStatisticsAndHistoryEntryWithHandCheckedValues() {
        // Hand-checked independent constants: 2 sentences, 7 lexical words, 54 characters, 2 numbers (2026 and -5,5).
        val text = "Labas rytas! Šiandien yra 2026 metai, kaina -5,5 euro."
        setInput(text)
        calculate()
        assertNoError()
        assertResults(sentences = 2, words = 7, characters = 54, numbers = 2)
        assertHistoryEntry(index = 0, text = text, sentences = 2, words = 7, characters = 54, numbers = 2)
        assertHistorySize(1)
    }

    @Test
    fun emptyAndWhitespaceValidation_showsErrorWithoutHistory_andRecoversUponValidInput() {
        for (text in listOf("", "   \t  \n  ")) {
            setInput(text)
            calculate()
            assertError()
            assertAbsentFromList(hasTestTag(WordCounterTestTags.RESULTS_CARD))
            assertHistorySize(0)
        }
        setInput("Labas rytas")
        assertNoError()
        assertAbsentFromList(hasTestTag(WordCounterTestTags.RESULTS_CARD))
        calculate()
        assertResults(sentences = 1, words = 2, characters = 11, numbers = 0)
        assertHistoryEntry(index = 0, text = "Labas rytas", sentences = 1, words = 2, characters = 11, numbers = 0)
        assertHistorySize(1)
    }

    @Test
    fun generateSample_cyclesThroughSamplesWithWraparound_clearsValidationWithoutAutoCalculate() {
        val samples = compose.activity.resources.getStringArray(R.array.test_samples)
        assertTrue("Resource array must contain multiple sample items", samples.size > 1)
        calculate()
        assertError()

        (samples.toList() + samples.first()).forEach { expected ->
            generateSample()
            assertInput(expected)
            assertNoError()
            assertAbsentFromList(hasTestTag(WordCounterTestTags.RESULTS_CARD))
            assertHistorySize(0)
        }
    }

    @Test
    fun activityRecreation_verifiesNewInstance_andPreservesInputResultsHistoryAndCyclePosition() {
        val samples = compose.activity.resources.getStringArray(R.array.test_samples)
        assertTrue(samples.size >= 2)
        setInput("Pirmas tekstas 100")
        calculate()
        setInput("Antrasis sakinys! Čia yra 2 žodžiai.")
        calculate()
        assertResults(sentences = 2, words = 5, characters = 36, numbers = 1)
        assertHistoryEntry(index = 0, text = "Antrasis sakinys! Čia yra 2 žodžiai.", sentences = 2, words = 5, characters = 36, numbers = 1)
        assertHistoryEntry(index = 1, text = "Pirmas tekstas 100", sentences = 1, words = 2, characters = 18, numbers = 1)
        assertHistorySize(2)
        generateSample()
        assertInput(samples[0])

        val oldActivity = compose.activity
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()

        compose.activityRule.scenario.onActivity { newActivity ->
            assertNotSame("Recreation must create a different Activity instance", oldActivity, newActivity)
            assertTrue("Original Activity must be destroyed", oldActivity.isDestroyed)
        }

        assertInput(samples[0])
        assertResults(sentences = 2, words = 5, characters = 36, numbers = 1)
        assertHistoryEntry(index = 0, text = "Antrasis sakinys! Čia yra 2 žodžiai.", sentences = 2, words = 5, characters = 36, numbers = 1)
        assertHistoryEntry(index = 1, text = "Pirmas tekstas 100", sentences = 1, words = 2, characters = 18, numbers = 1)
        assertHistorySize(2)
        generateSample()
        assertInput(samples[1])
        assertResults(sentences = 2, words = 5, characters = 36, numbers = 1)
        assertHistorySize(2)
    }

    @Test
    fun invalidInputAfterCalculation_preservesExistingResultsAndHistory() {
        setInput("Labas rytas")
        calculate()
        setInput("    ")
        calculate()
        assertError()
        assertResults(sentences = 1, words = 2, characters = 11, numbers = 0)
        assertHistoryEntry(index = 0, text = "Labas rytas", sentences = 1, words = 2, characters = 11, numbers = 0)
        assertHistorySize(1)

        generateSample()
        assertInput(compose.activity.resources.getStringArray(R.array.test_samples)[0])
        assertNoError()
        assertResults(sentences = 1, words = 2, characters = 11, numbers = 0)
        assertHistoryEntry(index = 0, text = "Labas rytas", sentences = 1, words = 2, characters = 11, numbers = 0)
        assertHistorySize(1)
    }

    @Test
    fun repeatedIdenticalCalculations_prependsNewHistoryEntryWithCorrectOrdering() {
        setInput("Labas rytas")
        calculate()
        setInput("Labas 42!")
        calculate()
        calculate()
        assertResults(sentences = 1, words = 1, characters = 9, numbers = 1)
        assertHistoryEntry(index = 0, text = "Labas 42!", sentences = 1, words = 1, characters = 9, numbers = 1)
        assertHistoryEntry(index = 1, text = "Labas 42!", sentences = 1, words = 1, characters = 9, numbers = 1)
        assertHistoryEntry(index = 2, text = "Labas rytas", sentences = 1, words = 2, characters = 11, numbers = 0)
        assertHistorySize(3)
    }

    @Test
    @Config(qualifiers = "w891dp-h411dp-land")
    fun landscapeLayout_rendersRowButtons_andPerformsValidCalculationAndSampleGeneration() {
        // Exercise the horizontal (Row) ActionButtons branch and landscape text field lines
        val text = "COVID-19 pandemijos metu temperatūra nukrito iki -5,5 laipsnių!"
        setInput(text)
        calculate()
        assertNoError()
        // Hand-checked independent constants: 1 sentence, 7 words, 63 characters, 1 number (-5,5)
        assertResults(sentences = 1, words = 7, characters = 63, numbers = 1)
        assertHistoryEntry(index = 0, text = text, sentences = 1, words = 7, characters = 63, numbers = 1)
        assertHistorySize(1)

        generateSample()
        assertInput(compose.activity.resources.getStringArray(R.array.test_samples)[0])
        assertNoError()
    }
}
