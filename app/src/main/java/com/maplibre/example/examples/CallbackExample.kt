package com.maplibre.example.examples

import android.graphics.PointF
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mapbox.mapboxsdk.geometry.LatLng
import com.maplibre.compose.MapView
import com.maplibre.compose.callbacks.ProjectionCallbackManager
import com.maplibre.compose.camera.MapViewCamera
import com.maplibre.compose.rememberSaveableMapViewCamera
import kotlinx.coroutines.launch

@Composable
fun CallbackExample() {

  val TAG = "CallbackExample"

  //    https://developer.android.com/develop/ui/compose/components/snackbar#basic_example

  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  fun getProjectionCallbackManager(): ProjectionCallbackManager {
    val projectionCallbackManager = ProjectionCallbackManager()
    projectionCallbackManager.addLatLngToPixelListener(
      LatLng(57.636576, -155.031807),
      onChange = { pixel -> Log.d(TAG, "LatLng at pixel: $pixel") }
    )
    projectionCallbackManager.addPixelToLatLngListener(
      PointF(15f, 15f),
      onChange = { location -> Log.d(TAG, "Pixel is now at location: $location") }
    )
    return projectionCallbackManager
  }

  val mapViewCamera =
      rememberSaveableMapViewCamera(
          initialCamera =
              MapViewCamera.Centered(latitude = 57.636576, longitude = -155.031807, zoom = 6.0))

  Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
    Box(modifier = Modifier.padding(it)) {
      MapView(
          modifier = Modifier.fillMaxSize(),
          styleUrl = "https://demotiles.maplibre.org/style.json",
          camera = mapViewCamera,
          onMapReadyCallback = { scope.launch { snackbarHostState.showSnackbar("Map ready!") } },
          onTapGestureCallback = {
            scope.launch { snackbarHostState.showSnackbar("Tapped at ${it.coordinate}") }
          },
          onLongPressGestureCallback = {
            scope.launch { snackbarHostState.showSnackbar("Long pressed at ${it.coordinate}") }
          },
          projectionCallbackManager = getProjectionCallbackManager())
    }
  }
}

// TODO: Can this work with the async map style?
@Composable
@Preview
fun CallbackExamplePreview() {
  CallbackExample()
}
