package com.example.data

import kotlinx.coroutines.flow.Flow

class AquariumProfileRepository(private val dao: AquariumProfileDao) {
  val allProfiles: Flow<List<AquariumProfileEntity>> = dao.getAllProfiles()

  suspend fun insertProfile(profile: AquariumProfileEntity): Long {
    return dao.insertProfile(profile)
  }

  suspend fun updateProfile(profile: AquariumProfileEntity) {
    dao.updateProfile(profile)
  }

  suspend fun deleteProfileById(id: Long) {
    dao.deleteProfileById(id)
  }
}
