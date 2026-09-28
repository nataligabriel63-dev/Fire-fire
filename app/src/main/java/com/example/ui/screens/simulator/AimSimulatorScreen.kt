package com.example.ui.screens.simulator

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CeifadorBorder
import com.example.ui.theme.CeifadorBorderActive
import com.example.ui.theme.CeifadorCyanGlow
import com.example.ui.theme.CeifadorDarkBg
import com.example.ui.theme.CeifadorEmerald
import com.example.ui.theme.CeifadorRedDark
import com.example.ui.theme.CeifadorRedPrimary
import com.example.ui.theme.CeifadorSurface
import com.example.ui.theme.CeifadorSurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CeifadorViewModel

enum class ShotResult(val title: String, val color: Color, val damage: Int, val description: String) {
    HEADSHOT("CAPA VERMELHO!", Color(0xFFE53935), 495, "Puxada perfeita alinhada com a velocidade de disparo da arma!"),
    BODY("PEITO (UNDER-AIM)", Color(0xFFD97706), 42, "Arraste muito lento ou incompleto. Aumente o DPI ou puxe com mais velocidade."),
    OVER_HEAD("PASSOU DA CABEÇA", Color(0xFF94A3B8), 0, "Arraste excessivo. Reduza a sensibilidade Geral ou suavize a arrancada do polegar.")
}

@Composable
fun AimSimulatorScreen(
    viewModel: CeifadorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedWeapon by viewModel.selectedWeapon.collectAsStateWithLifecycle()
    val activeSensi by viewModel.activeSensi.collectAsStateWithLifecycle()
    val currentDpi by viewModel.currentDpi.collectAsStateWithLifecycle()

    var dragStartY by remember { mutableFloatStateOf(0f) }
    var dragCurrentY by remember { mutableFloatStateOf(0f) }
    var dragStartX by remember { mutableFloatStateOf(0f) }
    var dragCurrentX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragStartTime by remember { mutableLongStateOf(0L) }
    var lastShotResult by remember { mutableStateOf<ShotResult?>(null) }
    var lastDragDurationMs by remember { mutableLongStateOf(0L) }

    var totalShots by remember { mutableIntStateOf(0) }
    var headshots by remember { mutableIntStateOf(0) }

    val vibrator = remember {
        try {
            context.getSystemService(Vibrator::class.java)
        } catch (_: Exception) {
            null
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
        // Section Title
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
                        text = "SIMULADOR DE PUXADA DE CAPA",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "TESTE DE TEMPO DE REAÇÃO, ARRASTE E CALIBRAÇÃO DE BOTÃO",
                    color = CeifadorCyanGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Active Weapon Sensi Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CALIBRADO PARA:",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedWeapon.name} (Sensi: ${activeSensi.geral} | DPI: ${currentDpi})",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CeifadorRedPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Alvo: ${selectedWeapon.dragVelocityMs}ms",
                            color = CeifadorRedPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Interactive Target Practice Arena
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_aim_arena"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF07090E)),
                shape = RoundedCornerShape(14.dp),
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
                            text = "ARENA TÁTICA DE DISPARO",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Arraste o botão para cima",
                            color = CeifadorCyanGlow,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tactical Simulation Canvas
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0A0D14))
                            .border(1.dp, CeifadorBorder, RoundedCornerShape(10.dp))
                    ) {
                        val canvasW = maxWidth
                        val canvasH = maxHeight

                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(selectedWeapon, activeSensi, currentDpi) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            dragStartX = offset.x
                                            dragStartY = offset.y
                                            dragCurrentX = offset.x
                                            dragCurrentY = offset.y
                                            dragStartTime = System.currentTimeMillis()
                                            isDragging = true
                                        },
                                        onDragEnd = {
                                            isDragging = false
                                            val durationMs = (System.currentTimeMillis() - dragStartTime).coerceAtLeast(1)
                                            lastDragDurationMs = durationMs
                                            val verticalDistance = dragStartY - dragCurrentY

                                            // Determine shot outcome:
                                            // Headshot window is around weapon's ideal duration (+- 90ms) and distance > 80px
                                            val idealMs = selectedWeapon.dragVelocityMs.toLong()
                                            val sensitivityMultiplier = (activeSensi.geral / 180f) * (currentDpi / 550f)

                                            val result = when {
                                                verticalDistance < 40f || durationMs > (idealMs + 180) -> {
                                                    ShotResult.BODY
                                                }
                                                verticalDistance > 220f && durationMs < (idealMs - 80) && sensitivityMultiplier > 1.15f -> {
                                                    ShotResult.OVER_HEAD
                                                }
                                                verticalDistance >= 60f && durationMs in (idealMs - 120)..(idealMs + 140) -> {
                                                    ShotResult.HEADSHOT
                                                }
                                                verticalDistance > 160f && sensitivityMultiplier < 0.9f -> {
                                                    ShotResult.BODY
                                                }
                                                else -> {
                                                    ShotResult.HEADSHOT
                                                }
                                            }

                                            lastShotResult = result
                                            totalShots++
                                            if (result == ShotResult.HEADSHOT) {
                                                headshots++
                                                // Haptic feedback on headshot
                                                try {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                                                    }
                                                } catch (_: Exception) {}
                                            }
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragCurrentX += dragAmount.x
                                            dragCurrentY += dragAmount.y
                                        }
                                    )
                                }
                        ) {
                            val w = size.width
                            val h = size.height

                            // Draw Headshot Target Silhouette in upper half
                            val targetHeadCenter = Offset(w * 0.5f, h * 0.22f)
                            val targetBodyCenter = Offset(w * 0.5f, h * 0.44f)

                            // Head circle
                            drawCircle(
                                color = if (lastShotResult == ShotResult.HEADSHOT) CeifadorRedPrimary else Color(0xFF1E2638),
                                radius = 26.dp.toPx(),
                                center = targetHeadCenter
                            )
                            drawCircle(
                                color = if (lastShotResult == ShotResult.HEADSHOT) Color.White else CeifadorBorderActive,
                                radius = 26.dp.toPx(),
                                center = targetHeadCenter,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Crosshair in head
                            drawLine(
                                color = if (lastShotResult == ShotResult.HEADSHOT) Color.White else Color.Red.copy(alpha = 0.5f),
                                start = Offset(targetHeadCenter.x - 14.dp.toPx(), targetHeadCenter.y),
                                end = Offset(targetHeadCenter.x + 14.dp.toPx(), targetHeadCenter.y),
                                strokeWidth = 2.dp.toPx()
                            )
                            drawLine(
                                color = if (lastShotResult == ShotResult.HEADSHOT) Color.White else Color.Red.copy(alpha = 0.5f),
                                start = Offset(targetHeadCenter.x, targetHeadCenter.y - 14.dp.toPx()),
                                end = Offset(targetHeadCenter.x, targetHeadCenter.y + 14.dp.toPx()),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Body rectangle / torso
                            drawRoundRect(
                                color = if (lastShotResult == ShotResult.BODY) Color(0xFFD97706) else Color(0xFF141A26),
                                topLeft = Offset(targetBodyCenter.x - 34.dp.toPx(), targetBodyCenter.y - 20.dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(68.dp.toPx(), 44.dp.toPx()),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )

                            // Fire button launch zone in lower area
                            val buttonCenter = Offset(w * 0.5f, h * 0.82f)
                            drawCircle(
                                color = if (isDragging) CeifadorRedPrimary.copy(alpha = 0.4f) else CeifadorSurfaceHighlight,
                                radius = ((activeSensi.buttonSize / 100f) * 38.dp.toPx()),
                                center = buttonCenter
                            )
                            drawCircle(
                                color = CeifadorRedPrimary,
                                radius = ((activeSensi.buttonSize / 100f) * 38.dp.toPx()),
                                center = buttonCenter,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Draw trajectory line if dragging
                            if (isDragging) {
                                drawLine(
                                    color = CeifadorCyanGlow,
                                    start = Offset(dragStartX, dragStartY),
                                    end = Offset(dragCurrentX, dragCurrentY),
                                    strokeWidth = 3.dp.toPx()
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 6.dp.toPx(),
                                    center = Offset(dragCurrentX, dragCurrentY)
                                )
                            }
                        }

                        // Instructional prompt inside canvas
                        if (!isDragging && lastShotResult == null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Toque no botão inferior e puxe em direção à cabeça",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Result Banner
                    lastShotResult?.let { result ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(result.color.copy(alpha = 0.15f))
                                .border(1.dp, result.color, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                                .testTag("sim_result_banner")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = result.title,
                                        color = result.color,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = result.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${lastDragDurationMs} ms",
                                        color = CeifadorCyanGlow,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Dano: ${result.damage}",
                                        color = if (result == ShotResult.HEADSHOT) Color.Red else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Training Session Performance Statistics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ESTATÍSTICAS DA SESSÃO DE TIRO",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        IconButton(
                            onClick = {
                                totalShots = 0
                                headshots = 0
                                lastShotResult = null
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reiniciar estatísticas",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBox(
                            label = "TOTAL TIROS",
                            value = totalShots.toString(),
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            label = "CAPAS (HS)",
                            value = headshots.toString(),
                            color = CeifadorRedPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            label = "TAXA CAPA",
                            value = if (totalShots > 0) "${((headshots.toFloat() / totalShots) * 100).toInt()}%" else "0%",
                            color = CeifadorEmerald,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CeifadorSurfaceHighlight)
            .border(1.dp, CeifadorBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = color,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black
            )
        }
    }
}
