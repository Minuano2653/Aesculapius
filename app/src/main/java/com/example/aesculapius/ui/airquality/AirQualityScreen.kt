package com.example.aesculapius.ui.airquality

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aesculapius.R
import com.example.aesculapius.ui.TopBar
import com.example.aesculapius.ui.airquality.components.AqiComponentsGrid
import com.example.aesculapius.ui.airquality.components.AqiHeaderCard
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

object AirQualityScreen : NavigationDestination {
    override val route = "AirQualityScreen"
}

@Composable
fun AirQualityScreen(
    onNavigateBack: () -> Unit,
    onEditLocation: () -> Unit,
    viewModel: AirQualityViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cache by viewModel.cache.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AirQualityEffect.RefreshFailed -> Toast.makeText(
                    context,
                    context.getString(R.string.aqi_error_load),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateBack = onNavigateBack,
                text = stringResource(R.string.air_quality_index),
                rightIcon = painterResource(R.drawable.edit_icon),
                onClickRightIcon = onEditLocation
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val location = cache?.location
        val airQuality = cache?.airQuality

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(AirQualityEvent.OnRefresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            indicator = { state, refreshTrigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTrigger,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (location == null) {
                    EmptyState(onEditLocation = onEditLocation)
                    return@Column
                }
                if (airQuality == null) {
                    NoDataState(
                        locationName = location.name,
                        onRetry = { viewModel.onEvent(AirQualityEvent.OnRefresh) }
                    )
                    return@Column
                }
                AqiHeaderCard(
                    aqi = airQuality.aqi,
                    locationName = location.name.ifBlank { stringResource(R.string.aqi_unknown_location) },
                    modifier = Modifier.padding(top = 16.dp)
                )
                AqiComponentsGrid(components = airQuality.components)
            }
        }
    }
}

@Composable
private fun EmptyState(onEditLocation: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.aqi_unknown_location),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            Button(
                onClick = onEditLocation,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.choose_place))
            }
        }
    }
}

@Composable
private fun NoDataState(locationName: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = locationName.ifBlank { stringResource(R.string.aqi_unknown_location) },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            text = stringResource(R.string.aqi_no_data),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(top = 12.dp)
        )
        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = stringResource(R.string.aqi_retry))
        }
    }
}
