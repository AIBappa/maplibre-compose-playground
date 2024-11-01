package com.maplibre.example.examples

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.maplibre.compose.MapView
import com.maplibre.compose.StaticLocationEngine
import com.maplibre.compose.camera.CameraState
import com.maplibre.compose.camera.MapViewCamera
import com.maplibre.compose.camera.cameraPaddingFractionOfScreen
import com.maplibre.compose.camera.extensions.incrementZoom
import com.maplibre.compose.camera.models.CameraPadding
import com.maplibre.compose.rememberSaveableMapViewCamera
import com.maplibre.example.support.locationPermissions
import com.maplibre.example.support.rememberLocationPermissionLauncher

@Composable
fun CameraExample() {

  val locationEngine by lazy {
    val engine = StaticLocationEngine()
    engine.lastLocation =
        Location("static").apply {
          latitude = 66.137331
          longitude = -18.529602
        }
    engine
  }

  val canChangeCamera = remember { mutableStateOf(false) }

  val cameraPadding = cameraPaddingFractionOfScreen(top = 0.8f)

  val mapViewCamera = rememberSaveableMapViewCamera() // Or rememberMapViewCamera()
  val nextCameraState = getNextCamera(mapViewCamera.value.state, cameraPadding)

  // TODO: This could use improvement to handle failure cases.
  //      Not really in the scope of this project, but just to reduce
  //      challenges using the example project.
  //      (see logs for warnings about LocationEngine)
  val permissionLauncher =
      rememberLocationPermissionLauncher(
          onAccess = {
            canChangeCamera.value = true
            mapViewCamera.value = MapViewCamera.TrackingUserLocation()
          },
          onFailed = { Log.w("CameraExample", "Location permission denied") })

  Scaffold {
    Box(modifier = Modifier.padding(it)) {
      MapView(
          modifier = Modifier.fillMaxSize(),
          styleUrl = "https://demotiles.maplibre.org/style.json",
          camera = mapViewCamera,
          locationEngine = remember { locationEngine })

      Text(
          "Camera: ${mapViewCamera.value}",
          modifier =
              Modifier.align(Alignment.TopCenter).padding(top = 16.dp, start = 16.dp, end = 16.dp),
          fontSize = 11.sp,
          textAlign = TextAlign.Center)

      Button(
          onClick = {
            if (!canChangeCamera.value) {
              permissionLauncher.launch(locationPermissions)
              return@Button
            }

            mapViewCamera.value = getNextCamera(mapViewCamera.value.state, cameraPadding)
          },
          modifier =
              Modifier.align(Alignment.BottomStart)
                  .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)) {
            Text("To ${nextCameraState.state}")
          }

      Column(
          modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 150.dp, end = 16.dp),
          horizontalAlignment = Alignment.End) {
            Button(onClick = { mapViewCamera.value = mapViewCamera.value.incrementZoom(1.0) }) {
              Text("+")
            }

            Button(onClick = { mapViewCamera.value = mapViewCamera.value.incrementZoom(-1.0) }) {
              Text("-")
            }
          }
    }
  }
}

private fun getNextCamera(currentState: CameraState, padding: CameraPadding): MapViewCamera {
  return when (currentState) {
    is CameraState.Centered -> MapViewCamera.TrackingUserLocation(zoom = 18.0, pitch = 45.0)
    is CameraState.TrackingUserLocation ->
        MapViewCamera.TrackingUserLocationWithBearing(zoom = 18.0, pitch = 45.0, padding = padding)
    is CameraState.TrackingUserLocationWithBearing ->
        MapViewCamera.BoundingBox(
            LatLngBounds.Companion.from(47.8308275417, 10.4427014502, 45.7769477403, 6.02260949059),
            padding = CameraPadding(20.0, 20.0, 2.0, 20.0))
    is CameraState.BoundingBox -> MapViewCamera.Default
  }
}

// TODO: Can this work with the async map style?
@Composable
@Preview
fun CameraExamplePreview() {
  CameraExample()
}
