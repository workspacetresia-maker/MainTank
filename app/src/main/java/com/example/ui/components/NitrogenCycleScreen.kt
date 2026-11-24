package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.model.BEGINNER_NITROGEN_TIPS
import com.example.model.NITROGEN_CYCLE_STEPS
import com.example.model.NitrogenCycleStep
import com.example.ui.theme.LocalAquaColors

@Composable
fun NitrogenCycleScreen(
  completedStepIds: Set<Int>,
  onToggleStep: (Int) -> Unit,
  onResetChecklist: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val totalSteps = NITROGEN_CYCLE_STEPS.size
  val completedCount = completedStepIds.size.coerceIn(0, totalSteps)
  val progress by
    animateFloatAsState(
      targetValue = if (totalSteps > 0) completedCount.toFloat() / totalSteps else 0f,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "cycleProgress",
    )

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item {
      NitrogenCycleOverviewCard(
        completedCount = completedCount,
        totalSteps = totalSteps,
        progress = progress,
        onResetChecklist = onResetChecklist,
      )
    }

    items(NITROGEN_CYCLE_STEPS, key = { it.id }) { step ->
      NitrogenCycleStepCard(
        step = step,
        isCompleted = completedStepIds.contains(step.id),
        onToggle = { onToggleStep(step.id) },
      )
    }

    item {
      BeginnerTipsCard()
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun NitrogenCycleOverviewCard(
  completedCount: Int,
  totalSteps: Int,
  progress: Float,
  onResetChecklist: () -> Unit,
) {
  val aquaColors = LocalAquaColors.current

  ElevatedCard(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
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
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(42.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.Science,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(9.dp),
            )
          }
          Column {
            Text(
              text = "Siklus Nitrogen Akuarium",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = "Panduan cycling akuarium baru (4–6 minggu)",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        if (completedCount > 0) {
          TextButton(
            onClick = onResetChecklist,
            modifier = Modifier.testTag("reset_cycle_checklist_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.RestartAlt,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Ulang")
          }
        }
      }

      // 3-Stage Nitrogen Flow Visual Diagram
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          CycleStagePill(
            formula = "NH₃",
            name = "Amonia",
            status = "Berbahaya",
            accentColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
          )
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = "menjadi",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 2.dp).size(16.dp),
          )
          CycleStagePill(
            formula = "NO₂⁻",
            name = "Nitrit",
            status = "Beracun",
            accentColor = aquaColors.warningAccent,
            modifier = Modifier.weight(1f),
          )
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = "menjadi",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 2.dp).size(16.dp),
          )
          CycleStagePill(
            formula = "NO₃⁻",
            name = "Nitrat",
            status = "Aman <20 ppm",
            accentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
          )
        }
      }

      // Progress Bar
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Progres Persiapan Cycling",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = "$completedCount / $totalSteps Langkah",
            style =
              MaterialTheme.typography.labelLarge.copy(
                fontFamily = MaterialTheme.typography.displaySmall.fontFamily
              ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("cycle_progress_text"),
          )
        }
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
          color = MaterialTheme.colorScheme.primary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
          strokeCap = StrokeCap.Round,
        )
      }
    }
  }
}

@Composable
private fun CycleStagePill(
  formula: String,
  name: String,
  status: String,
  accentColor: Color,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = formula,
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
            fontWeight = FontWeight.Bold,
          ),
        color = accentColor,
      )
      Text(
        text = name,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = status,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun NitrogenCycleStepCard(
  step: NitrogenCycleStep,
  isCompleted: Boolean,
  onToggle: () -> Unit,
) {
  val aquaColors = LocalAquaColors.current

  ElevatedCard(
    modifier =
      Modifier.fillMaxWidth()
        .clip(MaterialTheme.shapes.large)
        .clickable(onClick = onToggle)
        .testTag("cycle_step_card_${step.id}"),
    shape = MaterialTheme.shapes.large,
    colors =
      CardDefaults.elevatedCardColors(
        containerColor =
          if (isCompleted) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
          } else {
            MaterialTheme.colorScheme.surface
          }
      ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isCompleted) 1.dp else 2.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Icon(
          imageVector =
            if (isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
          contentDescription =
            if (isCompleted) "Tandai langkah ${step.stepNumber} belum selesai"
            else "Tandai langkah ${step.stepNumber} selesai",
          tint =
            if (isCompleted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(28.dp).testTag("cycle_step_checkbox_${step.id}"),
        )

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer,
            ) {
              Text(
                text = step.phaseLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              )
            }
            Text(
              text = "Langkah ${step.stepNumber}",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = step.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            color =
              if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
              else MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      Text(
        text = step.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )

      // Beginner Tip Callout
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = aquaColors.sandContainer.copy(alpha = 0.72f),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.Top,
        ) {
          Icon(
            imageVector = Icons.Rounded.Lightbulb,
            contentDescription = null,
            tint = aquaColors.sandAccent,
            modifier = Modifier.size(18.dp).padding(top = 1.dp),
          )
          Text(
            text = "Tips: ${step.beginnerTip}",
            style = MaterialTheme.typography.bodyMedium,
            color = aquaColors.sandOnContainer,
          )
        }
      }
    }
  }
}

@Composable
private fun BeginnerTipsCard() {
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
          imageVector = Icons.Rounded.TipsAndUpdates,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp),
        )
        Text(
          text = "Tips Penting untuk Pemula",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }

      // Target Parameter Summary Strip
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          TargetParamItem("Amonia", "0 ppm", Modifier.weight(1f))
          TargetParamItem("Nitrit", "0 ppm", Modifier.weight(1f))
          TargetParamItem("Nitrat", "< 20 ppm", Modifier.weight(1f))
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

      BEGINNER_NITROGEN_TIPS.forEach { tip ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.Top,
        ) {
          Box(
            modifier =
              Modifier.padding(top = 6.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
          )
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              text = tip.title,
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = tip.detail,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TargetParamItem(
  label: String,
  target: String,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = target,
      style =
        MaterialTheme.typography.titleMedium.copy(
          fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
          fontWeight = FontWeight.Bold,
        ),
      color = MaterialTheme.colorScheme.primary,
    )
  }
}
