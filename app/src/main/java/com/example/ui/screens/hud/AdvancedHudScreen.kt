package com.example.ui.screens.hud

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HudElement
import com.example.data.model.HudFingerLayout
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
fun AdvancedHudScreen(
    viewModel: CeifadorViewModel,
    onNavigateToSimulator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedWeapon by viewModel.selectedWeapon.collectAsStateWithLifecycle()
    val activeSensi by viewModel.activeSensi.collectAsStateWithLifecycle()
    val currentDpi by viewModel.currentDpi.collectAsStateWithLifecycle()
    val fingerLayout by viewModel.fingerLayout.collectAsStateWithLifecycle()
    val generatedHud by viewModel.generatedHud.collectAsStateWithLifecycle()

    var selectedElement by remember { mutableStateOf<HudElement?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CeifadorDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CeifadorRedPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HUD GERADO POR SENSIBILIDADE",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "DIMENSIONAMENTO E COORDENADAS VINCULADAS À ${selectedWeapon.name.uppercase()}",
                    color = CeifadorCyanGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Sensitivity Sync & Calculation Info Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_hud_sync_info"),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorderActive)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CALIBRAÇÃO ATIVA",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${selectedWeapon.name} • Sensi ${activeSensi.geral}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CeifadorRedPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, CeifadorRedPrimary, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Botão: ${generatedHud.calculatedButtonSize}%",
                                    color = CeifadorRedPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CeifadorCyanGlow.copy(alpha = 0.15f))
                                    .border(1.dp, CeifadorCyanGlow, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Base: ${(100 - (generatedHud.calculatedButtonYPercent * 100)).toInt()}%",
                                    color = CeifadorCyanGlow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = generatedHud.technicalExplanation,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Finger Layout Selector (2, 3, 4 Dedos)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ESTILO DE PEGADA / QUANTIDADE DE DEDOS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HudFingerLayout.values().forEach { layout ->
                        val isSelected = layout == fingerLayout
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CeifadorRedPrimary else CeifadorSurfaceHighlight)
                                .border(
                                    1.dp,
                                    if (isSelected) CeifadorRedPrimary else CeifadorBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setFingerLayout(layout) }
                                .padding(vertical = 10.dp, horizontal = 4.dp)
                                .testTag("btn_layout_${layout.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when (layout) {
                                        HudFingerLayout.TWO_FINGERS -> "2 DEDOS"
                                        HudFingerLayout.THREE_FINGERS -> "3 DEDOS"
                                        HudFingerLayout.FOUR_FINGERS -> "4 DEDOS"
                                    },
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = when (layout) {
                                        HudFingerLayout.TWO_FINGERS -> "Mobile Padrão"
                                        HudFingerLayout.THREE_FINGERS -> "Gatilho Superior"
                                        HudFingerLayout.FOUR_FINGERS -> "Claw Tático"
                                    },
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextMuted,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive HUD Screen Simulator Canvas
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_hud_canvas"),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MOCKUP DE TELA HUD (PROPORÇÃO 16:9)",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Toque em um elemento para detalhes",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulated Landscape Smartphone Display
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF07090D))
                            .border(1.dp, CeifadorBorderActive, RoundedCornerShape(8.dp))
                    ) {
                        val canvasWidth = maxWidth
                        val canvasHeight = maxHeight

                        // Center crosshair watermark
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(16.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        )

                        // Render each HUD element at precise coordinates
                        generatedHud.elements.forEach { element ->
                            val isSelected = selectedElement?.id == element.id
                            val baseElementSizeDp = ((element.sizePercent / 100f) * 44f).dp

                            val xOffset = canvasWidth * element.posXPercent - (baseElementSizeDp / 2)
                            val yOffset = canvasHeight * element.posYPercent - (baseElementSizeDp / 2)

                            Box(
                                modifier = Modifier
                                    .offset(x = xOffset, y = yOffset)
                                    .size(baseElementSizeDp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            element.id == "fire_btn" -> CeifadorRedPrimary.copy(alpha = 0.35f)
                                            element.id == "gloo_btn" -> CeifadorCyanGlow.copy(alpha = 0.3f)
                                            isSelected -> CeifadorEmerald.copy(alpha = 0.4f)
                                            else -> CeifadorSurfaceHighlight.copy(alpha = 0.5f)
                                        }
                                    )
                                    .border(
                                        width = if (isSelected || element.id == "fire_btn") 1.5.dp else 1.dp,
                                        color = when {
                                            element.id == "fire_btn" -> CeifadorRedPrimary
                                            element.id == "gloo_btn" -> CeifadorCyanGlow
                                            isSelected -> CeifadorEmerald
                                            else -> Color.White.copy(alpha = 0.35f)
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable { selectedElement = element }
                                    .testTag("hud_canvas_btn_${element.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = when (element.id) {
                                            "fire_btn" -> Icons.Default.TrackChanges
                                            "left_fire_btn" -> Icons.Default.FlashOn
                                            "gloo_btn" -> Icons.Default.Shield
                                            "scope_btn" -> Icons.Default.Visibility
                                            "weapon_switch_btn" -> Icons.Default.SwapHoriz
                                            "jump_btn" -> Icons.Default.TouchApp
                                            "crouch_btn" -> Icons.Default.FitnessCenter
                                            else -> Icons.Default.Info
                                        },
                                        contentDescription = element.name,
                                        tint = if (element.id == "fire_btn") CeifadorRedPrimary else Color.White,
                                        modifier = Modifier.size(if (baseElementSizeDp > 36.dp) 16.dp else 12.dp)
                                    )
                                    if (baseElementSizeDp > 34.dp) {
                                        Text(
                                            text = "${element.sizePercent}%",
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Selected element quick inspector
                    selectedElement?.let { elem ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CeifadorSurfaceHighlight)
                                .border(1.dp, CeifadorBorderActive, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = elem.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Tamanho: ${elem.sizePercent}% | Opacidade: ${elem.opacityPercent}%",
                                        color = CeifadorCyanGlow,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = elem.proRecommendation,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Full HUD Elements Table with exact values to apply
        item {
            Text(
                text = "TABELA EXATA DE ELEMENTOS DO HUD",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        items(generatedHud.elements) { element ->
            val isFireButton = element.id == "fire_btn"
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFireButton) CeifadorSurfaceHighlight else CeifadorSurface
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isFireButton) CeifadorRedPrimary.copy(alpha = 0.6f) else CeifadorBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = element.name,
                                color = if (isFireButton) CeifadorRedPrimary else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (isFireButton) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CeifadorRedPrimary)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "SINCRONIZADO",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                        Text(
                            text = element.functionRole,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Tamanho: ${element.sizePercent}%",
                            color = if (isFireButton) CeifadorRedPrimary else CeifadorCyanGlow,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Posição Y: ${(element.posYPercent * 100).toInt()}%",
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Action Buttons: Copy All HUD Parameters & Test in Simulator
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val hudSummary = buildString {
                            appendLine("=== [CEIFADOR SENSI FF - HUD CONFIG] ===")
                            appendLine("Arma Sincronizada: ${selectedWeapon.name}")
                            appendLine("Sensibilidade Geral: ${activeSensi.geral}")
                            appendLine("DPI Aplicada: ${currentDpi}")
                            appendLine("Esquema de Dedos: ${fingerLayout.title}")
                            appendLine("----------------------------------------")
                            generatedHud.elements.forEach {
                                appendLine("• ${it.name}: Tamanho ${it.sizePercent}% | Opacidade ${it.opacityPercent}% | Posição Y ${(it.posYPercent * 100).toInt()}%")
                            }
                            appendLine("----------------------------------------")
                            appendLine("Regra Ceifador: ${generatedHud.technicalExplanation}")
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Ceifador HUD", hudSummary)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Parâmetros do HUD copiados com sucesso!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_copy_hud"),
                    colors = ButtonDefaults.buttonColors(containerColor = CeifadorRedPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar Todos os Parâmetros do HUD", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onNavigateToSimulator,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_hud_to_simulator"),
                    colors = ButtonDefaults.buttonColors(containerColor = CeifadorSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorCyanGlow.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = CeifadorCyanGlow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Testar Puxada de Capa no Simulador",
                        color = CeifadorCyanGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
