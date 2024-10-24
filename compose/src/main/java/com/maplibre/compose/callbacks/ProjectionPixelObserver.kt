package com.maplibre.compose.callbacks

import android.graphics.PointF
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

data class PixelListener(
  val source: PointF,
  val onChange: (LatLng) -> Void
)

data class LatLngListener(
  val source: LatLng,
  val onChange: (PointF) -> Void
)

class ProjectionPixelObserver {

  private val pixelListeners = mutableListOf<PixelListener>()
  private val latLngListeners = mutableListOf<LatLngListener>()

  fun onMapViewChange(mapboxMap: MapboxMap) {
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

  fun addPixelToLatLngListener(source: PointF, onChange: (LatLng) -> Void) {
    pixelListeners.add(PixelListener(source, onChange))
  }

  fun addLatLngToPixelListener(source: LatLng, onChange: (PointF) -> Void) {
    latLngListeners.add(LatLngListener(source, onChange)
    )
  }
}