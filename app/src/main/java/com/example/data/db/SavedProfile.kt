package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_sensi_profiles")
data class SavedProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileName: String,
    val weaponId: String,
    val weaponName: String,
    val categoryName: String,
    val geral: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val scopeAwm: Int,
    val freeLook: Int,
    val buttonSize: Int,
    val buttonPosition: Int,
    val dpi: Int,
    val pollingRateHz: Int,
    val dragTechnique: String,
    val notes: String = "",
    val isFavorite: Boolean = false,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
