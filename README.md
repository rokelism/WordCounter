# WordCounter – Teksto analizės Android programėlė

„WordCounter“ – tai Android operacinei sistemai skirta programėlė, sukurta su **Kotlin** ir šiuolaikine deklaratyvia **Jetpack Compose** (Material 3) naudotojo sąsaja. Programėlė atlieka detalią vartotojo įvesto teksto analizę ir realiuoju laiku suskaičiuoja keturis pagrindinius rodiklius: sakinius, žodžius, visus rašybos ženklus bei skaičius.

Ši dokumentacija parengta pagal praktikos darbo reikalavimus ir aprašo faktiškai įgyvendintą programėlės funkcionalumą, skaičiavimo taisykles, architektūrą bei automatinių testų vykdymą.

---

## Turinys

- [Programėlės paskirtis ir funkcijos](#programėlės-paskirtis-ir-funkcijos)
- [Projekto atidarymas, paruošimas ir paleidimas Android Studio](#projekto-atidarymas-paruošimas-ir-paleidimas-android-studio)
- [Naudojimas](#naudojimas)
  - [Teksto įvedimas ir skaičiavimas](#teksto-įvedimas-ir-skaičiavimas)
  - [Pavyzdžių generavimas](#pavyzdžių-generavimas)
  - [Tuščios įvesties klaida](#tuščios-įvesties-klaida)
  - [Skaičiavimų istorija](#skaičiavimų-istorija)
- [Tikslios skaičiavimo taisyklės ir žinomi apribojimai](#tikslios-skaičiavimo-taisyklės-ir-žinomi-apribojimai)
  - [Rašybos ženklai](#1-rašybos-ženklai-simboliai)
  - [Skaičiai](#2-skaičiai)
  - [Žodžiai](#3-žodžiai)
  - [Sakiniai](#4-sakiniai)
  - [Žinomi apribojimai](#5-žinomi-apribojimai)
- [Užduoties pavyzdžiai su rezultatais](#užduoties-pavyzdžiai-su-rezultatais)
- [Projekto struktūra ir atskira skaičiavimo klasė](#projekto-struktūra-ir-atskira-skaičiavimo-klasė)
- [Automatinių testų paleidimas Android Studio](#automatinių-testų-paleidimas-android-studio)
- [Patikrinimo būsena](#patikrinimo-būsena)

---

## Programėlės paskirtis ir funkcijos

Programėlės tikslas – suteikti vartotojui paprastą, patikimą ir aiškų įrankį teksto kiekybiniams parametrams nustatyti.

### Pagrindinės funkcijos:
1. **Keturių parametrų analizė**:
   - Sakinių skaičius;
   - Žodžių skaičius;
   - Visų rašybos ženklų (simbolių) skaičius;
   - Skaičių kiekis tekste.
2. **Įvesties validacija**: apsauga nuo tuščio teksto ar vien tarpų skaičiavimo su vaizdiniu klaidos indikatoriumi ir pranešimu.
3. **Testinių pavyzdžių generatorius**: mygtuko paspaudimu laukas užpildomas iš anksto paruoštais įvairaus pobūdžio tekstais (su datomis, skaičiais, santrumpomis, specialiaisiais terminais).
4. **Skaičiavimų istorija**: kiekvienas sėkmingas skaičiavimas išsaugomas sąraše ekrano apačioje (naujausias įrašas rodomas viršuje). Istorijos kortelėse atvaizduojamas visas įvestas tekstas be apkirpimo ir visi keturi apskaičiuoti rodikliai.
5. **Būsenos išsaugojimas**: architektūra naudoja `WordCounterViewModel`, todėl visi įvesti duomenys, rezultatai ir istorija išlieka pasikeitus ekrano konfigūracijai.

---

## Projekto atidarymas, paruošimas ir paleidimas Android Studio

### Reikalavimai aplinkai
- **Android Studio**: Ladybug / Meerkat arba naujesnė versija su Kotlin 2.x ir Jetpack Compose palaikymu.
- **Gradle vykdymo JDK (Gradle JDK)**:
  - Projektas naudoja **Android Gradle Plugin (AGP) 9.4.1** ir **Gradle 9.6.0** (iš `gradle-wrapper.properties`).
  - Šiems įrankiams paleisti reikalinga **JDK 17 arba JDK 21**.
  - **Rekomendacija**: naudoti „Android Studio“ integruotą Gradle JDK (*Embedded JDK* / *JetBrains Runtime* – pvz., versiją 17 arba 21), kurią galima nustatyti per:
    `Settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK`.
- **Java kodo ir baitkodo suderinamumas**:
  - `build.gradle.kts` nurodyti `sourceCompatibility = JavaVersion.VERSION_11` ir `targetCompatibility = JavaVersion.VERSION_11` nustato Java kodo ir sugeneruoto baitkodo suderinamumą (Java 11).
  - Šis nustatymas yra atskirtas tiek nuo Gradle kompiliavimo sistemos vykdymo JDK reikalavimų, tiek nuo minimalios Android versijos (`minSdk`) nustatymo.
- **Android SDK**:
  - `minSdk`: 24 (Android 7.0 Nougat – apibrėžia minimalią palaikomą Android operacinės sistemos versiją)
  - `compileSdk`: 37
  - `targetSdk`: 37

### Paleidimo žingsniai
1. **Atidaryti projektą**:
   - Paleiskite „Android Studio“.
   - Pasirinkite **File -> Open...** (arba pasveikinimo lange spustelėkite **Open**).
   - Nurodykite šakninį projekto aplanką `WordCounter` ir paspauskite **OK**.
2. **Gradle sinchronizacija**:
   - Atidarius projektą, Android Studio automatiškai pradės sinchronizaciją (`Gradle Sync`).
   - Jei sinchronizacija neprasideda automatiškai, įrankių juostoje paspauskite piktogramą **Sync Project with Gradle Files** (dramblio piktograma su rodykle).
   - Sulaukite, kol fone bus atsiųstos visos priklausomybės ir Gradle praneš: *BUILD SUCCESSFUL*.
3. **Įrenginio paruošimas**:
   - Viršutinėje įrankių juostoje pasirinkite **Android Virtual Device (AVD)** emuliatorių su API 24 ar naujesne versija (pvz., API 34).
   - *Pastaba*: programėlės rankinis patikrinimas šiame etape atliktas naudojant emuliatoriaus profilį **Small Desktop API 34**.
4. **Paleidimas**:
   - Viršutinėje įrankių juostoje paspauskite žalią **Run 'app'** mygtuką (trikampį) arba klaviatūros kombinaciją `Shift + F10`.
   - Programėlė bus sukompiliuota, įdiegta ir atidaryta emuliatoriuje.

---

## Naudojimas

### Teksto įvedimas ir skaičiavimas
1. Ekrano viršuje esančiame laukelyje **„Tekstas analizei“** įveskite arba įklijuokite norimą tekstą.
2. Paspauskite mygtuką **„Skaičiuoti“**.
3. Žemiau mygtukų pasirodys pakelta kortelė **„Dabartiniai rezultatai“**, kurioje aiškiai pateikiami 4 rodikliai:
   - **Sakiniai**
   - **Žodžiai**
   - **Rašybos ženklai**
   - **Skaičiai**
4. Naujas įrašas tuo pačiu metu automatiškai įtraukiamas į **„Skaičiavimų istorija“** skilties viršų.

### Pavyzdžių generavimas
- Šalia skaičiavimo mygtuko yra mygtukas **„Generuoti pavyzdį“**.
- Paspaudus šį mygtuką, į teksto laukelį paeiliui cikliškai įrašomas vienas iš šešių iš anksto paruoštų testinių pavyzdžių iš `strings.xml` išteklių:
  1. `Labas rytas`
  2. `Rugsėjo 1 prasidėjo karas. Liūdna`
  3. `COVID-19 pandemijos metu temperatūra nukrito iki -5,5 laipsnių!`
  4. `Prekė A4 kainuoja 3.14 euro... Ar tikrai?!`
  5. `1-asis etapas: atlikti 3D modeliavimą.`
  6. `Šiandien yra 2026 metai, kaina yra 2,5 euro.`
- **Svarbi pastaba**: pavyzdžio sugeneravimas **neatlieka** skaičiavimo automatiškai. Tekstas tiesiog įrašomas į laukelį, o skaičiavimui atlikti vartotojas turi paspausti mygtuką **„Skaičiuoti“**.
- Sugeneravus pavyzdį, anksčiau rodyta klaidos žymė (jei buvo) yra automatiškai anuliuojama.

### Tuščios įvesties klaida
- Jeigu vartotojas paspaudžia mygtuką **„Skaičiuoti“**, kai įvesties laukelis yra tuščias arba jį sudaro vien tarpai, tabuliacija ar eilučių lūžiai:
  - Laukelio rėmelis nusidažo klaidos (raudona) spalva.
  - Po laukeliu atsiranda pranešimas: **„Teksto laukas negali būti tuščias!“**.
  - Skaičiavimas neatliekamas.
  - Jei anksčiau jau buvo atliktas sėkmingas skaičiavimas, jo rezultatai ir sukaupta istorija **nėra prarandami** ir lieka ekrane.
- Vos tik vartotojas pradeda vesti tekstą arba paspaudžia „Generuoti pavyzdį“, klaidos pranešimas ir raudonas rėmelis iš karto išnyksta.

### Skaičiavimų istorija
- Po rezultatų kortele yra skiltis **„Skaičiavimų istorija“**.
- Kol neatliktas nė vienas skaičiavimas, rodomas pranešimas: *„Istorija tuščia. Įveskite tekstą ir paspauskite „Skaičiuoti“.“*.
- Po kiekvieno sėkmingo skaičiavimo sukuriamas naujas įrašas, kuris įterpiamas į sąrašo viršų (chronologinė tvarka nuo naujausio).
- Kiekvienoje istorijos kortelėje pateikiama:
  - **Pilnas įvestas tekstas** be trumpinimo ar apkirpimo;
  - Visi 4 skaičiavimo rodikliai, gauti to skaičiavimo metu.

---

## Tikslios skaičiavimo taisyklės ir žinomi apribojimai

Teksto analizę atlieka atskira verslo logikos klasė `TextStatisticsCalculator`. Joje įgyvendintos šios griežtos taisyklės:

### 1. Rašybos ženklai (simboliai)
- Skaičiuojamas tikslus visų teksto simbolių kiekis (`text.length`).
- Įskaičiuojama viskas: raidės, skaitmenys, tarpai, skyrybos ženklai, tabuliacijos simboliai (`\t`) ir eilučių lūžiai (`\n`).
- Tekstas prieš simbolių skaičiavimą **nėra apkarpomas** (*no trim*).

### 2. Skaičiai
- Skaičiumi laikomas atskiras leksinis vienetas, atitinkantis reguliarųjį reiškinį `^-?\d+([.,]\d+)?$` ir **neturintis jokių raidžių**.
- Palaikomi formatai:
  - Teigiami sveikieji skaičiai (pvz., `2026`, `1`);
  - Neigiami sveikieji skaičiai (pvz., `-5`);
  - Dešimtainiai skaičiai su tašku (pvz., `3.14`);
  - Dešimtainiai skaičiai su kableliu (pvz., `2,5`, `-5,5`).
- Jei skaičius yra apgaubtas skyrybos ženklais (pvz., skliausteliuose `(2026)` ar po jo eina kablelis `2,5,`), skyrybos ženklai nuo kraštų pašalinami ir skaičius atpažįstamas teisingai.

### 3. Žodžiai
- Žodžiu laikomas leksinis vienetas, turintis **bent vieną raidę** (`cleaned.any { it.isLetter() }`).
- Pilnai palaikomos lietuviškos raidės su diakritiniais ženklais (`ą`, `č`, `ę`, `ė`, `į`, `š`, `ų`, `ū`, `ž`, `Ą`, `Č`, `Ę`, `Ė`, `Į`, `Š`, `Ų`, `Ū`, `Ž`) bei visos standartinės Unicode raidės.
- **Hibridiniai žodžiai**: žodžiai, turintys skaitmenų ar brūkšnelių (pvz., `COVID-19`, `3D`, `A4`, `1-asis`), yra traktuojami kaip **vienas žodis**. Juose esantys skaitmenys **nėra** atskirai įskaitomi prie skaičių.

### 4. Sakiniai
- Tekstas dalijamas į atkarpas pagal sakinio pabaigos skyrybos ženklus: tašką `.`, šauktuką `!`, klaustuką `?` ir jų kombinacijas (`...`, `?!`, `!!!`).
- **Dešimtainio skaičiaus taisyklė**: taškas tarp dviejų skaitmenų (pvz., `3.14`) sakinio pabaiga nelaikomas ir teksto į du sakinius neskaido (naudojamas regex `(?<!\d)[.!?]+|[.!?]+(?!\d)`).
- **Prasmingumo taisyklė**: atkarpa pripažįstama sakiniu tik tada, jei joje yra **bent vienas žodis arba skaičius**. Vien skyrybos ženklai (pvz., `... ?! ---`) sakiniu nelaikomi.
- **Paskutinis sakinys**: jei tekstas baigiasi be skyrybos ženklo (pvz., `Labas rytas`), ši dalis teisingai pripažįstama sakiniu, nes joje yra žodžių.

### 5. Žinomi apribojimai
- **Santrumpos**: sudėtingos lietuviškos ar tarptautinės santrumpos su taškais (pvz., `t.y.`, `dr.`, `pan.`, `kt.`) nėra specialiai atpažįstamos žodyno lygiu. Taškas po tokios santrumpos algoritme veikia kaip sakinio pabaigos ženklas (išskyrus atvejus, kai jis yra tarp skaitmenų).

---

## Užduoties pavyzdžiai su rezultatais

Praktikos užduotyje pateikti du kontroliniai pavyzdžiai ir jų gauti skaičiavimo rezultatai:

### 1 pavyzdys
- **Įvesties tekstas:**
  ```text
  Labas rytas
  ```
- **Rezultatai:**
  - **Sakiniai:** `1`
  - **Žodžiai:** `2`
  - **Rašybos ženklai:** `11`
  - **Skaičiai:** `0`

### 2 pavyzdys
- **Įvesties tekstas:**
  ```text
  Rugsėjo 1 prasidėjo karas. Liūdna
  ```
- **Rezultatai:**
  - **Sakiniai:** `2` (Pirmas sakinys: *„Rugsėjo 1 prasidėjo karas“*, antras sakinys: *„Liūdna“*)
  - **Žodžiai:** `4` (*„Rugsėjo“*, *„prasidėjo“*, *„karas“*, *„Liūdna“*)
  - **Rašybos ženklai:** `33` (įskaitant tašką ir tarpus)
  - **Skaičiai:** `1` (*„1“*)

---

## Projekto struktūra ir atskira skaičiavimo klasė

Projektas organizuotas pagal švarios architektūros principus, atskiriant skaičiavimo verslo logiką, būsenos valdymą ir naudotojo sąsajos sluoksnį.

```text
app/src/
├── main/
│   ├── java/com/example/wordcounter/
│   │   ├── domain/                               # Verslo logikos (Domain) sluoksnis
│   │   │   ├── TextStatistics.kt                 # Rezultatų duomenų modelis (data class)
│   │   │   └── TextStatisticsCalculator.kt       # ATSKIRA SKAIČIAVIMO KLASĖ (singleton object)
│   │   ├── ui/                                   # Naudotojo sąsajos (UI) sluoksnis
│   │   │   ├── theme/                            # Compose temos, spalvų ir tipografijos failai
│   │   │   ├── WordCounterUiState.kt             # Ekrano būsenos ir istorijos įrašo modeliai
│   │   │   ├── WordCounterViewModel.kt           # Būsenos valdiklis (ViewModel)
│   │   │   └── WordCounterScreen.kt              # Jetpack Compose UI komponentai
│   │   └── MainActivity.kt                       # Pagrindinė Android veikla (Activity)
│   └── res/                                      # Ištekliai
│       └── values/strings.xml                    # Lokalizuoti tekstai ir testiniai pavyzdžiai
└── test/
    └── java/com/example/wordcounter/             # Automatiniai vienetų testai (Unit Tests)
        ├── TextStatisticsCalculatorTest.kt       # 10 skaičiavimo logikos testų
        └── WordCounterViewModelTest.kt           # 10 ViewModel būsenos valdymo testų
```

### Atskira skaičiavimo klasė: `TextStatisticsCalculator`
- Failas: `app/src/main/java/com/example/wordcounter/domain/TextStatisticsCalculator.kt`
- Ši klasė yra realizuota kaip grynas Kotlin `object` (vienetas/singletonas) ir neturi jokių priklausomybių nuo Android karkaso (`android.*`) ar naudotojo sąsajos.
- Jai perduodamas `String` tipo tekstas, o grąžinamas `TextStatistics` objektas su 4 laukais (`sentenceCount`, `wordCount`, `characterCount`, `numberCount`).
- Dėl visiško nepriklausomumo nuo Android SDK, skaičiavimo algoritmas gali būti testuojamas žaibiškai paprastais JVM vienetų testais be emuliatoriaus ar prietaisinio testavimo paleidimo.

---

## Automatinių testų paleidimas Android Studio

Projekte realizuoti 20 automatinių vienetų testų, padengiančių visas skaičiavimo taisykles, ribinius atvejus ir vartotojo sąsajos logiką.

### Testų paleidimo būdai:

#### 1 būdas: Paleidimas per projekto medį (rekomenduojamas)
1. Kairėje esančiame **Project** lange išskleiskite aplanką:
   `app -> src -> test -> java -> com.example.wordcounter`.
2. Dešiniuoju pelės klavišu spustelėkite ant paketo `com.example.wordcounter`.
3. Pasirinkite meniu punktą **Run 'Tests in com.example.wordcounter'**.
4. Ekrano apačioje atsidarys **Run** langas, kuriame bus matoma testų eiga ir žalias patvirtinimas su visais įvykdytais testais.

#### 2 būdas: Paleidimas tiesiai iš kodo redaktoriaus
1. Dukart spustelėkite ir atidarykite failą `TextStatisticsCalculatorTest.kt` arba `WordCounterViewModelTest.kt`.
2. Kairėje redaktoriaus paraštėje, šalia klasės deklaracijos (pvz., `class TextStatisticsCalculatorTest`), spustelėkite žalią dvigubą rodyklę.
3. Pasirinkite **Run 'TextStatisticsCalculatorTest'**.

#### 3 būdas: Paleidimas per Gradle įrankių langą
1. Dešinėje ekrano pusėje atidarykite **Gradle** skiltį.
2. Išskleiskite: `WordCounter -> app -> Tasks -> verification`.
3. Dukart spustelėkite užduotį **test** (arba **testDebugUnitTest**).

### Realizuoti testai:
- **`TextStatisticsCalculatorTest` (10 testų)**:
  1. `testExample1_LabasRytas` – 1-ojo užduoties pavyzdžio testas.
  2. `testExample2_RugsejoKaras` – 2-ojo užduoties pavyzdžio testas.
  3. `testEmptyAndWhitespaceInputs` – tuščio teksto, tarpų, tabuliacijų ir lūžių testas.
  4. `testIntegersNegativesAndDecimals` – teigiamų, neigiamų ir dešimtainių skaičių (su tašku ir kableliu) testas.
  5. `testHybridWordsDoNotCountAsNumbers` – hibridinių žodžių (`COVID-19`, `3D`, `A4`, `1-asis`) atpažinimas žodžiais, o ne skaičiais.
  6. `testNegativeDecimalWithHybridWord` – neigiamų dešimtainių skaičių su hibridiniais žodžiais testas.
  7. `testDecimalDotDoesNotSplitSentence` – dešimtainio taško įtaka sakinio skaidymui (užtikrinimas, kad neskaido).
  8. `testLithuanianCharacters` – lietuviškų diakritinių raidžių palaikymo testas.
  9. `testMultiplePunctuationMarks` – pasikartojančių skyrybos ženklų (`...`, `?!`, `!!!`) testas.
  10. `testPunctuationOnlyInput` – teksto, sudaryto tik iš skyrybos ženklų, apdorojimo testas.
- **`WordCounterViewModelTest` (10 testų)**:
  1. `initialState_hasDefaultValues` – pradinės UI būsenos reikšmių patikra.
  2. `onInputTextChanged_updatesTextAndClearsError` – teksto atnaujinimas ir klaidos išvalymas vedant tekstą.
  3. `onCalculateClicked_withEmptyInput_setsErrorAndDoesNotAddToHistory` – klaidos nustatymas esant tuščiam tekstui.
  4. `onCalculateClicked_withWhitespaceInput_setsErrorAndDoesNotAddToHistory` – klaidos nustatymas esant vien tarpams.
  5. `onCalculateClicked_withInvalidInput_preservesPreviousResultsAndHistory` – ankstesnių rezultatų ir istorijos išlaikymas įvykus klaidai.
  6. `onCalculateClicked_withValidText_calculatesStatsAndAddsToHistory` – sėkmingas skaičiavimas ir įtraukimas į istoriją.
  7. `onCalculateClicked_maintainsHistoryOrder_newestFirst` – istorijos rikiavimo tvarka (naujausias viršuje).
  8. `onGenerateSampleClicked_cyclesSamplesAndDoesNotCalculateAutomatically` – ciklinis pavyzdžių keitimas ir automatinio skaičiavimo neatlikimas.
  9. `onGenerateSampleClicked_clearsErrorFlag` – klaidos išvalymas generuojant pavyzdį.
  10. `onGenerateSampleClicked_withEmptyList_doesNothing` – atsparumas tuščiam pavyzdžių sąrašui.

---

## Patikrinimo būsena

- **Automatiniai vienetų testai**: „Android Studio“ aplinkoje paleisti visi **20 vienetų testų** (`TextStatisticsCalculatorTest` ir `WordCounterViewModelTest`), ir visi **20 testų sėkmingai praėjo** (100 % pass rate).
- **Rankinis patikrinimas**: pagrindinės programėlės funkcijos – teksto įvedimas, skaičiavimas, keturių rodiklių atvaizdavimas, tuščios įvesties klaidos būsenos atsiradimas ir išnykimas, testinių pavyzdžių ciklinis generavimas bei skaičiavimų istorijos kaupimas – buvo patikrintos rankiniu būdu **Android Studio aplinkoje, naudojant „Small Desktop API 34“ emuliatorių**.
- **Svarbios pastabos ir dar nepatvirtinti scenarijai**:
  - **Netestuota fiziniame telefone**: rankinis patikrinimas atliktas tik Android Studio emuliatoriuje („Small Desktop API 34“) su kompiuterio klaviatūros įvestimi; programėlė **nebuvo** testuota fiziniame išmaniajame telefone.
  - **Horizontalus (landscape) išdėstymas**: veikimas pasukus ekraną realiame mobiliajame įrenginyje dar nėra galutinai patvirtintas.
  - **Ekraninė klaviatūra**: programėlės elgsena, elementų matomumas ir slinkimas su atidaryta programine ekranine klaviatūra (IME / virtualia klaviatūra) dar nėra patvirtinti.
  - Nors kode numatyti adaptyvūs išdėstymo sprendimai (`imePadding()`, prisitaikantys įvesties laukelio ir mygtukų parametrai), realiomis mobiliojo telefono sąlygomis jie dar nėra patikrinti ir nelaikomi patvirtintais.
