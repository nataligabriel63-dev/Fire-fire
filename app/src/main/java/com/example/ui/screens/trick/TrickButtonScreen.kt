package com.example.ui.screens.trick

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.FloatingTrickService
import com.example.ui.theme.CeifadorBorder
import com.example.ui.theme.CeifadorBorderActive
import com.example.ui.theme.CeifadorCyanGlow
import com.example.ui.theme.CeifadorDarkBg
import com.example.ui.theme.CeifadorEmerald
import com.example.ui.theme.CeifadorRedDark
import com.example.ui.theme.CeifadorRedPrimary
import com.example.ui.theme.CeifadorSurface
import com.example.ui.theme.CeifadorSurfaceHighlight
import com.example.ui.theme.CeifadorSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CeifadorViewModel

@Composable
fun TrickButtonScreen(
    viewModel: CeifadorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedWeapon by viewModel.selectedWeapon.collectAsStateWithLifecycle()
    val activeSensi by viewModel.activeSensi.collectAsStateWithLifecycle()

    var isServiceActive by remember { mutableStateOf(FloatingTrickService.isRunning) }
    var isPositionLocked by remember { mutableStateOf(FloatingTrickService.isLocked) }
    var selectedShape by remember { mutableIntStateOf(FloatingTrickService.shapeType) }
    var buttonSizeDp by remember { mutableIntStateOf(FloatingTrickService.overlaySize) }
    var opacityPercent by remember { mutableIntStateOf(FloatingTrickService.overlayOpacity) }
    var selectedColorHex by remember { mutableIntStateOf(FloatingTrickService.overlayColor) }
    var strokeThickness by remember { mutableFloatStateOf(FloatingTrickService.strokeWidthDp) }
    var enableAntiOveraimBar by remember { mutableStateOf(FloatingTrickService.showAntiOveraimBar) }
    var boundaryHeightDp by remember { mutableIntStateOf(FloatingTrickService.boundaryDistanceDp) }

    fun updateServiceConfig() {
        FloatingTrickService.shapeType = selectedShape
        FloatingTrickService.overlaySize = buttonSizeDp
        FloatingTrickService.overlayOpacity = opacityPercent
        FloatingTrickService.overlayColor = selectedColorHex
        FloatingTrickService.strokeWidthDp = strokeThickness
        FloatingTrickService.isLocked = isPositionLocked
        FloatingTrickService.showAntiOveraimBar = enableAntiOveraimBar
        FloatingTrickService.boundaryDistanceDp = boundaryHeightDp

        if (isServiceActive) {
            val intent = Intent(context, FloatingTrickService::class.java).apply {
                action = FloatingTrickService.ACTION_UPDATE_CONFIG
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
        // Title Header
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
                        text = "BOTÃO TRICK SOBREPOSTO (OVERLAY)",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "ÂNCOra VISUAL E GUIA DE LIMITE ANTI-PASSAR DA CABEÇA NO JOGO",
                    color = CeifadorCyanGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Overlay Activation Control Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_trick_activation"),
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
                                text = "STATUS DA SOBREPOSIÇÃO",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isServiceActive) "ATIVO SOBRE O FREE FIRE" else "DESATIVADO",
                                color = if (isServiceActive) CeifadorEmerald else TextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Start / Stop Main Button
                        Button(
                            onClick = {
                                if (!hasOverlayPermission()) {
                                    Toast.makeText(context, "Conceda a permissão de sobreposição para exibir sobre o jogo!", Toast.LENGTH_LONG).show()
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                    return@Button
                                }

                                if (isServiceActive) {
                                    val intent = Intent(context, FloatingTrickService::class.java).apply {
                                        action = FloatingTrickService.ACTION_STOP
                                    }
                                    context.startService(intent)
                                    isServiceActive = false
                                } else {
                                    updateServiceConfig()
                                    val intent = Intent(context, FloatingTrickService::class.java).apply {
                                        action = FloatingTrickService.ACTION_START
                                    }
                                    context.startService(intent)
                                    isServiceActive = true
                                    Toast.makeText(context, "Botão Trick ativado! Abra o Free Fire.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isServiceActive) CeifadorRedPrimary else CeifadorEmerald
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_toggle_trick_overlay")
                        ) {
                            Icon(
                                imageVector = if (isServiceActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isServiceActive) "Parar Overlay" else "Ativar no Jogo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Lock/Unlock Position Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CeifadorSurfaceHighlight)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPositionLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (isPositionLocked) CeifadorEmerald else CeifadorCyanGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isPositionLocked) "Modo Jogo (Toques Livres)" else "Modo Ajuste (Arrastável)",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isPositionLocked) "O botão ignora toques para você atirar sem bloquear a tela" else "Arraste o botão com o dedo para alinhar sobre o HUD",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = isPositionLocked,
                            onCheckedChange = {
                                isPositionLocked = it
                                updateServiceConfig()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CeifadorEmerald,
                                checkedTrackColor = CeifadorEmerald.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CeifadorSurface
                            ),
                            modifier = Modifier.testTag("switch_lock_position")
                        )
                    }
                }
            }
        }

        // Sync with Selected Weapon Button
        item {
            Button(
                onClick = {
                    buttonSizeDp = ((activeSensi.buttonSize / 100f) * 110).toInt().coerceIn(36, 85)
                    boundaryHeightDp = when (selectedWeapon.category) {
                        com.example.data.model.WeaponCategory.DOZE -> 80
                        com.example.data.model.WeaponCategory.SMG -> 68
                        com.example.data.model.WeaponCategory.DMR -> 90
                        com.example.data.model.WeaponCategory.AR -> 60
                        else -> 70
                    }
                    updateServiceConfig()
                    Toast.makeText(context, "Sincronizado com ${selectedWeapon.name}!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_sync_trick_with_weapon"),
                colors = ButtonDefaults.buttonColors(containerColor = CeifadorSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorCyanGlow.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = CeifadorCyanGlow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sincronizar com ${selectedWeapon.name} (${activeSensi.buttonSize}%)",
                    color = CeifadorCyanGlow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Reticle Style & Shape Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "FORMATO DO BOTÃO TRICK",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        0 to "Anel Âncora",
                        1 to "Cruz Tática",
                        2 to "Ponto Cirúrgico",
                        3 to "Anel Duplo"
                    ).forEach { (typeId, label) ->
                        val isSelected = selectedShape == typeId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CeifadorRedPrimary else CeifadorSurface)
                                .border(1.dp, if (isSelected) CeifadorRedPrimary else CeifadorBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedShape = typeId
                                    updateServiceConfig()
                                }
                                .padding(vertical = 10.dp, horizontal = 2.dp)
                                .testTag("shape_btn_$typeId"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Color Customization
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "COR DO BOTÃO TRICK",
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
                        0xFFE53935.toInt() to "Vermelho Ceifador",
                        0xFF38BDF8.toInt() to "Ciano Neon",
                        0xFF10B981.toInt() to "Verde Tático",
                        0xFFD97706.toInt() to "Âmbar",
                        0xFFFFFFFF.toInt() to "Branco"
                    ).forEach { (colorInt, label) ->
                        val isSelected = selectedColorHex == colorInt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(colorInt).copy(alpha = 0.25f))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color(colorInt),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedColorHex = colorInt
                                    updateServiceConfig()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorInt))
                            )
                        }
                    }
                }
            }
        }

        // Granular Sliders for Size, Opacity, Thickness
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
                        Text(text = "Tamanho do Botão Trick", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${buttonSizeDp} dp", color = CeifadorCyanGlow, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Slider(
                        value = buttonSizeDp.toFloat(),
                        onValueChange = {
                            buttonSizeDp = it.toInt()
                            updateServiceConfig()
                        },
                        valueRange = 24f..90f,
                        colors = SliderDefaults.colors(
                            thumbColor = CeifadorRedPrimary,
                            activeTrackColor = CeifadorRedPrimary,
                            inactiveTrackColor = CeifadorBorder
                        )
                    )

                    // Opacity slider
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
                            thumbColor = CeifadorEmerald,
                            activeTrackColor = CeifadorEmerald,
                            inactiveTrackColor = CeifadorBorder
                        )
                    )
                }
            }
        }

        // Anti-Overaim Boundary Line Settings (Barra Anti-Passar da Cabeça)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Column {
                            Text(
                                text = "BARRA DE LIMITE ANTI-PASSAR DA CABEÇA",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Linha tática que define o teto máximo de arraste vertical",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Switch(
                            checked = enableAntiOveraimBar,
                            onCheckedChange = {
                                enableAntiOveraimBar = it
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

                    if (enableAntiOveraimBar) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Altura do Batente de Capa", color = TextSecondary, fontSize = 11.sp)
                            Text(text = "${boundaryHeightDp} dp", color = CeifadorRedPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Slider(
                            value = boundaryHeightDp.toFloat(),
                            onValueChange = {
                                boundaryHeightDp = it.toInt()
                                updateServiceConfig()
                            },
                            valueRange = 35f..120f,
                            colors = SliderDefaults.colors(
                                thumbColor = CeifadorRedPrimary,
                                activeTrackColor = CeifadorRedPrimary,
                                inactiveTrackColor = CeifadorBorder
                            )
                        )
                    }
                }
            }
        }

        // How it works technical explanation
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CeifadorSurfaceHighlight)
                    .border(1.dp, CeifadorBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CeifadorCyanGlow,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "COMO FUNCIONA O BOTÃO TRICK ANTI-OVERAIM:",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Ative o botão no app e abra o Free Fire.\n2. No 'Modo Ajuste', posicione o botão trick exatamente sobre o botão de tiro do seu HUD.\n3. Trave em 'Modo Jogo'. A linha vermelha 'LIMITE CAPA' ficará visível logo acima do seu botão.\n4. Ao subir o capa com o polegar, pare o arraste no momento em que seu dedo atingir a linha. Isso cria memória muscular visual perfeita, impedindo que a bala vá para o céu!",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
