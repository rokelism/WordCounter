package com.example.wordcounter

import android.app.KeyguardManager
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.text.AnnotatedString
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordcounter.ui.WordCounterScreenErrorPreview
import com.example.wordcounter.ui.WordCounterScreenLandscapePreview
import com.example.wordcounter.ui.WordCounterScreenPortraitPreview
import com.example.wordcounter.ui.WordCounterTestTags
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Papildomi Compose peržiūrų (preview) ir atvaizdavimo instrumentiniai testai.
 *
 * Testuoja realias programėlės peržiūrų funkcijas [WordCounterScreenPortraitPreview],
 * [WordCounterScreenLandscapePreview] ir [WordCounterScreenErrorPreview], padengiant
 * jų kompozicijos bei atgalinio ryšio (no-op callback) lambdas.
 *
 * Ekrano orientacija konfigūruojama kaip imituota išdėstymo konfigūracija
 * (simulated layout configuration) per [CompositionLocalProvider] ir [LocalConfiguration],
 * o ne fiziniu įrenginio pasukimu.
 */
@RunWith(AndroidJUnit4::class)
class WordCounterPreviewRenderTest {

    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    @Suppress("DEPRECATION")
    private fun keepTestWindowVisible(activity: ComponentActivity) {
        // Testavimo lango vėliavėlės emuliatoriaus matomumui užtikrinti
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
        // Neužlaikome laukimo tuščiam ComponentActivity prieš setContent
    }

    private fun renderPreviewWithConfiguration(orientation: Int, content: @Composable () -> Unit) {
        val configuration = Configuration(compose.activity.resources.configuration).apply {
            this.orientation = orientation
        }
        compose.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                content()
            }
        }
        compose.waitForIdle()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasTestTag(WordCounterTestTags.LAZY_COLUMN))
                .fetchSemanticsNodes(atLeastOneRootRequired = false).isNotEmpty()
        }
    }

    private fun string(id: Int) = compose.activity.getString(id)
    private fun historyTag(index: Int) = "${WordCounterTestTags.HISTORY_CARD}:$index"
    private fun list() = compose.onNodeWithTag(WordCounterTestTags.LAZY_COLUMN)

    private fun show(matcher: SemanticsMatcher, useUnmergedTree: Boolean = true) {
        Log.d("WordCounterPreviewTest", "Show: ${matcher.description}")
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
        val previousOffset = node.config[SemanticsProperties.VerticalScrollAxisRange].value()
        list().performTouchInput {
            if (down) swipeUp() else swipeDown()
        }
        compose.waitForIdle()
        val currentOffset = list().fetchSemanticsNode()
            .config[SemanticsProperties.VerticalScrollAxisRange].value()
        return kotlin.math.abs(currentOffset - previousOffset) >= 0.01f
    }

    private fun assertAbsentFromList(matcher: SemanticsMatcher) {
        Log.d("WordCounterPreviewTest", "Assert absent: ${matcher.description}")
        scrollToStart()
        repeat(100) {
            compose.onNode(matcher, useUnmergedTree = true).assertDoesNotExist()
            if (!scrollOneViewport()) {
                compose.onNode(matcher, useUnmergedTree = true).assertDoesNotExist()
                return
            }
        }
        throw AssertionError("List search exceeded 100 viewports; absence was not established")
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

    // =========================================================================
    // 1. Portreto peržiūros testas (WordCounterScreenPortraitPreview)
    // =========================================================================

    @Test
    fun portraitPreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState() {
        // Imituota išdėstymo konfigūracija: portreto orientacija
        renderPreviewWithConfiguration(Configuration.ORIENTATION_PORTRAIT) {
            WordCounterScreenPortraitPreview()
        }

        // Fiksuoto snapshot rodiklių reikšmės pagal esamą peržiūros apibrėžimą
        val fixtureInput = "Labas rytas! Šiandien yra 2026 metai."
        assertInput(fixtureInput)
        assertNoError()
        // Peržiūros apibrėžime simbolių skaičius pažymėtas pažodine reikšme 37
        assertResults(sentences = 2, words = 5, characters = 37, numbers = 1)

        val historyText = "COVID-19 pandemijos metu temperatūra nukrito iki -5,5 laipsnių!"
        assertHistoryEntry(index = 0, text = historyText, sentences = 1, words = 7, characters = 63, numbers = 1)
        assertHistorySize(1)

        // Iškviečiamas realus onInputTextChanged atgalinis ryšys per performTextReplacement
        invokeInputTextChange("Pakeistas tekstas")
        // Kadangi peržiūroje callback yra tuščias (no-op), įvestis lieka nepakitusi
        assertInput(fixtureInput)

        // Iškviečiami abu mygtukai (onCalculateClicked ir onGenerateSampleClicked callback'ams padengti)
        calculate()
        generateSample()

        // Patikrinama, kad po atgalinių ryšių peržiūros fiksuoti duomenys išlieka nepakitę
        assertInput(fixtureInput)
        assertNoError()
        assertResults(sentences = 2, words = 5, characters = 37, numbers = 1)
        assertHistoryEntry(index = 0, text = historyText, sentences = 1, words = 7, characters = 63, numbers = 1)
        assertHistorySize(1)
    }

    // =========================================================================
    // 2. Gulsčio ekrano peržiūros testas (WordCounterScreenLandscapePreview)
    // =========================================================================

    @Test
    fun landscapePreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState() {
        // Imituota išdėstymo konfigūracija: horizontali (landscape) orientacija
        renderPreviewWithConfiguration(Configuration.ORIENTATION_LANDSCAPE) {
            WordCounterScreenLandscapePreview()
        }

        // Fiksuoto snapshot rodiklių reikšmės pagal esamą peržiūros apibrėžimą
        val fixtureInput = "Labas rytas! Šiandien yra 2026 metai."
        assertInput(fixtureInput)
        assertNoError()
        assertResults(sentences = 2, words = 5, characters = 37, numbers = 1)

        val historyText = "Prekė A4 kainuoja 3.14 euro..."
        assertHistoryEntry(index = 0, text = historyText, sentences = 1, words = 4, characters = 31, numbers = 1)
        assertHistorySize(1)

        // Iškviečiamas realus onInputTextChanged atgalinis ryšys per performTextReplacement
        invokeInputTextChange("Pakeistas tekstas")
        assertInput(fixtureInput)

        // Iškviečiami abu mygtukai gulsčiame išdėstyme
        calculate()
        generateSample()

        // Patikrinama, kad po atgalinių ryšių peržiūros fiksuoti duomenys išlieka nepakitę
        assertInput(fixtureInput)
        assertNoError()
        assertResults(sentences = 2, words = 5, characters = 37, numbers = 1)
        assertHistoryEntry(index = 0, text = historyText, sentences = 1, words = 4, characters = 31, numbers = 1)
        assertHistorySize(1)
    }

    // =========================================================================
    // 3. Klaidos būsenos peržiūros testas (WordCounterScreenErrorPreview)
    // =========================================================================

    @Test
    fun errorPreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState() {
        // Imituota išdėstymo konfigūracija: portreto orientacija
        renderPreviewWithConfiguration(Configuration.ORIENTATION_PORTRAIT) {
            WordCounterScreenErrorPreview()
        }

        // Klaidos peržiūros fiksuotos būsenos patikrinimai
        assertInput("")
        assertError()
        assertAbsentFromList(hasTestTag(WordCounterTestTags.RESULTS_CARD))
        assertHistorySize(0)

        // Iškviečiamas realus onInputTextChanged atgalinis ryšys per performTextReplacement
        invokeInputTextChange("Pakeistas tekstas")
        assertInput("")

        // Iškviečiami abu mygtukai
        calculate()
        generateSample()

        // Patikrinama, kad po atgalinių ryšių klaidos būsena ir tuščia istorija išlieka
        assertInput("")
        assertError()
        assertAbsentFromList(hasTestTag(WordCounterTestTags.RESULTS_CARD))
        assertHistorySize(0)
    }
}
