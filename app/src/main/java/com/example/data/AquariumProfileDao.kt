package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AquariumProfileDao {
  @Query("SELECT * FROM aquarium_profiles ORDER BY updatedAtMillis DESC")
  fun getAllProfiles(): Flow<List<AquariumProfileEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProfile(profile: AquariumProfileEntity): Long

  @Update
  suspend fun updateProfile(profile: AquariumProfileEntity)

  @Query("DELETE FROM aquarium_profiles WHERE id = :id")
  suspend fun deleteProfileById(id: Long)
}
