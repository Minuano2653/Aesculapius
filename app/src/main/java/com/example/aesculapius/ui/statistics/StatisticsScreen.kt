package com.example.aesculapius.ui.statistics

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aesculapius.R
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.ui.statistics.components.DownloadReportFab
import com.example.aesculapius.ui.statistics.components.ReportGenerationDialog
import com.example.aesculapius.ui.statistics.components.StatisticsChartCard

object StatisticsScreen : NavigationDestination {
    override val route = "StatisticsScreen"
}

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    userUiState: SignUpUiState,
    statisticsViewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by statisticsViewModel.uiState.collectAsState()
    val chartEntryModelLine by statisticsViewModel.chartEntryModelLine.collectAsState()
    val chartEntryModelColumn by statisticsViewModel.chartEntryModelColumn.collectAsState()
    val datesForLineChart by statisticsViewModel.datesForLineChart.collectAsState()
    val datesForColumnChart by statisticsViewModel.datesForColumnChart.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        statisticsViewModel.effects.collect { effect ->
            when (effect) {
                is StatisticsEffect.LaunchPdfChooser ->
                    context.startActivity(effect.intent)
                is StatisticsEffect.ShowErrorToast ->
                    Toast.makeText(context, effect.messageRes, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                DownloadReportFab(
                    enabled = !state.isReportLoading,
                    onClick = { statisticsViewModel.onEvent(StatisticsEvent.OnDownloadReportClick) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            StatisticsChartCard(
                state = state,
                chartEntryModelLine = chartEntryModelLine,
                chartEntryModelColumn = chartEntryModelColumn,
                datesForLineChart = datesForLineChart,
                datesForColumnChart = datesForColumnChart,
                userUiState = userUiState,
                convertToRussian = statisticsViewModel::convertToRussian,
                onEvent = statisticsViewModel::onEvent
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.how_good_is_your_asthma),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.description_dinamic),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
                    .fillMaxWidth()
            )
        }
    }

    if (state.isReportLoading) {
        ReportGenerationDialog()
    }
}
