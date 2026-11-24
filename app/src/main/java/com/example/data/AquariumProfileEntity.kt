package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AquariumCondition
import com.example.model.AquariumUiState
import com.example.model.SandType
import com.example.model.StoneSizePreset
import com.example.model.VolumeUnit
import com.example.model.WoodSizePreset
import com.example.ui.formatLiters
import com.example.ui.formatVolumeForUnit

@Entity(tableName = "aquarium_profiles")
data class AquariumProfileEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val profileName: String,
  val conditionName: String,
  val lengthCm: String,
  val widthCm: String,
  val heightCm: String,
  val topGapCm: String,
  val glassThicknessMm: Int,
  val sandSyncWithTank: Boolean,
  val sandLengthCm: String,
  val sandWidthCm: String,
  val sandHeightCm: String,
  val sandTypeName: String,
  val stoneInputModeName: String = "GRAM",
  val stoneTypeName: String = "AVERAGE",
  val stoneCount: Int,
  val stonePresetName: String,
  val stoneWeightGramsPerUnit: String = "1500",
  val stoneVolumePerUnitLiters: String = "0.6",
  val woodCount: Int,
  val woodPresetName: String,
  val woodWeightGramsPerUnit: String = "850",
  val woodVolumePerUnitLiters: String = "1.0",
  val woodSubmergedPercent: Int,
  val netWaterVolumeLiters: Double,
  val bareWaterVolumeLiters: Double,
  val totalDecorationDisplacedLiters: Double,
  val updatedAtMillis: Long = System.currentTimeMillis(),
) {
  val condition: AquariumCondition
    get() =
      AquariumCondition.entries.firstOrNull { it.name == conditionName }
        ?: AquariumCondition.POLOS

  fun toShareSummaryText(): String = buildString {
    appendLine("MainTank - $profileName")
    appendLine("Dimensi: $lengthCm × $widthCm × $heightCm cm")
    if (glassThicknessMm > 0) {
      appendLine("Tebal Kaca: $glassThicknessMm mm | Ruang Atas: $topGapCm cm")
    } else {
      appendLine("Ruang Kosong Atas: $topGapCm cm")
    }
    appendLine("Kondisi: ${condition.label}")
    if (condition.includesSand) {
      appendLine("• Pasir: $sandLengthCm × $sandWidthCm × $sandHeightCm cm")
    }
    if (condition.includesStone) {
      appendLine("• Batu: $stoneCount buah (${stoneWeightGramsPerUnit} g/buah)")
    }
    if (condition.includesWood) {
      appendLine(
        "• Kayu: $woodCount potong (${woodWeightGramsPerUnit} g/potong, terendam $woodSubmergedPercent%)"
      )
    }
    appendLine()
    appendLine("Volume Air Bersih:")
    appendLine("• ${formatVolumeForUnit(netWaterVolumeLiters, VolumeUnit.LITER)} Liter")
    appendLine("• ${formatVolumeForUnit(netWaterVolumeLiters, VolumeUnit.US_GALLON)} Galon US")
    appendLine(
      "• ${formatVolumeForUnit(netWaterVolumeLiters, VolumeUnit.IMPERIAL_GALLON)} Galon Imperial"
    )
    appendLine("• ${formatVolumeForUnit(netWaterVolumeLiters, VolumeUnit.CUBIC_CM)} cm³")
    if (totalDecorationDisplacedLiters > 0.0) {
      appendLine(
        "Volume Polos: ${formatLiters(bareWaterVolumeLiters)} L (Dekorasi: -${formatLiters(totalDecorationDisplacedLiters)} L)"
      )
    }
    append("Ganti Air 30%: ${formatLiters(netWaterVolumeLiters * 0.30, 1)} L")
  }

  fun toUiState(): AquariumUiState {
    val parsedCondition = condition
    val parsedSandType =
      SandType.entries.firstOrNull { it.name == sandTypeName } ?: SandType.GEOMETRIS
    val parsedStonePreset =
      StoneSizePreset.entries.firstOrNull { it.name == stonePresetName }
        ?: StoneSizePreset.SEDANG
    val parsedWoodPreset =
      WoodSizePreset.entries.firstOrNull { it.name == woodPresetName }
        ?: WoodSizePreset.SEDANG

    return AquariumUiState(
      lengthCm = lengthCm,
      widthCm = widthCm,
      heightCm = heightCm,
      topGapCm = topGapCm,
      glassThicknessMm = glassThicknessMm,
      selectedCondition = parsedCondition,
      sandSyncWithTank = sandSyncWithTank,
      sandLengthCm = sandLengthCm,
      sandWidthCm = sandWidthCm,
      sandHeightCm = sandHeightCm,
      sandType = parsedSandType,
      stoneCount = stoneCount,
      stonePreset = parsedStonePreset,
      stoneWeightGramsPerUnit = stoneWeightGramsPerUnit,
      woodCount = woodCount,
      woodPreset = parsedWoodPreset,
      woodWeightGramsPerUnit = woodWeightGramsPerUnit,
      woodSubmergedPercent = woodSubmergedPercent,
    )
  }

  companion object {
    fun fromUiState(
      profileName: String,
      state: AquariumUiState,
      existingId: Long = 0,
    ): AquariumProfileEntity {
      val result = state.calculation
      return AquariumProfileEntity(
        id = existingId,
        profileName = profileName.trim(),
        conditionName = state.selectedCondition.name,
        lengthCm = state.lengthCm,
        widthCm = state.widthCm,
        heightCm = state.heightCm,
        topGapCm = state.topGapCm,
        glassThicknessMm = state.glassThicknessMm,
        sandSyncWithTank = state.sandSyncWithTank,
        sandLengthCm = state.sandLengthCm,
        sandWidthCm = state.sandWidthCm,
        sandHeightCm = state.sandHeightCm,
        sandTypeName = state.sandType.name,
        stoneInputModeName = "GRAM",
        stoneTypeName = "AVERAGE",
        stoneCount = state.stoneCount,
        stonePresetName = state.stonePreset.name,
        stoneWeightGramsPerUnit = state.stoneWeightGramsPerUnit,
        stoneVolumePerUnitLiters = formatLiters(result.stonePerUnitLiters),
        woodCount = state.woodCount,
        woodPresetName = state.woodPreset.name,
        woodWeightGramsPerUnit = state.woodWeightGramsPerUnit,
        woodVolumePerUnitLiters = formatLiters(result.woodPerUnitLiters),
        woodSubmergedPercent = state.woodSubmergedPercent,
        netWaterVolumeLiters = result.netWaterVolumeLiters,
        bareWaterVolumeLiters = result.bareWaterVolumeLiters,
        totalDecorationDisplacedLiters = result.totalDecorationDisplacedLiters,
        updatedAtMillis = System.currentTimeMillis(),
      )
    }
  }
}
