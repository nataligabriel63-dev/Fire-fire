package com.example.ui.screens.crosshair

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.FloatingCrosshairService
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
fun CustomCrosshairScreen(
    viewModel: CeifadorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedWeapon by viewModel.selectedWeapon.collectAsStateWithLifecycle()

    var isCrosshairActive by remember { mutableStateOf(FloatingCrosshairService.isRunning) }
    var isPositionLocked by remember { mutableStateOf(FloatingCrosshairService.isLocked) }
    var crosshairStyle by remember { mutableIntStateOf(FloatingCrosshairService.crosshairStyle) }
    var sizeDp by remember { mutableIntStateOf(FloatingCrosshairService.sizeDp) }
    var strokeThickness by remember { mutableFloatStateOf(FloatingCrosshairService.strokeWidthDp) }
    var gapSizeDp by remember { mutableFloatStateOf(FloatingCrosshairService.gapSizeDp) }
    var lineLengthDp by remember { mutableFloatStateOf(FloatingCrosshairService.lineLengthDp) }
    var opacityPercent by remember { mutableIntStateOf(FloatingCrosshairService.opacityPercent) }
    var selectedColorArgb by remember { mutableIntStateOf(FloatingCrosshairService.colorArgb) }
    var hasCenterDot by remember { mutableStateOf(FloatingCrosshairService.hasCenterDot) }
    var centerDotRadius by remember { mutableFloatStateOf(FloatingCrosshairService.centerDotRadiusDp) }
    var hasOuterRing by remember { mutableStateOf(FloatingCrosshairService.hasOuterRing) }

    fun updateServiceConfig() {
        FloatingCrosshairService.crosshairStyle = crosshairStyle
        FloatingCrosshairService.sizeDp = sizeDp
        FloatingCrosshairService.strokeWidthDp = strokeThickness
        FloatingCrosshairService.gapSizeDp = gapSizeDp
        FloatingCrosshairService.lineLengthDp = lineLengthDp
        FloatingCrosshairService.opacityPercent = opacityPercent
        FloatingCrosshairService.colorArgb = selectedColorArgb
        FloatingCrosshairService.hasCenterDot = hasCenterDot
        FloatingCrosshairService.centerDotRadiusDp = centerDotRadius
        FloatingCrosshairService.hasOuterRing = hasOuterRing
        FloatingCrosshairService.isLocked = isPositionLocked

        if (isCrosshairActive) {
            val intent = Intent(context, FloatingCrosshairService::class.java).apply {
                action = FloatingCrosshairService.ACTION_UPDATE
            }
            context.startService(intent)
        }
    }

    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CeifadorDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(selectedColorArgb))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SISTEMA DE MIRA PERSONALIZADA",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "RETÍCULA TÁTICA SOBREPOSTA COM CONTROLE TOTAL DE GEOMETRIA",
                    color = CeifadorCyanGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Live Crosshair Preview Stage
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_crosshair_preview"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF07090E)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorderActive)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PREVIEW EM TEMPO REAL",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isCrosshairActive) "SOBREPOSIÇÃO ATIVA" else "DESLIGADA",
                            color = if (isCrosshairActive) CeifadorEmerald else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Crosshair Canvas
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0C1017))
                            .border(1.dp, CeifadorBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(110.dp)) {
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val drawColor = Color(selectedColorArgb).copy(alpha = opacityPercent / 100f)
                            val strokePx = strokeThickness.dp.toPx()
                            val gapPx = gapSizeDp.dp.toPx()
                            val lenPx = lineLengthDp.dp.toPx()
                            val mainSizePx = (sizeDp / 2f).dp.toPx()

                            when (crosshairStyle) {
                                0 -> {
                                    // Tactical Dot
                                    drawCircle(drawColor, radius = (sizeDp / 4f).dp.toPx())
                                    drawCircle(drawColor, radius = (sizeDp / 4f + 4f).dp.toPx(), style = Stroke(1.5f.dp.toPx()))
                                }
                                1 -> {
                                    // Classic Esports Cross
                                    drawLine(drawColor, Offset(cx, cy - gapPx), Offset(cx, cy - gapPx - lenPx), strokeWidth = strokePx, cap = StrokeCap.Round)
                                    drawLine(drawColor, Offset(cx, cy + gapPx), Offset(cx, cy + gapPx + lenPx), strokeWidth = strokePx, cap = StrokeCap.Round)
                                    drawLine(drawColor, Offset(cx - gapPx, cy), Offset(cx - gapPx - lenPx, cy), strokeWidth = strokePx, cap = StrokeCap.Round)
                                    drawLine(drawColor, Offset(cx + gapPx, cy), Offset(cx + gapPx + lenPx, cy), strokeWidth = strokePx, cap = StrokeCap.Round)
                                }
                                2 -> {
                                    // Circle + Ticks
                                    drawCircle(drawColor, radius = mainSizePx, style = Stroke(strokePx))
                                    drawLine(drawColor, Offset(cx, cy - mainSizePx - 4.dp.toPx()), Offset(cx, cy - mainSizePx + 2.dp.toPx()), strokeWidth = strokePx)
                                    drawLine(drawColor, Offset(cx, cy + mainSizePx - 2.dp.toPx()), Offset(cx, cy + mainSizePx + 4.dp.toPx()), strokeWidth = strokePx)
                                    drawLine(drawColor, Offset(cx - mainSizePx - 4.dp.toPx(), cy), Offset(cx - mainSizePx + 2.dp.toPx(), cy), strokeWidth = strokePx)
                                    drawLine(drawColor, Offset(cx + mainSizePx - 2.dp.toPx(), cy), Offset(cx + mainSizePx + 4.dp.toPx(), cy), strokeWidth = strokePx)
                                }
                                3 -> {
                                    // T-Cross
                                    drawLine(drawColor, Offset(cx, cy + gapPx), Offset(cx, cy + gapPx + lenPx), strokeWidth = strokePx, cap = StrokeCap.Round)
                                    drawLine(drawColor, Offset(cx - gapPx, cy), Offset(cx - gapPx - lenPx, cy), strokeWidth = strokePx, cap = StrokeCap.Round)
                                    drawLine(drawColor, Offset(cx + gapPx, cy), Offset(cx + gapPx + lenPx, cy), strokeWidth = strokePx, cap = StrokeCap.Round)
                                }
                                4 -> {
                                    // Diamond
                                    val dSize = mainSizePx * 0.8f
                                    drawLine(drawColor, Offset(cx, cy - dSize), Offset(cx + dSize, cy), strokeWidth = strokePx)
                                    drawLine(drawColor, Offset(cx + dSize, cy), Offset(cx, cy + dSize), strokeWidth = strokePx)
                                    drawLine(drawColor, Offset(cx, cy + dSize), Offset(cx - dSize, cy), strokeWidth = strokePx)
                                    drawLine(drawColor, Offset(cx - dSize, cy), Offset(cx, cy - dSize), strokeWidth = strokePx)
                                }
                            }

                            if (hasCenterDot && crosshairStyle != 0) {
                                drawCircle(drawColor, radius = centerDotRadius.dp.toPx())
                            }

                            if (hasOuterRing && crosshairStyle != 2) {
                                drawCircle(drawColor, radius = (sizeDp / 1.6f).dp.toPx(), style = Stroke(1.2.dp.toPx()))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Activation Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!hasOverlayPermission()) {
                                    Toast.makeText(context, "Conceda permissão de sobreposição para a mira!", Toast.LENGTH_LONG).show()
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                    return@Button
                                }

                                if (isCrosshairActive) {
                                    val intent = Intent(context, FloatingCrosshairService::class.java).apply {
                                        action = FloatingCrosshairService.ACTION_STOP
                                    }
                                    context.startService(intent)
                                    isCrosshairActive = false
                                } else {
                                    updateServiceConfig()
                                    val intent = Intent(context, FloatingCrosshairService::class.java).apply {
                                        action = FloatingCrosshairService.ACTION_START
                                    }
                                    context.startService(intent)
                                    isCrosshairActive = true
                                    Toast.makeText(context, "Mira Ativada sobre o Free Fire!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCrosshairActive) CeifadorRedPrimary else CeifadorEmerald
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp)
                                .testTag("btn_toggle_crosshair")
                        ) {
                            Icon(
                                imageVector = if (isCrosshairActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCrosshairActive) "Desativar Mira" else "Ativar Mira no Jogo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Position Lock Toggle
                        OutlinedButton(
                            onClick = {
                                isPositionLocked = !isPositionLocked
                                updateServiceConfig()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorderActive),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isPositionLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (isPositionLocked) CeifadorEmerald else CeifadorCyanGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPositionLocked) "Travada" else "Arrastável",
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Style Selector Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ESTILO DA MIRA",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        0 to "Ponto",
                        1 to "Cruz",
                        2 to "Círculo",
                        3 to "T-Cross",
                        4 to "Diamante"
                    ).forEach { (styleId, label) ->
                        val isSelected = crosshairStyle == styleId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CeifadorRedPrimary else CeifadorSurface)
                                .border(1.dp, if (isSelected) CeifadorRedPrimary else CeifadorBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    crosshairStyle = styleId
                                    updateServiceConfig()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Color Customization Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "COR DA RETÍCULA",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        0xFF00E5FF.toInt() to "Ciano Neon",
                        0xFFFF1744.toInt() to "Vermelho Ceifador",
                        0xFF00E676.toInt() to "Verde Tóxico",
                        0xFFFFEA00.toInt() to "Amarelo Ouro",
                        0xFFD500F9.toInt() to "Roxo Laser",
                        0xFFFFFFFF.toInt() to "Branco Titânio"
                    ).forEach { (colorArgb, label) ->
                        val isSelected = selectedColorArgb == colorArgb
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(colorArgb).copy(alpha = 0.2f))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color(colorArgb),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedColorArgb = colorArgb
                                    updateServiceConfig()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorArgb))
                            )
                        }
                    }
                }
            }
        }

        // Granular Sliders: Size, Stroke, Gap, Length, Opacity
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Size slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Tamanho Geral da Retícula", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${sizeDp} dp", color = CeifadorCyanGlow, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Slider(
                        value = sizeDp.toFloat(),
                        onValueChange = {
                            sizeDp = it.toInt()
                            updateServiceConfig()
                        },
                        valueRange = 16f..70f,
                        colors = SliderDefaults.colors(
                            thumbColor = CeifadorRedPrimary,
                            activeTrackColor = CeifadorRedPrimary,
                            inactiveTrackColor = CeifadorBorder
                        )
                    )

                    // Stroke Thickness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Espessura das Linhas", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = String.format("%.1f dp", strokeThickness), color = CeifadorCyanGlow, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Slider(
                        value = strokeThickness,
                        onValueChange = {
                            strokeThickness = it
                            updateServiceConfig()
                        },
                        valueRange = 1f..6f,
                        colors = SliderDefaults.colors(
                            thumbColor = CeifadorCyanGlow,
                            activeTrackColor = CeifadorCyanGlow,
                            inactiveTrackColor = CeifadorBorder
                        )
                    )

                    // Gap Size
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Espaçamento Central (Gap)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = String.format("%.1f dp", gapSizeDp), color = CeifadorCyanGlow, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Slider(
                        value = gapSizeDp,
                        onValueChange = {
                            gapSizeDp = it
                            updateServiceConfig()
                        },
                        valueRange = 0f..16f,
                        colors = SliderDefaults.colors(
                            thumbColor = CeifadorEmerald,
                            activeTrackColor = CeifadorEmerald,
                            inactiveTrackColor = CeifadorBorder
                        )
                    )

                    // Line Length
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Comprimento das Linhas", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = String.format("%.1f dp", lineLengthDp), color = CeifadorCyanGlow, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Slider(
                        value = lineLengthDp,
                        onValueChange = {
                            lineLengthDp = it
                            updateServiceConfig()
                        },
                        valueRange = 4f..24f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFD97706),
                            activeTrackColor = Color(0xFFD97706),
                            inactiveTrackColor = CeifadorBorder
                        )
                    )

                    // Opacity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Opacidade / Transparência", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${opacityPercent}%", color = CeifadorCyanGlow, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Slider(
                        value = opacityPercent.toFloat(),
                        onValueChange = {
                            opacityPercent = it.toInt()
                            updateServiceConfig()
                        },
                        valueRange = 20f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = CeifadorCyanGlow,
                            activeTrackColor = CeifadorCyanGlow,
                            inactiveTrackColor = CeifadorBorder
                        )
                    )
                }
            }
        }

        // Toggles: Center Dot & Outer Ring
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Ponto Central (Dot)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = hasCenterDot,
                            onCheckedChange = {
                                hasCenterDot = it
                                updateServiceConfig()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CeifadorRedPrimary,
                                checkedTrackColor = CeifadorRedPrimary.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CeifadorSurfaceHighlight
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Anel Externo Circundante", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = hasOuterRing,
                            onCheckedChange = {
                                hasOuterRing = it
                                updateServiceConfig()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CeifadorCyanGlow,
                                checkedTrackColor = CeifadorCyanGlow.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CeifadorSurfaceHighlight
                            )
                        )
                    }
                }
            }
        }

        // Fine Tuning Position D-Pad (Center / Offset)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AJUSTE FINO DE POSIÇÃO (D-PAD)",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                FloatingCrosshairService.offsetX -= 2
                                updateServiceConfig()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CeifadorSurfaceHighlight)
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Esquerda", tint = TextPrimary)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    FloatingCrosshairService.offsetY -= 2
                                    updateServiceConfig()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CeifadorSurfaceHighlight)
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Cima", tint = TextPrimary)
                            }

                            IconButton(
                                onClick = {
                                    FloatingCrosshairService.offsetX = 0
                                    FloatingCrosshairService.offsetY = 0
                                    updateServiceConfig()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CeifadorRedPrimary)
                            ) {
                                Icon(Icons.Default.CenterFocusStrong, contentDescription = "Centralizar", tint = Color.White)
                            }

                            IconButton(
                                onClick = {
                                    FloatingCrosshairService.offsetY += 2
                                    updateServiceConfig()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CeifadorSurfaceHighlight)
                            ) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Baixo", tint = TextPrimary)
                            }
                        }

                        IconButton(
                            onClick = {
                                FloatingCrosshairService.offsetX += 2
                                updateServiceConfig()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CeifadorSurfaceHighlight)
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Direita", tint = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Offset: X: ${FloatingCrosshairService.offsetX} | Y: ${FloatingCrosshairService.offsetY} (Botão central redefine ao centro da tela)",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
