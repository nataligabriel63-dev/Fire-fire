package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.crosshair.CustomCrosshairScreen
import com.example.ui.screens.dpi.DpiPollingScreen
import com.example.ui.screens.hud.AdvancedHudScreen
import com.example.ui.screens.profiles.SavedProfilesScreen
import com.example.ui.screens.simulator.AimSimulatorScreen
import com.example.ui.screens.trick.TrickButtonScreen
import com.example.ui.screens.weapon.WeaponSensiScreen
import com.example.ui.theme.CeifadorBorder
import com.example.ui.theme.CeifadorBorderActive
import com.example.ui.theme.CeifadorCyanGlow
import com.example.ui.theme.CeifadorDarkBg
import com.example.ui.theme.CeifadorRedPrimary
import com.example.ui.theme.CeifadorSurface
import com.example.ui.theme.CeifadorSurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CeifadorViewModel

enum class CeifadorTab(val title: String, val shortTitle: String, val icon: ImageVector) {
    WEAPONS("Armas & Sensi", "Armas", Icons.Default.TrackChanges),
    DPI_POLLING("DPI & Polling", "DPI/Hz", Icons.Default.Speed),
    HUD("HUD Dinâmico", "HUD", Icons.Default.Dashboard),
    TRICK_BUTTON("Botão Trick", "Trick", Icons.Default.Adjust),
    CROSSHAIR("Mira Custom", "Mira", Icons.Default.CenterFocusStrong),
    SIMULATOR("Simulador Capa", "Treino", Icons.Default.SportsEsports),
    PROFILES("Perfis Salvos", "Perfis", Icons.Default.BookmarkBorder)
}

@Composable
fun CeifadorMainScreen(
    viewModel: CeifadorViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(CeifadorTab.WEAPONS) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CeifadorDarkBg,
        topBar = {
            ScrollableTabRow(
                selectedTabIndex = currentTab.ordinal,
                containerColor = CeifadorSurface,
                contentColor = TextPrimary,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                        color = CeifadorRedPrimary,
                        height = 3.dp
                    )
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CeifadorBorder)
                    )
                }
            ) {
                CeifadorTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        text = {
                            Text(
                                text = tab.title.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) CeifadorRedPrimary else TextMuted,
                                letterSpacing = 0.5.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) CeifadorRedPrimary else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag("top_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CeifadorSurface,
                contentColor = TextPrimary,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .height(64.dp)
                    .border(androidx.compose.foundation.BorderStroke(1.dp, CeifadorBorder))
            ) {
                listOf(
                    CeifadorTab.WEAPONS,
                    CeifadorTab.DPI_POLLING,
                    CeifadorTab.HUD,
                    CeifadorTab.TRICK_BUTTON,
                    CeifadorTab.CROSSHAIR,
                    CeifadorTab.SIMULATOR
                ).forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.shortTitle,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.shortTitle,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CeifadorRedPrimary,
                            selectedTextColor = CeifadorRedPrimary,
                            indicatorColor = CeifadorRedPrimary.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                CeifadorTab.WEAPONS -> WeaponSensiScreen(
                    viewModel = viewModel,
                    onNavigateToHud = { currentTab = CeifadorTab.HUD },
                    onNavigateToSimulator = { currentTab = CeifadorTab.SIMULATOR }
                )
                CeifadorTab.DPI_POLLING -> DpiPollingScreen(
                    viewModel = viewModel
                )
                CeifadorTab.HUD -> AdvancedHudScreen(
                    viewModel = viewModel,
                    onNavigateToSimulator = { currentTab = CeifadorTab.SIMULATOR }
                )
                CeifadorTab.TRICK_BUTTON -> TrickButtonScreen(
                    viewModel = viewModel
                )
                CeifadorTab.CROSSHAIR -> CustomCrosshairScreen(
                    viewModel = viewModel
                )
                CeifadorTab.SIMULATOR -> AimSimulatorScreen(
                    viewModel = viewModel
                )
                CeifadorTab.PROFILES -> SavedProfilesScreen(
                    viewModel = viewModel,
                    onProfileLoaded = { currentTab = CeifadorTab.WEAPONS }
                )
            }
        }
    }
}
