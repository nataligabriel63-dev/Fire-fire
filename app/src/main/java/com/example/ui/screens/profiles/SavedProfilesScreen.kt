package com.example.ui.screens.profiles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.db.SavedProfile
import com.example.ui.theme.CeifadorBorder
import com.example.ui.theme.CeifadorBorderActive
import com.example.ui.theme.CeifadorCyanGlow
import com.example.ui.theme.CeifadorDarkBg
import com.example.ui.theme.CeifadorEmerald
import com.example.ui.theme.CeifadorRedPrimary
import com.example.ui.theme.CeifadorSurface
import com.example.ui.theme.CeifadorSurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CeifadorViewModel

@Composable
fun SavedProfilesScreen(
    viewModel: CeifadorViewModel,
    onProfileLoaded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedProfiles by viewModel.savedProfiles.collectAsStateWithLifecycle()

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
                        text = "PERFIS TÁTICOS SALVOS",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "BANCO DE DADOS DE CONFIGURAÇÕES DE CAMPEONATOS E X1",
                    color = CeifadorCyanGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Quick Preload competitive presets if empty
        if (savedProfiles.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = CeifadorCyanGlow,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Nenhum Perfil Personalizado Ainda",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gere uma sensibilidade no módulo de armas e clique em 'Salvar Perfil', ou adicione perfis competitivos predefinidos abaixo:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.saveCurrentProfile("M1887 X1 Apostado (Crias)", "DPI 620 | Meia-lua rápida | Botão 46%")
                                viewModel.saveCurrentProfile("MP40 4v4 Tático Pro", "DPI 580 | Sensi Geral 178 | Puxada com alívio")
                                viewModel.saveCurrentProfile("Desert Eagle One Shot", "DPI 640 | Puxada em Gancho J | Botão 42%")
                                Toast.makeText(context, "Perfis competitivos carregados!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CeifadorRedPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Carregar 3 Perfis Competitivos Meta")
                        }
                    }
                }
            }
        }

        // List of Saved Profiles
        items(savedProfiles) { profile ->
            SavedProfileCard(
                profile = profile,
                onLoad = {
                    viewModel.loadProfile(profile)
                    Toast.makeText(context, "Perfil '${profile.profileName}' ativado!", Toast.LENGTH_SHORT).show()
                    onProfileLoaded()
                },
                onToggleFavorite = {
                    viewModel.toggleFavorite(profile.id, profile.isFavorite)
                },
                onDelete = {
                    viewModel.deleteProfile(profile.id)
                    Toast.makeText(context, "Perfil removido", Toast.LENGTH_SHORT).show()
                },
                onCopy = {
                    val formatted = buildString {
                        appendLine("--- [CEIFADOR SENSI FF - PERFIL] ---")
                        appendLine("Nome: ${profile.profileName}")
                        appendLine("Arma: ${profile.weaponName}")
                        appendLine("Geral: ${profile.geral}")
                        appendLine("Red Dot: ${profile.redDot}")
                        appendLine("Mira 2x: ${profile.scope2x}")
                        appendLine("Mira 4x: ${profile.scope4x}")
                        appendLine("AWM: ${profile.scopeAwm}")
                        appendLine("Olhadinha: ${profile.freeLook}")
                        appendLine("Botão: ${profile.buttonSize}%")
                        appendLine("DPI: ${profile.dpi}")
                        appendLine("Técnica: ${profile.dragTechnique}")
                    }
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Perfil Ceifador", formatted))
                    Toast.makeText(context, "Configuração copiada!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun SavedProfileCard(
    profile: SavedProfile,
    onLoad: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CeifadorSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (profile.isFavorite) CeifadorRedPrimary.copy(alpha = 0.7f) else CeifadorBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = profile.profileName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${profile.weaponName} • DPI ${profile.dpi}",
                        color = CeifadorCyanGlow,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (profile.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (profile.isFavorite) CeifadorRedPrimary else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sensitivity Values Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ProfileMetricChip("GER", profile.geral.toString(), modifier = Modifier.weight(1f))
                ProfileMetricChip("RED", profile.redDot.toString(), modifier = Modifier.weight(1f))
                ProfileMetricChip("2X", profile.scope2x.toString(), modifier = Modifier.weight(1f))
                ProfileMetricChip("4X", profile.scope4x.toString(), modifier = Modifier.weight(1f))
                ProfileMetricChip("AWM", profile.scopeAwm.toString(), modifier = Modifier.weight(1f))
                ProfileMetricChip("BOTÃO", "${profile.buttonSize}%", modifier = Modifier.weight(1f))
            }

            if (profile.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profile.notes,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorderActive),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar", color = TextPrimary, fontSize = 11.sp)
                }

                Button(
                    onClick = onLoad,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CeifadorRedPrimary),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Carregar Sensi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProfileMetricChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CeifadorSurfaceHighlight)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
    }
}
