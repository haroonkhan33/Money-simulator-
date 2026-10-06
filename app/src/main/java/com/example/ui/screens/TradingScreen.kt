package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TycoonSoundManager
import com.example.model.MarketAsset
import com.example.model.TradeDirection
import com.example.model.TradePosition
import com.example.ui.components.CandlestickChart
import com.example.viewmodel.TycoonViewModel

@Composable
fun TradingScreen(
    marketAssets: List<MarketAsset>,
    selectedAsset: MarketAsset?,
    openPositions: List<TradePosition>,
    cashBalance: Double,
    onSelectAsset: (String) -> Unit,
    onOpenTrade: (TradeDirection, Double, Int) -> Unit,
    onCloseTrade: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDirection by remember { mutableStateOf(TradeDirection.LONG) }
    var selectedLeverage by remember { mutableIntStateOf(2) }
    var tradeMarginAmount by remember { mutableStateOf(100.0) }
    var selectedInterval by remember { mutableStateOf("1m") }

    val currentAsset = selectedAsset ?: marketAssets.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tickers Selector Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(marketAssets) { asset ->
                    val isSelected = asset.symbol == currentAsset?.symbol
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFF0D291F) else Color(0xFF131D27),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.5.dp,
                                if (isSelected) Color(0xFF00FF88) else Color(0xFF22303D),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                TycoonSoundManager.playChartIntervalSwitch()
                                onSelectAsset(asset.symbol)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = asset.symbol,
                                color = if (isSelected) Color(0xFF00FF88) else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${String.format("%.2f", asset.currentPrice)}",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Price & Chart Card
        if (currentAsset != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121B24)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2F3D))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = currentAsset.name,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$${String.format("%.2f", currentAsset.currentPrice)}",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Timeframe interval switchers: 1m, 5m, 1h
                                Row(
                                    modifier = Modifier
                                        .background(Color(0xFF0A1118), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFF1B2B38), RoundedCornerShape(8.dp))
                                        .padding(2.dp)
                                ) {
                                    listOf("1m", "5m", "1h").forEach { interval ->
                                        val isCurrentInterval = selectedInterval == interval
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isCurrentInterval) Color(0xFF00FF88) else Color.Transparent,
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .clickable {
                                                    if (selectedInterval != interval) {
                                                        selectedInterval = interval
                                                        TycoonSoundManager.playChartIntervalSwitch()
                                                    }
                                                }
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = interval,
                                                color = if (isCurrentInterval) Color(0xFF070B0E) else Color(0xFF94A3B8),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                val isUp = currentAsset.change24h >= 0
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isUp) Color(0xFF0D291F) else Color(0xFF2B1218),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isUp) Color(0xFF00FF88) else Color(0xFFFF3B5C),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${if (isUp) "+" else ""}${String.format("%.2f", currentAsset.change24h)}%",
                                        color = if (isUp) Color(0xFF00FF88) else Color(0xFFFF3B5C),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live Candlestick & Price Chart
                        CandlestickChart(
                            priceHistory = currentAsset.priceHistory,
                            isPositive = currentAsset.change24h >= 0
                        )
                    }
                }
            }

            // Trading Order Execution Card (Long / Short / Leverage)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14202B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3529))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "ORDER TERMINAL",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Direction: LONG vs SHORT
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { selectedDirection = TradeDirection.LONG },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedDirection == TradeDirection.LONG) Color(0xFF00FF88) else Color(0xFF1A2633),
                                    contentColor = if (selectedDirection == TradeDirection.LONG) Color(0xFF070B0E) else Color(0xFF94A3B8)
                                )
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "LONG (BUY)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { selectedDirection = TradeDirection.SHORT },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedDirection == TradeDirection.SHORT) Color(0xFFFF3B5C) else Color(0xFF1A2633),
                                    contentColor = if (selectedDirection == TradeDirection.SHORT) Color.White else Color(0xFF94A3B8)
                                )
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "SHORT (SELL)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Leverage Selector (1x, 2x, 5x, 10x)
                        Text(
                            text = "LEVERAGE MULTIPLIER",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(1, 2, 5, 10).forEach { lev ->
                                val isLevSelected = selectedLeverage == lev
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .background(
                                            if (isLevSelected) Color(0xFF0D291F) else Color(0xFF1B2836),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isLevSelected) Color(0xFF00FF88) else Color(0xFF2C3E50),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedLeverage = lev },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${lev}X",
                                        color = if (isLevSelected) Color(0xFF00FF88) else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Margin Amount Presets
                        Text(
                            text = "MARGIN SIZE: $${TycoonViewModel.formatCompact(tradeMarginAmount)}",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(50.0, 250.0, 1000.0, 5000.0).forEach { preset ->
                                val isSelectedPreset = tradeMarginAmount == preset
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(32.dp)
                                        .background(
                                            if (isSelectedPreset) Color(0xFF0D291F) else Color(0xFF182430),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelectedPreset) Color(0xFF00FF88) else Color(0xFF2C3E50),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { tradeMarginAmount = preset },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$${TycoonViewModel.formatCompact(preset)}",
                                        color = if (isSelectedPreset) Color(0xFF00FF88) else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Submit Order Button
                        val canAfford = cashBalance >= tradeMarginAmount && tradeMarginAmount > 0
                        Button(
                            onClick = { onOpenTrade(selectedDirection, tradeMarginAmount, selectedLeverage) },
                            enabled = canAfford,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedDirection == TradeDirection.LONG) Color(0xFF00FF88) else Color(0xFFFF3B5C),
                                disabledContainerColor = Color(0xFF1B2836),
                                contentColor = if (selectedDirection == TradeDirection.LONG) Color(0xFF070B0E) else Color.White,
                                disabledContentColor = Color(0xFF475569)
                            )
                        ) {
                            Text(
                                text = if (canAfford) "EXECUTE ${selectedDirection.name} ($${TycoonViewModel.formatCompact(tradeMarginAmount)} • ${selectedLeverage}X)"
                                       else "INSUFFICIENT BALANCE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // Active Open Positions Header & List
        item {
            Text(
                text = "ACTIVE OPEN POSITIONS (${openPositions.size})",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (openPositions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF101820), RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No open positions. Select an asset above to start trading!",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(openPositions, key = { it.id }) { pos ->
                val asset = marketAssets.find { it.symbol == pos.assetSymbol }
                val currentPrice = asset?.currentPrice ?: pos.entryPrice
                val pnl = pos.calculateUnrealizedPnl(currentPrice)
                val pnlPercent = pos.calculatePnlPercentage(currentPrice)
                val isProfit = pnl >= 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121D28)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isProfit) Color(0xFF1E4D38) else Color(0xFF4D1E28)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = pos.assetSymbol,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (pos.direction == TradeDirection.LONG) Color(0xFF0D291F) else Color(0xFF2B1218),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${pos.direction.name} ${pos.leverage}X",
                                        color = if (pos.direction == TradeDirection.LONG) Color(0xFF00FF88) else Color(0xFFFF3B5C),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Entry: $${String.format("%.2f", pos.entryPrice)} • Margin: $${TycoonViewModel.formatCompact(pos.marginAmount)}",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (isProfit) "+" else ""}$${String.format("%.2f", pnl)}",
                                    color = if (isProfit) Color(0xFF00FF88) else Color(0xFFFF3B5C),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${if (isProfit) "+" else ""}${String.format("%.1f", pnlPercent)}%",
                                    color = if (isProfit) Color(0xFF00FF88) else Color(0xFFFF3B5C),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onCloseTrade(pos.id) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFF1B2A38), RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Position",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
