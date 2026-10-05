package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Business
import com.example.viewmodel.TycoonViewModel

@Composable
fun BusinessesScreen(
    businesses: List<Business>,
    cashBalance: Double,
    onTapEarn: (Float, Float) -> Unit,
    onUpgradeBusiness: (String) -> Unit,
    onHireManager: (String) -> Unit,
    onManualCollect: (String, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Hero Interactive Tap to Earn Card
        item(span = { GridItemSpan(2) }) {
            HeroTapCard(
                cashBalance = cashBalance,
                onTap = onTapEarn
            )
        }

        // Section Title
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BUSINESS EMPIRE ASSETS",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${businesses.count { it.level > 0 }}/${businesses.size} ACTIVE",
                    color = Color(0xFF00FF88),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Grid of Business Cards
        items(businesses, key = { it.id }) { business ->
            BusinessAssetCard(
                business = business,
                cashBalance = cashBalance,
                onUpgrade = { onUpgradeBusiness(business.id) },
                onHireManager = { onHireManager(business.id) },
                onCardTap = { x, y -> onManualCollect(business.id, x, y) }
            )
        }
    }
}

@Composable
private fun HeroTapCard(
    cashBalance: Double,
    onTap: (Float, Float) -> Unit
) {
    var rootX = 0f
    var rootY = 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInRoot()
                rootX = pos.x
                rootY = pos.y
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onTap(rootX + offset.x, rootY + offset.y)
                }
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14202B)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00FF88))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF1D3528), Color(0xFF101920)),
                        radius = 450f
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color(0xFF0D291F), shape = RoundedCornerShape(16.dp))
                        .border(1.5.dp, Color(0xFF00FF88), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Tap to Earn",
                        tint = Color(0xFF00FF88),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "TAP TO GENERATE CASH FLOW",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Interactive Floating Text on Tap • Instant Cash",
                    color = Color(0xFF00FF88),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BusinessAssetCard(
    business: Business,
    cashBalance: Double,
    onUpgrade: () -> Unit,
    onHireManager: () -> Unit,
    onCardTap: (Float, Float) -> Unit
) {
    var cardX = 0f
    var cardY = 0f
    val isAffordable = cashBalance >= business.currentCost
    val isManagerAffordable = cashBalance >= business.managerCost && !business.hasManager

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInRoot()
                cardX = pos.x
                cardY = pos.y
            }
            .pointerInput(business.id) {
                detectTapGestures { offset ->
                    onCardTap(cardX + offset.x, cardY + offset.y)
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D27)),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (business.level > 0) Color(0xFF1E4D38) else Color(0xFF22303D)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top row: Emoji & Level Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF0D291F), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = business.icon, fontSize = 18.sp)
                }

                Box(
                    modifier = Modifier
                        .background(
                            if (business.level > 0) Color(0xFF0D291F) else Color(0xFF1B2631),
                            RoundedCornerShape(99.dp)
                        )
                        .border(
                            1.dp,
                            if (business.level > 0) Color(0xFF00FF88) else Color(0xFF334155),
                            RoundedCornerShape(99.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (business.level > 0) "LVL ${business.level}" else "LOCKED",
                        color = if (business.level > 0) Color(0xFF00FF88) else Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Business Name
            Text(
                text = business.name,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Production Rate
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "+$${TycoonViewModel.formatCompact(business.incomePerSecond)}/s",
                    color = Color(0xFF00FF88),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { if (business.level > 0) business.currentCycleProgress else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF00FF88),
                trackColor = Color(0xFF0B141B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Manager Automation Button
            if (business.level > 0) {
                if (business.hasManager) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF082218), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(8.dp))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AUTOMATED ✓",
                            color = Color(0xFF00FF88),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isManagerAffordable) Color(0xFF142921) else Color(0xFF16212B),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isManagerAffordable) Color(0xFF00FF88) else Color(0xFF2E3F4E),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = isManagerAffordable, onClick = onHireManager)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hire Mgr: $${TycoonViewModel.formatCompact(business.managerCost)}",
                            color = if (isManagerAffordable) Color(0xFF00FF88) else Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Upgrade Button
            Button(
                onClick = onUpgrade,
                enabled = isAffordable,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF88),
                    disabledContainerColor = Color(0xFF1A2A36),
                    contentColor = Color(0xFF070B0E),
                    disabledContentColor = Color(0xFF475569)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (business.level == 0) "BUY $${TycoonViewModel.formatCompact(business.currentCost)}"
                           else "UP $${TycoonViewModel.formatCompact(business.currentCost)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
