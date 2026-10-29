# WordCounter testavimas

**Naujausias rezultatas (2026-10-29, 13:52): visi 10 instrumentinių testų (6 integraciniai UI scenarijai, 3 peržiūrų atvaizdavimo testai ir 1 numatytasis paketo testas) praėjo. 25/25 vienetų testai taip pat praėjo. Visa nefiltruota instrumentinė JaCoCo ataskaita: klasės 11/11 (100 %), metodai 84/84 (100 %), eilutės 430/437 (98.40 %), šakos 68/104 (65.38 %).** Tai 100 % klasių ir metodų padengimas instrumentinėje ataskaitoje; tai nėra 100 % eilučių / šakų ar 100 % visos programėlės JVM vienetų testų padengimas.

## Vienetų testų rezultatai

2026 m. spalio 29 d. Android Studio aplinkoje įvykdyta užduotis `:app:createDebugUnitTestCoverageReport`. Visi 25 vienetų testai praėjo be klaidų ir praleistų testų.

| Testų klasė | Testai | Praėjo | Nepraėjo |
| --- | ---: | ---: | ---: |
| TextStatisticsCalculatorTest | 14 | 14 | 0 |
| WordCounterViewModelTest | 11 | 11 | 0 |
| Iš viso | 25 | 25 | 0 |

Papildomi testai tikrina vien skaičių turintį tekstą, kabutėmis ir skliaustais apgaubtus žodžius bei skaičius, brūkšnelių ir tuščių skliaustų apdorojimą, kitų simbolių ignoravimą bei skirtingus stabilius istorijos įrašų ID. Unikalūs ID reikalingi, kad pakartotinai skaičiuojant tą patį tekstą istorijos sąrašas turėtų skirtingus elementų raktus.

## Išmatuotas kodo padengimas

JaCoCo ataskaita apima visą programėlės kodą. Lentelėje atskirai pateiktos klasės, kurias patikrina JVM vienetų testai.

| Klasė | Klasės | Funkcijos | Eilutės | Šakos |
| --- | ---: | ---: | ---: | ---: |
| TextStatisticsCalculator | 1/1 | 3/3 | 52/52 | 38/38 |
| TextStatistics | 1/1 | 5/5 | 5/5 | Nėra |
| WordCounterViewModel | 1/1 | 5/5 | 33/33 | 4/4 |
| WordCounterUiState | 1/1 | 7/7 | 7/7 | Nėra |
| CalculationHistoryItem | 1/1 | 5/5 | 5/5 | Nėra |
| Šių penkių klasių suma | 5/5 | 25/25 | 102/102 | 42/42 |

Šių penkių logikos ir duomenų klasių padengimas yra 100 %. Ataskaitos funkcijų skaičius apima jos skaičiuojamus konstruktorius, savybių skaitymo funkcijas ir statinį inicijavimą.

**Istorinis rezultatas prieš UI etapą (12:41): visos programėlės 100 % padengimas dar nebuvo pasiektas.** Tuometinės visos ataskaitos rezultatai: klasės 5/11 (45,45 %), funkcijos 25/84 (29,76 %), eilutės 102/430 (23,72 %), šakos 42/104 (40,38 %). Čia įtraukti MainActivity, Compose ekranas, peržiūros, temos kodas ir kompiliatoriaus generuojamos Compose funkcijos. Jų neišskyrėme iš ataskaitos. Ekrano veikimui patikrinti reikia Android įrenginyje ar emuliatoriuje vykdomų testų.

## Pakartotinis paleidimas Android Studio

1. Atidaryti WordCounter projektą.
2. Jei siūloma, atlikti Gradle sinchronizaciją.
3. Per Find Action (`Ctrl+Shift+A`) pasirinkti `Execute Gradle Task`.
4. Vykdyti `gradle :app:createDebugUnitTestCoverageReport`.
5. Patikrinti sėkmingą vykdymą ir atidaryti `app/build/reports/coverage/test/debug/index.html`.

Projekte taip pat išsaugota bendra Gradle paleidimo konfigūracija `WordCounter unit coverage`.

Pirminiai rezultatai: `app/build/test-results/testDebugUnitTest/TEST-*.xml`. Padengimo duomenys: `app/build/reports/coverage/test/debug/report.xml`.

Debug variante įjungti `enableUnitTestCoverage` ir `enableAndroidTestCoverage`, kaip aprašyta [oficialioje Android dokumentacijoje](https://developer.android.com/studio/test/coverage-report). Antroji parinktis paruošia instrumentinių testų padengimo matavimą; ji nereiškia, kad UI testai jau įvykdyti.

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

## Naujausias išmatuotas padengimas

Ataskaitos pateikiamos **atskirai**, jos nesujungtos. Procentai tarp ataskaitų nesudedami. Visas aplikacijos kodas, įskaitant MainActivity, Compose UI, temas ir previews, liko ataskaitų įvestyje; jokių naujų application-code exclude filtrų nepridėta. JaCoCo metodų skaitiklis apima jo skaičiuojamus konstruktorius, skaitymo funkcijas, statinį inicijavimą ir Compose generuotus metodus.

| Visa debug programėlė, ataskaitos apimtis | Klasės | Metodai | Eilutės | Šakos |
| --- | ---: | ---: | ---: | ---: |
| Instrumentiniai testai, 13:52 | **11/11 (100 %)** | **84/84 (100 %)** | **430/437 (98.40 %)** | **68/104 (65.38 %)** |
| Pakartotiniai JVM vienetų testai, 13:48 | 5/11 (45.45 %) | 25/84 (29.76 %) | 102/437 (23.34 %) | 42/104 (40.38 %) |
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

Neįrodyta: 100 % eilučių / šakų, būsena žuvus procesui, fizinis įrenginio pasukimas ar 100 % visos programėlės padengimas vien JVM unit testais. Šie rezultatai nepateikiami kaip įvykdyti.

## Pateikimo būsena ir likę reikalavimai

- Mažiausiai keturi nepriklausomi UI / integraciniai scenarijai sugeneruoti ir praktiškai įvykdyti: **įvykdyta, 6/6**. Tikras Activity atkūrimas patikrintas.
- Penkių logikos / duomenų klasių JVM padengimas: **100 %**. Visos programėlės nefiltruotos instrumentinės ataskaitos klasės ir metodai: **100 % (11/11 ir 84/84)**. JVM vienetų testų visos programėlės ataskaita atskirai tebėra 5/11 klasių ir 25/84 metodų. Jei kurse reikalaujama 100 % būtent JVM unit ataskaitoje, instrumentinis rezultatas to nepakeičia ir ši interpretacija turi būti suderinta.
- Pirminiam generavimui panaudotas užduotyje įvardytas **Antigravity**. Codex pataisymai nurodyti atvirai; mišraus įrankių naudojimo priimtinumui nėra atskiro dėstytojo patvirtinimo. Metodų padengimo spraga instrumentinėje ataskaitoje uždaryta; galutinio pateikimo workflow dar priklauso nuo kurso įrankių / unit aprėpties reikalavimų ir atskiros Git sesijos.
- **GitHub commit ir push šioje sesijoje neatlikti**; jie palikti užduoties vėlesnei atskirai AI sesijai. Esami uncommitted pakeitimai išsaugoti.

