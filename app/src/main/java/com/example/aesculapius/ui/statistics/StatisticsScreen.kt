package com.example.aesculapius.ui.statistics

import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aesculapius.R
import com.example.aesculapius.data.graphicsNavigationItemContentList
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.ui.theme.primary
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.context.DrawContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Period

object StatisticsScreen : NavigationDestination {
    override val route = "StatisticsScreen"
}

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    userUiState: SignUpUiState,
    statisticsViewModel: StatisticsViewModel = hiltViewModel<StatisticsViewModel>(),
) {
    val statisticsUiState by statisticsViewModel.statisticsUiState.collectAsState()
    val datesForColumnChart by statisticsViewModel.datesForColumnChart.collectAsState()
    val modelProducerColumn by statisticsViewModel.chartEntryModelColumn.collectAsState()
    val datesForLineChart by statisticsViewModel.datesForLineChart.collectAsState()
    val modelProducerLine by statisticsViewModel.chartEntryModelLine.collectAsState()

    var currentLinePointsAmount by remember { mutableIntStateOf(0) }
    var currentColumnPointsAmount by remember { mutableIntStateOf(0) }

    var isLineChart by remember { mutableStateOf(true) }

    // dates for bar and line charts, setting styles for line charts
    val datasetLineSpec = remember { arrayListOf<LineChart.LineSpec>() }
    // setting styles for line chart
    datasetLineSpec.add(
        LineChart.LineSpec(
            lineColor = MaterialTheme.colorScheme.primary.toArgb(),
            lineThicknessDp = 4f
        )
    )

    // states for data changes
    var dateTextLine by remember { mutableStateOf(LocalDate.now()) }
    var dateTextColumn by remember { mutableStateOf(LocalDate.now()) }
    var pointsAmountText by remember { mutableIntStateOf(-1) }
    var dateBegin by remember { mutableStateOf(LocalDate.now().minusDays(6)) }

    LaunchedEffect(key1 = statisticsUiState) {
        currentColumnPointsAmount = withContext(Dispatchers.IO) {
            statisticsViewModel.getColumnPointsAmountOnDates(LocalDate.now().minusYears(1), LocalDate.now())
        }
        statisticsViewModel.setScoresInRange()
        currentLinePointsAmount = withContext(Dispatchers.IO) {
            when (statisticsUiState.graphicTypes) {
                GraphicTypes.Week -> {
                    dateBegin = LocalDate.now().minusDays(6)
                    statisticsViewModel.setMetricsOnDatesShort(dateBegin, LocalDate.now())
                    statisticsViewModel.getLinePointsAmountOnDates(dateBegin, LocalDate.now())
                }

                GraphicTypes.Month -> {
                    dateBegin = LocalDate.now().minusMonths(1)
                    statisticsViewModel.setMetricsOnDatesShort(dateBegin, LocalDate.now())
                    statisticsViewModel.getLinePointsAmountOnDates(dateBegin, LocalDate.now())
                }

                GraphicTypes.ThreeMonths -> {
                    dateBegin = LocalDate.now().minusMonths(3)
                    statisticsViewModel.setMetricsOnDatesLong(dateBegin, LocalDate.now())
                    statisticsViewModel.getLinePointsAmountOnDates(dateBegin, LocalDate.now())
                }

                GraphicTypes.HalfYear -> {
                    dateBegin = LocalDate.now().minusMonths(6)
                    statisticsViewModel.setMetricsOnDatesLong(dateBegin, LocalDate.now())
                    statisticsViewModel.getLinePointsAmountOnDates(dateBegin, LocalDate.now())
                }

                else -> {
                    dateBegin = LocalDate.now().minusYears(1)
                    statisticsViewModel.setMetricsOnDatesLong(dateBegin, LocalDate.now())
                    statisticsViewModel.getLinePointsAmountOnDates(dateBegin, LocalDate.now())
                }
            }
        }
    }
    Box {
        LazyColumn(modifier = modifier) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = if (isLineChart) stringResource(R.string.metrics_name) else stringResource(id = R.string.ast_test_name),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(vertical = 22.dp)
                            .fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column() {
                            if (!isLineChart)
                                DisplayDatesForColumn(
                                    pointsAmountText = pointsAmountText,
                                    dateTextColumn = dateTextColumn,
                                    convertToRussian = { statisticsViewModel.convertToRussian(it) }
                                )
                            else
                                DisplayDatesForLine(
                                    graphicTypes = statisticsUiState.graphicTypes,
                                    dateText = dateTextLine, dateBegin = dateBegin
                                )
                        }
                        Spacer(Modifier.weight(1f))
                        Card(
                            elevation = 0.dp,
                            modifier = Modifier
                                .clickable { isLineChart = !isLineChart }
                                .wrapContentSize(),
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                Card(
                                    modifier = Modifier.size(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = 0.dp,
                                    backgroundColor = if (isLineChart) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.line_graphic_icon),
                                        contentDescription = null,
                                        alignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(10.dp)
                                    )
                                }
                                Card(
                                    modifier = Modifier.size(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = 0.dp,
                                    backgroundColor = if (!isLineChart) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.bar_chart_icon),
                                        contentDescription = null,
                                        alignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                    val assets = LocalContext.current
                    val customTypeface =
                        Typeface.createFromAsset(assets.assets, "inter_regular.ttf")

                    ProvideChartStyle {
                        if ((currentLinePointsAmount < 1 && (statisticsUiState.graphicTypes == GraphicTypes.Week ||
                                    statisticsUiState.graphicTypes == GraphicTypes.Month) && isLineChart) ||
                            (currentLinePointsAmount < 7 && (statisticsUiState.graphicTypes == GraphicTypes.ThreeMonths ||
                                    statisticsUiState.graphicTypes == GraphicTypes.HalfYear ||
                                    statisticsUiState.graphicTypes == GraphicTypes.Year)) && isLineChart)
                            Box(
                                modifier = Modifier
                                    .heightIn(min = 250.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.no_data),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        else if (isLineChart)
                            ShowLineChart(
                                datasetDates = datesForLineChart,
                                modelProducer = modelProducerLine,
                                datasetLineSpec = datasetLineSpec,
                                onChangeMarker = { dateTextLine = it },
                                typeface = customTypeface,
                                userUiState = userUiState
                            )
                        else if (currentColumnPointsAmount < 1)
                            Box(
                                modifier = Modifier
                                    .heightIn(min = 250.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.no_data),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        else
                            ShowColumnChart(
                                typeface = customTypeface,
                                onDataChanged = { x, y ->
                                    pointsAmountText = y
                                    dateTextColumn = datesForColumnChart[x]
                                },
                                modelProducerColumn = modelProducerColumn,
                                amountPoints = datesForColumnChart.size
                            )
                    }
                }
                if (isLineChart) {
                    Row(
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = 16.dp, start = 3.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        graphicsNavigationItemContentList.forEach {
                            Card(
                                modifier = Modifier
                                    .height(32.dp)
                                    .wrapContentWidth()
                                    .clickable { statisticsViewModel.updateCurrentNavType(it) }
                                    .padding(end = 3.dp),
                                backgroundColor = if (it == statisticsUiState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                                contentColor =
                                if (it == statisticsUiState) MaterialTheme.colorScheme.tertiaryContainer
                                else MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp),
                                elevation = 0.dp
                            ) {
                                Text(
                                    text = stringResource(id = it.nameOfType),
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.how_good_is_your_asthma),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 20.dp, start = 23.dp, end = 23.dp)
                )
                Text(
                    text = stringResource(R.string.description_dinamic),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 8.dp, start = 23.dp, end = 23.dp)
                )
                Spacer(Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun ShowColumnChart(
    amountPoints: Int,
    typeface: Typeface,
    modelProducerColumn: ChartEntryModelProducer,
    onDataChanged: (Int, Int) -> Unit
) {
    val defaultColumns = currentChartStyle.columnChart.columns
    val thresholdLineNullLevel = rememberNullLevel(typeface)
    Chart(
        modifier = Modifier.padding(bottom = 30.dp),
        chart = columnChart(
            columns = remember(defaultColumns) {
                defaultColumns.map { _ ->
                    object : LineComponent(
                        color = primary.toArgb(),
                        thicknessDp = 8f,
                        shape = Shapes.roundedCornerShape(
                            topLeftPercent = 40,
                            topRightPercent = 40,
                            bottomLeftPercent = 0,
                            bottomRightPercent = 0
                        )
                    ) {
                        override fun drawVertical(
                            context: DrawContext,
                            top: Float,
                            bottom: Float,
                            centerX: Float,
                            thicknessScale: Float,
                            opacity: Float,
                        ) {
                            super.drawVertical(
                                context,
                                top,
                                bottom,
                                centerX,
                                1f,
                                opacity
                            )
                        }
                    }
                }
            },
            decorations = remember(thresholdLineNullLevel) {
                listOf(thresholdLineNullLevel)
            },
            spacing = if (amountPoints > 1) (14 + (12 - amountPoints) * (14 / (amountPoints - 1))).dp else 500.dp
        ),
        chartModelProducer = modelProducerColumn,
        isZoomEnabled = false,
        chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
        startAxis = rememberStartAxis(
            label = textComponent(
                color = MaterialTheme.colorScheme.secondary,
                textSize = 12.sp,
                typeface = typeface
            ),
            valueFormatter = { value, _ -> value.toInt().toString() },
            tickLength = 0.dp,
            tick = LineComponent(color = Color.Transparent.toArgb()),
            axis = LineComponent(color = Color.Transparent.toArgb()),
            verticalLabelPosition = VerticalAxis.VerticalLabelPosition.Top,
            horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
            itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 6),
            guideline = LineComponent(
                strokeColor = MaterialTheme.colorScheme.secondary.toArgb(),
                color = MaterialTheme.colorScheme.secondary.toArgb(),
                thicknessDp = 1f
            ),
        ),
        horizontalLayout = HorizontalLayout.FullWidth(
            unscalableStartPaddingDp = 45f,
            unscalableEndPaddingDp = 20f
        ),
        bottomAxis = rememberBottomAxis(
            label = null,
            axis = LineComponent(
                color = MaterialTheme.colorScheme.secondary.toArgb(),
                thicknessDp = 1f
            ),
            tickLength = 0.dp,
            guideline = null
        ),
        marker = rememberColumnMarker(),
        markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
            // update data when on marker moved
            override fun onMarkerMoved(
                marker: Marker,
                markerEntryModels: List<Marker.EntryModel>,
            ) {
                super.onMarkerMoved(marker, markerEntryModels)
                onDataChanged(
                    markerEntryModels.first().entry.x.toInt(),
                    markerEntryModels.first().entry.y.toInt()
                )
            }

            // update data on marker shown
            override fun onMarkerShown(
                marker: Marker,
                markerEntryModels: List<Marker.EntryModel>
            ) {
                super.onMarkerShown(marker, markerEntryModels)
                onDataChanged(
                    markerEntryModels.first().entry.x.toInt(),
                    markerEntryModels.first().entry.y.toInt()
                )
            }
        }
    )
}

@Composable
fun DisplayDatesForColumn(pointsAmountText: Int, dateTextColumn: LocalDate, convertToRussian: (Int) -> String) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
    Text(
        text =
        (if (pointsAmountText != -1) "${convertToRussian(pointsAmountText)}, "
        else "") + dateTextColumn.format(formatter),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondary
    )
    Text(
        text = "${LocalDate.now().minusYears(1).format(formatter)} - ${
            LocalDate.now().format(formatter)
        }",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primaryContainer
    )
}

@Composable
fun DisplayDatesForLine(graphicTypes: GraphicTypes, dateText: LocalDate, dateBegin: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
    val formatterSide = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
    val formatterThreeMonths = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
    val formatterYear = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("ru"))
    val formatterYearFull = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
    when (graphicTypes) {
        GraphicTypes.Week -> Text(
            text = dateText.format(formatter),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary
        )

        GraphicTypes.Month -> Text(
            text = dateText.format(formatter),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary
        )

        GraphicTypes.ThreeMonths -> Text(
            text = "${
                dateText.format(
                    formatterThreeMonths
                )
            } - ${
                dateText.plusDays(6).format(formatterThreeMonths)
            } ${dateText.year}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary
        )

        GraphicTypes.HalfYear -> Text(
            text = "${dateText.format(formatterThreeMonths)} - ${
                dateText.plusDays(
                    6
                ).format(formatterThreeMonths)
            } ${dateText.year}", style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary
        )

        else -> Text(
            text = "${dateText.format(formatterYear)} - ${
                dateText.plusDays(
                    6
                ).format(formatterYear)
            }", style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
    Text(
        text = "${dateBegin.format(formatterYearFull)} - ${
            LocalDate.now().format(formatterYearFull)
        }", style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primaryContainer
    )
}

@Composable
private fun rememberNullLevel(customTypeface: Typeface): ThresholdLine {
    val line = shapeComponent(color = Color.Transparent, strokeColor = Color.Transparent)
    val label = textComponent(
        color = MaterialTheme.colorScheme.secondary,
        textSize = 12.sp,
        typeface = customTypeface
    )
    return remember(line, label) {
        ThresholdLine(
            thresholdValue = 0f,
            lineComponent = line,
            labelComponent = label,
            labelHorizontalPosition = ThresholdLine.LabelHorizontalPosition.Start,
            labelVerticalPosition = ThresholdLine.LabelVerticalPosition.Bottom
        )
    }
}

@Composable
fun ShowLineChart(
    datasetLineSpec: ArrayList<LineChart.LineSpec>,
    modelProducer: ChartEntryModelProducer,
    datasetDates: MutableList<LocalDate>,
    onChangeMarker: (LocalDate) -> Unit,
    userUiState: SignUpUiState,
    typeface: Typeface
) {
    val thresholdLine = rememberNullLevel(typeface)
    Chart(
        chart = lineChart(
            lines = datasetLineSpec,
            decorations = remember(thresholdLine) { listOf(thresholdLine) }
        ),
        chartModelProducer = modelProducer,
        isZoomEnabled = false,
        chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
        startAxis = rememberStartAxis(
            label = textComponent(
                color = MaterialTheme.colorScheme.secondary,
                textSize = 12.sp,
                typeface = typeface
            ),
            valueFormatter = { value, _ -> value.toInt().toString() },
            tickLength = 0.dp,
            tick = LineComponent(color = Color.Transparent.toArgb()),
            axis = LineComponent(color = Color.Transparent.toArgb()),
            verticalLabelPosition = VerticalAxis.VerticalLabelPosition.Top,
            horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
            itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 6),
            guideline = LineComponent(
                strokeColor = MaterialTheme.colorScheme.secondary.toArgb(),
                color = MaterialTheme.colorScheme.secondary.toArgb(),
                thicknessDp = 1f
            )
        ),
        horizontalLayout = HorizontalLayout.FullWidth(
            unscalableStartPaddingDp = 45f,
            unscalableEndPaddingDp = 20f
        ),
        bottomAxis = rememberBottomAxis(
            label = null,
            axis = LineComponent(
                color = MaterialTheme.colorScheme.secondary.toArgb(),
                thicknessDp = 1f
            ),
            tickLength = 0.dp,
            guideline = null
        ),
        marker = rememberMarker(age = Period.between(userUiState.birthday, LocalDate.now()).years, height = userUiState.height.toDouble().toInt()),
        markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
            // update data when on marker moved
            override fun onMarkerMoved(
                marker: Marker,
                markerEntryModels: List<Marker.EntryModel>,
            ) {
                super.onMarkerMoved(marker, markerEntryModels)
                onChangeMarker(datasetDates[markerEntryModels.first().entry.x.toInt()])
            }

            // update data on marker shown
            override fun onMarkerShown(
                marker: Marker,
                markerEntryModels: List<Marker.EntryModel>
            ) {
                super.onMarkerShown(marker, markerEntryModels)
                onChangeMarker(datasetDates[markerEntryModels.first().entry.x.toInt()])
            }
        }
    )
}