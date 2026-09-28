package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CeifadorDao {
    @Query("SELECT * FROM saved_sensi_profiles ORDER BY isFavorite DESC, createdAtTimestamp DESC")
    fun getAllProfiles(): Flow<List<SavedProfile>>

    @Query("SELECT * FROM saved_sensi_profiles WHERE weaponId = :weaponId ORDER BY createdAtTimestamp DESC")
    fun getProfilesForWeapon(weaponId: String): Flow<List<SavedProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: SavedProfile): Long

    @Update
    suspend fun updateProfile(profile: SavedProfile)

    @Query("DELETE FROM saved_sensi_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Long)

    @Query("UPDATE saved_sensi_profiles SET isFavorite = :isFav WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFav: Boolean)
}
