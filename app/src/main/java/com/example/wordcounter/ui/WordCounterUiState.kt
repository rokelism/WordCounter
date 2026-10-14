package com.example.wordcounter.ui

import com.example.wordcounter.domain.TextStatistics
import java.util.UUID

/**
 * Vienas atlikto skaičiavimo istorijos įrašas.
 *
 * @property id Unikalus įrašo identifikatorius.
 * @property text Visas vartotojo įvestas tekstas (be apkarpymo).
 * @property stats Apskaičiuoti teksto statistikos rodikliai.
 */
data class CalculationHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val stats: TextStatistics
)

/**
 * „WordCounter“ ekrano būsenos modelis.
 *
 * @property inputText Vartotojo įvesties laukelyje esantis tekstas.
 * @property isInputError Ar įvestis atmesta dėl tuščio lauko klaidos.
 * @property currentStats Paskutinio sėkmingo skaičiavimo rezultatai.
 * @property history Atliktų skaičiavimų istorija (naujausias įrašas viršuje).
 * @property sampleIndex Sekančio testinio pavyzdžio indeksas.
 */
data class WordCounterUiState(
    val inputText: String = "",
    val isInputError: Boolean = false,
    val currentStats: TextStatistics? = null,
    val history: List<CalculationHistoryItem> = emptyList(),
    val sampleIndex: Int = 0
)
