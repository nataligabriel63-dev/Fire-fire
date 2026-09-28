package com.example.ui.screens.weapon

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.Weapon
import com.example.data.model.WeaponCategory
import com.example.ui.components.SensiSliderRow
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
import com.example.ui.theme.TextDisabled
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CeifadorViewModel

@Composable
fun WeaponSensiScreen(
    viewModel: CeifadorViewModel,
    onNavigateToHud: () -> Unit,
    onNavigateToSimulator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedWeapon by viewModel.selectedWeapon.collectAsStateWithLifecycle()
    val activeSensi by viewModel.activeSensi.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val filteredWeapons by viewModel.filteredWeapons.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentDpi by viewModel.currentDpi.collectAsStateWithLifecycle()

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveProfileName by remember { mutableStateOf("") }
    var saveProfileNotes by remember { mutableStateOf("") }
    var showWeaponSelectorModal by remember { mutableStateOf(false) }
    var showTipsExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CeifadorDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Header / Tactical Identification
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                            text = "CEIFADOR SENSI FF",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "MÓDULO DE SENSIBILIDADE POR ARMA",
                        color = CeifadorCyanGlow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CeifadorSurfaceVariant)
                        .border(1.dp, CeifadorBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "DPI ATIVA: ${currentDpi}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Weapon Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(WeaponCategory.values()) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CeifadorRedPrimary else CeifadorSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) CeifadorRedPrimary else CeifadorBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.setCategory(category) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("filter_${category.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.displayName,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Active Weapon Card / Switcher Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showWeaponSelectorModal = true }
                    .testTag("weapon_selector_trigger"),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedWeapon.name.uppercase(),
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CeifadorRedDark.copy(alpha = 0.5f))
                                        .border(1.dp, CeifadorRedPrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = selectedWeapon.category.badge,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Clique para trocar de arma",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Trocar arma",
                            tint = CeifadorRedPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tactical Weapon Specs Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TacticalStatBadge(
                            label = "RECUO",
                            value = selectedWeapon.recoilDifficulty,
                            modifier = Modifier.weight(1f)
                        )
                        TacticalStatBadge(
                            label = "ALCANCE",
                            value = selectedWeapon.optimalEngagementRange,
                            modifier = Modifier.weight(1f)
                        )
                        TacticalStatBadge(
                            label = "PUXADA",
                            value = selectedWeapon.dragTechnique,
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    // Stability toggle expander
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CeifadorSurfaceVariant.copy(alpha = 0.6f))
                            .clickable { showTipsExpanded = !showTipsExpanded }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dicas Táticas do Ceifador para esta arma",
                            color = CeifadorCyanGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = if (showTipsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = CeifadorCyanGlow,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    AnimatedVisibility(visible = showTipsExpanded) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = selectedWeapon.stabilityTips,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedWeapon.description,
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }

        // Preset Calibration Profiles Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "PREDEFINIÇÕES TÁTICAS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PresetButton(
                        title = "Ceifador Pro",
                        subtitle = "Equilibrado",
                        icon = Icons.Default.TrackChanges,
                        color = CeifadorRedPrimary,
                        onClick = { viewModel.applyProPreset() },
                        modifier = Modifier.weight(1f),
                        testTag = "preset_pro"
                    )
                    PresetButton(
                        title = "Rush Rápido",
                        subtitle = "Alta Sensi",
                        icon = Icons.Default.FlashOn,
                        color = CeifadorCyanGlow,
                        onClick = { viewModel.applyHighSpeedPreset() },
                        modifier = Modifier.weight(1f),
                        testTag = "preset_rush"
                    )
                    PresetButton(
                        title = "Anti-Passar",
                        subtitle = "Estável",
                        icon = Icons.Default.Security,
                        color = CeifadorEmerald,
                        onClick = { viewModel.applyStablePreset() },
                        modifier = Modifier.weight(1f),
                        testTag = "preset_stable"
                    )
                    PresetButton(
                        title = "One Shot",
                        subtitle = "X1 Crias",
                        icon = Icons.Default.Speed,
                        color = Color(0xFFD97706),
                        onClick = { viewModel.applyOneShotPreset() },
                        modifier = Modifier.weight(1f),
                        testTag = "preset_oneshot"
                    )
                }
            }
        }

        // Granular Sensitivity Sliders
        item {
            Text(
                text = "AJUSTES GRANULARES DE MIRA",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            SensiSliderRow(
                title = "Geral",
                subtitle = "Movimentação de câmera e velocidade base de subida de capa",
                value = activeSensi.geral,
                range = 0f..200f,
                accentColor = CeifadorRedPrimary,
                onValueChange = { viewModel.updateSensi(activeSensi.copy(geral = it)) }
            )
        }

        item {
            SensiSliderRow(
                title = "Ponto Vermelho (Red Dot)",
                subtitle = "Mira sem ótica acoplada / tiro inicial de curta distância",
                value = activeSensi.redDot,
                range = 0f..200f,
                accentColor = CeifadorRedPrimary,
                onValueChange = { viewModel.updateSensi(activeSensi.copy(redDot = it)) }
            )
        }

        item {
            SensiSliderRow(
                title = "Mira 2x",
                subtitle = "Precisão para combates a meia distância (SMG e AR)",
                value = activeSensi.scope2x,
                range = 0f..200f,
                accentColor = CeifadorCyanGlow,
                onValueChange = { viewModel.updateSensi(activeSensi.copy(scope2x = it)) }
            )
        }

        item {
            SensiSliderRow(
                title = "Mira 4x",
                subtitle = "Alvos a longa distância e rajadas de suporte",
                value = activeSensi.scope4x,
                range = 0f..200f,
                accentColor = CeifadorCyanGlow,
                onValueChange = { viewModel.updateSensi(activeSensi.copy(scope4x = it)) }
            )
        }

        item {
            SensiSliderRow(
                title = "Mira AWM / Snipers",
                subtitle = "Sensibilidade para quickscope e rastreio de alvos com fuzil de precisão",
                value = activeSensi.scopeAwm,
                range = 0f..200f,
                accentColor = Color(0xFFD97706),
                onValueChange = { viewModel.updateSensi(activeSensi.copy(scopeAwm = it)) }
            )
        }

        item {
            SensiSliderRow(
                title = "Olhadinha (Câmera Livre)",
                subtitle = "Rotação de visão 360° em corrida sem perder a direção",
                value = activeSensi.freeLook,
                range = 0f..200f,
                accentColor = CeifadorEmerald,
                onValueChange = { viewModel.updateSensi(activeSensi.copy(freeLook = it)) }
            )
        }

        // HUD & Button Configuration for this Weapon
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
                        Text(
                            text = "BOTÃO DE TIRO RECOMENDADO",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        OutlinedButton(
                            onClick = onNavigateToHud,
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorCyanGlow),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Abrir HUD Completo", color = CeifadorCyanGlow, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SensiSliderRow(
                        title = "Tamanho do Botão",
                        subtitle = "Diâmetro exato do botão de disparo (%) no HUD",
                        value = activeSensi.buttonSize,
                        range = 30f..80f,
                        accentColor = CeifadorRedPrimary,
                        onValueChange = { viewModel.updateSensi(activeSensi.copy(buttonSize = it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SensiSliderRow(
                        title = "Posição Vertical",
                        subtitle = "Distância da borda inferior (%) para permitir curso de arraste",
                        value = activeSensi.buttonPosition,
                        range = 5f..35f,
                        accentColor = CeifadorCyanGlow,
                        onValueChange = { viewModel.updateSensi(activeSensi.copy(buttonPosition = it)) }
                    )
                }
            }
        }

        // Quick Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy Sensi Button
                    Button(
                        onClick = {
                            val sensiText = buildString {
                                appendLine("--- [CEIFADOR SENSI FF] ---")
                                appendLine("Arma: ${selectedWeapon.name}")
                                appendLine("Geral: ${activeSensi.geral}")
                                appendLine("Ponto Vermelho: ${activeSensi.redDot}")
                                appendLine("Mira 2x: ${activeSensi.scope2x}")
                                appendLine("Mira 4x: ${activeSensi.scope4x}")
                                appendLine("Mira AWM: ${activeSensi.scopeAwm}")
                                appendLine("Olhadinha: ${activeSensi.freeLook}")
                                appendLine("Tamanho Botão: ${activeSensi.buttonSize}%")
                                appendLine("Posição Botão: ${activeSensi.buttonPosition}%")
                                appendLine("DPI Recomendada: ${currentDpi}")
                                appendLine("Técnica: ${selectedWeapon.dragTechnique}")
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Ceifador Sensi", sensiText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Sensibilidade copiada para a área de transferência!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_copy_sensi"),
                        colors = ButtonDefaults.buttonColors(containerColor = CeifadorRedPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copiar Sensi", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Save Profile Button
                    OutlinedButton(
                        onClick = {
                            saveProfileName = "${selectedWeapon.name} Pro"
                            saveProfileNotes = "DPI ${currentDpi} | Botão ${activeSensi.buttonSize}%"
                            showSaveDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_save_profile"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorderActive),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = CeifadorCyanGlow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Salvar Perfil", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Test in Simulator Button
                Button(
                    onClick = onNavigateToSimulator,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_test_simulator"),
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
                        "Testar Puxada Desta Sensi no Simulador",
                        color = CeifadorCyanGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    // Modal Dialog to Select Weapon
    if (showWeaponSelectorModal) {
        AlertDialog(
            onDismissRequest = { showWeaponSelectorModal = false },
            title = {
                Text(
                    text = "SELECIONAR ARMA",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Buscar arma...", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .testTag("input_search_weapon"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CeifadorRedPrimary,
                            unfocusedBorderColor = CeifadorBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredWeapons) { weapon ->
                            val isCurrent = weapon.id == selectedWeapon.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) CeifadorRedPrimary.copy(alpha = 0.2f) else CeifadorSurfaceHighlight)
                                    .border(
                                        1.dp,
                                        if (isCurrent) CeifadorRedPrimary else CeifadorBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.selectWeapon(weapon)
                                        showWeaponSelectorModal = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = weapon.name,
                                        color = if (isCurrent) CeifadorRedPrimary else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${weapon.category.displayName} • Recuo ${weapon.recoilDifficulty}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = CeifadorRedPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWeaponSelectorModal = false }) {
                    Text("Fechar", color = CeifadorCyanGlow)
                }
            },
            containerColor = CeifadorSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Save Profile Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = "SALVAR PERFIL TÁTICO",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Salve esta configuração de sensibilidade para carregar rapidamente em campeonatos e partidas.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = saveProfileName,
                        onValueChange = { saveProfileName = it },
                        label = { Text("Nome do Perfil") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CeifadorRedPrimary,
                            unfocusedBorderColor = CeifadorBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = saveProfileNotes,
                        onValueChange = { saveProfileNotes = it },
                        label = { Text("Anotações (Opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CeifadorRedPrimary,
                            unfocusedBorderColor = CeifadorBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentProfile(saveProfileName, saveProfileNotes)
                        showSaveDialog = false
                        Toast.makeText(context, "Perfil salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CeifadorRedPrimary)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = CeifadorSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun TacticalStatBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CeifadorSurfaceHighlight)
            .border(1.dp, CeifadorBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(
                text = label,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PresetButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CeifadorSurface)
            .border(1.dp, CeifadorBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}
