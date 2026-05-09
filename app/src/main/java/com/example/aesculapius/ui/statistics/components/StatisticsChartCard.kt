package com.example.aesculapius.ui.statistics.components

import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.aesculapius.R
import com.example.aesculapius.data.graphicsNavigationItemContentList
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.ui.statistics.GraphicTypes
import com.example.aesculapius.ui.statistics.StatisticsEvent
import com.example.aesculapius.ui.statistics.StatisticsUiState
import com.example.aesculapius.ui.statistics.rememberColumnMarker
import com.example.aesculapius.ui.statistics.rememberMarker
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
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.context.DrawContext
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StatisticsChartCard(
    state: StatisticsUiState,
    chartEntryModelLine: ChartEntryModelProducer,
    chartEntryModelColumn: ChartEntryModelProducer,
    datesForLineChart: List<LocalDate>,
    datesForColumnChart: List<LocalDate>,
    userUiState: SignUpUiState,
    convertToRussian: (Int) -> String,
    onEvent: (StatisticsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = if (state.isLineChart) stringResource(R.string.metrics_name)
                else stringResource(R.string.ast_test_name),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(vertical = 22.dp)
                    .fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    //.padding(bottom = 24.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (state.isLineChart) {
                        DisplayDatesForLine(
                            graphicTypes = state.selectedPeriod.graphicTypes,
                            dateText = state.dateTextLine,
                            dateBegin = state.dateBegin
                        )
                    } else {
                        DisplayDatesForColumn(
                            pointsAmountText = state.pointsAmountText,
                            dateTextColumn = state.dateTextColumn,
                            convertToRussian = convertToRussian
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                ChartTypeToggle(
                    isLineChart = state.isLineChart,
                    onToggle = { onEvent(StatisticsEvent.OnChartTypeToggled) }
                )
            }

            val context = LocalContext.current
            val customTypeface = remember(context) {
                Typeface.createFromAsset(context.assets, "inter_regular.ttf")
            }

            ProvideChartStyle {
                ChartArea(
                    state = state,
                    chartEntryModelLine = chartEntryModelLine,
                    chartEntryModelColumn = chartEntryModelColumn,
                    datesForLineChart = datesForLineChart,
                    datesForColumnChart = datesForColumnChart,
                    userUiState = userUiState,
                    typeface = customTypeface,
                    onEvent = onEvent
                )
            }

            if (state.isLineChart) {
                Row(
                    modifier = Modifier
                        .padding(top = 32.dp, bottom = 16.dp, start = 3.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    graphicsNavigationItemContentList.forEach { period ->
                        val isSelected = period == state.selectedPeriod
                        Card(
                            modifier = Modifier
                                .height(32.dp)
                                .wrapContentWidth()
                                .clickable { onEvent(StatisticsEvent.OnPeriodSelected(period)) }
                                .padding(end = 3.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.tertiary,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer
                                else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = stringResource(id = period.nameOfType),
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
        }
    }
}

@Composable
private fun ChartArea(
    state: StatisticsUiState,
    chartEntryModelLine: ChartEntryModelProducer,
    chartEntryModelColumn: ChartEntryModelProducer,
    datesForLineChart: List<LocalDate>,
    datesForColumnChart: List<LocalDate>,
    userUiState: SignUpUiState,
    typeface: Typeface,
    onEvent: (StatisticsEvent) -> Unit
) {
    val period = state.selectedPeriod.graphicTypes
    val isLineChart = state.isLineChart
    val tooFewLineShort = state.linePointsAmount < 1 &&
        (period == GraphicTypes.Week || period == GraphicTypes.Month)
    val tooFewLineLong = state.linePointsAmount < 7 &&
        (period == GraphicTypes.ThreeMonths || period == GraphicTypes.HalfYear || period == GraphicTypes.Year)

    when {
        isLineChart && (tooFewLineShort || tooFewLineLong) -> NoDataPlaceholder()
        isLineChart -> {
            val datasetLineSpec = remember(MaterialTheme.colorScheme.primary) {
                arrayListOf(
                    LineChart.LineSpec(
                        lineColor = primary.toArgb(),
                        lineThicknessDp = 4f
                    )
                )
            }
            ShowLineChart(
                datasetDates = datesForLineChart,
                modelProducer = chartEntryModelLine,
                datasetLineSpec = datasetLineSpec,
                onChangeMarker = { onEvent(StatisticsEvent.OnLineMarkerMoved(it)) },
                typeface = typeface,
                userUiState = userUiState
            )
        }
        !isLineChart && state.columnPointsAmount < 1 -> NoDataPlaceholder()
        else -> ShowColumnChart(
            typeface = typeface,
            onDataChanged = { x, y ->
                onEvent(StatisticsEvent.OnColumnMarkerMoved(xIndex = x, score = y))
            },
            modelProducerColumn = chartEntryModelColumn,
            amountPoints = datesForColumnChart.size
        )
    }
}

@Composable
private fun NoDataPlaceholder() {
    Box(
        modifier = Modifier
            .heightIn(min = 200.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.no_data),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ChartTypeToggle(
    isLineChart: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            ToggleIcon(
                iconRes = R.drawable.line_graphic_icon,
                selected = isLineChart,
                onClick = onToggle
            )
            ToggleIcon(
                iconRes = R.drawable.bar_chart_icon,
                selected = !isLineChart,
                onClick = onToggle
            )
        }
    }
}

@Composable
private fun ToggleIcon(
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .size(44.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { if (!selected) onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DisplayDatesForLine(
    graphicTypes: GraphicTypes,
    dateText: LocalDate,
    dateBegin: LocalDate
) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
    val formatterThreeMonths = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
    val formatterYear = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("ru"))
    val formatterYearFull = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))

    val primaryText = when (graphicTypes) {
        GraphicTypes.Week, GraphicTypes.Month -> dateText.format(formatter)
        GraphicTypes.ThreeMonths, GraphicTypes.HalfYear ->
            "${dateText.format(formatterThreeMonths)} - ${
                dateText.plusDays(6).format(formatterThreeMonths)
            } ${dateText.year}"
        GraphicTypes.Year ->
            "${dateText.format(formatterYear)} - ${
                dateText.plusDays(6).format(formatterYear)
            }"
    }

    Text(
        text = primaryText,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondary
    )
    Text(
        text = "${dateBegin.format(formatterYearFull)} - ${LocalDate.now().format(formatterYearFull)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primaryContainer
    )
}

@Composable
private fun DisplayDatesForColumn(
    pointsAmountText: Int,
    dateTextColumn: LocalDate,
    convertToRussian: (Int) -> String
) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
    val prefix = if (pointsAmountText != -1) "${convertToRussian(pointsAmountText)}, " else ""
    Text(
        text = prefix + dateTextColumn.format(formatter),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondary
    )
    Text(
        text = "${LocalDate.now().minusYears(1).format(formatter)} - ${LocalDate.now().format(formatter)}",
        style = MaterialTheme.typography.bodySmall,
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
private fun ShowLineChart(
    datasetLineSpec: ArrayList<LineChart.LineSpec>,
    modelProducer: ChartEntryModelProducer,
    datasetDates: List<LocalDate>,
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
        marker = rememberMarker(
            age = java.time.Period.between(userUiState.birthday, LocalDate.now()).years,
            height = userUiState.height.toDouble().toInt()
        ),
        markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
            override fun onMarkerMoved(
                marker: Marker,
                markerEntryModels: List<Marker.EntryModel>
            ) {
                super.onMarkerMoved(marker, markerEntryModels)
                val index = markerEntryModels.first().entry.x.toInt()
                if (index in datasetDates.indices) onChangeMarker(datasetDates[index])
            }

            override fun onMarkerShown(
                marker: Marker,
                markerEntryModels: List<Marker.EntryModel>
            ) {
                super.onMarkerShown(marker, markerEntryModels)
                val index = markerEntryModels.first().entry.x.toInt()
                if (index in datasetDates.indices) onChangeMarker(datasetDates[index])
            }
        }
    )
}

@Composable
private fun ShowColumnChart(
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
                            opacity: Float
                        ) {
                            super.drawVertical(context, top, bottom, centerX, 1f, opacity)
                        }
                    }
                }
            },
            decorations = remember(thresholdLineNullLevel) { listOf(thresholdLineNullLevel) },
            spacing = if (amountPoints > 1) (14 + (12 - amountPoints) * (14 / (amountPoints - 1))).dp
            else 500.dp
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
        marker = rememberColumnMarker(),
        markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
            override fun onMarkerMoved(
                marker: Marker,
                markerEntryModels: List<Marker.EntryModel>
            ) {
                super.onMarkerMoved(marker, markerEntryModels)
                onDataChanged(
                    markerEntryModels.first().entry.x.toInt(),
                    markerEntryModels.first().entry.y.toInt()
                )
            }

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
