package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AquariumCalculationResult
import com.example.model.AquariumUiState
import com.example.model.VolumeUnit
import com.example.ui.formatCompactDecimal
import com.example.ui.formatLiters
import com.example.ui.formatVolumeForUnit
import com.example.ui.theme.LocalAquaColors

@Composable
fun BreakdownCard(
  state: AquariumUiState,
  result: AquariumCalculationResult,
  onSelectDisplayUnit: (VolumeUnit) -> Unit,
  modifier: Modifier = Modifier,
) {
  val aquaColors = LocalAquaColors.current
  val condition = state.selectedCondition

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Unit Conversion Grid Card
    ElevatedCard(
      modifier = Modifier.fillMaxWidth(),
      shape = MaterialTheme.shapes.large,
      colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Icon(
            imageVector = Icons.Rounded.SwapHoriz,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
          )
          Column {
            Text(
              text = "Konversi Satuan Volume",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = "Ketuk kartu satuan untuk mengubah tampilan utama",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            UnitConversionTile(
              unit = VolumeUnit.LITER,
              formattedValue = formatVolumeForUnit(result.netWaterVolumeLiters, VolumeUnit.LITER),
              isSelected = state.selectedDisplayUnit == VolumeUnit.LITER,
              onClick = { onSelectDisplayUnit(VolumeUnit.LITER) },
              modifier = Modifier.weight(1f),
            )
            UnitConversionTile(
              unit = VolumeUnit.US_GALLON,
              formattedValue =
                formatVolumeForUnit(result.netWaterVolumeLiters, VolumeUnit.US_GALLON),
              isSelected = state.selectedDisplayUnit == VolumeUnit.US_GALLON,
              onClick = { onSelectDisplayUnit(VolumeUnit.US_GALLON) },
              modifier = Modifier.weight(1f),
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            UnitConversionTile(
              unit = VolumeUnit.IMPERIAL_GALLON,
              formattedValue =
                formatVolumeForUnit(result.netWaterVolumeLiters, VolumeUnit.IMPERIAL_GALLON),
              isSelected = state.selectedDisplayUnit == VolumeUnit.IMPERIAL_GALLON,
              onClick = { onSelectDisplayUnit(VolumeUnit.IMPERIAL_GALLON) },
              modifier = Modifier.weight(1f),
            )
            UnitConversionTile(
              unit = VolumeUnit.CUBIC_CM,
              formattedValue =
                formatVolumeForUnit(result.netWaterVolumeLiters, VolumeUnit.CUBIC_CM),
              isSelected = state.selectedDisplayUnit == VolumeUnit.CUBIC_CM,
              onClick = { onSelectDisplayUnit(VolumeUnit.CUBIC_CM) },
              modifier = Modifier.weight(1f),
            )
          }
        }
      }
    }

    // Itemized Breakdown & Aquarist Recommendations Card
    ElevatedCard(
      modifier = Modifier.fillMaxWidth(),
      shape = MaterialTheme.shapes.large,
      colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Icon(
            imageVector = Icons.Rounded.Analytics,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
          )
          Text(
            text = "Rincian Volume & Rekomendasi",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }

        // Proportional Composition Bar
        if (result.bareWaterVolumeLiters > 0.0) {
          val total = result.bareWaterVolumeLiters
          val waterWeight = (result.netWaterVolumeLiters / total).toFloat().coerceIn(0f, 1f)
          val sandWeight = (result.sandDisplacedLiters / total).toFloat().coerceIn(0f, 1f)
          val stoneWeight = (result.stoneDisplacedLiters / total).toFloat().coerceIn(0f, 1f)
          val woodWeight = (result.woodDisplacedLiters / total).toFloat().coerceIn(0f, 1f)

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .height(14.dp)
                  .clip(RoundedCornerShape(7.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
              if (waterWeight > 0.005f) {
                Box(
                  modifier =
                    Modifier.weight(waterWeight)
                      .fillMaxHeight()
                      .background(aquaColors.waterDeep)
                )
              }
              if (sandWeight > 0.005f) {
                Box(
                  modifier =
                    Modifier.weight(sandWeight)
                      .fillMaxHeight()
                      .background(aquaColors.sandAccent)
                )
              }
              if (stoneWeight > 0.005f) {
                Box(
                  modifier =
                    Modifier.weight(stoneWeight)
                      .fillMaxHeight()
                      .background(aquaColors.stoneAccent)
                )
              }
              if (woodWeight > 0.005f) {
                Box(
                  modifier =
                    Modifier.weight(woodWeight)
                      .fillMaxHeight()
                      .background(aquaColors.woodAccent)
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              LegendDot("Air", aquaColors.waterDeep)
              if (condition.includesSand) LegendDot("Pasir", aquaColors.sandAccent)
              if (condition.includesStone) LegendDot("Batu", aquaColors.stoneAccent)
              if (condition.includesWood) LegendDot("Kayu", aquaColors.woodAccent)
            }
          }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        BreakdownRow(
          label = "Volume Kotor Kaca (P × L × T)",
          value = "${formatLiters(result.grossVolumeLiters)} L",
        )
        BreakdownRow(
          label = "Volume Air Polos (Tinggi air efektif)",
          value = "${formatLiters(result.bareWaterVolumeLiters)} L",
        )

        if (condition.includesSand) {
          BreakdownRow(
            label = "Pengurangan Pasir (${state.sandType.label})",
            value = "-${formatLiters(result.sandDisplacedLiters)} L",
            valueColor = aquaColors.sandAccent,
          )
        }
        if (condition.includesStone) {
          BreakdownRow(
            label =
              "Pengurangan Batu (${state.stoneCount} buah, ${formatCompactDecimal(result.stoneTotalWeightGrams, 0)} g)",
            value = "-${formatLiters(result.stoneDisplacedLiters)} L",
            valueColor = aquaColors.stoneAccent,
          )
        }
        if (condition.includesWood) {
          BreakdownRow(
            label =
              "Pengurangan Kayu (${state.woodCount} potong, ${formatCompactDecimal(result.woodTotalWeightGrams, 0)} g, ${state.woodSubmergedPercent}%)",
            value = "-${formatLiters(result.woodDisplacedLiters)} L",
            valueColor = aquaColors.woodAccent,
          )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Total Volume Air Bersih",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text =
              "${formatVolumeForUnit(result.netWaterVolumeLiters, state.selectedDisplayUnit)} ${state.selectedDisplayUnit.shortLabel}",
            style =
              MaterialTheme.typography.titleLarge.copy(
                fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
                fontWeight = FontWeight.Bold,
              ),
            color = MaterialTheme.colorScheme.primary,
          )
        }

        // Aquarist Care Tips Strip
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            TipColumn(
              title = "Ganti Air 30%",
              value = "${formatLiters(result.waterChange30Liters, 1)} L",
              modifier = Modifier.weight(1f),
            )
            TipColumn(
              title = "Ganti Air 50%",
              value = "${formatLiters(result.waterChange50Liters, 1)} L",
              modifier = Modifier.weight(1f),
            )
            TipColumn(
              title = "Filter Ideal",
              value = "${result.recommendedFilterLphMin}–${result.recommendedFilterLphMax} L/j",
              modifier = Modifier.weight(1.2f),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun UnitConversionTile(
  unit: VolumeUnit,
  formattedValue: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier =
      modifier
        .clip(RoundedCornerShape(14.dp))
        .clickable(onClick = onClick)
        .testTag("unit_tile_${unit.name.lowercase()}"),
    shape = RoundedCornerShape(14.dp),
    color =
      if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      },
    border =
      if (isSelected) {
        BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
      } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
      },
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        text = unit.label,
        style = MaterialTheme.typography.labelMedium,
        color =
          if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
          } else {
            MaterialTheme.colorScheme.onSurfaceVariant
          },
      )
      Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
          text = formattedValue,
          style =
            MaterialTheme.typography.titleLarge.copy(
              fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
              fontWeight = FontWeight.Bold,
            ),
          color =
            if (isSelected) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.onSurface
            },
        )
        Text(
          text = unit.shortLabel,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(bottom = 2.dp),
        )
      }
    }
  }
}

@Composable
private fun LegendDot(label: String, color: Color) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(5.dp),
  ) {
    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun BreakdownRow(
  label: String,
  value: String,
  valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.weight(1f).padding(end = 8.dp),
    )
    Text(
      text = value,
      style =
        MaterialTheme.typography.titleMedium.copy(
          fontFamily = MaterialTheme.typography.displaySmall.fontFamily
        ),
      color = valueColor,
    )
  }
}

@Composable
private fun TipColumn(
  title: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = value,
      style =
        MaterialTheme.typography.labelLarge.copy(
          fontFamily = MaterialTheme.typography.displaySmall.fontFamily
        ),
      color = MaterialTheme.colorScheme.onSurface,
    )
  }
}
