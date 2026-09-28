package com.example.data.model

enum class DevicePlatform(val title: String) {
    ANDROID_MOBILE("Dispositivo Móvel (Android)"),
    IOS_IPHONE("Dispositivo Móvel (iOS)"),
    PC_EMULATOR("Emulador / Mouse (PC)")
}

data class DpiPreset(
    val brand: String,
    val modelTier: String,
    val defaultStockDpi: Int,
    val recommendedCeifadorDpi: Int,
    val maxSafeDpi: Int,
    val recommendedTouchPollingHz: Int,
    val notes: String
)

enum class EdpiStatus(val label: String, val description: String, val colorHex: Long) {
    UNDER_AIM("Under-Aim (Baixa Demais)", "Arrasto muito pesado. A mira tende a prender no peito do adversário e não alcançar a cabeça a tempo no rush.", 0xFFD97706),
    BALANCED_PRO("Calibração Ceifador Pro", "Ponto de equilíbrio perfeito para subir capa consistente sem espalhar tiro além do capacete.", 0xFF10B981),
    HIGH_COMPETITIVE("Alta Competitiva (Rush)", "Altíssima velocidade de virada de tela e resposta de capa instantânea em curta distância. Exige controle firme.", 0xFF38BDF8),
    OVER_AIM("Over-Aim (Passando da Cabeça)", "Sensibilidade excessiva. A mira treme no disparo contínuo ou a bala passa pelo ar acima do adversário.", 0xFFEF4444)
}

object DpiCalculator {
    fun calculateEdpi(dpi: Int, inGameGeralSensi: Int): Int {
        // eDPI formula: DPI * (inGameSensi / 100.0)
        return ((dpi * inGameGeralSensi) / 100.0).toInt()
    }

    fun getEdpiStatus(edpi: Int): EdpiStatus {
        return when {
            edpi < 550 -> EdpiStatus.UNDER_AIM
            edpi in 550..950 -> EdpiStatus.BALANCED_PRO
            edpi in 951..1350 -> EdpiStatus.HIGH_COMPETITIVE
            else -> EdpiStatus.OVER_AIM
        }
    }

    fun getLatencyMsForPolling(hz: Int): Double {
        if (hz <= 0) return 0.0
        return 1000.0 / hz
    }
}
