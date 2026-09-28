package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.CeifadorDatabase
import com.example.data.db.SavedProfile
import com.example.data.model.GeneratedHud
import com.example.data.model.HudFingerLayout
import com.example.data.model.HudGenerator
import com.example.data.model.SensitivityConfig
import com.example.data.model.Weapon
import com.example.data.model.WeaponCategory
import com.example.data.repository.ProfileRepository
import com.example.data.repository.WeaponRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CeifadorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CeifadorDatabase.getInstance(application)
    private val profileRepository = ProfileRepository(db.ceifadorDao())

    val allWeapons = WeaponRepository.weapons

    private val _selectedWeapon = MutableStateFlow(allWeapons.first())
    val selectedWeapon: StateFlow<Weapon> = _selectedWeapon.asStateFlow()

    private val _activeSensi = MutableStateFlow(allWeapons.first().proPreset)
    val activeSensi: StateFlow<SensitivityConfig> = _activeSensi.asStateFlow()

    private val _selectedCategory = MutableStateFlow(WeaponCategory.ALL)
    val selectedCategory: StateFlow<WeaponCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentDpi = MutableStateFlow(580)
    val currentDpi: StateFlow<Int> = _currentDpi.asStateFlow()

    private val _currentPollingHz = MutableStateFlow(240)
    val currentPollingHz: StateFlow<Int> = _currentPollingHz.asStateFlow()

    private val _fingerLayout = MutableStateFlow(HudFingerLayout.TWO_FINGERS)
    val fingerLayout: StateFlow<HudFingerLayout> = _fingerLayout.asStateFlow()

    val filteredWeapons: StateFlow<List<Weapon>> = combine(
        _selectedCategory,
        _searchQuery
    ) { category, query ->
        allWeapons.filter { weapon ->
            val matchCategory = category == WeaponCategory.ALL || weapon.category == category
            val matchQuery = query.isBlank() ||
                weapon.name.contains(query, ignoreCase = true) ||
                weapon.category.displayName.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), allWeapons)

    val generatedHud: StateFlow<GeneratedHud> = combine(
        _selectedWeapon,
        _activeSensi,
        _currentDpi,
        _fingerLayout
    ) { weapon, sensi, dpi, layout ->
        HudGenerator.generateHudForSensi(weapon, sensi, dpi, layout)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HudGenerator.generateHudForSensi(
            allWeapons.first(),
            allWeapons.first().proPreset,
            580,
            HudFingerLayout.TWO_FINGERS
        )
    )

    val savedProfiles: StateFlow<List<SavedProfile>> = profileRepository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectWeapon(weapon: Weapon) {
        _selectedWeapon.value = weapon
        _activeSensi.value = weapon.proPreset
    }

    fun setCategory(category: WeaponCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSensi(config: SensitivityConfig) {
        _activeSensi.value = config
    }

    fun updateDpi(dpi: Int) {
        _currentDpi.value = dpi.coerceIn(320, 1600)
    }

    fun updatePollingHz(hz: Int) {
        _currentPollingHz.value = hz
    }

    fun setFingerLayout(layout: HudFingerLayout) {
        _fingerLayout.value = layout
    }

    fun applyProPreset() {
        val weapon = _selectedWeapon.value
        _activeSensi.value = weapon.proPreset
    }

    fun applyHighSpeedPreset() {
        val current = _activeSensi.value
        _activeSensi.value = current.copy(
            geral = 198,
            redDot = 195,
            scope2x = 188,
            scope4x = 175,
            scopeAwm = 65,
            freeLook = 160,
            buttonSize = 42,
            dragTechnique = "Puxada Ultra Rápida de Rush"
        )
    }

    fun applyStablePreset() {
        val current = _activeSensi.value
        _activeSensi.value = current.copy(
            geral = 172,
            redDot = 174,
            scope2x = 165,
            scope4x = 155,
            scopeAwm = 50,
            freeLook = 125,
            buttonSize = 52,
            dragTechnique = "Puxada Firme Anti-Treme"
        )
    }

    fun applyOneShotPreset() {
        val current = _activeSensi.value
        _activeSensi.value = current.copy(
            geral = 195,
            redDot = 190,
            scope2x = 172,
            scope4x = 160,
            scopeAwm = 55,
            freeLook = 150,
            buttonSize = 40,
            buttonPosition = 11,
            dragTechnique = "Puxada em Gancho de Um Tiro (X1)"
        )
    }

    fun saveCurrentProfile(customName: String, notes: String) {
        viewModelScope.launch {
            val weapon = _selectedWeapon.value
            val sensi = _activeSensi.value
            val profile = SavedProfile(
                profileName = customName.ifBlank { "Ceifador - ${weapon.name}" },
                weaponId = weapon.id,
                weaponName = weapon.name,
                categoryName = weapon.category.displayName,
                geral = sensi.geral,
                redDot = sensi.redDot,
                scope2x = sensi.scope2x,
                scope4x = sensi.scope4x,
                scopeAwm = sensi.scopeAwm,
                freeLook = sensi.freeLook,
                buttonSize = sensi.buttonSize,
                buttonPosition = sensi.buttonPosition,
                dpi = _currentDpi.value,
                pollingRateHz = _currentPollingHz.value,
                dragTechnique = sensi.dragTechnique,
                notes = notes,
                isFavorite = false
            )
            profileRepository.saveProfile(profile)
        }
    }

    fun loadProfile(profile: SavedProfile) {
        val weapon = allWeapons.firstOrNull { it.id == profile.weaponId } ?: allWeapons.first()
        _selectedWeapon.value = weapon
        _activeSensi.value = SensitivityConfig(
            geral = profile.geral,
            redDot = profile.redDot,
            scope2x = profile.scope2x,
            scope4x = profile.scope4x,
            scopeAwm = profile.scopeAwm,
            freeLook = profile.freeLook,
            buttonSize = profile.buttonSize,
            buttonPosition = profile.buttonPosition,
            recommendedDpi = profile.dpi,
            dragTechnique = profile.dragTechnique,
            notes = profile.notes
        )
        _currentDpi.value = profile.dpi
        _currentPollingHz.value = profile.pollingRateHz
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch {
            profileRepository.deleteProfile(id)
        }
    }

    fun toggleFavorite(id: Long, currentFav: Boolean) {
        viewModelScope.launch {
            profileRepository.toggleFavorite(id, !currentFav)
        }
    }
}
