package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.TycoonViewModel

@Composable
fun TopHeaderStats(
    netWorth: Double,
    cashBalance: Double,
    monthlyCashFlow: Double,
    cashFlowPerSec: Double,
    pendingMissionsCount: Int,
    onOpenMissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top status row: Branding + Daily Missions Icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF00FF88), shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MONEY TYCOON CORP",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Dedicated Daily Missions Icon with Badge
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onOpenMissions,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF14202B), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Daily Missions",
                        tint = Color(0xFF00FF88),
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (pendingMissionsCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color(0xFFFF3B5C), CircleShape)
                            .border(1.5.dp, Color(0xFF070B0E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$pendingMissionsCount",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Primary Header Stats Card: Total Net Worth & Monthly Cash Flow
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121D28)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00FF88))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF192B38),
                                Color(0xFF101C26),
                                Color(0xFF0B141B)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    // Net Worth & Available Cash
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL NET WORTH",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$${TycoonViewModel.formatCompact(netWorth)}",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Cash on Hand Badge
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "LIQUID CASH",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF0D291F), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF1E4D38), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$${TycoonViewModel.formatCompact(cashBalance)}",
                                    color = Color(0xFF00FF88),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Monthly Cash Flow Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color(0xFF0D291F))
                            .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(99.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFF00FF88),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MONTHLY CASH FLOW",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "+$${TycoonViewModel.formatCompact(monthlyCashFlow)}/mo",
                                    color = Color(0xFF00FF88),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(+$${TycoonViewModel.formatCompact(cashFlowPerSec)}/s)",
                                    color = Color(0xFF10FFA0),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
