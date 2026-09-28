package com.example.data.repository

import com.example.data.model.DpiPreset

object DpiRepository {

    val devicePresets: List<DpiPreset> = listOf(
        DpiPreset(
            brand = "Xiaomi / POCO / Redmi",
            modelTier = "Snapdragon / Dimensity (Ex: POCO X3/X5/X6, Redmi Note)",
            defaultStockDpi = 392,
            recommendedCeifadorDpi = 580,
            maxSafeDpi = 800,
            recommendedTouchPollingHz = 240,
            notes = "Ative 'Game Turbo' da MIUI/HyperOS e desative 'Otimização MIUI' apenas se houver conflito de toque. Resposta ao toque configurada em 240Hz ou 480Hz nativo."
        ),
        DpiPreset(
            brand = "Samsung Galaxy",
            modelTier = "Série A / M / S (Ex: A34, A54, S21, S23, S24)",
            defaultStockDpi = 411,
            recommendedCeifadorDpi = 600,
            maxSafeDpi = 850,
            recommendedTouchPollingHz = 240,
            notes = "Ative 'Sensibilidade do Toque' nas configurações de visor (para películas de vidro) e use o 'Game Booster' com taxa de atualização de 120Hz."
        ),
        DpiPreset(
            brand = "Motorola Moto",
            modelTier = "Moto G / Edge (Ex: G54, G84, Edge 30/40/50)",
            defaultStockDpi = 411,
            recommendedCeifadorDpi = 560,
            maxSafeDpi = 780,
            recommendedTouchPollingHz = 240,
            notes = "Abra o 'Moto Game' e ative 'Bloqueio de gestos' para evitar que o swipe de capa acione o gesto de voltar do Android."
        ),
        DpiPreset(
            brand = "Realme / OnePlus",
            modelTier = "Realme GT / Número (Ex: Realme 11, GT Neo)",
            defaultStockDpi = 360,
            recommendedCeifadorDpi = 590,
            maxSafeDpi = 820,
            recommendedTouchPollingHz = 360,
            notes = "A Realme UI possui amostragem de toque instantânea de até 360Hz. O arrasto de capa inicial é praticamente instantâneo."
        ),
        DpiPreset(
            brand = "iPhone / iPad (iOS)",
            modelTier = "iPhone 11 ao 16 Pro Max",
            defaultStockDpi = 400,
            recommendedCeifadorDpi = 120, // Ciclos do controle assistivo
            maxSafeDpi = 120,
            recommendedTouchPollingHz = 240,
            notes = "No iOS, utilize 'Controle Assistivo' com Escaneamento de 120 ciclos e cursor refinado em 120 para máxima precisão de toque no FF."
        ),
        DpiPreset(
            brand = "Emulador PC (BlueStacks / MSI)",
            modelTier = "Mouse Gamer (Logitech, Razer, Redragon, etc.)",
            defaultStockDpi = 800,
            recommendedCeifadorDpi = 1000,
            maxSafeDpi = 3200,
            recommendedTouchPollingHz = 1000,
            notes = "No emulador: ajuste DPI do mouse para 800-1000 e Polling Rate em 1000Hz (1ms). Sensi X: 1.25 a 1.65 / Sensi Y (subida de capa): 0.65 a 0.85."
        )
    )

    val safeDpiGuidelines: List<String> = listOf(
        "A 'Menor Largura' (DPI) altera a densidade lógica de pixels (dp) que o sistema Android renderiza. Quanto maior a Menor Largura, menores os elementos na tela e mais sensível é o movimento da câmera.",
        "LIMITE DE SEGURANÇA: Nunca defina uma Menor Largura superior a 960 no Android sem verificar a resolução nativa da tela. Valores absurdos (ex: 1440 ou 2000) podem forçar a interface do sistema (SystemUI) a crashar em loop.",
        "RELAÇÃO COM POLLING RATE: A taxa de amostragem de toque (Touch Polling Rate em Hz) determina quantas vezes por segundo a tela lê a ponta do seu polegar. Uma tela de 240Hz lê a cada 4.1ms, enquanto 120Hz lê a cada 8.3ms. Quanto maior a taxa de polling, mais uniforme é a curva do capa.",
        "SE O TIRO ESTIVER PASSANDO DA CABEÇA: Reduza 30 a 50 pontos de DPI ou reduza a sensibilidade 'Geral' em 8 pontos. Não altere os dois ao mesmo tempo para não perder o ajuste fino.",
        "SE A MIRA ESTIVER PRENDENDO NO PEITO: Aumente o DPI em +40 ou aumente o 'Ponto Vermelho' em +5 e diminua o tamanho do Botão de Atirar em 3%."
    )
}
