package com.example.wordcounter.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wordcounter.R
import com.example.wordcounter.domain.TextStatistics
import com.example.wordcounter.ui.theme.WordCounterTheme

/**
 * Pagrindinis „WordCounter“ naudotojo sąsajos ekranas, susietas su [WordCounterViewModel].
 */
@Composable
fun WordCounterScreen(
    viewModel: WordCounterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    WordCounterContent(
        uiState = uiState,
        onInputTextChanged = viewModel::onInputTextChanged,
        onCalculateClicked = viewModel::onCalculateClicked,
        onGenerateSampleClicked = {
            val samples = context.resources.getStringArray(R.array.test_samples).toList()
            viewModel.onGenerateSampleClicked(samples)
        },
        modifier = modifier
    )
}

/**
 * Gryna (stateless) „WordCounter“ naudotojo sąsajos kompozicija,
 * pritaikyta siauriems ir horizontaliems (landscape) ekranams bei klaviatūros atidarymui.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordCounterContent(
    uiState: WordCounterUiState,
    onInputTextChanged: (String) -> Unit,
    onCalculateClicked: () -> Unit,
    onGenerateSampleClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Įvesties laukas (kompaktiškesnis eilučių skaičius horizontaliame ekrane)
            item {
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = onInputTextChanged,
                    label = { Text(text = stringResource(R.string.input_label)) },
                    placeholder = { Text(text = stringResource(R.string.input_placeholder)) },
                    isError = uiState.isInputError,
                    supportingText = {
                        if (uiState.isInputError) {
                            Text(
                                text = stringResource(R.string.input_error_empty),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    minLines = if (isLandscape) 2 else 3,
                    maxLines = if (isLandscape) 4 else 6,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 2. Veiksmų mygtukai (portrete - vertikaliai stulpeliu siauram ekranui; landscape - horizontaliai eilutėje)
            item {
                ActionButtons(
                    isLandscape = isLandscape,
                    onCalculateClicked = onCalculateClicked,
                    onGenerateSampleClicked = onGenerateSampleClicked
                )
            }

            // 3. Dabartinių rezultatų kortelė (jei atliktas bent vienas skaičiavimas)
            if (uiState.currentStats != null) {
                item {
                    ResultsCard(stats = uiState.currentStats)
                }
            }

            // 4. Istorijos antraštė
            item {
                Text(
                    text = stringResource(R.string.history_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 5. Istorijos tuščios būsenos pranešimas arba įrašų sąrašas
            if (uiState.history.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.history_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(
                    items = uiState.history,
                    key = { it.id }
                ) { historyItem ->
                    HistoryCard(item = historyItem)
                }
            }
        }
    }
}

/**
 * Veiksmų mygtukai, adaptyviai prisitaikantys prie ekrano orientacijos.
 */
@Composable
private fun ActionButtons(
    isLandscape: Boolean,
    onCalculateClicked: () -> Unit,
    onGenerateSampleClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLandscape) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCalculateClicked,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.button_calculate))
            }

            OutlinedButton(
                onClick = onGenerateSampleClicked,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.button_generate_sample))
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCalculateClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.button_calculate))
            }

            OutlinedButton(
                onClick = onGenerateSampleClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.button_generate_sample))
            }
        }
    }
}

/**
 * Paskutinio skaičiavimo rezultatų kortelė su 4 rodikliais.
 */
@Composable
private fun ResultsCard(
    stats: TextStatistics,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.results_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    label = stringResource(R.string.label_sentences),
                    value = stats.sentenceCount,
                    modifier = Modifier.weight(1f)
                )
                MetricItem(
                    label = stringResource(R.string.label_words),
                    value = stats.wordCount,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    label = stringResource(R.string.label_characters),
                    value = stats.characterCount,
                    modifier = Modifier.weight(1f)
                )
                MetricItem(
                    label = stringResource(R.string.label_numbers),
                    value = stats.numberCount,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Vienas statistikos rodiklis (žyma ir reikšmė).
 */
@Composable
private fun MetricItem(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Istorijos įrašo kortelė, rodanti visą įvestą tekstą ir 4 apskaičiuotus rodiklius.
 */
@Composable
private fun HistoryCard(
    item: CalculationHistoryItem,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.history_item_text_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Visas įvestas tekstas be apkarpymo, kad būtų galima jį perskaityti
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${stringResource(R.string.label_sentences)}: ${item.stats.sentenceCount}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${stringResource(R.string.label_words)}: ${item.stats.wordCount}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${stringResource(R.string.label_characters)}: ${item.stats.characterCount}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${stringResource(R.string.label_numbers)}: ${item.stats.numberCount}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ==========================================
// Peržiūros (Previews) Android Studio
// ==========================================

@Preview(
    name = "Portretas - Su rezultatais",
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun WordCounterScreenPortraitPreview() {
    WordCounterTheme(dynamicColor = false) {
        WordCounterContent(
            uiState = WordCounterUiState(
                inputText = "Labas rytas! Šiandien yra 2026 metai.",
                currentStats = TextStatistics(
                    sentenceCount = 2,
                    wordCount = 5,
                    characterCount = 37,
                    numberCount = 1
                ),
                history = listOf(
                    CalculationHistoryItem(
                        text = "COVID-19 pandemijos metu temperatūra nukrito iki -5,5 laipsnių!",
                        stats = TextStatistics(
                            sentenceCount = 1,
                            wordCount = 7,
                            characterCount = 63,
                            numberCount = 1
                        )
                    )
                )
            ),
            onInputTextChanged = {},
            onCalculateClicked = {},
            onGenerateSampleClicked = {}
        )
    }
}

@Preview(
    name = "Gulsčias (Landscape) - Su rezultatais",
    showBackground = true,
    device = "spec:width=891dp,height=411dp,orientation=landscape"
)
@Composable
private fun WordCounterScreenLandscapePreview() {
    WordCounterTheme(dynamicColor = false) {
        WordCounterContent(
            uiState = WordCounterUiState(
                inputText = "Labas rytas! Šiandien yra 2026 metai.",
                currentStats = TextStatistics(
                    sentenceCount = 2,
                    wordCount = 5,
                    characterCount = 37,
                    numberCount = 1
                ),
                history = listOf(
                    CalculationHistoryItem(
                        text = "Prekė A4 kainuoja 3.14 euro...",
                        stats = TextStatistics(
                            sentenceCount = 1,
                            wordCount = 4,
                            characterCount = 31,
                            numberCount = 1
                        )
                    )
                )
            ),
            onInputTextChanged = {},
            onCalculateClicked = {},
            onGenerateSampleClicked = {}
        )
    }
}

@Preview(
    name = "Klaidos būsena (Tuščia įvestis)",
    showBackground = true
)
@Composable
private fun WordCounterScreenErrorPreview() {
    WordCounterTheme(dynamicColor = false) {
        WordCounterContent(
            uiState = WordCounterUiState(
                inputText = "",
                isInputError = true
            ),
            onInputTextChanged = {},
            onCalculateClicked = {},
            onGenerateSampleClicked = {}
        )
    }
}
