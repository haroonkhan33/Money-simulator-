package com.example.model

data class OfflineBusinessEarning(
    val businessName: String,
    val icon: String,
    val amountEarned: Double
)

data class OfflineIncomeReport(
    val offlineSeconds: Long,
    val totalCashEarned: Double,
    val breakdown: List<OfflineBusinessEarning>
)
