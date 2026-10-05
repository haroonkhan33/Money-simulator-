package com.example.model

import java.util.UUID

data class Business(
    val id: String,
    val name: String,
    val category: String,
    val icon: String,
    val baseIncome: Double,
    val baseCost: Double,
    val managerCost: Double,
    val cycleTimeSeconds: Float = 2.0f,
    var level: Int = 0,
    var hasManager: Boolean = false,
    var currentCycleProgress: Float = 0f
) {
    val isUnlocked: Boolean get() = level > 0
    val currentCost: Double get() = if (level == 0) baseCost else baseCost * Math.pow(1.15, level.toDouble())
    val incomePerSecond: Double get() = if (level == 0) 0.0 else (baseIncome * level * (1.0 + (level / 15.0))) / cycleTimeSeconds
    val incomePerCycle: Double get() = if (level == 0) 0.0 else baseIncome * level * (1.0 + (level / 15.0))
}

data class Property(
    val id: String,
    val name: String,
    val type: String, // "Real Estate" or "Luxury Asset"
    val price: Double,
    val monthlyRentalIncome: Double,
    val creditScoreBoost: Int,
    val icon: String,
    var isPurchased: Boolean = false
)

enum class TradeDirection {
    LONG, SHORT
}

data class MarketAsset(
    val symbol: String,
    val name: String,
    val basePrice: Double,
    var currentPrice: Double,
    var priceHistory: List<Float> = emptyList(),
    var high24h: Double = basePrice * 1.05,
    var low24h: Double = basePrice * 0.95,
    var change24h: Double = 0.0
)

data class TradePosition(
    val id: String = UUID.randomUUID().toString(),
    val assetSymbol: String,
    val direction: TradeDirection,
    val entryPrice: Double,
    val marginAmount: Double,
    val leverage: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun calculateUnrealizedPnl(currentPrice: Double): Double {
        val priceDiff = currentPrice - entryPrice
        val percentageChange = priceDiff / entryPrice
        val leveragedPercentage = if (direction == TradeDirection.LONG) {
            percentageChange * leverage
        } else {
            -percentageChange * leverage
        }
        return marginAmount * leveragedPercentage
    }

    fun calculatePnlPercentage(currentPrice: Double): Double {
        val pnl = calculateUnrealizedPnl(currentPrice)
        return (pnl / marginAmount) * 100.0
    }
}

data class DailyMission(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    var currentProgress: Int = 0,
    val rewardCash: Double,
    var isClaimed: Boolean = false
) {
    val isCompleted: Boolean get() = currentProgress >= target
}

data class FloatingText(
    val id: Long,
    val text: String,
    val x: Float,
    val y: Float,
    val createdAt: Long = System.currentTimeMillis()
)
