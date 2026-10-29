package com.example.wordcounter

import com.example.wordcounter.ui.WordCounterViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * [WordCounterViewModel] būsenos valdymo ir įvesties apdorojimo vienetų testai.
 */
class WordCounterViewModelTest {

    private lateinit var viewModel: WordCounterViewModel

    @Before
    fun setUp() {
        viewModel = WordCounterViewModel()
    }

    @Test
    fun repeatedCalculations_haveDistinctStableHistoryIds() {
        viewModel.onInputTextChanged("Tas pats tekstas")
        viewModel.onCalculateClicked()
        val firstId = viewModel.uiState.value.history.single().id

        viewModel.onCalculateClicked()
        val history = viewModel.uiState.value.history

        assertEquals(2, history.size)
        assertTrue(firstId.isNotBlank())
        assertTrue(history.first().id.isNotBlank())
        assertNotEquals(firstId, history.first().id)
        assertEquals(firstId, history.last().id)
    }

    @Test
    fun initialState_hasDefaultValues() {
        val state = viewModel.uiState.value

        assertEquals("", state.inputText)
        assertFalse(state.isInputError)
        assertNull(state.currentStats)
        assertTrue(state.history.isEmpty())
        assertEquals(0, state.sampleIndex)
    }

    @Test
    fun onInputTextChanged_updatesTextAndClearsError() {
        // Iššaukiame klaidą paspaudę skaičiavimą su tuščia įvestimi
        viewModel.onCalculateClicked()
        assertTrue(viewModel.uiState.value.isInputError)

        // Pradėjus vesti tekstą, klaida turi būti išvalyta, o tekstas atnaujintas
        viewModel.onInputTextChanged("Labas pasauli")

        val state = viewModel.uiState.value
        assertEquals("Labas pasauli", state.inputText)
        assertFalse(state.isInputError)
    }

    @Test
    fun onCalculateClicked_withEmptyInput_setsErrorAndDoesNotAddToHistory() {
        viewModel.onInputTextChanged("")
        viewModel.onCalculateClicked()

        val state = viewModel.uiState.value
        assertTrue(state.isInputError)
        assertNull(state.currentStats)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun onCalculateClicked_withWhitespaceInput_setsErrorAndDoesNotAddToHistory() {
        viewModel.onInputTextChanged("   \n\t  ")
        viewModel.onCalculateClicked()

        val state = viewModel.uiState.value
        assertTrue(state.isInputError)
        assertNull(state.currentStats)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun onCalculateClicked_withInvalidInput_preservesPreviousResultsAndHistory() {
        // 1. Sėkmingas skaičiavimas
        viewModel.onInputTextChanged("Labas rytas")
        viewModel.onCalculateClicked()

        val stateBeforeError = viewModel.uiState.value
        val previousStats = stateBeforeError.currentStats
        val previousHistory = stateBeforeError.history
        assertNotNull(previousStats)
        assertEquals(1, previousHistory.size)

        // 2. Vartotojas ištrina tekstą į tuščią ir vėl spaudžia „Skaičiuoti“
        viewModel.onInputTextChanged("")
        viewModel.onCalculateClicked()

        val stateAfterError = viewModel.uiState.value
        assertTrue(stateAfterError.isInputError)
        // Ankstesni rezultatai ir istorija neturi pakisti
        assertEquals(previousStats, stateAfterError.currentStats)
        assertEquals(previousHistory, stateAfterError.history)
        assertEquals(1, stateAfterError.history.size)
    }

    @Test
    fun onCalculateClicked_withValidText_calculatesStatsAndAddsToHistory() {
        val input = "Rugsėjo 1 prasidėjo karas. Liūdna"
        viewModel.onInputTextChanged(input)
        viewModel.onCalculateClicked()

        val state = viewModel.uiState.value
        assertFalse(state.isInputError)
        assertEquals(input, state.inputText)

        val stats = state.currentStats
        assertNotNull(stats)
        assertEquals(2, stats!!.sentenceCount)
        assertEquals(4, stats.wordCount)
        assertEquals(33, stats.characterCount)
        assertEquals(1, stats.numberCount)

        assertEquals(1, state.history.size)
        val historyItem = state.history.first()
        assertEquals(input, historyItem.text)
        assertEquals(stats, historyItem.stats)
    }

    @Test
    fun onCalculateClicked_maintainsHistoryOrder_newestFirst() {
        viewModel.onInputTextChanged("Pirmas įrašas")
        viewModel.onCalculateClicked()

        viewModel.onInputTextChanged("Antras įrašas")
        viewModel.onCalculateClicked()

        viewModel.onInputTextChanged("Trečias įrašas")
        viewModel.onCalculateClicked()

        val history = viewModel.uiState.value.history
        assertEquals(3, history.size)
        assertEquals("Trečias įrašas", history[0].text)
        assertEquals("Antras įrašas", history[1].text)
        assertEquals("Pirmas įrašas", history[2].text)
    }

    @Test
    fun onGenerateSampleClicked_cyclesSamplesAndDoesNotCalculateAutomatically() {
        val samples = listOf("Pavyzdys A", "Pavyzdys B", "Pavyzdys C")

        // 1-as paspaudimas: indeksas 0
        viewModel.onGenerateSampleClicked(samples)
        var state = viewModel.uiState.value
        assertEquals("Pavyzdys A", state.inputText)
        assertEquals(1, state.sampleIndex)
        assertNull("Generavimas neturi automatiškai atlikti skaičiavimo", state.currentStats)
        assertTrue("Generavimas neturi pildyti istorijos", state.history.isEmpty())

        // 2-as paspaudimas: indeksas 1
        viewModel.onGenerateSampleClicked(samples)
        state = viewModel.uiState.value
        assertEquals("Pavyzdys B", state.inputText)
        assertEquals(2, state.sampleIndex)
        assertNull(state.currentStats)
        assertTrue(state.history.isEmpty())

        // 3-ias paspaudimas: indeksas 2
        viewModel.onGenerateSampleClicked(samples)
        state = viewModel.uiState.value
        assertEquals("Pavyzdys C", state.inputText)
        assertEquals(3, state.sampleIndex)
        assertNull(state.currentStats)

        // 4-as paspaudimas: cikliškai grįžta į pradžią (3 % 3 = 0)
        viewModel.onGenerateSampleClicked(samples)
        state = viewModel.uiState.value
        assertEquals("Pavyzdys A", state.inputText)
        assertEquals(4, state.sampleIndex)
        assertNull(state.currentStats)
    }

    @Test
    fun onGenerateSampleClicked_clearsErrorFlag() {
        // Iššaukiame klaidą
        viewModel.onCalculateClicked()
        assertTrue(viewModel.uiState.value.isInputError)

        // Sugeneravus pavyzdį, klaida turi būti anuliuota
        viewModel.onGenerateSampleClicked(listOf("Naujas pavyzdys"))
        val state = viewModel.uiState.value
        assertEquals("Naujas pavyzdys", state.inputText)
        assertFalse(state.isInputError)
    }

    @Test
    fun onGenerateSampleClicked_withEmptyList_doesNothing() {
        viewModel.onInputTextChanged("Pradinis tekstas")
        viewModel.onGenerateSampleClicked(emptyList())

        val state = viewModel.uiState.value
        assertEquals("Pradinis tekstas", state.inputText)
        assertEquals(0, state.sampleIndex)
    }
}
