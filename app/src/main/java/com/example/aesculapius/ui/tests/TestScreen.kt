package com.example.aesculapius.ui.tests

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aesculapius.R
import com.example.aesculapius.ui.TopBar
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.theme.background

object TestScreen : NavigationDestination {
    override val route = "TestScreen"
    const val depart = "departure"
    val routeWithArgs: String = "$route/{$depart}"
}

@Composable
fun TestScreen(
    testName: String,
    questionsList: MutableList<Question>,
    isAstTest: Boolean,
    onNavigateBack: () -> Unit,
    onClickSummary: (List<Int>) -> Unit,
) {
    var isAlertDialogShown by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(0) }
    val currentAnswers by remember { mutableStateOf(MutableList(questionsList.size) { -1 }) }
    var currentAnswer by remember { mutableIntStateOf(-1) }
    val context = LocalContext.current

    BackHandler { isAlertDialogShown = true }

    Scaffold(topBar = {
        TopBar(
            onNavigateBack = { isAlertDialogShown = true },
            text = testName,
            existHelpButton = true,
            onClickHelpButton = { /* TODO */ }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                start = 24.dp,
                end = 24.dp
            )
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .height(37.dp)
            ) {
                items(questionsList.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .aspectRatio(0.9f)
                    ) {
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    currentAnswers[currentPage] = currentAnswer
                                    currentPage = index
                                    currentAnswer =
                                        if (currentAnswers[index] != -1)
                                            currentAnswers[index]
                                        else -1
                                }
                                .align(Alignment.TopCenter),
                            shape = MaterialTheme.shapes.small,
                            backgroundColor =
                            if (currentAnswers[index] != -1 || index == currentPage) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.errorContainer
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color =
                                    if (currentAnswers[index] != -1 || index == currentPage) MaterialTheme.colorScheme.tertiaryContainer
                                    else MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                        if (index == currentPage) {
                            Canvas(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.BottomCenter)
                            ) {
                                drawCircle(color = background, center = center)
                            }
                        }
                    }
                }
            }
            Text(
                text = stringResource(id = questionsList[currentPage].questionText),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 29.dp, bottom = 18.dp),
                color = MaterialTheme.colorScheme.onSecondary
            )
            LazyColumn() {
                itemsIndexed(questionsList[currentPage].answersList) { index, answer ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 10.dp).clickable { currentAnswer = index }
                    ) {
                        RadioButton(
                            selected = (index == currentAnswer),
                            onClick = { currentAnswer = index },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = stringResource(id = answer),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 17.dp).clickable { currentAnswer = index }
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    if (currentPage == questionsList.size - 1) {
                        Log.i("TAGTAG", "$currentAnswer $currentAnswers")
                        if (-1 in currentAnswers.subList(0, currentAnswers.size - 1) || currentAnswer == -1) {
                            Toast.makeText(context, context.getString(R.string.answer_all_questions), Toast.LENGTH_SHORT).show()
                            currentAnswers[currentPage] = currentAnswer
                        }
                        else {
                            currentAnswers[currentPage] = currentAnswer
                            onClickSummary(currentAnswers.toList())
                        }
                    } else {
                        currentAnswers[currentPage] = currentAnswer
                        currentPage++
                        currentAnswer = if (currentAnswers[currentPage] != -1) currentAnswers[currentPage] else -1
                    }
                },
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .height(56.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text =
                    if (currentPage == questionsList.size - 1) stringResource(R.string.sum_up)
                    else stringResource(id = R.string.next),
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )
            }
            if (isAlertDialogShown) {
                AlertDialog(
                    onDismissRequest = { isAlertDialogShown = false },
                    modifier = Modifier
                        .wrapContentHeight()
                        .width(315.dp),
                    shape = RoundedCornerShape(16.dp),
                    title = {
                        Text(
                            text = stringResource(R.string.are_you_sure),
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(top = 34.dp)
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.your_progress),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = { onNavigateBack() }, modifier = Modifier.padding(bottom = 24.dp)) {
                            Text(
                                text = stringResource(R.string.exit), style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { isAlertDialogShown = false }, modifier = Modifier.padding(bottom = 24.dp, end = 24.dp)) {
                            Text(
                                text = stringResource(id = R.string.back), style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}