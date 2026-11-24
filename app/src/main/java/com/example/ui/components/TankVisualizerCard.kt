package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
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
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

@Composable
fun TankVisualizerCard(
  state: AquariumUiState,
  result: AquariumCalculationResult,
  onSelectDisplayUnit: (VolumeUnit) -> Unit,
  modifier: Modifier = Modifier,
) {
  val aquaColors = LocalAquaColors.current
  val condition = state.selectedCondition

  val animatedWaterFraction by
    animateFloatAsState(
      targetValue =
        if (result.tankHeightCm > 0.0) {
          (result.effectiveWaterHeightCm / result.tankHeightCm).toFloat().coerceIn(0f, 1f)
        } else {
          0f
        },
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "waterFraction",
    )

  val animatedSandFraction by
    animateFloatAsState(
      targetValue =
        if (condition.includesSand && result.tankHeightCm > 0.0) {
          (result.sandEffectiveHeightCm / result.tankHeightCm).toFloat().coerceIn(0f, 0.75f)
        } else {
          0f
        },
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "sandFraction",
    )

  val animatedSandWidthFraction by
    animateFloatAsState(
      targetValue =
        if (condition.includesSand && result.innerLengthCm > 0.0) {
          val sandLen =
            if (state.sandSyncWithTank) {
              result.innerLengthCm
            } else {
              state.sandLengthCm.replace(',', '.').toDoubleOrNull() ?: 0.0
            }
          (sandLen / result.innerLengthCm).toFloat().coerceIn(0.15f, 1f)
        } else {
          0f
        },
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "sandWidthFraction",
    )

  val infiniteTransition = rememberInfiniteTransition(label = "waterWave")
  val wavePhase by
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = (2f * PI).toFloat(),
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 3600, easing = LinearEasing),
          repeatMode = RepeatMode.Restart,
        ),
      label = "wavePhase",
    )

  ElevatedCard(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
      // Hero Net Water Readout Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.WaterDrop,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp),
            )
            Text(
              text = "VOLUME AIR BERSIH",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Bold,
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Text(
              text = formatVolumeForUnit(result.netWaterVolumeLiters, state.selectedDisplayUnit),
              style = MaterialTheme.typography.displaySmall,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.testTag("hero_volume_value"),
            )
            Text(
              text = state.selectedDisplayUnit.shortLabel,
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(bottom = 4.dp),
            )
          }

          Text(
            text =
              "${formatVolumeForUnit(result.netWaterVolumeLiters, VolumeUnit.LITER)} L  •  ${formatVolumeForUnit(result.netWaterVolumeLiters, VolumeUnit.US_GALLON)} gal US  •  ${formatCompactDecimal(result.fillPercentOfGross, 0)}% terisi",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        // Condition Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End,
          ) {
            Text(
              text = condition.label,
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            if (result.totalDecorationDisplacedLiters > 0.0) {
              Text(
                text = "-${formatLiters(result.totalDecorationDisplacedLiters)} L",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
              )
            } else {
              Text(
                text = "Tanpa isi",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
              )
            }
          }
        }
      }

      // Quick Unit Switcher Chips
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(VolumeUnit.entries) { unit ->
          FilterChip(
            selected = state.selectedDisplayUnit == unit,
            onClick = { onSelectDisplayUnit(unit) },
            label = { Text(text = unit.label, style = MaterialTheme.typography.labelMedium) },
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
              ),
            modifier = Modifier.testTag("hero_unit_chip_${unit.name.lowercase()}"),
          )
        }
      }

      // Live Tank Cross-Section Canvas
      val borderGlassColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
      val tankBackdropTop = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
      val tankBackdropBottom = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)

      Box(
        modifier =
          Modifier.fillMaxWidth()
            .height(176.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.verticalGradient(colors = listOf(tankBackdropTop, tankBackdropBottom))
            )
      ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 14.dp)) {
          val aspect =
            if (result.tankLengthCm > 0.0 && result.tankHeightCm > 0.0) {
              (result.tankLengthCm / result.tankHeightCm).toFloat().coerceIn(0.8f, 3.2f)
            } else {
              1.7f
            }

          val maxW = size.width
          val maxH = size.height
          val tankW: Float
          val tankH: Float
          if (maxW / maxH > aspect) {
            tankH = maxH
            tankW = tankH * aspect
          } else {
            tankW = maxW
            tankH = tankW / aspect
          }

          val left = (maxW - tankW) / 2f
          val top = (maxH - tankH) / 2f
          val bottom = top + tankH
          val right = left + tankW

          // Draw subtle glass interior background
          drawRoundRect(
            color = Color.White.copy(alpha = 0.14f),
            topLeft = Offset(left, top),
            size = Size(tankW, tankH),
            cornerRadius = CornerRadius(6f, 6f),
          )

          // Clip all interior layers inside tank glass
          clipRect(left = left, top = top, right = right, bottom = bottom) {
            // 1. Water layer with gentle surface wave
            val waterTopY = bottom - (tankH * animatedWaterFraction)
            if (animatedWaterFraction > 0.01f && !result.isOverflowWarning) {
              val wavePath =
                Path().apply {
                  moveTo(left, bottom)
                  lineTo(left, waterTopY)
                  val steps = 28
                  val amplitude = 4.5f
                  for (i in 0..steps) {
                    val x = left + (tankW * i / steps)
                    val yOffset =
                      sin((i.toFloat() / steps) * 2.5f * PI.toFloat() + wavePhase) * amplitude
                    lineTo(x, (waterTopY + yOffset).coerceIn(top, bottom))
                  }
                  lineTo(right, bottom)
                  close()
                }
              drawPath(
                path = wavePath,
                brush =
                  Brush.verticalGradient(
                    colors =
                      listOf(
                        aquaColors.waterSurface.copy(alpha = 0.58f),
                        aquaColors.waterDeep.copy(alpha = 0.80f),
                      ),
                    startY = waterTopY,
                    endY = bottom,
                  ),
              )

              // Highlight crest line
              val crestPath =
                Path().apply {
                  val steps = 28
                  val amplitude = 4.5f
                  for (i in 0..steps) {
                    val x = left + (tankW * i / steps)
                    val yOffset =
                      sin((i.toFloat() / steps) * 2.5f * PI.toFloat() + wavePhase) * amplitude
                    val y = (waterTopY + yOffset).coerceIn(top, bottom)
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                  }
                }
              drawPath(
                path = crestPath,
                color = Color.White.copy(alpha = 0.65f),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round),
              )
            }

            // 2. Sand layer (if active)
            val sandHeightPx = tankH * animatedSandFraction
            if (animatedSandFraction > 0.005f && animatedSandWidthFraction > 0.01f) {
              val sandW = tankW * animatedSandWidthFraction
              val sandLeft = left + (tankW - sandW) / 2f
              val sandTop = (bottom - sandHeightPx).coerceAtLeast(top)
              val sandPath =
                Path().apply {
                  moveTo(sandLeft, bottom)
                  lineTo(sandLeft, sandTop + 3f)
                  quadraticTo(
                    sandLeft + sandW * 0.28f,
                    sandTop - 5f,
                    sandLeft + sandW * 0.55f,
                    sandTop + 2f,
                  )
                  quadraticTo(
                    sandLeft + sandW * 0.82f,
                    sandTop + 7f,
                    sandLeft + sandW,
                    sandTop,
                  )
                  lineTo(sandLeft + sandW, bottom)
                  close()
                }
              drawPath(
                path = sandPath,
                brush =
                  Brush.verticalGradient(
                    colors =
                      listOf(
                        aquaColors.sandAccent.copy(alpha = 0.92f),
                        aquaColors.sandAccent.copy(alpha = 0.72f),
                      ),
                    startY = sandTop,
                    endY = bottom,
                  ),
              )
            }

            val hardscapeBaseY = bottom - sandHeightPx * 0.72f

            // 3. Wood / Driftwood (if active and count > 0)
            if (condition.includesWood && state.woodCount > 0) {
              val visualWoodCount = min(state.woodCount, 4)
              drawDriftwoodCluster(
                count = visualWoodCount,
                tankLeft = left,
                tankWidth = tankW,
                tankHeight = tankH,
                baseY = hardscapeBaseY,
                woodColor = aquaColors.woodAccent,
              )
            }

            // 4. Stones / Rocks (if active and count > 0)
            if (condition.includesStone && state.stoneCount > 0) {
              val visualStoneCount = min(state.stoneCount, 8)
              drawStoneCluster(
                count = visualStoneCount,
                tankLeft = left,
                tankWidth = tankW,
                tankHeight = tankH,
                baseY = hardscapeBaseY,
                stoneColor = aquaColors.stoneAccent,
              )
            }
          }

          // Glass Rim Outline & Reflection
          drawRoundRect(
            color = borderGlassColor,
            topLeft = Offset(left, top),
            size = Size(tankW, tankH),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(width = 4f),
          )
          // Left glass specular highlight
          drawLine(
            color = Color.White.copy(alpha = 0.38f),
            start = Offset(left + 8f, top + 10f),
            end = Offset(left + 8f, bottom - 10f),
            strokeWidth = 3f,
            cap = StrokeCap.Round,
          )
        }

        // Dimension pill overlay at bottom-right of canvas
        Surface(
          modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        ) {
          Text(
            text =
              "${formatCompactDecimal(result.tankLengthCm)} × ${formatCompactDecimal(result.tankWidthCm)} × ${formatCompactDecimal(result.tankHeightCm)} cm",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }
      }

      // Quick Summary Badges Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        MiniStatPill(
          label = "Polos",
          value = "${formatLiters(result.bareWaterVolumeLiters, 1)} L",
          modifier = Modifier.weight(1f),
        )
        MiniStatPill(
          label = "Dekorasi",
          value = "-${formatLiters(result.totalDecorationDisplacedLiters, 1)} L",
          modifier = Modifier.weight(1f),
        )
        MiniStatPill(
          label = "Ganti Air 30%",
          value = "${formatLiters(result.waterChange30Liters, 1)} L",
          modifier = Modifier.weight(1f),
        )
      }

      AnimatedVisibility(visible = result.isOverflowWarning) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = aquaColors.warningContainer,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.WarningAmber,
              contentDescription = "Peringatan volume penuh",
              tint = aquaColors.warningAccent,
              modifier = Modifier.size(20.dp),
            )
            Text(
              text =
                "Volume dekorasi melebihi kapasitas air akuarium. Kurangi tinggi pasir atau jumlah dekorasi.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MiniStatPill(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(fontFamily = MaterialTheme.typography.displaySmall.fontFamily),
        color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

private fun DrawScope.drawStoneCluster(
  count: Int,
  tankLeft: Float,
  tankWidth: Float,
  tankHeight: Float,
  baseY: Float,
  stoneColor: Color,
) {
  val stoneTemplates =
    listOf(
      Triple(0.24f, 0.14f, 0.18f),
      Triple(0.72f, 0.16f, 0.21f),
      Triple(0.45f, 0.11f, 0.14f),
      Triple(0.15f, 0.09f, 0.11f),
      Triple(0.84f, 0.10f, 0.13f),
      Triple(0.58f, 0.09f, 0.12f),
      Triple(0.34f, 0.08f, 0.10f),
      Triple(0.66f, 0.07f, 0.09f),
    )
  for (i in 0 until min(count, stoneTemplates.size)) {
    val (xRatio, wRatio, hRatio) = stoneTemplates[i]
    val cx = tankLeft + tankWidth * xRatio
    val sw = tankWidth * wRatio
    val sh = tankHeight * hRatio
    val stonePath =
      Path().apply {
        moveTo(cx - sw * 0.55f, baseY + 6f)
        lineTo(cx - sw * 0.42f, baseY - sh * 0.68f)
        lineTo(cx - sw * 0.05f, baseY - sh)
        lineTo(cx + sw * 0.38f, baseY - sh * 0.72f)
        lineTo(cx + sw * 0.55f, baseY + 6f)
        close()
      }
    drawPath(
      path = stonePath,
      color = stoneColor.copy(alpha = if (i % 2 == 0) 0.92f else 0.78f),
    )
    // Subtle facet highlight
    drawLine(
      color = Color.White.copy(alpha = 0.22f),
      start = Offset(cx - sw * 0.32f, baseY - sh * 0.62f),
      end = Offset(cx - sw * 0.05f, baseY - sh * 0.88f),
      strokeWidth = 2.5f,
      cap = StrokeCap.Round,
    )
  }
}

private fun DrawScope.drawDriftwoodCluster(
  count: Int,
  tankLeft: Float,
  tankWidth: Float,
  tankHeight: Float,
  baseY: Float,
  woodColor: Color,
) {
  val anchors = listOf(0.54f, 0.34f, 0.72f, 0.44f)
  for (i in 0 until min(count, anchors.size)) {
    val startX = tankLeft + tankWidth * anchors[i]
    val dir = if (i % 2 == 0) 1f else -1f
    val reachX = tankWidth * (0.19f - i * 0.02f) * dir
    val reachY = tankHeight * (0.48f - i * 0.05f)

    val mainBranch =
      Path().apply {
        moveTo(startX, baseY + 4f)
        quadraticTo(
          startX + reachX * 0.35f,
          baseY - reachY * 0.55f,
          startX + reachX,
          baseY - reachY,
        )
      }
    drawPath(
      path = mainBranch,
      color = woodColor,
      style = Stroke(width = 11f - i * 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    val subBranch =
      Path().apply {
        moveTo(startX + reachX * 0.42f, baseY - reachY * 0.45f)
        quadraticTo(
          startX + reachX * 0.55f,
          baseY - reachY * 0.80f,
          startX + reachX * 0.25f,
          baseY - reachY * 0.95f,
        )
      }
    drawPath(
      path = subBranch,
      color = woodColor.copy(alpha = 0.9f),
      style = Stroke(width = 6.5f, cap = StrokeCap.Round),
    )
  }
}
