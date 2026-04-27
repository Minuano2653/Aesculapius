package com.example.aesculapius.ui.airquality.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun YandexMapView(
    modifier: Modifier = Modifier,
    initialLat: Double,
    initialLon: Double,
    initialZoom: Float = 14f,
    moveCameraTrigger: Triple<Double, Double, Float>? = null,
    onCameraIdle: (lat: Double, lon: Double) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val callback = rememberUpdatedState(onCameraIdle)

    val cameraListener = remember {
        CameraListener { _, position, _, _ ->
            callback.value(position.target.latitude, position.target.longitude)
        }
    }

    DisposableEffect(Unit) {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
        mapView.map.move(
            CameraPosition(Point(initialLat, initialLon), initialZoom, 0f, 0f)
        )
        mapView.map.addCameraListener(cameraListener)
        onDispose {
            mapView.map.removeCameraListener(cameraListener)
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    DisposableEffect(moveCameraTrigger) {
        moveCameraTrigger?.let { (lat, lon, zoom) ->
            mapView.map.move(CameraPosition(Point(lat, lon), zoom, 0f, 0f))
        }
        onDispose { }
    }

    AndroidView(factory = { mapView }, modifier = modifier)
}
