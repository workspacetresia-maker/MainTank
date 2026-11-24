package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AquariumDatabase
import com.example.data.AquariumProfileEntity
import com.example.model.AquariumCalculator
import com.example.model.AquariumCondition
import com.example.model.AquariumUiState
import com.example.model.SandType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: AquariumDatabase

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AquariumDatabase::class.java).allowMainThreadQueries().build()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun `app name matches MainTank`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MainTank", appName)
  }

  @Test
  fun `calculate bare tank volume`() {
    val state =
      AquariumUiState(
        lengthCm = "60",
        widthCm = "30",
        heightCm = "35",
        topGapCm = "3",
        glassThicknessMm = 0,
        selectedCondition = AquariumCondition.POLOS,
      )
    val result = AquariumCalculator.calculate(state)
    assertEquals(63.0, result.grossVolumeLiters, 0.001)
    assertEquals(57.6, result.bareWaterVolumeLiters, 0.001)
    assertEquals(57.6, result.netWaterVolumeLiters, 0.001)
  }

  @Test
  fun `calculate tank with sand dimensions P x L x T`() {
    val state =
      AquariumUiState(
        lengthCm = "60",
        widthCm = "30",
        heightCm = "35",
        topGapCm = "3",
        selectedCondition = AquariumCondition.PASIR,
        sandSyncWithTank = false,
        sandLengthCm = "60",
        sandWidthCm = "30",
        sandHeightCm = "5",
        sandType = SandType.GEOMETRIS,
      )
    val result = AquariumCalculator.calculate(state)
    assertEquals(9.0, result.sandDisplacedLiters, 0.001)
    assertEquals(48.6, result.netWaterVolumeLiters, 0.001)
  }

  @Test
  fun `calculate tank with combined sand stone grams and wood grams`() {
    assertEquals(2.5, AquariumCalculator.STONE_DENSITY_G_PER_CM3, 0.0001)
    assertEquals(0.85, AquariumCalculator.WOOD_DENSITY_G_PER_CM3, 0.0001)
    val state =
      AquariumUiState(
        lengthCm = "60",
        widthCm = "30",
        heightCm = "35",
        topGapCm = "3",
        selectedCondition = AquariumCondition.KOMBINASI,
        sandSyncWithTank = true,
        sandHeightCm = "5",
        sandType = SandType.GEOMETRIS,
        stoneCount = 3,
        stoneWeightGramsPerUnit = "1000", // 3000 g / 2.5 g/cm³ = 1.2 L
        woodCount = 2,
        woodWeightGramsPerUnit = "850", // 1700 g / 0.85 g/cm³ * 50% = 1.0 L
        woodSubmergedPercent = 50,
      )
    val result = AquariumCalculator.calculate(state)
    assertEquals(9.0, result.sandDisplacedLiters, 0.001)
    assertEquals(1.2, result.stoneDisplacedLiters, 0.001)
    assertEquals(3000.0, result.stoneTotalWeightGrams, 0.001)
    assertEquals(1.0, result.woodDisplacedLiters, 0.001)
    assertEquals(1700.0, result.woodTotalWeightGrams, 0.001)
    assertEquals(46.4, result.netWaterVolumeLiters, 0.001)
  }

  @Test
  fun `calculate substrate plus stone and substrate plus wood combinations`() {
    val sandAndStone =
      AquariumUiState(
        lengthCm = "60",
        widthCm = "30",
        heightCm = "35",
        topGapCm = "3",
        selectedCondition = AquariumCondition.PASIR_BATU,
        sandSyncWithTank = true,
        sandHeightCm = "5",
        sandType = SandType.GEOMETRIS,
        stoneCount = 2,
        stoneWeightGramsPerUnit = "1250", // 2500 / 2.5 = 1.0 L
        woodCount = 2,
        woodWeightGramsPerUnit = "850", // excluded in PASIR_BATU
      )
    val stoneComboResult = AquariumCalculator.calculate(sandAndStone)
    assertEquals(9.0, stoneComboResult.sandDisplacedLiters, 0.001)
    assertEquals(1.0, stoneComboResult.stoneDisplacedLiters, 0.001)
    assertEquals(0.0, stoneComboResult.woodDisplacedLiters, 0.001)
    assertEquals(47.6, stoneComboResult.netWaterVolumeLiters, 0.001)

    val sandAndWood =
      sandAndStone.copy(
        selectedCondition = AquariumCondition.PASIR_KAYU,
        woodCount = 1,
        woodWeightGramsPerUnit = "850", // 850 / 0.85 = 1.0 L
        woodSubmergedPercent = 100,
      )
    val woodComboResult = AquariumCalculator.calculate(sandAndWood)
    assertEquals(9.0, woodComboResult.sandDisplacedLiters, 0.001)
    assertEquals(0.0, woodComboResult.stoneDisplacedLiters, 0.001)
    assertEquals(1.0, woodComboResult.woodDisplacedLiters, 0.001)
    assertEquals(47.6, woodComboResult.netWaterVolumeLiters, 0.001)
  }

  @Test
  fun `save and retrieve named profile in Room database`() = runTest {
    val dao = db.aquariumProfileDao()
    val state =
      AquariumUiState(
        lengthCm = "90",
        widthCm = "45",
        heightCm = "45",
        topGapCm = "3",
        selectedCondition = AquariumCondition.BATU,
        stoneCount = 4,
        stoneWeightGramsPerUnit = "2500", // 10000 g / 2.5 = 4.0 L
      )
    val entity = AquariumProfileEntity.fromUiState("Iwagumi Ruang Tamu", state)
    val id = dao.insertProfile(entity)
    assertTrue(id > 0L)

    val profiles = dao.getAllProfiles().first()
    assertEquals(1, profiles.size)
    assertEquals("Iwagumi Ruang Tamu", profiles.first().profileName)
    assertEquals(166.1, profiles.first().netWaterVolumeLiters, 0.05)
  }

  @Test
  fun `convert liters to US gallons Imperial gallons and cubic centimeters`() {
    val state =
      AquariumUiState(
        lengthCm = "60",
        widthCm = "30",
        heightCm = "35",
        topGapCm = "3",
        selectedCondition = AquariumCondition.POLOS,
      )
    val result = AquariumCalculator.calculate(state)
    // 57.6 L
    assertEquals(57.6, result.netWaterVolumeLiters, 0.001)
    assertEquals(15.2163, result.netWaterVolumeGallons, 0.001)
    assertEquals(12.6702, result.netWaterVolumeImperialGallons, 0.001)
    assertEquals(57600.0, result.netWaterVolumeCubicCm, 0.001)
  }

  @Test
  fun `share summary text includes profile name dimensions and converted volumes`() {
    val state =
      AquariumUiState(
        lengthCm = "60",
        widthCm = "30",
        heightCm = "35",
        topGapCm = "3",
        selectedCondition = AquariumCondition.KOMBINASI,
        sandSyncWithTank = true,
        sandLengthCm = "60",
        sandWidthCm = "30",
        sandHeightCm = "5",
        stoneCount = 2,
        stoneWeightGramsPerUnit = "1250", // 2500 g / 2.5 = 1.0 L
        woodCount = 1,
        woodWeightGramsPerUnit = "850", // 850 g / 0.85 = 1.0 L
        woodSubmergedPercent = 100,
      )
    val entity = AquariumProfileEntity.fromUiState("Tank Ruang Tamu", state)
    val summary = entity.toShareSummaryText()

    assertTrue(summary.contains("Tank Ruang Tamu"))
    assertTrue(summary.contains("60 × 30 × 35 cm"))
    assertTrue(summary.contains("1250 g/buah"))
    assertTrue(summary.contains("850 g/potong"))
    assertTrue(summary.contains("46.60 Liter"))
    assertTrue(summary.contains("Galon US"))
    assertTrue(summary.contains("Galon Imperial"))
    assertTrue(summary.contains("46,600 cm³"))
  }
}
