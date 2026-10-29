# WordCounter testavimas

**Naujausias rezultatas (2026-10-29, 14:41, Europe/Vilnius): 40/40 vietinių JVM vienetų testų praėjo Android Studio. Visa nefiltruota JVM JaCoCo ataskaita: 11/11 klasių (100 %), 84/84 metodų (100 %), 437/437 eilučių (100 %), 75/86 šakų (87,21 %), 3089/3108 instrukcijų (99,39 %).** Užduoties 100 % klasių ir metodų reikalavimas dabar įrodytas būtent JVM Unit ataskaitoje. Tai nėra teiginys apie 100 % šakų ar instrukcijų.

Ankstesni 10/10 instrumentinių testų taip pat praėjo 13:52 tame pačiame projekte su esamu emuliatoriumi. Jų rezultatai ir ataskaita toliau išsaugoti atskirai; jie nesujungti su JVM rezultatais ir nenaudoti JVM procentams gauti.

## Vietinių JVM vienetų testų rezultatai (14:41)

Android Studio paleista išsaugota konfigūracija **WordCounter unit coverage**, vykdanti `:app:createDebugUnitTestCoverageReport` ir `:app:testDebugUnitTest`. Galutinis rezultatas: **BUILD SUCCESSFUL in 25s**, vykdymas baigtas 14:41:00; JUnit XML ir JaCoCo XML atnaujinti 14:40:59. Visi testai realiai vykdyti: testų užduotis nebuvo vien UP-TO-DATE. JUnit XML: **40 testų, 0 nepraėjusių, 0 klaidų, 0 praleistų**.

| Testų klasė | Testai | Praėjo | Nepraėjo |
| --- | ---: | ---: | ---: |
| TextStatisticsCalculatorTest (esama) | 14 | 14 | 0 |
| WordCounterViewModelTest (esama) | 11 | 11 | 0 |
| WordCounterActivityLocalTest (nauja) | 7 | 7 | 0 |
| WordCounterPreviewLocalTest (nauja) | 3 | 3 | 0 |
| WordCounterThemeLocalTest (nauja) | 5 | 5 | 0 |
| **Iš viso** | **40** | **40** | **0** |

Visos trys naujos klasės yra `app/src/test/java/com/example/wordcounter/`, o ne `src/androidTest`. Tai vietiniai JVM testai su Robolectric Android aplinka: jie turi integracinę apimtį (tikras MainActivity, Compose medis ir lifecycle), bet vykdomi Gradle Unit užduotyje be įrenginio / emuliatoriaus. Grynieji 25 Kotlin logikos / būsenos testai išlaikyti nepakeisti.

### Naujos JVM aplinkos paruošimas ir generavimo kilmė

- Projektas: `C:\Users\Rokas\AndroidStudioProjects\WordCounter`; ManualWordCounter nenaudotas. Prieš pakeitimus perskaitytas TESTING.md ir Git status; darbo medis buvo švarus, HEAD `83fa032cad5538d5c61e8af260596d439bb4dbd5`.
- Testus ir pradinę testų konfigūraciją sugeneravo užduotyje įvardytas **Antigravity**, atskirame pokalbyje **Robolectric Unit Test Generation**, UI nurodytu modeliu **Gemini 3.8 Flash High**. Antigravity taip pat pataisė temos tipų importą, atskyrė dinaminių temų scenarijus ir pridėjo aiškias ribotos slinkimo paieškos klaidas.
- Codex AI peržiūrėjo kodą, po tikro nepavykusio paleidimo sumažino Activity testų swipe žingsnį iki 30 % sąrašo aukščio ir aiškiai pritaikė `jacoco` Gradle papildinį, kad Robolectric klasių įkėlimui būtina agento konfigūracija nebūtų tyliai praleista. Dokumentacija parengta pagal tikrus XML. Rankinis kodo redagavimas Android Studio neatliktas; visi build / testų paleidimai atlikti Android Studio UI.
- Windows x64; Android Studio 2026.1.4 (`AI-261.26222.65.2614.16204760`), Studio JBR Java 25.0.3; Gradle 9.6.0; AGP 9.4.1; Kotlin Compose plugin 2.2.10; Compose BOM 2026.02.01 / UI Test 1.10.4; JUnit 4.13.2; **Robolectric 4.17**, emuliuojamas Android API 34, `GraphicsMode.NATIVE`, `LooperMode.PAUSED`; JaCoCo **0.8.14**.
- `unitTests.isIncludeAndroidResources = true` pateikia tikrus programėlės resursus ir manifestą. JVM `--add-opens` reikalingi Robolectric ir šiai JBR versijai. Pirmam aplinkos paruošimui reikalingas priklausomybių parsisiuntimas; Gradle sinchronizacija atlikta Android Studio. Žr. [Robolectric oficialias instrukcijas](https://robolectric.org/getting-started/).
- Esamos Compose UI Test / AndroidX Test bibliotekos pridėtos ir į `testImplementation`; instrumentinių priklausomybių versijos nekeistos. JaCoCo agentui įjungtas `isIncludeNoLocationClasses = true`. Vienintelis agento `excludes` yra **`jdk.internal.*`** (JDK vidinės klasės); jis nepašalina jokio programėlės kodo ir nėra ataskaitos application-code filtras.

### Nauji scenarijai ir tikri rezultatai

Septyni Activity scenarijai tikrina visus keturis matomus skaičiavimo rodiklius ir istoriją; tuščios / tarpų įvesties validaciją bei atsistatymą; visų šešių pavyzdžių ciklą be automatinio skaičiavimo; tikrą Activity atkūrimą su naujo objekto ir seno sunaikinimo patikra; ankstesnių rezultatų išlaikymą po klaidos; vienodų istorijos įrašų tvarką; gulsčią išdėstymą ir mygtukų veikimą. Laukiamos skaičiavimo reikšmės yra nepriklausomos konstantos, ne produkcinio analizatoriaus grąžinta reikšmė.

Trys preview testai vykdo pačias esamas portretinę, gulsčią ir klaidos preview funkcijas, tikrina jų pažodines maketo reikšmes bei iš tikrųjų kviečia teksto keitimo ir abiejų mygtukų callback'us, patikrindami nepakitusią maketo būseną. Penki temos testai tikrina statines šviesią / tamsią temas ir tipografiją, dinaminių šviesios / tamsios temų primary, surface, background spalvas bei šešias spalvų ir tipografijos konstantas.

| Naujas JVM testo vardas | Faktinis rezultatas | JUnit trukmė, s |
| --- | --- | ---: |
| `repeatedIdenticalCalculations_prependsNewHistoryEntryWithCorrectOrdering` | Praėjo | 11.534 |
| `validCalculation_displaysAllFourStatisticsAndHistoryEntryWithHandCheckedValues` | Praėjo | 0.607 |
| `generateSample_cyclesThroughSamplesWithWraparound_clearsValidationWithoutAutoCalculate` | Praėjo | 0.452 |
| `landscapeLayout_rendersRowButtons_andPerformsValidCalculationAndSampleGeneration` | Praėjo | 0.455 |
| `invalidInputAfterCalculation_preservesExistingResultsAndHistory` | Praėjo | 0.571 |
| `activityRecreation_verifiesNewInstance_andPreservesInputResultsHistoryAndCyclePosition` | Praėjo | 1.048 |
| `emptyAndWhitespaceValidation_showsErrorWithoutHistory_andRecoversUponValidInput` | Praėjo | 0.373 |
| `portraitPreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState` | Praėjo | 0.279 |
| `landscapePreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState` | Praėjo | 0.231 |
| `errorPreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState` | Praėjo | 0.15 |
| `theme_dynamicLight_appliesDynamicLightColorSchemeOnAndroidS` | Praėjo | 0.07 |
| `theme_darkStatic_appliesDarkSchemeAndTypography` | Praėjo | 0.054 |
| `theme_dynamicDark_appliesDynamicDarkColorSchemeOnAndroidS` | Praėjo | 0.098 |
| `theme_lightStatic_appliesLightSchemeAndTypography` | Praėjo | 0.053 |
| `colorTokensAndTypography_haveExpectedConstantValues` | Praėjo | 0.051 |

Naudojama realių resursų eilučių ir stabilių semantinių žymeklių paieška su slinkimu ir matomumo patikromis. Fiksuotų sleep nėra. Activity atkūrimas atliekamas per `ActivityScenario.recreate()`; gulsčias atskiras testas naudoja `@Config(qualifiers = "w891dp-h411dp-land")`. Preview orientacijos pateikiamos per LocalConfiguration ir nevadinamos fiziniu pasukimu.

**Paleidimų eiga pateikiama atvirai:** 14:37 pirmas paleidimas baigėsi 39/40, nes didelis swipe žingsnis praleido matomą rodiklį vienodų skaičiavimų teste. Sumažinus žingsnį 14:38 praėjo 40/40, tačiau ataskaita dar rodė 5/11 ir 25/84: Robolectric klasių vykdymo agento parinktys nebuvo pritaikytos. Pridėtas aiškus `jacoco` papildinys ir privaloma `JacocoTaskExtension` konfigūracija vietoj tylaus `findByType()?.apply`. Tik galutinis 14:41 paleidimas patvirtino ir 40/40 testų, ir pilną klasių / metodų padengimą. Ankstesni failai ar vien testų sėkmė nebuvo palaikyti padengimo įrodymu.

## Visa nefiltruota JVM Unit JaCoCo ataskaita (14:41)

| Apimtis | Klasės | Metodai | Eilutės | Šakos | Instrukcijos |
| --- | ---: | ---: | ---: | ---: | ---: |
| **Visa debug programėlė, tik vietiniai JVM Unit testai** | **11/11 (100 %)** | **84/84 (100 %)** | **437/437 (100 %)** | **75/86 (87,21 %)** | **3089/3108 (99,39 %)** |

| Klasė JVM ataskaitoje | Klasės | Metodai | Eilutės | Šakos |
| --- | ---: | ---: | ---: | ---: |
| WordCounterUiState | 1/1 | 7/7 | 7/7 | Nėra |
| WordCounterScreenKt | 1/1 | 20/20 | 205/205 | 21/26 |
| ComposableSingletons$WordCounterScreenKt | 1/1 | 23/23 | 89/89 | Nėra |
| WordCounterViewModel | 1/1 | 5/5 | 33/33 | 4/4 |
| CalculationHistoryItem | 1/1 | 5/5 | 5/5 | Nėra |
| TextStatistics | 1/1 | 5/5 | 5/5 | Nėra |
| TextStatisticsCalculator | 1/1 | 3/3 | 52/52 | 38/38 |
| ThemeKt | 1/1 | 2/2 | 23/23 | 12/18 |
| TypeKt | 1/1 | 2/2 | 7/7 | Nėra |
| ColorKt | 1/1 | 7/7 | 6/6 | Nėra |
| MainActivity | 1/1 | 5/5 | 10/10 | Nėra |

Neįvykdytų JaCoCo skaičiuojamų metodų sąrašas tuščias. Įtraukti MainActivity, Compose ekranas, ComposableSingletons, visos trys preview funkcijos, temos / spalvų / tipografijos kodas ir visos logikos / duomenų klasės. Konstruktoriai, savybių skaitymo funkcijos, statinis inicijavimas ir Compose generuoti metodai skaičiuojami taip, kaip juos pateikia JaCoCo.

Programėlės kodas nepaslėptas ir nepašalintas iš ataskaitos. Klasių / metodų / eilučių vardikliai liko **11 / 84 / 437**. Šios JVM ataskaitos šakų vardiklis yra **86**, ankstesnės atskiros instrumentinės ataskaitos – **104** (WordCounterScreenKt: 26 JVM ir 44 instrumentinės šakos); instrukcijų vardikliai atitinkamai 3108 ir 3155. Pateikiami tikri kiekvienos ataskaitos skaitikliai, jų nesutapatiname ir nesudedame. Naujų application-code filtrų ar pasirinktų klasių ataskaitos nepridėta; liko standartinė AGP visa debug ataskaita. Visi **14 prieš šį etapą buvusių Kotlin failų** (programėlė, esami JVM ir instrumentiniai testai) SHA-256 palyginime nepakito.

Lieka **11 nepadengtų šakų**: 5 WordCounterScreenKt ir 6 ThemeKt; 19 nepadengtų instrukcijų. Todėl nereikalaujamas 100 % šakų / instrukcijų padengimas nepasiektas ir nevaizduojamas kaip pasiektas. Tai neužkerta kelio atskirai išmatuotam 100 % klasių / metodų ir eilučių rezultatui.

## JVM rezultatų pakartojimas Android Studio

1. Atidaryti šį WordCounter projektą, atlikti Gradle sinchronizaciją, jei siūloma.
2. Pasirinkti bendrą Gradle konfigūraciją **WordCounter unit coverage** iš `.run/WordCounter unit coverage.run.xml` ir Run (`Shift+F10`). Ji vykdo `:app:createDebugUnitTestCoverageReport`; emuliatorius šiam paleidimui nereikalingas.
3. Patikrinti 40 testų, 0 failures / errors / skipped ir BUILD SUCCESSFUL. Jei reikia naujo vykdymo su nepakeistu kodu, Android Studio Execute Gradle Task galima tai pačiai užduočiai pridėti `--rerun-tasks`.
4. Testų HTML: `app/build/reports/tests/testDebugUnitTest/index.html`; pirminiai JUnit XML: `app/build/test-results/testDebugUnitTest/TEST-*.xml`.
5. Visos JVM aprėpties HTML: `app/build/reports/coverage/test/debug/index.html`; XML: `app/build/reports/coverage/test/debug/report.xml`; vykdymo duomenys: `app/build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec`.
6. Tikrinti pilnos ataskaitos 11 klasių / 84 metodų vardiklius ir atskirus MainActivity, UI, preview bei temos įrašus. Ši ataskaita naudoja tik JVM `.exec`; emuliatoriaus `.ec` neįtrauktas.

Istorinis JVM rezultatas prieš papildomus Robolectric testus buvo 25/25 testai, 5/11 klasių ir 25/84 metodų; penkių logikos / duomenų klasių 102/102 eilutės ir 42/42 šakos. Ši ankstesnė spraga dabar uždaryta būtent vietiniais Unit testais. Toliau išsaugoti atskiro ankstesnio instrumentinio etapo rezultatai.

## UI / integracinių testų aplinka ir generavimas

Testuotas projektas: `C:\Users\Rokas\AndroidStudioProjects\WordCounter`, paketas `com.example.wordcounter`. `ManualWordCounter` nenaudotas. Prieš pakeitimus perskaitytas šis failas ir patikrinti Git status bei esami diff. Ankstesni README, Gradle, vienetų testų ir paleidimo konfigūracijos pakeitimai išsaugoti.

- Data ir laikas: 2026-10-29, Europe/Vilnius (UTC+02:00). Galutinis instrumentinių testų XML: 13:52:35; coverage ataskaita: 13:52:36. Vienetų testai pakartotinai įvykdyti 13:48:13–13:48:14.
- Android Studio 2026.1.4, build `AI-261.26222.65.2614.16204760`; Windows x64; Studio JBR Java 25.0.3.
- Gradle 9.6.0; Android Gradle Plugin 9.4.1; Kotlin Compose plugin 2.2.10; Compose BOM 2026.02.01, išspręsta Compose UI Test versija 1.10.4; JaCoCo 0.8.14.
- Esamas AVD `Small Desktop`, įrenginys `emulator-5554`, Android 14 / API 34, x86_64, Desktop image, 1366 × 768, 160 dpi, 2048 MB RAM. Sistemos lokalė `en_US`, programėlės numatytosios eilutės lietuviškos. Emuliatoriaus ekranas gulsčias; paskutinio paleidimo MainActivity langas buvo portretinių proporcijų, šviesioje temoje. Papildomi peržiūrų testai aiškiai pateikia portretinę ir gulsčią LocalConfiguration konfigūraciją.
- Build fingerprint: `google/sdk_gpc_x86_64/emu64xa:14/UE2A.240712.001/12084736:userdebug/dev-keys`.
- Naudotos tik esamos Compose/JUnit/Espresso/Core priklausomybės. Priklausomybės ir coverage filtrai nekeisti.

Vartotojas patvirtino, kad užduotyje įvardytą generavimo įrankį naudoti privaloma. Todėl pirminiai šeši scenarijai, testų žymekliai ir Gradle paleidimo konfigūracija sugeneruoti **Antigravity**, atskirame pokalbyje **Compose UI Integration Tests**, naudojant jo UI nurodytą **Gemini 3.8 Flash High** modelį. Antigravity perskaitė dabartinį kodą, resursų eilutes ir TESTING.md. Codex AI peržiūrėjo bei tiesiogiai failuose patikslino testus: suderino API importus, apribojo parinkiklius konkrečiomis kortelėmis, sutvarkė slinkimą, klaviatūros/fokuso tvarkymą ir emuliatoriaus ekrano paruošimą. Rankinis kodo redagavimas Android Studio neatliktas. Android Studio naudota build ir testų vykdymui.

Generavimo kilmė pateikiama tiksliai: tai Antigravity generavimas su Codex AI pataisymais. Atskiro dėstytojo patvirtinimo dėl tokio mišraus įrankių naudojimo nėra; jei kurso taisyklė reikalauja ir visus pataisymus atlikti vien Antigravity / Google AI Studio, šį priimtinumą dar reikia patvirtinti prieš galutinį pateikimą.

## Įvykdyti pagrindiniai Compose UI scenarijai (pakartotinis paleidimas 13:52)

Failas: `app/src/androidTest/java/com/example/wordcounter/WordCounterComposeTest.kt`. Kiekvienas `@Test` paleidžia tikrą `MainActivity` per `createAndroidComposeRule<MainActivity>()` ir gauna naują ViewModel. Testai nesiremia kitų testų būsena ar vykdymo tvarka.

| Scenarijaus vardas | Tikrinimas | Faktinis rezultatas | Trukmė, s |
| --- | --- | --- | ---: |
| `validCalculation_displaysAllFourStatisticsAndHistoryEntryWithHandCheckedValues` | Visos 4 rezultatų ir istorijos reikšmės; visas tekstas; tiksliai vienas įrašas | Praėjo | 6.777 |
| `emptyAndWhitespaceValidation_showsErrorWithoutHistory_andRecoversUponValidInput` | Atskirai tuščia ir tarpų / tabuliacijos / naujos eilutės įvestis; matoma klaida; nėra rezultatų ar istorijos; klaidos išvalymas ir sėkmingas skaičiavimas | Praėjo | 9.079 |
| `generateSample_cyclesThroughSamplesWithWraparound_clearsValidationWithoutAutoCalculate` | Visi 6 resursų pavyzdžiai eilės tvarka ir grįžimas į pirmą; tikslus lauko tekstas; klaidos išvalymas; be automatinio skaičiavimo ar istorijos | Praėjo | 6.454 |
| `activityRecreation_verifiesNewInstance_andPreservesInputResultsHistoryAndCyclePosition` | Tikras Activity atkūrimas; naujo objekto ir seno sunaikinimo patikra; neapskaičiuota įvestis, visi paskutiniai rodikliai, abu istorijos įrašai ir jų tvarka, pavyzdžių ciklo pozicija prieš / po atkūrimo | Praėjo | 19.501 |
| `invalidInputAfterCalculation_preservesExistingResultsAndHistory` | Atmesta netinkama įvestis išsaugo ankstesnius rodiklius ir istoriją; pavyzdžio generavimas pašalina klaidą, nekeisdamas ankstesnio skaičiavimo | Praėjo | 11.353 |
| `repeatedIdenticalCalculations_prependsNewHistoryEntryWithCorrectOrdering` | Du atskiri matomi vienodo teksto įrašai virš ankstesnio skirtingo įrašo; rodikliai ir tikslus 3 įrašų skaičius | Praėjo | 9.602 |
| **Šeši reikalavimą atitinkantys scenarijai** | **0 klaidų, 0 praleistų** | **6/6 praėjo** | **62.766** |

Kontrolinis pirmo scenarijaus tekstas: `Labas rytas! Šiandien yra 2026 metai, kaina -5,5 euro.` Nepriklausomai nustatyti rezultatai: **2 sakiniai, 7 žodžiai, 54 ženklai, 2 skaičiai**. Tikrinamos ir rezultatų kortelė, ir istorijos kortelė. Tikėtinos reikšmės nekviečia produkcinio analizatoriaus.

Atkūrimo scenarijuje naudoti tekstai `Pirmas tekstas 100` (1, 2, 18, 1) ir `Antrasis sakinys! Čia yra 2 žodžiai.` (2, 5, 36, 1), reikšmių tvarka: sakiniai, žodžiai, ženklai, skaičiai. Prieš atkūrimą sugeneruotas pirmas, dar neapskaičiuotas pavyzdys. `ActivityScenario.recreate()` iš tikrųjų sunaikino `MainActivity@f69f16e` ir sukūrė `MainActivity@8921bbb` (13:51:49); testas patikrino objektų skirtumą ir `isDestroyed`. Tai **Activity atkūrimo**, o ne imituotos orientacijos perkompozicijos patikra. Fizinis ekrano pasukimas ir proceso žūtis šiame paleidime netikrinti.

Numatytasis `ExampleInstrumentedTest.useAppContext` paliktas nepakeistas ir taip pat praėjo (XML trukmė 0.000 s). Jis **neįskaičiuotas** į šešis UI scenarijus. Galutinis instrumentinis XML: **10 testų, 0 nepraėjusių, 0 klaidų, 0 praleistų**, įskaitant tris toliau aprašytus peržiūrų testus. Android Studio Gradle rezultatas: `BUILD SUCCESSFUL in 1m 35s`.

## Patikimumas ir taisytos vykdymo problemos

- Parinkikliai remiasi realiomis resursų eilutėmis, teksto įvedimo / mygtukų semantika, stabiliais sąrašo, rodiklių ir kortelių test tags. Vienodų tekstų ir skaičių paieška apribota konkrečia istorijos ar rodiklio kortele. Mygtukai tikrinami sujungtame, kortelių tekstai ir reikšmės – nesujungtame semantiniame medyje.
- Istorijos indeksas naudojamas tik žymekliui ir rodomai tvarkai patikrinti. Programėlės esami unikalūs UUID elementų raktai išlaikyti. Pradiniame UI etape prie programėlės pridėta elgsenai neutrali semantika ir `itemsIndexed` vietoj `items` šiems žymekliams perduoti. Vėlesniame padengimo etape tik trijų preview funkcijų matomumas pakeistas iš `private` į `internal`, išsaugant jų kūnus ir anotacijas. Skaičiavimo, ViewModel, istorijos ir temų elgsena nekeista.
- Testai slenka realiais Compose touch swipe veiksmais ir tikrina matomą tekstą. Neegzistavimas neįrodomas vien nepatekusio į ekraną lazy elemento nebuvimu: patikrinamas visas sąrašas iki faktinės slinkties pabaigos. Paieška apribota 100 veiksmų ir nepavykus aiškiai baigiasi klaida. Nenaudojami fiksuoti `sleep` ar iš anksto nustatyti laukimai vietoj sinchronizacijos.
- Ekranas paruošiamas tik testuojamos Activity lango flagais; emuliatorius turi būti be PIN / slaptažodžio. Naudojamas Compose sąlyginis laukimas, o po įvedimo išvalomas fokusas ir paslepiama IME tiesiogine Android API, nesiunčiant grįžimo veiksmo, kuris galėtų uždaryti Activity. Testų pabaigoje langas sunaikinamas.
- Pirmi paleidimai buvo nesėkmingi arba nutraukti: pataisytas API importas, miegantis / užrakintas emuliatorius ir netinkamas sujungto medžio mygtuko tikrinimas. Vėliau live thread diagnostika parodė Compose framework laukimą `waitForNextChoreographerFrame`, kai programėlė jau buvo idle. Pakartotinai paleistas Android tame pačiame esamame AVD. Po šio paleidimo visi šeši scenarijai baigėsi sėkmingai. Ankstesni nepavykę / nutraukti bandymai nelaikomi praėjusiais.
- ADB / Java debug ryšys naudotas tik emuliatoriaus diagnostikai ir Android perkrovimui. Galutiniai build ir testų paleidimai atlikti Android Studio per išsaugotas Gradle konfigūracijas.

## Instrumentinio etapo padengimas ir istorinis JVM palyginimas (13:52)

Ataskaitos pateikiamos **atskirai**, jos nesujungtos. Procentai tarp ataskaitų nesudedami. Visas aplikacijos kodas, įskaitant MainActivity, Compose UI, temas ir previews, liko ataskaitų įvestyje; jokių naujų application-code exclude filtrų nepridėta. JaCoCo metodų skaitiklis apima jo skaičiuojamus konstruktorius, skaitymo funkcijas, statinį inicijavimą ir Compose generuotus metodus.

| Visa debug programėlė, ataskaitos apimtis | Klasės | Metodai | Eilutės | Šakos |
| --- | ---: | ---: | ---: | ---: |
| Instrumentiniai testai, 13:52 | **11/11 (100 %)** | **84/84 (100 %)** | **430/437 (98.40 %)** | **68/104 (65.38 %)** |
| Istoriniai JVM vienetų testai, 13:48 (iki Robolectric etapo) | 5/11 (45.45 %) | 25/84 (29.76 %) | 102/437 (23.34 %) | 42/104 (40.38 %) |
| Tik penkios logikos / duomenų klasės, JVM ataskaitos dalis | 5/5 (100 %) | 25/25 (100 %) | 102/102 (100 %) | 42/42 (100 %) |

Instrumentinių testų instrukcijų padengimas: 3099/3155 (98.23 %). Nuo ankstesnės 430 eilučių ataskaitos bendras eilučių skaičius padidėjo iki 437 dėl UI semantinių žymeklių; apimtis nesumažinta.

| Klasė visoje instrumentinių testų ataskaitoje | Klasės | Metodai | Eilutės | Šakos |
| --- | ---: | ---: | ---: | ---: |
| MainActivity | 1/1 | 5/5 | 10/10 | Nėra |
| WordCounterScreenKt | 1/1 | 20/20 | 205/205 | 30/44 |
| ComposableSingletons$WordCounterScreenKt | 1/1 | 23/23 | 89/89 | Nėra |
| WordCounterViewModel | 1/1 | 5/5 | 33/33 | 3/4 |
| WordCounterUiState | 1/1 | 7/7 | 7/7 | Nėra |
| CalculationHistoryItem | 1/1 | 5/5 | 5/5 | Nėra |
| TextStatistics | 1/1 | 5/5 | 5/5 | Nėra |
| TextStatisticsCalculator | 1/1 | 3/3 | 45/52 | 26/38 |
| ThemeKt | 1/1 | 2/2 | 23/23 | 9/18 |
| TypeKt | 1/1 | 2/2 | 7/7 | Nėra |
| ColorKt | 1/1 | 7/7 | 6/6 | Nėra |

**Ankstesnė 17 metodų spraga uždaryta.** 13:29 ataskaitoje buvo 67/84 metodai: nepadengtos 3 preview funkcijos ir 14 Compose Singleton metodų (2 portretinių mygtukų bei 12 preview lambdų). 13:52 ataskaita rodo 84/84, nepadengtų metodų sąrašas tuščias. Įtraukti MainActivity, WordCounterScreenKt, ComposableSingletons, temos bei visi trys previews; bendras metodų skaičius liko 84 ir klasių skaičius liko 11. Jokios programėlės klasės, metodai ar previews nepašalinti iš ataskaitos. Vis dar liko 7 visiškai nepadengtos analizatoriaus eilutės ir 36 nepadengtos šakos, todėl 100 % eilučių / šakų nepasiekta.

Vienetų testų regresija: **25/25 praėjo**, klaidų ir praleistų nėra. Abiejų esamų vienetų testų failų SHA-256 liko toks pats kaip prieš šios sesijos pakeitimus.

## Pakartotinis UI testų ir ataskaitų paleidimas Android Studio

1. Atidaryti šį WordCounter projektą ir esamą `Small Desktop API 34` emuliatorių. Jei atkurta sena, stringanti būsena, perkrauti Android; testų aplinkai nereikia kurti naujo AVD ar trinti jo duomenų.
2. Pasirinkti bendrą paleidimo konfigūraciją **WordCounter Android test coverage** iš `.run/WordCounter Android test coverage.run.xml` ir paspausti Run (`Shift+F10`). Ji vykdo `:app:createDebugAndroidTestCoverageReport`, įskaitant `:app:connectedDebugAndroidTest`.
3. Reikalauti visų 10 testų sėkmės (6 pagrindiniai UI scenarijai, 3 peržiūrų atvaizdavimo testai ir 1 numatytasis paketo testas) ir `BUILD SUCCESSFUL`; vien ankstesnio HTML failo buvimas neįrodo naujo paleidimo sėkmės.
4. Atidaryti `app/build/reports/androidTests/connected/debug/index.html` ir `app/build/reports/coverage/androidTest/debug/connected/index.html`.
5. Vienetų regresijai pasirinkti **WordCounter unit coverage**, vykdančią `:app:createDebugUnitTestCoverageReport`.

Pirminiai galutinio paleidimo įrodymai:

- UI JUnit XML: `app/build/outputs/androidTest-results/connected/debug/TEST-Small_Desktop(AVD) - 14.xml`.
- UI JaCoCo XML: `app/build/reports/coverage/androidTest/debug/connected/report.xml`.
- Įrenginio coverage duomenys: `app/build/outputs/code_coverage/debugAndroidTest/connected/Small_Desktop(AVD) - 14/coverage_data/coverage.ec`.
- JVM JUnit XML: `app/build/test-results/testDebugUnitTest/TEST-*.xml`.
- JVM JaCoCo XML: `app/build/reports/coverage/test/debug/report.xml`.
- JVM coverage duomenys: `app/build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec`.

## Papildomas preview padengimo etapas (13:52)

Vartotojui paprašius tęsti, per Antigravity tame pačiame **Compose UI Integration Tests** pokalbyje sugeneruotas atskiras failas `app/src/androidTest/java/com/example/wordcounter/WordCounterPreviewRenderTest.kt`. Šiems papildomiems testams Codex kodo pataisymų neprireikė: visi trys pirmu paleidimu praėjo Android Studio.

| Papildomo testo vardas | Tikras tikrinamas atvaizdavimas | Rezultatas | Trukmė, s |
| --- | --- | --- | ---: |
| `portraitPreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState` | Esama portretinė preview funkcija; tikslus pradinis tekstas, 4 rodikliai, visas istorijos įrašas ir 4 jo rodikliai; teksto keitimo ir abiejų mygtukų no-op callback'ai | Praėjo | 11.725 |
| `landscapePreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState` | Esama gulsčia preview funkcija; jos pažodinė įvestis, 4 rodikliai ir atskiras istorijos maketas; visi 3 callback'ai; nepakitusi maketo būsena | Praėjo | 9.124 |
| `errorPreview_rendersLiteralFixtureSnapshot_andInvokesNoOpCallbacksWithoutMutatingState` | Esama klaidos preview funkcija; matoma klaida, tuščia įvestis / istorija ir rezultatų nebuvimas; visi 3 callback'ai; klaidos maketo būsena išlieka | Praėjo | 3.115 |
| **Papildomi atvaizdavimo testai** | **0 klaidų, 0 praleistų** | **3/3** | **23.964** |

Naudojamas esamas `ui-test-manifest` ComponentActivity hostas ir `createAndroidComposeRule<ComponentActivity>()`. Kompozicijoje kviečiamos pačios esamos preview funkcijos; jos neperkopijuotos į testą. `LocalConfiguration` pateikiama aiškiai (portretas arba landscape), nes @Preview device anotacija vykdymo metu pati orientacijos nenustato. Tai imituotos išdėstymo konfigūracijos patikra; pagrindinis Activity atkūrimo testas išlieka tikras Android lifecycle atkūrimas.

Tikrintos esamos pažodinės preview maketų reikšmės: dabartiniai rezultatai `(2, 5, 37, 1)` abiejuose užpildytuose maketuose; portretinės istorijos rezultatai `(1, 7, 63, 1)`, gulsčios istorijos rezultatai `(1, 4, 31, 1)`. Tvarka: sakiniai, žodžiai, ženklai, skaičiai. Šie preview duomenys yra maketo konstantos; testai jų nevadina tikru analizatoriaus skaičiavimo rezultatu. Analizatoriaus ir visos app skaičiavimo teisingumą tikrina atskiri vienetų ir pagrindiniai UI testai.

Kad būtų patikrintos ir vykdomos preview lambdos, testai iš tikrųjų keičia tekstą ir paspaudžia abu matomus mygtukus, tuomet tikrina nepakitusią maketo būseną. Tai papildomi maketų regresijos testai, o ne originalių šešių programėlės integracinių scenarijų pakaitalas.

**Pakeitimų ribos patikrintos:** lyginant su 13:29 kodo versija, vienintelis papildomas aplikacijos kodo skirtumas yra 3 funkcijų `private` → `internal`; jų kūnai / anotacijos, programėlės logika ir resursų tekstai nepakeisti. Šešių pagrindinių UI testų ir abiejų vienetų testų failų SHA-256 liko nepakitęs. Naujų priklausomybių ar coverage exclude filtrų nepridėta. Visas išmatuotas vardiklis liko **84 metodai, 11 klasių ir 437 eilutės**.

Galutinio paleidimo suma: **9 prasmingi UI / atvaizdavimo testai + 1 numatytasis paketo testas = 10/10 praėjo**. Pagrindinių šešių trukmė 62.766 s, papildomų trijų 23.964 s, kartu 86.730 s. Galutinis build baigėsi 13:52:36 su `BUILD SUCCESSFUL in 1m 35s`. 25/25 vienetų testų regresija atlikta 13:48:13–13:48:14 ir praėjo be klaidų ar praleistų testų.

Šiame istoriniame instrumentiniame etape nebuvo įrodyta 100 % eilučių / šakų ar 100 % JVM padengimo. Vėlesnis 14:41 JVM etapas aprašytas dokumento pradžioje. Proceso žūtis ir fizinis įrenginio pasukimas netikrinti; reikalaujamą atkūrimo alternatyvą tikrina tikras ActivityScenario.recreate().

## Pateikimo būsena ir likę reikalavimai

- Mažiausiai keturi nepriklausomi UI / integraciniai scenarijai: **įvykdyta, šeši scenarijai praėjo esamame emuliatoriuje**, papildomai trys preview testai ir paliktas numatytasis paketo testas. Paketo testas neskaičiuojamas kaip vienas iš reikalaujamų scenarijų.
- **100 % visos programėlės klasių ir metodų būtent JVM Unit ataskaitoje: įvykdyta, 11/11 ir 84/84; 40/40 testų praėjo.** Instrumentinė ataskaita šiam įrodymui nenaudota. Padengimo reikalavimas nebelaikomas interpretacijos ar derinimo klausimu.
- Užduotyje nurodytas **Antigravity panaudotas pirminiam abiejų etapų testų generavimui**. Antigravity ir Codex indėlis nurodytas tiksliai. Atskiro dėstytojo patvirtinimo, kad leidžiami ir vėlesni Codex AI pataisymai, neturime; jei kurso tvarka reikalauja išskirtinai vieno įrankio visiems žingsniams, šio workflow priimtinumo nevadiname patvirtintu.
- Šio JVM papildymo **commit / push neatlikti**: HEAD išliko `83fa032cad5538d5c61e8af260596d439bb4dbd5`. Dabartiniai nauji testai, testų konfigūracija ir dokumentacija palikti peržiūrėti ir išsiųsti vėlesnėje atskiroje AI Git sesijoje.
- Galutinis pateikimas universiteto / kurso sistemoje šiame darbe neatliktas. 100 % šakų / instrukcijų, fizinis pasukimas ir proceso žūtis neteigiami kaip patikrinti; Activity atkūrimo reikalavimo alternatyva įvykdyta.
