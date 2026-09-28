package com.example.ui.screens.dpi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DpiCalculator
import com.example.data.model.DpiPreset
import com.example.data.repository.DpiRepository
import com.example.ui.theme.CeifadorBorder
import com.example.ui.theme.CeifadorBorderActive
import com.example.ui.theme.CeifadorCyanGlow
import com.example.ui.theme.CeifadorDarkBg
import com.example.ui.theme.CeifadorEmerald
import com.example.ui.theme.CeifadorRedPrimary
import com.example.ui.theme.CeifadorSurface
import com.example.ui.theme.CeifadorSurfaceHighlight
import com.example.ui.theme.CeifadorSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CeifadorViewModel

@Composable
fun DpiPollingScreen(
    viewModel: CeifadorViewModel,
    modifier: Modifier = Modifier
) {
    val currentDpi by viewModel.currentDpi.collectAsStateWithLifecycle()
    val activeSensi by viewModel.activeSensi.collectAsStateWithLifecycle()
    val currentPollingHz by viewModel.currentPollingHz.collectAsStateWithLifecycle()

    var selectedPresetBrand by remember { mutableStateOf<DpiPreset?>(null) }
    var showStepByStepGuide by remember { mutableStateOf(false) }

    val edpi = DpiCalculator.calculateEdpi(currentDpi, activeSensi.geral)
    val edpiStatus = DpiCalculator.getEdpiStatus(edpi)
    val latencyMs = DpiCalculator.getLatencyMsForPolling(currentPollingHz)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CeifadorDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CeifadorCyanGlow)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OTIMIZAÇÃO DE DPI & POLLING RATE",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "CALIBRAÇÃO TÁTICA DE LATÊNCIA E SENSIBILIDADE EFETIVA (eDPI)",
                    color = CeifadorCyanGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Live eDPI & Interaction Analyzer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_edpi_analyzer"),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorderActive)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SENSIBILIDADE EFETIVA (eDPI)",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = edpi.toString(),
                                color = Color(edpiStatus.colorHex),
                                fontSize = 28.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(edpiStatus.colorHex).copy(alpha = 0.15f))
                                .border(1.dp, Color(edpiStatus.colorHex), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = edpiStatus.label,
                                color = Color(edpiStatus.colorHex),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = edpiStatus.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // DPI Slider Controller
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Menor Largura / DPI do Dispositivo",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${currentDpi} DPI",
                            color = CeifadorCyanGlow,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = currentDpi.toFloat(),
                        onValueChange = { viewModel.updateDpi(it.toInt()) },
                        valueRange = 320f..1000f,
                        steps = 67,
                        colors = SliderDefaults.colors(
                            thumbColor = CeifadorCyanGlow,
                            activeTrackColor = CeifadorCyanGlow,
                            inactiveTrackColor = CeifadorBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_dpi")
                    )

                    // Quick DPI presets chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(411, 480, 540, 600, 720, 800).forEach { dpiValue ->
                            val isSel = currentDpi == dpiValue
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) CeifadorCyanGlow.copy(alpha = 0.2f) else CeifadorSurfaceHighlight)
                                    .border(1.dp, if (isSel) CeifadorCyanGlow else CeifadorBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.updateDpi(dpiValue) }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dpiValue.toString(),
                                    color = if (isSel) CeifadorCyanGlow else TextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // In-Game General Sensitivity Integration Reference
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CeifadorSurfaceHighlight)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sensibilidade Geral Ativa no Jogo:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${activeSensi.geral} / 200",
                            color = CeifadorRedPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Polling Rate & Touch Sampling Rate Module
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_polling_rate"),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TAXA DE POLLING / TOUCH SAMPLING",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Frequência de leitura do toque na tela / mouse",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Response Latency badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CeifadorEmerald.copy(alpha = 0.15f))
                                .border(1.dp, CeifadorEmerald, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = String.format("%.2f ms", latencyMs),
                                color = CeifadorEmerald,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Selecione a taxa de amostragem de toque do seu aparelho ou mouse:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Polling rate selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            120 to "120Hz (Padrão)",
                            240 to "240Hz (Gamer)",
                            360 to "360Hz (Pro)",
                            480 to "480Hz (Ultra)",
                            1000 to "1000Hz (Mouse)"
                        ).forEach { (hz, label) ->
                            val isSelected = currentPollingHz == hz
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CeifadorCyanGlow.copy(alpha = 0.2f) else CeifadorSurfaceHighlight)
                                    .border(
                                        1.dp,
                                        if (isSelected) CeifadorCyanGlow else CeifadorBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updatePollingHz(hz) }
                                    .padding(vertical = 8.dp, horizontal = 2.dp)
                                    .testTag("polling_${hz}hz"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${hz}Hz",
                                        color = if (isSelected) CeifadorCyanGlow else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = String.format("%.1fms", 1000.0 / hz),
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Technical insight on how Polling Rate impacts Free Fire
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CeifadorSurfaceHighlight)
                            .border(1.dp, CeifadorBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = CeifadorCyanGlow,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Como o Polling Rate afeta a puxada: Em 120Hz, o jogo lê seu dedo a cada 8.3ms. Em 240Hz/360Hz, o registro cai para menos de 4ms. Isso significa que os primeiros 2 milímetros de arrancada do botão de tiro são computados mais cedo pelo motor do Free Fire, eliminando a sensação de 'mira pesada' sem precisar inflar a sensibilidade para 200.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Step by Step: How to find and configure optimal DPI safely
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStepByStepGuide = !showStepByStepGuide }
                    .testTag("card_step_by_step_guide"),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = CeifadorRedPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "COMO CONFIGURAR O DPI NO ANDROID",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Icon(
                            imageVector = if (showStepByStepGuide) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AnimatedVisibility(visible = showStepByStepGuide) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InstructionStep(
                                number = "1",
                                title = "Ativar Modo Desenvolvedor",
                                text = "Acesse Configurações > Sobre o Telefone > Toque 7 vezes consecutivas em 'Número da Versão' ou 'Versão da MIUI/Build'."
                            )
                            InstructionStep(
                                number = "2",
                                title = "Localizar Menor Largura",
                                text = "Vá em Sistema > Opções do Desenvolvedor > Role até a seção 'Desenho' e clique em 'Menor Largura' (ou 'Largura Mínima')."
                            )
                            InstructionStep(
                                number = "3",
                                title = "Anotar o Valor Padrão",
                                text = "IMPORTANTE: Antes de mudar, anote o valor de fábrica do seu aparelho (normalmente entre 360 e 411) para poder restaurar quando não estiver jogando."
                            )
                            InstructionStep(
                                number = "4",
                                title = "Inserir o DPI Ceifador Recomendado",
                                text = "Defina o valor calculado para sua arma (ex: 580 ou 600). O texto do sistema diminuirá proporcionalmente, aumentando a amplitude da mira no jogo."
                            )
                            InstructionStep(
                                number = "5",
                                title = "Regra de Ouro da Calibração",
                                text = "Se a mira estiver passando da cabeça do boneco no treino, reduza 30 pontos de DPI. Se estiver colando no peito, aumente 40 pontos."
                            )
                        }
                    }
                }
            }
        }

        // Hardware Presets by Manufacturer
        item {
            Text(
                text = "PREDEFINIÇÕES POR MARCA & PROCESSADOR",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        items(DpiRepository.devicePresets) { preset ->
            val isExpanded = selectedPresetBrand?.brand == preset.brand
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedPresetBrand = if (isExpanded) null else preset
                    },
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isExpanded) CeifadorCyanGlow.copy(alpha = 0.6f) else CeifadorBorder
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = preset.brand,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = preset.modelTier,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CeifadorSurfaceHighlight)
                                    .border(1.dp, CeifadorBorderActive, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${preset.recommendedCeifadorDpi} DPI",
                                    color = CeifadorCyanGlow,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "DPI de Fábrica: ${preset.defaultStockDpi}", color = TextSecondary, fontSize = 11.sp)
                                Text(text = "Limite Seguro: ${preset.maxSafeDpi}", color = CeifadorRedPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = preset.notes,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CeifadorCyanGlow.copy(alpha = 0.15f))
                                    .clickable {
                                        viewModel.updateDpi(preset.recommendedCeifadorDpi)
                                        viewModel.updatePollingHz(preset.recommendedTouchPollingHz)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .align(Alignment.End)
                            ) {
                                Text(
                                    text = "Aplicar ao App",
                                    color = CeifadorCyanGlow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Safety Warnings Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorRedPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CeifadorRedPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AVISO TÉCNICO DE SEGURANÇA",
                            color = CeifadorRedPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nunca configure valores de DPI acima de 960 no Android sem conhecer a densidade da tela do seu processador. Valores excessivos podem causar falhas no SystemUI. O Ceifador Sensi FF limita as sugestões estritamente a parâmetros estáveis e homologados.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionStep(
    number: String,
    title: String,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CeifadorSurfaceHighlight)
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(CeifadorRedPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
