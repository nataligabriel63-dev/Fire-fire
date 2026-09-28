package com.example.data.model

enum class HudFingerLayout(val title: String, val shortDesc: String) {
    TWO_FINGERS("2 Dedos (Padrão Mobile)", "Ideal para a grande maioria dos jogadores de celular. Foco em ergonomia dos polegares."),
    THREE_FINGERS("3 Dedos (Gatilho Superior)", "Indicador esquerdo dedicado para gelo rápido ou tiro esquerdo. Agilidade de campeonato."),
    FOUR_FINGERS("4 Dedos (Claw Tático Pro)", "Máximo controle competitivo. Disparar, mirar, agachar e pular em milissegundos simultâneos.")
}

data class HudElement(
    val id: String,
    val name: String,
    val sizePercent: Int,       // 10% to 100%
    val posXPercent: Float,     // 0.0f (left) to 1.0f (right)
    val posYPercent: Float,     // 0.0f (top) to 1.0f (bottom)
    val opacityPercent: Int,    // e.g. 80%
    val functionRole: String,
    val proRecommendation: String
)

data class GeneratedHud(
    val weaponName: String,
    val fingerLayout: HudFingerLayout,
    val calculatedButtonSize: Int,
    val calculatedButtonYPercent: Float,
    val elements: List<HudElement>,
    val technicalExplanation: String
)

object HudGenerator {

    fun generateHudForSensi(
        weapon: Weapon,
        sensi: SensitivityConfig,
        dpi: Int,
        layout: HudFingerLayout
    ): GeneratedHud {
        // Calculate optimal fire button size:
        // Higher sensitivity & higher DPI = smaller button for precise anchoring without accidental drag clipping
        // Lower sensitivity = larger button for reliable thumb registration
        val baseSizeFromWeapon = weapon.defaultButtonSize
        val dpiFactor = when {
            dpi >= 720 -> -4
            dpi >= 600 -> -2
            dpi <= 420 -> +4
            else -> 0
        }
        val sensiFactor = when {
            sensi.geral >= 195 -> -3
            sensi.geral >= 180 -> -1
            sensi.geral <= 160 -> +3
            else -> 0
        }
        val finalFireButtonSize = (baseSizeFromWeapon + dpiFactor + sensiFactor).coerceIn(38, 65)

        // Calculate vertical position of fire button:
        // Weapons requiring long upward drags (Desert Eagle, M1887, Shotguns) need lower button position (more vertical travel space)
        val finalButtonY = when (weapon.category) {
            WeaponCategory.DMR -> 0.86f // Desert Eagle: 14% from bottom
            WeaponCategory.DOZE -> 0.84f // M1887: 16% from bottom
            WeaponCategory.SMG -> 0.80f // MP40: 20% from bottom
            WeaponCategory.AR -> 0.78f // AK47/M4A1: 22% from bottom
            WeaponCategory.SNIPER -> 0.76f // AWM: 24% from bottom
            WeaponCategory.PISTOL -> 0.84f
            WeaponCategory.ALL -> 0.80f
        }

        val explanation = when {
            sensi.geral >= 190 -> "Para sensibilidade alta (${sensi.geral}) e DPI ${dpi}, o HUD Ceifador reduziu o Botão de Tiro para ${finalFireButtonSize}% e o posicionou a ${(100 - (finalButtonY * 100)).toInt()}% da base. Isso expande em 35% o espaço livre de tela acima do botão, impedindo que seu dedo atinja a barra de notificações durante o arraste rápido de capa."
            sensi.geral in 170..189 -> "Com sensibilidade balanceada (${sensi.geral}) e DPI ${dpi}, o Botão de Tiro foi calibrado em ${finalFireButtonSize}% com altura de ${(100 - (finalButtonY * 100)).toInt()}%. Esta configuração equilibra estabilidade contra dispersão lateral de recuo e velocidade de reação."
            else -> "Para sensibilidade controlada (${sensi.geral}), o botão foi ampliado para ${finalFireButtonSize}% para garantir registro pleno do toque sem falhas no primeiro tiro."
        }

        val elements = when (layout) {
            HudFingerLayout.TWO_FINGERS -> listOf(
                HudElement(
                    id = "fire_btn",
                    name = "Botão de Atirar (Direito)",
                    sizePercent = finalFireButtonSize,
                    posXPercent = 0.84f,
                    posYPercent = finalButtonY,
                    opacityPercent = 90,
                    functionRole = "Disparo Principal & Subida de Capa",
                    proRecommendation = "Calibrado com precisão de ${finalFireButtonSize}% para a ${weapon.name}."
                ),
                HudElement(
                    id = "jump_btn",
                    name = "Botão de Pular",
                    sizePercent = 65,
                    posXPercent = 0.90f,
                    posYPercent = 0.58f,
                    opacityPercent = 85,
                    functionRole = "Pulo de Emulador / Desvio Tático",
                    proRecommendation = "Posicionado logo acima do botão de agachar para transição fluida."
                ),
                HudElement(
                    id = "crouch_btn",
                    name = "Botão de Agachar",
                    sizePercent = 60,
                    posXPercent = 0.78f,
                    posYPercent = 0.68f,
                    opacityPercent = 85,
                    functionRole = "Gelo Agachando / Tiro Agachado",
                    proRecommendation = "Próximo ao botão de tiro para sequência rápida de capa + agachar."
                ),
                HudElement(
                    id = "gloo_btn",
                    name = "Granada / Parede de Gelo",
                    sizePercent = 95,
                    posXPercent = 0.16f,
                    posYPercent = 0.62f,
                    opacityPercent = 95,
                    functionRole = "Gelo Rápido de Emergência",
                    proRecommendation = "Tamanho maximizado (95%) no polegar esquerdo para defesa instantânea."
                ),
                HudElement(
                    id = "weapon_switch_btn",
                    name = "Troca Rápida de Armas",
                    sizePercent = 70,
                    posXPercent = 0.38f,
                    posYPercent = 0.78f,
                    opacityPercent = 80,
                    functionRole = "Troca Instantânea pós-disparo",
                    proRecommendation = "Fundamental para M1887 e Desert Eagle para cancelar animação."
                ),
                HudElement(
                    id = "scope_btn",
                    name = "Botão de Abrir Mira",
                    sizePercent = 65,
                    posXPercent = 0.88f,
                    posYPercent = 0.40f,
                    opacityPercent = 80,
                    functionRole = "Mira Ótica (Red Dot, 2x, 4x, AWM)",
                    proRecommendation = "Canto direito superior para acesso rápido com o polegar."
                ),
                HudElement(
                    id = "run_btn",
                    name = "Botão de Correr",
                    sizePercent = 75,
                    posXPercent = 0.22f,
                    posYPercent = 0.38f,
                    opacityPercent = 75,
                    functionRole = "Mobilidade Tática",
                    proRecommendation = "Posição clássica intermediária esquerda."
                ),
                HudElement(
                    id = "medkit_btn",
                    name = "Kit Médico",
                    sizePercent = 65,
                    posXPercent = 0.08f,
                    posYPercent = 0.44f,
                    opacityPercent = 70,
                    functionRole = "Cura e Inalador",
                    proRecommendation = "Borda lateral esquerda para não atrapalhar o campo de visão."
                )
            )

            HudFingerLayout.THREE_FINGERS -> listOf(
                HudElement(
                    id = "fire_btn",
                    name = "Botão de Atirar (Direito)",
                    sizePercent = finalFireButtonSize,
                    posXPercent = 0.84f,
                    posYPercent = finalButtonY,
                    opacityPercent = 90,
                    functionRole = "Subida de Capa Principal",
                    proRecommendation = "Ajustado especificamente para arraste limpo."
                ),
                HudElement(
                    id = "left_fire_btn",
                    name = "Botão de Tiro Esquerdo (Gatilho)",
                    sizePercent = 85,
                    posXPercent = 0.16f,
                    posYPercent = 0.15f,
                    opacityPercent = 90,
                    functionRole = "Gelo Agachando & Disparo Suporte",
                    proRecommendation = "Operado pelo indicador esquerdo no topo da tela."
                ),
                HudElement(
                    id = "gloo_btn",
                    name = "Parede de Gelo",
                    sizePercent = 90,
                    posXPercent = 0.16f,
                    posYPercent = 0.60f,
                    opacityPercent = 90,
                    functionRole = "Acionamento de Gelo com Polegar",
                    proRecommendation = "Combina com o gatilho superior para gelo em menos de 100ms."
                ),
                HudElement(
                    id = "jump_btn",
                    name = "Botão de Pular",
                    sizePercent = 70,
                    posXPercent = 0.90f,
                    posYPercent = 0.55f,
                    opacityPercent = 85,
                    functionRole = "Pulo Tático",
                    proRecommendation = "Ajustado para o polegar direito."
                ),
                HudElement(
                    id = "crouch_btn",
                    name = "Botão de Agachar",
                    sizePercent = 70,
                    posXPercent = 0.78f,
                    posYPercent = 0.68f,
                    opacityPercent = 85,
                    functionRole = "Agachar Dinâmico",
                    proRecommendation = "Tamanho 70% para toque sem erro durante o rush."
                ),
                HudElement(
                    id = "weapon_switch_btn",
                    name = "Troca Rápida de Armas",
                    sizePercent = 75,
                    posXPercent = 0.45f,
                    posYPercent = 0.75f,
                    opacityPercent = 85,
                    functionRole = "Cancelamento de Recarga",
                    proRecommendation = "Centralizado no alcance imediato de ambos os polegares."
                ),
                HudElement(
                    id = "scope_btn",
                    name = "Botão de Abrir Mira",
                    sizePercent = 65,
                    posXPercent = 0.88f,
                    posYPercent = 0.38f,
                    opacityPercent = 80,
                    functionRole = "Mira Ótica",
                    proRecommendation = "Canto direito superior."
                ),
                HudElement(
                    id = "run_btn",
                    name = "Botão de Correr",
                    sizePercent = 75,
                    posXPercent = 0.22f,
                    posYPercent = 0.38f,
                    opacityPercent = 75,
                    functionRole = "Movimentação Pro",
                    proRecommendation = "Espaçamento ergonômico."
                )
            )

            HudFingerLayout.FOUR_FINGERS -> listOf(
                HudElement(
                    id = "fire_btn",
                    name = "Botão de Atirar (Direito)",
                    sizePercent = finalFireButtonSize,
                    posXPercent = 0.85f,
                    posYPercent = finalButtonY,
                    opacityPercent = 90,
                    functionRole = "Subida de Capa (Polegar Direito)",
                    proRecommendation = "Puxada vertical limpa."
                ),
                HudElement(
                    id = "left_fire_btn",
                    name = "Botão de Tiro Esquerdo",
                    sizePercent = 90,
                    posXPercent = 0.14f,
                    posYPercent = 0.14f,
                    opacityPercent = 95,
                    functionRole = "Disparo com Indicador Esquerdo",
                    proRecommendation = "Gatilho de gelo instantâneo."
                ),
                HudElement(
                    id = "jump_btn",
                    name = "Botão de Pular",
                    sizePercent = 75,
                    posXPercent = 0.85f,
                    posYPercent = 0.15f,
                    opacityPercent = 90,
                    functionRole = "Pulo com Indicador Direito",
                    proRecommendation = "Pulo sem tirar o polegar da mira ou do botão de tiro."
                ),
                HudElement(
                    id = "crouch_btn",
                    name = "Botão de Agachar",
                    sizePercent = 75,
                    posXPercent = 0.75f,
                    posYPercent = 0.65f,
                    opacityPercent = 85,
                    functionRole = "Agachar Rápido",
                    proRecommendation = "Posição ergonômica para combo de agachado."
                ),
                HudElement(
                    id = "gloo_btn",
                    name = "Parede de Gelo",
                    sizePercent = 95,
                    posXPercent = 0.18f,
                    posYPercent = 0.60f,
                    opacityPercent = 95,
                    functionRole = "Seleção de Granada",
                    proRecommendation = "Tamanho 95% para não errar no susto."
                ),
                HudElement(
                    id = "weapon_switch_btn",
                    name = "Troca Rápida de Armas",
                    sizePercent = 80,
                    posXPercent = 0.50f,
                    posYPercent = 0.16f,
                    opacityPercent = 85,
                    functionRole = "Troca Rápida Central Topo",
                    proRecommendation = "Alcançável pelo indicador para combo de duas dozes ou sniper."
                ),
                HudElement(
                    id = "scope_btn",
                    name = "Botão de Abrir Mira",
                    sizePercent = 70,
                    posXPercent = 0.90f,
                    posYPercent = 0.42f,
                    opacityPercent = 80,
                    functionRole = "Mira Ótica",
                    proRecommendation = "Posicionamento tático lateral."
                ),
                HudElement(
                    id = "run_btn",
                    name = "Botão de Correr",
                    sizePercent = 75,
                    posXPercent = 0.20f,
                    posYPercent = 0.38f,
                    opacityPercent = 75,
                    functionRole = "Sprint Automático",
                    proRecommendation = "Lateral esquerda limpa."
                )
            )
        }

        return GeneratedHud(
            weaponName = weapon.name,
            fingerLayout = layout,
            calculatedButtonSize = finalFireButtonSize,
            calculatedButtonYPercent = finalButtonY,
            elements = elements,
            technicalExplanation = explanation
        )
    }
}
