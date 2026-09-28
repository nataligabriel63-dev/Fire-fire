package com.example.data.repository

import com.example.data.db.CeifadorDao
import com.example.data.db.SavedProfile
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val dao: CeifadorDao) {

    val allProfiles: Flow<List<SavedProfile>> = dao.getAllProfiles()

    fun getProfilesForWeapon(weaponId: String): Flow<List<SavedProfile>> {
        return dao.getProfilesForWeapon(weaponId)
    }

    suspend fun saveProfile(profile: SavedProfile): Long {
        return dao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: SavedProfile) {
        dao.updateProfile(profile)
    }

    suspend fun deleteProfile(id: Long) {
        dao.deleteProfileById(id)
    }

    suspend fun toggleFavorite(id: Long, isFav: Boolean) {
        dao.toggleFavorite(id, isFav)
    }
}
