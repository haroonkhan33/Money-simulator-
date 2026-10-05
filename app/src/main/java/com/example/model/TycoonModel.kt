package com.example.model

data class BusinessAsset(
    val id: String,
    val name: String,
    val category: String,
    val baseIncome: Double,
    val baseCost: Double,
    var level: Int = 1,
    val iconRes: Int,
    var cycleProgress: Float = 0f,
    val cycleDurationMs: Long = 2000L,
    var isUnlocked: Boolean = true
) {
    val currentIncomePerSec: Double
        get() = baseIncome * level * (1.0 + (level / 10.0))

    val upgradeCost: Double
        get() = baseCost * Math.pow(1.15, (level - 1).toDouble())

    fun formatCost(): String = formatCurrency(upgradeCost)
    fun formatIncome(): String = "+${formatCurrency(currentIncomePerSec)}/sec"

    companion object {
        fun formatCurrency(amount: Double): String {
            return when {
                amount >= 1_000_000_000 -> String.format("$%.2fB", amount / 1_000_000_000)
                amount >= 1_000_000 -> String.format("$%.2fM", amount / 1_000_000)
                amount >= 1_000 -> String.format("$%.1fK", amount / 1_000)
                else -> String.format("$%.0f", amount)
            }
        }
    }
}

data class CareerJob(
    val id: String,
    val title: String,
    val company: String,
    val tier: Int,
    val shiftPay: Double,
    val requiredNetWorth: Double,
    var isUnlocked: Boolean = false
)

data class CryptoTicker(
    val symbol: String,
    val name: String,
    var price: Double,
    var changePercent: Double,
    var userHoldings: Double = 0.0
)
