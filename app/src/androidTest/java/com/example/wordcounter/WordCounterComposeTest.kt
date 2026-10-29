package com.example.wordcounter

import android.app.KeyguardManager
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordcounter.ui.WordCounterTestTags
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generated in a separate Antigravity session for assignment 6; reviewed and
 * refined by Codex for the installed Compose API and lazy-list semantics.
 * Every test launches the real MainActivity with a fresh ViewModel.
 * Expected statistics are independent constants, never production-calculator output.
 */
@RunWith(AndroidJUnit4::class)
class WordCounterComposeTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Suppress("DEPRECATION")
    private fun keepTestWindowVisible(activity: MainActivity) {
        // Test-only flags: the existing emulator may start asleep or on its swipe lock.
        // These apply only to this Activity window and disappear at test teardown.
        activity.window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )
    }

    @Before
    fun prepareVisibleActivity() {
        compose.activityRule.scenario.onActivity { activity ->
            keepTestWindowVisible(activity)
            val keyguard = activity.getSystemService(KeyguardManager::class.java)
            check(!keyguard.isKeyguardSecure) { "Use an emulator without a PIN/password lock for UI tests" }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keyguard.requestDismissKeyguard(activity, null)
            }
        }
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasTestTag(WordCounterTestTags.LAZY_COLUMN))
                .fetchSemanticsNodes(atLeastOneRootRequired = false).isNotEmpty()
        }
    }

    private fun string(id: Int) = compose.activity.getString(id)
    private fun historyTag(index: Int) = "${WordCounterTestTags.HISTORY_CARD}:$index"
    private fun list() = compose.onNodeWithTag(WordCounterTestTags.LAZY_COLUMN)

    private fun show(matcher: SemanticsMatcher, useUnmergedTree: Boolean = true) {
        Log.d("WordCounterUiTest", "Show: ${matcher.description}")
        val target = compose.onNode(matcher, useUnmergedTree = useUnmergedTree)
        if (target.isDisplayed()) return
        scrollToStart()
        repeat(100) {
            if (target.isDisplayed()) return
            if (!scrollOneViewport()) {
                target.assertIsDisplayed() // Fail with the actual missing/hidden node.
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
        val previousOffset = node.config[SemanticsProperties.VerticalScrollAxisRange].value()
        list().performTouchInput {
            if (down) swipeUp() else swipeDown()
        }
        compose.waitForIdle()
        val currentOffset = list().fetchSemanticsNode()
            .config[SemanticsProperties.VerticalScrollAxisRange].value()
        return kotlin.math.abs(currentOffset - previousOffset) >= 0.01f
    }

    /** Search the WHOLE lazy list; an offscreen/uncomposed node is not proof of absence. */
    private fun assertAbsentFromList(matcher: SemanticsMatcher) {
        Log.d("WordCounterUiTest", "Assert absent: ${matcher.description}")
        scrollToStart()
        // performScrollToNode(missingMatcher) can loop indefinitely at the bottom of
        // this emulator's lazy list. Inspect each viewport using normal swipes, stopping on actual lack
        // of scroll progress rather than the estimated maximum scroll range.
        repeat(100) {
            compose.onNode(matcher, useUnmergedTree = true).assertDoesNotExist()
            if (!scrollOneViewport()) {
                compose.onNode(matcher, useUnmergedTree = true).assertDoesNotExist()
                return
            }
        }
        throw AssertionError("List search exceeded 100 viewports; absence was not established")
    }

    private fun setInput(text: String) {
        show(hasSetTextAction())
        compose.onNode(hasSetTextAction()).performTextReplacement(text)
        compose.activityRule.scenario.onActivity { activity ->
            activity.currentFocus?.clearFocus()
            WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                .hide(WindowInsetsCompat.Type.ime())
        }
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

    /** Order: sentences, words, characters (including spaces/punctuation), numbers. */
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
        // Scope duplicate text and repeated metric values to this particular history card.
        show(hasText(text) and inCard)
        labels().zip(listOf(sentences, words, characters, numbers)).forEach { (label, value) ->
            show(hasText("$label: $value") and inCard)
        }
    }

    private fun assertHistorySize(size: Int) {
        if (size == 0) {
            show(hasText(string(R.string.history_empty)))
        } else {
            // Index tags reflect actual UI order; stable UUID keys remain unchanged in the app.
            repeat(size) { show(hasTestTag(historyTag(it))) }
            assertAbsentFromList(hasText(string(R.string.history_empty)))
        }
        // Exhaustive scrolling proves no next entry exists, even if it would be offscreen.
        assertAbsentFromList(hasTestTag(historyTag(size)))
    }

    @Test
    fun validCalculation_displaysAllFourStatisticsAndHistoryEntryWithHandCheckedValues() {
        // 2 sentences; 7 lexical words; 2026 and -5,5 are the 2 numbers; 54 characters.
        val text = "Labas rytas! Šiandien yra 2026 metai, kaina -5,5 euro."
        setInput(text)
        calculate()
        assertNoError()
        assertResults(2, 7, 54, 2)
        assertHistoryEntry(0, text, 2, 7, 54, 2)
        assertHistorySize(1)
    }

    @Test
    fun emptyAndWhitespaceValidation_showsErrorWithoutHistory_andRecoversUponValidInput() {
        // Separate attempts within this scenario prove both empty and mixed-whitespace rejection.
        for (text in listOf("", "   \t  \n  ")) {
            setInput(text)
            calculate()
            assertError()
            assertAbsentFromList(hasTestTag(WordCounterTestTags.RESULTS_CARD))
            assertHistorySize(0)
        }
        setInput("Labas rytas")
        assertNoError() // Editing clears validation, before calculation.
        assertAbsentFromList(hasTestTag(WordCounterTestTags.RESULTS_CARD))
        calculate()
        assertResults(1, 2, 11, 0)
        assertHistoryEntry(0, "Labas rytas", 1, 2, 11, 0)
        assertHistorySize(1)
    }

    @Test
    fun generateSample_cyclesThroughSamplesWithWraparound_clearsValidationWithoutAutoCalculate() {
        val samples = compose.activity.resources.getStringArray(R.array.test_samples)
        assertTrue("The real resource array must contain multiple samples", samples.size > 1)
        calculate()
        assertError()
        // Includes every resource sample plus wraparound to the first.
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
        assertResults(2, 5, 36, 1)
        assertHistoryEntry(0, "Antrasis sakinys! Čia yra 2 žodžiai.", 2, 5, 36, 1)
        assertHistoryEntry(1, "Pirmas tekstas 100", 1, 2, 18, 1)
        assertHistorySize(2)
        generateSample() // Uncalculated draft differs from the last calculated text.
        assertInput(samples[0])

        val oldActivity = compose.activity
        compose.activityRule.scenario.recreate() // Real Android lifecycle recreation, not setContent.
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { newActivity ->
            keepTestWindowVisible(newActivity)
            assertNotSame("Activity recreation must create a different object", oldActivity, newActivity)
            assertTrue("The original Activity must have been destroyed", oldActivity.isDestroyed)
        }

        assertInput(samples[0])
        assertResults(2, 5, 36, 1)
        assertHistoryEntry(0, "Antrasis sakinys! Čia yra 2 žodžiai.", 2, 5, 36, 1)
        assertHistoryEntry(1, "Pirmas tekstas 100", 1, 2, 18, 1)
        assertHistorySize(2)
        generateSample()
        assertInput(samples[1]) // Sample cycle position also survives recreation.
        assertResults(2, 5, 36, 1)
        assertHistorySize(2)
    }

    @Test
    fun invalidInputAfterCalculation_preservesExistingResultsAndHistory() {
        setInput("Labas rytas")
        calculate()
        setInput("    ")
        calculate()
        assertError()
        assertResults(1, 2, 11, 0)
        assertHistoryEntry(0, "Labas rytas", 1, 2, 11, 0)
        assertHistorySize(1)

        // Sample generation also clears an error while retaining existing calculated data.
        generateSample()
        assertInput(compose.activity.resources.getStringArray(R.array.test_samples)[0])
        assertNoError()
        assertResults(1, 2, 11, 0)
        assertHistoryEntry(0, "Labas rytas", 1, 2, 11, 0)
        assertHistorySize(1)
    }

    @Test
    fun repeatedIdenticalCalculations_prependsNewHistoryEntryWithCorrectOrdering() {
        setInput("Labas rytas")
        calculate()
        setInput("Labas 42!")
        calculate()
        calculate()
        assertResults(1, 1, 9, 1)
        // Two distinct visible cards for the identical text, then the older different entry.
        assertHistoryEntry(0, "Labas 42!", 1, 1, 9, 1)
        assertHistoryEntry(1, "Labas 42!", 1, 1, 9, 1)
        assertHistoryEntry(2, "Labas rytas", 1, 2, 11, 0)
        assertHistorySize(3)
    }
}
