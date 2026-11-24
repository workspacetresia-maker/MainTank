package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Immutable
data class AquaSemanticColors(
  val waterSurface: Color,
  val waterDeep: Color,
  val sandAccent: Color,
  val sandContainer: Color,
  val sandOnContainer: Color,
  val stoneAccent: Color,
  val stoneContainer: Color,
  val stoneOnContainer: Color,
  val woodAccent: Color,
  val woodContainer: Color,
  val woodOnContainer: Color,
  val warningAccent: Color,
  val warningContainer: Color,
)

private val LightAquaSemanticColors =
  AquaSemanticColors(
    waterSurface = WaterSurfaceLight,
    waterDeep = WaterDeepLight,
    sandAccent = SandAccentLight,
    sandContainer = SandContainerLight,
    sandOnContainer = SandOnContainerLight,
    stoneAccent = StoneAccentLight,
    stoneContainer = StoneContainerLight,
    stoneOnContainer = StoneOnContainerLight,
    woodAccent = WoodAccentLight,
    woodContainer = WoodContainerLight,
    woodOnContainer = WoodOnContainerLight,
    warningAccent = WarningAmberLight,
    warningContainer = WarningContainerLight,
  )

private val DarkAquaSemanticColors =
  AquaSemanticColors(
    waterSurface = WaterSurfaceDark,
    waterDeep = WaterDeepDark,
    sandAccent = SandAccentDark,
    sandContainer = SandContainerDark,
    sandOnContainer = SandOnContainerDark,
    stoneAccent = StoneAccentDark,
    stoneContainer = StoneContainerDark,
    stoneOnContainer = StoneOnContainerDark,
    woodAccent = WoodAccentDark,
    woodContainer = WoodContainerDark,
    woodOnContainer = WoodOnContainerDark,
    warningAccent = WarningAmberDark,
    warningContainer = WarningContainerDark,
  )

val LocalAquaColors = staticCompositionLocalOf { LightAquaSemanticColors }

private val DarkColorScheme =
  darkColorScheme(
    primary = BioluminescentCyan,
    onPrimary = OnBioluminescentCyan,
    primaryContainer = DeepLagoonContainer,
    onPrimaryContainer = OnDeepLagoonContainer,
    secondary = DarkSecondary,
    onSecondary = OnDarkSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = OnDarkSecondaryContainer,
    tertiary = BioluminescentCyan,
    background = AbyssBackground,
    onBackground = OnAbyssSurface,
    surface = AbyssSurface,
    onSurface = OnAbyssSurface,
    surfaceVariant = AbyssSurfaceVariant,
    onSurfaceVariant = OnAbyssSurfaceVariant,
    outline = AbyssOutline,
    outlineVariant = AbyssOutlineVariant,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = OceanTealPrimary,
    onPrimary = OnOceanTealPrimary,
    primaryContainer = OceanTealContainer,
    onPrimaryContainer = OnOceanTealContainer,
    secondary = LagoonSecondary,
    onSecondary = OnLagoonSecondary,
    secondaryContainer = LagoonContainer,
    onSecondaryContainer = OnLagoonContainer,
    tertiary = CoralTertiary,
    onTertiary = OnCoralTertiary,
    tertiaryContainer = CoralContainer,
    onTertiaryContainer = OnCoralContainer,
    background = SeaMistBackground,
    onBackground = OnSeaSurface,
    surface = CrispSurface,
    onSurface = OnSeaSurface,
    surfaceVariant = SeaFoamSurfaceVariant,
    onSurfaceVariant = OnSeaSurfaceVariant,
    outline = SeaOutline,
    outlineVariant = SeaOutlineVariant,
  )

val AquaShapes =
  Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
  )

@Composable
fun AquaVolumeTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val semanticColors = if (darkTheme) DarkAquaSemanticColors else LightAquaSemanticColors

  CompositionLocalProvider(LocalAquaColors provides semanticColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = AquaTypography,
      shapes = AquaShapes,
      content = content,
    )
  }
}
