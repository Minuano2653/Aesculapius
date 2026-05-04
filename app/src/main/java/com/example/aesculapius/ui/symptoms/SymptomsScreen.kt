package com.example.aesculapius.ui.symptoms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aesculapius.R
import com.example.aesculapius.ui.TopBar
import com.example.aesculapius.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object SymptomsScreen : NavigationDestination {
    override val route = "SymptomsScreen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsScreen(
    modifier: Modifier = Modifier,
    viewModel: SymptomsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val symptoms by viewModel.symptoms.collectAsStateWithLifecycle()

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var inputName by rememberSaveable { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                onNavigateBack = onNavigateBack,
                text = stringResource(R.string.my_symptoms)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.new_symptom),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        if (symptoms.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.symptoms_list_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(symptoms, key = { it.id }) { symptom ->
                    SymptomCard(
                        symptom = symptom,
                        onDeleteClick = { pendingDeleteId = symptom.id }
                    )
                }
            }
        }
    }

    // Диалог подтверждения удаления
    pendingDeleteId?.let { symptomId ->
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onDismissRequest = { pendingDeleteId = null },
            title = {
                Text(
                    text = stringResource(R.string.delete_symptom_title),
                    style = MaterialTheme.typography.displayMedium
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_symptom_message),
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(SymptomEvent.OnDeleteSymptom(symptomId))
                    pendingDeleteId = null
                }) {
                    Text(
                        text = stringResource(R.string.confirm),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }

    // BottomSheet для добавления симптома
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                inputName = ""
            },
            sheetState = sheetState,
            tonalElevation = 0.dp,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_symptom),
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    label = {
                        Text(
                            text = stringResource(R.string.symptom_name_hint),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        modifier = Modifier.widthIn(min = 106.dp),
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showBottomSheet = false
                                inputName = ""
                            }
                        }) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Button(
                        modifier = Modifier.widthIn(min = 106.dp),
                        onClick = {
                            viewModel.onEvent(SymptomEvent.OnAddSymptom(inputName.trim()))
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showBottomSheet = false
                                inputName = ""
                            }
                        },
                        enabled = inputName.isNotBlank(),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SymptomCard(
    symptom: SymptomItem,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material.Card(
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = symptom.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_symptom_title),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
