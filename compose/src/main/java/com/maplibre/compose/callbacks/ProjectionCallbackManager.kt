package com.maplibre.compose.callbacks

import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.maplibre.compose.runtime.screenWidthPx

data class PixelListener(
  val source: PointF,
  val onChange: (LatLng) -> Unit
)

data class LatLngListener(
  val source: LatLng,
  val onChange: (PointF) -> Unit
)

class ProjectionCallbackManager {

  private val pixelListeners = mutableListOf<PixelListener>()
  private val latLngListeners = mutableListOf<LatLngListener>()

  fun addPixelToLatLngListener(source: PointF, onChange: (LatLng) -> Unit) {
    pixelListeners.add(PixelListener(source, onChange))
  }

  fun addLatLngToPixelListener(source: LatLng, onChange: (PointF) -> Unit) {
    latLngListeners.add(LatLngListener(source, onChange))
  }

  @Composable
  fun addPixelDpToLatLngListener(source: DpSize, density: Density, onChange: (LatLng) -> Unit) {
    density.run {
      val pixelWidth =

      val pixelSource = PointF(screenWidthPx source.width), toPx(source.height))
      pixelListeners.add(PixelListener(source = pixelSource, onChange))
    }

    val pixelSource = PointF(density.toPx(source.width), density.toPx(source.height))
    pixelListeners.add(PixelListener(source = pixelSource, onChange))
  }

  @Composable
  fun addLatLngToPixelDpListener(source: LatLng, onChange: (DpSize) -> Unit) {
    latLngListeners.add(LatLngListener(source, onChange))
  }

  internal fun onMapViewCameraChanged(mapboxMap: MapboxMap) {
    // This behavior has been profiled and does not seem to impact CPU usage.
    // We could consider debouncing this behavior if an issue is reported.
    val projection = mapboxMap.projection

    pixelListeners.forEach { listener ->
      val latLng = projection.fromScreenLocation(listener.source)
      listener.onChange(latLng)
    }

    latLngListeners.forEach { listener ->
      val pixel = projection.toScreenLocation(listener.source)
      listener.onChange(pixel)
    }
  }
}