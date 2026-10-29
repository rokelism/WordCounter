package com.example.wordcounter

import com.example.wordcounter.domain.TextStatisticsCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * [TextStatisticsCalculator] automatiniai vienetų (Unit) testai.
 */
class TextStatisticsCalculatorTest {

    @Test
    fun numbersOnly_areCountedAsOneSentenceWithoutWords() {
        val input = "42 -7 3.14 2,5"
        val stats = TextStatisticsCalculator.calculate(input)

        assertEquals(1, stats.sentenceCount)
        assertEquals(0, stats.wordCount)
        assertEquals(4, stats.numberCount)
        assertEquals(input.length, stats.characterCount)
    }

    @Test
    fun surroundingQuotesBracketsAndHyphens_preserveWordsAndNumbers() {
        val input = "„Labas“ (42) [2,5] {žodis} --tekstas--"
        val stats = TextStatisticsCalculator.calculate(input)

        assertEquals(1, stats.sentenceCount)
        assertEquals(3, stats.wordCount)
        assertEquals(2, stats.numberCount)
        assertEquals(input.length, stats.characterCount)
    }

    @Test
    fun singleHyphenAndEmptyBrackets_doNotCreateWordsOrSentences() {
        for (input in listOf("-", "()", "[]", "{}", "---")) {
            val stats = TextStatisticsCalculator.calculate(input)

            assertEquals(input, 0, stats.sentenceCount)
            assertEquals(input, 0, stats.wordCount)
            assertEquals(input, 0, stats.numberCount)
            assertEquals(input, input.length, stats.characterCount)
        }
    }

    @Test
    fun unsupportedSymbols_areIgnoredWithoutLosingAdjacentValidTokens() {
        val input = "@ # % Labas 42"
        val stats = TextStatisticsCalculator.calculate(input)

        assertEquals(1, stats.sentenceCount)
        assertEquals(1, stats.wordCount)
        assertEquals(1, stats.numberCount)
        assertEquals(input.length, stats.characterCount)
    }

    @Test
    fun testExample1_LabasRytas() {
        val input = "Labas rytas"
        val stats = TextStatisticsCalculator.calculate(input)

        // Pavyzdys 1: Sakiniai = 1, Žodžiai = 2, Ženklai = 11, Skaičiai = 0
        assertEquals(1, stats.sentenceCount)
        assertEquals(2, stats.wordCount)
        assertEquals(11, stats.characterCount)
        assertEquals(0, stats.numberCount)
    }

    @Test
    fun testExample2_RugsejoKaras() {
        val input = "Rugsėjo 1 prasidėjo karas. Liūdna"
        val stats = TextStatisticsCalculator.calculate(input)

        // Pavyzdys 2: Sakiniai = 2, Žodžiai = 4, Ženklai = 33, Skaičiai = 1
        assertEquals(2, stats.sentenceCount)
        assertEquals(4, stats.wordCount)
        assertEquals(33, stats.characterCount)
        assertEquals(1, stats.numberCount)
    }

    @Test
    fun testEmptyAndWhitespaceInputs() {
        // Visiškai tuščias tekstas
        val emptyStats = TextStatisticsCalculator.calculate("")
        assertEquals(0, emptyStats.sentenceCount)
        assertEquals(0, emptyStats.wordCount)
        assertEquals(0, emptyStats.characterCount)
        assertEquals(0, emptyStats.numberCount)

        // 3 tarpai
        val spacesStats = TextStatisticsCalculator.calculate("   ")
        assertEquals(0, spacesStats.sentenceCount)
        assertEquals(0, spacesStats.wordCount)
        assertEquals(3, spacesStats.characterCount)
        assertEquals(0, spacesStats.numberCount)

        // Eilutės lūžis, tabuliacija ir 2 tarpai (iš viso 4 simboliai)
        val whitespaceStats = TextStatisticsCalculator.calculate("\n\t  ")
        assertEquals(0, whitespaceStats.sentenceCount)
        assertEquals(0, whitespaceStats.wordCount)
        assertEquals(4, whitespaceStats.characterCount)
        assertEquals(0, whitespaceStats.numberCount)
    }

    @Test
    fun testIntegersNegativesAndDecimals() {
        // 2026, -5, 3.14 ir 2,5 yra 4 skaičiai, o ne žodžiai
        val input = "2026, -5, 3.14 ir 2,5 yra skaičiai."
        val stats = TextStatisticsCalculator.calculate(input)

        assertEquals(1, stats.sentenceCount)
        assertEquals(3, stats.wordCount) // "ir", "yra", "skaičiai"
        assertEquals(4, stats.numberCount) // 2026, -5, 3.14, 2,5
        assertEquals(input.length, stats.characterCount)
    }

    @Test
    fun testHybridWordsDoNotCountAsNumbers() {
        // COVID-19, 3D, A4, 1-asis laikomi po vieną žodį ir neprideda skaičių
        val input = "COVID-19 pandemija, 3D spausdinimas, A4 lapas ir 1-asis etapas."
        val stats = TextStatisticsCalculator.calculate(input)

        assertEquals(1, stats.sentenceCount)
        // Žodžiai: COVID-19, pandemija, 3D, spausdinimas, A4, lapas, ir, 1-asis, etapas = 9 žodžiai
        assertEquals(9, stats.wordCount)
        assertEquals(0, stats.numberCount)
        assertEquals(input.length, stats.characterCount)
    }

    @Test
    fun testNegativeDecimalWithHybridWord() {
        val input = "COVID-19 pandemijos metu temperatūra nukrito iki -5,5 laipsnių!"
        val stats = TextStatisticsCalculator.calculate(input)

        assertEquals(1, stats.sentenceCount)
        // Žodžiai: COVID-19, pandemijos, metu, temperatūra, nukrito, iki, laipsnių = 7 žodžiai
        assertEquals(7, stats.wordCount)
        assertEquals(1, stats.numberCount) // -5,5
        assertEquals(input.length, stats.characterCount)
    }

    @Test
    fun testDecimalDotDoesNotSplitSentence() {
        val input = "Prekė kainuoja 3.14 euro. Ar tai pigu?!"
        val stats = TextStatisticsCalculator.calculate(input)

        // 3.14 taškas neturi skaidyti sakinio. Sakiniai: "Prekė kainuoja 3.14 euro." ir "Ar tai pigu?!"
        assertEquals(2, stats.sentenceCount)
        assertEquals(6, stats.wordCount) // Prekė, kainuoja, euro, Ar, tai, pigu
        assertEquals(1, stats.numberCount) // 3.14
        assertEquals(input.length, stats.characterCount)
    }

    @Test
    fun testLithuanianCharacters() {
        val input = "Ąžuolas, šaka, įlanka, ėglis, ūkas, žąsis."
        val stats = TextStatisticsCalculator.calculate(input)

        assertEquals(1, stats.sentenceCount)
        assertEquals(6, stats.wordCount)
        assertEquals(0, stats.numberCount)
        assertEquals(input.length, stats.characterCount)
    }

    @Test
    fun testMultiplePunctuationMarks() {
        val input = "Labas... Kaip sekasi?! Puikiai!!!"
        val stats = TextStatisticsCalculator.calculate(input)

        // Sakiniai: "Labas", "Kaip sekasi", "Puikiai"
        assertEquals(3, stats.sentenceCount)
        assertEquals(4, stats.wordCount) // Labas, Kaip, sekasi, Puikiai
        assertEquals(0, stats.numberCount)
        assertEquals(input.length, stats.characterCount)
    }

    @Test
    fun testPunctuationOnlyInput() {
        val input = "... ?! ---"
        val stats = TextStatisticsCalculator.calculate(input)

        // Vien skyrybos ženklai nėra sakiniai, žodžiai ar skaičiai
        assertEquals(0, stats.sentenceCount)
        assertEquals(0, stats.wordCount)
        assertEquals(0, stats.numberCount)
        assertEquals(input.length, stats.characterCount)
    }
}
