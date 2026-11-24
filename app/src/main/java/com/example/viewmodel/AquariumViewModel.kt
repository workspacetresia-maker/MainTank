package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AquariumProfileEntity
import com.example.data.AquariumProfileRepository
import com.example.model.AquariumCalculator
import com.example.model.AquariumCondition
import com.example.model.AquariumUiState
import com.example.model.SandType
import com.example.model.StoneSizePreset
import com.example.model.TankPreset
import com.example.model.VolumeUnit
import com.example.model.WoodSizePreset
import com.example.ui.formatCompactDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AquariumViewModel(
  private val repository: AquariumProfileRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(AquariumUiState())
  val uiState: StateFlow<AquariumUiState> = _uiState.asStateFlow()

  val savedProfiles: StateFlow<List<AquariumProfileEntity>> =
    repository.allProfiles.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList(),
    )

  private val _completedCycleStepIds = MutableStateFlow<Set<Int>>(emptySet())
  val completedCycleStepIds: StateFlow<Set<Int>> = _completedCycleStepIds.asStateFlow()

  private fun sanitizeDecimalInput(input: String): String {
    val filtered = StringBuilder()
    var hasSeparator = false
    for (ch in input) {
      if (ch.isDigit()) {
        filtered.append(ch)
      } else if ((ch == '.' || ch == ',') && !hasSeparator) {
        filtered.append('.')
        hasSeparator = true
      }
    }
    return filtered.toString().take(8)
  }

  fun updateLength(value: String) {
    val sanitized = sanitizeDecimalInput(value)
    _uiState.update { current ->
      val updated = current.copy(lengthCm = sanitized)
      if (updated.sandSyncWithTank) {
        val calc = AquariumCalculator.calculate(updated)
        updated.copy(sandLengthCm = formatCompactDecimal(calc.innerLengthCm))
      } else {
        updated
      }
    }
  }

  fun updateWidth(value: String) {
    val sanitized = sanitizeDecimalInput(value)
    _uiState.update { current ->
      val updated = current.copy(widthCm = sanitized)
      if (updated.sandSyncWithTank) {
        val calc = AquariumCalculator.calculate(updated)
        updated.copy(sandWidthCm = formatCompactDecimal(calc.innerWidthCm))
      } else {
        updated
      }
    }
  }

  fun updateHeight(value: String) {
    val sanitized = sanitizeDecimalInput(value)
    _uiState.update { it.copy(heightCm = sanitized) }
  }

  fun updateTopGap(value: String) {
    val sanitized = sanitizeDecimalInput(value)
    _uiState.update { it.copy(topGapCm = sanitized) }
  }

  fun selectGlassThickness(mm: Int) {
    _uiState.update { current ->
      val updated = current.copy(glassThicknessMm = mm)
      if (updated.sandSyncWithTank) {
        val calc = AquariumCalculator.calculate(updated)
        updated.copy(
          sandLengthCm = formatCompactDecimal(calc.innerLengthCm),
          sandWidthCm = formatCompactDecimal(calc.innerWidthCm),
        )
      } else {
        updated
      }
    }
  }

  fun applyTankPreset(preset: TankPreset) {
    _uiState.update { current ->
      val updated =
        current.copy(
          lengthCm = preset.lengthCm,
          widthCm = preset.widthCm,
          heightCm = preset.heightCm,
          glassThicknessMm = preset.glassMm,
        )
      if (updated.sandSyncWithTank) {
        val calc = AquariumCalculator.calculate(updated)
        updated.copy(
          sandLengthCm = formatCompactDecimal(calc.innerLengthCm),
          sandWidthCm = formatCompactDecimal(calc.innerWidthCm),
        )
      } else {
        updated
      }
    }
  }

  fun selectCondition(condition: AquariumCondition) {
    _uiState.update { it.copy(selectedCondition = condition) }
  }

  fun selectDisplayUnit(unit: VolumeUnit) {
    _uiState.update { it.copy(selectedDisplayUnit = unit) }
  }

  fun toggleSandSync(sync: Boolean) {
    _uiState.update { current ->
      val calc = AquariumCalculator.calculate(current)
      current.copy(
        sandSyncWithTank = sync,
        sandLengthCm =
          if (sync) formatCompactDecimal(calc.innerLengthCm) else current.sandLengthCm,
        sandWidthCm =
          if (sync) formatCompactDecimal(calc.innerWidthCm) else current.sandWidthCm,
      )
    }
  }

  fun updateSandLength(value: String) {
    val sanitized = sanitizeDecimalInput(value)
    _uiState.update { it.copy(sandSyncWithTank = false, sandLengthCm = sanitized) }
  }

  fun updateSandWidth(value: String) {
    val sanitized = sanitizeDecimalInput(value)
    _uiState.update { it.copy(sandSyncWithTank = false, sandWidthCm = sanitized) }
  }

  fun updateSandHeight(value: String) {
    val sanitized = sanitizeDecimalInput(value)
    _uiState.update { it.copy(sandHeightCm = sanitized) }
  }

  fun updateSandHeightFromSlider(heightCm: Float) {
    _uiState.update { it.copy(sandHeightCm = formatCompactDecimal(heightCm.toDouble(), 1)) }
  }

  fun selectSandType(type: SandType) {
    _uiState.update { it.copy(sandType = type) }
  }

  fun incrementStoneCount() {
    _uiState.update { it.copy(stoneCount = (it.stoneCount + 1).coerceAtMost(99)) }
  }

  fun decrementStoneCount() {
    _uiState.update { it.copy(stoneCount = (it.stoneCount - 1).coerceAtLeast(0)) }
  }

  fun updateStoneCount(raw: String) {
    val digits = raw.filter { it.isDigit() }.take(2)
    val parsed = digits.toIntOrNull() ?: 0
    _uiState.update { it.copy(stoneCount = parsed.coerceIn(0, 99)) }
  }

  fun selectStonePreset(preset: StoneSizePreset) {
    _uiState.update { current ->
      if (preset == StoneSizePreset.KUSTOM) {
        current.copy(stonePreset = preset)
      } else {
        current.copy(
          stonePreset = preset,
          stoneWeightGramsPerUnit = formatCompactDecimal(preset.weightGrams, 0),
        )
      }
    }
  }

  fun updateStoneWeightGramsPerUnit(value: String) {
    val sanitized = sanitizeDecimalInput(value)
    _uiState.update {
      it.copy(stonePreset = StoneSizePreset.KUSTOM, stoneWeightGramsPerUnit = sanitized)
    }
  }

  fun incrementWoodCount() {
    _uiState.update { it.copy(woodCount = (it.woodCount + 1).coerceAtMost(99)) }
  }

  fun decrementWoodCount() {
    _uiState.update { it.copy(woodCount = (it.woodCount - 1).coerceAtLeast(0)) }
  }

  fun updateWoodCount(raw: String) {
    val digits = raw.filter { it.isDigit() }.take(2)
    val parsed = digits.toIntOrNull() ?: 0
    _uiState.update { it.copy(woodCount = parsed.coerceIn(0, 99)) }
  }

  fun selectWoodPreset(preset: WoodSizePreset) {
    _uiState.update { current ->
      if (preset == WoodSizePreset.KUSTOM) {
        current.copy(woodPreset = preset)
      } else {
        current.copy(
          woodPreset = preset,
          woodWeightGramsPerUnit = formatCompactDecimal(preset.weightGrams, 0),
        )
      }
    }
  }

  fun updateWoodWeightGramsPerUnit(value: String) {
    val sanitized = sanitizeDecimalInput(value)
    _uiState.update {
      it.copy(woodPreset = WoodSizePreset.KUSTOM, woodWeightGramsPerUnit = sanitized)
    }
  }

  fun updateWoodSubmergedPercent(percent: Int) {
    _uiState.update { it.copy(woodSubmergedPercent = percent.coerceIn(10, 100)) }
  }

  fun saveProfile(name: String, overwriteActive: Boolean = false) {
    val cleanName = name.trim()
    if (cleanName.isEmpty()) return
    val currentState = _uiState.value
    val targetId = if (overwriteActive) currentState.activeProfileId ?: 0L else 0L

    viewModelScope.launch {
      val entity =
        AquariumProfileEntity.fromUiState(
          profileName = cleanName,
          state = currentState,
          existingId = targetId,
        )
      val savedId = repository.insertProfile(entity)
      _uiState.update {
        it.copy(
          activeProfileId = if (targetId != 0L) targetId else savedId,
          activeProfileName = cleanName,
        )
      }
    }
  }

  fun loadProfile(profile: AquariumProfileEntity) {
    val restored =
      profile.toUiState().copy(
        activeProfileId = profile.id,
        activeProfileName = profile.profileName,
      )
    _uiState.value = restored
  }

  fun renameProfile(profile: AquariumProfileEntity, newName: String) {
    val cleanName = newName.trim()
    if (cleanName.isEmpty()) return
    viewModelScope.launch {
      repository.updateProfile(
        profile.copy(
          profileName = cleanName,
          updatedAtMillis = System.currentTimeMillis(),
        )
      )
      _uiState.update { current ->
        if (current.activeProfileId == profile.id) {
          current.copy(activeProfileName = cleanName)
        } else {
          current
        }
      }
    }
  }

  fun deleteProfile(id: Long) {
    viewModelScope.launch {
      repository.deleteProfileById(id)
      _uiState.update { current ->
        if (current.activeProfileId == id) {
          current.copy(activeProfileId = null, activeProfileName = null)
        } else {
          current
        }
      }
    }
  }

  fun toggleCycleStep(stepId: Int) {
    _completedCycleStepIds.update { current ->
      if (current.contains(stepId)) current - stepId else current + stepId
    }
  }

  fun resetCycleChecklist() {
    _completedCycleStepIds.value = emptySet()
  }

  fun resetAll() {
    _uiState.value = AquariumUiState()
  }

  class Factory(private val repository: AquariumProfileRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(AquariumViewModel::class.java)) {
        return AquariumViewModel(repository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
  }
}
