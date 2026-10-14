package com.example.wordcounter.domain

/**
 * Teksto analizatorius, skaičiuojantis sakinius, žodžius, visus rašybos ženklus ir skaičius.
 *
 * Taisyklės:
 * - Rašybos ženklai: visų simbolių kiekis (`text.length`), įskaitant tarpus, skyrybos ženklus ir eilučių lūžius.
 * - Skaičiai: sveikieji, neigiami ir dešimtainiai su tašku arba kableliu (pvz. 2026, -5, 3.14, 2,5).
 * - Žodžiai: leksiniai vienetai, turintys bent vieną raidę (įskaitant lietuviškas raides ąčęėįšųūž).
 *   Junginiai su skaitmenimis ar brūkšneliais (pvz., COVID-19, 3D, A4, 1-asis) yra vienas žodis,
 *   jų skaitmenys atskirai prie skaičių neįskaitomi.
 * - Sakiniai: atskiriami pagal tašką, šauktuką, klaustuką ir jų sekas (..., ?!, !!!).
 *   Dešimtainio skaičiaus taškas neskaido sakinio. Paskutinė teksto dalis be galutinio skyrybos ženklo
 *   skaičiuojama kaip sakinys, jei joje yra bent vienas žodis ar skaičius.
 * - Apribojimas: sudėtingos santrumpos (pvz. t.y., dr., pan.) nėra specialiai analizuojamos.
 */
object TextStatisticsCalculator {

    // Regex skaičiams: galimas minuso ženklas priekyje, skaitmenys, dešimtainė dalis su . arba ,
    private val NUMBER_REGEX = Regex("^-?\\d+([.,]\\d+)?$")

    // Sakinio pabaigos ženklai (. ! ? ir jų sekos), atmetant tašką tarp dviejų skaitmenų (pvz. 3.14)
    private val SENTENCE_SPLIT_REGEX = Regex("(?<!\\d)[.!?]+|[.!?]+(?!\\d)")

    // Skyrybos ir kiti apgaubiantys ženklai, pašalinami nuo tokeno kraštų
    private val STRIP_CHARS = setOf(
        '"', '\'', '„', '“', '”', '‘', '’', '«', '»',
        '(', ')', '[', ']', '{', '}', '<', '>',
        ',', ':', ';', '!', '?', '.'
    )

    /**
     * Apskaičiuoja teksto statistiką.
     *
     * @param text Analizuojamas tekstas (prieš skaičiavimą neapkerpamas).
     * @return [TextStatistics] su 4 parametrų reikšmėmis.
     */
    fun calculate(text: String): TextStatistics {
        val characterCount = text.length

        // Jei tekstas tuščias arba jį sudaro tik tarpai / eilučių lūžiai
        if (text.isBlank()) {
            return TextStatistics(
                sentenceCount = 0,
                wordCount = 0,
                characterCount = characterCount,
                numberCount = 0
            )
        }

        // Skaidome į sakinių atkarpas pagal sakinių ribas
        val segments = SENTENCE_SPLIT_REGEX.split(text)

        var sentenceCount = 0
        var totalWords = 0
        var totalNumbers = 0

        for (segment in segments) {
            val rawTokens = segment.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            var segmentWords = 0
            var segmentNumbers = 0

            for (rawToken in rawTokens) {
                val cleaned = cleanToken(rawToken)
                if (cleaned.isNotEmpty()) {
                    if (cleaned.any { it.isLetter() }) {
                        // Jei turi bent vieną raidę (įskaitant COVID-19, 3D, A4, 1-asis) - tai žodis
                        segmentWords++
                    } else if (NUMBER_REGEX.matches(cleaned)) {
                        // Jei raidžių neturi ir atitinka skaičiaus šabloną - tai skaičius
                        segmentNumbers++
                    }
                }
            }

            // Atkarpa yra prasmingas sakinys, jei joje yra bent vienas žodis arba skaičius
            if (segmentWords > 0 || segmentNumbers > 0) {
                sentenceCount++
                totalWords += segmentWords
                totalNumbers += segmentNumbers
            }
        }

        return TextStatistics(
            sentenceCount = sentenceCount,
            wordCount = totalWords,
            characterCount = characterCount,
            numberCount = totalNumbers
        )
    }

    /**
     * Pašalina apgaubiančius skyrybos ženklus nuo leksinio tokeno kraštų,
     * išsaugant minuso ženklą neigiamiems skaičiams (pvz. "-5").
     */
    private fun cleanToken(raw: String): String {
        var start = 0
        var end = raw.length

        // Šaliname nuo pradžios
        while (start < end) {
            val c = raw[start]
            if (c in STRIP_CHARS) {
                start++
            } else if (c == '-' && (start + 1 >= end || !raw[start + 1].isDigit())) {
                // Brūkšnelį pradžioje šaliname tik tada, kai po jo neina skaitmuo (t.y. ne neigiamas skaičius)
                start++
            } else {
                break
            }
        }

        // Šaliname nuo pabaigos
        while (end > start) {
            val c = raw[end - 1]
            if (c in STRIP_CHARS || c == '-') {
                end--
            } else {
                break
            }
        }

        return if (start < end) raw.substring(start, end) else ""
    }
}
