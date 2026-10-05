package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingTextOverlay
import com.example.ui.components.TopHeaderStats
import com.example.viewmodel.TycoonViewModel

@Composable
fun DashboardScreen(
    viewModel: TycoonViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val businesses by viewModel.businesses.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val marketAssets by viewModel.marketAssets.collectAsState()
    val openPositions by viewModel.openPositions.collectAsState()
    val dailyMissions by viewModel.dailyMissions.collectAsState()
    val floatingTexts by viewModel.floatingTexts.collectAsState()
    val selectedAsset by viewModel.selectedMarketAsset.collectAsState()

    var showMissionsDialog by remember { mutableStateOf(false) }

    val pendingMissionsCount = dailyMissions.count { it.isCompleted && !it.isClaimed }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B0E)),
        containerColor = Color(0xFF070B0E),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0A1117),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .border(
                        width = 1.dp,
                        color = Color(0xFF162430)
                    )
            ) {
                val navItems = listOf(
                    Triple("Businesses", Icons.Default.BusinessCenter, 0),
                    Triple("Trading", Icons.Default.CandlestickChart, 1),
                    Triple("Investments", Icons.Default.TrendingUp, 2),
                    Triple("Settings", Icons.Default.Settings, 3)
                )

                navItems.forEach { (title, icon, index) ->
                    val isSelected = uiState.selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedTab(index) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = if (isSelected) Color(0xFF00FF88) else Color(0xFF64748B)
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                color = if (isSelected) Color(0xFF00FF88) else Color(0xFF64748B),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FF88),
                            selectedTextColor = Color(0xFF00FF88),
                            indicatorColor = Color(0xFF0D291F),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Stats Card & Missions Icon
                TopHeaderStats(
                    netWorth = viewModel.totalNetWorth,
                    cashBalance = uiState.cash,
                    monthlyCashFlow = viewModel.monthlyCashFlow,
                    cashFlowPerSec = viewModel.totalCashFlowPerSec,
                    pendingMissionsCount = pendingMissionsCount,
                    onOpenMissions = { showMissionsDialog = true }
                )

                // Dedicated Screen Content based on selected tab
                Box(modifier = Modifier.weight(1f)) {
                    Crossfade(targetState = uiState.selectedTab, label = "TabSwitch") { tab ->
                        when (tab) {
                            0 -> BusinessesScreen(
                                businesses = businesses,
                                cashBalance = uiState.cash,
                                onTapEarn = { x, y -> viewModel.handleTap(x, y) },
                                onUpgradeBusiness = { viewModel.upgradeBusiness(it) },
                                onHireManager = { viewModel.hireManager(it) },
                                onManualCollect = { id, x, y -> viewModel.manuallyCollectBusiness(id, x, y) }
                            )

                            1 -> TradingScreen(
                                marketAssets = marketAssets,
                                selectedAsset = selectedAsset,
                                openPositions = openPositions,
                                cashBalance = uiState.cash,
                                onSelectAsset = { viewModel.selectMarketAsset(it) },
                                onOpenTrade = { dir, margin, lev -> viewModel.openTrade(dir, margin, lev) },
                                onCloseTrade = { viewModel.closeTrade(it) }
                            )

                            2 -> InvestmentsScreen(
                                properties = properties,
                                cashBalance = uiState.cash,
                                creditScore = uiState.creditScore,
                                onPurchaseProperty = { viewModel.purchaseProperty(it) }
                            )

                            3 -> SettingsScreen(
                                uiState = uiState,
                                onSubmitSecretCode = { viewModel.submitSecretAdminCode(it) },
                                onAdminAddMillion = { viewModel.adminAddMillion() },
                                onAdminAddBillion = { viewModel.adminAddBillion() },
                                onAdminUnlockManagers = { viewModel.adminUnlockAllManagers() },
                                onAdminMaxBusinesses = { viewModel.adminMaxAllBusinesses() },
                                onAdminReset = { viewModel.adminResetProgress() }
                            )
                        }
                    }
                }
            }

            // Floating Text Overlay for Tap animations
            FloatingTextOverlay(floatingTexts = floatingTexts)

            // Dialogs
            if (showMissionsDialog) {
                DailyMissionsDialog(
                    missions = dailyMissions,
                    onClaimReward = { viewModel.claimMissionReward(it) },
                    onDismiss = { showMissionsDialog = false }
                )
            }
        }
    }
}
