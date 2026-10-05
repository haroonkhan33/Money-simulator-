package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Property
import com.example.viewmodel.TycoonViewModel

@Composable
fun InvestmentsScreen(
    properties: List<Property>,
    cashBalance: Double,
    creditScore: Int,
    onPurchaseProperty: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Credit Score Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131E29)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF00FF88),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GLOBAL CREDIT SCORE",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        val rating = when {
                            creditScore >= 800 -> "EXCELLENT"
                            creditScore >= 740 -> "VERY GOOD"
                            creditScore >= 670 -> "GOOD"
                            else -> "FAIR"
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0D291F), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = rating,
                                color = Color(0xFF00FF88),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "$creditScore",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "MAX: 850 PTS",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val scoreProgress = ((creditScore - 300).toFloat() / 550f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { scoreProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF00FF88),
                        trackColor = Color(0xFF0B141B)
                    )
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "REAL ESTATE & LUXURY ASSETS",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Property Items
        items(properties, key = { it.id }) { prop ->
            val canAfford = cashBalance >= prop.price && !prop.isPurchased

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D27)),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (prop.isPurchased) Color(0xFF00FF88) else Color(0xFF1F2F3E)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color(0xFF0D291F), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = prop.icon, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = prop.name,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cost: $${TycoonViewModel.formatCompact(prop.price)}",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            if (prop.monthlyRentalIncome > 0) {
                                Text(
                                    text = "Rent: +$${TycoonViewModel.formatCompact(prop.monthlyRentalIncome)}/mo",
                                    color = Color(0xFF00FF88),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (prop.isPurchased) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF082218), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "ACQUIRED ✓",
                                color = Color(0xFF00FF88),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    } else {
                        Button(
                            onClick = { onPurchaseProperty(prop.id) },
                            enabled = canAfford,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF88),
                                disabledContainerColor = Color(0xFF1B2836),
                                contentColor = Color(0xFF070B0E),
                                disabledContentColor = Color(0xFF475569)
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "BUY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
