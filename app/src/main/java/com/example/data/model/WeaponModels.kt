package com.example.data.model

enum class WeaponCategory(val displayName: String, val badge: String) {
    ALL("Todas", "ALL"),
    DOZE("Escopetas (Doze)", "DOZE"),
    SMG("Submetralhadoras (SMG)", "SMG"),
    AR("Rifles de Assalto (AR)", "AR"),
    DMR("Atirador (Um Tiro)", "DMR"),
    SNIPER("Snipers (Precisão)", "SNP"),
    PISTOL("Secundárias / Pistolas", "SEC")
}

data class Weapon(
    val id: String,
    val name: String,
    val category: WeaponCategory,
    val fireRateStars: Int, // 1 to 5
    val damageStars: Int,   // 1 to 5
    val recoilDifficulty: String, // "Baixo", "Médio", "Severo", "Seco"
    val optimalEngagementRange: String, // "Curta Distância", "Média Distância", "Longa Distância"
    val defaultButtonSize: Int, // e.g. 45%
    val defaultButtonPosition: Int, // e.g. 14%
    val dragTechnique: String, // e.g. "Meia-lua rápida", "Puxada reta com alívio"
    val dragVelocityMs: Int, // ideal drag duration in ms
    val proPreset: SensitivityConfig,
    val stabilityTips: String,
    val description: String
)

data class SensitivityConfig(
    val geral: Int = 185,
    val redDot: Int = 180,
    val scope2x: Int = 175,
    val scope4x: Int = 165,
    val scopeAwm: Int = 60,
    val freeLook: Int = 140,
    val buttonSize: Int = 46,
    val buttonPosition: Int = 15,
    val recommendedDpi: Int = 580,
    val dragTechnique: String = "Puxada Reta Fluida",
    val notes: String = ""
) {
    fun copyWithAdjustment(delta: Int): SensitivityConfig {
        return this.copy(
            geral = (geral + delta).coerceIn(0, 200),
            redDot = (redDot + delta).coerceIn(0, 200),
            scope2x = (scope2x + delta).coerceIn(0, 200),
            scope4x = (scope4x + delta).coerceIn(0, 200),
            scopeAwm = (scopeAwm + (delta / 2)).coerceIn(0, 200),
            freeLook = (freeLook + delta).coerceIn(0, 200)
        )
    }
}
