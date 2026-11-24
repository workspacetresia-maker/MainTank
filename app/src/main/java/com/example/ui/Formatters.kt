package com.example.ui

import com.example.model.VolumeUnit
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

fun formatLiters(value: Double, decimals: Int = 2): String {
  val safe = if (value.isNaN() || value.isInfinite()) 0.0 else value
  return String.format(Locale.US, "%.${decimals}f", safe)
}

fun formatVolumeForUnit(liters: Double, unit: VolumeUnit): String {
  val converted = unit.fromLiters(if (liters.isNaN() || liters.isInfinite()) 0.0 else liters)
  return when (unit) {
    VolumeUnit.CUBIC_CM -> {
      val df = DecimalFormat("#,##0.#", DecimalFormatSymbols(Locale.US))
      df.format(converted)
    }
    VolumeUnit.LITER,
    VolumeUnit.US_GALLON,
    VolumeUnit.IMPERIAL_GALLON -> formatLiters(converted, 2)
  }
}

fun formatCompactDecimal(value: Double, maxDecimals: Int = 1): String {
  val safe = if (value.isNaN() || value.isInfinite()) 0.0 else value
  if (abs(safe - safe.toLong()) < 0.005) {
    return safe.toLong().toString()
  }
  return String.format(Locale.US, "%.${maxDecimals}f", safe)
    .trimEnd('0')
    .trimEnd('.')
}
