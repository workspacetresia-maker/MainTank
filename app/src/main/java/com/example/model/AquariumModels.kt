package com.example.model

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

enum class AquariumCondition(
  val label: String,
  val subtitle: String,
) {
  POLOS("Polos", "Tangki kosong tanpa dekorasi"),
  PASIR("Dengan Pasir", "Dikurangi volume lapisan pasir"),
  BATU("Dengan Batu", "Dikurangi volume berat batu"),
  KAYU("Dengan Kayu", "Dikurangi volume berat kayu"),
  PASIR_BATU("Pasir + Batu", "Kombinasi substrat pasir dengan batu"),
  PASIR_KAYU("Pasir + Kayu", "Kombinasi substrat pasir dengan kayu"),
  KOMBINASI("Ketiganya", "Kombinasi pasir, batu, dan kayu");

  val includesSand: Boolean
    get() =
      this == PASIR || this == PASIR_BATU || this == PASIR_KAYU || this == KOMBINASI

  val includesStone: Boolean
    get() = this == BATU || this == PASIR_BATU || this == KOMBINASI

  val includesWood: Boolean
    get() = this == KAYU || this == PASIR_KAYU || this == KOMBINASI
}

enum class SandType(
  val label: String,
  val solidFactor: Double,
  val kgPerLiter: Double,
  val hint: String,
) {
  GEOMETRIS("Penuh (100%)", 1.0, 1.50, "Sesuai volume geometris P × L × T"),
  PASIR_SILICA("Silica (65%)", 0.65, 1.55, "35% rongga antar butir terisi air"),
  PASIR_MALANG("Soil / Malang (55%)", 0.55, 1.15, "45% rongga berpori terisi air"),
}

enum class StoneSizePreset(
  val label: String,
  val weightGrams: Double,
) {
  KECIL("Kecil", 500.0),
  SEDANG("Sedang", 1500.0),
  BESAR("Besar", 3000.0),
  KUSTOM("Kustom", 1000.0),
}

enum class WoodSizePreset(
  val label: String,
  val weightGrams: Double,
) {
  KECIL("Kecil", 350.0),
  SEDANG("Sedang", 850.0),
  BESAR("Besar", 2000.0),
  KUSTOM("Kustom", 500.0),
}

data class TankPreset(
  val name: String,
  val lengthCm: String,
  val widthCm: String,
  val heightCm: String,
  val glassMm: Int,
)

val DEFAULT_TANK_PRESETS =
  listOf(
    TankPreset("Nano", "30", "20", "25", 5),
    TankPreset("Standar", "60", "30", "35", 6),
    TankPreset("90P", "90", "45", "45", 8),
    TankPreset("120P", "120", "50", "50", 10),
  )

enum class VolumeUnit(
  val label: String,
  val shortLabel: String,
  val factorFromLiter: Double,
) {
  LITER("Liter", "L", 1.0),
  US_GALLON("Galon US", "gal US", 0.264172052),
  IMPERIAL_GALLON("Galon Imperial", "gal UK", 0.219969248),
  CUBIC_CM("Sentimeter Kubik", "cm³", 1000.0);

  fun fromLiters(liters: Double): Double = liters * factorFromLiter
}

data class AquariumUiState(
  val activeProfileId: Long? = null,
  val activeProfileName: String? = null,
  val lengthCm: String = "60",
  val widthCm: String = "30",
  val heightCm: String = "35",
  val topGapCm: String = "3",
  val glassThicknessMm: Int = 0,
  val selectedCondition: AquariumCondition = AquariumCondition.POLOS,
  val selectedDisplayUnit: VolumeUnit = VolumeUnit.LITER,
  // Sand inputs (P x L x T)
  val sandSyncWithTank: Boolean = true,
  val sandLengthCm: String = "60",
  val sandWidthCm: String = "30",
  val sandHeightCm: String = "5",
  val sandType: SandType = SandType.GEOMETRIS,
  // Stone inputs (jumlah x berat dalam gram, dikonversi dengan densitas rata-rata 2.5 g/cm³)
  val stoneCount: Int = 3,
  val stonePreset: StoneSizePreset = StoneSizePreset.SEDANG,
  val stoneWeightGramsPerUnit: String = "1500",
  // Wood inputs (jumlah x berat dalam gram, dikonversi dengan densitas rata-rata 0.85 g/cm³ x % terendam)
  val woodCount: Int = 1,
  val woodPreset: WoodSizePreset = WoodSizePreset.SEDANG,
  val woodWeightGramsPerUnit: String = "850",
  val woodSubmergedPercent: Int = 100,
) {
  val calculation: AquariumCalculationResult
    get() = AquariumCalculator.calculate(this)
}

data class AquariumCalculationResult(
  val tankLengthCm: Double,
  val tankWidthCm: Double,
  val tankHeightCm: Double,
  val innerLengthCm: Double,
  val innerWidthCm: Double,
  val effectiveWaterHeightCm: Double,
  val grossVolumeLiters: Double,
  val bareWaterVolumeLiters: Double,
  val sandGeometricLiters: Double,
  val sandDisplacedLiters: Double,
  val sandEstimatedKg: Double,
  val sandEffectiveHeightCm: Double,
  val stoneDisplacedLiters: Double,
  val stoneTotalWeightGrams: Double,
  val stonePerUnitLiters: Double,
  val woodDisplacedLiters: Double,
  val woodTotalWeightGrams: Double,
  val woodPerUnitLiters: Double,
  val totalDecorationDisplacedLiters: Double,
  val netWaterVolumeLiters: Double,
  val netWaterVolumeGallons: Double,
  val netWaterVolumeImperialGallons: Double,
  val netWaterVolumeCubicCm: Double,
  val waterChange30Liters: Double,
  val waterChange50Liters: Double,
  val recommendedFilterLphMin: Int,
  val recommendedFilterLphMax: Int,
  val displacementPercent: Double,
  val fillPercentOfGross: Double,
  val isOverflowWarning: Boolean,
)

object AquariumCalculator {
  const val STONE_DENSITY_G_PER_CM3 = 2.5
  const val WOOD_DENSITY_G_PER_CM3 = 0.85

  fun parsePositiveDouble(raw: String): Double {
    val normalized = raw.trim().replace(',', '.')
    return normalized.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
  }

  fun calculate(state: AquariumUiState): AquariumCalculationResult {
    val tankL = parsePositiveDouble(state.lengthCm)
    val tankW = parsePositiveDouble(state.widthCm)
    val tankH = parsePositiveDouble(state.heightCm)
    val topGap = parsePositiveDouble(state.topGapCm)
    val glassCm = state.glassThicknessMm.coerceAtLeast(0) / 10.0

    val innerL = max(0.0, tankL - 2.0 * glassCm)
    val innerW = max(0.0, tankW - 2.0 * glassCm)
    val effectiveWaterHeight = max(0.0, tankH - glassCm - topGap)

    val grossVolumeLiters = (tankL * tankW * tankH) / 1000.0
    val bareWaterVolumeLiters = (innerL * innerW * effectiveWaterHeight) / 1000.0

    // Sand calculation (P x L x T)
    val rawSandL =
      if (state.sandSyncWithTank) innerL else parsePositiveDouble(state.sandLengthCm)
    val rawSandW =
      if (state.sandSyncWithTank) innerW else parsePositiveDouble(state.sandWidthCm)
    val rawSandH = parsePositiveDouble(state.sandHeightCm)

    val sandGeometricLiters =
      if (state.selectedCondition.includesSand) {
        (rawSandL * rawSandW * rawSandH) / 1000.0
      } else {
        0.0
      }
    val sandDisplacedLiters = sandGeometricLiters * state.sandType.solidFactor
    val sandEstimatedKg = sandGeometricLiters * state.sandType.kgPerLiter

    // Stone calculation: Volume (Liter) = Berat (gram) / (1000 * densitas 2.5 g/cm³)
    val gramsPerUnit = parsePositiveDouble(state.stoneWeightGramsPerUnit)
    val stonePerUnitLiters = gramsPerUnit / (1000.0 * STONE_DENSITY_G_PER_CM3)
    val stoneTotalWeightGrams =
      if (state.selectedCondition.includesStone) {
        state.stoneCount.coerceAtLeast(0) * gramsPerUnit
      } else {
        0.0
      }
    val stoneDisplacedLiters =
      if (state.selectedCondition.includesStone) {
        state.stoneCount.coerceAtLeast(0) * stonePerUnitLiters
      } else {
        0.0
      }

    // Wood calculation: Volume (Liter) = (Berat (gram) / (1000 * densitas 0.85 g/cm³)) * % terendam
    val woodGramsPerUnit = parsePositiveDouble(state.woodWeightGramsPerUnit)
    val woodSubmergedFactor = state.woodSubmergedPercent.coerceIn(0, 100) / 100.0
    val woodPerUnitLiters =
      (woodGramsPerUnit / (1000.0 * WOOD_DENSITY_G_PER_CM3)) * woodSubmergedFactor
    val woodTotalWeightGrams =
      if (state.selectedCondition.includesWood) {
        state.woodCount.coerceAtLeast(0) * woodGramsPerUnit
      } else {
        0.0
      }
    val woodDisplacedLiters =
      if (state.selectedCondition.includesWood) {
        state.woodCount.coerceAtLeast(0) * woodPerUnitLiters
      } else {
        0.0
      }

    val totalDecorationDisplaced = sandDisplacedLiters + stoneDisplacedLiters + woodDisplacedLiters
    val netWaterLiters = max(0.0, bareWaterVolumeLiters - totalDecorationDisplaced)
    val netGallons = VolumeUnit.US_GALLON.fromLiters(netWaterLiters)
    val netImperialGallons = VolumeUnit.IMPERIAL_GALLON.fromLiters(netWaterLiters)
    val netCubicCm = VolumeUnit.CUBIC_CM.fromLiters(netWaterLiters)

    val displacementPct =
      if (bareWaterVolumeLiters > 0.0) {
        min(100.0, (totalDecorationDisplaced / bareWaterVolumeLiters) * 100.0)
      } else {
        0.0
      }

    val fillPctOfGross =
      if (grossVolumeLiters > 0.0) {
        min(100.0, (netWaterLiters / grossVolumeLiters) * 100.0)
      } else {
        0.0
      }

    val isOverflow =
      bareWaterVolumeLiters > 0.0 && totalDecorationDisplaced >= bareWaterVolumeLiters

    return AquariumCalculationResult(
      tankLengthCm = tankL,
      tankWidthCm = tankW,
      tankHeightCm = tankH,
      innerLengthCm = innerL,
      innerWidthCm = innerW,
      effectiveWaterHeightCm = effectiveWaterHeight,
      grossVolumeLiters = grossVolumeLiters,
      bareWaterVolumeLiters = bareWaterVolumeLiters,
      sandGeometricLiters = sandGeometricLiters,
      sandDisplacedLiters = sandDisplacedLiters,
      sandEstimatedKg = sandEstimatedKg,
      sandEffectiveHeightCm = if (state.selectedCondition.includesSand) rawSandH else 0.0,
      stoneDisplacedLiters = stoneDisplacedLiters,
      stoneTotalWeightGrams = stoneTotalWeightGrams,
      stonePerUnitLiters = stonePerUnitLiters,
      woodDisplacedLiters = woodDisplacedLiters,
      woodTotalWeightGrams = woodTotalWeightGrams,
      woodPerUnitLiters = woodPerUnitLiters,
      totalDecorationDisplacedLiters = totalDecorationDisplaced,
      netWaterVolumeLiters = netWaterLiters,
      netWaterVolumeGallons = netGallons,
      netWaterVolumeImperialGallons = netImperialGallons,
      netWaterVolumeCubicCm = netCubicCm,
      waterChange30Liters = netWaterLiters * 0.30,
      waterChange50Liters = netWaterLiters * 0.50,
      recommendedFilterLphMin = (netWaterLiters * 5.0).roundToInt(),
      recommendedFilterLphMax = (netWaterLiters * 8.0).roundToInt(),
      displacementPercent = displacementPct,
      fillPercentOfGross = fillPctOfGross,
      isOverflowWarning = isOverflow,
    )
  }
}
