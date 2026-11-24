package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AquariumProfileEntity
import com.example.model.AquariumUiState
import com.example.ui.components.BreakdownCard
import com.example.ui.components.ConditionSelectorCard
import com.example.ui.components.DecorationConfigurationSection
import com.example.ui.components.DimensionInputCard
import com.example.ui.components.NitrogenCycleScreen
import com.example.ui.components.ProfileNameDialog
import com.example.ui.components.SavedProfilesScreen
import com.example.ui.components.TankVisualizerCard
import com.example.viewmodel.AquariumViewModel
import kotlinx.coroutines.launch

private enum class AquaTab(val label: String) {
  CALCULATOR("Kalkulator"),
  PROFILES("Profil"),
  NITROGEN_CYCLE("Cycling"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AquariumApp(
  viewModel: AquariumViewModel,
  modifier: Modifier = Modifier,
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val savedProfiles by viewModel.savedProfiles.collectAsStateWithLifecycle()
  val completedCycleStepIds by viewModel.completedCycleStepIds.collectAsStateWithLifecycle()
  val result = state.calculation

  var selectedTab by rememberSaveable { mutableStateOf(AquaTab.CALCULATOR) }
  var showSaveDialog by rememberSaveable { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  if (selectedTab != AquaTab.CALCULATOR) {
    BackHandler { selectedTab = AquaTab.CALCULATOR }
  }

  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isExpanded = maxWidth >= 760.dp

    Row(modifier = Modifier.fillMaxSize()) {
      if (isExpanded) {
        NavigationRail(
          containerColor = MaterialTheme.colorScheme.surface,
          header = {
            Icon(
              imageVector = Icons.Rounded.WaterDrop,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(vertical = 12.dp).size(28.dp),
            )
          },
        ) {
          NavigationRailItem(
            selected = selectedTab == AquaTab.CALCULATOR,
            onClick = { selectedTab = AquaTab.CALCULATOR },
            icon = { Icon(Icons.Rounded.Calculate, contentDescription = "Kalkulator") },
            label = { Text(AquaTab.CALCULATOR.label) },
            modifier = Modifier.testTag("nav_rail_calculator"),
          )
          NavigationRailItem(
            selected = selectedTab == AquaTab.PROFILES,
            onClick = { selectedTab = AquaTab.PROFILES },
            icon = {
              BadgedBox(
                badge = {
                  if (savedProfiles.isNotEmpty()) {
                    Badge { Text(savedProfiles.size.toString()) }
                  }
                }
              ) {
                Icon(Icons.Rounded.Bookmarks, contentDescription = "Profil Tersimpan")
              }
            },
            label = { Text(AquaTab.PROFILES.label) },
            modifier = Modifier.testTag("nav_rail_profiles"),
          )
          NavigationRailItem(
            selected = selectedTab == AquaTab.NITROGEN_CYCLE,
            onClick = { selectedTab = AquaTab.NITROGEN_CYCLE },
            icon = { Icon(Icons.Rounded.Science, contentDescription = "Siklus Nitrogen") },
            label = { Text(AquaTab.NITROGEN_CYCLE.label) },
            modifier = Modifier.testTag("nav_rail_nitrogen"),
          )
        }
      }

      Scaffold(
        modifier = Modifier.weight(1f).fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
          CenterAlignedTopAppBar(
            colors =
              TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
              ),
            title = {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text =
                    when (selectedTab) {
                      AquaTab.CALCULATOR -> "MainTank"
                      AquaTab.PROFILES -> "Profil Tersimpan"
                      AquaTab.NITROGEN_CYCLE -> "Siklus Nitrogen"
                    },
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Bold,
                )
                val subtitle =
                  when {
                    selectedTab == AquaTab.CALCULATOR && state.activeProfileName != null ->
                      "Profil: ${state.activeProfileName}"
                    selectedTab == AquaTab.CALCULATOR -> "Kalkulator Volume Air Akuarium"
                    selectedTab == AquaTab.PROFILES ->
                      "${savedProfiles.size} profil tersimpan di perangkat"
                    else -> "Checklist & Tips Akuarium Baru"
                  }
                Text(
                  text = subtitle,
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.primary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            },
            actions = {
              if (selectedTab == AquaTab.CALCULATOR) {
                IconButton(
                  onClick = {
                    viewModel.resetAll()
                    scope.launch {
                      snackbarHostState.showSnackbar("Kalkulator direset ke standar")
                    }
                  },
                  modifier = Modifier.size(48.dp).testTag("reset_button"),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = "Reset kalkulator",
                  )
                }
              }
            },
          )
        },
        bottomBar = {
          if (!isExpanded) {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
              NavigationBarItem(
                selected = selectedTab == AquaTab.CALCULATOR,
                onClick = { selectedTab = AquaTab.CALCULATOR },
                icon = { Icon(Icons.Rounded.Calculate, contentDescription = "Kalkulator") },
                label = { Text(AquaTab.CALCULATOR.label) },
                modifier = Modifier.testTag("nav_bar_calculator"),
              )
              NavigationBarItem(
                selected = selectedTab == AquaTab.PROFILES,
                onClick = { selectedTab = AquaTab.PROFILES },
                icon = {
                  BadgedBox(
                    badge = {
                      if (savedProfiles.isNotEmpty()) {
                        Badge { Text(savedProfiles.size.toString()) }
                      }
                    }
                  ) {
                    Icon(Icons.Rounded.Bookmarks, contentDescription = "Profil Tersimpan")
                  }
                },
                label = { Text(AquaTab.PROFILES.label) },
                modifier = Modifier.testTag("nav_bar_profiles"),
              )
              NavigationBarItem(
                selected = selectedTab == AquaTab.NITROGEN_CYCLE,
                onClick = { selectedTab = AquaTab.NITROGEN_CYCLE },
                icon = { Icon(Icons.Rounded.Science, contentDescription = "Siklus Nitrogen") },
                label = { Text(AquaTab.NITROGEN_CYCLE.label) },
                modifier = Modifier.testTag("nav_bar_nitrogen"),
              )
            }
          }
        },
        floatingActionButton = {
          if (selectedTab == AquaTab.CALCULATOR) {
            ExtendedFloatingActionButton(
              onClick = { showSaveDialog = true },
              icon = {
                Icon(
                  imageVector =
                    if (state.activeProfileId != null) Icons.Rounded.Bookmark
                    else Icons.Rounded.BookmarkAdd,
                  contentDescription = null,
                )
              },
              text = {
                Text(
                  if (state.activeProfileId != null) "Simpan / Salin" else "Simpan Profil"
                )
              },
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.testTag("save_profile_fab"),
            )
          }
        },
      ) { innerPadding ->
        when (selectedTab) {
          AquaTab.CALCULATOR -> {
            CalculatorContent(
              state = state,
              isExpanded = isExpanded,
              viewModel = viewModel,
              contentPadding = innerPadding,
            )
          }
          AquaTab.PROFILES -> {
            Box(
              modifier = Modifier.fillMaxSize().padding(innerPadding),
              contentAlignment = Alignment.TopCenter,
            ) {
              SavedProfilesScreen(
                profiles = savedProfiles,
                activeProfileId = state.activeProfileId,
                onLoadProfile = { profile: AquariumProfileEntity ->
                  viewModel.loadProfile(profile)
                  selectedTab = AquaTab.CALCULATOR
                  scope.launch {
                    snackbarHostState.showSnackbar("Memuat profil \"${profile.profileName}\"")
                  }
                },
                onRenameProfile = { profile, newName ->
                  viewModel.renameProfile(profile, newName)
                  scope.launch {
                    snackbarHostState.showSnackbar("Nama profil diperbarui menjadi \"$newName\"")
                  }
                },
                onDeleteProfile = { profile ->
                  viewModel.deleteProfile(profile.id)
                  scope.launch {
                    snackbarHostState.showSnackbar("Profil \"${profile.profileName}\" dihapus")
                  }
                },
                onGoToCalculator = { selectedTab = AquaTab.CALCULATOR },
                modifier = Modifier.widthIn(max = 760.dp),
              )
            }
          }
          AquaTab.NITROGEN_CYCLE -> {
            Box(
              modifier = Modifier.fillMaxSize().padding(innerPadding),
              contentAlignment = Alignment.TopCenter,
            ) {
              NitrogenCycleScreen(
                completedStepIds = completedCycleStepIds,
                onToggleStep = viewModel::toggleCycleStep,
                onResetChecklist = viewModel::resetCycleChecklist,
                modifier = Modifier.widthIn(max = 760.dp),
              )
            }
          }
        }
      }
    }
  }

  if (showSaveDialog) {
    val defaultSuggestion =
      state.activeProfileName
        ?: "Akuarium ${formatCompactDecimal(result.tankLengthCm)}×${formatCompactDecimal(result.tankWidthCm)}×${formatCompactDecimal(result.tankHeightCm)}"
    ProfileNameDialog(
      title =
        if (state.activeProfileId != null) "Simpan Profil Akuarium"
        else "Beri Nama Profil Baru",
      confirmLabel = "Simpan",
      initialName = defaultSuggestion,
      onDismiss = { showSaveDialog = false },
      onConfirm = { chosenName ->
        val overwrite =
          state.activeProfileId != null && chosenName == state.activeProfileName
        viewModel.saveProfile(name = chosenName, overwriteActive = overwrite)
        showSaveDialog = false
        scope.launch {
          snackbarHostState.showSnackbar("Profil \"$chosenName\" tersimpan")
        }
      },
    )
  }
}

@Composable
private fun CalculatorContent(
  state: AquariumUiState,
  isExpanded: Boolean,
  viewModel: AquariumViewModel,
  contentPadding: PaddingValues,
) {
  val result = state.calculation

  if (isExpanded) {
    Row(
      modifier =
        Modifier.fillMaxSize()
          .padding(contentPadding)
          .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      Column(
        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        TankVisualizerCard(
          state = state,
          result = result,
          onSelectDisplayUnit = viewModel::selectDisplayUnit,
        )
        BreakdownCard(
          state = state,
          result = result,
          onSelectDisplayUnit = viewModel::selectDisplayUnit,
        )
        Spacer(modifier = Modifier.height(72.dp))
      }

      Column(
        modifier = Modifier.weight(1.1f).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        ConditionSelectorCard(
          selectedCondition = state.selectedCondition,
          onSelectCondition = viewModel::selectCondition,
        )
        DimensionInputCard(
          state = state,
          onLengthChange = viewModel::updateLength,
          onWidthChange = viewModel::updateWidth,
          onHeightChange = viewModel::updateHeight,
          onTopGapChange = viewModel::updateTopGap,
          onSelectGlassThickness = viewModel::selectGlassThickness,
          onApplyPreset = viewModel::applyTankPreset,
        )
        DecorationConfigurationSection(
          state = state,
          result = result,
          onToggleSandSync = viewModel::toggleSandSync,
          onSandLengthChange = viewModel::updateSandLength,
          onSandWidthChange = viewModel::updateSandWidth,
          onSandHeightChange = viewModel::updateSandHeight,
          onSandHeightSliderChange = viewModel::updateSandHeightFromSlider,
          onSelectSandType = viewModel::selectSandType,
          onIncrementStone = viewModel::incrementStoneCount,
          onDecrementStone = viewModel::decrementStoneCount,
          onStoneCountChange = viewModel::updateStoneCount,
          onSelectStonePreset = viewModel::selectStonePreset,
          onStoneWeightGramsChange = viewModel::updateStoneWeightGramsPerUnit,
          onIncrementWood = viewModel::incrementWoodCount,
          onDecrementWood = viewModel::decrementWoodCount,
          onWoodCountChange = viewModel::updateWoodCount,
          onSelectWoodPreset = viewModel::selectWoodPreset,
          onWoodWeightGramsChange = viewModel::updateWoodWeightGramsPerUnit,
          onWoodSubmergedChange = viewModel::updateWoodSubmergedPercent,
        )
        Spacer(modifier = Modifier.height(72.dp))
      }
    }
  } else {
    Box(
      modifier = Modifier.fillMaxSize().padding(contentPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.widthIn(max = 600.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        TankVisualizerCard(
          state = state,
          result = result,
          onSelectDisplayUnit = viewModel::selectDisplayUnit,
        )

        ConditionSelectorCard(
          selectedCondition = state.selectedCondition,
          onSelectCondition = viewModel::selectCondition,
        )

        DimensionInputCard(
          state = state,
          onLengthChange = viewModel::updateLength,
          onWidthChange = viewModel::updateWidth,
          onHeightChange = viewModel::updateHeight,
          onTopGapChange = viewModel::updateTopGap,
          onSelectGlassThickness = viewModel::selectGlassThickness,
          onApplyPreset = viewModel::applyTankPreset,
        )

        DecorationConfigurationSection(
          state = state,
          result = result,
          onToggleSandSync = viewModel::toggleSandSync,
          onSandLengthChange = viewModel::updateSandLength,
          onSandWidthChange = viewModel::updateSandWidth,
          onSandHeightChange = viewModel::updateSandHeight,
          onSandHeightSliderChange = viewModel::updateSandHeightFromSlider,
          onSelectSandType = viewModel::selectSandType,
          onIncrementStone = viewModel::incrementStoneCount,
          onDecrementStone = viewModel::decrementStoneCount,
          onStoneCountChange = viewModel::updateStoneCount,
          onSelectStonePreset = viewModel::selectStonePreset,
          onStoneWeightGramsChange = viewModel::updateStoneWeightGramsPerUnit,
          onIncrementWood = viewModel::incrementWoodCount,
          onDecrementWood = viewModel::decrementWoodCount,
          onWoodCountChange = viewModel::updateWoodCount,
          onSelectWoodPreset = viewModel::selectWoodPreset,
          onWoodWeightGramsChange = viewModel::updateWoodWeightGramsPerUnit,
          onWoodSubmergedChange = viewModel::updateWoodSubmergedPercent,
        )

        BreakdownCard(
          state = state,
          result = result,
          onSelectDisplayUnit = viewModel::selectDisplayUnit,
        )

        Spacer(modifier = Modifier.height(76.dp))
      }
    }
  }
}
