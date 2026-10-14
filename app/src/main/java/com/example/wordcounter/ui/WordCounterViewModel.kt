package com.example.wordcounter.ui

import androidx.lifecycle.ViewModel
import com.example.wordcounter.domain.TextStatisticsCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * „WordCounter“ ekrano būsenos valdiklis (ViewModel).
 *
 * Saugo duomenis atmintyje, išlaiko juos pasukus ekraną ir atlieka įvesties validaciją.
 */
class WordCounterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WordCounterUiState())
    val uiState: StateFlow<WordCounterUiState> = _uiState.asStateFlow()

    /**
     * Vartotojui keičiant įvesties tekstą, atnaujina būseną ir išvalo klaidos žymę.
     */
    fun onInputTextChanged(newText: String) {
        _uiState.update { current ->
            current.copy(
                inputText = newText,
                isInputError = false
            )
        }
    }

    /**
     * Vartotojui paspaudus „Skaičiuoti“:
     * - Jei tekstas tuščias ar vien tarpai: nustato klaidą, nekeičia rezultatų ir nepildo istorijos.
     * - Jei tekstas tinkamas: apskaičiuoja statistiką ir prideda įrašą į istorijos viršų.
     */
    fun onCalculateClicked() {
        val text = _uiState.value.inputText
        if (text.isBlank()) {
            _uiState.update { current ->
                current.copy(isInputError = true)
            }
            return
        }

        val stats = TextStatisticsCalculator.calculate(text)
        val historyItem = CalculationHistoryItem(text = text, stats = stats)

        _uiState.update { current ->
            current.copy(
                isInputError = false,
                currentStats = stats,
                history = listOf(historyItem) + current.history
            )
        }
    }

    /**
     * Vartotojui paspaudus „Generuoti pavyzdį“:
     * Įrašo pavyzdinį tekstą į laukelį iš duoto sąrašo.
     * Skaičiavimo automatiškai neatlieka (laukiama mygtuko „Skaičiuoti“ paspaudimo).
     */
    fun onGenerateSampleClicked(samples: List<String>) {
        if (samples.isEmpty()) return

        val currentIndex = _uiState.value.sampleIndex
        val sample = samples[currentIndex % samples.size]

        _uiState.update { current ->
            current.copy(
                inputText = sample,
                isInputError = false,
                sampleIndex = currentIndex + 1
            )
        }
    }
}
