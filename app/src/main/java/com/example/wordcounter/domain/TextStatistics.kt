package com.example.wordcounter.domain

/**
 * Teksto analizės statistikos rezultatai.
 *
 * @property sentenceCount Apskaičiuotų sakinių skaičius.
 * @property wordCount Apskaičiuotų žodžių skaičius.
 * @property characterCount Visi įvesties simboliai (įskaitant tarpus, skyrybos ženklus ir eilučių lūžius).
 * @property numberCount Apskaičiuotų skaičių kiekis (sveikieji, neigiami, dešimtainiai).
 */
data class TextStatistics(
    val sentenceCount: Int,
    val wordCount: Int,
    val characterCount: Int,
    val numberCount: Int
)
