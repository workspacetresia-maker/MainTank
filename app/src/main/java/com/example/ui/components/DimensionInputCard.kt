package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SquareFoot
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.AquariumUiState
import com.example.model.DEFAULT_TANK_PRESETS
import com.example.model.TankPreset

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DimensionInputCard(
  state: AquariumUiState,
  onLengthChange: (String) -> Unit,
  onWidthChange: (String) -> Unit,
  onHeightChange: (String) -> Unit,
  onTopGapChange: (String) -> Unit,
  onSelectGlassThickness: (Int) -> Unit,
  onApplyPreset: (TankPreset) -> Unit,
  modifier: Modifier = Modifier,
) {
  val glassOptions = listOf(0, 5, 6, 8, 10, 12)

  ElevatedCard(
    modifier = modifier.fillMaxWidth(),
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
          imageVector = Icons.Rounded.SquareFoot,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp),
        )
        Text(
          text = "Dimensi Akuarium",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }

      // Quick Tank Presets
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(DEFAULT_TANK_PRESETS) { preset ->
          val isSelected =
            state.lengthCm == preset.lengthCm &&
              state.widthCm == preset.widthCm &&
              state.heightCm == preset.heightCm
          FilterChip(
            selected = isSelected,
            onClick = { onApplyPreset(preset) },
            label = {
              Text(
                text = "${preset.name} (${preset.lengthCm}×${preset.widthCm}×${preset.heightCm})",
                style = MaterialTheme.typography.labelMedium,
              )
            },
            modifier = Modifier.testTag("preset_${preset.name.lowercase()}"),
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
              ),
          )
        }
      }

      // P x L x T Fields
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        DimensionTextField(
          label = "Panjang",
          value = state.lengthCm,
          onValueChange = onLengthChange,
          suffix = "cm",
          modifier = Modifier.weight(1f).testTag("tank_length_input"),
        )
        DimensionTextField(
          label = "Lebar",
          value = state.widthCm,
          onValueChange = onWidthChange,
          suffix = "cm",
          modifier = Modifier.weight(1f).testTag("tank_width_input"),
        )
        DimensionTextField(
          label = "Tinggi",
          value = state.heightCm,
          onValueChange = onHeightChange,
          suffix = "cm",
          modifier = Modifier.weight(1f).testTag("tank_height_input"),
        )
      }

      // Top Gap & Glass Thickness
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        DimensionTextField(
          label = "Ruang Kosong Atas",
          value = state.topGapCm,
          onValueChange = onTopGapChange,
          suffix = "cm",
          modifier = Modifier.weight(1f).testTag("tank_top_gap_input"),
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Tebal Kaca",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          glassOptions.forEach { mm ->
            FilterChip(
              selected = state.glassThicknessMm == mm,
              onClick = { onSelectGlassThickness(mm) },
              label = {
                Text(
                  text = if (mm == 0) "Ukuran Dalam (0 mm)" else "$mm mm",
                  style = MaterialTheme.typography.labelMedium,
                )
              },
              modifier = Modifier.testTag("glass_chip_$mm"),
            )
          }
        }
      }
    }
  }
}

@Composable
fun DimensionTextField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  suffix: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    enabled = enabled,
    singleLine = true,
    label = { Text(text = label, maxLines = 1) },
    suffix = {
      Text(
        text = suffix,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    },
    textStyle =
      MaterialTheme.typography.bodyLarge.copy(
        fontFamily = MaterialTheme.typography.displaySmall.fontFamily
      ),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    shape = RoundedCornerShape(12.dp),
    modifier = modifier,
  )
}
