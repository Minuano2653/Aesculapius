package com.example.aesculapius.ui.airquality

import android.Manifest
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aesculapius.R
import com.example.aesculapius.ui.TopBar
import com.example.aesculapius.ui.airquality.components.CenterMarkerOverlay
import com.example.aesculapius.ui.airquality.components.YandexMapView
import com.example.aesculapius.ui.airquality.util.hasLocationPermission
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

object LocationPickerScreen : NavigationDestination {
    override val route = "LocationPickerScreen"
}

@Composable
fun LocationPickerScreen(
    onNavigateBack: () -> Unit,
    onLocationSaved: () -> Unit,
    viewModel: LocationPickerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val moveCameraTrigger = remember { mutableStateOf<Triple<Double, Double, Float>?>(null) }

    val resolutionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onEvent(LocationPickerEvent.OnRequestCurrentLocation)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.enable_location_services),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            ensureLocationServicesEnabled(
                context = context,
                resolutionLauncher = resolutionLauncher,
                onReady = { viewModel.onEvent(LocationPickerEvent.OnRequestCurrentLocation) }
            )
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.location_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LocationPickerEffect.ShowError -> {
                    val msgRes = when (effect.message) {
                        "location_unavailable" -> R.string.location_unavailable
                        else -> R.string.something_went_wrong
                    }
                    Toast.makeText(context, context.getString(msgRes), Toast.LENGTH_SHORT).show()
                }
                LocationPickerEffect.LocationSaved -> onLocationSaved()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.moveCameraTo.collect { (lat, lon) ->
            moveCameraTrigger.value = Triple(lat, lon, 14f)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateBack = onNavigateBack,
                text = stringResource(R.string.select_location_title)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (!uiState.isInitialised) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues))
            return@Scaffold
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            YandexMapView(
                modifier = Modifier.fillMaxSize(),
                initialLat = uiState.lat,
                initialLon = uiState.lon,
                moveCameraTrigger = moveCameraTrigger.value,
                onCameraIdle = { lat, lon ->
                    viewModel.onEvent(LocationPickerEvent.OnCameraIdle(lat, lon))
                }
            )
            CenterMarkerOverlay()

            BottomControls(
                currentName = uiState.currentName,
                isLoadingName = uiState.isLoadingName,
                onConfirm = { viewModel.onEvent(LocationPickerEvent.OnConfirmLocation) },
                onClickMyLocation = {
                    if (context.hasLocationPermission()) {
                        ensureLocationServicesEnabled(
                            context = context,
                            resolutionLauncher = resolutionLauncher,
                            onReady = { viewModel.onEvent(LocationPickerEvent.OnRequestCurrentLocation) }
                        )
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun BottomControls(
    currentName: String,
    isLoadingName: Boolean,
    onConfirm: () -> Unit,
    onClickMyLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onClickMyLocation),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.my_location_icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.choose_place),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                val secondary = when {
                    isLoadingName -> stringResource(R.string.aqi_loading_location)
                    currentName.isBlank() -> stringResource(R.string.aqi_unknown_location)
                    else -> currentName
                }
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun ensureLocationServicesEnabled(
    context: Context,
    resolutionLauncher: ActivityResultLauncher<IntentSenderRequest>,
    onReady: () -> Unit
) {
    val request = LocationSettingsRequest.Builder()
        .addLocationRequest(
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0L).build()
        )
        .build()
    LocationServices.getSettingsClient(context)
        .checkLocationSettings(request)
        .addOnSuccessListener { onReady() }
        .addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                runCatching {
                    resolutionLauncher.launch(
                        IntentSenderRequest.Builder(e.resolution).build()
                    )
                }.onFailure {
                    Toast.makeText(
                        context,
                        context.getString(R.string.enable_location_services),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.enable_location_services),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}
