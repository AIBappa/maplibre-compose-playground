package com.maplibre.compose.callbacks

import android.graphics.PointF
import android.util.Log
import com.mapbox.geojson.Feature
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

enum class MapGestureType {
  TAP,
  LONG_PRESS
}

data class MapGestureContext(

  /** The screen location of the gesture. */
  val screenLocation: PointF,

  /** The type of gesture. */
  val type: MapGestureType,

  /** The coordinate of the gesture. */
  val coordinate: LatLng,

  /** The features at the gesture location. */
  val features: List<Feature>,

  // TODO: Bundle other relevant gesture context information here.
)

/**
 * Optionally adds gesture callbacks to the map.
 *
 * @param onTapGestureCallback The callback for a tap gesture.
 * @param onLongPressGestureCallback The callback for a long press gesture.
 */
internal fun MapboxMap.setupEventCallbacks(
  onTapGestureCallback: ((MapGestureContext) -> Unit)? = null,
  onLongPressGestureCallback: ((MapGestureContext) -> Unit)? = null,
) {
  onTapGestureCallback?.let {
    this.addOnMapClickListener { point ->
      val screenLocation = projection.toScreenLocation(point)
      val features = queryRenderedFeatures(screenLocation)

      onTapGestureCallback.invoke(
          MapGestureContext(
              screenLocation, MapGestureType.TAP, LatLng(point.latitude, point.longitude), features)
      )
      true
    }
  }

  onLongPressGestureCallback?.let {
    this.addOnMapLongClickListener { point ->
      val screenLocation = projection.toScreenLocation(point)
      val features = queryRenderedFeatures(screenLocation)

      onLongPressGestureCallback.invoke(
          MapGestureContext(
              screenLocation, MapGestureType.LONG_PRESS, LatLng(point.latitude, point.longitude), features)
      )
      true
    }
  }
}
