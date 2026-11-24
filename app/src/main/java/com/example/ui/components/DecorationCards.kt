package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.Landscape
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.AquariumCalculationResult
import com.example.model.AquariumCalculator
import com.example.model.AquariumCondition
import com.example.model.AquariumUiState
import com.example.model.SandType
import com.example.model.StoneSizePreset
import com.example.model.WoodSizePreset
import com.example.ui.formatCompactDecimal
import com.example.ui.formatLiters
import com.example.ui.theme.LocalAquaColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConditionSelectorCard(
  selectedCondition: AquariumCondition,
  onSelectCondition: (AquariumCondition) -> Unit,
  modifier: Modifier = Modifier,
) {
  val singleConditions =
    listOf(
      AquariumCondition.POLOS,
      AquariumCondition.PASIR,
      AquariumCondition.BATU,
      AquariumCondition.KAYU,
    )
  val comboConditions =
    listOf(
      AquariumCondition.PASIR_BATU,
      AquariumCondition.PASIR_KAYU,
      AquariumCondition.KOMBINASI,
    )

  ElevatedCard(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Column {
        Text(
          text = "Kondisi Isi Akuarium",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = selectedCondition.subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Kondisi Tunggal",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          singleConditions.forEach { condition ->
            ConditionChoiceChip(
              condition = condition,
              selected = selectedCondition == condition,
              onClick = { onSelectCondition(condition) },
            )
          }
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Kombinasi Substrat & Hardscape",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          comboConditions.forEach { condition ->
            ConditionChoiceChip(
              condition = condition,
              selected = selectedCondition == condition,
              onClick = { onSelectCondition(condition) },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ConditionChoiceChip(
  condition: AquariumCondition,
  selected: Boolean,
  onClick: () -> Unit,
) {
  val icon =
    when (condition) {
      AquariumCondition.POLOS -> Icons.Rounded.WaterDrop
      AquariumCondition.PASIR -> Icons.Rounded.Grain
      AquariumCondition.BATU -> Icons.Rounded.Landscape
      AquariumCondition.KAYU -> Icons.Rounded.Forest
      AquariumCondition.PASIR_BATU,
      AquariumCondition.PASIR_KAYU,
      AquariumCondition.KOMBINASI -> Icons.Rounded.Layers
    }
  FilterChip(
    selected = selected,
    onClick = onClick,
    leadingIcon = {
      Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(18.dp),
      )
    },
    label = { Text(text = condition.label, style = MaterialTheme.typography.labelLarge) },
    modifier = Modifier.testTag("condition_chip_${condition.name.lowercase()}"),
    colors =
      FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
      ),
  )
}

@Composable
fun DecorationConfigurationSection(
  state: AquariumUiState,
  result: AquariumCalculationResult,
  onToggleSandSync: (Boolean) -> Unit,
  onSandLengthChange: (String) -> Unit,
  onSandWidthChange: (String) -> Unit,
  onSandHeightChange: (String) -> Unit,
  onSandHeightSliderChange: (Float) -> Unit,
  onSelectSandType: (SandType) -> Unit,
  onIncrementStone: () -> Unit,
  onDecrementStone: () -> Unit,
  onStoneCountChange: (String) -> Unit,
  onSelectStonePreset: (StoneSizePreset) -> Unit,
  onStoneWeightGramsChange: (String) -> Unit,
  onIncrementWood: () -> Unit,
  onDecrementWood: () -> Unit,
  onWoodCountChange: (String) -> Unit,
  onSelectWoodPreset: (WoodSizePreset) -> Unit,
  onWoodWeightGramsChange: (String) -> Unit,
  onWoodSubmergedChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    AnimatedVisibility(
      visible = state.selectedCondition.includesSand,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically(),
    ) {
      SandConfigCard(
        state = state,
        result = result,
        onToggleSync = onToggleSandSync,
        onLengthChange = onSandLengthChange,
        onWidthChange = onSandWidthChange,
        onHeightChange = onSandHeightChange,
        onHeightSliderChange = onSandHeightSliderChange,
        onSelectSandType = onSelectSandType,
      )
    }

    AnimatedVisibility(
      visible = state.selectedCondition.includesStone,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically(),
    ) {
      StoneConfigCard(
        state = state,
        result = result,
        onIncrement = onIncrementStone,
        onDecrement = onDecrementStone,
        onCountChange = onStoneCountChange,
        onSelectPreset = onSelectStonePreset,
        onWeightGramsChange = onStoneWeightGramsChange,
      )
    }

    AnimatedVisibility(
      visible = state.selectedCondition.includesWood,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically(),
    ) {
      WoodConfigCard(
        state = state,
        result = result,
        onIncrement = onIncrementWood,
        onDecrement = onDecrementWood,
        onCountChange = onWoodCountChange,
        onSelectPreset = onSelectWoodPreset,
        onWeightGramsChange = onWoodWeightGramsChange,
        onSubmergedPercentChange = onWoodSubmergedChange,
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SandConfigCard(
  state: AquariumUiState,
  result: AquariumCalculationResult,
  onToggleSync: (Boolean) -> Unit,
  onLengthChange: (String) -> Unit,
  onWidthChange: (String) -> Unit,
  onHeightChange: (String) -> Unit,
  onHeightSliderChange: (Float) -> Unit,
  onSelectSandType: (SandType) -> Unit,
) {
  val aquaColors = LocalAquaColors.current

  DecorationCardShell(
    title = "Pasir / Substrat",
    subtitle = "Dimensi (P × L × T) & rongga air",
    icon = Icons.Rounded.Grain,
    accentColor = aquaColors.sandAccent,
    containerColor = aquaColors.sandContainer,
    onContainerColor = aquaColors.sandOnContainer,
    displacedText = "-${formatLiters(result.sandDisplacedLiters)} L",
  ) {
    // Sync switch
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "Ikuti panjang & lebar dalam akuarium",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(1f),
      )
      Switch(
        checked = state.sandSyncWithTank,
        onCheckedChange = onToggleSync,
        modifier = Modifier.testTag("sand_sync_switch"),
      )
    }

    // P x L x T Sand Inputs
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      DimensionTextField(
        label = "Panjang",
        value =
          if (state.sandSyncWithTank) {
            formatLiters(result.innerLengthCm, 1).trimEnd('0').trimEnd('.')
          } else {
            state.sandLengthCm
          },
        onValueChange = onLengthChange,
        suffix = "cm",
        enabled = !state.sandSyncWithTank,
        modifier = Modifier.weight(1f).testTag("sand_length_input"),
      )
      DimensionTextField(
        label = "Lebar",
        value =
          if (state.sandSyncWithTank) {
            formatLiters(result.innerWidthCm, 1).trimEnd('0').trimEnd('.')
          } else {
            state.sandWidthCm
          },
        onValueChange = onWidthChange,
        suffix = "cm",
        enabled = !state.sandSyncWithTank,
        modifier = Modifier.weight(1f).testTag("sand_width_input"),
      )
      DimensionTextField(
        label = "Tinggi",
        value = state.sandHeightCm,
        onValueChange = onHeightChange,
        suffix = "cm",
        modifier = Modifier.weight(1f).testTag("sand_height_input"),
      )
    }

    // Height slider for effortless tweaking
    val sliderVal =
      (state.sandHeightCm.replace(',', '.').toFloatOrNull() ?: 0f).coerceIn(0f, 20f)
    Column {
      Slider(
        value = sliderVal,
        onValueChange = onHeightSliderChange,
        valueRange = 0f..20f,
        colors =
          SliderDefaults.colors(
            thumbColor = aquaColors.sandAccent,
            activeTrackColor = aquaColors.sandAccent,
          ),
        modifier = Modifier.testTag("sand_height_slider"),
      )
    }

    // Sand Type / Porosity
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = "Jenis & Kepadatan Pasir",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        SandType.entries.forEach { type ->
          FilterChip(
            selected = state.sandType == type,
            onClick = { onSelectSandType(type) },
            label = { Text(type.label, style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.testTag("sand_type_${type.name.lowercase()}"),
          )
        }
      }
      Text(
        text =
          "${state.sandType.hint}  •  Est. berat pasir: ~${formatLiters(result.sandEstimatedKg, 1)} kg",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoneConfigCard(
  state: AquariumUiState,
  result: AquariumCalculationResult,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  onCountChange: (String) -> Unit,
  onSelectPreset: (StoneSizePreset) -> Unit,
  onWeightGramsChange: (String) -> Unit,
) {
  val aquaColors = LocalAquaColors.current

  DecorationCardShell(
    title = "Batu / Hardscape",
    subtitle = "Berat (gram) ÷ densitas rata-rata ${AquariumCalculator.STONE_DENSITY_G_PER_CM3} g/cm³",
    icon = Icons.Rounded.Landscape,
    accentColor = aquaColors.stoneAccent,
    containerColor = aquaColors.stoneContainer,
    onContainerColor = aquaColors.stoneOnContainer,
    displacedText = "-${formatLiters(result.stoneDisplacedLiters)} L",
  ) {
    QuantityStepperRow(
      label = "Jumlah Batu",
      count = state.stoneCount,
      unitLabel = "buah",
      onIncrement = onIncrement,
      onDecrement = onDecrement,
      onCountChange = onCountChange,
      tagPrefix = "stone",
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = "Estimasi Berat per Batu",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        StoneSizePreset.entries.forEach { preset ->
          val chipText =
            if (preset == StoneSizePreset.KUSTOM) {
              preset.label
            } else {
              "${preset.label} (${formatCompactDecimal(preset.weightGrams, 0)} g)"
            }
          FilterChip(
            selected = state.stonePreset == preset,
            onClick = { onSelectPreset(preset) },
            label = { Text(chipText, style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.testTag("stone_preset_${preset.name.lowercase()}"),
          )
        }
      }
    }

    DimensionTextField(
      label = "Berat per Batu",
      value = state.stoneWeightGramsPerUnit,
      onValueChange = onWeightGramsChange,
      suffix = "gram",
      modifier = Modifier.fillMaxWidth().testTag("stone_weight_input"),
    )

    Surface(
      shape = RoundedCornerShape(10.dp),
      color = aquaColors.stoneContainer.copy(alpha = 0.65f),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        text =
          "1 batu ≈ ${formatLiters(result.stonePerUnitLiters)} L  •  Total ${formatCompactDecimal(result.stoneTotalWeightGrams, 0)} g ÷ ${AquariumCalculator.STONE_DENSITY_G_PER_CM3} g/cm³ = -${formatLiters(result.stoneDisplacedLiters)} L air",
        style = MaterialTheme.typography.bodyMedium,
        color = aquaColors.stoneOnContainer,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WoodConfigCard(
  state: AquariumUiState,
  result: AquariumCalculationResult,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  onCountChange: (String) -> Unit,
  onSelectPreset: (WoodSizePreset) -> Unit,
  onWeightGramsChange: (String) -> Unit,
  onSubmergedPercentChange: (Int) -> Unit,
) {
  val aquaColors = LocalAquaColors.current

  DecorationCardShell(
    title = "Kayu / Driftwood",
    subtitle = "Berat (gram) ÷ densitas rata-rata ${AquariumCalculator.WOOD_DENSITY_G_PER_CM3} g/cm³",
    icon = Icons.Rounded.Forest,
    accentColor = aquaColors.woodAccent,
    containerColor = aquaColors.woodContainer,
    onContainerColor = aquaColors.woodOnContainer,
    displacedText = "-${formatLiters(result.woodDisplacedLiters)} L",
  ) {
    QuantityStepperRow(
      label = "Jumlah Kayu",
      count = state.woodCount,
      unitLabel = "potong",
      onIncrement = onIncrement,
      onDecrement = onDecrement,
      onCountChange = onCountChange,
      tagPrefix = "wood",
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = "Estimasi Berat per Kayu",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        WoodSizePreset.entries.forEach { preset ->
          val chipText =
            if (preset == WoodSizePreset.KUSTOM) {
              preset.label
            } else {
              "${preset.label} (${formatCompactDecimal(preset.weightGrams, 0)} g)"
            }
          FilterChip(
            selected = state.woodPreset == preset,
            onClick = { onSelectPreset(preset) },
            label = { Text(chipText, style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.testTag("wood_preset_${preset.name.lowercase()}"),
          )
        }
      }
    }

    DimensionTextField(
      label = "Berat per Kayu",
      value = state.woodWeightGramsPerUnit,
      onValueChange = onWeightGramsChange,
      suffix = "gram",
      modifier = Modifier.fillMaxWidth().testTag("wood_weight_input"),
    )

    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = "Bagian Terendam Air",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = "${state.woodSubmergedPercent}%",
          style = MaterialTheme.typography.labelLarge.copy(
            fontFamily = MaterialTheme.typography.displaySmall.fontFamily
          ),
          color = aquaColors.woodAccent,
        )
      }
      Slider(
        value = state.woodSubmergedPercent.toFloat(),
        onValueChange = { onSubmergedPercentChange(it.toInt()) },
        valueRange = 10f..100f,
        steps = 8,
        colors =
          SliderDefaults.colors(
            thumbColor = aquaColors.woodAccent,
            activeTrackColor = aquaColors.woodAccent,
          ),
        modifier = Modifier.testTag("wood_submerged_slider"),
      )
    }

    Surface(
      shape = RoundedCornerShape(10.dp),
      color = aquaColors.woodContainer.copy(alpha = 0.65f),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        text =
          "1 kayu ≈ ${formatLiters(result.woodPerUnitLiters)} L  •  Total ${formatCompactDecimal(result.woodTotalWeightGrams, 0)} g ÷ ${AquariumCalculator.WOOD_DENSITY_G_PER_CM3} g/cm³ × ${state.woodSubmergedPercent}% = -${formatLiters(result.woodDisplacedLiters)} L air",
        style = MaterialTheme.typography.bodyMedium,
        color = aquaColors.woodOnContainer,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      )
    }
  }
}

@Composable
private fun QuantityStepperRow(
  label: String,
  count: Int,
  unitLabel: String,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  onCountChange: (String) -> Unit,
  tagPrefix: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = "$count $unitLabel",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      FilledTonalIconButton(
        onClick = onDecrement,
        enabled = count > 0,
        colors =
          IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
        modifier = Modifier.size(48.dp).testTag("${tagPrefix}_decrement_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.Remove,
          contentDescription = "Kurangi $label",
        )
      }

      OutlinedTextField(
        value = count.toString(),
        onValueChange = onCountChange,
        singleLine = true,
        textStyle =
          MaterialTheme.typography.titleMedium.copy(
            fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
            textAlign = TextAlign.Center,
          ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(72.dp).testTag("${tagPrefix}_count_input"),
      )

      FilledTonalIconButton(
        onClick = onIncrement,
        colors =
          IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
          ),
        modifier = Modifier.size(48.dp).testTag("${tagPrefix}_increment_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.Add,
          contentDescription = "Tambah $label",
        )
      }
    }
  }
}

@Composable
private fun DecorationCardShell(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  containerColor: Color,
  onContainerColor: Color,
  displacedText: String,
  content: @Composable () -> Unit,
) {
  ElevatedCard(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f),
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = containerColor,
            modifier = Modifier.size(40.dp),
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.padding(8.dp),
            )
          }
          Column {
            Text(
              text = title,
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = containerColor,
          border = BorderStroke(1.dp, accentColor.copy(alpha = 0.28f)),
        ) {
          Text(
            text = displacedText,
            style =
              MaterialTheme.typography.labelLarge.copy(
                fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
                fontWeight = FontWeight.Bold,
              ),
            color = onContainerColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          )
        }
      }

      content()
    }
  }
}
